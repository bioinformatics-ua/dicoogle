/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.server;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.server.DicomStorage.ImageElement;

/** Index worker actor.
 * 
 * This singleton actor module is responsible for
 * dispatching indexing tasks for the DICOM files received via C-STORE.
 * 
 * @see DicomStorage
 */
public final class IndexQueueWorker {

    private static final boolean ASYNC_INDEX = Boolean.valueOf(System.getProperty("dicoogle.index.async", "true"));

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(IndexQueueWorker.class);

    private static IndexQueueWorker instance;

    public synchronized static IndexQueueWorker getInstance() {
        if (instance == null) {
            instance = new IndexQueueWorker();
        }
        return instance;
    }

    private IndexQueueWorker() {}

    private final BlockingQueue<ImageElement> queue = new PriorityBlockingQueue<>();
    private Thread thread;

    public synchronized void start() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(WORKER_RUN, "indexer-queue-worker");
            thread.start();
            thread.setUncaughtExceptionHandler((Thread t, Throwable e) -> {
                LOG.error("Fatal error in indexer queue worker", e);
                ControlServices.getInstance().stopStorage();
                LOG.warn("DICOM storage service was taken down to prevent further errors");
            });
        }
    }

    /** Push this DICOM element to be indexed. */
    public void addElement(ImageElement element) {
        queue.add(element);
    }

    private final Runnable WORKER_RUN = () -> {
        while (true) {
            try {
                // Fetch an element from the priority queue
                ImageElement element = queue.take();
                URI exam = element.getUri();
                if (ASYNC_INDEX) {
                    PluginController.getInstance().index(exam);
                } else {
                    PluginController.getInstance().indexBlocking(exam);
                }
            } catch (InterruptedException ex) {
                LOG.warn("Indexer queue worker thread interrupted", ex);
            } catch (Exception ex) {
                LOG.error("Unexpected error in indexer queue worker", ex);
            }
        }
    };
}
