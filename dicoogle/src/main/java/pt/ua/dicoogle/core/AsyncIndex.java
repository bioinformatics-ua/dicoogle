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
package pt.ua.dicoogle.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.LoggerFactory;


import org.slf4j.Logger;
import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.plugins.PluginController;


/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class AsyncIndex {
    private static final Logger logger = LoggerFactory.getLogger(AsyncIndex.class);
    private final long pollingInterval = 30 * 1000;

    public AsyncIndex() {

        // path to watch
        String path = ServerSettings.getInstance().getDicoogleDir();


        FileAlterationObserver observer = new FileAlterationObserver(path);
        FileAlterationMonitor monitor =
                new FileAlterationMonitor(pollingInterval);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            // Is triggered when a file is created in the monitored folder
            @Override
            public void onFileCreate(File file) {


                try {
                    logger.debug("created {} : {}", file.toPath(), file.toURI());
                    URI uri = file.toURI();
                    PluginController.getInstance().index(uri);
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }


            }
            // Is triggered when a file is deleted from the monitored folder
            @Override
            public void onFileDelete(File file) {
                try {
                    // "file" is the reference to the removed file
                    System.out.println("File removed: "
                            + file.getCanonicalPath());
                    // "file" does not exists anymore in the location
                    System.out.println("File still exists in location: "
                            + file.exists());

                    logger.debug("deleted {} : {}", file.toPath(), file.toURI());
                    try {
                        URI uri = file.toURI();
                        PluginController.getInstance().unindex(uri);
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        };
        observer.addListener(listener);
        monitor.addObserver(observer);
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

