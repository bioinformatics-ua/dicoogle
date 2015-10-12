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
package pt.ua.dicoogle.plugins.webui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import pt.ua.dicoogle.plugins.PluginController;

/** A class type for managing web UI plugins.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class WebUIPluginManager {
    
    private static final Logger logger = LoggerFactory.getLogger(PluginController.class);
    
    private static final String DEFAULT_WEBPLUGINS_BASE_DIR = "WebPlugins";

    private static class WebUIEntry {
        public final WebUIPlugin plugin;
        public final File directory;

        public WebUIEntry(WebUIPlugin plugin, File directory) {
            this.plugin = plugin;
            this.directory = directory;
        }
    }
            
    private final Map<String, WebUIEntry> plugins;
    private final Set<WebUIPlugin> justPlugins;
    
    public WebUIPluginManager() {
        this.plugins = new HashMap<>();
        this.justPlugins = new HashSet<>();
    }
    
    public void loadAll(File directory) {
        assert directory != null;
//        if (!directory.exists()) {
//            logger.info("No web plugins in {}", directory);
//            return;
//        }
        if (!directory.isDirectory()) {
            logger.warn("Can't load web UI plugins, file {} is not a directory", directory);
            return;
        }
        
        for (File f : directory.listFiles()) {
            if (!f.isDirectory()) continue;
            try {
                WebUIPlugin plugin = this.load(f);
                if (!f.getName().equals(plugin.getName())) {
                    logger.warn("Plugin {} does not match directory name, ignoring plugin", plugin.getName());
                    this.unload(plugin.getName());
                } else {
                  logger.info("Loaded web plugin: {}", plugin.getName());
                }
            } catch (IOException ex) {
                logger.error("Attempt to load plugin at {} failed", f.getName(), ex);
            } catch (PluginFormatException ex) {
                logger.warn("Could not load plugin at {} failed", f.getName(), ex);
            }
        }
    }
    
    public void unload(String name) {
        this.plugins.remove(name);
    }

    @Deprecated
    public void loadAll() {
        this.loadAll(new File(DEFAULT_WEBPLUGINS_BASE_DIR));
    }

    @Deprecated
    public WebUIPlugin loadByPluginName(String name) throws IOException, PluginFormatException {
        return this.load(new File(DEFAULT_WEBPLUGINS_BASE_DIR + File.separatorChar + name));
    }

    public WebUIPlugin load(File directory) throws IOException, PluginFormatException {
        assert directory != null;
        assert directory.isDirectory();
        File packageJSON = new File(directory.getAbsolutePath() + File.separatorChar + "package.json");
        try (BufferedReader reader = new BufferedReader(new FileReader(packageJSON))) {
            String acc = "";
            String line;
            while ((line = reader.readLine()) != null) {
                acc += line;
            }
            WebUIPlugin plugin = WebUIPlugin.fromPackageJSON((JSONObject)JSONSerializer.toJSON(acc));
            File moduleFile = new File(directory.getAbsolutePath() + File.separatorChar + plugin.getModuleFile());
            if (!moduleFile.canRead()) {
                throw new IOException("Module file " + moduleFile.getName() + " cannot be read");
            }
            this.plugins.put(plugin.getName(), new WebUIEntry(plugin, directory));
            this.justPlugins.add(plugin);
            return plugin;
        }
    }

    /** Load all web plugins from a jar file.
     * @param pluginJar the jar file containing the plugins
     */
    public void loadAllFromJar(JarFile pluginJar) {
        assert pluginJar != null;

        Enumeration<? extends JarEntry> entries = pluginJar.entries();
        if (entries.hasMoreElements()) {
            for (JarEntry e = entries.nextElement(); entries.hasMoreElements() ; e = entries.nextElement()) {
                if (e.getName().startsWith("WebPlugins"))
                    logger.info("Jar entry {} in {}", e.getName(), pluginJar.getName());
            }
        }
        // TODO
    }
    
        
    /** Get the descriptor of a web UI plugin.
     * @param name the name of the plugin
     * @return a copy of the installed web UI plugin
     */
    public WebUIPlugin get(String name) {
        WebUIPlugin plugin = this.plugins.get(name).plugin;
        return plugin == null ? null : plugin.copy();
    }
    
    /** Retrieve and return the original JSON object of the plugin.
     * @param name the name of the plugin
     * @return a JSON object of the original "package.json"
     * @throws IOException on error reading "package.json"
     */
    public JSONObject retrieveJSON(String name) throws IOException {
        File packageJSON = new File(this.plugins.get(name).directory.getPath() + File.separatorChar + "package.json");
        return retrieveJSON(new FileReader(packageJSON));
    }

    /** Retrieve and return the original JSON object of the plugin.
     * @param istream a reader providing the JSON object
     * @return a JSON object of the original "package.json"
     * @throws IOException on error reading "package.json"
     */
    private JSONObject retrieveJSON(Reader istream) throws IOException {
        try (BufferedReader reader = new BufferedReader(istream)) {
            String acc = "";
            String line;
            while ((line = reader.readLine()) != null) {
                acc += line;
            }
            
            return (JSONObject)JSONSerializer.toJSON(acc);
        }
    }

    public String retrieveModuleJS(String name) throws IOException {
        String moduleFile = this.plugins.get(name).plugin.getModuleFile();
        File moduleJS = new File(this.plugins.get(name).directory.getPath() + File.separatorChar + moduleFile);
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
        return Collections.unmodifiableSet(justPlugins);
    }

    public void loadSettings(File settingsFolder) {
        for (WebUIPlugin plugin : pluginSet()) {
            try {
                File pluginSettingsFile = new File(settingsFolder + "/" + plugin.getName() + ".json");
                if (!pluginSettingsFile.exists()) { 
                    logger.info("Web plugin {} has no settings file", plugin.getName());
                    continue;
                }
                BufferedReader reader = new BufferedReader(new FileReader(pluginSettingsFile));
                String acc = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    acc += line;
                }
                JSONObject settings = (JSONObject) JSONSerializer.toJSON(acc);
                plugin.setSettings(settings);
            } catch (IOException ex) {
                logger.error("Failed to load web plugin settings", ex);
            }
        }
    }    

    @Deprecated
    public String getWebBaseDir() {
        return DEFAULT_WEBPLUGINS_BASE_DIR;
    }
}
