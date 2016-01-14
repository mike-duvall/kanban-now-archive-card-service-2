package main.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ForbiddenException extends WebApplicationException {
    public ForbiddenException() {
        super(Response.status(Response.Status.FORBIDDEN)
                      .entity("").type(MediaType.TEXT_PLAIN).build());
    }
}
