package solarsitingucsc.smartsolarsiting.Controller;
//glide or picasso
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

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
import com.google.api.services.vision.v1.model.BoundingPoly;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;
import com.google.api.services.vision.v1.model.Vertex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Calendar;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import solarsitingucsc.smartsolarsiting.R;

import static android.content.ContentValues.TAG;

public class DisplayCalculationsActivity extends AppCompatActivity {

    private double latitude, longitude;
    private final String DATASET_API_KEY = "iF9CgCZD45uP45g5ybzqYdvLINrToH60600nH9it";
    private final  String GOOGLE_VISION_API_KEY = "AIzaSyDx2wu1igClYSoMYTfhvH5Mp0u5x9AxwrE";
    private ProgressBar progressBar;
    private double[] hourlyArray = new double[8760];
    private TextView[] textViews;
    private Bitmap originalImage;
    private Bitmap rotatedImage;
    private Bitmap screenshot;
    private Bitmap b;
    private ImageView watershed;
    private ImageView dots;
    private List<List<Vertex>> janV = new ArrayList<List<Vertex>>();
    private List<List<Vertex>> febV = new ArrayList<List<Vertex>>();
    private List<List<Vertex>> marV = new ArrayList<List<Vertex>>();
    private List<List<Vertex>> aprV = new ArrayList<List<Vertex>>();
    private List<List<Vertex>> mayV = new ArrayList<List<Vertex>>();
    private List<List<Vertex>> junV = new ArrayList<List<Vertex>>();
    private List<List<Vertex>> julV = new ArrayList<List<Vertex>>();
    private List<List<Vertex>> augV = new ArrayList<List<Vertex>>();
    private List<List<Vertex>> sepV = new ArrayList<List<Vertex>>();
    private List<List<Vertex>> octV = new ArrayList<List<Vertex>>();
    private List<List<Vertex>> novV = new ArrayList<List<Vertex>>();
    private List<List<Vertex>> decV = new ArrayList<List<Vertex>>();
    private int janBox = 0;
    private int febBox = 0;
    private int marBox = 0;
    private int aprBox = 0;
    private int mayBox = 0;
    private int junBox = 0;
    private int julBox = 0;
    private int augBox = 0;
    private int sepBox = 0;
    private int octBox = 0;
    private int novBox = 0;
    private int decBox = 0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_calc);
        findViewById(R.id.display_calc_view).setOnTouchListener(
                new OnSwipeTouchListener(DisplayCalculationsActivity.this) {
                    public void onSwipeRight() {
                        finish();
                    }
                });
        findViewById(R.id.button_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressBar = findViewById(R.id.progressBar);
        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
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
        System.out.println("HERE");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
//        originalImage = BitmapFactory.decodeStream(imageFis, null, options);
        System.out.println("HERE");
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

//        rotatedImage = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(),
//                originalImage.getHeight(), matrix, true);
        screenshot = BitmapFactory.decodeStream(screenshotFis);
        textViews = new TextView[13];
        int[] months = {R.id.janKwTxt, R.id.febKwTxt, R.id.marKwTxt, R.id.aprKwTxt, R.id.mayKwTxt,
                R.id.junKwTxt, R.id.julKwTxt, R.id.augKwTxt, R.id.sepKwTxt, R.id.octKwTxt,
                R.id.novKwTxt, R.id.decKwTxt, R.id.annualKwTxt};
        for (int i = 0; i < textViews.length; i++) {
            textViews[i] = findViewById(months[i]);
        }

        if(getIntent().hasExtra("byteArray")) {
            Log.d(TAG, "FOUNDBYTEARRAY");
            b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent()
                            .getByteArrayExtra("byteArray").length, options);
            watershed = (ImageView) findViewById(R.id.watershed);
            watershed.setImageBitmap(b);
            watershed.setScaleType(ImageView.ScaleType.FIT_XY);
            watershed.setImageBitmap(b);
        }
        dots = (ImageView) findViewById(R.id.dots);
        dots.setImageBitmap(screenshot);
        dots.setScaleType(ImageView.ScaleType.FIT_XY);
        dots.setImageBitmap(screenshot);

        b = Bitmap.createScaledBitmap(b,screenshot.getWidth(),screenshot.getHeight(),true);
        Log.d(TAG, "watershed width x height: " + b.getWidth() +"x" + b.getHeight());
        Log.d(TAG, "dots width x height: " + screenshot.getWidth() + "x" + screenshot.getHeight());
        new MakeGoogleRequest().execute(screenshot);

