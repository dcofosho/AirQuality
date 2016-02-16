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
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Daniel on 2/7/2016.
 */
public class MainActivity extends Activity {

    Button button;
    TextView textView;
    HandleService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView) findViewById(R.id.textView);
        button=(Button) findViewById(R.id.button);
        service = new HandleService();


        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                new MyTask().execute();
            }
        });
    }
    class MyTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            Log.i("data_dan",service.getAQ());
            return service.getAQ();
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
