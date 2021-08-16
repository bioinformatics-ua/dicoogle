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

import org.slf4j.LoggerFactory;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import pt.ua.dicoogle.server.web.rest.ExamTimeResource;
import pt.ua.dicoogle.server.web.rest.ForceIndexing;
import pt.ua.dicoogle.server.web.rest.RestDcmImageResource;
import pt.ua.dicoogle.server.web.rest.RestDimResource;
import pt.ua.dicoogle.server.web.rest.RestDumpResource;
import pt.ua.dicoogle.server.web.rest.RestFileResource;
import pt.ua.dicoogle.server.web.rest.RestTagsResource;
import pt.ua.dicoogle.server.web.rest.RestWADOResource;

/** A Restlet Application for aggregating legacy web services.
 *
 * @author psytek
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class LegacyRestletApplication extends Application {

    private Router internalRouter;

    public LegacyRestletApplication() {
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

        // Define routing to resources
        this.internalRouter.setDefaultMatchingQuery(false);
        internalRouter.attach("/dim", RestDimResource.class);// search resource
        internalRouter.attach("/file", RestFileResource.class);// file download resource
        internalRouter.attach("/dump", RestDumpResource.class);// dump resource
        internalRouter.attach("/tags", RestTagsResource.class);// list of avalilable tags resource
        internalRouter.attach("/wado", RestWADOResource.class);
        internalRouter.attach("/img", RestDcmImageResource.class);
        internalRouter.attach("/examTime", ExamTimeResource.class);

        // Advanced Dicoogle Features
        internalRouter.attach("/doIndex", ForceIndexing.class);

        LoggerFactory.getLogger(LegacyRestletApplication.class).debug("Legacy service routes: {}",
                internalRouter.getRoutes());
        return internalRouter;
    }
}
