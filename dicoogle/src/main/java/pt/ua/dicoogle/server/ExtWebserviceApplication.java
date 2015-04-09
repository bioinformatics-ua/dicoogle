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
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import pt.ua.dicoogle.server.web.rest.ExtResource;
import pt.ua.dicoogle.server.web.rest.TestResource;
import pt.ua.dicoogle.server.web.rest.ExamTimeResource;
import pt.ua.dicoogle.server.web.rest.ExamTimeResource;
import pt.ua.dicoogle.server.web.rest.ForceIndexing;
import pt.ua.dicoogle.server.web.rest.ForceIndexing;
import pt.ua.dicoogle.server.web.rest.RestDcmImageResource;
import pt.ua.dicoogle.server.web.rest.RestDcmImageResource;
import pt.ua.dicoogle.server.web.rest.RestDimResource;
import pt.ua.dicoogle.server.web.rest.RestDimResource;
import pt.ua.dicoogle.server.web.rest.RestDumpResource;
import pt.ua.dicoogle.server.web.rest.RestDumpResource;
import pt.ua.dicoogle.server.web.rest.RestFileResource;
import pt.ua.dicoogle.server.web.rest.RestFileResource;
import pt.ua.dicoogle.server.web.rest.RestTagsResource;
import pt.ua.dicoogle.server.web.rest.RestTagsResource;
import pt.ua.dicoogle.server.web.rest.RestWADOResource;
import pt.ua.dicoogle.server.web.rest.RestWADOResource;

/** A Restlet Application for aggregating extended services and 
 *
 * @author psytek
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ExtWebserviceApplication extends Application {

    //static Component component = null;
    private static final List<ServerResource> pluginServices = new ArrayList<>();
    
    private Router internalRouter;
    
    public ExtWebserviceApplication() {
        super();
    }
/*    
    public static void startWebservice(){
        try{
            
            Logger.getLogger(ExtWebserviceApplication.class.getName()).log(Level.INFO,
                    "Adding Webservice in port {0}", ServerSettings.getInstance().getWeb().getServicePort());
            
            component = new Component();
            component.getServers().add(Protocol.HTTP,ServerSettings.getInstance().getWeb().getServicePort());
            
            // Attaches this application to the server.
            component.getDefaultHost().attach(new ExtWebserviceApplication());

            // And starts the component.
            component.start();
            org.restlet.engine.Engine.setLogLevel(java.util.logging.Level.OFF);
        }
        catch(Exception e){
            //TODO:log this properly...
            System.err.println(e.getMessage());
        }
    }
    public static void stopWebservice(){
        // TODO
        for(Server server : component.getServers()){
            try {
                server.stop();
            } catch (Exception ex) {
                LoggerFactory.getLogger(DicoogleWebservice.class).error(ex.getMessage(), ex);
            }
        }
    }
*/
    
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
        //internalRouter.attach("/test/{something}", TestResource.class);
        internalRouter.attach("/dim", RestDimResource.class);//search resource
        internalRouter.attach("/file", RestFileResource.class);//file download resource
        internalRouter.attach("/dump", RestDumpResource.class);//dump resource
        internalRouter.attach("/tags", RestTagsResource.class);//list of avalilable tags resource
        //router.attach("/image", RestImageResource.class);//jpg image resource
        //router.attach("/enumField", RestEnumField.class);
        //router.attach("/countResuls", RestCountQueryResults.class);
        internalRouter.attach("/wado", RestWADOResource.class);
        internalRouter.attach("/img", RestDcmImageResource.class);
        internalRouter.attach("/examTime", ExamTimeResource.class);
        
        //Advanced Dicoogle Features
        internalRouter.attach("/doIndex", ForceIndexing.class);

        internalRouter.attachDefault(ExtResource.class);
        
        loadPlugins();
        
        LoggerFactory.getLogger(ExtWebserviceApplication.class).debug(internalRouter.getRoutes().toString());
        return internalRouter;
    }
    
    protected void loadPlugins() {
        //lets add plugin registred services
        //this is still a little brittle... :(
        for(ServerResource resource : pluginServices) {
            LoggerFactory.getLogger(ExtWebserviceApplication.class).debug(
                    "Inbound: {}", resource);
            internalRouter.attach("/" + resource.toString(), resource.getClass());
        }
    }
    
    public static void attachRestPlugin(ServerResource resource){
        pluginServices.add(resource);
    }
    
}
