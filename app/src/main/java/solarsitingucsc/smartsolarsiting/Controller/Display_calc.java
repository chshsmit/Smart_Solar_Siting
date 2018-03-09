package solarsitingucsc.smartsolarsiting.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import solarsitingucsc.smartsolarsiting.R;
import solarsitingucsc.smartsolarsiting.View.DrawOnTop;
import solarsitingucsc.smartsolarsiting.View.CameraPreview;



public class Display_calc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_calc);

        makeRequest(36.9, -122.03);
        configureCamButton();
    }

    private void configureCamButton() {
        Button camButton = (Button) findViewById(R.id.cam_button);
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    final private String API_KEY = "iF9CgCZD45uP45g5ybzqYdvLINrToH60600nH9it";

    public void makeRequest(double latitude, double longitude){
        System.out.println("We are making a JSONObject Request");
        //Instantiate the request queue
        RequestQueue queue = Volley.newRequestQueue(this);

//        String url ="https://developer.nrel.gov/api/solar/solar_resource/v1.json?" +
//                "api_key=" +API_KEY+ "&lat="+latitude+  "&lon="+longitude;

        String url = "https://developer.nrel.gov/api/pvwatts/v5.json?" +
        "api_key=" +API_KEY+"&lat=36.9&lon=-122.03&system_capacity=4&azimuth=180&" +
                "tilt=40&array_type=1&module_type=1&losses=10&timeframe=hourly";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Response: " + response.toString());
                        try {
                            System.out.println("Hourly Size: " + response.get("ac"));
                        }catch(JSONException e){
                            System.out.println(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.out.println("That didn't work!");


                    }
                });

        // Add the request to the request queue
        queue.add(jsObjRequest);
    }

}
