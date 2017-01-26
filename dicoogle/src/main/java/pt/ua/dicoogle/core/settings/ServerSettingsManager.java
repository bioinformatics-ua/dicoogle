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
package pt.ua.dicoogle.core.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import pt.ua.dicoogle.core.XMLSupport;
import pt.ua.dicoogle.sdk.Utils.Platform;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ServerSettingsManager
{
    private static final Logger logger = LoggerFactory.getLogger(ServerSettingsManager.class);

    private static final Path MAIN_CONFIG_PATH = Paths.get(Platform.homePath(), "config.json");
    private static final Path LEGACY_CONFIG_PATH = Paths.get(Platform.homePath(), "config.xml");

    private ServerSettingsManager() {}

    private static ServerSettings inner;
    private static XMLSupport xml;
    private static ObjectMapper mapper;

    static {
        // configure object mapper
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /** Retrieve the managed server settings instance. Must be called only after explicit initialization with
     * {@linkplain #init()}.
     *
     * @return a global instance of the server's settings
     */
    public static ServerSettings getSettings() {
        if (inner == null) throw new IllegalStateException();
        return inner;
    }

    public static void init() throws IOException {
        // this is called by the main application in order to
        // load the appropriate settings.

        // check whether config.xml exists and config.json doesn't
        if (!Files.exists(MAIN_CONFIG_PATH)) {
            if (Files.exists(LEGACY_CONFIG_PATH)) {
                logger.info("Using legacy server configuration file. Consider updating to the new format in the future.");
                xml = new XMLSupport();
                inner = xml.getXML();
            } else {
                // create a new configuration file
                inner = ServerSettingsImpl.createDefault();
            }
        } else {
            // use main configuration file
            inner = loadSettingsAt(MAIN_CONFIG_PATH);
        }
    }

    /** Save the internal settings. */
    public static void saveSettings() {
        try {
            if (xml != null) {
                xml.printXML();
            } else {
                mapper.writeValue(Files.newOutputStream(MAIN_CONFIG_PATH), inner);
            }
        } catch (Exception ex) {
            logger.error("Failed to save server settings", ex);
        }
    }

    // independent static methods

    /** Load settings in the new format from a URL. */
    public static ServerSettings loadSettingsAt(URL url) throws IOException {
        Objects.requireNonNull(url);
        return mapper.readValue(url.openStream(), ServerSettingsImpl.class);
    }

    public static ServerSettings loadLegacySettings() {
        return new XMLSupport().getXML();
    }

    public static ServerSettings loadLegacySettingsAt(URL url) throws IOException {
        try {
            return new XMLSupport().parseXML(url.openStream());
        } catch (SAXException e) {
            throw new IOException("SAX problem", e);
        }
    }

    /** Load settings in the new format from a file system path. */
    public static ServerSettings loadSettingsAt(Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(Files.newInputStream(path), ServerSettingsImpl.class);
    }

    /** Load settings in the new format from a file. */
    public static ServerSettings loadSettingsAt(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new FileInputStream(file), ServerSettingsImpl.class);
    }

    /** Save the given settings to a path in the file system. This method will always
     * output settings according to the new format.
     */
    public static void saveSettingsTo(ServerSettings settings, Path path) throws IOException {
        mapper.writeValue(Files.newOutputStream(path), settings);
    }

    /** Save the global settings to a path in the file system. The resulting format
     * depends on whether the loaded configuration file is in the legacy format
     * or the new one.
     */
    public static void saveSettingsTo(Path path) throws IOException {
        if (inner == null) throw new IllegalStateException();
        if (xml != null) {
            xml.printXML(path);
        } else {
            mapper.writeValue(Files.newOutputStream(path), inner);
        }
    }

}
