/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk.
 *
 * Dicoogle/dicoogle-sdk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk.settings;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author: Frederico Valente
 * 
 * This object will be serialized to disk before shutdown and on configuration save.
 * 
 */
public class Settings{
    
    private static final Logger log = LoggerFactory.getLogger(Settings.class);
    
    File settingsXmlFile; //xml file handler
    String settingsXmlString;//xml file contents
    
    /**
     * 
     * @param settingsXmlFile an xml file pointing to the settings
     * @throws IOException 
     */
    public Settings(File settingsXmlFile) throws IOException{
        log.debug("Loading settings from: {}", settingsXmlFile.getAbsolutePath());
        this.settingsXmlFile = settingsXmlFile;
        
        //creates the settings file if it does not exist
        if(!settingsXmlFile.exists()){
            settingsXmlFile.createNewFile();
            settingsXmlString = "";
            return;
        }
        
        reload();
    }

    /**
     * Given an xml string (possibly from an editor), makes it the current settings and stores it in disk
     * @param xml
     * @throws IOException 
     */
    public void setXml(String xml) throws IOException{
        settingsXmlString = xml;
        save();
    }
    
    /**
     * reloads the xml file
     */
    public final void reload() throws IOException{
        //we have a settings file, lets read it to the string
        try (FileInputStream stream = new FileInputStream(settingsXmlFile)) {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            String xmlSettings = Charset.defaultCharset().decode(bb).toString();
            settingsXmlString = xmlSettings;
        }
        
        log.debug("Settings for: {}", settingsXmlFile.getAbsolutePath());
        log.debug(settingsXmlString);
        log.debug("...");
    }
    
    /**
    * Saves the settings xml to disk (on the path provided to the constructor)
    * TODO: this is poorly done, we should save to a tmp file and then move the files
    * as opposed to just write on top of the old file
    * TODO: also, we should validate the xml
    */
    public void save() throws IOException{
        try(FileOutputStream fileOutputStream = new FileOutputStream(settingsXmlFile, true)){
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(settingsXmlString.getBytes(Charset.defaultCharset()));
            bufferedOutputStream.flush();
        }
    }
    
    /**
     * 
     * @return the xml contents of the file
     */
    public String getXml(){return settingsXmlString;}
    
    /*
     * Helper methods below
     */
    public String field(String name){return null;}
    public int fieldAsInt(String name){return 0;}
    public double fieldAsDouble(String name){return 0.0;}
}
