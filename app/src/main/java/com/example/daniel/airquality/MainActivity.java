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
    Button locationBtn;
    Button aqiBtn;
    Button descriptionBtn;


    TextView aqiTextView;
    TextView descriptionTextView;
    HandleService service;
//    Double latitude;
//    Double longitude;
    String location;
    EditText editText;
    JSONObject aqi;
    JSONObject description;
//    EditText editText2;
    String selection;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationBtn=(Button) findViewById(R.id.locationBtn);
        aqiTextView=(TextView) findViewById(R.id.aqiTextView);
        aqiBtn=(Button) findViewById(R.id.aqiBtn);
        descriptionTextView=(TextView)findViewById(R.id.descriptionTextView);
        descriptionBtn=(Button) findViewById(R.id.descriptionBtn);


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
                location=editText.getText().toString().replace(",","").replace(" ", "+");
                aqiTextView.setText("");
                descriptionTextView.setText("");
            }
        });
        aqiBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                selection="aqi";
                location=editText.getText().toString().replace(",","").replace(" ", "+");
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
                } catch (Exception e) {
                    e.printStackTrace();
                }

//            textView.setText(result.replace("breezometer_aqi", "").replace(": ","").replace("}","").replace("{", ""));
            }else if(selection.equals("description")){
                try {
                    description = new JSONObject(result);
                    descriptionTextView.setTextColor(Color.parseColor(description.optString("breezometer_color")));
                    descriptionTextView.setText(description.optString("breezometer_description"));
                } catch (Exception e) {
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
}