//        ImageView imageView = findViewById(R.id.imageView);

        //Use this to set image as background in the new activity
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView.setImageBitmap(rotatedImage);

        //Use this to set the screenshot (with just the lines) as background in the new activity
//        imageView.setImageBitmap(screenshot);
    }

    private void makeDatasetRequest(double latitude, double longitude) {
        System.out.println("We are making a JSONObject Request");
        //Instantiate the request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        //This is the link that we are making our Volley call to
        String url = "https://developer.nrel.gov/api/pvwatts/v5.json?" +
                "api_key=" + DATASET_API_KEY + "&lat=" +latitude+ "&lon=" +longitude+
                "&system_capacity=4" + "&azimuth=180" + "&tilt=40" + "&array_type=1" +
                "&module_type=1" + "&losses=10" + "&timeframe=hourly";

        //JSONObject Response Listener
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Response: " + response.toString());
                        try {
                            JSONObject outputs = response.getJSONObject("outputs");
                            JSONArray arr = outputs.getJSONArray("ac");

                            //Adding objects to our hourlyArray to be split
                            for(int i=0; i<arr.length(); i++){
                                hourlyArray[i] = arr.getDouble(i);
                            }
                        } catch(JSONException e){
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

    private double getPowerForMonthAndHour(int month, int hour, List<List<Vertex>> vertices) {
        //We increment by 24 hour time periods
        Log.d(TAG, "month: " + Integer.toString(month));
        final int TWENTY_FOUR_HOURS = 24;
        double[] arrayForMonth = splitForMonth(month);
        double totalAcWatts = 0;
//        int box=0;
//        int[] pixels = new int[400];
//        boolean add=true;
//        b.getPixels(pixels, 0, 20,
//                vertices.get(box).get(box).getX(), vertices.get(box).get(box).getY(), 20, 20);
//        Log.d(TAG, "arrayformonth: " + Integer.toString(arrayForMonth.length) + "index: " + Integer.toString(hour));
//        int w = rotatedImage.getWidth();
//        int h = rotatedImage.getHeight();

//        int[] pixels = new int[h*w];
//        rotatedImage.getPixels(pixels, 0, w,
//                0, 0, w, h);
//        box dimensions for text: 70 x 120

        //Add up the total amount of Watts we will be receiving at the indicated hour
        //for the indicated month
//        for (int i = 0; i < 20; i++) {
//            for (int j = 0; j < 20; j++) {
//                if (Color.red(pixels[i + 20 * j]) != 255 && Color.green(pixels[i + 20 * j]) != 255 &&
//                        Color.blue(pixels[i + 20 * j]) != 255) {
////                            Log.d(TAG, "NOT ADDED");
//                    add = false;
//                    break;
//                }
//            }
//        }
//        if(add==true)
//        {
            for(int index = hour; index < arrayForMonth.length; index += TWENTY_FOUR_HOURS){
//                Log.d(TAG, "month: " + month + " index: " + index + " value: " + arrayForMonth[index]);
//            int[] pixels = new int[400];
//            Log.d(TAG, "index: " + index);
//                if((vertices.size())>index) {
////                Log.d(TAG, "vertice size" + Integer.toString(vertices.size()) + " index size: " +index
////                + " vertex x: " + vertices.get(box).get(box).getX() + " vertex y: " + vertices.get(box).get(box).getY()
////                + " pixel size: " + pixels.length);
//                    b.getPixels(pixels, 0, 20,
//                            vertices.get(box).get(box).getX(), vertices.get(box).get(box).getY(), 20, 20);
//                    boolean add = true;
//                    for (int i = 0; i < 20; i++) {
//                        for (int j = 0; j < 20; j++) {
//                            if (Color.red(pixels[i + 20 * j]) != 255 && Color.green(pixels[i + 20 * j]) != 255 &&
//                                    Color.blue(pixels[i + 20 * j]) != 255) {
////                            Log.d(TAG, "NOT ADDED");
//                                add = false;
//                                break;
//                            }
//                        }
//                    }
//                    if (add == true)
                        totalAcWatts += arrayForMonth[index];
//                }
//            }
        }


        return totalAcWatts/1000;   //Converting from Watts to Kilowatts
    }

    //This function gets only the indexes for the month we are working with
    private double[] splitForMonth(int month) {
        double[] monthlyArray = null;
        switch(month){
            case Calendar.JANUARY:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 0, 744);
                break;

            case Calendar.FEBRUARY:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 744, 1416);
                break;

            case Calendar.MARCH:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 1416, 2161);
                break;

            case Calendar.APRIL:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 2161, 2881);
                break;

            case Calendar.MAY:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 2881, 3625);
                break;

            case Calendar.JUNE:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 3625, 4345);
                break;

            case Calendar.JULY:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 4345, 5089);
                break;

            case Calendar.AUGUST:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 5089, 5833);
                break;

            case Calendar.SEPTEMBER:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 5833, 6553);
                break;

            case Calendar.OCTOBER:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 6553, 7297);
                break;

            case Calendar.NOVEMBER:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 7297, 8017);
                break;

            case Calendar.DECEMBER:
                monthlyArray = Arrays.copyOfRange(hourlyArray, 8017, 8760);
                break;

        }
        return monthlyArray;
    }

    private Map<String, Integer> translateResponseToMap(String response) {
        //Sometimes 8s are confused with Bs by Google's API -- there should be no Bs so change to 8s
        response = response.replace("B", "8");
        //remove newlines
        String[] newline_lines = response.split("\\r?\\n");
        //reassemble to remove commas and spaces
        String joined_newlines = TextUtils.join(",", newline_lines);
        //remove commas to remove spaces
        String[] comma_lines = joined_newlines.split(",");
        String joined_comma = TextUtils.join(" ", comma_lines);
        //remove spaces
        String[] lines = joined_comma.split(" ");
        return analyseResponse(lines);
    }

    private Map<String, Integer> analyseResponse(String[] response) {
        String[] months = {"00", "01", "02", "03", "04", "06", "07", "08", "09", "10", "11"};
        String[] hours = new String[24];
        Map<String, Integer> finalResult = new HashMap<>();
        for (int i = 1; i < hours.length + 1; i++) {
            if (i < 10)
                hours[i - 1] = "0" + i;
            else
                hours[i - 1] = i + "";
        }
        Map<String, Integer> result = new HashMap<>();
        for (String s : response) {
            Integer count = result.get(s);
            if (count != null) {
                result.put(s, count + 1);
            } else {
                result.put(s, 1);
            }
        }
        Iterator it = result.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String key = (String) pair.getKey();
            int monthInt, hourInt, dashIndex = key.indexOf("-"), previousIndex = 0;
            String month = "", hour = "";
            while(dashIndex >= 0) {
                //Get the month and hour strings from the key
                month = key.substring(previousIndex, dashIndex);
                hour = key.substring(dashIndex + 1, key.length());

                //Try and match the month and hour strings to the closest possibility
                month = getClosestString(months, month);
                hour = getClosestString(hours, hour);

                try {
                    //Check if month and hour can be integers
                    monthInt = Integer.parseInt(month);
                    hourInt = Integer.parseInt(hour);

                    //If so, add the concatenation of the two to the final hashmap
                    String finalString = month + "-" + hour;
                    Integer count = finalResult.get(finalString);
                    if (count != null) {
                        finalResult.put(finalString, count + 1);
                    } else {
                        finalResult.put(finalString, 1);
                    }
                } catch (NumberFormatException e) {
                    //ignored
                }

                //Move on to next dash if it exists
                previousIndex = dashIndex;
                dashIndex = key.indexOf("-", dashIndex + 1);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return finalResult;
    }

    private String getClosestString(String[] selection, String stringToMatch) {
        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < selection.length; i++) {
            int distance = getLevenshteinDistance(stringToMatch, selection[i]);
            if (min > distance) {
                min = distance;
                minIndex = i;
            }
        }
        if (min > 3)
            return stringToMatch;
        else
            return selection[minIndex];
    }

    private int getLevenshteinDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    private Image bitmapToImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Image inputImage = new Image();
        inputImage.encodeContent(byteArray);
        return inputImage;
    }

    private class MakeGoogleRequest extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            makeDatasetRequest(latitude, longitude);
        }

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
//                EntityAnnotation test = new EntityAnnotation();
//                BoundingPoly test1 = new BoundingPoly();
//                test1.getVertices(); //list of vertex
//                Vertex test2 = new Vertex();
//                test2.


                List<EntityAnnotation> textAnnotations = batchResponse.getResponses().get(0)
                        .getTextAnnotations();
