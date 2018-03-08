package solarsitingucsc.smartsolarsiting.Controller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import solarsitingucsc.smartsolarsiting.Model.ScreenshotUtils;
import solarsitingucsc.smartsolarsiting.R;
import solarsitingucsc.smartsolarsiting.View.DrawOnTop;
import solarsitingucsc.smartsolarsiting.View.CameraPreview;

public class MainActivity extends Activity {

    private CameraPreview mCameraPreview;
    private DrawOnTop mDraw;
    private FrameLayout cameraPreviewPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        checkForPermissionsAndOpenCamera();
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

        cameraPreviewPane.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeLeft() {
                startActivity(new Intent(MainActivity.this,
                        DisplayCalculationsActivity.class));
            }
        });

        configureSeeCalcButton();
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

    /**
     *  Button to change camera view to calculations view
     */
    private void configureSeeCalcButton() {
        Button seeCalcButton = (Button) findViewById(R.id.button_see_calculations);
        seeCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,
                        DisplayCalculationsActivity.class));
            }
        } );
    }

    private void configureCaptureButton() {
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeScreenshot();
                mCameraPreview.takePicture();
            }
        });
    }

    private void takeScreenshot() {
        try {
            File cacheDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Smart Solar Siting");

            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Smart Solar Siting") + "/" + timeStamp + ".jpg";

            ScreenshotUtils.savePic(ScreenshotUtils.takeScreenShot(this), path);

            Toast.makeText(this, "Screenshot saved", Toast.LENGTH_LONG).show();
        } catch (NullPointerException ignored) {
            ignored.printStackTrace();
        }
    }

//    private void configureCaptureButton() {
//        Button captureButton = (Button) findViewById(R.id.button_capture);
//        captureButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
////                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
////                }
//
//                // get an image from the camera
//                camera.takePicture(null, null, mPicture);
//            }
//        });
//    }
}
