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
package pt.ua.dicoogle.server.web.rest;

import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.core.Version;

/**
 * Created by bastiao on 23/09/15.
 */
public class VersionResource extends ServerResource {

    @Get("json")
    public JsonRepresentation represent() {
        
        Version v = new Version();
        JsonRepresentation sr = new JsonRepresentation("{\"version\":\""+v.getVersion()+
                "\"}");

        sr.setMediaType(MediaType.APPLICATION_JSON);

        return sr;

    }
    
    @Override
    public String toString(){
        return "version";
    }
}
