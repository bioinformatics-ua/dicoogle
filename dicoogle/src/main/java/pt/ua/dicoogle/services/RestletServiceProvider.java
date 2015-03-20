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

package pt.ua.dicoogle.services;

import java.util.ArrayList;
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
    public class RestletServiceProvider{
        static final Logger logger = Logger.getLogger("dicoogle");
        static ArrayList<ServerResource> pluginServices = new ArrayList<>();

        RestletService service = null;
        
        private static class RestletService extends Application{
            Component component = null;
            int port;
            
            RestletService(int port) throws Exception{
                this.port = port;
                // Adds a new HTTP server listening on customized port.
                // The implementation used is based on java native classes
                // It's not the fastest possible implementation
                // but it suffices for the time being
                // (we can always use other backends at the expense of a few more dependencies)
                component = new Component();
                component.getServers().add(Protocol.HTTP, ServerSettings.getInstance().getWeb().getServicePort());

                // Attaches this application to the server.
                component.getDefaultHost().attach(this);

                // And starts the component.
                component.start();
                org.restlet.engine.Engine.setLogLevel(Level.OFF);
                
            }
            
            @Override
            public void stop() throws Exception{
                if(component == null) return;

                for(Server server : component.getServers()){
                    server.stop();
                }
                component=null;
            }

            /**
             * Creates a root Restlet that will receive all incoming calls.
             *
             * @return
             */
            @Override
            public synchronized Restlet createInboundRoot() {
                // Create a router Restlet that routes each call to a
                // new instance of our resources
                Router router = new Router(getContext());

                // Defines routing to resources
                router.attach("/dim", RestDimResource.class);//search resource
                router.attach("/file", RestFileResource.class);//file download resource
                router.attach("/dump", RestDumpResource.class);//dump resource
                router.attach("/tags", RestTagsResource.class);//list of avalilable tags resource
                //router.attach("/img", RestImageResource.class);//jpg image resource
                //router.attach("/enumField", RestEnumField.class);
                //router.attach("/countResuls", RestCountQueryResults.class);
                router.attach("/wado", RestWADOResource.class);

                //Advanced Dicoogle Features
                router.attach("/doIndex", ForceIndexing.class);

                //lets add plugin registred servicesHandler
                //this is still a little brittle... :(
                for (ServerResource resource : pluginServices) {
                    logger.info("restlet inbound: " + resource.toString());
                    router.attach("/" + resource.toString(), resource.getClass());
                }

                return router;
            }
        }
        
        
        public void RestletServiceProvider(){}

        public void start() throws Exception{
            logger.info("Stating Webservice in port:"+ServerSettings.getInstance().getWeb().getServicePort());
            service = new RestletService(ServerSettings.getInstance().getWeb().getServicePort());
        }
        
        public void attachRestPlugin(ServerResource resource) {
            pluginServices.add(resource);
        }
    }

