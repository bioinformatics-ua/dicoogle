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

import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.plugins.webui.WebUIPlugin;
import pt.ua.dicoogle.sdk.utils.TagsStruct;
import pt.ua.dicoogle.server.web.rest.VersionResource;
import pt.ua.dicoogle.server.web.servlets.RestletHttpServlet;
import pt.ua.dicoogle.server.web.servlets.ExportToCSVServlet;
import pt.ua.dicoogle.server.web.servlets.SettingsServlet;
import pt.ua.dicoogle.server.web.servlets.TagsServlet;
import pt.ua.dicoogle.server.web.servlets.ExportCSVToFILEServlet;
import pt.ua.dicoogle.server.web.servlets.IndexerServlet;
import pt.ua.dicoogle.server.web.servlets.ImageServlet;
import pt.ua.dicoogle.server.web.servlets.plugins.PluginsServlet;
import pt.ua.dicoogle.server.web.servlets.management.*;
import pt.ua.dicoogle.server.web.servlets.search.*;
import pt.ua.dicoogle.server.web.servlets.search.ExportServlet.ExportType;
import pt.ua.dicoogle.server.web.servlets.search.SearchServlet.SearchType;
import pt.ua.dicoogle.server.web.servlets.accounts.LoginServlet;
import pt.ua.dicoogle.server.web.servlets.accounts.UserServlet;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.EnumSet;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.restlet.Restlet;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;

import pt.ua.dicoogle.server.LegacyRestletApplication;
import pt.ua.dicoogle.server.web.servlets.accounts.LogoutServlet;
import pt.ua.dicoogle.server.web.servlets.webui.WebUIModuleServlet;
import pt.ua.dicoogle.server.web.servlets.webui.WebUIServlet;
import pt.ua.dicoogle.server.web.utils.LocalImageCache;
import pt.ua.dicoogle.server.PluginRestletApplication;
import pt.ua.dicoogle.server.web.utils.SimpleImageRetriever;

