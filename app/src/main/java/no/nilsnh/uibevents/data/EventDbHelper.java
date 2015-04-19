package no.nilsnh.uibevents.data;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class EventDbHelper {

    final String LOG_TAG = "UibEvents:" + EventDbHelper.class.getSimpleName();
    final String filename = "storage.data";
    private Context ctx = null;

    final String api_key = "KEYjapewe8ys";
    final String api_url = "https://eventcalendar.data.uib.no/" + api_key + "/calendar.json";

    public EventDbHelper() {
    }

    public EventDbHelper(Context context) {
        this.ctx = context;
    }

    public String fetchWebEventData() {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String eventJsonStr = null;

        try {
            Log.d(LOG_TAG, "starting fetchWebEventData");
            URL url = new URL(api_url);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return eventJsonStr;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return eventJsonStr;
            }
            eventJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return eventJsonStr;
    }

    public static ArrayList<Event> parseJsonEvents(String data) throws JSONException {
        ArrayList<Event> parsedEvents = new ArrayList<>();
        JSONArray events = new JSONObject(data).getJSONArray("events");
        for (int i = 0; i < events.length(); i++) {
            Event event = new Event(events.getJSONObject(i));
            parsedEvents.add(event);
        }
        return parsedEvents;
    }

    public void saveFile(String data) {
        FileOutputStream outputStream;
        try {
            String fullFilePath = ctx.getFilesDir() + "/" + filename;
            BufferedWriter buf = new BufferedWriter(new FileWriter(fullFilePath));
            buf.write(data);
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.d(LOG_TAG, "Data was saved!");
        }
    }

    public void saveFile(ArrayList<Event> events) {
        StringBuilder sb = new StringBuilder();
        for (Event event: events) {
            sb.append(event.toString() + "\n");
        }
        saveFile(sb.toString());
    }

    public ArrayList<Event> getStoredData() {
        File file = new File(ctx.getFilesDir() + "/" + filename);

        if(file.exists()){
            HashSet<String> eventStrings = new HashSet<>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    eventStrings.add(line);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<Event> events = new ArrayList<>();
            for (String eventString: eventStrings) {
                events.add(new Event(eventString));
            }
            return events;
        }
        else {
            Log.d(LOG_TAG, "Was not able to find file");
            return new ArrayList<Event>();
        }
    }

    public Long insert(ContentValues values) {
        Event newEvent = new Event(values);

        //First retrieve all data,then check if the value is already stored.
        ArrayList<Event> storedEvents = getStoredData();

        //If no data exist already create new arrayList
        if (storedEvents == null) storedEvents = new ArrayList<Event>();

        //If the new data is already there stop execution.
        if (storedEvents.contains(newEvent)) {
            return Long.valueOf(storedEvents.indexOf(newEvent) + 1);
        }
        storedEvents.add(newEvent);
        Long storedDataPosition = Long.valueOf(storedEvents.indexOf(newEvent) + 1);
        saveFile(storedEvents);
        return storedDataPosition;
    }

    public int delete(String tableName, String selection, String[] selectionArgs) {
        ArrayList<Event> storedEvents = getStoredData();
        ArrayList<Event> entriesToDelete = new ArrayList<>();
        Integer numDeletedRows = null;

        //If everything is to be deleted just delete the file
        if (selection == "1") {
            numDeletedRows = storedEvents.size();
            File file = new File(ctx.getFilesDir() + "/" + filename);
            file.delete();
            return numDeletedRows;
        }

        //Loop through removing any events satisfying the criteria
        for (Event event : storedEvents) {
            if(event.isToBeDeleted(selection, selectionArgs)) {
                storedEvents.remove(event);
                numDeletedRows = numDeletedRows + 1;
            }
        }

        saveFile(storedEvents);

        return numDeletedRows;
    }
}
