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
package pt.ua.dicoogle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.LoggerFactory;
import javax.swing.filechooser.FileSystemView;

/**
 * System information methods
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author psytek
 */
public class SystemInfo{

    public static File getHomeDirectory(){
        File result = null;
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1){
            try {
                Process p = Runtime.getRuntime().exec("cmd /c echo %HOMEDRIVE%%HOMEPATH%");
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                try{
                    p.waitFor();
                }
                catch (InterruptedException e) {
                    throw new IOException("Interrupted: " + e.getMessage());
                }
                result = new File(reader.readLine());
            }
            catch (IOException ex) {
                LoggerFactory.getLogger(SystemInfo.class.getName()).error(ex.getMessage(), ex);
            }
        }
        else{
            result = FileSystemView.getFileSystemView().getHomeDirectory();
        }
        return result;
    }
}
