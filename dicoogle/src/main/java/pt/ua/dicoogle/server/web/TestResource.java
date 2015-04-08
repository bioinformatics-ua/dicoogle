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

package pt.ua.dicoogle.server.web;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class TestResource extends ServerResource {

    @Get("txt")
    public String testGet() {
        throw new ResourceException(204);
//                return "extended GET!";
    }

    @Post("txt:txt")
    public String testPost(String something) {
        return "extended POST: " + something;
    }

    @Put("txt:txt")
    public String testPut(String something) {
        return "extended PUT: " + something;
    }

    @Delete("txt")
    public String testDelete() {
        return "extended DELETE!";
    }

    public TestResource() {
        super();
    }
}
