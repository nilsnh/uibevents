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
        ArrayList<Event> events = eventDbHelper.parseJsonEvents(eventDbHelper.fetchWebEventData());
        assertTrue(events.size() > 150);
    }

    public void testSaveFileWithApiData() throws Throwable {
        ArrayList<Event> events = eventDbHelper.parseJsonEvents(eventDbHelper.fetchWebEventData());
        eventDbHelper.saveFile(events);
        ArrayList storedEvents = eventDbHelper.getStoredData();
        assertTrue(storedEvents.size() > (events.size() / 2));
    }

    public void testParseJsonEvent() {
        ArrayList<Event> events = null;
        events = EventDbHelper.parseJsonEvents(TestUtils.getExampleJson());
        assertTrue(events.size() == 2);
    }

    public void testInsertIntoTextFile() {
        ArrayList<Event> events = null;
        events = EventDbHelper.parseJsonEvents(TestUtils.getExampleJson());
        getContext().getContentResolver()
                .insert(EventContract.EventEntry.CONTENT_URI, events.get(0).getContentValues());
        assertTrue(eventDbHelper.getStoredData().contains(events.get(0)));
    }

    public void testDeleteAll() {
        ArrayList<Event> events = null;
        events = EventDbHelper.parseJsonEvents(TestUtils.getExampleJson());
        getContext().getContentResolver()
                .insert(EventContract.EventEntry.CONTENT_URI, events.get(0).getContentValues());
        assertTrue(eventDbHelper.getStoredData().contains(events.get(0)));
        Integer deletedRows = getContext().getContentResolver()
                .delete(EventContract.EventEntry.CONTENT_URI, null, null);
        assertTrue("Nothing have been deleted", deletedRows > 0);
    }

    public void testGetCursor() {
        ArrayList<Event> events = null;
        events = EventDbHelper.parseJsonEvents(TestUtils.getExampleJson());
        getContext().getContentResolver()
                .insert(EventContract.EventEntry.CONTENT_URI, events.get(0).getContentValues());
        Cursor cursor = getContext().getContentResolver()
                .query(EventContract.EventEntry.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
    }
}