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

import java.io.File;
import java.io.Serializable;

/**
 * This Class represents one Remote File
 * is useful to see the properties of a file
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class RemoteFile implements Serializable {
    static final long serialVersionUID = 1L;

    private String filePath;
    private String name;
    
    private boolean canRead;
    private boolean canWrite;
    private boolean canExecute;

    private boolean isDirectory;
    private boolean isFile;
    private boolean isHidden;

    private long length;
    

    public RemoteFile(File file){
        filePath = file.getAbsolutePath();
        name = file.getName();

        if(name.equals("") && System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1)
        {
            name = file.getPath();
        }
        else if(name.equals(""))
            name = "/";


        canRead = file.canRead();
        canWrite = file.canWrite();
        canExecute = file.canExecute();

        isDirectory = file.isDirectory();
        isFile = file.isFile();
        isHidden = file.isHidden();

        length = file.length();
        
    }

    /**
     *
     * @return the current path of the remote file
     */
    public String getPath(){
        return filePath;
    }

    /**
     *
     * @return the name of the remote file
     */
    public String getName(){
        return name;
    }

    /**
     *
     * @return true if the server have the permitions to read the file
     */
    public boolean canRead(){
        return canRead;
    }

    /**
     *
     * @return true if the server have the permitions to write in file
     */
    public boolean canWrite(){
        return canWrite;
    }

    /**
     *
     * @return true if the server have the permitions to execute the file
     */
    public boolean canExecute(){
        return canExecute;
    }

    /**
     *
     * @return true if the file is one directory
     */
    public boolean isDirectory(){
        return isDirectory;
    }

    /**
     *
     * @return if the file is not a directory
     */
    public boolean isFile(){
        return isFile;
    }

    /**
     *
     * @return true if the file is hidden
     */
    public boolean isHidden(){
        return isHidden;
    }

    /**
     *
     * @return the file length (in bytes)
     */
    public long length(){
        return length;
    }

    /**
     *
     * @return the name of the file
     */
    @Override
    public String toString(){
        return this.getName();
    }

    /**
     * Test if the file is equals to another
     * 
     * @param other
     * @return
     */
    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        if (other == this) {
            return true;
        }

        RemoteFile tmp = (RemoteFile) other;

        if (filePath.equals(tmp.filePath) && name.equals(tmp.name) && canRead == tmp.canRead
                && canWrite == tmp.canWrite && canExecute == tmp.canExecute
                && isDirectory == tmp.isDirectory && isFile == tmp.isFile
                && isHidden == tmp.isHidden && length == tmp.length) {
            return true;
        }

        return false;
    }

}
