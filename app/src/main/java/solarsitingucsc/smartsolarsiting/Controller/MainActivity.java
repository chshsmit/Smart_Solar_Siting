package solarsitingucsc.smartsolarsiting.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import solarsitingucsc.smartsolarsiting.View.DrawOnTop;
import solarsitingucsc.smartsolarsiting.View.CameraPreview;

public class MainActivity extends Activity {

    private CameraPreview mCameraPreview;
    private DrawOnTop mDraw;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Remove title and notification bars
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mCameraPreview = new CameraPreview(this);
        mDraw = new DrawOnTop(this);

        checkForPermissions();
    }

    /**
     * Process the result from the asking of permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                showAlert();
                break;
            }
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    /**
     * Display the camera view
     */
    private void openCamera() {
        setContentView(mCameraPreview);
        addContentView(mDraw,
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    /**
     * Display an alert dialog telling the user the permissions are needed
     */
    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Your camera and location permissions need to be granted" +
                " for this app to work. Please enable them through:" +
                "\n Settings > Apps > Smart Solar Siting > Permissions, and restart the " +
                "app");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Ask for the permissions that we need, if we already have camera permissions, open the camera
     */
    private void checkForPermissions() {
        String[] permissions = {Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
        ActivityCompat.requestPermissions(this, permissions, 1);
    }
}
