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
package pt.ua.dicoogle.rGUI.interfaces.controllers;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Directory Settings interface
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public interface IDirectory extends Remote {

    /**
     *
     * @param path
     * @return  0 - Ok
     *          1 - Not a directory
     *          2 - Can't read
     *          3 - Can't write
     * @throws RemoteException
     */
    public int setStoragePath(String path) throws RemoteException;
    public String getStoragePath() throws RemoteException;

    /**
     *
     * @param path
     * @return  0 - Ok
     *          1 - Not a directory
     *          2 - Can't read
     *          3 - Can't write
     * @throws RemoteException
     */
    public int setDicoogleDir(String path) throws RemoteException;
    public String getDicoogleDir() throws RemoteException;

    /**
     *
     * @param effort
     * @return  true - OK
     *          false - wrong effort 0 <= effort <= 100
     * @throws RemoteException
     */
    public boolean setIndexerEffort(int effort) throws RemoteException;
    public int getIndexerEffort() throws RemoteException;

    public void setSaveThumbnails(boolean value) throws RemoteException;
    public boolean getSaveThumbnails() throws RemoteException;

    public void setThumbnailsMatrix(String ThumbnailsMatrix) throws RemoteException;
    public String getThumbnailsMatrix() throws RemoteException;

    /**
     *
     * @return  0 - OK
     *          1 - Defined storage path is invalid
     *          2 - Server running
     * @throws RemoteException
     */
    public int rebuildIndex() throws RemoteException;

    /**
     *
     * @return  0 - OK
     *          1 - Defined storage path is invalid
     *          2 - Server running
     * @throws RemoteException
     */
    public int rebuildDICOMDir() throws RemoteException;


    public boolean isIndexZip() throws RemoteException;

    public void setIndexZip(boolean value) throws RemoteException;
    
    
    public boolean isGZipStorage() throws RemoteException;

    public void setGZipStorage(boolean value) throws RemoteException;
    
    public boolean isIndexAnonymous() throws RemoteException;

    public void setIndexAnonymous(boolean value) throws RemoteException;
    
    
    
    
    public void setMonitorWatcher(boolean monitorWatcher) throws RemoteException;
    
    public boolean isMonitorWatcher() throws RemoteException;


}
