package solarsitingucsc.smartsolarsiting.View;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import java.text.DateFormatSymbols;
import java.util.GregorianCalendar;

import solarsitingucsc.smartsolarsiting.Model.AzimuthZenithAngle;
import solarsitingucsc.smartsolarsiting.Model.DeltaT;
import solarsitingucsc.smartsolarsiting.Model.Grena3;

public class DrawOnTop extends View implements SensorEventListener, LocationListener {

    public static final String DEBUG_TAG = "DrawOnTop Log";

    private final Context context;
    private Handler handler;

    String accelData = "Accelerometer Data";
    String compassData = "Compass Data";
    String gyroData = "Gyro Data";

    private LocationManager locationManager = null;
    private SensorManager sensors = null;
    private AzimuthZenithAngle[][] yearAveragePositionArray;
    private Location lastLocation;
    private float[] lastAccelerometer;
    private float[] lastCompass;

    private float verticalFOV;
    private float horizontalFOV;
    private AzimuthZenithAngle currSunPosition = null;
    private boolean alreadyCalculated = false;

    private boolean isAccelAvailable;
    private boolean isCompassAvailable;
    private boolean isGyroAvailable;
    private Sensor accelSensor;
    private Sensor compassSensor;
    private Sensor gyroSensor;

    private TextPaint contentPaint;

    private Paint targetPaint;
    private Paint timePaint;
    private Criteria criteria;

    private Rect r = new Rect();

    public DrawOnTop(Context context) {
        super(context);
        this.context = context;
        this.handler = new Handler();
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        yearAveragePositionArray = new AzimuthZenithAngle[12][];

        initializeSensors();
        startSensors();
        startGPS(locationManager.getBestProvider(criteria, true));

        initializeCameraParamters();
        initializePaints();
    }

