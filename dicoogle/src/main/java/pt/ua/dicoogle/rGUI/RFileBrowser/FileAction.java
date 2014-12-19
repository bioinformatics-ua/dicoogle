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
package pt.ua.dicoogle.rGUI.RFileBrowser;

/**
 * Class that defines an action to do when the file is selected
 *
 * This is an abstract class that needs to be implemented
 * when RemoteFileBrowser is used.
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public abstract class FileAction{
    
    /**
     * Defines an action to do when the file is choosed
     * @param filePath
     */
    public abstract void setFileChoosed(String filePath);
}
