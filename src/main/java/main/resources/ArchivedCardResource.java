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
import java.util.List;

@Component
@Path("/archivedCards")
@Produces(MediaType.APPLICATION_JSON)
public class ArchivedCardResource {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static String archivedCardsTableName = "public.test_table_for_mike";


    @Context
    private HttpServletRequest servletRequest;

    @GET
    public List<ArchivedCard> getArrchivedCards() {

        Account account = AccountResolver.INSTANCE.getAccount(servletRequest);

        if (account == null) { throw new ForbiddenException(); }

        List<ArchivedCard> archivedCardList = this.jdbcTemplate.query(
                "select * from " + archivedCardsTableName,
                new RowMapper<ArchivedCard>() {
                    public ArchivedCard mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ArchivedCard card = new ArchivedCard();
                        card.setText(rs.getString("cardtext"));
                        card.setDate(rs.getString("archiveddate"));
                        card.setId(rs.getLong("id"));
                        return card;
                    }
                });

        return archivedCardList;
    }

    @POST
    public ArchivedCard addArchivedCard(  ArchivedCard archivedCard) {

        Account account = AccountResolver.INSTANCE.getAccount(servletRequest);
        if (account == null) { throw new ForbiddenException(); }
        return insertArchivedCard(archivedCard);
    }


    private ArchivedCard insertArchivedCard(ArchivedCard archivedCard) {
        final String INSERT_SQL = "insert into " + archivedCardsTableName + " (cardtext, archiveddate) values (?, ?)";
        final String cardText = archivedCard.getText();
        final String cardDate = archivedCard.getDate();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    String[] keysToReturnInKeyHolder = new String[] {"id"};
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, keysToReturnInKeyHolder);
                    ps.setString(1, cardText );
                    ps.setString(2, cardDate );
                    return ps;
                }
            },
            keyHolder);

        archivedCard.setId(keyHolder.getKey().longValue());
        return archivedCard;
    }


    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {

        this.jdbcTemplate.update(
                "delete from " + archivedCardsTableName + " where id = ?",
                Long.valueOf(id));

        return Response.noContent().build();
    }


//    public String getTodayDateAsString() {
//        DateTimeZone chicagoTimeZone = DateTimeZone.forID( "America/Chicago" );
//        LocalDate localDate = new LocalDate(chicagoTimeZone);
//        String dateAsString = localDate.toString("M/d/yyy");
//        return dateAsString;
//    }


}
