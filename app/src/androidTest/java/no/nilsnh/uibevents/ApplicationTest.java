package no.nilsnh.uibevents;

import android.app.Application;
import android.test.ApplicationTestCase;

import no.nilsnh.uibevents.data.EventContract;
import no.nilsnh.uibevents.data.EventDbHelper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testEventDb() throws Throwable {
        EventDbHelper db = new EventDbHelper();
        db.fetchWebEventData();
    }
}