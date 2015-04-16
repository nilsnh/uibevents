package no.nilsnh.uibevents.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

// Define tables and collumns
public class EventContract {

    public static final String CONTENT_AUTHORITY = "no.nilsnh.uibevents";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_EVENT = "event";

    public static final class EventEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI;

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;

        public static final Integer COLUMN_EVENT_ID = 1;
        public static final Integer COLUMN_EVENT_TYPE = 2;
        public static final Integer COLUMN_EVENT_TITLE = 3;
        public static final Integer COLUMN_EVENT_DATE_FROM = 4;
        public static final Integer COLUMN_EVENT_DATE_TO = 5;
        public static final Integer COLUMN_EVENT_DATE_LOCATION = 6;
        public static final Integer COLUMN_EVENT_DETAILS = 7;
        public static final Integer COLUMN_EVENT_URL = 8;

        public static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
