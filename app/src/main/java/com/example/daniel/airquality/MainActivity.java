package com.example.daniel.airquality;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Daniel on 2/7/2016.
 */
public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback{

    HttpResponse response;
    String responseString;
    private GoogleMap map;
    Button locationBtn;
    Button aqiBtn;
    Button descriptionBtn;
    Button polBtn;
    Button childBtn;
    Button sportBtn;
    Button healthBtn;
    Button indoorBtn;
    Button outdoorBtn;
    Button effectsBtn;
    Button causesBtn;


    TextView aqiTextView;
    TextView descriptionTextView;
    TextView polTextView;
    TextView childTextView;
    TextView sportTextView;
    TextView healthTextView;
    TextView indoorTextView;
    TextView outdoorTextView;
    TextView effectsTextView;
    TextView causesTextView;



    HandleService service;
    Double latitude;
    Double longitude;
    String location;
    EditText editText;
    JSONObject aqi;
    JSONObject description;
    JSONObject pol;
    JSONObject recommendations;
    JSONObject jsonChild;
    JSONObject jsonsport;
    JSONObject jsonhealth;
    JSONObject jsonindoors;
    JSONObject jsonoutdoors;
    JSONObject jsoneffects;
    JSONObject jsoncauses;

    JSONObject locationObj;
    JSONObject locationInfo;

    JSONObject aq;
//    EditText editText2;
    String selection;
    String result;
    SupportMapFragment fm;

    MarkerOptions marker;
    Boolean gettingAqi;
    Boolean gettingDescription;
    Boolean gettingPol;
    Boolean gettingChild;
    Boolean gettingSport;
    Boolean gettingHealth;
    Boolean gettingIndoors;
    Boolean gettingOutdoors;
    Boolean gettingEffects;
    Boolean gettingCauses;

    StringBuilder mySnippet;

    String breezometerAqi;
    String aqDescription;
    String pollutant;
    String child;
    String sport;
    String health;
    String indoors;
    String outdoors;
    String effects;
    String causes;


    Boolean aqiException;
    Boolean descriptionException;
    Boolean pollutantException;
    Boolean childException;
    Boolean sportException;
    Boolean healthException;
    Boolean indoorsException;
    Boolean outdoorsException;
    Boolean effectsException;
    Boolean causesException;

    private Location mLastLocation;
    public LocationManager mLocationManager;
    TextView gpsTextView;
    String strAdd;
    Boolean usingGps;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude=0.0;
        longitude=0.0;
        int LOCATION_REFRESH_TIME = 1000;
        int LOCATION_REFRESH_DISTANCE = 5;
//        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
//                LOCATION_REFRESH_DISTANCE, mLocationListener);

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

        sportTextView=(TextView) findViewById(R.id.sportTextView);
        sportBtn=(Button) findViewById(R.id.sportBtn);

        healthTextView=(TextView) findViewById(R.id.healthTextView);
        healthBtn=(Button) findViewById(R.id.healthBtn);

        indoorTextView=(TextView) findViewById(R.id.indoorTextView);
        indoorBtn=(Button) findViewById(R.id.indoorBtn);

        outdoorTextView=(TextView) findViewById(R.id.outdoorTextView);
        outdoorBtn=(Button) findViewById(R.id.outdoorBtn);

        effectsTextView=(TextView) findViewById(R.id.effectsTextView);
        effectsBtn=(Button) findViewById(R.id.effectsBtn);

        causesTextView=(TextView) findViewById(R.id.causesTextView);
        causesBtn=(Button) findViewById(R.id.causesBtn);

        gpsTextView = (TextView) findViewById(R.id.gpsTextView);


        service = new HandleService();
        editText= (EditText) findViewById(R.id.editText);
//        editText2= (EditText) findViewById(R.id.editText2);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location l = null;

        for (int i = 0; i < providers.size(); i++) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) {
                latitude = l.getLatitude();
                longitude = l.getLongitude();
                usingGps=true;

                strAdd = getCompleteAddressString(latitude, longitude);
                location=strAdd.replace(",","").replace(" ", "+");
                if(!(strAdd=="")) {
                    gpsTextView.setText("Got GPS Location : " + strAdd);
                }
                Log.v("_dan_location",location);
                break;
            }
        }

        if (map != null) {

//            MarkerOptions marker = new MarkerOptions().position(
//                    new LatLng(latitude, longitude)).title("Hello Maps").snippet("Discription");
//
//            marker.icon(BitmapDescriptorFactory
//                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//
//// Moving Camera to a Location with animation
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(latitude, longitude)).zoom(12).build();
//
//            map.animateCamera(CameraUpdateFactory
//                    .newCameraPosition(cameraPosition));
//
//            map.addMarker(marker);

            aqiBtn.setVisibility(View.VISIBLE);
            aqiTextView.setVisibility(View.VISIBLE);
            descriptionBtn.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.VISIBLE);
            polBtn.setVisibility(View.VISIBLE);
            polTextView.setVisibility(View.VISIBLE);
            childBtn.setVisibility(View.VISIBLE);
            childTextView.setVisibility(View.VISIBLE);
            sportBtn.setVisibility(View.VISIBLE);
            sportTextView.setVisibility(View.VISIBLE);
            healthBtn.setVisibility(View.VISIBLE);
            healthTextView.setVisibility(View.VISIBLE);
            indoorBtn.setVisibility(View.VISIBLE);
            indoorTextView.setVisibility(View.VISIBLE);
            outdoorBtn.setVisibility(View.VISIBLE);
            outdoorTextView.setVisibility(View.VISIBLE);
            effectsBtn.setVisibility(View.VISIBLE);
            effectsTextView.setVisibility(View.VISIBLE);
            causesBtn.setVisibility(View.VISIBLE);
            causesTextView.setVisibility(View.VISIBLE);

            aqiTextView.setText("");
            descriptionTextView.setText("");
            polTextView.setText("");
            childTextView.setText("");
            sportTextView.setText("");
            healthTextView.setText("");
            indoorTextView.setText("");
            outdoorTextView.setText("");
            effectsTextView.setText("");
            causesTextView.setText("");

            mySnippet=new StringBuilder("");
            gettingAqi=false;
            gettingDescription=false;
            gettingPol=false;
            gettingChild=false;
            gettingSport=false;
            gettingHealth=false;
            gettingIndoors=false;
            gettingOutdoors=false;
            gettingEffects=false;
            gettingCauses=false;

            aqiException=false;
            childException=false;
            descriptionException=false;
            pollutantException=false;
            sportException=false;
            healthException=false;
            indoorsException=false;
            outdoorsException=false;
            effectsException=false;
            causesException=false;

            onMapReady(map);
        }

        Log.v("_dan_strAdd",location);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usingGps = false;
                aqiBtn.setVisibility(View.VISIBLE);
                aqiTextView.setVisibility(View.VISIBLE);
                descriptionBtn.setVisibility(View.VISIBLE);
                descriptionTextView.setVisibility(View.VISIBLE);
                polBtn.setVisibility(View.VISIBLE);
                polTextView.setVisibility(View.VISIBLE);
                childBtn.setVisibility(View.VISIBLE);
                childTextView.setVisibility(View.VISIBLE);
                location = editText.getText().toString().replace(",", "").replace(" ", "+");
                aqiTextView.setText("");
                descriptionTextView.setText("");
                polTextView.setText("");
                mySnippet = new StringBuilder("");

                gettingAqi = false;
                gettingDescription = false;
                gettingPol = false;
                gettingChild = false;
                gettingSport = false;
                gettingHealth = false;
                gettingIndoors = false;
                gettingOutdoors = false;
                gettingEffects = false;
                gettingCauses = false;

                aqiException = false;
                childException = false;
                descriptionException = false;
                pollutantException = false;
                sportException = false;
                healthException = false;
                indoorsException = false;
                outdoorsException = false;
                effectsException = false;
                causesException = false;

                new LatLongTask().execute();
            }
        });
        aqiBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                map.clear();
                selection="aqi";
