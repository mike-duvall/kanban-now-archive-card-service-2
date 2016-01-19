package main.resources;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.account.DefaultAccount;
import com.stormpath.sdk.servlet.account.AccountResolver;
import main.api.ArchivedCard;
import main.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;



@Component
@Path("/archivedCards")
@Produces(MediaType.APPLICATION_JSON)
public class ArchivedCardResource {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static String archivedCardsTableName = "public.archived_card";
    private final Calendar tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));



    @Context
    private HttpServletRequest servletRequest;

    @GET
    @Path("{userId}")
    public List<ArchivedCard> getArrchivedCards(@PathParam("userId") String userId) {
        securityCheck();
        String query = "select cardtext, archiveddate, id, user_id from " + archivedCardsTableName +
                " where user_id = ?";
        String[] parameters = new String[] {userId};
        List<ArchivedCard> archivedCardList = this.jdbcTemplate.query(
                query,
                parameters,
                new ArchivedCardRowMapper()
        );

        return archivedCardList;
    }


    private class ArchivedCardRowMapper implements RowMapper<ArchivedCard> {
        @Override
        public ArchivedCard mapRow(ResultSet rs, int rowNum) throws SQLException {
            ArchivedCard card = new ArchivedCard();
            card.setText(rs.getString("cardtext"));
            Timestamp archiveDateTimestamp = rs.getTimestamp("archiveddate", tzUTC);
            card.setDate(convertTimestampToISO8601String(archiveDateTimestamp));
            card.setId(rs.getLong("id"));
            card.setUserId(rs.getString("user_id"));
            return card;
        }
    };



    private String convertTimestampToISO8601String(Timestamp timestamp) {
        Date returnedDate = new Date(timestamp.getTime());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String my8601formattedDate = df.format(returnedDate);
        return  my8601formattedDate;
    }

    @POST
    @Path("{userId}")
    public ArchivedCard addArchivedCard( @PathParam("userId") String userId, ArchivedCard archivedCard) {
        securityCheck();
        return insertArchivedCard(userId, archivedCard);
    }



    private void securityCheck() {
        Account account = AccountResolver.INSTANCE.getAccount(servletRequest);
        if (account == null){
            throw new ForbiddenException();
        }
        else {
            DefaultAccount defaultAccount = (DefaultAccount) account;
            String href = defaultAccount.getHref();
            String stormpathId = parseStormPathIdFromHref(href);
            int x = 3;
        }

    }

    private String parseStormPathIdFromHref(String href) {
        int lastSlash = href.lastIndexOf("/");
        String id = href.substring(lastSlash + 1, href.length());
        return id;
    }


    private ArchivedCard insertArchivedCard(final String userId, ArchivedCard archivedCard) {
        final String INSERT_SQL = "insert into " + archivedCardsTableName + " (user_id, cardtext, archiveddate) values (?, ?, ?)";
        final String cardText = archivedCard.getText();
        java.util.Date currentDateAndTime = new java.util.Date();
        final Timestamp archiveDateTimestamp = new Timestamp( currentDateAndTime.getTime() );
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    String[] keysToReturnInKeyHolder = new String[] {"id"};
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, keysToReturnInKeyHolder);
                    ps.setString(1, userId );
                    ps.setString(2, cardText );
                    ps.setTimestamp(3, archiveDateTimestamp, tzUTC );
                    return ps;
                }
            },
            keyHolder);

        archivedCard.setId(keyHolder.getKey().longValue());
        archivedCard.setUserId(userId);
        archivedCard.setDate(convertTimestampToISO8601String(archiveDateTimestamp));
        return archivedCard;
    }


    @DELETE
    @Path("{userId}/{id}")
    public Response delete(@PathParam("userId") String userId, @PathParam("id") Long cardId) {
        securityCheck();
        if(!doesCardExist(userId, cardId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.jdbcTemplate.update(
                "delete from " + archivedCardsTableName + " where id = ?",
                Long.valueOf(cardId));

        return Response.noContent().build();
    }

    private boolean doesCardExist(String userId, Long cardId) {
        String query = "select cardtext, archiveddate, id, user_id from " + archivedCardsTableName +
                " where user_id = ? and id = ?";
        Object[] parameters = new Object[] {userId, cardId};
        List<ArchivedCard> archivedCardList =
                jdbcTemplate.query(
                       query,
                       parameters,
                       new ArchivedCardRowMapper());
        if(archivedCardList.size() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

}
