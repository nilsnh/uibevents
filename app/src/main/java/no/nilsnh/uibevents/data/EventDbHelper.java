package no.nilsnh.uibevents.data;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;

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

    public void fetchWebEventData() {
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
                return;
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
                return;
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
        return;
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

    public String getStoredData() {
        File file = new File(filename);

        if(file.exists()){
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('n');
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return text.toString();
        }
        else {
            return "Was not able to find file";
        }
    }

    public Long insert(ContentValues values) {

        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(filename, true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
