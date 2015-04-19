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

    public static ArrayList<ContentValues> parseJsonEvents(String data) throws JSONException {
        ArrayList<ContentValues> parsedEvents = new ArrayList<>();
        JSONArray events = new JSONObject(data).getJSONArray("events");
        for (int i = 0; i < events.length(); i++) {
            JSONObject event = events.getJSONObject(i);

            Integer id = event.getInt("id");
            String type = event.getString("type");
            String title = event.getString("title");
            String date_from = event.getString("date_from");
            String date_to = event.getString("date_to");
            String location = event.getString("location");
            String details = event.getString("lead");
            String url = event.getString("path");

            ContentValues cValues = new ContentValues();
            cValues.put(EventContract.EventEntry.COLUMN_EVENT_ID, id);
            cValues.put(EventContract.EventEntry.COLUMN_EVENT_TYPE, type);
            cValues.put(EventContract.EventEntry.COLUMN_EVENT_TITLE, title);
            cValues.put(EventContract.EventEntry.COLUMN_EVENT_DATE_FROM, date_from);
            cValues.put(EventContract.EventEntry.COLUMN_EVENT_DATE_TO, date_to);
            cValues.put(EventContract.EventEntry.COLUMN_EVENT_LOCATION, location);
            cValues.put(EventContract.EventEntry.COLUMN_EVENT_DETAILS, details);
            cValues.put(EventContract.EventEntry.COLUMN_EVENT_URL, url);
            parsedEvents.add(cValues);
        }
        return parsedEvents;
    }

    public void saveFile(String data) {
        FileOutputStream outputStream;
        try {
            outputStream = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.d(LOG_TAG, "Data was saved!");
        }
    }

    public ArrayList<ContentValues> getStoredData() {
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

            String[] eventDetailsString = null;
            ContentValues eventDetailsCValue = new ContentValues();
            ArrayList<ContentValues> events = new ArrayList<>();
            for (String event: eventStrings) {
                eventDetailsString = event.split(";");
                eventDetailsCValue.put("id", eventDetailsString[0]);
                eventDetailsCValue.put("type", eventDetailsString[1]);
                eventDetailsCValue.put("title", eventDetailsString[2]);
                eventDetailsCValue.put("date_from", eventDetailsString[3]);
                eventDetailsCValue.put("date_to", eventDetailsString[4]);
                eventDetailsCValue.put("location", eventDetailsString[5]);
                eventDetailsCValue.put("details", eventDetailsString[6]);
                eventDetailsCValue.put("url", eventDetailsString[7]);
                events.add(eventDetailsCValue);
            }
            return events;
        }
        else {
            Log.d(LOG_TAG, "Was not able to find file");
            return new ArrayList<ContentValues>();
        }
    }

    public Long insert(ContentValues values) {
        File file = new File(ctx.getFilesDir(),filename);

        //First retrieve all data,then check if the value is already stored.
        ArrayList<ContentValues> storedValues = getStoredData();

        //If no data exist already create new arrayList
        if (storedValues == null) storedValues = new ArrayList<>();

        //If the new data is already there stop execution.
        if (storedValues.contains(values)) return null;

        storedValues.add(values);
        Integer storedDataPosition = storedValues.indexOf(values) + 1;

        //Write new data to textFile
        try {

            if (!file.exists()) file.createNewFile();

            BufferedWriter buf = new BufferedWriter(new FileWriter(ctx.getFilesDir() + "/" + filename, true));
            for (ContentValues event : storedValues) {
                buf.write(event.getAsString("id") + ";");
                buf.write(event.getAsString("type") + ";");
                buf.write(event.getAsString("title") + ";");
                buf.write(event.getAsString("date_from") + ";");
                buf.write(event.getAsString("date_to") + ";");
                buf.write(event.getAsString("location") + ";");
                buf.write(event.getAsString("details") + ";");
                buf.write(event.getAsString("url"));
                buf.newLine();
            }
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Long.valueOf(storedDataPosition);
    }
}
