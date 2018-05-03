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
import java.io.InputStream;
import java.net.URI;

/** An entity containing the means to read a file or a blob via Dicoogle.
 * Instances of this class are returned when asking for listings of resources, and do not open an input
 * stream automatically, thus why the class does not extend {@code InputStream}.
 * 
 * @author Frederico Valente
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public interface StorageInputStream {
    /** Obtains the file's path.
     * 
     * @return the URI path of the file
     */
    public URI getURI();
    
    /** Obtains a new input stream for reading the file.
     * 
     * @return a new input stream
     * @throws IOException if an I/O error occurs
     */
    public InputStream getInputStream() throws IOException;
    
    /** Obtains the file's size.
     * 
     * @return the storage element's size in byte
     * @throws IOException if an I/O error occurs
     */
    public long getSize() throws IOException;
}
