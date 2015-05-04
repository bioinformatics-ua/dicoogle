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
package pt.ua.dicoogle.server;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import pt.ua.dicoogle.sdk.RestletInterface;

/** A Restlet Application for aggregating web services from plugins
 *
 * @author psytek
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class PluginRestletApplication extends Application {

    private static final List<ServerResource> pluginServerResources = new ArrayList<>();
    private static final List<RestletInterface> pluginRestlets = new ArrayList<>();
    
    private Router internalRouter;
    
    public PluginRestletApplication() {
        super();
    }
    
    /**
     * Creates a root Restlet that will receive all incoming calls.
     * @return a Restlet for the root 
     */
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of our resources
        this.internalRouter = new Router(getContext());
        // Defines routing to resources
        this.internalRouter.setDefaultMatchingQuery(false);
        
        // add plugin services

        // server resources are attached to the path given by <tt>toString</tt> (sure is a little brittle...)
        for(ServerResource resource : pluginServerResources) {
            LoggerFactory.getLogger(PluginRestletApplication.class).debug("Inbound Server Resource: {}", resource.toString());
            internalRouter.attach("/" + resource.toString(), resource.getClass());
        }
        
        // restlet interfaces provide a base path
        for(RestletInterface restletInt : pluginRestlets) {
            LoggerFactory.getLogger(PluginRestletApplication.class).debug("Inbound Restlet {}", restletInt.getPath());
            internalRouter.attach("/" + restletInt.getPath() + "/*", restletInt.getRestlet());
        }

        LoggerFactory.getLogger(PluginRestletApplication.class).debug("Installed plugin restlets: {}",
                pluginServerResources);
        return internalRouter;
    }
    
    protected void loadPlugins() {
    }
    
    public static void attachRestResourcePlugin(ServerResource resource){
        pluginServerResources.add(resource);
    }
    
    public static void attachRestletPlugin() {
        
    }
    
}
