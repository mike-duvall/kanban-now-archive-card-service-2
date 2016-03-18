package main.archivecardserviceclient

import feign.Headers
import feign.Param
import feign.RequestLine

interface ArchivedCardClient {
    @RequestLine("GET /archivedCards/{userId}/")
    List<Card> cards(@Param("userId") String userId);


    @RequestLine("GET /archivedCards/{userId}/?boardId={boardId}")
    List<Card> getCardsForUserAndBoard(@Param("userId") String userId, @Param("boardId") Integer boardId);


    @RequestLine("GET /archivedCards/{userId}/?pageNumber={pageNumber}&pageSize={pageSize}")
    List<Card> getCardsForUserWithPaging(
            @Param("userId") String userId,
            @Param("pageNumber") Integer pageNumber,
            @Param("pageSize") Integer pageSize);


//    @RequestLine("GET /archivedCards/{userId}/?page={page},pageSize={pageSize}")
//    List<Card> cards(@Param("userId") String userId, @Param("page") Integer page, @Param("pageSize") Integer pageSize );



    @RequestLine("POST /archivedCards/{userId}/")
    @Headers("Content-Type: application/json")
    Card createCard(@Param("userId") String userId, Card aCard);

    @RequestLine("DELETE /archivedCards/{userId}/{cardId}")
    void deleteCard(@Param("userId") String userId, @Param("cardId") Long cardId);

}

