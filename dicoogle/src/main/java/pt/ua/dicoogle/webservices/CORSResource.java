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
package pt.ua.dicoogle.webservices;

import java.util.concurrent.ConcurrentMap;
import jdk.net.SocketFlow;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Options;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

/**
 * A resource for handling a CORS-compliant OPTIONS call.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class CORSResource extends ServerResource {

    @Options
    public void doOptions(Representation entity) {
        ConcurrentMap<String,Object> attributes = getResponse().getAttributes();
        Series<Header> responseHeaders = (Series) attributes.get(HeaderConstants.ATTRIBUTE_HEADERS);
        if (responseHeaders == null) {
            responseHeaders = new Series(Header.class);
            attributes.put(HeaderConstants.ATTRIBUTE_HEADERS, responseHeaders);
        }
        responseHeaders.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
        responseHeaders.add("Access-Control-Allow-Credentials", "false");
        responseHeaders.add("Access-Control-Max-Age", "60");
    }

}
