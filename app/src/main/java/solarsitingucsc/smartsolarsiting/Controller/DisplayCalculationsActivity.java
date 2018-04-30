package solarsitingucsc.smartsolarsiting.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
<<<<<<< Updated upstream
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
=======
>>>>>>> Stashed changes
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

import solarsitingucsc.smartsolarsiting.Model.SolarSiting;
import solarsitingucsc.smartsolarsiting.R;

import static android.content.ContentValues.TAG;

public class DisplayCalculationsActivity extends AppCompatActivity {

    private static final String[] months = {"January" , "February" , "March" , "April", "May",
            "June", "July", "August", "September", "October",
            "November", "December"};

    private double latitude, longitude;
    private final String DATASET_API_KEY = "iF9CgCZD45uP45g5ybzqYdvLINrToH60600nH9it";
    private final String GOOGLE_VISION_API_KEY = "AIzaSyDx2wu1igClYSoMYTfhvH5Mp0u5x9AxwrE";
    private ProgressBar progressBar;
    private double[] hourlyArray = new double[8760];
    private TextView[] textViews;
    private FirebaseAuth mAuth;
    private FloatingActionButton saveBtn;
    private String imageName;
    private String screenshotName;
    private Bitmap imageToSave;
    private Bitmap thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_calc);
//        Runtime.getRuntime().freeMemory();
//        mAuth = FirebaseAuth.getInstance();
//        findViewById(R.id.display_calc_view).setOnTouchListener(
//                new OnSwipeTouchListener(DisplayCalculationsActivity.this) {
//                    public void onSwipeRight() {
//                        finish();
//                    }
//                });
//        FloatingActionButton capture = findViewById(R.id.button_capture);
//        capture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//        saveBtn = findViewById(R.id.button_save);
//        progressBar = findViewById(R.id.progressBar);
//        latitude = getIntent().getDoubleExtra("latitude", 0.0);
//        longitude = getIntent().getDoubleExtra("longitude", 0.0);
//        imageName = getIntent().getStringExtra("imageName");
//        screenshotName = getIntent().getStringExtra("screenshotName");
//        FileInputStream imageFis = null;
//        FileInputStream screenshotFis = null;
//        try {
//            imageFis = openFileInput(imageName);
//            screenshotFis = openFileInput(screenshotName);
//        } catch (FileNotFoundException e) {
//            Log.d(TAG, "File not found: " + e.getMessage());
//        }
//        Bitmap originalImage = BitmapFactory.decodeStream(imageFis);
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//
//        imageToSave = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(),
//                originalImage.getHeight(), matrix, true);
//        Bitmap screenshot = BitmapFactory.decodeStream(screenshotFis);
//        thumbnail = makeThumbnail(originalImage, matrix);
//
//        textViews = new TextView[13];
//        int[] months = {R.id.janKwTxt, R.id.febKwTxt, R.id.marKwTxt, R.id.aprKwTxt, R.id.mayKwTxt,
//                R.id.junKwTxt, R.id.julKwTxt, R.id.augKwTxt, R.id.sepKwTxt, R.id.octKwTxt,
//                R.id.novKwTxt, R.id.decKwTxt, R.id.annualKwTxt};
//        for (int i = 0; i < textViews.length; i++) {
//            textViews[i] = findViewById(months[i]);
//        }
//
//        new MakeGoogleRequest().execute(screenshot);
//        deleteFile(screenshotName);
//

