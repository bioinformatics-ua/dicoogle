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
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class RemoteFileSystemServer implements IRemoteFileSystem {

    private FileSystemView fs = FileSystemView.getFileSystemView();

    private static Semaphore sem = new Semaphore(1, true);
    private static RemoteFileSystemServer instance = null;

    public static synchronized RemoteFileSystemServer getInstance() {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new RemoteFileSystemServer();
            }
            sem.release();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(RemoteFileSystemServer.class).error(ex.getMessage(), ex);
        }

        return instance;
    }

    private RemoteFileSystemServer(){
        
    }

    /**
     * get FilePath from root to actualFile (One RemoteFile for each folder)
     *
     * @param filePath
     * @return
     */
    @Override
    public RemoteFile[] getFilePath(String filePath) {
        if(filePath == null || filePath.equals(""))
            filePath = ".";

        ArrayList<RemoteFile> files = new ArrayList<RemoteFile>();
        RemoteFile rFile;

        File file = new File(filePath);
        rFile = new RemoteFile(file);

        if(file.isDirectory())
            files.add(rFile);

        while(rFile != null && !isRoot(rFile.getPath())){
            rFile = getParentDirectory(rFile.getPath());

            if (rFile != null)
                files.add(rFile);
        }

        if(System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1)
        {
            File[] roots = File.listRoots();
            
            for(File f: roots)
                if(f.exists())
                    files.add(new RemoteFile(f));
        }

        RemoteFile[] rFilesArray = new RemoteFile[files.size()];
        return files.toArray(rFilesArray);
    }

    @Override
    public RemoteFile[] getFiles(String dirPath, boolean useFileHiding) {
        if(dirPath == null || dirPath.equals(""))
            dirPath = ".";


        File dir = new File(dirPath);
        
        if (!dir.exists())
           dir = new File(".");
        
        File[] file = fs.getFiles(dir, useFileHiding);

        
        RemoteFile[] remoteFile = new RemoteFile[file.length];

        for(int i = 0; i < file.length; i++)
            remoteFile[i] = new RemoteFile(file[i]);

        return remoteFile;
    }

    @Override
    public boolean isFileSystemRoot(String dirPath) {
        File dir = new File(dirPath);

        if (!dir.exists())
           return false;
        
        return fs.isFileSystemRoot(dir);
    }

    @Override
    public boolean isRoot(String dirPath){
        File dir = new File(dirPath);

        if (!dir.exists())
           return false;

        return fs.isRoot(dir);
    }

    @Override
    public boolean isFileSystem(String dirPath) {
        File f = new File(dirPath);

        if (!f.exists())
           return false;

        return fs.isFileSystem(f);
    }

    @Override
    public boolean isDrive(String dirPath) {
        File dir = new File(dirPath);

        return fs.isDrive(dir);
    }

    @Override
    public boolean isComputerNode(String dirPath) {
        File dir = new File(dirPath);

        if (!dir.exists())
           dir = new File(".");
        
        return fs.isComputerNode(dir);
    }

    @Override
    public RemoteFile getParentDirectory(String dirPath) {
        File dir = new File(dirPath);
        
        if (!dir.exists())
           dir = new File(".");
        
        if (fs.isRoot(dir))
            return null;

        if(System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1){
            if(dir.getAbsolutePath().lastIndexOf('\\') == (dir.getAbsolutePath().length()-1) )
                return null;
        }
        
        File parent = fs.getParentDirectory(dir);

        if(parent != null)
            return new RemoteFile(parent);

        return null;
    }

    @Override
    public String getSystemDisplayName(String filePath) {
        File f = new File(filePath);

        return fs.getSystemDisplayName(f);
    }

    @Override
    public String getSystemTypeDescription(String filePath) {
        File f = new File(filePath);

        return fs.getSystemTypeDescription(f);
    }

    @Override
    public RemoteFile getDefaultDirectory() {
        return new RemoteFile(fs.getDefaultDirectory());
    }

    @Override
    public RemoteFile getHomeDirectory() {
        return new RemoteFile(fs.getHomeDirectory());
    }

    @Override
    public RemoteFile[] getRoots() {
        File[] file = fs.getRoots();

        RemoteFile[] remoteFile = new RemoteFile[file.length];

        for(int i = 0; i < file.length; i++)
            remoteFile[i] = new RemoteFile(file[i]);

        return remoteFile;
    }
}
