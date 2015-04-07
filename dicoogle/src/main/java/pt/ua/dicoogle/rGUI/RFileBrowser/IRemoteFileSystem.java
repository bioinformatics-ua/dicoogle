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


import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface provides the main methods to browse a remote File System
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public interface IRemoteFileSystem extends Remote {

    //File createFileObject(String path) throws RemoteException;
    //File createFileObject(File dir, String filename) throws RemoteException;
    //File getChild(File parent, String fileName) throws RemoteException;
    //boolean isFloppyDrive(File dir) throws RemoteException;
    //File createNewFolder(File containingDir) throws RemoteException, IOException;
    //Icon getSystemIcon(File f) throws RemoteException;


    /**
     * get FilePath from root to actualFile (One RemoteFile for each folder)
     *
     * @param filePath
     * @return
     * @throws RemoteException
     */
    RemoteFile[] getFilePath(String filePath) throws RemoteException;

    RemoteFile[] getFiles(String dirPath, boolean useFileHiding) throws RemoteException;

    boolean isFileSystemRoot(String dirPath) throws RemoteException;

    boolean isFileSystem(String dirPath) throws RemoteException;

    boolean isDrive(String dirPath) throws RemoteException;

    boolean isRoot(String dirPath) throws RemoteException;

    boolean isComputerNode(String dirPath) throws RemoteException;

    RemoteFile getParentDirectory(String dirPath) throws RemoteException;

    String getSystemDisplayName(String filePath) throws RemoteException;

    String getSystemTypeDescription(String filePath) throws RemoteException;

    RemoteFile getDefaultDirectory() throws RemoteException;

    RemoteFile getHomeDirectory() throws RemoteException;

    RemoteFile[] getRoots() throws RemoteException;
}
