package solarsitingucsc.smartsolarsiting.Controller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import solarsitingucsc.smartsolarsiting.R;
import solarsitingucsc.smartsolarsiting.View.DrawOnTop;
import solarsitingucsc.smartsolarsiting.View.CameraPreview;

public class MainActivity extends Activity {

    private CameraPreview mCameraPreview;
    private DrawOnTop mDraw;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        checkForPermissionsAndOpenCamera();



        //Remove title and notification bars
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        openCamera();
        //function to change camera view to calculations view
        configureSeeCalcButton();
//
//        checkForPermissions();
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
        FrameLayout cameraPreviewPane = (FrameLayout) findViewById(R.id.camera_preview_pane);
        mCameraPreview = new CameraPreview(getApplicationContext(), this);
        cameraPreviewPane.addView(mCameraPreview);
        mDraw = new DrawOnTop(getApplicationContext());
        cameraPreviewPane.addView(mDraw);
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
                Manifest.permission.INTERNET};

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
    private void configureSeeCalcButton(){
        Button seeCalcButton = (Button) findViewById(R.id.seeCalcButton);
        seeCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Display_calc.class));
            }
        } );

    }


}
