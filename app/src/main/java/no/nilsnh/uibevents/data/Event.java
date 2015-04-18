package no.nilsnh.uibevents.data;

import android.content.ContentValues;
import android.net.Uri;

import java.util.Date;

public class Event {

    private String id;
    private String type;
    private String title;
    private Date dateFrom;
    private Date dateTo;
    private String location;
    private String details;
    private Uri url;

    public Event(ContentValues values) {
    }
}
