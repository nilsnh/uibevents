package no.nilsnh.uibevents;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.test.AndroidTestCase;

import org.json.JSONException;

import java.util.ArrayList;

import no.nilsnh.uibevents.data.Event;
import no.nilsnh.uibevents.data.EventContract;
import no.nilsnh.uibevents.data.EventDbHelper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends AndroidTestCase {

    EventDbHelper eventDbHelper;

    public void setUp() {
        eventDbHelper = new EventDbHelper(getContext());
    }

    public void testFetchFromApi() throws Throwable {
        String result = eventDbHelper.fetchWebEventData();
        assertNotNull(result);
    }

    public void testParseJsonEvent() {
        ArrayList<Event> events = null;
        try {
            events = EventDbHelper.parseJsonEvents(TestUtils.getExampleJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertTrue(events.size() == 2);
    }

    public void testInsertIntoTextFile() {
        ArrayList<Event> events = null;
        try {
            events = EventDbHelper.parseJsonEvents(TestUtils.getExampleJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getContext().getContentResolver()
                .insert(EventContract.EventEntry.CONTENT_URI, events.get(0).getContentValues());
        assertTrue(eventDbHelper.getStoredData().contains(events.get(0)));
    }

    public void testDeleteAll() {
        ArrayList<Event> events = null;
        try {
            events = EventDbHelper.parseJsonEvents(TestUtils.getExampleJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getContext().getContentResolver()
                .insert(EventContract.EventEntry.CONTENT_URI, events.get(0).getContentValues());
        assertTrue(eventDbHelper.getStoredData().contains(events.get(0)));
        Integer deletedRows = getContext().getContentResolver()
                .delete(EventContract.EventEntry.CONTENT_URI, null, null);
        assertTrue("Nothing have been deleted", deletedRows > 0);
    }

    public void testGetCursor() {
        ArrayList<Event> events = null;
        try {
            events = EventDbHelper.parseJsonEvents(TestUtils.getExampleJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getContext().getContentResolver()
                .insert(EventContract.EventEntry.CONTENT_URI, events.get(0).getContentValues());
        Cursor cursor = getContext().getContentResolver()
                .query(EventContract.EventEntry.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
    }
}