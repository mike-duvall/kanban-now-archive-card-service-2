package com.mike

import groovyx.net.http.RESTClient
import org.apache.http.client.HttpResponseException
import spock.lang.Ignore
import spock.lang.Specification




@Ignore
class ArchivedCardsSpec extends Specification {

//    String host = "localhost"
//    String port = "9000"

    String host = "mike-test-4.cfapps.io"
//    String port = "80"
    String port = "443"


    String baseUrl = "https://${host}:${port}/"
    RESTClient restClient = new RESTClient(baseUrl)


    def "get cards, no certificate"() {

        when:
        String accessKeyId  = System.getProperty("accessKeyId")
        String secretKey = System.getProperty("secretKey")

        String userAndPassword = accessKeyId + ":" + secretKey
        String userAndPasswordEncoded = userAndPassword.bytes.encodeBase64().toString()
        String headerValue = "Basic " + userAndPasswordEncoded

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


    static def callRest(def closure) {
        try {
            return closure()
        } catch (HttpResponseException ex) {
            return ex.response
        }
    }

}