//                System.out.println(Arrays.toString(textAnnotations.toArray()));
//                textAnnotations.get(0).getDes
//                dashIndex = key.indexOf("-")
                //grab list of vertices of all points in picture
                Log.d(TAG,"text Annotations Size: " + Integer.toString(textAnnotations.size()));
                for(int i=0;i<textAnnotations.size()-1;i++)
                {
                    Log.d(TAG,"TA Element: " + Integer.toString(i));
//                    EntityAnnotation point = textAnnotations.get(i);

                    BoundingPoly textBox = textAnnotations.get(i).getBoundingPoly();
                    String desc = textAnnotations.get(i).getDescription();
                    if(desc.length()==5) {
                        Log.d(TAG, desc);
                        String month = desc.substring(0, 2);
                        if (month.equals("00")) janV.add(textBox.getVertices());
                        else if (month.equals("01")) febV.add(textBox.getVertices());
                        else if (month.equals("02")) marV.add(textBox.getVertices());
                        else if (month.equals("03")) aprV.add(textBox.getVertices());
                        else if (month.equals("04")) mayV.add(textBox.getVertices());
                        else if (month.equals("05")) junV.add(textBox.getVertices());
                        else if (month.equals("06")) julV.add(textBox.getVertices());
                        else if (month.equals("07")) augV.add(textBox.getVertices());
                        else if (month.equals("08")) sepV.add(textBox.getVertices());
                        else if (month.equals("09")) octV.add(textBox.getVertices());
                        else if (month.equals("10")) novV.add(textBox.getVertices());
                        else if (month.equals("11")) decV.add(textBox.getVertices());
                    }

//                    vertices.add(textBox.getVertices());
                }
                Log.d(TAG, "jan length: " + Integer.toString(janV.size()));
                Log.d(TAG, "feb length: " + Integer.toString(febV.size()));
                Log.d(TAG, "mar length: " + Integer.toString(marV.size()));
                Log.d(TAG, "apr length: " + Integer.toString(aprV.size()));
                Log.d(TAG, "may length: " + Integer.toString(mayV.size()));
                Log.d(TAG, "jun length: " + Integer.toString(junV.size()));
                Log.d(TAG, "jul length: " + Integer.toString(julV.size()));
                Log.d(TAG, "aug length: " + Integer.toString(augV.size()));
                Log.d(TAG, "sep length: " + Integer.toString(sepV.size()));
                Log.d(TAG, "oct length: " + Integer.toString(octV.size()));
                Log.d(TAG, "nov length: " + Integer.toString(novV.size()));
                Log.d(TAG, "dec length: " + Integer.toString(decV.size()));


                //WHAT I NEED TO DO:
