package no.nilsnh.uibevents.data;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {

    private String id;
    private String type;
    private String title;
    private String dateFrom;
    private String dateTo;
    private String location;
    private String details;
    private String url;

    public Event(ContentValues values) {
        id = values.getAsString("id");
        type = values.getAsString("type");
        title = values.getAsString("title");
        dateFrom = values.getAsString("date_from");
        dateTo = values.getAsString("date_to");
        location = values.getAsString("location");
        details = values.getAsString("details");
        url = values.getAsString("url");
    }

    public Event(String event) {
        String[] eventDetailsString = event.split(";");
        id = eventDetailsString[0];
        type = eventDetailsString[1];
        title = eventDetailsString[2];
        dateFrom = eventDetailsString[3];
        dateTo = eventDetailsString[4];
        location = eventDetailsString[5];
        details = eventDetailsString[6];
        url = eventDetailsString[7];
    }

    public Event(JSONObject event) {
        try {
            id = event.getString("id");
            type = event.getString("type");
            title = event.getString("title");
            dateFrom = event.getString("date_from");
            dateTo = event.getString("date_to");
            location = event.getString("location");
            details = event.getString("lead");
            url = event.getString("path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ContentValues getContentValues() {
        ContentValues cValues = new ContentValues();
        cValues.put(EventContract.EventEntry.COLUMN_EVENT_ID, id);
        cValues.put(EventContract.EventEntry.COLUMN_EVENT_TYPE, type);
        cValues.put(EventContract.EventEntry.COLUMN_EVENT_TITLE, title);
        cValues.put(EventContract.EventEntry.COLUMN_EVENT_DATE_FROM, dateFrom);
        cValues.put(EventContract.EventEntry.COLUMN_EVENT_DATE_TO, dateTo);
        cValues.put(EventContract.EventEntry.COLUMN_EVENT_LOCATION, location);
        cValues.put(EventContract.EventEntry.COLUMN_EVENT_DETAILS, details);
        cValues.put(EventContract.EventEntry.COLUMN_EVENT_URL, url);
        return cValues;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public String getLocation() {
        return location;
    }

    public String getDetails() {
        return details;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return id + ";" +
                type + ";" +
                title + ";" +
                dateFrom + ";" +
                dateTo + ";" +
                location + ";" +
                details + ";" +
                url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (id != null ? !id.equals(event.id) : event.id != null) return false;
        if (type != null ? !type.equals(event.type) : event.type != null) return false;
        if (title != null ? !title.equals(event.title) : event.title != null) return false;
        if (dateFrom != null ? !dateFrom.equals(event.dateFrom) : event.dateFrom != null)
            return false;
        if (dateTo != null ? !dateTo.equals(event.dateTo) : event.dateTo != null) return false;
        if (location != null ? !location.equals(event.location) : event.location != null)
            return false;
        if (details != null ? !details.equals(event.details) : event.details != null) return false;
        return !(url != null ? !url.equals(event.url) : event.url != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (dateFrom != null ? dateFrom.hashCode() : 0);
        result = 31 * result + (dateTo != null ? dateTo.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    public boolean isToBeDeleted(String selection, String[] selectionArgs) {
        Boolean isToBeDeleted = false;
        for (String arg : selectionArgs) {
            if (getContentValues().getAsString(selection).equals(arg)) isToBeDeleted = true;
        }
        return isToBeDeleted;
    }
}
