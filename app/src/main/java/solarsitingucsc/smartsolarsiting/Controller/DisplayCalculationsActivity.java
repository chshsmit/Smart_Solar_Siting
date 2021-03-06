package solarsitingucsc.smartsolarsiting.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.res.Resources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.google.api.services.vision.v1.model.BoundingPoly;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;
import com.google.api.services.vision.v1.model.Vertex;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.CDL;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import solarsitingucsc.smartsolarsiting.Model.SolarSiting;
import solarsitingucsc.smartsolarsiting.R;

import static android.content.ContentValues.TAG;

public class DisplayCalculationsActivity extends AppCompatActivity implements
        PopupMenu.OnMenuItemClickListener {

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
    private List<EntityAnnotation> textAnnotations;
    private int textCount = 0;
    private List<String> textDescriptions = new ArrayList<String>();
    private Set<String> numCheck = new HashSet<String>();
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
                (HashMap<String, HashMap<String, Double>>) getIntent().getSerializableExtra("powerList");

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
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap originalImage = BitmapFactory.decodeStream(imageFis);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap screenshot = BitmapFactory.decodeStream(screenshotFis);
        thumbnail = makeThumbnail(originalImage, matrix);
        if(getIntent().hasExtra("byteArray")) {
            Log.d(TAG, "FOUNDBYTEARRAY");
            b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent()
                            .getByteArrayExtra("byteArray").length, options);

        }
//        dots = (ImageView) findViewById(R.id.dots);
//        dots.setImageBitmap(screenshot);
//        dots.setScaleType(ImageView.ScaleType.FIT_XY);
//        dots.setImageBitmap(screenshot);

        b = Bitmap.createScaledBitmap(b,screenshot.getWidth(),screenshot.getHeight(),true);
