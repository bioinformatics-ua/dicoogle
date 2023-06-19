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
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import pt.ua.dicoogle.core.XMLSupport;
import pt.ua.dicoogle.sdk.datastructs.AdditionalSOPClass;
import pt.ua.dicoogle.sdk.datastructs.AdditionalTransferSyntax;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;
import pt.ua.dicoogle.server.SOPList;
import pt.ua.dicoogle.server.TransfersStorage;
import pt.ua.dicoogle.utils.Platform;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ServerSettingsManager {
    private static final Logger logger = LoggerFactory.getLogger(ServerSettingsManager.class);

    private static final Path MAIN_CONFIG_PATH = Paths.get(Platform.homePath(), "confs/server.xml");
    private static final Path LEGACY_CONFIG_PATH = Paths.get(Platform.homePath(), "config.xml");

    private ServerSettingsManager() {}

    private static ServerSettings inner;
    private static ObjectMapper mapper;

    static {
        // configure object mapper
        mapper = createObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private static ObjectMapper createObjectMapper() {
        return new XmlMapper();
    }

    /** Retrieve the managed server settings instance. Must be called only after explicit initialization with
     * {@linkplain #init()}.
     *
     * @return a global instance of the server's settings
     */
    public static ServerSettings getSettings() {
        if (inner == null)
            throw new IllegalStateException("Server settings not initialized");
        return inner;
    }

    /// This is called by the main application in order to
    /// load the appropriate settings into the manager.
    public static void init() throws IOException {

        // check whether the confs directory exists first,
        // so as to make it accept a symlink
        Path configParent = MAIN_CONFIG_PATH.getParent();
        if (!Files.isDirectory(configParent)) {
            Files.createDirectories(configParent);
        }

        // check whether the old config.xml exists and the new one doesn't
        if (!Files.exists(MAIN_CONFIG_PATH)) {
            if (Files.exists(LEGACY_CONFIG_PATH)) {
                logger.info("Legacy server configuration file found. Migrating settings to {} ...", MAIN_CONFIG_PATH);
                LegacyServerSettings old = new XMLSupport().getXML();
                // time to migrate!
                saveSettingsTo(old, MAIN_CONFIG_PATH);
                inner = loadSettingsAt(MAIN_CONFIG_PATH);
                logger.info("Settings were migrated.");
            } else {
                // create a new configuration file
                logger.info("Creating server settings file {} ...", MAIN_CONFIG_PATH);
                inner = ServerSettingsImpl.createDefault();
            }
        } else {
            // use main configuration file
            inner = loadSettingsAt(MAIN_CONFIG_PATH);
            /*
             TODO#498 (feedback needed) export this following block of code to somewhere more fitting
             Check which new UIDs from "additional*" tags are valid (SOP Class UIDs can skip this if
             they are already present in the transfer options: e.g. in SOPList) and filter them
             Validate and filter Transfer Syntaxes;
            */
            inner.getDicomServicesSettings()
                    .setAdditionalTransferSyntaxes(inner.getDicomServicesSettings().getAdditionalTransferSyntaxes()
                            .stream().filter(AdditionalTransferSyntax::isValid).collect(Collectors.toList()));
            TransfersStorage.completeList();
            // Validate and filter SOP Classes; add them to SOPList
            inner.getDicomServicesSettings()
                    .setAdditionalSOPClasses(inner.getDicomServicesSettings().getAdditionalSOPClasses().stream()
                            .filter(AdditionalSOPClass::isValid).collect(Collectors.toList()));
            SOPList.getInstance().updateList();
        }
    }

    /** Save the internal settings.
     */
    public static void saveSettings() {
        try {
            try (OutputStream ostream = Files.newOutputStream(MAIN_CONFIG_PATH)) {
                mapper.writeValue(ostream, inner);
            }
        } catch (Exception ex) {
            logger.error("Failed to save server settings", ex);
        }
    }

    // independent static methods

    /** Load settings in the new format from a URL. */
    public static ServerSettings loadSettingsAt(URL url) throws IOException {
        Objects.requireNonNull(url);
        try (InputStream istream = url.openStream()) {
            return mapper.readValue(istream, ServerSettingsImpl.class);
        }
    }

    @Deprecated
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
        ObjectMapper mapper = createObjectMapper();
        try (InputStream istream = Files.newInputStream(path)) {
            return mapper.readValue(istream, ServerSettingsImpl.class);
        }
    }

    /** Load settings in the new format from a file. */
    public static ServerSettings loadSettingsAt(File file) throws IOException {
        ObjectMapper mapper = createObjectMapper();
        try (InputStream istream = new FileInputStream(file)) {
            return mapper.readValue(istream, ServerSettingsImpl.class);
        }
    }

    /** Save the given settings to a path in the file system. This method will always
     * output settings according to the new format.
     */
    public static void saveSettingsTo(ServerSettings settings, Path path) throws IOException {
        try (OutputStream ostream = Files.newOutputStream(path)) {
            mapper.writeValue(ostream, settings);
        }
    }

    /** Save the global settings to a path in the file system.
     */
    public static void saveSettingsTo(Path path) throws IOException {
        if (inner == null)
            throw new IllegalStateException("Server settings not initialized");
        saveSettingsTo(inner, path);
    }

}
