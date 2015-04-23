package no.nilsnh.uibevents.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    public static ArrayList<Event> parseJsonEvents(String data) {
        ArrayList<Event> parsedEvents = new ArrayList<>();
        JSONArray events;
        try {
            events = new JSONObject(data).getJSONArray("events");
            for (int i = 0; i < events.length(); i++) {
                Event event = new Event(events.getJSONObject(i));
                parsedEvents.add(event);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parsedEvents;
    }

    public void saveFile(String data) {
        try {
            File file = new File(ctx.getFilesDir(), filename);
            if (file.exists()) file.delete(); //TODO Only delete events older than X date.

            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            try {
                outputStream.write(data.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
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
        File file = new File(ctx.getFilesDir(), filename);

        if(file.exists()){
//            HashSet<String> eventStrings = new HashSet<>();
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    if(!line.isEmpty()) sb.append(line);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<Event> events = parseJsonEvents(sb.toString());
            Log.d(LOG_TAG, "Finished reading stored data");
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
        Integer numDeletedRows = null;

        //If everything is to be deleted just delete the file
        if (selection == null) {
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

    public MatrixCursor query() {
        ArrayList<Event> storedEvents = getStoredData();
        MatrixCursor mCursor = new MatrixCursor(Event.getKeysAsStringList());
        Event event;
        for (int i = 0; i < storedEvents.size() -1; i++) {
            event = storedEvents.get(i);
            mCursor.addRow(new String[]{
                    String.valueOf(i + 1),
                    event.getId(),
                    event.getType(),
                    event.getTitle(),
                    event.getDateFrom(),
                    event.getDateTo(),
                    event.getLocation(),
                    event.getDetails(),
                    event.getUrl()
            });
        }
        return mCursor;
    }

    public MatrixCursor query(Uri uri) {
        ArrayList<Event> storedEvents = getStoredData();
        MatrixCursor mCursor = new MatrixCursor(Event.getKeysAsStringList());
        Event event;
        String[] values;
        for (int i = 0; i < storedEvents.size() -1; i++) {
            event = storedEvents.get(i);
            values = new String[]{
                    String.valueOf(i + 1),
                    event.getId(),
                    event.getType(),
                    event.getTitle(),
                    event.getDateFrom(),
                    event.getDateTo(),
                    event.getLocation(),
                    event.getDetails(),
                    event.getUrl()
            };
            if (event.getId().equals(uri.getLastPathSegment()))
                mCursor.addRow(values);
        }
        return mCursor;
    }

    public String getEventIdFromUri(Uri uri){
        return uri.getLastPathSegment();
    }

    //This is done in order to have some data on the first run.
    public void initializeDataFile() {
        File file = new File(ctx.getFilesDir(), filename);
        if (!file.exists()) saveFile(getInitialData());
    }

    private String getInitialData() {
        return "{\n" +
                "  \"from_date\": \"2015-04-16T16:40:41+02:00\",\n" +
                "  \"events\": [\n" +
                "    {\n" +
                "      \"type\": \"exhibition\",\n" +
                "      \"title\": \" The Norwegian Constitution \\\"VI: 1814 - 2014\\\"\",\n" +
                "      \"date_from\": \"2014-05-11 22:00:00Z\",\n" +
                "      \"date_to\": \"2016-12-31 15:00:00Z\",\n" +
                "      \"location\": null,\n" +
                "      \"lead\": null,\n" +
                "      \"id\": 70450,\n" +
                "      \"path\": \"universitymuseum/70450/norwegian-constitution-vi-1814-2014\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"exhibition\",\n" +
                "      \"title\": \"Grunnlovsutstillingen \\\"Vi 1814-2014\\\"\",\n" +
                "      \"date_from\": \"2014-05-12 10:00:00Z\",\n" +
                "      \"date_to\": \"2016-12-31 11:00:00Z\",\n" +
                "      \"location\": \"De kulturhistoriske samlinger, Haakon Sheteligs plass 10\",\n" +
                "      \"lead\": \"En annerledes og nyskapende utstilling om demokrati for ungdom og voksne. \",\n" +
                "      \"id\": 48468,\n" +
                "      \"path\": \"grunnlovsjubileet2014/48468/grunnlovsutstillingen-vi-1814-2014\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"exhibition\",\n" +
                "      \"title\": \"Runer i utstillingene\",\n" +
                "      \"date_from\": \"2014-08-27 22:00:00Z\",\n" +
                "      \"date_to\": \"2016-09-28 22:00:00Z\",\n" +
                "      \"location\": \"De kulturhistoriske samlinger\",\n" +
                "      \"lead\": \"Universitetsmuseets samling med runer er stor. Innskrifter med runer finner man p\\u00e5 v\\u00e5pen, redskaper, smykker, kammer, gullbrakteater og amuletter. I Norden og England er de ogs\\u00e5 funnet p\\u00e5 minnesteiner. I Norge finner vi runer i senere tid ogs\\u00e5 i stavkirkene.\",\n" +
                "      \"id\": 80443,\n" +
                "      \"path\": \"universitetsmuseet/80443/runer-i-utstillingene\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"exhibition\",\n" +
                "      \"title\": \"Studio for eksperimenter og kunnskap\",\n" +
                "      \"date_from\": \"2014-08-28 08:00:00Z\",\n" +
                "      \"date_to\": \"2016-12-31 14:00:00Z\",\n" +
                "      \"location\": \"De kulturhistoriske samlinger\",\n" +
                "      \"lead\": \"STUDIO - utstilling, aktivitetsrom og et rom for l\\u00e6ring\",\n" +
                "      \"id\": 80417,\n" +
                "      \"path\": \"universitetsmuseet/80417/studio-eksperimenter-og-kunnskap\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"exhibition\",\n" +
                "      \"title\": \"Inntrykk fra koloniene\",\n" +
                "      \"date_from\": \"2014-08-28 08:00:00Z\",\n" +
                "      \"date_to\": \"2016-12-31 14:00:00Z\",\n" +
                "      \"location\": \"De kulturhistoriske samlinger, Haakon Sheteligsplass 10\",\n" +
                "      \"lead\": \"I denne utstillingen presenterer vi noen av de fantastiske gjenstandene som kom til museet i l\\u00f8pet av kolonitiden. Samlingen gir et inntrykk av det mangfoldet av eksotiske steder hvor nordmenn og bergensere oppholdt seg p\\u00e5 slutten av det 19. og i begynnelsen av det 20. \\u00e5rhundret.\",\n" +
                "      \"id\": 80413,\n" +
                "      \"path\": \"universitetsmuseet/80413/inntrykk-fra-koloniene\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"exhibition\",\n" +
                "      \"title\": \"Ludvig Holberg som faglitter\\u00e6r forfattar\",\n" +
                "      \"date_from\": \"2015-03-09 07:30:00Z\",\n" +
                "      \"date_to\": \"2015-06-05 17:00:00Z\",\n" +
                "      \"location\": \"Bibliotek for humaniora, Haakon Sheteligs plass 7.\",\n" +
                "      \"lead\": \"Kven f\\u00e5r Holbergprisen i 2015? Det blir kunngjort 12. mars. Tre dagar f\\u00f8r opnar Universitetsbiblioteket utstillinga \\\"Holberg som faglitter\\u00e6r forfattar\\\". Vi vil markere at Holberg f\\u00f8rst og fremst var eit universitetsmenneske: professor og vitskapsmann som publiserte n\\u00e6rmare 16 000 sider med faglitteratur.\",\n" +
                "      \"id\": 86659,\n" +
                "      \"path\": \"ub/86659/ludvig-holberg-som-faglitter\\u00e6r-forfattar\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"course_language\",\n" +
                "      \"title\": \"Nynorsk for framandspr\\u00e5klege\",\n" +
                "      \"date_from\": \"2015-04-08 10:15:00Z\",\n" +
                "      \"date_to\": \"2015-04-22 14:00:00Z\",\n" +
                "      \"location\": null,\n" +
                "      \"lead\": \"Personalavdelinga tilbyr i samarbeid med Institutt for lingvistiske, litter\\u00e6re og estetiske studiar (LLE) eit innf\\u00f8ringskurs i nynorsk for dei som ikkje har norsk som morsm\\u00e5l.\",\n" +
                "      \"id\": 85633,\n" +
                "      \"path\": \"sim/85633/nynorsk-framandspr\\u00e5klege\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"course_language\",\n" +
                "      \"title\": \"Nynorsk for framandspr\\u00e5klege\",\n" +
                "      \"date_from\": \"2015-04-08 10:15:00Z\",\n" +
                "      \"date_to\": \"2015-04-22 14:00:00Z\",\n" +
                "      \"location\": null,\n" +
                "      \"lead\": \"Personalavdelinga tilbyr i samarbeid med Institutt for lingvistiske, litter\\u00e6re og estetiske studiar (LLE) eit innf\\u00f8ringskurs i nynorsk for dei som ikkje har norsk som morsm\\u00e5l.\",\n" +
                "      \"id\": 87022,\n" +
                "      \"path\": \"sim/87022/nynorsk-framandspr\\u00e5klege\"\n" +
                "    }]\n" +
                "}";
    }

}
