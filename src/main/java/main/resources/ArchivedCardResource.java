package main.resources;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import main.api.ArchivedCard;
import main.exception.ForbiddenException;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
//    @Context private HttpServletContext servletContext;

    @GET
    public List<ArchivedCard> getArrchivedCards() {

        Account account = AccountResolver.INSTANCE.getAccount(servletRequest);

        if (account == null) { throw new ForbiddenException(); }

        List<ArchivedCard> archivedCardList = this.jdbcTemplate.query(
                "select cardtext, archiveddate from " + archivedCardsTableName,
                new RowMapper<ArchivedCard>() {
                    public ArchivedCard mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ArchivedCard card = new ArchivedCard();
                        card.setText(rs.getString("cardtext"));
                        card.setDate(rs.getString("archiveddate"));
                        return card;
                    }
                });

        return archivedCardList;
    }

//    @RequestMapping(value = "/archivedCards",  method = RequestMethod.POST)
    @POST
    public ArchivedCard addArchivedCard(  ArchivedCard archivedCard) {

        Account account = AccountResolver.INSTANCE.getAccount(servletRequest);

        if (account == null) { throw new ForbiddenException(); }

        this.jdbcTemplate.update(
                "insert into " + archivedCardsTableName + " (cardtext, archiveddate) values (?, ?)",
                archivedCard.getText(), archivedCard.getDate());

        return archivedCard;
    }


    public String getTodayDateAsString() {
        DateTimeZone chicagoTimeZone = DateTimeZone.forID( "America/Chicago" );
        LocalDate localDate = new LocalDate(chicagoTimeZone);
        String dateAsString = localDate.toString("M/d/yyy");
        return dateAsString;
    }


}
