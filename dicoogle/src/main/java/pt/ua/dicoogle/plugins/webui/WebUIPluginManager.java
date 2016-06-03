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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/** A class type for managing web UI plugins.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class WebUIPluginManager {
    
    private static final Logger logger = LoggerFactory.getLogger(WebUIPluginManager.class);

    private static class WebUIEntry {
        public final WebUIPlugin plugin;
        public final String directory;
        public final String zipPath;

        public WebUIEntry(WebUIPlugin plugin, String directory, String zipPath) {
            assert plugin != null;
            assert directory != null;
            this.plugin = plugin;
            this.directory = directory;
            this.zipPath = zipPath;
        }
        public WebUIEntry(WebUIPlugin plugin, String directory) {
            this(plugin, directory, null);
        }
        public boolean isZipped() {
            return this.zipPath != null;
        }
        public InputStream readFile(String file) throws IOException {
            if (this.isZipped()) {
                ZipFile zip = new ZipFile(this.zipPath);
                return zip.getInputStream(zip.getEntry(this.directory + File.separatorChar + file));
            } else {
                File f = new File(this.directory + File.separatorChar + file);
                return new FileInputStream(f);
            }
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
        if (!directory.exists()) {
            logger.debug("No web plugins directory, ignoring");
            return;
        }
        if (!directory.isDirectory()) {
            logger.warn("Can't load web UI plugins, file {} is not a directory", directory);
            return;
        }
        
        for (File f : directory.listFiles()) {
            if (!f.isDirectory()) continue;
            try {
                WebUIPlugin plugin = this.load(f);
                logger.info("Loaded web plugin: {}", plugin.getName());
            } catch (IOException ex) {
                logger.error("Attempt to load plugin at {} failed", f.getName(), ex);
            } catch (PluginFormatException ex) {
                logger.warn("Could not load plugin at {} failed", f.getName(), ex);
            }
        }
    }
    
    public WebUIPlugin load(File directory) throws IOException, PluginFormatException {
        assert directory != null;
        assert directory.isDirectory();
        final String dirname = directory.getCanonicalPath();
        File packageJSON = new File(dirname + File.separatorChar + "package.json");
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
            this.plugins.put(plugin.getName(), new WebUIEntry(plugin, dirname));
            this.justPlugins.add(plugin);
            return plugin;
        }
    }

    /** Load all web plugins from a zip or jar file.
     * @param pluginZip the zip file containing the plugins
     * @throws java.io.IOException if the zip file can not be read
     */
    public void loadAllFromZip(ZipFile pluginZip) throws IOException {
        assert pluginZip != null;
        logger.trace("Discovering web UI plugins in {} ...", pluginZip.getName());
        Pattern pckDescrMatcher = Pattern.compile("WebPlugins\\" + File.separatorChar + "(\\p{Alnum}|\\-|\\_)+\\" + File.separatorChar + "package.json");
        Enumeration<? extends ZipEntry> entries = pluginZip.entries();
        final int DIRNAME_TAIL = "/package.json".length();
        while (entries.hasMoreElements()) {
            ZipEntry e = entries.nextElement();
            if (pckDescrMatcher.matcher(e.getName()).matches()) {
                String dirname = e.getName().substring(0, e.getName().length() - DIRNAME_TAIL);
                logger.info("Found web UI plugin in {} at \"{}\"", pluginZip.getName(), dirname);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(pluginZip.getInputStream(e)))) {
                    String acc = "";
                    String line;
                    while ((line = reader.readLine()) != null) {
                        acc += line;
                    }
                    WebUIPlugin plugin = WebUIPlugin.fromPackageJSON((JSONObject)JSONSerializer.toJSON(acc));
                    this.plugins.put(plugin.getName(), new WebUIEntry(plugin, dirname, pluginZip.getName()));
                    this.justPlugins.add(plugin);
                } catch (PluginFormatException ex) {
                    logger.warn("Failed to load plugin at \"{}\": {}", e.getName(), ex.getMessage());
                }
            }
        }
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
        return retrieveJSON(readFile(name, "package.json"));
    }
    
    /** Retrieve and return the original JSON object of the plugin.
     * @param reader a reader providing the JSON object
     * @return a JSON object of the original "package.json"
     * @throws IOException on error reading "package.json"
     */
    private JSONObject retrieveJSON(Reader reader) throws IOException {
        try (BufferedReader bufreader = new BufferedReader(reader)) {
            String acc = "";
            String line;
            while ((line = bufreader.readLine()) != null) {
                acc += line;
            }
            try {
                return (JSONObject)JSONSerializer.toJSON(acc);
            } catch (ClassCastException ex) {
                throw new IOException("Not a JSON object", ex);
            }
        }
    }

    /** Retrieve and return the original JSON object of the plugin.
     * @param istream an input stream providing the JSON object
     * @return a JSON object of the original "package.json"
     * @throws IOException on error reading "package.json"
     */
    private JSONObject retrieveJSON(InputStream istream) throws IOException {
        return retrieveJSON(new InputStreamReader(istream));
    }

    public String retrieveModuleJS(String name) throws IOException {
        String moduleFile = this.plugins.get(name).plugin.getModuleFile();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(readFile(name, moduleFile)))) {
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
                File pluginSettingsFile = new File(settingsFolder.getPath() + File.separatorChar + plugin.getName() + ".json");
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

    /** Read a file from an installed plugin's directory.
     * 
     * @param pluginName the name of the plugin
     * @param filename the relative path of the file
     * @return an input stream with the file's content
     * @throws IOException if the file does not exist or can not be read
     */
    private InputStream readFile(String pluginName, String filename) throws IOException {
        WebUIEntry e = this.plugins.get(pluginName);
        if (e == null) {
            throw new IllegalArgumentException("No such web UI plugin");
        }
        return e.readFile(filename);
    }
}
