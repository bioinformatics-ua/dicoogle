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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import pt.ua.dicoogle.core.ServerSettings;

import java.net.URL;
import java.util.logging.Level;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import pt.ua.dicoogle.Dicoogle;


/**
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Frederico Valente
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class JettyServiceProvider {

    private static final Logger log = Logger.getLogger("dicoogle");
    /**
     * Sets the path where the web-pages/scripts or .war are.
     */
    public static final String WEBAPPDIR = "WEB-INF";

    /**
     * Sets the context path used to serve the contents.
     */
    public static final String CONTEXTPATH = "/";
    private Server server = null;

    private ContextHandlerCollection contextHandlers;

    /**
     * The global list of GUI hooks and actions.
     */

    /**
     * Initializes and starts the Dicoogle Web service.
     * @param port
     */
    public JettyServiceProvider(int port) {
        log.info("Starting Jetty Web Services... Port: " + port);

        //wtf is this?
        System.setProperty("org.apache.jasper.compiler.disablejsr199throws Exception", "true");

        // abort if the server is already running
        if (server != null) {
            Logger.getLogger("dicoogle").warning("Server is not null!!");
            return;
        }
        
        // "build" the input location, based on the www directory/.war chosen
        URI webPathUri;
        try {
            String jarPath = Dicoogle.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File webFile = new File(jarPath+"/web");
            if(!webFile.exists()){
                webFile.mkdir();
            }
            webPathUri = webFile.toURI();
        }
        catch (URISyntaxException ex) {
            Logger.getLogger("dicoogle").log(Level.SEVERE, null, ex);
            return;
        }
        
        // setup the plugins data, xslt and pages servlet
        final ServletContextHandler plugin = new ServletContextHandler(ServletContextHandler.SESSIONS); // servlet with session support enabled
        plugin.setContextPath(CONTEXTPATH);
        
        // setup the web pages/scripts app
        final WebAppContext webpages = new WebAppContext(webPathUri.toString(), CONTEXTPATH);
        webpages.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false"); // disables directory listing
        webpages.setInitParameter("useFileMappedBuffer", "false");
        webpages.setInitParameter("cacheControl", "max-age=0, public");

        webpages.setWelcomeFiles(new String[]{"newsearch.jsp"});


        // list the all the handlers mounted above
        Handler[] handlers = new Handler[]{
            plugin,
            webpages
        };

        // setup the server
        server = new Server(port);

        // register the handlers on the server
        this.contextHandlers = new ContextHandlerCollection();
        this.contextHandlers.setHandlers(handlers);
        server.setHandler(this.contextHandlers);

    }
    
    private ServletContextHandler createServletHandler(HttpServlet servlet, String path){
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS); // servlet with session support enabled
        handler.setContextPath(CONTEXTPATH);
        handler.addServlet(new ServletHolder(servlet), path);
        return handler;
    }

    /**
     * Stops the Dicoogle Web service.
     */
    public void stop() throws Exception {
        // abort if the server is not running
        if (server == null) {
            return;
        }

        // stop the server
        server.stop();

        // voiding its value
        server = null;
    }
    
    public void start() throws Exception{
        server.start();
    }

    public void addContextHandlers(Handler handler) {
        this.contextHandlers.addHandler(handler);
    }

}
