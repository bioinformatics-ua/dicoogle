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
package pt.ua.dicoogle.sdk;

import java.io.IOException;
import java.net.URI;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;

/**
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Frederico Valente
 */
public interface StorageInterface extends DicooglePlugin {    
    
    /**
     * Gets the Scheme of this storage plug-in URI.
     *
     * @see URI
     * @return String denoting the scheme
     */
    public String getScheme();
    
    /**
     * 
     * @param location, only the scheme matters
     * @return true if this storage plugin is in charge of uris in the given form 
     */
    public boolean handles(URI location);
    
    /**
     * Allows iteration over a specified location 
     * nice to use in foreach loops
     * the scheme is redundant when calling directly in the interface
     * 
     * for(StorageInputStream dicomObj : storagePlugin.at("file://dataset/")){
     *      System.err.println(dicomObj.getURI());
     * }
     * 
     */
    public Iterable<StorageInputStream> at(URI location);
    
    /**
     * Stores a DicomObject in the Storage Plug-in
     *
     * @param dicomObject Object to be Stored
     * @return The URI of the previously stored Object.
     */
    public URI store(DicomObject dicomObject);

    /**
     * Stores a InputStream in the Storage
     *
     * @param inputStream Stream to be stored.
     * @return The URI of the previously stored data.
     * @throws IOException If anything goes wrong
     */
    public URI store(DicomInputStream inputStream) throws IOException;
    
    public void remove(URI location);
}
