package main;

import groovyx.net.http.RESTClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.URISyntaxException;

@Configuration
public class ApplicationTestConfig {

    @Value("${testservice.urlprotocol}://${testservice.host}:${testservice.port}/")
    String baseUrl;

    @Value("${accesskeyid}")
    String accesskeyid;

    @Value("${secretkey}")
    String secretkey;


    @Bean
    public RESTClient restClient() throws URISyntaxException {
        return new RESTClient(baseUrl);
    }

    @Bean
    public String accesskeyid() {
        return accesskeyid;
    }

    @Bean
    public String secretkey() {
        return secretkey;
    }

}
