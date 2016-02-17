package main;

//import com.blackbaud.bluemoon.dojo.client.api.CustomersApi;
import groovyx.net.http.RESTClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.URISyntaxException;

@Configuration
public class ApplicationTestConfig {

//    @Value("${test-server.base_url:http://localhost}")
//    private String serverBaseUrl;
//    @Value("${test-server.base_url:http://localhost}:${server.port}")
//    private String hostUri;

//    @Value("${serviceport}")
//    private String serviceport;
//
//    @Value("${servicehost}")
//    private String servicehost;
//
//    @Value("${urlprotocol}")
//    private String urlprotocol;


    @Value("${testservice.urlprotocol}://${testservice.host}:${testservice.port}/")
    String baseUrl;


//    @Value("${urlprotocol}://${servicehost}:${serviceport}/")
//    String baseUrl;


    @Bean
    public RESTClient restClient() throws URISyntaxException {
        return new RESTClient(baseUrl);
    }
//    @Bean
//    public CustomersApi customersApi() {
//        return new CustomersApi();
//    }
//
//    @Bean
//    @Primary
//    public RESTClient restClient() throws URISyntaxException {
//        RESTClient client = new RESTClient(hostUri);
//        return client;
//    }
//
//    @Bean
//    public RESTClient managementRestClient(@Value("${management.port}") String managementPort) throws URISyntaxException {
//        RESTClient client = new RESTClient(serverBaseUrl + ":" + managementPort);
//        return client;
//    }

}
