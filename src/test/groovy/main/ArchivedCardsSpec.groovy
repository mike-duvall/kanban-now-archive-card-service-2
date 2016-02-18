package main

import groovyx.net.http.RESTClient
import org.apache.http.client.HttpResponseException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import spock.lang.Specification


@ComponentTest
class ArchivedCardsSpec extends Specification {

    static final int HTTP_OK = 200
    static final int HTTP_NO_CONTENT = 204
    static final int HTTP_FORBIDDEN = 403
    static final int HTTP_NOT_FOUND = 404

    private String validAuthHeader

    @Autowired
    private RESTClient restClient

    @Autowired
    private String accesskeyid

    @Autowired
    private String secretkey


    @Bean
    public String secretkey() {
        return secretkey;
    }


    String userId1 = 'userId1'
    String userId2 = 'userId2'
    String basePathUser1 = "/archivedCards/${userId1}"
    String basePathUser2 = "/archivedCards/${userId2}"


    def setup() {
        String userAndPassword = accesskeyid + ":" + secretkey
        String userAndPasswordEncoded = userAndPassword.bytes.encodeBase64().toString()
        validAuthHeader = "Basic " + userAndPasswordEncoded
    }

    def "unauthorized user cannot call GET"() {
        given:
        String bogusAuthHeader = generateBogusAuthenticationHeader()

        when:
        def response = callRest {
            restClient.get(
                    path : basePathUser1,
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
                    path : basePathUser1,
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
        String deletePath = "/archivedCards/${userId1}/${bogusId}"
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


    def "should allow query of cards for a particular board"() {
        when:
        def newCard1 = [
                text: 'Card for userId1, board 5',
                boardId: 5
        ]

        def response = callRest {
            restClient.post(
                    path : basePathUser1,
                    body: newCard1,
                    contentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader]
            )
        }
        newCard1 = response.responseData

        then:
        assert response.status == HTTP_OK
        assert response.responseData.text == newCard1.text
        assert response.responseData.userId == userId1
        assert response.responseData.date != null
        assert response.responseData.id != null
        assert response.responseData.boardId == newCard1.boardId

        when:
        def newCard2 = [
                text: 'Card for userId1, board 7',
                boardId: 7
        ]


        response = callRest {
            restClient.post(
                    path : basePathUser1,
                    body: newCard2,
                    contentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader]
            )
        }
        newCard2 = response.responseData

        then:
        assert response.status == HTTP_OK
        assert response.responseData.text == newCard2.text
        assert response.responseData.userId == userId1
        assert response.responseData.date != null
        assert response.responseData.id != null
        assert response.responseData.boardId == newCard2.boardId



        when:
        response = callRest {
            restClient.get(
                    path : basePathUser1,
                    requestContentType:  'application/json',
                    query:[boardId: 7],
                    headers: ["Authorization" : validAuthHeader ]

            )
        }

        then:
        assert response.status == HTTP_OK
        assert response.responseData.size == 1
        assert response.responseData[0].text == newCard2.text
        assert response.responseData[0].userId == newCard2.userId
        assert response.responseData[0].date == newCard2.date
        assert response.responseData[0].id != null
        assert response.responseData[0].boardId == 7

    }



    def "add, retrieve, and delete cards, for two users"() {
        when:

        def newCard1 = [
                text: 'A new card'
        ]


        def response = callRest {
            restClient.post(
                    path : basePathUser1,
                    body: newCard1,
                    contentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader]
            )
        }
        newCard1 = response.responseData

        then:
        assert response.status == HTTP_OK
        assert response.responseData.text == newCard1.text
        assert response.responseData.userId == userId1
        assert response.responseData.date != null
        assert response.responseData.id != null


        when:
        def newCard2 = [
                text: 'A new card for user 2'
        ]


        response = callRest {
            restClient.post(
                    path : basePathUser2,
                    body: newCard2,
                    contentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader]
            )
        }
        newCard2 = response.responseData

        then:
        assert response.status == HTTP_OK
        assert response.responseData.text == newCard2.text
        assert response.responseData.userId == userId2
        assert response.responseData.date != null
        assert response.responseData.id != null



        when:
        response = callRest {
            restClient.get(
                    path : basePathUser1,
                    requestContentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader ]

            )
        }

        then:
        assert response.status == HTTP_OK
        assert response.responseData.size == 1
        assert response.responseData[0].text == newCard1.text
        assert response.responseData[0].userId == newCard1.userId
        assert response.responseData[0].date == newCard1.date
        assert response.responseData[0].id != null


        when:
        response = callRest {
            restClient.get(
                    path : basePathUser2,
                    requestContentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader ]

            )
        }

        then:
        assert response.status == HTTP_OK
        assert response.responseData.size == 1
        assert response.responseData[0].text == newCard2.text
        assert response.responseData[0].userId == newCard2.userId
        assert response.responseData[0].date == newCard2.date
        assert response.responseData[0].id != null



        when:
        String cardId1 = newCard1.id
        String deletePath = "/archivedCards/${userId1}/${cardId1}"
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
                    path : basePathUser1,
                    requestContentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader ]

            )
        }

        then:
        assert response.status == HTTP_OK
        assert response.responseData.size == 0


        when:
        String cardId2 = newCard2.id
        String deletePath2 = "/archivedCards/${userId2}/${cardId2}"
        response = callRest {
            restClient.delete(
                    path : deletePath2,
                    requestContentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader ]

            )
        }

        then:
        assert response.status == HTTP_NO_CONTENT

        when:
        response = callRest {
            restClient.get(
                    path : basePathUser2,
                    requestContentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader ]

            )
        }

        then:
        assert response.status == HTTP_OK
        assert response.responseData.size == 0


    }

    def "Return NOT_FOUND if attempt to delete cardId with wrong user"() {
        when:
        String userId1 = 'userId1'

        def newCard1 = [
                text: 'A new card'
        ]

        String basePathUser1 = "/archivedCards/${userId1}"
        def response = callRest {
            restClient.post(
                    path : basePathUser1,
                    body: newCard1,
                    contentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader]
            )
        }
        newCard1 = response.responseData

        then:
        assert response.status == HTTP_OK

        when:
        String wrongUser = "user2"
        String cardId1 = newCard1.id
        String deletePath = "/archivedCards/${wrongUser}/${cardId1}"
        response = callRest {
            restClient.delete(
                    path : deletePath,
                    requestContentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader ]

            )
        }

        then:
        assert response.status == HTTP_NOT_FOUND

        cleanup:

        deletePath = "/archivedCards/${userId1}/${cardId1}"
        response = callRest {
            restClient.delete(
                    path : deletePath,
                    requestContentType:  'application/json',
                    headers: ["Authorization" : validAuthHeader ]

            )
        }


    }

    private String generateBogusAuthenticationHeader() {
        String baseUserAndPassword = accesskeyid + ":" + secretkey + "xxx"
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