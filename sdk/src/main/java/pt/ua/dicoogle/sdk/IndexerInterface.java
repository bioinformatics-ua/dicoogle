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
import java.util.Collection;

import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * Index Interface Plugin. Indexers analyze documents for performing queries. They may index
 * documents by DICOM metadata for instance, but other document processing procedures may be involved.
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Frederico Valente <fmvalente@ua.pt>
 */
public interface IndexerInterface extends DicooglePlugin {

    /**
     * Indexes the file path to the database. Indexation procedures are asynchronous, and will return
     * immediately after the call. The outcome is a report that can be retrieved from the given task
     * as a future.
     *
     * @param file directory or file to index
     * @return a representation of the asynchronous indexation task
     */
    public Task<Report> index(StorageInputStream file, Object... parameters);

    /**
     * Indexes multiple file paths to the database. Indexation procedures are asynchronous, and will return
     * immediately after the call. The outcomes are aggregated into a single report and can be retrieved from
     * the given task as a future.
     *
     * @param files a collection of directories and/or files to index
     * @return a representation of the asynchronous indexation task
     */
    public Task<Report> index(Iterable<StorageInputStream> files, Object... parameters);

    /**
     * Checks whether the file in the given path can be indexed by this indexer. The indexer should verify if
     * the file holds compatible content (e.g. a DICOM file). If this method returns false, the file will not
     * be indexed.
     *
     * @param path a URI to the file to check
     * @return whether the indexer can handle the file at the given path
     */
    public default boolean handles(URI path) {
        return true;
    }

    /**
     * Removes the indexed file at the given path from the database.
     * 
     * This operation is synchronous.
     * 
     * @param path the URI of the document
     * @return whether it was successfully deleted from the database
     */
    public boolean unindex(URI path);

    /**
     * Removes indexed files from the database in bulk.
     *
     * The default implementation unindexes each item one by one
     * in a non-specified order via {@linkplain #unindex(URI)},
     * but indexers may implement this as
     * one or more individual operations in batch,
     * thus becoming faster than unindexing each item individually.
     * 
     * This operation is synchronous.
     * Consider running long unindexing tasks in a separate thread.
     *
     * @param uris the URIs of the items to unindex
     * @return the number of files successfully unindexed,
     *         in the event that some entries were not found in the database
     */
    public default int unindex(Collection<URI> uris) {
        int unindexed = 0;
        for (URI uri : uris) {
            unindexed += unindex(uri) ? 1 : 0;
        }
        return unindexed;
    }
}
