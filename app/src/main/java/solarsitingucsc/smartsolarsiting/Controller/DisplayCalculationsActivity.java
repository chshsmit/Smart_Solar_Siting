package solarsitingucsc.smartsolarsiting.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.res.Resources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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
import java.lang.reflect.Field;
import java.text.DateFormat;
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

import solarsitingucsc.smartsolarsiting.Model.SolarSiting;
import solarsitingucsc.smartsolarsiting.R;

import static android.content.ContentValues.TAG;

public class DisplayCalculationsActivity extends AppCompatActivity {

    private double latitude, longitude;
    private final String DATASET_API_KEY = "iF9CgCZD45uP45g5ybzqYdvLINrToH60600nH9it";
    private final String GOOGLE_VISION_API_KEY = "AIzaSyDx2wu1igClYSoMYTfhvH5Mp0u5x9AxwrE";
    private static final String[] months = {"January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October",
            "November", "December"};
    private ProgressBar progressBar;
    private double[] hourlyArray = new double[8760];
    private FirebaseAuth mAuth;
    private String imageName;
    private String screenshotName;
    private Bitmap thumbnail;
    private LineChart lineChart;
    private BarChart barChart;
    private List<Entry> lineEntries;
    private List<BarEntry> barEntries;
    private boolean saved;
    private boolean showLineChart;
    private Spinner dropdown;
    private HashMap<String, HashMap<String, Double>> powerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_calc);
        powerMap = null;
        showLineChart = true;

        //Getting values from shared preferences
        setPanelConstraints();

        //Free memory and set firebase auth
        freeMemAndFirebase();

        //Set up save button and progress bar
        initializeViews();

        HashMap<String, HashMap<String, Double>> powerList =
                (HashMap<String, HashMap<String, Double>>) getIntent()
                        .getSerializableExtra("powerList");

        if (powerList != null) {
            powerMap = powerList;
            initializeToolBar();
//            setupDropdown(powerList);
            saved = true;
        }
        else {
            saved = false;
            //Set values from the intent
            getIntentValues();

            //Get the image and make google request
            setBitmap();
        }
    }

    //----------------------------------------------------------------------------------------------
    //On create functions
    //----------------------------------------------------------------------------------------------

    private void setBitmap() {
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

        Bitmap screenshot = BitmapFactory.decodeStream(screenshotFis);
        thumbnail = makeThumbnail(originalImage, matrix);

        new MakeGoogleRequest().execute(screenshot);
        deleteFile(screenshotName);
    }

    private void freeMemAndFirebase() {
        Runtime.getRuntime().freeMemory();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initializeViews() {
        findViewById(R.id.display_calc_view).setOnTouchListener(
                new OnSwipeTouchListener(DisplayCalculationsActivity.this) {
                    public void onSwipeRight() {
                        finish();
                    }
                });
        progressBar = findViewById(R.id.progressBar);
    }

    private void getIntentValues() {
        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        imageName = getIntent().getStringExtra("imageName");
        screenshotName = getIntent().getStringExtra("screenshotName");
    }


    //----------------------------------------------------------------------------------------------
    //Toolbar functions
    //----------------------------------------------------------------------------------------------

    private void initializeToolBar() {
        //Toolbar setup
        Toolbar topToolBar = findViewById(R.id.results_toolbar);
        Drawable homeIcon = ContextCompat.getDrawable(this, R.drawable.baseline_home_black_24);
        topToolBar.setNavigationIcon(homeIcon);
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToHomePage();
            }
        });
        getSupportActionBar().setTitle(null);
    }

    private void changeToHomePage() {
        if (!saved) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to go back to the home page? Unsaved data will be discarded.");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Go back to the home page
                    Intent homePage = new Intent(DisplayCalculationsActivity.this, HomePageActivity.class);
                    startActivity(homePage);
                }
            });
            builder.show();
        } else {
            //Go back to the home page
            Intent homePage = new Intent(DisplayCalculationsActivity.this, HomePageActivity.class);
            startActivity(homePage);
        }
    }


    //ToolBar function to setup res/menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.results_bar_menu, menu);
        dropdown = (Spinner) menu.findItem(R.id.spinner).getActionView();
        if (powerMap != null)
            setupDropdown(powerMap);
        return true;
    }

    //Toolbar function for when the dots are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save) {
            if (saved) {
                Toast.makeText(DisplayCalculationsActivity.this,
                        "Already saved!", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (powerMap != null) {
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
                            dialog.cancel();
                        } else if (m.find()) {
                            Toast.makeText(DisplayCalculationsActivity.this,
                                    "Name can't contain special symbols", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        } else {
                            name[0] = input.getText().toString();
                            storeResults(name[0], powerMap);
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
            return true;
        }

        String text = dropdown.getSelectedItem().toString();
        if (text.equals("All"))
            text += " months";
        if (id == R.id.share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            Bitmap bitmap = lineChart.getChartBitmap();
            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
            Uri bitmapUri = Uri.parse(bitmapPath);
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);

            intent.putExtra(Intent.EXTRA_TEXT, text);
            intent.setType("*/*");
            startActivity(Intent.createChooser(intent, "Share"));

            return true;
        }
        else if (id == R.id.change_chart_type) {
            showLineChart = !showLineChart;
            if (showLineChart) {
                displayLineChart(text);
                item.setIcon(R.drawable.baseline_bar_chart_black_24dp);
            } else {
                displayBarChart(text);
                item.setIcon(R.drawable.baseline_timeline_black_24dp);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    //----------------------------------------------------------------------------------------------
    //Fucntions that we are over riding
    //----------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        changeToHomePage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageName != null)
            deleteFile(imageName);
        if (screenshotName != null)
            deleteFile(screenshotName);
    }

    //----------------------------------------------------------------------------------------------
    //Functions to make call to database
    //----------------------------------------------------------------------------------------------

    //Variables for the API url
    private int sysCapacity;
    private int sysAzimuth;
    private int sysTilt;
    private int sysLosses;
    private String sysArrayType;
    private String sysModuleType;
    private String sysDataset;


    private void setPanelConstraints() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        sysCapacity = prefs.getInt("system_capacity", 4);
        sysAzimuth = prefs.getInt("sys_azimuth", 180);
        sysTilt = prefs.getInt("sys_tilt", 40);
        sysLosses = prefs.getInt("sys_losses", 10);
        sysArrayType = prefs.getString("sys_array_type", "1");
        sysModuleType = prefs.getString("sys_mod_type", "1");
        sysDataset = prefs.getString("sys_dataset", "tmy2");
    }

    private void makeDatasetRequest(double latitude, double longitude) {
        System.out.println("We are making a JSONObject Request");
        //Instantiate the request queue
        RequestQueue queue = Volley.newRequestQueue(this);


        //This is the link that we are making our Volley call to
        String url = "https://developer.nrel.gov/api/pvwatts/v5.json?" +
                "api_key=" + DATASET_API_KEY + "&lat=" + latitude + "&lon=" + longitude +
                "&system_capacity=" + sysCapacity + "&azimuth=" + sysAzimuth + "&tilt=" + sysTilt +
                "&array_type=" + sysArrayType + "&module_type=" + sysModuleType + "&losses=" + sysLosses +
                "&dataset=" + sysDataset + "&timeframe=hourly";

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

    //----------------------------------------------------------------------------------------------
    //Functions to parse through the database results
    //----------------------------------------------------------------------------------------------

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


    //----------------------------------------------------------------------------------------------
    //Functions for the Google Vision API
    //----------------------------------------------------------------------------------------------

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

    //----------------------------------------------------------------------------------------------
    //Functions to process the bitmap/image
    //----------------------------------------------------------------------------------------------

    private Image bitmapToImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Image inputImage = new Image();
        inputImage.encodeContent(byteArray);
        return inputImage;
    }

    private Bitmap makeThumbnail(Bitmap imageBitmap, Matrix matrix) {
        final int THUMBNAIL_SIZE = 64;
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(),
                imageBitmap.getHeight(), matrix, true);
    }


    //----------------------------------------------------------------------------------------------
    //Fuctions for storing information to firebase
    //----------------------------------------------------------------------------------------------

    private String getCurrentUserId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return "";
    }

    private void storeResults(final String name, final HashMap<String, HashMap<String, Double>> monthlyPower) {
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
                    SolarSiting solarSiting = new SolarSiting(id, name, monthlyPower, df.format(today));
                    solarSiting.store();
                    Toast.makeText(DisplayCalculationsActivity.this,
                            "Saved!", Toast.LENGTH_SHORT).show();
                    saved = true;
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

    private void generateChartData(String month, HashMap<String, HashMap<String, Double>> powerMap) {
        lineEntries = new ArrayList<>();
        barEntries= new ArrayList<>();
        if (month.equals("All")) {
            Double[] monthValues = new Double[12];
            HashMap<String, Double> allValues = powerMap.get(month);
            for (int i = 0; i < monthValues.length; i++) {
                if (allValues.get(months[i]) != null && allValues.get(months[i]) instanceof Double)
                    monthValues[i] = allValues.get(months[i]);
                else
                    monthValues[i] = 0d;
            }
            for (int i = 0; i < monthValues.length; i++) {
                lineEntries.add(new Entry(i, monthValues[i].floatValue()));
                barEntries.add(new BarEntry((float)i, monthValues[i].floatValue()));
            }

        } else {
            Double[] hours = new Double[24];
            HashMap<String, Double> monthValues = powerMap.get(month);
            for (int i = 0; i < hours.length; i++) {
                try {
                    if (monthValues != null && monthValues.get("" + i) != null)
                        hours[i] = monthValues.get("" + i);
                    else
                        hours[i] = 0d;
                } catch (ClassCastException e) {
                    hours[i] = 0d;
                }
            }
            for (int i = 0; i < hours.length; i++) {
                lineEntries.add(new Entry(i, hours[i].floatValue()));
                barEntries.add(new BarEntry((float)i, hours[i].floatValue()));

            }
            String[] stringHours = new String[hours.length];
            for (int i = 0; i < stringHours.length; i++) {
                stringHours[i] = i + ":00";
            }
        }
    }

    private void displayLineChart(String month) {
        lineChart = findViewById(R.id.line_chart);
        barChart = findViewById(R.id.bar_chart);
        lineChart.setVisibility(View.VISIBLE);
        barChart.setVisibility(View.GONE);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        if (month.equals("All")) {
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return months[(int) value];
                }
            });
        } else {
            String[] stringHours = new String[24];
            for (int i = 0; i < stringHours.length; i++) {
                stringHours[i] = i + ":00";
            }

            final String[] finalHours = stringHours;
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return finalHours[(int) value];
                }
            });
        }
        LineDataSet dataSet = new LineDataSet(lineEntries, "Total kW");
        dataSet.setLineWidth(5);
        LineData lineData = new LineData(dataSet);
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void displayBarChart(String month) {
        barChart = findViewById(R.id.bar_chart);
        lineChart = findViewById(R.id.line_chart);
        barChart.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.GONE);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        BarDataSet dataSet = new BarDataSet(barEntries, "Power in KW");
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate();
        barData.setBarWidth(0.9f);
        barChart.setData(barData);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.invalidate(); // refresh
    }

    private void setupDropdown(final HashMap<String, HashMap<String, Double>> powerByTimeAndMonth) {
//        final Spinner dropdown = findViewById(R.id.spinner);

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(dropdown);

            // Set popupWindow height to 500px
            popupWindow.setHeight(1000);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }

        // Initializing a new String Array
        Resources res = getResources();

        // Create a List from String Array elements
        List<String> months = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.months)));

        // Create a ArrayAdapter from List
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, months);

        // Populate spinner with items from ArrayAdapter
        dropdown.setAdapter(arrayAdapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String month = dropdown.getSelectedItem().toString();
                generateChartData(month, powerByTimeAndMonth);
                if (showLineChart)
                    displayLineChart(month);
                else
                    displayBarChart(month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
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
            Iterator it = result.entrySet().iterator();
            Double annualPower = 0d;
            final Double[] monthlyPower = new Double[12];
            Arrays.fill(monthlyPower, 0d);
            final HashMap<String, HashMap<String, Double>> powerByTimeAndMonth = new HashMap<>();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
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
                        HashMap<String, Double> newMap = powerByTimeAndMonth.get(months[monthInt]);
                        if (newMap == null) {
                            HashMap<String, Double> n = new HashMap<>();
                            n.put("" + hourInt, power);
                            powerByTimeAndMonth.put(months[monthInt], n);
                        } else {
                            if (newMap.get("" + hourInt) == null) {
                                powerByTimeAndMonth.get(months[monthInt]).put("" + hourInt, power);
                            } else {
                                powerByTimeAndMonth.get(months[monthInt]).put("" + hourInt, newMap.get("" + hourInt) + power);
                            }
                        }
                    } catch (NumberFormatException e) {
                        break;
                    }
                }
                it.remove(); // avoids a ConcurrentModificationException
            }
            HashMap<String, Double> t = new HashMap<>();
            t.put("0", annualPower);
            powerByTimeAndMonth.put("Annual", t);
            HashMap<String, Double> temp = new HashMap<>();
            for (int i = 0; i < monthlyPower.length; i++) {
                temp.put(months[i], monthlyPower[i]);
            }
            powerByTimeAndMonth.put("All", temp);
            powerMap = powerByTimeAndMonth;
            progressBar.setVisibility(View.GONE);
            //Setting up the toolbar
            initializeToolBar();
//            setupDropdown(powerByTimeAndMonth);
        }
    }
}

