package main

import feign.Feign
import feign.auth.BasicAuthRequestInterceptor
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import groovyx.net.http.RESTClient
import main.archivecardserviceclient.ArchivedCardClient
import main.archivecardserviceclient.Card
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import spock.lang.Specification


@ComponentTest
class PagingSpec extends Specification {

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

    @Autowired
    private String baseUrl

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




    def "should page results"() {

        given:
        ArchivedCardClient archivedCardClient = Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .requestInterceptor(new BasicAuthRequestInterceptor(accesskeyid, secretkey))
                .target(ArchivedCardClient.class, baseUrl);


        Card newCard1 = new Card()
        newCard1.setText("Test text 1")
        newCard1.setBoardId(444)
        newCard1 = archivedCardClient.createCard(userId1, newCard1)

        Card newCard2 = new Card()
        newCard2.setText("Test text 2")
        newCard2.setBoardId(445)
        newCard2 = archivedCardClient.createCard(userId1, newCard2)

        Card newCard3 = new Card()
        newCard3.setText("Test text 3")
        newCard3.setBoardId(445)
        newCard3 = archivedCardClient.createCard(userId1, newCard3)

        Card newCard4 = new Card()
        newCard4.setText("Test text 4")
        newCard4.setBoardId(445)
        newCard4 = archivedCardClient.createCard(userId1, newCard4)

        Card newCard5 = new Card()
        newCard5.setText("Test text 5")
        newCard5.setBoardId(445)
        newCard5 = archivedCardClient.createCard(userId1, newCard5)


        when:
        List<Card> cardList = archivedCardClient.getCardsForUserWithPaging(userId1, 0, 2);

        then:
        assert cardList.size == 2
        assert cardList[0].id == newCard1.id
        assert cardList[1].id == newCard2.id

        when:
        cardList = archivedCardClient.getCardsForUserWithPaging(userId1, 0, 3);

        then:
        assert cardList.size == 3
        assert cardList[0].id == newCard1.id
        assert cardList[1].id == newCard2.id
        assert cardList[2].id == newCard3.id

        when:
        cardList = archivedCardClient.getCardsForUserWithPaging(userId1, 1, 2);

        then:
        assert cardList.size == 2
        assert cardList[0].id == newCard3.id
        assert cardList[1].id == newCard4.id


        cleanup:
        archivedCardClient.deleteCard(userId1, newCard1.id )
        archivedCardClient.deleteCard(userId1, newCard2.id )
        archivedCardClient.deleteCard(userId1, newCard3.id )
        archivedCardClient.deleteCard(userId1, newCard4.id )
        archivedCardClient.deleteCard(userId1, newCard5.id )

    }


}