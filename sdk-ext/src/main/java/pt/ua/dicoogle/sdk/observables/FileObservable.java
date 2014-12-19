/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk-ext.
 *
 * Dicoogle/dicoogle-sdk-ext is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk-ext is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk.observables;

import java.io.Serializable;
import java.util.Observable;

/**
 *
 * @author Carlos Ferreira
 */
public class FileObservable extends Observable implements Serializable
{
    private String fileOrigin;
    private String fileName;
    private String filePath = null;

    public FileObservable(String fileOrigin, String fileName)
    {
        this.fileOrigin = fileOrigin;
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getFileOrigin()
    {
        return fileOrigin;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public boolean isCompleted()
    {
        return (this.filePath != null);
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
        this.setChanged();
        this.notifyObservers();
    }
}
