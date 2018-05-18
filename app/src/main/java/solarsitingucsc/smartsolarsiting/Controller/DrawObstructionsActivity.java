package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import solarsitingucsc.smartsolarsiting.Model.ScreenshotUtils;
import solarsitingucsc.smartsolarsiting.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

import static android.content.ContentValues.TAG;


public class DrawObstructionsActivity extends AppCompatActivity {

    private String imageName, screenshotName;
    private double latitude, longitude;


    public Mat img=new Mat();
    public Mat result = new Mat();
    public Bitmap rotatedImage;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_obstructions);
        imageName = getIntent().getStringExtra("imageName");
        screenshotName = getIntent().getStringExtra("screenshotName");
        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        FileInputStream imageFis = null;
        try {
            imageFis = openFileInput(imageName);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap originalImage = BitmapFactory.decodeStream(imageFis,null,options);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        rotatedImage = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(),
                originalImage.getHeight(), matrix, true);

        ImageView imageView = findViewById(R.id.imageView);

        //Set image as background in the new activity
        Mat img = Imgcodecs.imread(screenshotName);
        Mat gray_mat=new Mat();
        Utils.bitmapToMat(rotatedImage, gray_mat, true);
        Utils.bitmapToMat(rotatedImage,img, true);
        Log.d(TAG, "hihihihih" + imageName);
        result = steptowatershed(img, gray_mat);
        Utils.matToBitmap(result, rotatedImage, true);
        Log.i(TAG, "all okay");
        imageView.setImageBitmap(rotatedImage);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageBitmap(rotatedImage);


        final FloatingActionButton fabDelete = findViewById(R.id.fabDelete);
        final FloatingActionButton fabConfirm = findViewById(R.id.fabConfirm);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fabConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabConfirm.setVisibility(View.GONE);
                fabDelete.setVisibility(View.GONE);
                FileInputStream imageFis = null;
                String ipScreenshot = takeScreenshot();
                try {
                    imageFis = openFileInput(ipScreenshot);
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap originalImage = BitmapFactory.decodeStream(imageFis,null,options);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);

                Bitmap ipImage = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(),
                        originalImage.getHeight());

                Context context = getBaseContext();
                Intent intent = new Intent(context, DisplayCalculationsActivity.class);
                intent.putExtra("imageName", imageName);
                intent.putExtra("screenshotName", screenshotName);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //send bitmap to DisplayCalculationsActivity
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                ipImage.compress(Bitmap.CompressFormat.PNG, 100, bs);
                intent.putExtra("byteArray", bs.toByteArray());
                context.startActivity(intent);
                fabConfirm.setVisibility(View.VISIBLE);
                fabDelete.setVisibility(View.VISIBLE);
//                fabConfirm.show();
//                fabDelete.show();
                finish();
            }
        });
    }

    public Mat steptowatershed(Mat img, Mat gray_mat)
    {
        Mat threeChannel = new Mat();
//        Mat gray_mat=new Mat();
        Imgproc.cvtColor(gray_mat,img,Imgproc.COLOR_RGBA2RGB);
        Log.d(TAG, "hihihihih" + Integer.toString(img.width()));
        Imgproc.cvtColor(img, threeChannel, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(threeChannel, threeChannel, 100, 255, Imgproc.THRESH_BINARY);

        Mat fg = new Mat(img.size(),CvType.CV_8U);
        Imgproc.erode(threeChannel,fg,new Mat());

        Mat bg = new Mat(img.size(),CvType.CV_8U);
        Imgproc.dilate(threeChannel,bg,new Mat());
        Imgproc.threshold(bg,bg,1, 128,Imgproc.THRESH_BINARY_INV);

        Mat markers = new Mat(img.size(),CvType.CV_8U, new Scalar(0));
        Core.add(fg, bg, markers);
        Mat result1=new Mat();
        WatershedSegmenter segmenter = new WatershedSegmenter();
        segmenter.setMarkers(markers);
        result1 = segmenter.process(img);
        return result1;
    }

    public class WatershedSegmenter
    {
        public Mat markers=new Mat();

        public void setMarkers(Mat markerImage)
        {

            markerImage.convertTo(markers, CvType.CV_32S);
        }

        public Mat process(Mat image)
        {
            Imgproc.watershed(image,markers);
            markers.convertTo(markers,CvType.CV_8U);
            return markers;
        }
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

    private static File getInternalOutputMediaFile(Context context) {
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) +
                "SCREENSHOT";
        File file = new File(context.getFilesDir(), timeStamp);
        return file;
    }
}