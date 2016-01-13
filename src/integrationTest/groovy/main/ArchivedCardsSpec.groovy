package main

import groovyx.net.http.RESTClient
import org.apache.http.client.HttpResponseException
import spock.lang.Specification


class ArchivedCardsSpec extends Specification {

    static final int HTTP_OK = 200

    String host = readTestProperty("serviceHost")
    String port = readTestProperty("servicePort")

    String accessKeyId = readTestProperty("accessKeyId")
    String secretKey = readTestProperty("secretKey")

    String urlProtocol = readTestProperty("urlProtocol")

    String baseUrl = "${urlProtocol}://${host}:${port}/"
    RESTClient restClient = new RESTClient(baseUrl)

    String userAndPassword = accessKeyId + ":" + secretKey
    String userAndPasswordEncoded = userAndPassword.bytes.encodeBase64().toString()
    String headerValue = "Basic " + userAndPasswordEncoded

    private static String readTestProperty(String key) {
        String result = System.getProperty(key)
        if(result == null) {
            result = System.getenv(key);
        }

        return result
    }

    def "get cards"() {
        when:
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


    def "add cards then retrieve them"() {

        when:
        def newCard1 = [
                text: 'A new card',
                date: '1/1/1967'
        ]

        def postResponse = callRest {
            restClient.post(
                    path : '/archivedCards',
                    body: newCard1,
                    requestContentType:  'application/json',
                    headers: ["Authorization" : headerValue ]

            )
        }

        then:
        assert postResponse.status == HTTP_OK

        and:
        def response = callRest {
            restClient.get(
                    path : '/archivedCards',
                    requestContentType:  'application/json',
                    headers: ["Authorization" : headerValue ]

            )
        }

        then:
        assert response.responseData.size == 1
        assert response.responseData[0].text == newCard1.text

    }




    static def callRest(def closure) {
        try {
            return closure()
        } catch (HttpResponseException ex) {
            return ex.response
        }
    }

}