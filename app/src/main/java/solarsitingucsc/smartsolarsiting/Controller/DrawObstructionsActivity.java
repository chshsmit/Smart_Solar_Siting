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

import static android.content.ContentValues.TAG;


public class DrawObstructionsActivity extends AppCompatActivity {

    private String imageName, screenshotName;
    private double latitude, longitude;

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
}