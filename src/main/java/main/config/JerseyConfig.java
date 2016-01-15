package main.config;


import main.exception.GenericExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {

    @PostConstruct
    public void initialize() {
        packages("main.resources");
        register(GenericExceptionMapper.class);
    }
}
