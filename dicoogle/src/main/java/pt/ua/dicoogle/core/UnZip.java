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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class UnZip
{
    final int BUFFER = 2048;

    private String filePath = null;
    private ZipInputStream zis = null;
    private BufferedOutputStream dest = null;
    private CheckedInputStream checksum = null;


    public UnZip(String filePath)
    {
        this.filePath = filePath;
    }

    public void loadFile()
    {

        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(this.filePath);
        }
        catch (FileNotFoundException ex) {
            LoggerFactory.getLogger(UnZip.class).error(ex.getMessage(), ex);
        }
        checksum = new CheckedInputStream(fis, new Adler32());
        
        
        zis = new ZipInputStream(new BufferedInputStream(checksum));

      }
        


    public void decompress()
    {


        if (zis==null)
            return;
        
        ZipEntry entry;
        try {
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println("Extracting: " + entry);
                int count;
                byte[] data = new byte[BUFFER];
                // write the files to the disk
                FileOutputStream fos = new FileOutputStream(entry.getName());
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
        } catch (IOException ex) {
            LoggerFactory.getLogger(UnZip.class).error(ex.getMessage(), ex);
        }


    }

    public void close()
    {

        try
        {
            zis.close();
        } catch (IOException ex)
        {
            LoggerFactory.getLogger(UnZip.class).error(ex.getMessage(), ex);
        }
        System.out.println("Checksum:"+checksum.getChecksum().getValue());


    }

}
