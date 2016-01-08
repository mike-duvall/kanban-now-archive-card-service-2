package com.stormpath.tutorial.controller;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.tutorial.exception.ForbiddenException;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ArchivedCardController {


    @RequestMapping("/archivedCards")
    public List<ArchivedCard> archivedCard(HttpServletRequest req) {

        Account account = AccountResolver.INSTANCE.getAccount(req);

        if (account == null) { throw new ForbiddenException(); }

        List<ArchivedCard> archivedCardList = new ArrayList<ArchivedCard>();
        ArchivedCard card1 = new ArchivedCard();

        String todayDateAsString = getTodayDateAsString();

        card1.setText("Take Claritin");
        card1.setDate(todayDateAsString);
        archivedCardList.add(card1);

        ArchivedCard card2 = new ArchivedCard();
        card2.setText("Save the whales");
        card2.setDate(todayDateAsString);
        archivedCardList.add(card2);

        return archivedCardList;
    }

    public String getTodayDateAsString() {
//        DateFormat df = new SimpleDateFormat("M/d/yyyy");
//        TimeZone centralTime = TimeZone.getTimeZone("US/Central");
//        Calendar calendar = new GregorianCalendar(centralTime);
//        Date today = calendar.getTime();
//        return df.format(today);
        DateTimeZone chicagoTimeZone = DateTimeZone.forID( "America/Chicago" );
        LocalDate localDate = new LocalDate(chicagoTimeZone);
        String dateAsString = localDate.toString("M/d/yyy");
        return dateAsString;
    }



}
