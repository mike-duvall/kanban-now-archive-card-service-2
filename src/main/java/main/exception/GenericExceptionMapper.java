package main.exception;




import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        exception.printStackTrace();
        RestError error = new RestError();
        error.setStatus(500);
        error.setCode(666);
        return Response.serverError().entity(error).build();
    }

}
