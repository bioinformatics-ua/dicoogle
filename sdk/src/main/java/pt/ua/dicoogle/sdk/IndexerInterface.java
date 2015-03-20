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

import java.net.URI;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * Represents the Index Interface Plugin. It is related with the storage of the
 * document, for instance, DICOM metadata.
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Frederico Valente <fmvalente@ua.pt>
 */
public interface IndexerInterface extends DicooglePlugin {

    /**
     * Index the file path to the database It can be a directory
     *
     * @param file
     * @return 
     */
    public Task<Report> index(StorageInputStream file);

    public Task<Report> index(Iterable<StorageInputStream> files);

    public boolean handles(URI uri);
    
    
    /* 
     * Remove the entry in the database
     * 
     * @param uri URI of the document
     * @return boolean true if it was deleted from database, false otherwise.
     * @see URI
     */
    public boolean unindex(URI uri);
}
