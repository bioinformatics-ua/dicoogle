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

/**
 * Contains the list of Dicoogle Settings 
 * 
 * <p>
 * <b> This list can be accessed by the Plugins</b>
 * </p>
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public interface CoreSettings
{
    /**
     * This option is related with the de-identification of the patient data
     * If this option is enable the documents will be indexed and shown in an
     * anonymous format. 
     * @return boolean true if anonymize is enable, false otherwise. 
     */
    public boolean isIndexAnonymous();
    
    /**
     * If this option is enable, the database will store a thumbnail of each 
     * image 
     * @return boolean true if is necessary to create thumbnail, false otherwise
     */
    public boolean isThumbnails();
    /**
     * Get the matrix size of the thumbnail
     * 
     * @return String matrix size in the format numberXnumber, for example,
     * 8x8 or 16x16 pixels 
     */
    public String getThumbnailsMatrix();
    
}
