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
package pt.ua.dicoogle.server;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.media.ApplicationProfile;
import org.dcm4che2.media.BasicApplicationProfile;
import org.dcm4che2.media.DicomDirReader;
import org.dcm4che2.media.DicomDirWriter;
import org.dcm4che2.media.FileSetInformation;



/**
 * Creates, updates and rebuilds DICOMDIR Files.
 * @author Marco
 */
public class DicomDirCreator {
    
    private String Path;
    private String id;
    private DicomDirReader dicomdir;
    private FileSetInformation fsinfo = null;
    private ApplicationProfile ap = new BasicApplicationProfile();
    
    /**
     * Creates a new DicomDirCreator object.
     * If the DICOMDIR file doesn't exist it creates a new one,
     * otherwise it will open the existing one.
     * @param P Path to where the DICOMDIR file is going to be created.
     * @param I FilesetID
     */
    public DicomDirCreator(String P, String I)
    {
        Path = P;
        id = I;
        File file = new File(Path+File.separator+"DICOMDIR");
        if (!file.exists())
        {           
            try {
                fsinfo = new FileSetInformation();
                fsinfo.init();                
                if(id != null)
                {
                    if(!id.isEmpty())
                    {
                        fsinfo.setFileSetID(id);
                    }
                }
                dicomdir = new DicomDirWriter(file, fsinfo);
            } catch (IOException ex) {
                LoggerFactory.getLogger(DicomDirCreator.class).error(ex.getMessage(), ex);
            }
        }
        else
        {
            try {
                dicomdir = new DicomDirWriter(file);
                fsinfo = dicomdir.getFileSetInformation();
            } catch (IOException ex) {
                LoggerFactory.getLogger(DicomDirCreator.class).error(ex.getMessage(), ex);
            }
        }
     }
    
    /**
     * Updates the DICOMDIR file with a new entry
     * @param f File to be added to DICOMDIR
     */
    public synchronized void updateDicomDir(File f)
    {
        // Severe dcm4che bug fix; it had a problem building dicom directories with files / folders beginning with a dot
        if (f != null && !f.getName().startsWith("."))
        {
            if(f.isDirectory())
            {
                File[] fs = f.listFiles();
                
                for (int i = 0; i < fs.length; i++)
                    updateDicomDir(fs[i]);
               
                return;
            }
            
            // Only dcm files will be listed in the dicom directory
            if (f.getName().endsWith(".dcm"))
            {
                DicomInputStream dis = null;
                try {
                    dis = new DicomInputStream(f);
                    dis.setHandler(new StopTagInputHandler(Tag.PixelData));
                    DicomObject d = dis.readDicomObject();
                    DicomObject p_record = ap.makePatientDirectoryRecord(d);
                    DicomObject sty_record = ap.makeStudyDirectoryRecord(d);
                    DicomObject s_record = ap.makeSeriesDirectoryRecord(d);
                    DicomObject i_record = ap.makeInstanceDirectoryRecord(d, dicomdir.toFileID(f));
                    DicomObject record = ((DicomDirWriter) dicomdir).addPatientRecord(p_record);
                    record = ((DicomDirWriter) dicomdir).addStudyRecord(record, sty_record);
                    record = ((DicomDirWriter) dicomdir).addSeriesRecord(record, s_record);
                    ((DicomDirWriter) dicomdir).addChildRecord(record, i_record);
                    ((DicomDirWriter) dicomdir).commit();

                } catch (IOException ex) {
                    LoggerFactory.getLogger(DicomDirCreator.class).error(ex.getMessage(), ex);
                } finally {
                    try {
                        dis.close();
                    } catch (IOException ex) {
                        LoggerFactory.getLogger(DicomDirCreator.class).error(ex.getMessage(), ex);
                    }
                }
            }
        }
   }
    
     /**
     * Rebuilds the DICOMDIR file, by rescanning the storage path.
     * To avoid conflicts should only be called when the server is not running.
     */
    public synchronized void dicomdir_rebuild()
    {
        try {
            dicomdir.close();
            File file = new File(Path + File.separator + "DICOMDIR");
            file.delete();
            File f = new File(Path);            
            fsinfo = new FileSetInformation();
                fsinfo.init();                
                if(id != null)
                {
                    if(!id.isEmpty())
                    {
                        fsinfo.setFileSetID(id);
                    }
                }
                dicomdir = new DicomDirWriter(file, fsinfo);                
                updateDicomDir(f);                                 
        } catch (IOException ex) {
            LoggerFactory.getLogger(DicomDirCreator.class).error(ex.getMessage(), ex);
        }
    }
    
    /**
     * Closes the DICOMDIR Reader/Writer     
     */
    public void dicomdir_close()
    {
        try {
            dicomdir.close();
        } catch (IOException ex) {
            LoggerFactory.getLogger(DicomDirCreator.class).error(ex.getMessage(), ex);
        }
    }
    
    
}
