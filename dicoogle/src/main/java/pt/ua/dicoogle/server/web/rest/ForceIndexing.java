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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

public class ForceIndexing extends ServerResource{
    
	private static final Logger log = LoggerFactory.getLogger(ForceIndexing.class);	
	
    @Get
    public Representation represent(){
    	
        Form queryForm = getRequest().getResourceRef().getQueryAsForm();
        //System.out.println("Fetching Data");
        String[] uris = queryForm.getValuesArray("uri");
        String pluginName = queryForm.getValues("plugin");
        
        PluginController pc = PluginController.getInstance();
        //System.out.println("Generating Tasks");
        List<Task<Report>> reports  = new ArrayList<>(uris.length);
        for(String uriString : uris) {
            URI u = null;
            try {
                u = new URI(uriString.replaceAll(" ","%20"));
            } catch (URISyntaxException ex) {
            	log.error("Could not create URI", ex);
                ex.printStackTrace();
            } 
            if(u != null){
            	log.info("Sent Index Request: {}, {}",pluginName, u.toString());
            	if(pluginName == null)
            		reports.addAll(pc.index(u));
            	else
            		reports.addAll(pc.index(pluginName,u));
            }
        }
        
        //System.out.println("Waiting for Results");
        List<Report> done = new ArrayList<>(reports.size());
        StringBuilder builder = new StringBuilder();
        for(Task<Report> t : reports){
            try {
            	Report r = t.get();
                done.add(r);
                builder.append(r).append("\n");
            }catch(InterruptedException | ExecutionException ex){
            	log.error("UNKNOW ERROR", ex);
            	ex.printStackTrace();
            }
        }
        //System.out.println("Exporting Results");
        
        log.info("Finished forced indexing procedure: {}", reports.size());
        
        return new StringRepresentation(builder.toString(), MediaType.TEXT_PLAIN);
    }
}
