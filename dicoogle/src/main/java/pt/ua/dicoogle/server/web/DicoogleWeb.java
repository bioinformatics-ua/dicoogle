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
package pt.ua.dicoogle.server.web;

import pt.ua.dicoogle.server.web.servlets.search.ProvidersServlet;
import pt.ua.dicoogle.server.web.servlets.search.SearchServlet;
import pt.ua.dicoogle.server.web.servlets.search.WadoServlet;
import pt.ua.dicoogle.server.web.servlets.accounts.LoginServlet;
import pt.ua.dicoogle.server.web.servlets.accounts.UserServlet;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.server.web.servlets.management.AETitleServlet;
import pt.ua.dicoogle.server.web.servlets.management.DicomSettingsServlet;
import pt.ua.dicoogle.server.web.servlets.management.ForceIndexing;
import pt.ua.dicoogle.server.web.servlets.management.IndexerSettingsServlet;
import pt.ua.dicoogle.server.web.servlets.management.ServicesServlet;
import pt.ua.dicoogle.server.web.servlets.management.TransferenceOptionsServlet;

import java.io.File;
import java.net.URL;
import java.util.EnumSet;
import java.util.logging.Level;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import java.util.logging.Logger;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.GzipFilter;

import pt.ua.dicoogle.server.web.servlets.accounts.LogoutServlet;
import pt.ua.dicoogle.server.web.servlets.search.DumpServlet;
import pt.ua.dicoogle.server.web.utils.LocalImageCache;
import pt.ua.dicoogle.webservices.WebservicePluginApplication;

