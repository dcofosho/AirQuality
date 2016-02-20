package com.example.daniel.airquality;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Daniel on 2/7/2016.
 */
public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    HttpResponse response;
    String responseString;
    GoogleMap map;
    Button locationBtn;
    Button aqiBtn;
    Button descriptionBtn;
    Button polBtn;
    Button childBtn;


    TextView aqiTextView;
    TextView descriptionTextView;
    TextView polTextView;
    TextView childTextView;

    HandleService service;
    Double latitude;
    Double longitude;
    String location;
    EditText editText;
    JSONObject aqi;
    JSONObject description;
    JSONObject pol;
    JSONObject recommendations;
    JSONObject locationObj;
    JSONObject locationInfo;
    String child;
    JSONObject aq;
//    EditText editText2;
    String selection;
    String result;
    SupportMapFragment fm;

    MarkerOptions marker;
    Boolean gettingAqi;
    Boolean gettingDescription;

    String snippet;
    String breezometerAqi;
    String aqDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        snippet="";
        gettingAqi=false;
        gettingDescription=false;
        fm = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map);
        map = fm.getMap();

        locationBtn=(Button) findViewById(R.id.locationBtn);
        aqiTextView=(TextView) findViewById(R.id.aqiTextView);
        aqiBtn=(Button) findViewById(R.id.aqiBtn);

        descriptionTextView=(TextView)findViewById(R.id.descriptionTextView);
        descriptionBtn=(Button) findViewById(R.id.descriptionBtn);

        polTextView=(TextView) findViewById(R.id.polTextView);
        polBtn=(Button) findViewById(R.id.polBtn);

        childTextView=(TextView) findViewById(R.id.childTextView);
        childBtn=(Button) findViewById(R.id.childBtn);


        service = new HandleService();
        editText= (EditText) findViewById(R.id.editText);
//        editText2= (EditText) findViewById(R.id.editText2);

        locationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                aqiBtn.setVisibility(View.VISIBLE);
                aqiTextView.setVisibility(View.VISIBLE);
                descriptionBtn.setVisibility(View.VISIBLE);
                descriptionTextView.setVisibility(View.VISIBLE);
                polBtn.setVisibility(View.VISIBLE);
                polTextView.setVisibility(View.VISIBLE);
                childBtn.setVisibility(View.VISIBLE);
                childTextView.setVisibility(View.VISIBLE);
                location=editText.getText().toString().replace(",","").replace(" ", "+");
                aqiTextView.setText("");
                descriptionTextView.setText("");
                polTextView.setText("");
                snippet=editText.getText().toString();
                new LatLongTask().execute();
            }
        });
        aqiBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                selection="aqi";
                location=editText.getText().toString().replace(",", "").replace(" ", "+");
//                longitude=Double.parseDouble(editText2.getText().toString());
                new MyTask().execute(location);

            }
        });

        descriptionBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                selection="description";
                location=editText.getText().toString().replace(",","").replace(" ", "+");
//                longitude=Double.parseDouble(editText2.getText().toString());
                new MyTask().execute(location);
            }
        });

        polBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                selection="pol";
                location=editText.getText().toString().replace(",","").replace(" ", "+");
//                longitude=Double.parseDouble(editText2.getText().toString());
                new MyTask().execute(location);
            }
        });

        childBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                selection="child";
                location=editText.getText().toString().replace(",","").replace(" ", "+");
