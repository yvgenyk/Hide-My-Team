package com.example.yavengy.hidemyteam.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.yavengy.hidemyteam.R;
import com.example.yavengy.hidemyteam.activity.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static com.example.yavengy.hidemyteam.Util.DbBitmapUtility.getBytes;
import static com.example.yavengy.hidemyteam.activity.HomeFragment.currentMaxIndex;
import static com.example.yavengy.hidemyteam.activity.MainActivity.mainContext;

/**
 * Created by yavengy on 1/30/17.
 */

public class BrContentDownload {

    private TagNFilters tagsNFilters;
    private DataBase myDataBaseClass;

    public BrContentDownload(){
        tagsNFilters = new TagNFilters(mainContext.getString(R.string.tags_api_call));
        myDataBaseClass = new DataBase();
    }


    public void getArticles() {

        String filteredTags = tagsNFilters.getTags();;

        String url = "http://bleacherreport.com/api/front/lead_articles.json?tags=" +
                filteredTags + "&appversion=1.4&perpage=40";

        DownloadContext task = new DownloadContext();

        task.execute(url);

    }

    public void saveToDb (String result){

        String title;

        if (result != null) {

            String allArticles = myDataBaseClass.getAllPermalinks();

            try {

                JSONArray arr = new JSONArray(result);

                for (int i = 0; i < arr.length(); i++) {


                    if (!arr.get(i).toString().equals("null")) {
                        if (arr.getJSONObject(i).has("tag")) {

                            title = arr.getJSONObject(i).getString("title");

                            title = title.replaceAll(":", " -");
                            title = title.replaceAll("'", "");

                            if (!allArticles.contains(arr.getJSONObject(i).getString("permalink"))) {

                                myDataBaseClass.saveArticle(title,
                                        getBytes(new DownloadImage().execute(arr.getJSONObject(i).getString("primary_image_650x440")).get()),
                                        arr.getJSONObject(i).getString("permalink"));
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public class DownloadContext extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            saveToDb(result);
        }

    }
}