//        watershed = (ImageView) findViewById(R.id.watershed);
//        watershed.setImageBitmap(b);
//        watershed.setScaleType(ImageView.ScaleType.FIT_XY);
//        watershed.setImageBitmap(b);
        Log.d(TAG, "watershed width x height: " + b.getWidth() +"x" + b.getHeight());
        Log.d(TAG, "dots width x height: " + screenshot.getWidth() + "x" + screenshot.getHeight());
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
            View menuViewItem = findViewById(R.id.share);
            PopupMenu exportMenu = new PopupMenu(this, menuViewItem);
            exportMenu.setOnMenuItemClickListener(this);
            exportMenu.inflate(R.menu.export_calc_menu);
            exportMenu.show();
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
    //Export popup menu functions
    //----------------------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_json:
                exportJSON();
                return true;

            case R.id.export_csv:
                exportCSV();
                return true;

            case R.id.export_jpeg:
                exportJPEG();
                return true;

            default:
                return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void exportJSON() {
        System.out.println("JSON Exporting");
        String text = dropdown.getSelectedItem().toString();
        Gson gsonObj = new Gson();
        String jsonStr = gsonObj.toJson(powerMap);
        File file = new File("data/data/solarsitingucsc.smartsolarsiting/export.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (Writer writer = new FileWriter("data/data/solarsitingucsc.smartsolarsiting/export.json")) {
            writer.append(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri path = FileProvider.getUriForFile(getApplicationContext(),
                getString(R.string.file_provider_authority),
                file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_STREAM, path);
        intent.setType("*/*");
        startActivity(Intent.createChooser(intent, "Share"));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void exportCSV() {
        System.out.println("CSV Exporting");
        String text = dropdown.getSelectedItem().toString();
        File file = new File("data/data/solarsitingucsc.smartsolarsiting/export.csv");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (Writer writer = new FileWriter("data/data/solarsitingucsc.smartsolarsiting/export.csv")) {
            String eol = System.getProperty("line.separator");
            String header = "Time, " + Arrays.toString(powerMap.keySet().toArray()).replaceAll("\\[(.*?)\\]", "$1").replace("All, ", "").replace("Annual, ", "");
            writer.append(header).append(eol);
            StringBuilder totals = new StringBuilder("Totals,");
            String[] times = new String[]{"6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00"};
            for (String key : (powerMap.keySet().toArray(new String[0]))) {
                if (!key.equals("All") && !key.equals("Annual"))
                    totals.append(powerMap.get("All").get(key)).append(",");
            }
            StringBuilder row = new StringBuilder();
            for (String time : times) {
                row.append(time).append(",");
                for (String key : powerMap.keySet()) {
                    if (!key.equals("All") && !key.equals("Annual")) {
                        HashMap<String, Double> vals = powerMap.get(key);
                        Double val;
                        try {
                            val = vals.get(time.substring(0, time.indexOf(":")));
                        } catch (ClassCastException e) {
                            val = 0d;
                        }
                        if (val == null)
                            row.append("0,");
                        else
                            row.append(val.toString()).append(",");
                    }
                }
                writer.append(row.toString()).append(eol);
                row = new StringBuilder();
            }
            writer.append(eol).append(eol).append(totals.toString());
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        Uri path = FileProvider.getUriForFile(getApplicationContext(),
                getString(R.string.file_provider_authority),
                file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_STREAM, path);
        intent.setType("*/*");
        startActivity(Intent.createChooser(intent, "Share"));
    }


    private void exportJPEG() {
        System.out.println("JPEG Exporting");
        String text = dropdown.getSelectedItem().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        Bitmap bitmap = lineChart.getChartBitmap();
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
        Uri bitmapUri = Uri.parse(bitmapPath);
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        if (text.equals("All"))
            text += " months";
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("*/*");
        startActivity(Intent.createChooser(intent, "Share"));
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

    private double getPowerForMonthAndHour(int month, int hour, List<List<Vertex>> vertices) {
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
            numCheck.add(hours[i-1]);
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
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
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
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
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
                textAnnotations = batchResponse.getResponses().get(0)
                        .getTextAnnotations();
                if(textAnnotations!=null) {
                    for (int i = 0; i < textAnnotations.size() - 1; i++) {
                        Log.d(TAG, "TA Element: " + Integer.toString(i) + " " + textAnnotations.get(i).getDescription());
//                    EntityAnnotation point = textAnnotations.get(i);

                        BoundingPoly textBox = textAnnotations.get(i).getBoundingPoly();
//                        Log.d(TAG, "size: " + textBox.getVertices().get(0).getX()+" "+textBox.getVertices().get(0).getY()
//                        + " "+ textBox.getVertices().get(2).getX()+" "+textBox.getVertices().get(2).getY());
                        String TA = textAnnotations.get(i).getDescription();
                        String desc = analyseString(TA);
                        Log.d(TAG, "actual string: " + desc);
                        if (desc.length() == 5) {
//                            Log.d(TAG, desc);
                            textCount++;
                            String month = desc.substring(0, 2);
                            textDescriptions.add(desc);
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
                }
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
//            Log.d(TAG, "RESPONSE: " + response);
            Map<String, Integer> result = translateResponseToMap(response);
            Iterator it = result.entrySet().iterator();
            Double annualPower = 0d;
            final Double[] monthlyPower = new Double[12];
            int[] ipMonthlyPower = new int[12];
            int ipPower = 0;
            Arrays.fill(monthlyPower, 0d);
            final HashMap<String, HashMap<String, Double>> powerByTimeAndMonth = new HashMap<>();
//            while (it.hasNext()) {
            for(int j=0;j<textDescriptions.size();j++) {
//                Map.Entry pair = (Map.Entry) it.next();
//                String key = (String) pair.getKey();
//                int value = (int) pair.getValue();
//                int dashIndex = key.indexOf("-");
//                String month = "", hour = "";
//                if (dashIndex != -1) {
//                    month = key.substring(0, dashIndex);
//                    hour = key.substring(dashIndex + 1, key.length());
//                }
//                Log.d(TAG, "key: " + key + " value: " + value);
//                for (int i = 0; i < value; i++) {
//                    try {
//                        int monthInt = Integer.parseInt(month), hourInt = Integer.parseInt(hour);
                String month = textDescriptions.get(j).substring(0, 2);
                String hour = textDescriptions.get(j).substring(3, 5);
                if (numCheck.contains(month) && numCheck.contains(hour)) {
                    int monthInt = Integer.parseInt(month);
                    int hourInt = Integer.parseInt(hour);
                    double power = 0;
                    if (monthInt == 0 && janV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(janV, janBox) && janBox < janV.size()) {
                            janBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, janV) / 4;
                    } else if (monthInt == 1 && febV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(febV, febBox) && febBox < febV.size()) {
                            febBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, febV) / 4;

                    } else if (monthInt == 2 && marV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(marV, marBox) && marBox < marV.size()) {
                            marBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, marV) / 4;
                    } else if (monthInt == 3 && aprV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(aprV, aprBox) && aprBox < aprV.size()) {
                            aprBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, aprV) / 4;
                    } else if (monthInt == 4 && mayV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(mayV, mayBox) && mayBox < mayV.size()) {
                            mayBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, mayV) / 4;
                    } else if (monthInt == 5 && junV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(junV, junBox) && junBox < junV.size()) {
                            junBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, junV) / 4;
                    } else if (monthInt == 6 && julV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(julV, julBox) && julBox < julV.size()) {
                            julBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, julV) / 4;
                    } else if (monthInt == 7 && augV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(augV, augBox) && augBox < augV.size()) {
                            augBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, augV) / 4;
                    } else if (monthInt == 8 && sepV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(sepV, sepBox) && sepBox < sepV.size()) {
                            sepBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, sepV) / 4;
                    } else if (monthInt == 9 && octV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(octV, octBox) && octBox < octV.size()) {
                            octBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, octV) / 4;
                    } else if (monthInt == 10 && novV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(novV, novBox) && novBox < novV.size()) {
                            novBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, novV) / 4;
                    } else if (monthInt == 11 && decV.size() != 0) {
                        Log.d(TAG, "Month: " + monthInt);
                        if (!processPixels(decV, decBox) && decBox < decV.size()) {
                            decBox++;
                        } else power = getPowerForMonthAndHour(monthInt, hourInt, decV) / 4;
                    }
                    double pow = getPower(monthInt, hourInt) / 4;
                    ipPower += getPower(monthInt, hourInt);
                    annualPower += power;
                    monthlyPower[monthInt] += power;
