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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ServerSettings
{
    private static final Logger logger = LoggerFactory.getLogger(ServerSettings.class);

    static {
        init();
    }

    private static void init() {

    }

    public static ServerSettings getSettingsAt(URL url) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public static ServerSettings getLegacySettings() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public static ServerSettings getSettingsAt(File file) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public static ServerSettings getSettingsAt(String filePath) throws IOException {
        return getSettingsAt(new File(filePath));
    }

    public static void saveSettingsTo(ServerSettings settings, String path) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

}
