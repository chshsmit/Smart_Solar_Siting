package solarsitingucsc.smartsolarsiting.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import solarsitingucsc.smartsolarsiting.Controller.DrawObstructionsActivity;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {


    public static final String DEBUG_TAG = "CameraPreview Log";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private String screenshotName;
    private Activity mActivity;
    public double latitude, longitude;

    public CameraPreview(Context context, Activity activity) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mActivity = activity;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        screenshotName = "";
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        mCamera = Camera.open();

        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch(rotation){
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotate);
        mCamera.setParameters(params);

        mCamera.setDisplayOrientation((info.orientation - degrees + 360) % 360);
        //mCamera.setDisplayOrientation(90);
        //mCamera.setDisplayOrientation(270);

        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        Log.d(DEBUG_TAG, "surfaceDestroyed");
        mCamera.stopPreview();
        mCamera.release();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(DEBUG_TAG, "surfaceChnaged");

        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> prevSizes = params.getSupportedPreviewSizes();
        for(Camera.Size s : prevSizes){
            if((s.height <= height) && (s.width <= width)){
                params.setPreviewSize(s.width, s.height);
                break;
            }
        }

        mCamera.setParameters(params);
        mCamera.startPreview();
    }

    public void takePicture() {
        mCamera.takePicture(null, null, mPicture);
    }


    public void setScreenshotName(String screenshotName) {
        this.screenshotName = screenshotName;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        final Context finalContext = getContext();

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getInternalOutputMediaFile(MEDIA_TYPE_IMAGE, finalContext);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            Intent intent = new Intent(finalContext, DrawObstructionsActivity.class);
            intent.putExtra("imageName", pictureFile.getName());
            intent.putExtra("screenshotName", screenshotName);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finalContext.startActivity(intent);
        }
    };

    /**
     * Create a File for saving an image or video in internal memory
     */
    private static File getInternalOutputMediaFile(int type, Context context) {

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(context.getFilesDir(), timeStamp);
        return file;
    }

    /**
     * Create a File for saving an image or video in external storage
     */
    private static File getExternalOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Smart Solar Siting");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Smart Solar Siting", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
