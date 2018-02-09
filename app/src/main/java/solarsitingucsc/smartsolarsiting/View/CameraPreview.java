package solarsitingucsc.smartsolarsiting.View;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder mHolder;
    Camera mCamera;

    public CameraPreview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        mCamera = Camera.open();
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin the preview.
         Camera.Parameters parameters = mCamera.getParameters();
        // parameters.setPreviewSize(w, h);
        // mCamera.setParameters(parameters);
        List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
        Camera.Size size = allSizes.get(0); // get top size
        for (int i = 0; i < allSizes.size(); i++) {
            if (allSizes.get(i).width > size.width)
                size = allSizes.get(i);
        }
        //set max Picture Size
        parameters.setPictureSize(size.width, size.height);
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }
}
