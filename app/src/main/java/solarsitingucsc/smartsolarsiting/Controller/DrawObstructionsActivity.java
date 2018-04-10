package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
        Bitmap originalImage = BitmapFactory.decodeStream(imageFis);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap rotatedImage = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(),
                originalImage.getHeight(), matrix, true);

        ImageView imageView = findViewById(R.id.imageView);
        //Set image as background in the new activity
        Mat img = Imgcodecs.imread(screenshotName);
        Utils.bitmapToMat(originalImage,img, true);
        Log.d(TAG, "hihihihih" + imageName);
        result = steptowatershed(img);
//        Utils.matToBitmap(result, rotatedImage, true);
//        Log.i(TAG, "all okay");
        imageView.setImageBitmap(rotatedImage);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageBitmap(rotatedImage);

        FloatingActionButton fabDelete = findViewById(R.id.fabDelete);
        FloatingActionButton fabConfirm = findViewById(R.id.fabConfirm);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fabConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getBaseContext();
                Intent intent = new Intent(context, DisplayCalculationsActivity.class);
                intent.putExtra("imageName", imageName);
                intent.putExtra("screenshotName", screenshotName);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                finish();
            }
        });
    }

    public Mat steptowatershed(Mat img)
    {
        Mat threeChannel = new Mat();
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

            markerImage.convertTo(markers, CvType.CV_32SC1);
        }

        public Mat process(Mat image)
        {
            Imgproc.watershed(image,markers);
            markers.convertTo(markers,CvType.CV_8U);
            return markers;
        }
    }
}