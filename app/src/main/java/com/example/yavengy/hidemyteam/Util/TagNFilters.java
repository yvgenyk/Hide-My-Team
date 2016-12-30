package com.example.yavengy.hidemyteam.Util;

import android.os.AsyncTask;
import android.util.Log;

import com.example.yavengy.hidemyteam.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Created by yavengy on 12/30/16.
 */

public class TagNFilters {

    public static int[] filterArray = {0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    final String teamTags[] = {"boston-celtics", "brooklyn-nets", "new-york-knicks", "philadelphia-76ers",
            "toronto-raptors", "golden-state-warriors", "los-angeles-clippers",
            "los-angeles-lakers", "phoenix-suns", "sacramento-kings", "chicago-bulls",
            "cleveland-cavaliers", "detroit-pistons", "indiana-pacers", "milwaukee-bucks",
            "atlanta-hawks", "charlotte-hornets", "miami-heat", "orlando-magic",
            "washington-wizards", "denver-nuggets", "minnesota-timberwolves",
            "oklahoma-city-thunder", "portland-trail-blazers", "utah-jazz",
            "dallas-mavericks", "houston-rockets", "memphis-grizzlies",
            "new-orleans-pelicans", "san-antonio-spurs"};


    private String tags[];

    public TagNFilters(String url){

        String result = null;

        try {
            result = new DownloadContext().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        if (result != null){
            tags = prepareTags(result);
        }

    }

    public class DownloadContext extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;

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

    }

    public String getTags() {
        return filterTags();
    }

    private String[] prepareTags(String result){

        JSONObject json;
        boolean error = false;
        String tempTags[] = {};
        JSONArray partJson = null;

        try {
            json = new JSONObject(result);
            partJson = new JSONArray(json.get("tags").toString());
        } catch (JSONException e) {
            error = true;
            e.printStackTrace();
        }

        if(!error || partJson != null){

            try {
                tempTags = partJson.getJSONObject(0).getJSONObject("links").getString("children").split("/");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tempTags[tempTags.length - 1].split(",");

        } else {
            return null;
        }
    }

    private String filterTags(){

        String newTags = "";

        int correctIndex = 0;

        ArrayList<String> arrList = new ArrayList<>(Arrays.asList(tags));

        String division1 = arrList.get(0);
        String division2 = arrList.get(1);
        String division3 = arrList.get(2);
        String division4 = arrList.get(3);
        String division5 = arrList.get(4);
        String division6 = arrList.get(5);

        arrList.remove(0);
        arrList.remove(0);
        arrList.remove(0);
        arrList.remove(0);
        arrList.remove(0);
        arrList.remove(0);

        for(int i = 0; i < filterArray.length; i++){

            if(i != 0 && i != 6 && i != 12 && i != 18 && i != 24 && i != 30){

                if (filterArray[i] == 1) {
                    newTags += teamTags[correctIndex] + ",";
                }
                correctIndex++;
            }
        }

        for(String tag : arrList ){
            newTags += tag + ",";
        }

        if (newTags.length() > 1) {
            newTags = newTags.substring(0, newTags.length() - 6);
        }

        return newTags;

    }

}
