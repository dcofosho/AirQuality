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
    Boolean gettingPol;
    Boolean gettingChild;

    StringBuilder mySnippet;

    String breezometerAqi;
    String aqDescription;
    String pollutant;


    Boolean aqiException;
    Boolean descriptionException;
    Boolean pollutantException;
    Boolean childException;

    private Location mLastLocation;
    public LocationManager mLocationManager;
    TextView experiment;
    String strAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int LOCATION_REFRESH_TIME = 1000;
        int LOCATION_REFRESH_DISTANCE = 5;
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

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

        experiment = (TextView) findViewById(R.id.experiment);

        try{
            mLastLocation=mLocationManager.getLastKnownLocation("gps");
            experiment.setText(mLastLocation.getLatitude()+"");
        }catch (Exception e){
            Log.v("_dan_Exception",e.getMessage());
        }

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
                mySnippet=new StringBuilder("");
                gettingAqi=false;
                gettingDescription=false;
                gettingPol=false;
                gettingChild=false;
                aqiException=false;
                childException=false;
                descriptionException=false;
                pollutantException=false;
                new LatLongTask().execute();
            }
        });
        aqiBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                map.clear();
                selection="aqi";
                location=editText.getText().toString().replace(",", "").replace(" ", "+");
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
                location=editText.getText().toString().replace(",","").replace(" ", "+");
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
                location=editText.getText().toString().replace(",","").replace(" ", "+");
//                longitude=Double.parseDouble(editText2.getText().toString());
                gettingChild=true;

                new MyTask().execute(location);
            }
        });

        childBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                selection = "child";
                location = editText.getText().toString().replace(",", "").replace(" ", "+");
//                longitude=Double.parseDouble(editText2.getText().toString());

                new MyTask().execute(location);
            }
        });



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
                    strAdd = getCompleteAddressString(latitude, longitude);
                    experiment.setText("Complete Address : " + strAdd);
                    break;
                }
            }

            if (map != null) {

                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(latitude, longitude)).title("Hello Maps").snippet("Discription");

                marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

// Moving Camera to a Location with animation
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude)).zoom(12).build();

                map.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

                map.addMarker(marker);

            }

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
                Log.w("My Current loction address",
                        "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //code
            System.out.println("onLocationChanged");
            mLastLocation = location;
            try{

                experiment.setText(mLastLocation.getLatitude()+"");
            }catch (Exception e){
                Log.v("_dan_Exception",e.getMessage());
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("onProviderDisabled");
            //turns off gps services
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            Log.i("data_dan", service.getAQ(params[0]));
            result=service.getAQ(params[0]);
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
                    aq = new JSONObject(result);
                    recommendations=aq.optJSONObject("random_recommendations");
                    child=recommendations.optString("children");
                } catch (Exception e) {
                    childException=true;
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
                    childTextView.setTextColor(Color.parseColor(aq.optString("breezometer_color")));
                    childTextView.setText(child);
                }catch (Exception e){
                    childException=true;
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

        googleMap.addMarker(new MarkerOptions().position(pos).title(location).snippet(mySnippet.toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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