//        new MakeGoogleRequest().execute(screenshot);
        createSomthing();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteFile(imageName);
        deleteFile(screenshotName);
    }

    private void makeDatasetRequest(double latitude, double longitude) {
        System.out.println("We are making a JSONObject Request");
        //Instantiate the request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        //This is the link that we are making our Volley call to
        String url = "https://developer.nrel.gov/api/pvwatts/v5.json?" +
                "api_key=" + DATASET_API_KEY + "&lat=" + latitude + "&lon=" + longitude +
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
                            JSONArray poa = outputs.getJSONArray("poa");

                            System.out.println(arr.toString());
                            System.out.println(poa.toString());

                            //Adding objects to our hourlyArray to be split
                            for (int i = 0; i < arr.length(); i++) {
                                hourlyArray[i] = arr.getDouble(i);

                            }
                        } catch (JSONException e) {
                            System.out.println(e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("That didn't work!");
                    }
                });
        // Add the request to the request queue
        queue.add(jsObjRequest);
    }

    private double getPowerForMonthAndHour(int month, int hour) {
        //We increment by 24 hour time periods
        final int TWENTY_FOUR_HOURS = 24;
        double[] arrayForMonth = splitForMonth(month);
        double totalAcWatts = 0;

        //Add up the total amount of Watts we will be receiving at the indicated hour
        //for the indicated month
        for (int index = hour; index < arrayForMonth.length; index += TWENTY_FOUR_HOURS) {
            totalAcWatts += arrayForMonth[index];
        }


        return totalAcWatts / 1000;   //Converting from Watts to Kilowatts
    }

    //This function gets only the indexes for the month we are working with
    private double[] splitForMonth(int month) {
        double[] monthlyArray = null;
        switch (month) {
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
        String[] months = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"};
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
            Map.Entry pair = (Map.Entry) it.next();
            String key = (String) pair.getKey();
            int monthInt, hourInt, dashIndex = key.indexOf("-"), previousIndex = 0;
            String month = "", hour = "";
            while (dashIndex >= 0) {
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
        int[] costs = new int[b.length() + 1];
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

    class ChartValue{
        int month, value;

        public ChartValue(int month, int value) {
            this.month = month;
            this.value = value;
        }
    }


    public void createSomthing() {
        Random random = new Random();
        final LineChart chart = findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entries.add(new Entry(i, random.nextInt(100)));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Power in KW");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();

<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months[(int) value];
            }
        });

        findViewById(R.id.save_plot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.saveToGallery("mychart.jpg", 85); // 85 is the quality of the image
            }
        });
    }


    private Bitmap makeThumbnail(Bitmap imageBitmap, Matrix matrix) {
        final int THUMBNAIL_SIZE = 64;
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(),
                imageBitmap.getHeight(), matrix, true);
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return "";
    }

    private void storeResults(final String name, final int[] monthlyPower) {
        final String id = getCurrentUserId();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("pictures");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(id + name)) {
                    Toast.makeText(DisplayCalculationsActivity.this,
                            "Name already in use", Toast.LENGTH_SHORT).show();
                } else {
                    storeImage(thumbnail, id + name);
                    Date today = Calendar.getInstance().getTime();
                    DateFormat df = DateFormat.getDateTimeInstance();
                    SolarSiting solarSiting = new SolarSiting(
                            id, name,
                            Arrays.toString(monthlyPower).split("[\\[\\]]")[1].split(", "),
                            id, df.format(today));
                    solarSiting.store();
                    Toast.makeText(DisplayCalculationsActivity.this,
                            "Saved!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DisplayCalculationsActivity.this,
                        "Unknown error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeImage(Bitmap image, String name) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(name + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(DisplayCalculationsActivity.this,
                        "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
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
                List<EntityAnnotation> textAnnotations = batchResponse.getResponses().get(0)
                        .getTextAnnotations();
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

            ArrayList<Integer> list = new ArrayList<>(12);
            Random random = new Random();
            for (int i = 0; i < 12; i++) {
                list.add(random.nextInt(100));
            }

            Iterator it = list.iterator();

            int annualPower = 0;
            final int[] monthlyPower = new int[13];
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String key = (String) pair.getKey();
                int value = (int) pair.getValue();
                int dashIndex = key.indexOf("-");
                String month = "", hour = "";
                if (dashIndex != -1) {
                    month = key.substring(0, dashIndex);
                    hour = key.substring(dashIndex + 1, key.length());
                }
                for (int i = 0; i < value; i++) {
                    try {
                        int monthInt = Integer.parseInt(month), hourInt = Integer.parseInt(hour);
                        double power = getPowerForMonthAndHour(monthInt, hourInt) / 6;
                        annualPower += power;
                        monthlyPower[monthInt] += power;
                    } catch (NumberFormatException e) {
                        break;
                    }
                }
                it.remove(); // avoids a ConcurrentModificationException
            }
            monthlyPower[monthlyPower.length - 1] = annualPower;
            progressBar.setVisibility(View.GONE);
            for (int i = 0; i < monthlyPower.length; i++) {
                String text = monthlyPower[i] + "kW";
                textViews[i].setText(text);
            }

            saveBtn.setVisibility(View.VISIBLE);
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String[] name = {""};
                    Context context = DisplayCalculationsActivity.this;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setTitle("Name: ");
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = input.getText().toString();
                            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                            Matcher m = p.matcher(text);
                            if (text.equals("")) {
                                Toast.makeText(DisplayCalculationsActivity.this,
                                        "Name can't be empty", Toast.LENGTH_SHORT).show();
                            }
                            else if (m.find()) {
                                Toast.makeText(DisplayCalculationsActivity.this,
                                        "Name can't contain special symbols", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                name[0] = input.getText().toString();
                                storeResults(name[0], monthlyPower);
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });
        }

    }
}

