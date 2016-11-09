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

package pt.ua.dicoogle.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class Zip implements Closeable
{

    static final int BUFFER = 2048;

    private BufferedInputStream origin = null;
    private ZipOutputStream out = null;

    private String zipName = null;

    private boolean createFile = false;

    public Zip(String  zipName)
    {
        this.zipName = zipName;
    }

    public void createZip()
    {

        // just do it one-time.
        if (createFile)
            return;

        ServerSettings ss = ServerSettingsManager.getSettings() ;

        try 
        {
            System.out.println("DIR: "+getZipName());
            FileOutputStream dest = new FileOutputStream(getZipName());
            
             out = new ZipOutputStream(new 
           BufferedOutputStream(dest));


            //out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];


            createFile = true;
            
        
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public void addFile(byte [] arr)
    {

        
    }



    public void addFile(String fullPath)
    {

        File file = new File(fullPath);
        byte data[] = new byte[BUFFER];
        FileInputStream fi = null;
        try
        {
            fi = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            LoggerFactory.getLogger(Zip.class).error(ex.getMessage(), ex);
        }
        origin = new BufferedInputStream(fi, BUFFER);
        ZipEntry entry = new ZipEntry(fullPath);
        if (out==null)
            System.err.println("OUT NULL");


        if (entry==null)
            System.err.println("entry NULL");

        try
        {
            out.putNextEntry(entry);
        } catch (IOException ex) {
            LoggerFactory.getLogger(Zip.class).error(ex.getMessage(), ex);
        }
        int count;
        try {
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
        } catch (IOException ex) {
            LoggerFactory.getLogger(Zip.class).error(ex.getMessage(), ex);
        }
        try {
            origin.close();
        } catch (IOException ex) {
            LoggerFactory.getLogger(Zip.class).error(ex.getMessage(), ex);
        }

    }

    public void close()
    {
        try {
            out.close();
        } catch (IOException ex) {
            LoggerFactory.getLogger(Zip.class).error(ex.getMessage(), ex);
        }

    }

    /**
     * @return the createFile
     */
    public boolean isCreateFile() {
        return createFile;
    }

    /**
     * @param createFile the createFile to set
     */
    public void setCreateFile(boolean createFile) {
        this.createFile = createFile;
    }

    /**
     * @return the zipName
     */
    public String getZipName() {
        return zipName;
    }

    /**
     * @param zipName the zipName to set
     */
    public void setZipName(String zipName) {
        this.zipName = zipName;
    }

    
    
    public static void main(String [] args)
    {
    
    
        long timeEncryptStart = System.currentTimeMillis();
        Zip zip = new Zip("/Volumes/Extend/dataset/IM-0001-0001.zip");
        zip.createZip();
        zip.addFile("/Volumes/Extend/dataset/XABraga/IM-0001-0001.dcm");
        
        zip.close();
                  
            
            long timeEncryptEnd = System.currentTimeMillis() - timeEncryptStart;
            System.out.println("Encrypted in " + timeEncryptEnd + " (ms)");
            
            timeEncryptStart = System.currentTimeMillis();
            
            UnZip unzip = new UnZip("/Volumes/Extend/dataset/IM-0001-0001.zip");
            unzip.loadFile();
            unzip.decompress();
            
            timeEncryptEnd = System.currentTimeMillis() - timeEncryptStart;
            System.out.println("Encrypted in " + timeEncryptEnd + " (ms)");
            
            
    }
    
    
}
