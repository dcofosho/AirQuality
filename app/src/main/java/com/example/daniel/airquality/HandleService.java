package com.example.daniel.airquality;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;



/**
 * Created by Daniel on 2/7/2016.
 */
public class HandleService {
    HttpResponse response;
    String responseString;
    JSONObject aqi;
    public String getAQ(String location) {
        try{
            URI uri = new URI("http://api.breezometer.com/baqi/?location="+location+"&key=156744bdb0b44019b4e4a4d3e022bcca");
            HttpGet request = new HttpGet(uri);
            HttpClient client = new DefaultHttpClient();
            response = client.execute(request);
            HttpEntity httpEntity = response.getEntity();
            responseString = EntityUtils.toString(httpEntity);
        }catch (Exception e){
            Log.v("exception_dan", e.getMessage());
        }

        return responseString;

    }
    public String getAQI(String location) {
        try{
            URI uri = new URI("http://api.breezometer.com/baqi/?location="+location+"&fields=breezometer_aqi&key=156744bdb0b44019b4e4a4d3e022bcca");
            HttpGet request = new HttpGet(uri);
            HttpClient client = new DefaultHttpClient();
            response = client.execute(request);
            HttpEntity httpEntity = response.getEntity();
            responseString = EntityUtils.toString(httpEntity);
        }catch (Exception e){
            Log.v("exception_dan", e.getMessage());
        }

        return responseString;

    }

}

