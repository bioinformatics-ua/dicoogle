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

import java.util.Collection;
import org.apache.log4j.Logger;
import org.restlet.resource.ServerResource;
import pt.ua.dicoogle.sdk.JettyPluginInterface;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.services.JettyServiceProvider;
import pt.ua.dicoogle.services.RestletServiceProvider;

/**
 *
 * @author psytek
 */
public class ServiceController {
    Logger logger = Logger.getLogger("dicoogle");
    RestletServiceProvider restletHandler = new RestletServiceProvider();
    JettyServiceProvider jettyHandler = new JettyServiceProvider(8080);

    public ServiceController(){
        //load services.xml
    }
    
    public void manageJettyPlugins(Collection<JettyPluginInterface> jettyInterfaces){
        jettyInterfaces.stream().forEach((resource) -> {jettyHandler.addContextHandlers( resource.getJettyHandlers() );});
    }
    
    public void manageRestPlugins(Collection<ServerResource> restInterfaces){
        restInterfaces.stream().forEach((resource) -> {restletHandler.attachRestPlugin(resource);});
    }
    
    public void startEnabledServices() throws Exception{
        logger.info("stating enabled jetty services");
        jettyHandler.start();
        logger.info("stating enabled restlet services");
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
