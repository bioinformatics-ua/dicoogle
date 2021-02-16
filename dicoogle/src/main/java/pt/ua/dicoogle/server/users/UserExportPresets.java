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
package pt.ua.dicoogle.server.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.sdk.Utils.Platform;
import pt.ua.dicoogle.server.web.DicoogleWeb;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Saves and lists user's presets for fields to be exported.
 *
 * @author Leonardo Oliveira <leonardooliveira@ua.pt>
 */
public class UserExportPresets {
    private static final Logger logger = LoggerFactory.getLogger(UserExportPresets.class);

    /**
     * Saves a user's preset.
     *
     * @param username the username the preset belongs to
     * @param presetName the preset description
     * @param fields the list of fields that make the preset
     * @throws IOException if an I/O error occurs
     */
    public static void savePreset(String username, String presetName, String[] fields) throws IOException {
        Path presetsDir = Paths.get(Platform.homePath(), "userdata", username, "presets");

        Files.createDirectories(presetsDir);

        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(presetsDir.resolve(presetName)))) {
            out.writeObject(fields);
        } catch (IOException e) {
            logger.error("Could not save preset.", e.getMessage());
        }
    }

    /**
     * Gets a user's presets.
     *
     * @param username the username the presets belong to
     * @return a map with the association of the presets' descriptions with their list of fields
     * @throws IOException if an I/O error occurs
     */
    public static Map<String, String[]> getPresets(String username) throws IOException {
        Path presetsDir = Paths.get(Platform.homePath(), "userdata", username, "presets");

        Map<String, String[]> presets = new HashMap<>();

        if (!Files.isDirectory(presetsDir)) {
            return presets;
        }

        Stream<Path> fileList = Files.list(presetsDir);
        fileList.filter(file -> !Files.isDirectory(file))
                .forEach(file -> {
                    try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
                        String[] preset = (String[]) in.readObject();
                        presets.put(file.getFileName().toString(), preset);
                    } catch (IOException | ClassNotFoundException e) {
                        logger.error("Could not read preset.", e.getMessage());
                    }
                });

        return presets;
    }
}
