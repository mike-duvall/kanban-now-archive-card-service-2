package com.mike

import groovyx.net.http.RESTClient
import org.apache.http.client.HttpResponseException
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory
import spock.lang.Ignore
import spock.lang.Specification
import java.security.KeyStore



@Ignore
class SampleSpec extends Specification {

    String host = "localhost"
    String port = "9000"

    String baseUrl = "https://${host}:${port}/"
    RESTClient restClient = new RESTClient(baseUrl)



    def "Sample"() {

        when:
        String accessKeyId  = System.getProperty("accessKeyId")
        String secretKey = System.getProperty("secretKey")

        String userAndPassword = accessKeyId + ":" + secretKey
        String userAndPasswordEncoded = userAndPassword.bytes.encodeBase64().toString()
        String headerValue = "Basic " + userAndPasswordEncoded

        def keyStore = KeyStore.getInstance( KeyStore.defaultType )

        String keyStoreFilename = System.getProperty("server_ssl_key_store")
        String keyStorePassword = System.getProperty("server_ssl_key_store_password")

        String keyStorePath = "/" + keyStoreFilename

        getClass().getResource( keyStorePath ).withInputStream {
            keyStore.load( it, keyStorePassword.toCharArray() )
        }


//        SSLSocketFactory sf = new SSLSocketFactory(
//                SSLContext.getInstance("TLS"),
//                SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        SSLSocketFactory sf = new SSLSocketFactory(keyStore);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
        Scheme sch = new Scheme("https", 443, sf);

        restClient.client.connectionManager.schemeRegistry.register( sch )

        def response = callRest {
            restClient.get(
                    path : '/archivedCards',
                    requestContentType:  'application/json',
                    headers: ["Authorization" : headerValue ]

            )
        }


        then:
        assert response.responseData.size == 2
        assert response.responseData[0].text == "Take Claritin"
        assert response.responseData[1].text == "Save the whales"

    }

    def "test" () {
        given:
        int x = 3;

        when:
        x = 4

        then:
        true
    }




    static def callRest(def closure) {
        try {
            return closure()
        } catch (HttpResponseException ex) {
            return ex.response
        }
    }

}