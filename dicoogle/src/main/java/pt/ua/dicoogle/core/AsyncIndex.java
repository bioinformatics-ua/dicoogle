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
import java.util.logging.Level;
import java.util.logging.Logger;


import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;
import pt.ua.dicoogle.plugins.PluginController;

/**
 *
 * Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class AsyncIndex {

    PluginController pluginController;
    
    public AsyncIndex(PluginController pController) {

        pluginController = pController;
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
        if (ServerSettings.getInstance().isMonitorWatcher())
        {
            try {
                JNotify.addWatch(path, mask, watchSubtree, new Listener(pluginController));
            }
            catch (JNotifyException ex) {
                Logger.getLogger(AsyncIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class Listener implements JNotifyListener {

    PluginController pluginController;
    
    Listener(PluginController p){
        pluginController=p;
    }
    
    public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
        print("renamed " + rootPath + " : " + oldName + " -> " + newName);
    }

    public void fileModified(int wd, String rootPath, String name) {
        print("modified " + rootPath + " : " + name);
    }

    public void fileDeleted(int wd, String rootPath, String name) {
        print("deleted " + rootPath + " : " + name);
        try {
            pluginController.unindex(new URI(rootPath + File.pathSeparator+ name));
        }
        catch (URISyntaxException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    public void fileCreated(int wd, String rootPath, String name) {
        try {
            print("created " + rootPath + " : " + name);
            pluginController.index(new URI(rootPath + File.pathSeparator+ name));
        }
        catch (URISyntaxException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void print(String msg) {
        System.err.println(msg);
    }
}
