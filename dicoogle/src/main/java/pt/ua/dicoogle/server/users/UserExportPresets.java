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


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import pt.ua.dicoogle.sdk.Utils.Platform;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Saves and lists user's presets for fields to be exported.
 *
 * @author Leonardo Oliveira <leonardooliveira@ua.pt>
 */
public class UserExportPresets {

    /**
     * Saves a user's preset.
     *
     * @param username the username the preset belongs to
     * @param presetName the preset description
     * @param fields the list of fields that make the preset
     * @throws IOException if an I/O error occurs
     */
    public static void savePreset(String username, String presetName, String[] fields) throws IOException {
        File presetsDir = new File(Platform.homePath() + "users/" + username + "/presets");

        // create presets dir, if it doesn't exist
        Files.createDirectories(presetsDir.toPath());

        File presetFile = new File(presetsDir + "/" + presetName + ".txt");

        try (Writer writer = new PrintWriter(presetFile)) {
            for (String field: fields) {
                writer.write(field);
                writer.write('\n');
            }
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
        File presetsDir = new File(Platform.homePath() + "users/" + username + "/presets/");

        Map<String, String[]> presets = new HashMap<>();

        if (!presetsDir.isDirectory()) {
            return presets;
        }

        for (File presetFile: presetsDir.listFiles()) {
            String[] fields = FileUtils.readFileToString(presetFile, "UTF-8").split("\n");
            presets.put(FilenameUtils.removeExtension(presetFile.getName()), fields);
        }

        return presets;
    }
}
