package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import solarsitingucsc.smartsolarsiting.R;

public class SolarEnergyCalculations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solar_energy_calculations);
    }


    
    private String API_KEY = "iF9CgCZD45uP45g5ybzqYdvLINrToH60600nH9it";

    public void makeRequest(double latitude, double longitude){

        String lat = String.valueOf(latitude);
        String lon = String.valueOf(longitude);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://developer.nrel.gov/api/solar/solar_resource/v1.json?" +
                "api_key=" +API_KEY+ "&lat="+lat+  "&lon="+lon;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Response is: "+ response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}
