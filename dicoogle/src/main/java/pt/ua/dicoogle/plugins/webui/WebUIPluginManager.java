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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import pt.ua.dicoogle.server.users.RolesStruct;

import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
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

        /** 
         * Reads the contents of the requested file as UTF-8 text.
         */
        public String readToString(String file) throws IOException {
            if (this.isZipped()) {
                try (ZipFile zip = new ZipFile(this.zipPath)) {
                    InputStream istream = zip.getInputStream(zip.getEntry(this.directory + "/" + file));
                    return IOUtils.toString(istream, StandardCharsets.UTF_8);
                }
            } else {
                return IOUtils.toString(Files.newInputStream(Paths.get(this.directory, file)), StandardCharsets.UTF_8);
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
            if (!f.isDirectory())
                continue;
            try {
                WebUIPlugin plugin = this.load(f);
                logger.info("Loaded web plugin: {}", plugin.getName());

                for (Iterator<String> it = plugin.getRoles().iterator(); it.hasNext();) {
                    String pluginRole = it.next();
                    if (RolesStruct.getInstance().getRole(pluginRole) == null) {
                        logger.warn("Web UI plugin {} mentions unregistered role {}; Please update roles.xml",
                                plugin.getName(), pluginRole);
                        it.remove();
                    }
                }

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
        Path packageJSON = directory.toPath().resolve("package.json");
        String packageJSONTxt = IOUtils.toString(Files.newInputStream(packageJSON), StandardCharsets.UTF_8);
        WebUIPlugin plugin = WebUIPlugin.fromPackageJSON((JSONObject) JSONSerializer.toJSON(packageJSONTxt));
        Path moduleFile = directory.toPath().resolve(plugin.getModuleFile());
        if (Files.isReadable(moduleFile)) {
            throw new IOException("Module file " + plugin.getModuleFile() + " does not exist or is not readable");
        }
        this.plugins.put(plugin.getName(), new WebUIEntry(plugin, dirname));
        this.justPlugins.add(plugin);
        return plugin;
    }

    /** Load all web plugins from a zip or jar file.
     * @param pluginZip the zip file containing the plugins
     * @throws java.io.IOException if the zip file can not be read
     */
    public void loadAllFromZip(ZipFile pluginZip) throws IOException {
        assert pluginZip != null;
        logger.trace("Discovering web UI plugins in {} ...", pluginZip.getName());
        Pattern pckDescrMatcher = Pattern.compile(
                "WebPlugins\\" + File.separatorChar + "(\\p{Alnum}|\\-|\\_)+\\" + File.separatorChar + "package.json");
        Enumeration<? extends ZipEntry> entries = pluginZip.entries();
        final int DIRNAME_TAIL = "/package.json".length();
        while (entries.hasMoreElements()) {
            ZipEntry e = entries.nextElement();
            if (pckDescrMatcher.matcher(e.getName()).matches()) {
                String dirname = e.getName().substring(0, e.getName().length() - DIRNAME_TAIL);
                logger.info("Found web UI plugin in {} at \"{}\"", pluginZip.getName(), dirname);
                try {
                    String jsonTxt = IOUtils.toString(pluginZip.getInputStream(e), StandardCharsets.UTF_8);
                    WebUIPlugin plugin = WebUIPlugin.fromPackageJSON((JSONObject) JSONSerializer.toJSON(jsonTxt));
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
        String txt = this.readFile(name, "package.json");
        try {
            return (JSONObject) JSONSerializer.toJSON(txt);
        } catch (ClassCastException ex) {
            throw new IOException("Not a JSON object", ex);
        }
    }

    public String retrieveModuleJS(String name) throws IOException {
        String moduleFile = this.plugins.get(name).plugin.getModuleFile();

        return readFile(name, moduleFile);
    }

    public Collection<WebUIPlugin> pluginSet() {
        return Collections.unmodifiableSet(justPlugins);
    }

    public void loadSettings(File settingsFolder) {
        for (WebUIPlugin plugin : pluginSet()) {
            try {
                Path pluginSettingsPath = settingsFolder.toPath().resolve(plugin.getName() + ".json");
                if (!Files.exists(pluginSettingsPath)) {
                    logger.info("Web plugin {} has no settings file", plugin.getName());
                    continue;
                }
                String jsonTxt = IOUtils.toString(Files.newInputStream(pluginSettingsPath), StandardCharsets.UTF_8);
                JSONObject settings = (JSONObject) JSONSerializer.toJSON(jsonTxt);
                plugin.setSettings(settings);
            } catch (IOException ex) {
                logger.error("Failed to load web plugin settings", ex);
            }
        }
    }

    /** Read all contents of a file from an installed plugin's directory.
     * 
     * @param pluginName the name of the plugin
     * @param filename the relative path of the file
     * @return a string with the file's content, interpreted as UTF-8 text
     * @throws IOException if the file does not exist or can not be read
     */
    private String readFile(String pluginName, String filename) throws IOException {
        WebUIEntry e = this.plugins.get(pluginName);
        if (e == null) {
            throw new IllegalArgumentException("No such web UI plugin");
        }
        return e.readToString(filename);
    }
}
