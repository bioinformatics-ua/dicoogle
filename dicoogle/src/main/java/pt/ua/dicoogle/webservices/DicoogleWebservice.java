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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.util.Series;
import pt.ua.dicoogle.core.ServerSettings;

/**
 *
 * @author psytek
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 *
 */
public class DicoogleWebservice extends Application {

    static Component component = null;
    static ArrayList<ServerResource> pluginServices = new ArrayList<>();

    public static void startWebservice(){
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
            component.getDefaultHost().attach(new DicoogleWebservice());

            // And starts the component.
            component.start();
            org.restlet.engine.Engine.setLogLevel(Level.OFF);
        }
        catch(Exception e){
            //TODO:log this properly...
            System.err.println(e.getMessage());
        }
    }

    public static void stopWebservice(){
        if(component == null) return;

        for(Server server : component.getServers()){
            try {
                server.stop();
            } catch (Exception ex) {
                Logger.getLogger(DicoogleWebservice.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        component=null;
    }

     /**
     * Creates a root Restlet that will receive all incoming calls.
     * @return a restlet 
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
        
        // OPTIONS method with CORS support
        router.attach("/", CORSResource.class);
        
        //lets add plugin registred services
        //this is still a little brittle... :(
        for(ServerResource resource : pluginServices){
            System.err.println("Inbound: "+resource.toString());
            router.attach("/"+resource.toString(), resource.getClass());
        }
        
        // add CORS support to all resources
        Filter corsFilter = new Filter(getContext(), router) {

            @Override
            protected void afterHandle(Request request, Response response) {
                Series<Header> responseHeaders = (Series) response.getAttributes()
                        .get(HeaderConstants.ATTRIBUTE_HEADERS);
                if (responseHeaders == null) {
                    responseHeaders = new Series(Header.class);
                    response.getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS, responseHeaders);
                }
                responseHeaders.add("Access-Control-Allow-Origin", "*");
            }
            
        };

        return corsFilter;
    }
    
    public static void attachRestPlugin(ServerResource resource){
        pluginServices.add(resource);
    }
    
}
