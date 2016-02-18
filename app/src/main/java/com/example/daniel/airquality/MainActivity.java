package com.example.daniel.airquality;

import android.app.Activity;
import android.content.Context;
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
    Button button;
    TextView textView;
    HandleService service;
//    Double latitude;
//    Double longitude;
    String location;
    EditText editText;
//    EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView) findViewById(R.id.textView);
        button=(Button) findViewById(R.id.button);
        service = new HandleService();
        editText= (EditText) findViewById(R.id.editText);
//        editText2= (EditText) findViewById(R.id.editText2);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                location=editText.getText().toString().replace(",","").replace(" ", "+");
//                longitude=Double.parseDouble(editText2.getText().toString());
                new MyTask().execute(location);
            }
        });
    }
    class MyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i("data_dan",service.getAQ(params[0]));
            return service.getAQ(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);
        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
}