/**
 * @author António Novo <antonio.novo@ua.pt>
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Frederico Valente
 * @author Frederico Silva <fredericosilva@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class DicoogleWeb {

    private static final Logger logger = LoggerFactory.getLogger(DicoogleWeb.class);
    /**
     * Sets the path where the web-pages/scripts or .war are.
     */
    public static final String WEBAPPDIR = "webapp";

    /**
     * Sets the context path used to serve the contents.
     */
    public static final String CONTEXTPATH = "/";
    private LocalImageCache cache = null;
    private Server server = null;
    private final int port;

    private final ContextHandlerCollection contextHandlers;
    private Handler pluginHandler;
    private PluginRestletApplication pluginApp = null;
    private Handler legacyHandler = null;
    private LegacyRestletApplication legacyApp = null;

    /**
     * Initializes and starts the Dicoogle Web service.
     * @param port the server port
     * @throws java.lang.Exception
     */
    public DicoogleWeb(int port) throws Exception {
        logger.info("Starting Web Services in DicoogleWeb. Port: {}", port);
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");

        this.port = port;
        this.contextHandlers = this.createHandlerCollection();

        // setup the server
        server = new Server(port);
        server.setHandler(this.contextHandlers);

        // Increase maxFormContentSize to avoid issues with big forms
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 3325000);

        // and then start the server
        server.start();
    }

    /** Root context handler, contains the full tree of services. */
    private ContextHandlerCollection createHandlerCollection() {
        // setup the DICOM to PNG image servlet, with a local cache
        cache = new LocalImageCache("dic2png", 300, 900, new SimpleImageRetriever()); // pooling rate of 12/hr and max un-used cache age of 15 minutes
        cache.start(); // start the caching system

        // temporary folder for export CSV files
        File tempDir = Paths.get(System.getProperty("java.io.tmpdir")).toFile();

        // set up plugin Restlet application for plugin services
        this.pluginApp = new PluginRestletApplication();
        this.pluginHandler = createRestletHandler("ext", this.pluginApp);

        // set up plugin Restlet application for legacy services
        this.legacyApp = new LegacyRestletApplication();
        this.legacyHandler = createRestletHandler("legacy", this.legacyApp);

        // Add Static RESTlet Plugins
        PluginRestletApplication.attachRestPlugin(new VersionResource());

        ServletContextHandler rootContextHandler = new ServletContextHandler(0);
        rootContextHandler.setContextPath(CONTEXTPATH);

        // list handlers for services mounted in the root context directly

        // search
        rootContextHandler.addServlet(new ServletHolder("search", new SearchServlet()), "/search");
        // DICOM to PNG image servlet
        rootContextHandler.addServlet(new ServletHolder("dic2png", new ImageServlet(cache)), "/dic2png");
        // export to CSV servlets
        rootContextHandler.addServlet(new ServletHolder("export", new ExportToCSVServlet()), "/export");
        rootContextHandler.addServlet(new ServletHolder(new ExportCSVToFILEServlet(tempDir)), "/exportFile");
        // dictags
        rootContextHandler.addServlet(new ServletHolder(new TagsServlet()), "/dictags");
        // User servlets: logging in and out, and fetching user information.
        rootContextHandler.addServlet(new ServletHolder(new LoginServlet()), "/login");
        rootContextHandler.addServlet(new ServletHolder(new LogoutServlet()), "/logout");
        rootContextHandler.addServlet(new ServletHolder(new UserServlet()), "/user/*");
        // DIM search servlet
        rootContextHandler.addServlet(new ServletHolder(new SearchServlet(SearchType.PATIENT)), "/searchDIM");
        // metadata dump servlet
        rootContextHandler.addServlet(new ServletHolder(new DumpServlet()), "/dump");
        // list query/index/storage providers
        rootContextHandler.addServlet(new ServletHolder(new ProvidersServlet()), "/providers");
        // service to retrieve plugin information
        rootContextHandler.addServlet(new ServletHolder(new PluginsServlet()), "/plugins/*");
        // service to retrieve export presets
        rootContextHandler.addServlet(new ServletHolder(new PresetsServlet()), "/presets/*");
        // retrieve logs
        rootContextHandler.addServlet(new ServletHolder(new LoggerServlet()), "/logger");
        // get running index tasks and do operations on them
        rootContextHandler.addServlet(new ServletHolder(new RunningTasksServlet()), "/index/task");
        // deprecated stuff
        rootContextHandler.addServlet(new ServletHolder(new IndexerServlet()), "/indexer");
        rootContextHandler.addServlet(new ServletHolder(new SettingsServlet()), "/settings");
        rootContextHandler.addServlet(new ServletHolder(new WadoServlet()), "/wado");
        rootContextHandler.addServlet(new ServletHolder(new ExportServlet(ExportType.EXPORT_CVS)), "/export/cvs");
        rootContextHandler.addServlet(new ServletHolder(new ExportServlet(ExportType.LIST)), "/export/list");
        // webapp
        rootContextHandler.addServlet(createWebappServlet(), "/");

        this.addCORSFilter(rootContextHandler);

        // list ALL of the handlers mounted above
        Handler[] handlers = new Handler[] {
                // /management
                createManagementHandler(),

                // /webui
                createWebUIServletHandler(),

                // /ext (handler for Restlet resources from Dicoogle plugins)
                pluginHandler,
                // /legacy (legacy Restlet service handling)
                legacyHandler,

                // /
                rootContextHandler};

        ContextHandlerCollection col = new ContextHandlerCollection();
        col.setHandlers(handlers);
        return col;
    }

    private ServletHolder createWebappServlet() {
        final URL warUrl = Thread.currentThread().getContextClassLoader().getResource(WEBAPPDIR);

        // set up static serving of the webapp
        ServletHolder webpages = new ServletHolder("webapp", DefaultServlet.class);
        webpages.setInitParameter("resourceBase", warUrl.toExternalForm());
        webpages.setInitParameter("welcomeFile", "index.html");
        webpages.setInitParameter(AbstractCacheFilter.CACHE_CONTROL_PARAM, "public, max-age=2592000"); // cache for 30 days

        return webpages;
    }

    /** Management services: create all servlets and respective handlers */
    private ServletContextHandler createManagementHandler() {
        ServletContextHandler handler = new ServletContextHandler(0);

        handler.setContextPath(CONTEXTPATH + "management");

        handler.addServlet(new ServletHolder(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.path)),
                "/settings/index/path");
        handler.addServlet(new ServletHolder(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.zip)),
                "/settings/index/zip");
        handler.addServlet(new ServletHolder(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.effort)),
                "/settings/index/effort");
        handler.addServlet(new ServletHolder(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.thumbnail)),
                "/settings/index/thumbnail");
        handler.addServlet(new ServletHolder(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.watcher)),
                "/settings/index/watcher");
        handler.addServlet(
                new ServletHolder(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.thumbnailSize)),
                "/settings/index/thumbnail/size");
        handler.addServlet(new ServletHolder(new IndexerSettingsServlet(IndexerSettingsServlet.SettingsType.all)),
                "/settings/index");
        handler.addServlet(new ServletHolder(new TransferOptionsServlet()), "/settings/transfer");
        handler.addServlet(new ServletHolder(new DicomQuerySettingsServlet()), "/settings/dicom/query");
        handler.addServlet(new ServletHolder(new DimTagsServlet(TagsStruct.getInstance())), "/settings/dicom/tags");
        handler.addServlet(new ServletHolder(new ForceIndexing()), "/tasks/index");
        handler.addServlet(new ServletHolder(new UnindexServlet()), "/tasks/unindex");
        handler.addServlet(new ServletHolder(new RemoveServlet()), "/tasks/remove");
        handler.addServlet(new ServletHolder(new ServicesServlet(ServicesServlet.STORAGE)), "/dicom/storage");
        handler.addServlet(new ServletHolder(new ServicesServlet(ServicesServlet.QUERY)), "/dicom/query");
        handler.addServlet(new ServletHolder(new AETitleServlet()), "/settings/dicom");
        handler.addServlet(new ServletHolder(new ServerStorageServlet()), "/settings/storage/dicom");

        this.addCORSFilter(handler);

        return handler;
    }

    /** Web UI functionality: create all servlets and respective handlers */
    private ServletContextHandler createWebUIServletHandler() {
        ServletContextHandler handler = new ServletContextHandler(0);
        handler.setContextPath(CONTEXTPATH + "webui");

        // CORS support
        this.addCORSFilter(handler);

        // Caching!
        FilterHolder cacheHolder = new FilterHolder(new AbstractCacheFilter() {
            @Override
            protected String etag(HttpServletRequest req) {
                String name = req.getRequestURI().substring("/webui/module/".length());
                WebUIPlugin plugin = PluginController.getInstance().getWebUIPlugin(name);
                if (plugin == null)
                    return null;
                if (WebUIModuleServlet.isPrerelease(plugin.getVersion())) {
                    // pre-release, use hash (to facilitate development)
                    String fingerprint = PluginController.getInstance().getWebUIModuleJS(name);
                    return '"' + Hashing.murmur3_32().hashString(fingerprint, StandardCharsets.UTF_8).toString() + '"';
                } else {
                    // normal release, use weak ETag
                    String pProcess = req.getParameter("process");
                    boolean process = pProcess == null || Boolean.parseBoolean(pProcess);
                    if (process) {
                        return "W/\"" + plugin.getName() + '@' + plugin.getVersion() + '"';
                    } else {
                        return "W/\"" + plugin.getName() + '@' + plugin.getVersion() + ";raw\"";
                    }
                }
            }
        });

        cacheHolder.setInitParameter(AbstractCacheFilter.CACHE_CONTROL_PARAM, "private, max-age=2592000"); // cache for 30 days

        // servlet for web UI extension info retrieval
        handler.addServlet(new ServletHolder("webui", new WebUIServlet()), "");
        // servlet for web UI JavaScript retrieval (with Etag support)
        handler.addFilter(cacheHolder, "/module/*", EnumSet.of(DispatcherType.REQUEST));
        handler.addServlet(new ServletHolder("webui/module", new WebUIModuleServlet()), "/module/*");
        return handler;
    }

    private Handler createRestletHandler(String path, Restlet app) {

        ServletContextHandler handler = new ServletContextHandler(0);
        handler.setContextPath(CONTEXTPATH + path);

        handler.addServlet(new ServletHolder(path, new RestletHttpServlet(app)), "/*");

        return handler;
    }

    private void addCORSFilter(ServletContextHandler handler) {
        String origins = ServerSettingsManager.getSettings().getWebServerSettings().getAllowedOrigins();
        if (origins != null) {
            handler.setDisplayName("cross-origin");
            FilterHolder corsHolder = new FilterHolder(CORSFilter.class);
            corsHolder.setInitParameter(CORSFilter.ALLOWED_ORIGINS_PARAM, origins);
            corsHolder.setInitParameter(CORSFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD,PUT,DELETE");
            corsHolder.setInitParameter(CORSFilter.ALLOWED_HEADERS_PARAM,
                    "X-Requested-With,Content-Type,Accept,Origin,Authorization,Content-Length");
            handler.addFilter(corsHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
        }
    }

    private void addCORSFilter(Handler handler) {
        String origins = ServerSettingsManager.getSettings().getWebServerSettings().getAllowedOrigins();
        if (origins == null) {
            return;
        }

        logger.debug("Applying CORS filter to {}", handler);
        if (handler instanceof ServletContextHandler) {
            ServletContextHandler svHandler = (ServletContextHandler) handler;
            this.addCORSFilter(svHandler);
            logger.debug("Applied CORS filter to {}!", svHandler);
        } else if (handler instanceof HandlerWrapper) {
            for (Handler h : ((HandlerWrapper) handler).getHandlers()) {
                addCORSFilter(h);
            }
        } else if (handler instanceof HandlerCollection) {
            for (Handler h : ((HandlerCollection) handler).getHandlers()) {
                addCORSFilter(h);
            }
        }
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

        try {
            // stop the server
            server.stop();

            // voiding its value
            server = null;
        } finally {

            // and remove the local cache, if any
            if (cache != null) {
                cache.terminate();
                cache = null;
            }
        }

        this.pluginHandler = null;
    }

    public void addContextHandlers(HandlerList handler) {
        this.contextHandlers.addHandler(handler);

        this.addCORSFilter(handler);
    }

    public void stopPluginWebServices() {
        if (this.pluginHandler != null) {
            this.contextHandlers.removeHandler(this.pluginHandler);
        }
    }

    public void stopLegacyWebServices() {
        if (this.legacyHandler != null) {
            this.contextHandlers.removeHandler(this.legacyHandler);
        }
    }

}
