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
package pt.ua.dicoogle.common;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 * This class is used to filter file types (extensions) on JFileChooser
 *
 * @author Samuel da Costa Campos <samuelcampos@ua.pt>
 *
 */
public class ExtensionFilter extends FileFilter {

    private String extensions[];
    private String description;

    public ExtensionFilter(String description, String extension) {
        this(description, new String[]{extension});
    }

    public ExtensionFilter(String description, String extensions[]) {
        this.description = description;
        this.extensions = (String[]) extensions.clone();
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        int count = extensions.length;
        String path = file.getAbsolutePath();
        for (int i = 0; i < count; i++) {
            String ext = extensions[i];
            if (path.endsWith(ext)
                    && (path.charAt(path.length() - ext.length()) == '.')) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return (description == null ? extensions[0] : description);
    }
}
