package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import solarsitingucsc.smartsolarsiting.R;

public class SolarEnergyCalculations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solar_energy_calculations);
    }



    private String API_KEY = "iF9CgCZD45uP45g5ybzqYdvLINrToH60600nH9it";

    public void makeRequest(double latitude, double longitude){
        System.out.println("We are making a JSONObject Request");
        //Instantiate the request queue
        RequestQueue queue = Volley.newRequestQueue(this);


        String lat = String.valueOf(latitude);
        String lon = String.valueOf(longitude);
        String url ="https://developer.nrel.gov/api/solar/solar_resource/v1.json?" +
                "api_key=" +API_KEY+ "&lat="+lat+  "&lon="+lon;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Response: " + response.toString());
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
