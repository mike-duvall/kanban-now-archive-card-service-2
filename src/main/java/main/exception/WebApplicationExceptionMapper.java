package main.exception;




import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        exception.printStackTrace();
        String message = exception.getMessage();
        RestError error = new RestError();
        int exceptionStatus = exception.getResponse().getStatus();
        error.setStatus(exceptionStatus);
        error.setCode(666);
        return Response.status(exceptionStatus).entity(error).build();

    }

}
