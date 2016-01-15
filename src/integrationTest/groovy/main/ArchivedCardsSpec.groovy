package main

import groovyx.net.http.RESTClient
import org.apache.http.client.HttpResponseException
import spock.lang.Specification


class ArchivedCardsSpec extends Specification {

    static final int HTTP_OK = 200
    static final int HTTP_NO_CONTENT = 204
    static final int HTTP_FORBIDDEN = 403

    String host = readTestProperty("serviceHost")
    String port = readTestProperty("servicePort")

    String accessKeyId = readTestProperty("accessKeyId")
    String secretKey = readTestProperty("secretKey")

    String urlProtocol = readTestProperty("urlProtocol")

    String baseUrl = "${urlProtocol}://${host}:${port}/"
    RESTClient restClient = new RESTClient(baseUrl)

    String userAndPassword = accessKeyId + ":" + secretKey
    String userAndPasswordEncoded = userAndPassword.bytes.encodeBase64().toString()
    String validAuthHeader = "Basic " + userAndPasswordEncoded

    private static String readTestProperty(String key) {
        String result = System.getProperty(key)
        if(result == null) {
            result = System.getenv(key);
        }

        return result
    }


    def "unauthorized user cannot call GET"() {
        given:
        String bogusAuthHeader = generateBogusAuthenticationHeader()

        when:
        def response = callRest {
            restClient.get(
                    path : '/archivedCards',
                    contentType:  'application/json',
                    headers: ["Authorization" : bogusAuthHeader ]

            )
        }

        then:
        assert response.status == HTTP_FORBIDDEN
    }

    def "unauthorized user cannot call POST"() {
        given:
        String bogusAuthHeader = generateBogusAuthenticationHeader()

        when:
        def newCard1 = [
                text: 'A new card',
                date: '1/1/1967'
        ]

        def response = callRest {
            restClient.post(
                    path : '/archivedCards',
                    body: newCard1,
                    contentType:  'application/json',
                    headers: ["Authorization" : bogusAuthHeader ]

            )
        }

        then:
        assert response.status == HTTP_FORBIDDEN

    }


    def "unauthorized user cannot call DELETE"() {
        given:
        String bogusAuthHeader = generateBogusAuthenticationHeader()

        when:
        long bogusId = -1
        String deletePath = '/archivedCards/' + bogusId
        def response = callRest {
            restClient.delete(
                    path : deletePath,
                    contentType:  'application/json',
                    headers: ["Authorization" : bogusAuthHeader ]

            )
        }

        then:
        assert response.status == HTTP_FORBIDDEN

    }


    def "add, retrieve, and delete cards"() {
        when:
        def newCard1 = [
                text: 'A new card'
        ]

        def response = callRest {
            restClient.post(
                    path : '/archivedCards',
                    body: newCard1,
                    contentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader]
            )
        }
        newCard1 = response.responseData

        then:
        assert response.status == HTTP_OK
        assert response.responseData.text == newCard1.text
        assert response.responseData.date != null
        assert response.responseData.id != null

        when:
        response = callRest {
            restClient.get(
                    path : '/archivedCards',
                    requestContentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader ]

            )
        }

        then:
        assert response.responseData.size == 1
        assert response.responseData[0].text == newCard1.text
        assert response.responseData[0].date == newCard1.date
        assert response.responseData[0].id != null


        when:
        String deletePath = '/archivedCards/' + response.responseData[0].id
        response = callRest {
            restClient.delete(
                    path : deletePath,
                    requestContentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader ]

            )
        }

        then:
        assert response.status == HTTP_NO_CONTENT

        when:
        response = callRest {
            restClient.get(
                    path : '/archivedCards',
                    requestContentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader ]

            )
        }

        then:
        assert response.responseData.size == 0

    }


    private String generateBogusAuthenticationHeader() {
        String baseUserAndPassword = accessKeyId + ":" + secretKey + "xxx"
        String badUserAndPasswordEncoded = baseUserAndPassword.bytes.encodeBase64().toString()
        String headerValue = "Basic " + badUserAndPasswordEncoded
        return headerValue
    }



    static def callRest(def closure) {
        try {
            return closure()
        } catch (HttpResponseException ex) {
            return ex.response
        }
    }

}