//                longitude=Double.parseDouble(editText2.getText().toString());
                gettingAqi=true;

                new MyTask().execute(location);
            }
        });

        descriptionBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                map.clear();
                selection="description";
//                longitude=Double.parseDouble(editText2.getText().toString());
                gettingDescription=true;

                new MyTask().execute(location);
            }
        });

        polBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                map.clear();
                selection="pol";
//                longitude=Double.parseDouble(editText2.getText().toString());
                gettingPol=true;

                new MyTask().execute(location);
            }
        });

        childBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                selection = "child";
                gettingChild=true;
//                longitude=Double.parseDouble(editText2.getText().toString());

                new MyTask().execute(location);
            }
        });

        sportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                selection = "sport";
                gettingSport=true;
//                longitude=Double.parseDouble(editText2.getText().toString());

                new MyTask().execute(location);
            }
        });

        healthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                selection = "health";
                gettingHealth= true;
//                longitude=Double.parseDouble(editText2.getText().toString());

                new MyTask().execute(location);
            }
        });

        indoorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                selection = "indoors";
                gettingIndoors=true;
//                longitude=Double.parseDouble(editText2.getText().toString());

                new MyTask().execute(location);
            }
        });

        outdoorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                selection = "outdoors";
                gettingOutdoors=true;
//                longitude=Double.parseDouble(editText2.getText().toString());

                new MyTask().execute(location);
            }
        });

        effectsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                selection = "effects";
                gettingEffects=true;
