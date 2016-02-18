package com.example.daniel.airquality;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Daniel on 2/7/2016.
 */
public class MainActivity extends Activity {
    LocationManager manager;
    Button aqiBtn;
    TextView aqiTextView;
    HandleService service;
//    Double latitude;
//    Double longitude;
    String location;
    EditText editText;
    JSONObject aqi;
//    EditText editText2;
    String selection;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aqiTextView=(TextView) findViewById(R.id.aqiTextView);
        aqiBtn=(Button) findViewById(R.id.aqiBtn);
        service = new HandleService();
        editText= (EditText) findViewById(R.id.editText);
//        editText2= (EditText) findViewById(R.id.editText2);

        aqiBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                selection="aqi";
                location=editText.getText().toString().replace(",","").replace(" ", "+");
//                longitude=Double.parseDouble(editText2.getText().toString());
                new MyTask().execute(location);
            }
        });
    }
    class MyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            if(selection.equals("aqi")) {
                Log.i("data_dan", service.getAQ(params[0]));
                result=service.getAQ(params[0]);
            }
                return result;

        }
        @Override
        protected void onPostExecute(String result) {
            if(selection.equals("aqi")) {
                try {
                    aqi = new JSONObject(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                aqiTextView.setTextColor(Color.parseColor(aqi.optString("breezometer_color")));
                aqiTextView.setText(aqi.optString("breezometer_aqi"));
//            textView.setText(result.replace("breezometer_aqi", "").replace(": ","").replace("}","").replace("{", ""));
            }
        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
}