//                longitude=Double.parseDouble(editText2.getText().toString());
                new MyTask().execute(location);
            }
        });

    }

    class MyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {

            Log.i("data_dan", service.getAQ(params[0]));
            result=service.getAQ(params[0]);
            return result;

        }
        @Override
        protected void onPostExecute(String result) {
            if(selection.equals("aqi")) {
                try {
                    aqi = new JSONObject(result);
                    aqiTextView.setTextColor(Color.parseColor(aqi.optString("breezometer_color")));
                    aqiTextView.setText(aqi.optString("breezometer_aqi"));
                    breezometerAqi=aqi.optString("breezometer_aqi");
                    snippet=snippet+"Air Quality Index = "+breezometerAqi+"\n";
                    gettingAqi=true;
                    onMapReady(map);
                } catch (Exception e) {
                    aqiTextView.setText("Info not available");
                    e.printStackTrace();
                }

//            textView.setText(result.replace("breezometer_aqi", "").replace(": ","").replace("}","").replace("{", ""));
            }else if(selection.equals("description")){
                try {
                    description = new JSONObject(result);
                    descriptionTextView.setTextColor(Color.parseColor(description.optString("breezometer_color")));
                    descriptionTextView.setText(description.optString("breezometer_description"));
                    aqDescription=description.optString("breezometer_description");
                    gettingDescription=true;
                    snippet=snippet+aqDescription;
                    onMapReady(map);
                } catch (Exception e) {
                    polTextView.setText("Info not available");
                    e.printStackTrace();
                }
            }else if(selection.equals("pol")){
                try {
                    pol = new JSONObject(result);
                    polTextView.setTextColor(Color.parseColor(pol.optString("breezometer_color")));
                    polTextView.setText(pol.optString("dominant_pollutant_description"));
                } catch (Exception e) {
                    polTextView.setText("Info not available");
                    e.printStackTrace();
                }
            }else if(selection.equals("child")){
                try {
                    aq = new JSONObject(result);
                    recommendations=aq.optJSONObject("random_recommendations");
                    child=recommendations.optString("children");
                    childTextView.setTextColor(Color.parseColor(aq.optString("breezometer_color")));
                    childTextView.setText(child);
                } catch (Exception e) {
                    polTextView.setText("Info not available");
                    e.printStackTrace();
                }
            }
        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
    class LatLongTask extends AsyncTask<String,Integer,JSONObject>{
        @Override
        protected JSONObject doInBackground(String...params) {
            JSONObject latLong=new JSONObject();
            try {
                latLong=getLatLong();
                Log.v("JSON_result_dan",latLong.toString());
                Log.v("JSON_result_dan2", latLong.optJSONArray("results").toString());
                JSONArray resultJSONArray = latLong.optJSONArray("results");
                JSONObject locationJSONObj = resultJSONArray.getJSONObject(0).optJSONObject("geometry").optJSONObject("location");
                latitude=locationJSONObj.optDouble("lat");
                longitude=locationJSONObj.optDouble("lng");
                onMapReady(map);
                Log.v("_dan_loc",locationJSONObj.toString());
                Log.v("_dan_lat",latitude+"");
                Log.v("_dan_lng",longitude+"");
            }catch(Exception e){
                Log.v("_dan",e.getMessage());
            }
            return latLong;
        }
        @Override
        protected void onPostExecute(JSONObject result) {

        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
    public JSONObject getLatLong() {

        try {
            URI uri = new URI("https://maps.googleapis.com/maps/api/geocode/json?address=" +location+"&key=AIzaSyAOolIF3JIZfb-1PyotIkVYIV0LXNFW7fs");
            HttpGet request = new HttpGet(uri);
            HttpClient client = new DefaultHttpClient();
            response = client.execute(request);
            HttpEntity httpEntity = response.getEntity();
            responseString = EntityUtils.toString(httpEntity);
            locationInfo = new JSONObject(responseString);
//            locationObj = locationInfo.optJSONObject("results").optJSONObject("geometry").optJSONObject("location");
//            locationObj = locationInfo.optJSONObject("results");
        }catch(Exception e){
            Log.v("data_dan_latlng", "nil");
        }
        return locationInfo;

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney and move the camera

        LatLng pos = new LatLng(latitude,longitude);

        marker = new MarkerOptions().position(pos).title(snippet);

        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        try {
            Log.v("data_dan_JSON", getLatLong()+"");
        }catch(Exception e){
            Log.v("data_dan_JSON", "nil");
        }
    }
}
