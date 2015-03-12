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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.ua.dicoogle.plugins.PluginController;

/** A class type for managing web UI plugins.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class WebUIPluginManager {
    
    private static final Logger logger = LogManager.getLogger(PluginController.class.getName());
    
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
            if (!f.isDirectory()) continue;
            try {
                this.load(f);
            } catch (IOException ex) {
                logger.error("Attempt to load plugin at '" + f.getName() + "' failed", ex);
            } catch (PluginFormatException ex) {
                logger.warn("Could not load plugin at '" + f.getName() + "'", ex);
            }
        }
    }

    public void loadAll() {
        this.loadAll(new File(webBaseDir));
    }

    public void load(File directory) throws IOException, PluginFormatException {
        File packageJSON = new File(directory.getAbsolutePath() + File.pathSeparatorChar + "package.json");
        try (BufferedReader reader = new BufferedReader(new FileReader(packageJSON))) {
            String acc = "";
            String line;
            while ((line = reader.readLine()) != null) {
                acc += line;
            }
            WebUIPlugin plugin = WebUIPlugin.fromPackageJSON((JSONObject)JSONSerializer.toJSON(acc));
            File moduleFile = new File(directory.getAbsolutePath() + File.pathSeparatorChar + plugin.getModuleFile());
            if (moduleFile.canRead()) {
                throw new IOException("Module file " + moduleFile.getName() + " cannot be read");
            }
            this.plugins.put(plugin.getName(), plugin);
        }
    }
    
    /** Get the descriptor of a web UI plugin.
     * @param name the name of the plugin
     * @return a copy of the installed web UI plugin
     */
    public WebUIPlugin get(String name) {
        WebUIPlugin plugin = this.plugins.get(name);
        return plugin == null ? null : plugin.copy();
    }
    
    /** Retrieve and return the original JSON object of the object.
     * @param name the name of the plugin
     * @return a JSON object of the original "package.json"
     * @throws IOException on error reading "package.json"
     */
    public JSONObject retrieveJSON(String name) throws IOException {
        File packageJSON = new File(name + File.pathSeparatorChar + "package.json");
        try (BufferedReader reader = new BufferedReader(new FileReader(packageJSON))) {
            String acc = "";
            String line;
            while ((line = reader.readLine()) != null) {
                acc += line;
            }
            return (JSONObject) JSONSerializer.toJSON(acc);
        }
    }

    public String retrieveModuleJS(String name) throws IOException {
        String moduleFile = this.plugins.get(name).getModuleFile();
        File moduleJS = new File(name + File.pathSeparatorChar + moduleFile);
        try (BufferedReader reader = new BufferedReader(new FileReader(moduleJS))) {
            String acc = "";
            String line;
            while ((line = reader.readLine()) != null) {
                acc += line + '\n';
            }
            return acc;
        }
    }

    public Collection<WebUIPlugin> pluginSet() {
        return this.plugins.values();
    }

    public void loadSettings(File settingsFolder) {
        for (WebUIPlugin plugin : pluginSet()) {
            try {
                File pluginSettingsFile = new File(settingsFolder + "/" + plugin.getName() + ".json");
                BufferedReader reader = new BufferedReader(new FileReader(pluginSettingsFile));
                String acc = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    acc += line;
                }
                JSONObject settings = (JSONObject) JSONSerializer.toJSON(acc);
                plugin.setSettings(settings);
            } catch (IOException ex) {
                logger.log(Level.ERROR, (String)null, ex);
            }
        }
    }    
}
