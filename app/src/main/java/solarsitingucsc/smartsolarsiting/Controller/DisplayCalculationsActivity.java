package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import solarsitingucsc.smartsolarsiting.R;

import static android.content.ContentValues.TAG;


public class DisplayCalculationsActivity extends AppCompatActivity {

    private final String DATASET_API_KEY = "iF9CgCZD45uP45g5ybzqYdvLINrToH60600nH9it";
    private final static String GOOGLE_VISION_API_KEY = "AIzaSyDx2wu1igClYSoMYTfhvH5Mp0u5x9AxwrE";
    private ProgressBar progressBar;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_calc);
        mContext = getBaseContext();
//        configureCamButton();
        findViewById(R.id.display_calc_view).setOnTouchListener(
                new OnSwipeTouchListener(DisplayCalculationsActivity.this) {
            public void onSwipeRight() {
                finish();
            }
        });
        progressBar = findViewById(R.id.progressBar);
        String imageName = getIntent().getStringExtra("imageName");
        String screenshotName = getIntent().getStringExtra("screenshotName");
        FileInputStream imageFis = null;
        FileInputStream screenshotFis = null;
        try {
            imageFis = openFileInput(imageName);
            screenshotFis = openFileInput(screenshotName);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        }
        Bitmap originalImage = BitmapFactory.decodeStream(imageFis);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap rotatedImage = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(),
                originalImage.getHeight(), matrix, true);
        Bitmap screenshot = BitmapFactory.decodeStream(screenshotFis);

        ImageView imageView = findViewById(R.id.imageView);

        new MakeGoogleRequest().execute(screenshot);

        //Use this to set image as background in the new activity
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView.setImageBitmap(rotatedImage);

        //Use this to set the screenshot (with just the lines) as background in the new activity
        imageView.setImageBitmap(screenshot);
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

    public void makeDatasetRequest(double latitude, double longitude){
        System.out.println("We are making a JSONObject Request");
        //Instantiate the request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="https://developer.nrel.gov/api/solar/solar_resource/v1.json?" +
                "api_key=" + DATASET_API_KEY + "&lat="+latitude+  "&lon="+longitude;

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

    private static Map<String, Integer> translateResponseToMap(String response) {
        //remove newlines
        String[] newline_lines = response.split("\\r?\\n");
        System.out.println(Arrays.toString(newline_lines));
        //reassemble to remove commas and spaces
        String joined_newlines = TextUtils.join(",", newline_lines);
        //remove commas to remove spaces
        String[] comma_lines = joined_newlines.split(",");
        System.out.println(Arrays.toString(comma_lines));
        String joined_comma = TextUtils.join(" ", comma_lines);
        //remove spaces
        String[] lines = joined_comma.split(" ");
        System.out.println(Arrays.toString(lines));
        Map<String, Integer> result = new HashMap<>();
        for (String line : lines) {
            Integer count = result.get(line);
            if (count != null) {
                result.put(line, count + 1);
            } else {
                result.put(line, 1);
            }
        }
        return result;
    }

    private static Image bitmapToImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Image inputImage = new Image();
        inputImage.encodeContent(byteArray);
        return inputImage;
    }

    private static class MakeGoogleRequest extends AsyncTask<Bitmap, Void, String> {

        protected String doInBackground(Bitmap... bitmaps) {
            Image inputScreenshot = bitmapToImage(bitmaps[0]);
            //Use text_detection
            Feature desiredFeature = new Feature();
            desiredFeature.setType("TEXT_DETECTION");

            //Setup requests
            Vision.Builder visionBuilder = new Vision.Builder(
                    new NetHttpTransport(),
                    new AndroidJsonFactory(),
                    null);
            visionBuilder.setVisionRequestInitializer(
                    new VisionRequestInitializer(GOOGLE_VISION_API_KEY));

            Vision vision = visionBuilder.build();

            AnnotateImageRequest requestScreenshot = new AnnotateImageRequest();
            requestScreenshot.setImage(inputScreenshot);
            requestScreenshot.setFeatures(Arrays.asList(desiredFeature));

            BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
            batchRequest.setRequests(Arrays.asList(requestScreenshot));

            //Make requests
            BatchAnnotateImagesResponse batchResponse = null;
            try {
                batchResponse = vision.images().annotate(batchRequest).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Get response
            if (batchResponse != null) {
                TextAnnotation fullTextAnnotation = batchResponse.getResponses().get(0).getFullTextAnnotation();
                //This contains the coordinates for each -- to be used later
                List<EntityAnnotation> textAnnotations = batchResponse.getResponses().get(0)
                        .getTextAnnotations();
                String text = fullTextAnnotation.getText();
                return text;
            }
            return "";
        }

        @Override
        protected void onPostExecute(String response) {
//            super.onPostExecute(response);
            Map<String, Integer> result = translateResponseToMap(response);
            Iterator it = result.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String key = (String) pair.getKey();
                int x;
                System.out.println(key + " = " + pair.getValue() + " occurrences.");
                if (mContext != null)
                    Toast.makeText(mContext, key + " = " + pair.getValue() + " occurrences.",
                            Toast.LENGTH_SHORT).show();
//            try {
//                x = Integer.parseInt(key);
//                if (x < 23 && x > 5 && key.length() == 2) {
//                    System.out.println(key + " = " + pair.getValue() + " occurrences.");
//                    Toast.makeText(this, key + " = " + pair.getValue() + " occurrences.",
//                            Toast.LENGTH_SHORT).show();
//                }
//            } catch(NumberFormatException e) {
//                System.out.println(key + " = " + pair.getValue() + " occurrences.");
//                Toast.makeText(this, key + " = " + pair.getValue() + " occurrences.",
//                        Toast.LENGTH_SHORT).show();
//            }
                {
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }
    }



}