//                longitude=Double.parseDouble(editText2.getText().toString());

                new MyTask().execute(location);
            }
        });

        causesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                selection = "causes";
                gettingCauses=true;
//                longitude=Double.parseDouble(editText2.getText().toString());

                new MyTask().execute(location);
            }
        });




    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder
                    .getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                android.location.Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(i)).append(
                            "\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current address",
                        "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    class MyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i("data_dan", service.getAQ(params[0], usingGps, latitude, longitude));
            result=service.getAQ(params[0], usingGps, latitude,longitude);
            if(selection.equals("aqi")) {
                try {
                    aqi = new JSONObject(result);
                    breezometerAqi=aqi.optString("breezometer_aqi");

                    gettingAqi=true;
                } catch (Exception e) {
                    aqiException=true;
                    e.printStackTrace();
                }

//            textView.setText(result.replace("breezometer_aqi", "").replace(": ","").replace("}","").replace("{", ""));
            }else if(selection.equals("description")){
                try {
                    description = new JSONObject(result);
                    aqDescription=description.optString("breezometer_description");
                    gettingDescription=true;
                } catch (Exception e) {
                    descriptionException=true;
                    e.printStackTrace();
                }
            }else if(selection.equals("pol")){
                try {
                    pol = new JSONObject(result);
                    pollutant=pol.optString("dominant_pollutant_description");

                } catch (Exception e) {
                    pollutantException=true;
                    e.printStackTrace();
                }
            }else if(selection.equals("child")){
                try {
                    jsonChild = new JSONObject(result);
                    recommendations=jsonChild.optJSONObject("random_recommendations");
                    child=recommendations.optString("children");
                } catch (Exception e) {
                    childException=true;
                    e.printStackTrace();
                }
            }else if(selection.equals("sport")){
                try {
                    jsonsport = new JSONObject(result);
                    recommendations=jsonsport.optJSONObject("random_recommendations");
                    sport=recommendations.optString("sport");
                } catch (Exception e) {
                    sportException=true;
                    e.printStackTrace();
                }
            }else if(selection.equals("health")){
                try {
                    jsonhealth = new JSONObject(result);
                    recommendations=jsonhealth.optJSONObject("random_recommendations");
                    health=recommendations.optString("health");
                } catch (Exception e) {
                    healthException=true;
                    e.printStackTrace();
                }
            }else if(selection.equals("indoors")){
                try {
                    jsonindoors = new JSONObject(result);
                    recommendations=jsonindoors.optJSONObject("random_recommendations");
                    indoors=recommendations.optString("inside");
                } catch (Exception e) {
                    indoorsException=true;
                    e.printStackTrace();
                }
            }else if(selection.equals("outdoors")){
                try {
                    jsonoutdoors = new JSONObject(result);
                    recommendations=jsonoutdoors.optJSONObject("random_recommendations");
                    outdoors=recommendations.optString("outside");
                } catch (Exception e) {
                    outdoorsException=true;
                    e.printStackTrace();
                }
            }else if(selection.equals("effects")){
                try {
                    jsoneffects = new JSONObject(result);
                    recommendations=jsoneffects.optJSONObject("dominant_pollutant_text");
                    effects=recommendations.optString("effects");
                } catch (Exception e) {
                    sportException=true;
                    e.printStackTrace();
                }
            }else if(selection.equals("causes")){
                try {
                    jsoncauses = new JSONObject(result);
                    recommendations=jsoncauses.optJSONObject("dominant_pollutant_text");
                    causes=recommendations.optString("causes");
                } catch (Exception e) {
                    causesException=true;
                    e.printStackTrace();
                }
            }
            return result;

        }
        @Override
        protected void onPostExecute(String result) {
            if(gettingAqi) {
                try {
                    if(!mySnippet.toString().contains(breezometerAqi)) {
                        mySnippet.append(System.getProperty("line.separator") + "Air Quality Index = " + breezometerAqi);
                    }
                    aqiTextView.setTextColor(Color.parseColor(aqi.optString("breezometer_color")));
                    aqiTextView.setText(aqi.optString("breezometer_aqi"));
                }catch(Exception e){
                    aqiException=true;
                }
            }
            if(gettingDescription) {
                try {
                    if(!mySnippet.toString().contains(aqDescription)) {
                        mySnippet.append(System.getProperty("line.separator") + aqDescription);
                    }
                    descriptionTextView.setTextColor(Color.parseColor(description.optString("breezometer_color")));
                    descriptionTextView.setText(description.optString("breezometer_description"));
                }catch(Exception e){
                    descriptionException=true;
                }
            }
            if(gettingPol) {
                try {
                    if(!mySnippet.toString().contains(pollutant)) {
                        mySnippet.append(System.getProperty("line.separator") + "Main pollutant = "+pollutant);
                    }
                    polTextView.setTextColor(Color.parseColor(pol.optString("breezometer_color")));
                    polTextView.setText(pol.optString("dominant_pollutant_description"));
                }catch (Exception e){
                    pollutantException=true;
                }
            }
            if(gettingChild) {
                try {
                    if(!mySnippet.toString().contains(child)) {
                        mySnippet.append(System.getProperty("line.separator") +"Recommendations for children: " + System.getProperty("line.separator")+child);
                    }
                    childTextView.setTextColor(Color.parseColor(jsonChild.optString("breezometer_color")));
                    childTextView.setText(child);
                }catch (Exception e){
                    childException=true;
                }
            }
            if(gettingSport) {
                try {
                    if(!mySnippet.toString().contains(sport)) {
                        mySnippet.append(System.getProperty("line.separator") +"Recommendations for children: " + System.getProperty("line.separator")+sport);
                    }
                    sportTextView.setTextColor(Color.parseColor(jsonsport.optString("breezometer_color")));
                    sportTextView.setText(sport);
                }catch (Exception e){
                    sportException=true;
                }
            }
            if(gettingHealth) {
                try {
                    if(!mySnippet.toString().contains(health)) {
                        mySnippet.append(System.getProperty("line.separator") +"Recommendations for health: " + System.getProperty("line.separator")+health);
                    }
                    healthTextView.setTextColor(Color.parseColor(jsonhealth.optString("breezometer_color")));
                    healthTextView.setText(health);
                }catch (Exception e){
                    healthException=true;
                }
            }
            if(gettingIndoors) {
                try {
                    if(!mySnippet.toString().contains(indoors)) {
                        mySnippet.append(System.getProperty("line.separator") +"Recommendations for indoors: " + System.getProperty("line.separator")+indoors);
                    }
                    indoorTextView.setTextColor(Color.parseColor(jsonindoors.optString("breezometer_color")));
                    indoorTextView.setText(indoors);
                }catch (Exception e){
                    indoorsException=true;
                }
            }
            if(gettingOutdoors) {
                try {
                    if(!mySnippet.toString().contains(outdoors)) {
                        mySnippet.append(System.getProperty("line.separator") +"Recommendations for outdoors: " + System.getProperty("line.separator")+outdoors);
                    }
                    outdoorTextView.setTextColor(Color.parseColor(jsonoutdoors.optString("breezometer_color")));
                    outdoorTextView.setText(outdoors);
                }catch (Exception e){
                    outdoorsException=true;
                }
            }
            if(gettingEffects) {
                try {
                    if(!mySnippet.toString().contains(effects)) {
                        mySnippet.append(System.getProperty("line.separator") +"Recommendations for children: " + System.getProperty("line.separator")+effects);
                    }
                    effectsTextView.setTextColor(Color.parseColor(jsoneffects.optString("breezometer_color")));
                    effectsTextView.setText(effects);
                }catch (Exception e){
                    effectsException=true;
                }
            }if(gettingCauses) {
                try {
                    if (!mySnippet.toString().contains(causes)) {
                        mySnippet.append(System.getProperty("line.separator") + "Recommendations for children: " + System.getProperty("line.separator") + causes);
                    }
                    causesTextView.setTextColor(Color.parseColor(jsoncauses.optString("breezometer_color")));
                    causesTextView.setText(causes);
                } catch (Exception e) {
                    causesException = true;
                }
            }


            if(aqiException){
                aqiTextView.setText("Info not available");
            }
            if(descriptionException){
                descriptionTextView.setText("Info not available");
            }
            if(pollutantException){
                polTextView.setText("Info not available");
            }
            if(childException){
                childTextView.setText("Info not available");
            }
            if(sportException){
                sportTextView.setText("Info not available");
            }
            if(healthException){
                healthTextView.setText("Info not available");
            }
            if(indoorsException){
                indoorTextView.setText("Info not available");
            }
            if(outdoorsException){
                outdoorTextView.setText("Info not available");
            }
            if(effectsException){
                effectsTextView.setText("Info not available");
            }
            if(causesException){
                causesTextView.setText("Info not available");
            }
            onMapReady(map);
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

            onMapReady(map);
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

        googleMap.addMarker(new MarkerOptions().position(pos).title(location.replace("+"," ")).snippet(mySnippet.toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplication());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getBaseContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        try {
            Log.v("data_dan_JSON", getLatLong()+"");
        }catch(Exception e){
            Log.v("data_dan_JSON", "nil");
        }
    }
}