//                READ WHAT MONTH IT IS SHOULD SHOW IN PHONE
//                CREATE LIST FOR EACH MONTH TO STORE VERTICES
//                USE SPECIFIC LIST FOR PIXEL COMPARING
//                Log.d(TAG, "vertice x: " + Integer.toString(vertices.get(0).get(0).getX().intValue()) +
//                        "vertice y: " + Integer.toString(vertices.get(0).get(0).getY().intValue()));
//                Log.d(TAG, "vertice x: " + Integer.toString(vertices.get(0).get(1).getX().intValue()) +
//                        "vertice y: " + Integer.toString(vertices.get(0).get(1).getY().intValue()));
//                Log.d(TAG, "vertice x: " + Integer.toString(vertices.get(0).get(2).getX().intValue()) +
//                        "vertice y: " + Integer.toString(vertices.get(0).get(2).getY().intValue()));
//                Log.d(TAG, "vertice x: " + Integer.toString(vertices.get(0).get(3).getX().intValue()) +
//                        "vertice y: " + Integer.toString(vertices.get(0).get(3).getY().intValue()));
                String text;
                if (fullTextAnnotation != null)
                    text = fullTextAnnotation.getText();
                else
                    text = "";
                return text;
            }
            return "";
        }

        @Override
        protected void onPostExecute(String response) {
            Map<String, Integer> result = translateResponseToMap(response);
            Iterator it = result.entrySet().iterator();
            int annualPower = 0;
            int[] monthlyPower = new int[12];
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String key = (String) pair.getKey();
                int value = (int) pair.getValue();
//                Log.d(TAG,"Value: " + value);
                int dashIndex = key.indexOf("-");
                String month = "", hour = "";
                if (dashIndex != -1) {
                    month = key.substring(0, dashIndex);
                    hour = key.substring(dashIndex + 1, key.length());
                }
//                int box=0;
//                int[] pixels = new int[400];
//                boolean add=true;
                for (int i = 0; i < value; i++) {
                    try {
                        int monthInt = Integer.parseInt(month), hourInt = Integer.parseInt(hour);
                        double power=0;
                        Log.d(TAG, "month: " + month);
//                        for (int k = 0; i < 20; i++) {
//                            for (int j = 0; j < 20; j++) {
//                                if (Color.red(pixels[k + 20 * j]) != 255 && Color.green(pixels[k + 20 * j]) != 255 &&
//                                        Color.blue(pixels[k + 20 * j]) != 255) {
////                            Log.d(TAG, "NOT ADDED");
//                                    add = false;
//                                    break;
//                                }
//                            }
//                        }
//                        if(add==true) {
                            if (monthInt == 0) {
                                if(!processPixels(janV,janBox) && janBox!=janV.size())
                                {
                                    janBox++;
                                } else power = getPowerForMonthAndHour(monthInt, hourInt, janV) / 6;
                            }
                            else if (monthInt == 1){
                                if(!processPixels(febV, febBox) && febBox!=febV.size())
                                {
                                    febBox++;
                                }
                                power = getPowerForMonthAndHour(monthInt, hourInt, febV) / 6;
                            }
                            else if (monthInt == 2) {
                                if(!processPixels(marV, marBox) && marBox!=marV.size())
                                {
                                    marBox++;
                                } else power = getPowerForMonthAndHour(monthInt, hourInt, marV) / 6;
                            }
                            else if (monthInt == 3) {
                                if(!processPixels(aprV, aprBox) && aprBox!=aprV.size())
                                {
                                    aprBox++;
                                } else power = getPowerForMonthAndHour(monthInt, hourInt, aprV) / 6;
                            }
                            else if (monthInt == 4) {
                                if(!processPixels(mayV, mayBox) && mayBox!=mayV.size())
                                {
                                    mayBox++;
                                } else power = getPowerForMonthAndHour(monthInt, hourInt, mayV) / 6;
                            }
                            else if (monthInt == 5) {
                                if(!processPixels(junV, junBox) && junBox!=junV.size())
                                {
                                    junBox++;
                                } else power = getPowerForMonthAndHour(monthInt, hourInt, junV) / 6;
                            }
                            else if (monthInt == 6) {
                                if(!processPixels(julV, julBox) && julBox!=julV.size())
                                {
                                    julBox++;
                                } else power = getPowerForMonthAndHour(monthInt, hourInt, julV) / 6;
                            }
                            else if (monthInt == 7) {
                                if(!processPixels(augV, augBox) && augBox!=augV.size())
                                {
                                    augBox++;
                                } else power = getPowerForMonthAndHour(monthInt, hourInt, augV) / 6;
                            }
                            else if (monthInt == 8) {
                                if(!processPixels(sepV, sepBox) && sepBox!=sepV.size())
                                {
                                    sepBox++;
                                } else power = getPowerForMonthAndHour(monthInt, hourInt, sepV) / 6;
                            }
                            else if (monthInt == 9) {
                                if(!processPixels(octV, octBox) && octBox!=octV.size())
                                {
                                    octBox++;
                                } else power = getPowerForMonthAndHour(monthInt, hourInt, octV) / 6;
                            }
                            else if (monthInt == 10) {
                                if(!processPixels(novV, novBox) && novBox!=novV.size())
                                {
                                    novBox++;
                                } else power = getPowerForMonthAndHour(monthInt, hourInt, novV) / 6;
                            }
                            else if (monthInt == 11) {
                                if(!processPixels(decV, decBox) && decBox!=decV.size())
                                {
                                    decBox++;
                                } else power = getPowerForMonthAndHour(monthInt, hourInt, decV) / 6;
                            }
                            annualPower += power;
                            monthlyPower[monthInt] += power;
//                        }
                    } catch (NumberFormatException e) {
                        break;
                    }

                }
                it.remove(); // avoids a ConcurrentModificationException
            }
            progressBar.setVisibility(View.GONE);
            String text = annualPower + "kW";
            textViews[12].setText(text);
            for (int i = 0; i < monthlyPower.length; i++) {
                text = monthlyPower[i] + "kW";
                textViews[i].setText(text);
            }
        }
    }

    public boolean processPixels(List<List<Vertex>> vertices, int box)
    {
        Log.d(TAG, "month size: " + vertices.size() + " verx: " + vertices.get(box).get(0).getX());
        int[] pixels = new int[400];
        b.getPixels(pixels, 0, 20,
                vertices.get(box).get(0).getX(), vertices.get(box).get(0).getY(), 20, 20);
        for (int k = 0; k < 20; k++) {
            for (int j = 0; j < 20; j++) {
                if (Color.red(pixels[k + 20 * j]) != 255 && Color.green(pixels[k + 20 * j]) != 255 &&
                        Color.blue(pixels[k + 20 * j]) != 255) {
                            Log.d(TAG, "NOT ADDED");
                    return false;
                }
            }
        }
        return true;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

