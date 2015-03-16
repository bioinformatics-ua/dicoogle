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
    public class RestletServiceProvider extends Application {

        Component component = null;
        ArrayList<ServerResource> pluginServices = new ArrayList<>();

        void RestletServiceProvider(){
            try{
                System.err.println("Stating Webservice in port:"+ServerSettings.getInstance().getWeb().getServicePort());

                // Adds a new HTTP server listening on customized port.
                // The implementation used is based on java native classes
                // It's not the fastest possible implementation
                // but it suffices for the time being
                // (we can always use other backends at the expense of a few more dependencies)
                component = new Component();
                component.getServers().add(Protocol.HTTP,ServerSettings.getInstance().getWeb().getServicePort());

                // Attaches this application to the server.
                component.getDefaultHost().attach(this);

                // And starts the component.
                component.start();
                org.restlet.engine.Engine.setLogLevel(Level.OFF);
            }
            catch(Exception e){
                //TODO:log this properly...
                System.err.println(e.getMessage());
            }
        }

        public void stop(){
            if(component == null) return;

            for(Server server : component.getServers()){
                try {
                    server.stop();
                }
                catch (Exception ex) {
                    Logger.getLogger("global").log(Level.SEVERE, null, ex);
                }
            }
            component=null;
        }

        /**
        * Creates a root Restlet that will receive all incoming calls.
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
           //router.attach("/image", RestImageResource.class);//jpg image resource
           //router.attach("/enumField", RestEnumField.class);
           //router.attach("/countResuls", RestCountQueryResults.class);
           router.attach("/wado", RestWADOResource.class);
           router.attach("/examTime", ExamTimeResource.class);

           //Advanced Dicoogle Features
           router.attach("/doIndex", ForceIndexing.class);


           //lets add plugin registred servicesHandler
           //this is still a little brittle... :(
           for(ServerResource resource : pluginServices){
               System.err.println("Inbound: "+resource.toString());
               router.attach("/"+resource.toString(), resource.getClass());
           }

           return router;
       }

       public void attachRestPlugin(ServerResource resource){
           pluginServices.add(resource);
       }
    }

