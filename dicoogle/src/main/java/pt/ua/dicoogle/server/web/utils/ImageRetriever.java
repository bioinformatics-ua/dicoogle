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

package pt.ua.dicoogle.server.web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/** Interface for retrieving the image content of an instance, in a format compatible with browsers.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public interface ImageRetriever {

    /** Obtain an image by URI.
     * 
     * @param uri the URI to the image
     * @param frame the frame number
     * @param thumbnail whether to retrieve a thumbnail (true) or the image in its original size (false)
     * @return an input stream for retrieving the image's content
     * @throws IOException 
     */
    public InputStream get(URI uri, int frame, boolean thumbnail) throws IOException;
}
