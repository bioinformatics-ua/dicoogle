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
