package no.nilsnh.uibevents.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

// Define tables and collumns
public class EventContract {

    public static final String CONTENT_AUTHORITY = "no.nilsnh.uibevents";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_EVENT = "event";

    public static final class EventEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;

        public static final String TABLE_NAME = "event";

        public static final String COLUMN_EVENT_ID = "id";
        public static final String COLUMN_EVENT_TYPE = "type";
        public static final String COLUMN_EVENT_TITLE = "title";
        public static final String COLUMN_EVENT_DATE_FROM = "date_from";
        public static final String COLUMN_EVENT_DATE_TO = "date_to";
        public static final String COLUMN_EVENT_LOCATION = "location";
        public static final String COLUMN_EVENT_DETAILS = "details";
        public static final String COLUMN_EVENT_URL = "url";

        public static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
