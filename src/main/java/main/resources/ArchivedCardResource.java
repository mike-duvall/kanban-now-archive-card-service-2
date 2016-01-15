package main.resources;

import com.stormpath.sdk.account.Account;
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

    private static String archivedCardsTableName = "public.test_table_for_mike";
    private final Calendar tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));



    @Context
    private HttpServletRequest servletRequest;

    @GET
    public List<ArchivedCard> getArrchivedCards() {
        securityCheck();
        List<ArchivedCard> archivedCardList = this.jdbcTemplate.query(
                "select cardtext, archiveddate, id from " + archivedCardsTableName,
                new RowMapper<ArchivedCard>() {
                    public ArchivedCard mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ArchivedCard card = new ArchivedCard();
                        card.setText(rs.getString("cardtext"));
                        Timestamp archiveDateTimestamp = rs.getTimestamp("archiveddate", tzUTC);
                        card.setDate(convertTimestampToISO8601String(archiveDateTimestamp));
                        card.setId(rs.getLong("id"));
                        return card;
                    }
                });

        return archivedCardList;
    }

    private String convertTimestampToISO8601String(Timestamp timestamp) {
        Date returnedDate = new Date(timestamp.getTime());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String my8601formattedDate = df.format(returnedDate);
        return  my8601formattedDate;
    }

    @POST
    public ArchivedCard addArchivedCard(  ArchivedCard archivedCard) {
        securityCheck();
        return insertArchivedCard(archivedCard);
    }

    private void securityCheck() {
        Account account = AccountResolver.INSTANCE.getAccount(servletRequest);
        if (account == null) { throw new ForbiddenException(); }
    }


    private ArchivedCard insertArchivedCard(ArchivedCard archivedCard) {
        final String INSERT_SQL = "insert into " + archivedCardsTableName + " (cardtext, archiveddate) values (?, ?)";
        final String cardText = archivedCard.getText();
        java.util.Date currentDateAndTime = new java.util.Date();
        final Timestamp archiveDateTimestamp = new Timestamp( currentDateAndTime.getTime() );
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    String[] keysToReturnInKeyHolder = new String[] {"id"};
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, keysToReturnInKeyHolder);
                    ps.setString(1, cardText );
                    ps.setTimestamp(2, archiveDateTimestamp, tzUTC );
                    return ps;
                }
            },
            keyHolder);

        archivedCard.setId(keyHolder.getKey().longValue());
        archivedCard.setDate(convertTimestampToISO8601String(archiveDateTimestamp));
        return archivedCard;
    }


    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        securityCheck();
        this.jdbcTemplate.update(
                "delete from " + archivedCardsTableName + " where id = ?",
                Long.valueOf(id));

        return Response.noContent().build();
    }

}