//                    Log.d(TAG, "monthly power: " + power);
                    ipMonthlyPower[monthInt] += pow;
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
//                    } catch (NumberFormatException e) {
//                        break;
//                    }
//                }
//                it.remove(); // avoids a ConcurrentModificationException
                }
            }
//            }
            HashMap<String, Double> t = new HashMap<>();
            t.put("0", annualPower);
            powerByTimeAndMonth.put("Annual", t);
            HashMap<String, Double> temp = new HashMap<>();
            for (int i = 0; i < monthlyPower.length; i++) {
                temp.put(months[i], monthlyPower[i]);
                Log.d(TAG, "month: " + i + " power w/ ip: " + monthlyPower[i] + " power w/o: " + ipMonthlyPower[i]);
            }
            powerByTimeAndMonth.put("All", temp);
            powerMap = powerByTimeAndMonth;
            progressBar.setVisibility(View.GONE);
            //Setting up the toolbar
            initializeToolBar();
//            setupDropdown(powerByTimeAndMonth);
        }
    }

    public boolean processPixels(List<List<Vertex>> vertices, int box)
    {
        if(box<vertices.size()) {
//            Log.d(TAG, "month size: " + vertices.size() + " verx: " + vertices.get(box).get(0).getX());
            if (vertices.get(box).get(0).getX() != null && vertices.get(box).get(0).getY() !=null) {
                int[] pixels = new int[400];
//            Log.d(TAG, "X: " + vertices.get(box).get(0).getX());
//                Log.d(TAG, "Y: " + vertices.get(box).get(0).getY());
                int x = vertices.get(box).get(0).getX();
                int y = vertices.get(box).get(0).getY();
                int width, height;
                if(b.getWidth()-x < 20) width = b.getWidth()-x;
                else width = 20;
                if(b.getHeight()-y < 20) height = b.getHeight()-y;
                else height = 20;

                b.getPixels(pixels, 0, 20, x, y, width, height);
                for (int k = 0; k < 400; k++) {
//                    for (int j = 0; j < 20; j++) {
                    if (Color.red(pixels[k]) <= 230 && Color.green(pixels[k]) <= 230 &&
                            Color.blue(pixels[k]) <= 230) {
                        Log.d(TAG, "NOT ADDED");
                        return false;
                    }
//                    }
                }
            }
        }
        return true;
    }

    private double getPower(int month, int hour) {
        //We increment by 24 hour time periods
//        Log.d(TAG, "month: " + Integer.toString(month));
        final int TWENTY_FOUR_HOURS = 24;
        double[] arrayForMonth = splitForMonth(month);
        double totalAcWatts = 0;
        for(int index = hour; index < arrayForMonth.length; index += TWENTY_FOUR_HOURS){
            totalAcWatts += arrayForMonth[index];
        }
        return totalAcWatts/1000;   //Converting from Watts to Kilowatts
    }

    private String analyseString(String text)
    {
        int dash = text.indexOf("-");
        text.replace("g","9");
        text.replace("B", "8");
        if(dash>1 && text.length()>4)
        {
            if(text.charAt(dash-1)<='9' && text.charAt(dash-1)>='0')
            {
                if(text.charAt(dash-2)<='9' && text.charAt(dash-2)>='0')
                {
                    if(text.charAt(dash+2)<='9' && text.charAt(dash+2)>='0')
                    {
                        if(text.charAt(dash+1)<='9' && text.charAt(dash+1)>='0')
                        {
                            return text.substring(0,dash+3);
                        }
                    }
                }
            }
        } else if(dash==1)
        {
            if(text.charAt(dash-1)<='9' && text.charAt(dash-1)>='3')
            {
                if(text.charAt(dash+2)<='9' && text.charAt(dash+2)>='0')
                {
                    if(text.charAt(dash+1)<='9' && text.charAt(dash+1)>='0')
                    {
                        return "1" + text.substring(0,dash+2);
                    }
                }
            }
        }
        return "";
    }


}

