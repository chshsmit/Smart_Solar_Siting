package solarsitingucsc.smartsolarsiting.Controller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.view.SurfaceHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import solarsitingucsc.smartsolarsiting.Model.ScreenshotUtils;
import solarsitingucsc.smartsolarsiting.R;
import solarsitingucsc.smartsolarsiting.View.DrawOnTop;
import solarsitingucsc.smartsolarsiting.View.CameraPreview;

import android.support.design.widget.FloatingActionButton;



import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class MainActivity extends Activity {

    private CameraPreview mCameraPreview;
    private DrawOnTop mDraw;
    private FrameLayout cameraPreviewPane;
    private ProgressBar progressBar;


    static{
        System.loadLibrary("opencv_java3");
        System.loadLibrary("MyLib");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting orientation of the view based on shared prefs
        setOrientation();

        checkForPermissionsAndOpenCamera();
        if (progressBar == null)
            progressBar = findViewById(R.id.progressBar);
        else
            progressBar.setVisibility(View.GONE);
    }

    private void setOrientation(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean land_orientation = prefs.getBoolean("land_orient", false);
        if(land_orientation){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }



    /**
     * Process the result from the asking of permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (grantResults.length != 0) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    showAlert();
                    return;
                }
            }
            openCamera();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (progressBar != null )
            progressBar.setVisibility(View.GONE);
    }

    /**
     * Display the camera view
     */
    private void openCamera() {
        setContentView(R.layout.activity_main);
        cameraPreviewPane = (FrameLayout) findViewById(R.id.camera_preview_pane);
        mCameraPreview = new CameraPreview(getApplicationContext(), this);
        cameraPreviewPane.addView(mCameraPreview);
        mDraw = new DrawOnTop(getApplicationContext());
        cameraPreviewPane.addView(mDraw);

        configureCaptureButton();
    }

    /**
     * Display an alert dialog telling the user the permissions are needed
     */
    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Your camera and location permissions need to be granted" +
                " for this app to work.\nPlease enable them through:" +
                " Settings > Apps > Smart Solar Siting > Permissions, and restart the " +
                "app");
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    /**
     * Ask for the permissions that we need, if we already have camera permissions, open the camera
     */
    private void checkForPermissionsAndOpenCamera() {
        String[] permissions = {Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        Boolean permissionsGranted = true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = false;
            }

        }

        if (permissionsGranted)
            openCamera();
        else
            ActivityCompat.requestPermissions(this, permissions, 1);
    }

    private void configureCaptureButton() {
        FloatingActionButton captureButton = (FloatingActionButton) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressBar != null )
                    progressBar.setVisibility(View.VISIBLE);
                String path = takeScreenshot();
                Location userLocation = mDraw.lastLocation;

                mCameraPreview.latitude = userLocation.getLatitude();
                mCameraPreview.longitude = userLocation.getLongitude();

                mCameraPreview.setScreenshotName(path);
                mCameraPreview.takePicture();
            }
        });
    }

    private String takeScreenshot() {
        try {
            File screenshotFile = getInternalOutputMediaFile(this);
            ScreenshotUtils.savePic(ScreenshotUtils.takeScreenShot(this), screenshotFile);

            //Save screenshot to phone's images
//            File screenshotFile2 = getExternalOutputMediaFile(MEDIA_TYPE_IMAGE);
//            ScreenshotUtils.savePic(ScreenshotUtils.takeScreenShot(this), screenshotFile2);

            return screenshotFile.getName();
        } catch (NullPointerException ignored) {
            ignored.printStackTrace();
        }
        return "";
    }

    /**
     * Create a File for saving an image or video in internal memory
     */
    private static File getInternalOutputMediaFile(Context context) {
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) +
                "SCREENSHOT";
        File file = new File(context.getFilesDir(), timeStamp);
        return file;
    }

//    /**
//     * Create a File for saving an image or video in external storage
//     */
//    private static File getExternalOutputMediaFile(int type){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "Smart Solar Siting");
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                Log.d("Smart Solar Siting", "failed to create directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        if (type == MEDIA_TYPE_IMAGE){
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_"+ timeStamp + ".jpg");
//        } else if(type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "VID_"+ timeStamp + ".mp4");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }




    /*
    * Function that allows button to switch to panorama activity
    * */
    public void configurePanoramaButton() {
        Button panoButton = (Button) findViewById(R.id.panoramaButton);
        panoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getBaseContext();
                Intent intent = new Intent(context, PanoramaActivity.class);

                mCameraPreview.surfaceDestroyed(mCameraPreview.getHolder());

                startActivity(intent);
                //finish();


            }

        });

    }

}
