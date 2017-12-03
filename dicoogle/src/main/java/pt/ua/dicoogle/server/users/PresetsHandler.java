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


import pt.ua.dicoogle.sdk.Utils.Platform;

import java.io.*;
import java.util.*;

/**
 * Saves and lists user's presets for fields to be exported.
 *
 * @author Leonardo Oliveira <leonardooliveira@ua.pt>
 */
public class PresetsHandler {

    public static boolean savePreset(String username, String presetName, String presetContent) {
        File presetsDir = new File(Platform.homePath() + "users/" + username + "/presets");

        if (!presetsDir.isDirectory()) {
            // try to create non-existent dir
            if (!presetsDir.mkdirs()) {
                System.out.println("NOOO!");
                return false;
            }
        }

        File presetFile = new File(presetsDir + "/" + presetName + ".txt");

        // stop if a file with the same name already exists
        if (presetFile.isFile()) {
            return false;
        }

        try {
            Writer writer = new PrintWriter(presetFile);
            writer.write(presetContent);
            writer.close();

        } catch (Exception e) {
            presetFile.delete();
            return false;
        }

        return true;
    }

    public static Map getPresets(String username) {
        File presetsDir = new File(Platform.homePath() + "users/" + username + "/presets/");

        Map<String, String> presets = new HashMap<>();

        if (!presetsDir.isDirectory()) {
            return presets;
        }

        for (File presetFile: presetsDir.listFiles()) {
            try {
                // read all lines
                String presetContent = new Scanner(presetFile).useDelimiter("\\Z").next();
                presets.put(presetFile.getName(), presetContent);

            } catch (FileNotFoundException e) {
                // this should never happen, because the file exists
                return null;
            }
        }

        return presets;
    }
}
