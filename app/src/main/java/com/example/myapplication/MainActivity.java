package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    // Will contain the raw JSON response as a string.
    String data = null;
    ProgressBar progressBar;
    TextView tvName1,tvName2,tvName3,tvId1,tvId2,tvId3;
    ImageView img1,img2,img3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        new JSONRequest().execute();
    }

    private void findViews() {
        tvName1=findViewById(R.id.textViewName1);
        tvName2=findViewById(R.id.textViewName2);
        tvName3=findViewById(R.id.textViewName3);
        tvId1=findViewById(R.id.textViewStudentId1);
        tvId2=findViewById(R.id.textViewStudentId2);
        tvId3=findViewById(R.id.textViewStudentId3);
        img1=findViewById(R.id.ImageView1);
        img2=findViewById(R.id.ImageView2);
        img3=findViewById(R.id.ImageView3);
        progressBar=findViewById(R.id.progressBar);
    }

    private class JSONRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("https://api.myjson.com/bins/zjv68");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    data = null;
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
                    data = null;
                }
                data = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                data = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            //Read JSON
            try {
                JSONObject returnData=new JSONObject(data);
                JSONArray studentsArray=returnData.getJSONArray("students");

                JSONObject student1=studentsArray.getJSONObject(0);
                String student1Name=student1.getString("studentName");
                String student1Id=student1.getString("studentID");
                String student1Photo=student1.getString("studentPhoto");
                tvName1.setText(student1Name);
                tvId1.setText(student1Id);
                Picasso.get().load(student1Photo).into(img1);

                JSONObject student2=studentsArray.getJSONObject(1);
                String student2Name=student2.getString("studentName");
                String student2Id=student2.getString("studentID");
                String student2Photo=student2.getString("studentPhoto");
                tvName2.setText(student2Name);
                tvId2.setText(student2Id);
                Picasso.get().load(student2Photo).into(img2);

                JSONObject student3=studentsArray.getJSONObject(2);
                String student3Name=student3.getString("studentName");
                String student3Id=student3.getString("studentID");
                String student3Photo=student3.getString("studentPhoto");
                tvName3.setText(student3Name);
                tvId3.setText(student3Id);
                Picasso.get().load(student3Photo).into(img3);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
