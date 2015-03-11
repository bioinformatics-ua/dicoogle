/**
 * Copyright (C) 2014 Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Dicoogle. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.plugins.webui;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/** A class type for managing web UI plugins.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class WebUIPluginManager {
    
    private final String webBaseDir;

    private final Map<String, WebUIPlugin> plugins;
    
    public WebUIPluginManager() {
        this.plugins = new HashMap<>();
        this.webBaseDir = "WebPlugins";
    }

    public WebUIPluginManager(String pluginbaseDir) {
        this.plugins = new HashMap<>();
        this.webBaseDir = pluginbaseDir;
    }
    
    public void loadAll(File directory) {
        assert directory != null;
        for (File f : directory.listFiles()) {
            
        }
    }

    public void loadAll() {
        this.loadAll(new File(webBaseDir));
    }

    public void load(File directory) throws IOException {
        
    }
    
    /** Get the descriptor of a web UI plugin.
     * @param name the name of the plugin
     * @return a copy of the installed web UI plugin
     */
    public WebUIPlugin get(String name) {
        WebUIPlugin plugin = this.plugins.get(name);
        return plugin == null ? null : plugin.copy();
    }

    public Collection<WebUIPlugin> pluginSet() {
        return this.plugins.values();
    }

    public void loadSettings(File settingsFolder) {
        for (WebUIPlugin plugin : pluginSet()) {
            
            File pluginSettingsFile = new File(settingsFolder + "/" + plugin.getName() + ".json");
            // TODO
            JSONObject settings = (JSONObject) JSONSerializer.toJSON("{}");
            plugin.setSettings(settings);
        }
    }
}
