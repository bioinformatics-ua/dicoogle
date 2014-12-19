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

/**
 *  We don't derive directly from inputStream because instances of this class
 * are returned when asking for listings of resources. In that case we don't want to
 * return an opened stream, hence it must be manually required.
 * 
 * @author Frederico Valente
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public interface StorageInputStream {
    public abstract URI getURI();
    public abstract InputStream getInputStream() throws IOException;
    public abstract long getSize() throws IOException;
}
