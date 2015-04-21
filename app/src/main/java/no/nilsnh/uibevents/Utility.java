package no.nilsnh.uibevents;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    public static String getFriendLyDate(String dateString) {

        SimpleDateFormat dateInputFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);

        SimpleDateFormat dateOutPutFormat =
                new SimpleDateFormat("dd.MM.yy HH:mm", Locale.GERMAN);

        Date date = null;
        try {
            date = dateInputFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateOutPutFormat.format(date);
    }

    public static String parseDetailsText(String eventDetailsText) {
        if (eventDetailsText.equals("null")) return "Denne eventen har ingen beskrivelse.";
        else return eventDetailsText.replace("/\"", "\"");
    }

}
