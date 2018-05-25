package solarsitingucsc.smartsolarsiting.Controller;

//import android.graphics.Camera;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.hardware.Camera;
import android.widget.Toast;

import org.opencv.core.Mat;

import java.io.File;
import java.util.ArrayList;
import org.opencv.android.Utils;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.util.List;

import solarsitingucsc.smartsolarsiting.R;

import static solarsitingucsc.smartsolarsiting.Controller.NativePanorama.processPanorama;


public class PanoramaActivity extends AppCompatActivity {

    //objects to work with layout
    private Button captureBtn, saveBtn; // used to interact with capture and save Button in UI
    private SurfaceView mSurfaceView, mSurfaceViewOnTop; // used to display the camera frame in UI
    private Camera mCam;
    private boolean isPreview; // Is the camera frame displaying?
    private boolean safeToTakePicture = true; // Is it safe to capture a picture?
    private List<Mat> listImage = new ArrayList<>();

    ProgressDialog ringProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama);

        // listeners for capture and save button
        View.OnClickListener captureOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCam != null && safeToTakePicture){
                    // set the flag to false so we don't take two picture at a same time
                    safeToTakePicture = false;
                    mCam.takePicture(null, null, jpegCallback);
                }
            }
        };

        View.OnClickListener saveOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(imageProcessingRunnable);
                thread.start();
            }
        };

        isPreview = false;
        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback(mSurfaceCallback);
        mSurfaceViewOnTop = (SurfaceView)findViewById(R.id.surfaceViewOnTop);
        mSurfaceViewOnTop.setZOrderOnTop(true); // necessary
        mSurfaceViewOnTop.getHolder().setFormat(PixelFormat.TRANSPARENT);
        captureBtn = (Button) findViewById(R.id.capture);
        captureBtn.setOnClickListener(captureOnClickListener);
        saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(saveOnClickListener);


    } //end of on create

    // Code below is for saveOnClickListener
    // show a processing dialog at the start of the process
    private Runnable imageProcessingRunnable = new Runnable() {
        @Override
        public void run() {
            showProcessingDialog();

            try {
                // Create a long array to store all image address
                int elems= listImage.size();
                long[] tempobjadr = new long[elems];
                for (int i=0;i<elems;i++){
                    tempobjadr[i]= listImage.get(i).getNativeObjAddr();
                }
                // Create a Mat to store the final panorama image
                Mat result = new Mat();
                // Call the OpenCV C++ Code to perform stitching process
                NativePanorama.processPanorama(tempobjadr, result.getNativeObjAddr());
                // Save the image to external storage
                File sdcard = Environment.getExternalStorageDirectory();
                final String fileName = sdcard.getAbsolutePath() + "/opencv_" +
                        System.currentTimeMillis() + ".png";
                Imgcodecs.imwrite(fileName, result);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "File saved at: " +
                                fileName, Toast.LENGTH_LONG).show();
                    }
                });
                listImage.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }

            closeProcessingDialog();
        }
    };


    private void showProcessingDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCam.stopPreview();
                ringProgressDialog = ProgressDialog.show(PanoramaActivity.this, "",
                        "Panorama", true);
                ringProgressDialog.setCancelable(false);
            }
        });
    }

    private void closeProcessingDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCam.startPreview();
                ringProgressDialog.dismiss();
            }
        });
    }

    // code below is for CaptureOnClickListener
    //called when the camera saves the picture as a JPEG
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // decode the byte array to a bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            // Rotate the picture to fit portrait mode
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);

            // TODO: Save the image to a List to pass them to OpenCV method
            Mat mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);
            listImage.add(mat);


            Canvas canvas = null;
            try {
                canvas = mSurfaceViewOnTop.getHolder().lockCanvas(null);
                synchronized (mSurfaceViewOnTop.getHolder()) {
                    // Clear canvas
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    // Scale the image to fit the SurfaceView
                    float scale = 1.0f * mSurfaceView.getHeight() /
                            bitmap.getHeight();
                    Bitmap scaleImage = Bitmap.createScaledBitmap(bitmap, (int)
                            (scale * bitmap.getWidth()), mSurfaceView.getHeight() , false);
                    Paint paint = new Paint();
                    // Set the opacity of the image
                    paint.setAlpha(200);
                    // Draw the image with an offset so we only see one third of image.
                    canvas.drawBitmap(scaleImage, -scaleImage.getWidth() * 2 / 3, 0, paint);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    mSurfaceViewOnTop.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            // Start preview the camera again and set the take picture flag to true
            mCam.startPreview();
            safeToTakePicture = true;
        }
    };



    //helps with displaying previous panorama to help take a more accurate picture
    SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback(){
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
            // Tell the camera to display the frame on this surfaceview
                mCam.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("SurfaceHolderErrorTag", "SurfaceHolder not working");
            }
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // Get the default parameters for camera
            Camera.Parameters myParameters = mCam.getParameters();
            // Select the best preview size
            Camera.Size myBestSize = getBestPreviewSize( myParameters );
            if(myBestSize != null){
                // Set the preview Size
                myParameters.setPreviewSize(myBestSize.width,
                        myBestSize.height);
                 // Set the parameters to the camera
                mCam.setParameters(myParameters);
                // Rotate the display frame 90 degree to view in portrait mode
                mCam.setDisplayOrientation(90);
                // Start the preview
                mCam.startPreview();
                isPreview = true;
            }
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    //function to get the preview size that has the biggest number of pixels
    private Camera.Size getBestPreviewSize(Camera.Parameters parameters){
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        bestSize = sizeList.get(0);
        for(int i = 1; i < sizeList.size(); i++){
            if((sizeList.get(i).width * sizeList.get(i).height) >
                    (bestSize.width * bestSize.height)){
                bestSize = sizeList.get(i);
            }
        }
        return bestSize;
    }



    @Override
    protected void onResume() {
        super.onResume();

        mCam = Camera.open(0); // 0 for back camera

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isPreview){
            mCam.stopPreview();
        }
        mCam.release();
        mCam = null;
        isPreview = false;
    }


} //end of panoramaActivity