    private void initializeSensors() {
        sensors = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compassSensor = sensors.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroSensor = sensors.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private void initializeCameraParamters() {
        // get some camera parameters
        Camera camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        verticalFOV = params.getVerticalViewAngle();
        horizontalFOV = params.getHorizontalViewAngle();
        camera.release();
    }

    private void initializePaints() {
        // paint for text
        contentPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        contentPaint.setTextAlign(Align.LEFT);
        contentPaint.setTextSize(40);
        contentPaint.setColor(Color.RED);

        // paint for target
        targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        targetPaint.setColor(Color.GREEN);
        targetPaint.setTextSize(100);

        //paint for times
        timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timePaint.setColor(Color.BLACK);
        timePaint.setTextSize(20);
    }

    private void startSensors() {
        isAccelAvailable = sensors.registerListener(this, accelSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        isCompassAvailable = sensors.registerListener(this, compassSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        isGyroAvailable = sensors.registerListener(this, gyroSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void startGPS(String bestProvider) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 50, 0, this);
        lastLocation = locationManager.getLastKnownLocation(bestProvider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d(DEBUG_TAG, "onDraw");
        super.onDraw(canvas);

        // Draw something fixed (for now) over the camera view
        StringBuilder text = buildDebugInformation();

        // compute rotation matrix
        float rotation[] = new float[9];
        float identity[] = new float[9];
        if (lastAccelerometer != null && lastCompass != null) {
            boolean gotRotation = SensorManager.getRotationMatrix(rotation,
                    identity, lastAccelerometer, lastCompass);
            if (gotRotation) {
                float cameraRotation[] = new float[9];

                // remap such that the camera is pointing straight down the Y
                // axis
                SensorManager.remapCoordinateSystem(rotation,
                        SensorManager.AXIS_X, SensorManager.AXIS_Z,
                        cameraRotation);

                // orientation vector
                float orientation[] = new float[3];
                SensorManager.getOrientation(cameraRotation, orientation);

                text.append(String.format("Orientation (%.3f, %.3f, %.3f)",
                            Math.toDegrees(orientation[0]), Math.toDegrees(orientation[1]),
                            Math.toDegrees(orientation[2]))).append("\n");

                // draw the target (if it's on the screen)
                canvas.save();

                if (lastLocation == null)
                    startGPS(LocationManager.GPS_PROVIDER);
                if (lastLocation == null)
                    startGPS(LocationManager.NETWORK_PROVIDER);

                if(!alreadyCalculated && lastLocation != null) {
                    //currSunPosition = calculateCurrentSunPosition();
                    yearAveragePositionArray = Grena3.calculateWholeYear(lastLocation.getLatitude(),
                            lastLocation.getLongitude());
                    alreadyCalculated = true;
                }

                if (alreadyCalculated && yearAveragePositionArray[0] != null) {
                    for (int i = 0; i < yearAveragePositionArray.length; i++) {
                        drawMultipleCircles(canvas, yearAveragePositionArray[i], orientation, i);
                    }
                }
            }
        }

        canvas.save();
        canvas.translate(15.0f, 15.0f);
        if (lastLocation == null) {
            String msg = "Searching for your location...";
            drawCenter(canvas, targetPaint, msg);
        }
        else {
            StaticLayout textBox = new StaticLayout(text.toString(), contentPaint,
                    480, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            textBox.draw(canvas);
        }
        canvas.restore();
    }

    private void drawCenter(Canvas canvas, Paint paint, String text) {
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }

    // Calling this method will doDraw Draw the patterns drawn on their own canvas upper
    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }

    public StringBuilder buildDebugInformation(){
        StringBuilder text = new StringBuilder(accelData).append("\n");
        text.append(compassData).append("\n");
        text.append(gyroData).append("\n");

        if (lastLocation != null) {

            text.append(
                    String.format("GPS = (%.3f, %.3f) @ (%.2f meters up)",
                            lastLocation.getLatitude(),
                            lastLocation.getLongitude(),
                            lastLocation.getAltitude())).append("\n");


        }
        return text;
    }

    public void drawMultipleCircles(Canvas canvas, AzimuthZenithAngle[] averageArray,
                                    float[] orientation, int month) {
        //Grena3.printAngleArray(averageArray);
        float dx, dy;
        boolean foundHighestPoint = false;

        for (int i = 0; i < averageArray.length; i++) {
            canvas.save();
            if(Math.toDegrees(orientation[0]) < 0) {
                //AZIMUTH CORRECTION
                dx = (float) ( (canvas.getWidth()/ horizontalFOV) *
                        (Math.toDegrees(orientation[0])-(((float)
                                averageArray[i].getNegativeAzimuth()))));
                //PITCH/ELEVATION CORRECTION
                dy = (float) ( (canvas.getHeight()/ verticalFOV) *
                        (Math.toDegrees(orientation[1])-((float)
                                averageArray[i].getElevationFromTheHorizon()*-1)));

            }
            else {
                //AZIMUTH CORRECTION
                dx = (float) ( (canvas.getWidth()/ horizontalFOV) *
                        (Math.toDegrees(orientation[0])-((float) averageArray[i].getAzimuth())));
                //PITCH/ELEVATION CORRECTION
                dy = (float) ( (canvas.getHeight()/ verticalFOV) *
                        (Math.toDegrees(orientation[1])-((float)
                                averageArray[i].getElevationFromTheHorizon()*-1)));
            }

            // wait to translate the dx so the horizon doesn't get pushed off
            canvas.translate(0.0f, 0.0f-dy);

            // now translate the dx
            canvas.translate(0.0f-dx, 0.0f);

            // draw our point -- we've rotated and translated this to the right spot already
            canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, 15.0f,
                    targetPaint);

            canvas.drawText(averageArray[i].getTime(), canvas.getWidth()/2 - 10f,
                    canvas.getHeight()/2 + 7.5f, timePaint);

            //write the name of the month at the highest point of elevation on the arc
            if (i != averageArray.length - 1 && averageArray[i].getElevationFromTheHorizon() >
                    averageArray[i + 1].getElevationFromTheHorizon() && !foundHighestPoint) {
                String text = new DateFormatSymbols().getMonths()[month];
                canvas.drawText(text, canvas.getWidth()/2, canvas.getHeight()/2, targetPaint);
                foundHighestPoint = true;
            }

            canvas.restore();
        }
    }

    public AzimuthZenithAngle calculateCurrentSunPosition(){
        GregorianCalendar date = new GregorianCalendar();
        double deltaT = DeltaT.estimate(date);
        return Grena3.calculateSolarPosition(date, 36.9, -122.03, deltaT);
    }

    //This is a low pass filter used to smooth the position of the dot on the screen
    static final float ALPHA = 0.1f;
    protected float[] lowPass(float[] input, float[] output){
        if(output == null) return input;

        for(int i=0; i<input.length; i++){
            output[i] = output[i] + ALPHA *(input[i] - output[i]);
        }
        return output;
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
        Log.d(DEBUG_TAG, "onAccuracyChanged");

    }

    public void onSensorChanged(SensorEvent event) {
        // Log.d(DEBUG_TAG, "onSensorChanged");

        StringBuilder msg = new StringBuilder(event.sensor.getName())
                .append(" ");
        for (float value : event.values) {
            msg.append("[").append(String.format("%.3f", value)).append("]");
        }

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                lastAccelerometer = lowPass(event.values.clone(), lastAccelerometer);
                //lastAccelerometer = event.values.clone();
                accelData = msg.toString();
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroData = msg.toString();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                lastCompass = lowPass(event.values.clone(), lastCompass);
                //lastCompass = event.values.clone();
                compassData = msg.toString();
                break;
        }

        this.invalidate();
    }

    public void onLocationChanged(Location location) {
        // store it off for use when we need it
        lastLocation = location;
    }

    public void onProviderDisabled(String provider) {
        // ...
    }

    public void onProviderEnabled(String provider) {
        // ...
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // ...
    }

    // this is not an override
    public void onPause() {
        locationManager.removeUpdates(this);
        sensors.unregisterListener(this);
    }

    // this is not an override
    public void onResume() {
        startSensors();
        startGPS(locationManager.getBestProvider(criteria, true));
    }
}
