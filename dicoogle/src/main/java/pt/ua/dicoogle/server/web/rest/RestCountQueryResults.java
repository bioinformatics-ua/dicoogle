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

import org.apache.commons.lang.NotImplementedException;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;


import pt.ua.dicoogle.core.QueryExpressionBuilder;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class RestCountQueryResults extends ServerResource{ 
    
    @Get
    public Representation represent(){
        
        String search = getRequest().getResourceRef().getQueryAsForm().getValues("q");
        String advSearch = getRequest().getResourceRef().getQueryAsForm().getValues("advq");

        if(advSearch == null){
            //prepares query
            if(search == null) search="";
            else if(search.equals("null")) search = "";

            if (search.equals(""))
                search = "*:*";
            else{
                QueryExpressionBuilder q = new QueryExpressionBuilder(search);
                search = q.getQueryString();
            }
        }
        else{
            search = advSearch;
        }

        
        throw new NotImplementedException("Deprecated: RMI", null);
        
        //return new StringRepresentation("" + nResults, MediaType.TEXT_PLAIN);
    }
}