/**
 * @author António Novo <antonio.novo@ua.pt>
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Frederico Valente
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class DicoogleWeb {

    private static final Logger log = Logger.getLogger(DicoogleWeb.class.getName());
    /**
     * Sets the path where the web-pages/scripts or .war are.
     */
    public static final String WEBAPPDIR = "WEB-INF";

    /**
     * Sets the context path used to serve the contents.
     */
    public static final String CONTEXTPATH = "/";
    private LocalImageCache cache = null;
    private Server server = null;
    private final int port;

    private ContextHandlerCollection contextHandlers;
    private ServletContextHandler pluginHandler = null;
    private WebservicePluginApplication pluginApp = null;

    /**
     * The global list of GUI hooks and actions.
     */

    /**
     * Initializes and starts the Dicoogle Web service.
     * @param port the server port
     */
    public DicoogleWeb(int port) throws Exception {
        log.log(Level.INFO, "Starting Web Services in DicoogleWeb. Port: {0}", port);
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");
      //  System.setProperty("org.mortbay.jetty.webapp.parentLoaderPriority", "true");
        // System.setProperty("production.mode", "true");

        this.port = port;

        // "build" the input location, based on the www directory/.war chosen
        final URL warUrl = Thread.currentThread().getContextClassLoader().getResource(WEBAPPDIR);
        final String warUrlString = warUrl.toExternalForm();

        //setup the DICOM to PNG image servlet, with a local cache
        final ServletContextHandler dic2png = new ServletContextHandler(ServletContextHandler.SESSIONS); // servlet with session support enabled
        dic2png.setContextPath(CONTEXTPATH);
        cache = new LocalImageCache("dic2png", 300, 900); // pooling rate of 12/hr and max un-used cache age of 15 minutes
        dic2png.addServlet(new ServletHolder(new ImageServlet(cache)), "/dic2png");
        cache.start(); // start the caching system

        // setup the DICOM to PNG image servlet
        final ServletContextHandler dictags = new ServletContextHandler(ServletContextHandler.SESSIONS); // servlet with session support enabled
        dictags.setContextPath(CONTEXTPATH);
        dictags.addServlet(new ServletHolder(new TagsServlet()), "/dictags");

        // setup the Export to CSV Servlet
        final ServletContextHandler csvServletHolder = new ServletContextHandler(ServletContextHandler.SESSIONS); // servlet with session support enabled
        csvServletHolder.setContextPath(CONTEXTPATH);
        csvServletHolder.addServlet(new ServletHolder(new ExportToCSVServlet()), "/export");

        File tempDir = new File(ServerSettings.getInstance().getPath());
        csvServletHolder.addServlet(new ServletHolder(new ExportCSVToFILEServlet(tempDir)), "/exportFile");

        // setup the search (DIMSE-service-user C-FIND ?!?) servlet
        //final ServletContextHandler search = new ServletContextHandler(ServletContextHandler.SESSIONS); // servlet with session support enabled
        //search.setContextPath(CONTEXTPATH);
        //search.addServlet(new ServletHolder(new SearchServlet()), "/search");

        // setup the plugins data, xslt and pages servlet
        final ServletContextHandler plugin = new ServletContextHandler(ServletContextHandler.SESSIONS); // servlet with session support enabled
        plugin.setContextPath(CONTEXTPATH);
        /*hooks = getRegisteredHookActions();
         plugin.addServlet(new ServletHolder(new PluginsServlet(hooks)), "/plugin/*");
         */

        // setup the plugins data, xslt and pages servlet
        final ServletContextHandler indexer = new ServletContextHandler(ServletContextHandler.SESSIONS); // servlet with session support enabled
        indexer.setContextPath(CONTEXTPATH);
        indexer.addServlet(new ServletHolder(new IndexerServlet()), "/indexer");

        final ServletContextHandler indexeres = new ServletContextHandler(ServletContextHandler.SESSIONS); // servlet with session support enabled
        indexer.setContextPath(CONTEXTPATH);
        indexer.addServlet(new ServletHolder(new IndexControlServlet()), "/indexamos");

        // setup the general/advanced Dicoogle settings servlet
        final ServletContextHandler settings = new ServletContextHandler(ServletContextHandler.SESSIONS); // servlet with session support enabled
        settings.setContextPath(CONTEXTPATH);
        settings.addServlet(new ServletHolder(new SettingsServlet()), "/settings");

        // setup the web pages/scripts app
        final WebAppContext webpages = new WebAppContext(warUrlString, CONTEXTPATH);
        webpages.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false"); // disables directory listing
        webpages.setInitParameter("useFileMappedBuffer", "false");
        webpages.setInitParameter("cacheControl", "max-age=0, public");

        webpages.setWelcomeFiles(new String[]{"newsearch.jsp"});
        webpages.addServlet(new ServletHolder(new SearchHolderServlet()), "/search/holders");
        FilterHolder filter = webpages.addFilter(GzipFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

        this.pluginApp = new WebservicePluginApplication();
        this.pluginHandler = new ServletContextHandler();
        this.pluginHandler.setContextPath(CONTEXTPATH);
        this.pluginHandler.addServlet(new ServletHolder(new RestletHttpServlet(this.pluginApp)), "/ext/*");

        // list the all the handlers mounted above
        Handler[] handlers = new Handler[]{
            pluginHandler,
            dic2png,
            dictags,
            plugin,
            indexer,
            indexeres,
            settings,
            csvServletHolder,
            createServletHandler(new LoginServlet(), "/login"),
            createServletHandler(new LogoutServlet(), "/logout"),
            createServletHandler(new UserServlet(), "/user"),
            createServletHandler(new SearchServlet(), "/search"),
            createServletHandler(new DumpServlet(), "/dump"),
            createServletHandler(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.path) , "/management/settings/index/path"),
            createServletHandler(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.zip), "/management/settings/index/zip"),
            createServletHandler(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.effort), "/management/settings/index/effort"),
            createServletHandler(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.thumbnail), "/management/settings/index/thumbnail"),
            createServletHandler(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.thumbnailSize), "/management/settings/index/thumbnail/size"),
            createServletHandler(new TransferenceOptionsServlet(), "/management/settings/transfer"),
            createServletHandler(new WadoServlet(), "/wado"),
            createServletHandler(new ProvidersServlet(), "/providers"),
            createServletHandler(new DicomSettingsServlet(), "/management/settings/dicom/query"),
            createServletHandler(new ForceIndexing(), "/management/tasks/index"),
            createServletHandler(new ServicesServlet(ServicesServlet.STORAGE), "/management/dicom/storage"),
            createServletHandler(new ServicesServlet(ServicesServlet.QUERY), "/management/dicom/query"),
            createServletHandler(new ServicesServlet(ServicesServlet.PLUGIN), "/management/plugins/"),
            createServletHandler(new AETitleServlet(), "/management/settings/dicom")
            ,
            webpages
        };

        // setup the server
        server = new Server(port);
        server = new Server(ServerSettings.getInstance().getWeb().getServerPort());

        // register the handlers on the server
        this.contextHandlers = new ContextHandlerCollection();
        this.contextHandlers.setHandlers(handlers);
        server.setHandler(this.contextHandlers);
        
        // and then start the server
        server.start();
    }
    
    private ServletContextHandler createServletHandler(HttpServlet servlet, String path){
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS); // servlet with session support enabled
        handler.setContextPath(CONTEXTPATH);
        handler.addServlet(new ServletHolder(servlet), path);
        return handler;
    }

    /**
     * Stops the Dicoogle Web service.
     * @throws java.lang.Exception if a problem occurs when stopping the server
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

        // and remove the local cache, if any
        if (cache != null) {
            cache.terminate();
            cache = null;
        }
        
        this.pluginHandler = null;
    }

    public void addContextHandlers(Handler handler) {
        this.contextHandlers.addHandler(handler);
        //this.server.setHandler(this.contextHandlers);
    }

    public void startPluginWebServices() {
        // TODO right now plugins are started by default
    }

    public void stopPluginWebServices() {
        if (this.pluginHandler != null) {
            this.contextHandlers.removeHandler(this.pluginHandler);
        }
    }

}
