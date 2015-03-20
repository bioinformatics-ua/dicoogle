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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

public class ForceIndexing extends ServerResource {
    
	private static final Logger logger = Logger.getLogger("dicoogle");
	
    @Get
    public Representation represent(){
    	
        Form queryForm = getRequest().getResourceRef().getQueryAsForm();
        //System.out.println("Fetching Data");
        String[] uris = queryForm.getValuesArray("uri");
        String pluginName = queryForm.getValues("plugin");
        
        PluginController pc = PluginController.get();
        Report returnReport = new Report();
        for(String uriString : uris) {
            try {
                URI u = new URI(uriString.replaceAll(" ","%20"));

                logger.info("Sent Index Request: " + pluginName + u.toString());
                if (pluginName == null) { //no name somehow means we are going to index using all indexers
                    Task<Report> indexTask = pc.indexAllClosure(u);
                    indexTask.run();
                    returnReport.addChild(indexTask.get());
                }
                else {
                    Task<Report> indexTask = pc.indexClosure(pluginName, u);
                    indexTask.run();
                    returnReport.addChild(indexTask.get());
                }
            }
            catch (URISyntaxException  | InterruptedException  | ExecutionException ex) {
                logger.log(Level.SEVERE, null, ex);
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex);
            }            
        }
        return new StringRepresentation(returnReport.toString(), MediaType.TEXT_PLAIN);
    }
}
