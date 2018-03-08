package solarsitingucsc.smartsolarsiting.Controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import solarsitingucsc.smartsolarsiting.R;

import static android.content.ContentValues.TAG;


public class DisplayCalculationsActivity extends AppCompatActivity {

    private final String API_KEY = "iF9CgCZD45uP45g5ybzqYdvLINrToH60600nH9it";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_calc);
//        configureCamButton();
        findViewById(R.id.display_calc_view).setOnTouchListener(
                new OnSwipeTouchListener(DisplayCalculationsActivity.this) {
            public void onSwipeRight() {
                finish();
            }
        });
        String imageName = getIntent().getStringExtra("imageName");
        FileInputStream fis = null;
        try {
            fis = openFileInput(imageName);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        ImageView imageView = findViewById(R.id.imageView);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageBitmap(resizedBitmap);
    }

//    private void configureCamButton() {
//        Button camButton = (Button) findViewById(R.id.cam_button);
//        camButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//    }

    public void makeRequest(double latitude, double longitude){
        System.out.println("We are making a JSONObject Request");
        //Instantiate the request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="https://developer.nrel.gov/api/solar/solar_resource/v1.json?" +
                "api_key=" +API_KEY+ "&lat="+latitude+  "&lon="+longitude;

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
