package solarsitingucsc.smartsolarsiting.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
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
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

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

    private Location lastLocation;
    private double currLatitude;
    private double currLongitude;
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



    public DrawOnTop(Context context) {
        super(context);
        this.context = context;
        this.handler = new Handler();
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        sensors = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compassSensor = sensors.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroSensor = sensors.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        startSensors();
        startGPS();

        // get some camera parameters
        Camera camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        verticalFOV = params.getVerticalViewAngle();
        horizontalFOV = params.getHorizontalViewAngle();
        camera.release();

        // paint for text
        contentPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        contentPaint.setTextAlign(Align.LEFT);
        contentPaint.setTextSize(20);
        contentPaint.setColor(Color.RED);

        // paint for target
        targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        targetPaint.setColor(Color.GREEN);
    }

    private void startSensors() {
        isAccelAvailable = sensors.registerListener(this, accelSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        isCompassAvailable = sensors.registerListener(this, compassSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        isGyroAvailable = sensors.registerListener(this, gyroSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void startGPS() {
        Criteria criteria = new Criteria();
        // criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // while we want fine accuracy, it's unlikely to work indoors where we
        // do our testing. :)
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

        String best = locationManager.getBestProvider(criteria, true);

        Log.v(DEBUG_TAG, "Best provider: " + best);

        locationManager.requestLocationUpdates(best, 50, 0, this);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d(DEBUG_TAG, "onDraw");
        super.onDraw(canvas);

        // Draw something fixed (for now) over the camera view

        StringBuilder text = new StringBuilder(accelData).append("\n");
        text.append(compassData).append("\n");
        text.append(gyroData).append("\n");

        if (lastLocation != null) {

            text.append(
                    String.format("GPS = (%.3f, %.3f) @ (%.2f meters up)",
                            lastLocation.getLatitude(),
                            lastLocation.getLongitude(),
                            lastLocation.getAltitude())).append("\n");


            text.append(String.format("Bearing to MW: %.3f", 200.0f))
                    .append("\n");
        }

        // compute rotation matrix
        float rotation[] = new float[9];
        float identity[] = new float[9];
        if (lastAccelerometer != null && lastCompass != null && lastLocation != null) {
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

                text.append(
                        String.format("Orientation (%.3f, %.3f, %.3f)",
                                Math.toDegrees(orientation[0]), Math.toDegrees(orientation[1]), Math.toDegrees(orientation[2])))
                        .append("\n");


                // draw the target (if it's on the screen)
                canvas.save();
                // use roll for screen rotation
                canvas.rotate((float)(0.0f- Math.toDegrees(orientation[2])));


                if(!alreadyCalculated) {
                    currSunPosition = calculateCurrentSunPosition();

                    System.out.println(currSunPosition.toString());
                    alreadyCalculated = true;
                }
                float dx, dy;

                if(Math.toDegrees(orientation[0]) < 0){
                    dx = (float) ( (canvas.getWidth()/ horizontalFOV) * (Math.toDegrees(orientation[0])-(((float) currSunPosition.getNegativeAzimuth()))));   //AZIMUTH CORRECTION
                    dy = (float) ( (canvas.getHeight()/ verticalFOV) * (Math.toDegrees(orientation[1])-((float) currSunPosition.getElevationFromTheHorizon()*-1))) ;    //PITCH/ELEVATION CORRECTION

                } else{
                    dx = (float) ( (canvas.getWidth()/ horizontalFOV) * (Math.toDegrees(orientation[0])-((float) currSunPosition.getAzimuth())));   //AZIMUTH CORRECTION
                    dy = (float) ( (canvas.getHeight()/ verticalFOV) * (Math.toDegrees(orientation[1])-((float) currSunPosition.getElevationFromTheHorizon()*-1))) ;    //PITCH/ELEVATION CORRECTION

                }

                // wait to translate the dx so the horizon doesn't get pushed off
                canvas.translate(0.0f, 0.0f-dy);


                // make our line big enough to draw regardless of rotation and translation
                //canvas.drawLine(0f - canvas.getHeight(), canvas.getHeight()/2, canvas.getWidth()+canvas.getHeight(), canvas.getHeight()/2, targetPaint);

                // now translate the dx
                canvas.translate(0.0f-dx, 0.0f);

                // draw our point -- we've rotated and translated this to the right spot already
                canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, 30.0f, targetPaint);

                canvas.restore();

            }
        }

        canvas.save();
        canvas.translate(15.0f, 15.0f);
        StaticLayout textBox = new StaticLayout(text.toString(), contentPaint,
                480, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        textBox.draw(canvas);
        canvas.restore();
    }

    public AzimuthZenithAngle calculateCurrentSunPosition(){
        GregorianCalendar date = new GregorianCalendar();
        double deltaT = DeltaT.estimate(date);
        currLatitude = lastLocation.getLatitude();
        currLongitude = lastLocation.getLongitude();
        System.out.println(currLatitude);
        System.out.println(currLongitude);

        return Grena3.calculateSolarPosition(date, currLatitude, currLongitude, deltaT);
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


    //------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------

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
                accelData = msg.toString();
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroData = msg.toString();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                lastCompass = lowPass(event.values.clone(), lastCompass);
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
        startGPS();
    }

}
