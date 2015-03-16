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

package pt.ua.dicoogle.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.sdk.JettyPluginInterface;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.services.JettyServiceProvider;
import pt.ua.dicoogle.services.RestletServiceProvider;
import pt.ua.dicoogle.webservices.ExamTimeResource;
import pt.ua.dicoogle.webservices.ForceIndexing;
import pt.ua.dicoogle.webservices.RestDimResource;
import pt.ua.dicoogle.webservices.RestDumpResource;
import pt.ua.dicoogle.webservices.RestFileResource;
import pt.ua.dicoogle.webservices.RestTagsResource;
import pt.ua.dicoogle.webservices.RestWADOResource;

/**
 *
 * @author psytek
 */
public class ServiceController {
    
    RestletServiceProvider restletHandler = new RestletServiceProvider();
    JettyServiceProvider jettyHandler = new JettyServiceProvider(8080);

    

    
    
    ///////////////////////////////////////////////////////jetty
    
    
    
    
    //////////////////////////////////////SController
    public ServiceController(){
        //load services.xml
        
        
    }
    
    public void manageJettyPlugins(Collection<JettyPluginInterface> jettyInterfaces){
        for(JettyPluginInterface resource : jettyInterfaces){
            jettyHandler.addContextHandlers( resource.getJettyHandlers() );
         }
    }
    
    public void manageRestPlugins(Collection<ServerResource> restInterfaces){
        for (ServerResource resource : restInterfaces) {
            restletHandler.attachRestPlugin(resource);
        }
    }
    
    public void startEnabledServices() throws Exception{
        jettyHandler.start();
        restletHandler.start();
    }
    
    public void startService(String service){
        
    }
    
    public void stopService(String service){
        
    }
    
    public Report status(String service){
        return null;
    }
    
    
}
