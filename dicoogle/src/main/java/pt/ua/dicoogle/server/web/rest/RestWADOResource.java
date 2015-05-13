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

import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.server.web.rest.elements.FileDownloadUtils;

/**
 *
 * @author psytek
 *
 * WARNING: This will return *any* file on the host! The best way to correct
 * this will be to change the generated XML in order to produce SOPInstanceUID,
 * and let them act as a tag for the file. This will imply a new query every
 * time we want to download a new file, but that's probably fine...
 *
 */
public class RestWADOResource extends ServerResource {
    
    @Get
    public OutputRepresentation represent() {
        String studyUID = getRequest().getResourceRef().getQueryAsForm().getValues("studyUID");
        String seriesUID = getRequest().getResourceRef().getQueryAsForm().getValues("seriesUID");
        String objectUID = getRequest().getResourceRef().getQueryAsForm().getValues("objectUID");
        String requestType = getRequest().getResourceRef().getQueryAsForm().getValues("requestType");
        String contentType = getRequest().getResourceRef().getQueryAsForm().getValues("contentType");

        

        setStatus( Status.SUCCESS_OK );
        //lets make sure that all the REQUIRED (all caps, according to standard :)) are here
        if(objectUID==null || objectUID.isEmpty())
        {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            //result = generateErrorRepresentation("Please enter a UID", "400");
            //400 means bad request
        }

        //the standard also requires that the request type is present and == WADO
        if(requestType == null || !requestType.equals("WADO")){
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
        
        return FileDownloadUtils.gerFileRepresentation(objectUID);
    }
}
