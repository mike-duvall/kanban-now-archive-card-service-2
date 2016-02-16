package main.exception;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);



    @Override
    public Response toResponse(Exception exception) {
        logger.error("Error", exception);
        exception.printStackTrace();
        RestError error = new RestError();
        error.setStatus(500);
        error.setCode(666);
        return Response.serverError().entity(error).build();
    }

}
