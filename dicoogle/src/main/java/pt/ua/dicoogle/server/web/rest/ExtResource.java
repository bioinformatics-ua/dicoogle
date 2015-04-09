package pt.ua.dicoogle.server.web.rest;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ExtResource extends ServerResource {
    @Override
    protected Representation doHandle() {
        throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
}
