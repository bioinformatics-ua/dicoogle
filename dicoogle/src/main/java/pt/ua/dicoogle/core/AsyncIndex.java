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
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.LoggerFactory;


import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;
import org.slf4j.Logger;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.Utils.Platform;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class AsyncIndex {
    private static final Logger logger = LoggerFactory.getLogger(AsyncIndex.class);
    
    public AsyncIndex() {

        // path to watch
        String path = ServerSettings.getInstance().getDicoogleDir();

        // watch mask, specify events you care about,
        // or JNotify.FILE_ANY for all events.
        int mask = JNotify.FILE_CREATED
                | JNotify.FILE_DELETED
                | JNotify.FILE_MODIFIED
                | JNotify.FILE_RENAMED;

        // watch subtree?
        boolean watchSubtree = true;

        // add actual watch
        if (ServerSettings.getInstance().isMonitorWatcher()) {
            try {
                int watchID = JNotify.addWatch(path, mask, watchSubtree, new Listener());
            } catch (JNotifyException ex) {
                logger.error("Failed to create directory watcher", ex);
            }
        }
    }
}

class Listener implements JNotifyListener {
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);
    
    @Override
    public void fileRenamed(int wd, String rootPath, String oldName,
            String newName) {
        logger.debug("renamed {} : {} -> {}", rootPath, oldName, newName);
    }

    @Override
    public void fileModified(int wd, String rootPath, String name) {
        logger.debug("modified {} : {}", rootPath, name);
    }

    @Override
    public void fileDeleted(int wd, String rootPath, String name) {
        logger.debug("deleted {} : {}", rootPath, name);
        try {
            URI uri = new File(rootPath + File.separator + name).toURI();
            PluginController.getInstance().unindex(uri);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void fileCreated(int wd, String rootPath, String name) {
        try {
            logger.debug("created {} : {}", rootPath, name);
            URI uri = new File(rootPath + File.separator + name).toURI();
            PluginController.getInstance().index(uri);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
