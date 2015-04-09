package pt.ua.dicoogle.server.web.rest;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;

/**
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ExtResource extends ServerResource {
    @Override
    protected Representation doHandle() {
        this.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        return new StringRepresentation("Extended services resource. Sub-resource interaction expected.");
    }
}
