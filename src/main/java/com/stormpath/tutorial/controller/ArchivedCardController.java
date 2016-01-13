package com.stormpath.tutorial.controller;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.tutorial.exception.ForbiddenException;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
public class ArchivedCardController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static String archivedCardsTableName = "public.test_table_for_mike";

    @RequestMapping(value = "/archivedCards",  method = RequestMethod.GET)
    public List<ArchivedCard> getArrchivedCards(HttpServletRequest req) {

        Account account = AccountResolver.INSTANCE.getAccount(req);

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

    @RequestMapping(value = "/archivedCards",  method = RequestMethod.POST)
    public ArchivedCard addArchivedCard(HttpServletRequest req, @RequestBody ArchivedCard archivedCard) {

        Account account = AccountResolver.INSTANCE.getAccount(req);

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
