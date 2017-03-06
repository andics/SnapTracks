package fusster.eu.snaptracks.views;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by ComBoroPC_L on 3/5/2017.
 */

public class CameraPreviewSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    public Camera camera;
    private View cameraView;
    private Context context;

    private Camera.Size cameraPreviewSize;
    private List<Camera.Size> cameraPreviewSizes;

    public CameraPreviewSurfaceView(Context context, Camera camera, View cameraView) {
        super(context);
        this.context = context;
        this.cameraView = cameraView;

        setCamera(camera);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setKeepScreenOn(true);
    }

    public void startCameraPreview() {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            Log.e("Camera Preview", "Failed to start");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null)
                camera.setPreviewDisplay(holder);
            else {
                Log.e("surfaceCreated", "NULL CAMERA");
            }
        } catch (IOException e) {
            Log.e("surfaceCreated", "Surface is unavailable or unsuitable");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null)
            return; // no surface :(

        try {
            Camera.Parameters params = camera.getParameters();

            if (cameraPreviewSize != null) {
                Camera.Size previewSize = cameraPreviewSize;
                params.setPreviewSize(previewSize.width, previewSize.height);
            }

            camera.setParameters(params);
            camera.setPreviewDisplay(surfaceHolder);
        } catch (Exception e) {
            Log.e("Surface Changed", "Camera Error");
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        if (changed) {
            final int width = right - left;
            final int height = bottom - top;

            int previewWidth = width;
            int previewHeight = height;

            if (cameraPreviewSize != null && camera != null) {
                Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                switch (display.getRotation()) {
                    case Surface.ROTATION_0:
                        previewWidth = cameraPreviewSize.height;
                        previewHeight = cameraPreviewSize.width;
                        camera.setDisplayOrientation(90);
                        break;
                    case Surface.ROTATION_90:
                        previewWidth = cameraPreviewSize.width;
                        previewHeight = cameraPreviewSize.height;
                        break;
                    case Surface.ROTATION_180:
                        previewWidth = cameraPreviewSize.height;
                        previewHeight = cameraPreviewSize.width;
                        break;
                    case Surface.ROTATION_270:
                        previewWidth = cameraPreviewSize.width;
                        previewHeight = cameraPreviewSize.height;
                        camera.setDisplayOrientation(180);
                        break;
                }
            }

            final int scaledChildHeight = previewHeight * width / previewWidth;
            cameraView.layout(0, height - scaledChildHeight, width, height);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (cameraPreviewSizes != null) {
            cameraPreviewSize = getOptimalPreviewSize(cameraPreviewSizes, width, height);
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        Camera.Size optimalSize = null;

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        // Try to find a size match which suits the whole screen minus the menu on the left.
        for (Camera.Size size : sizes) {

            if (size.height != width) continue;
            double ratio = (double) size.width / size.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE) {
                optimalSize = size;
            }
        }

        // If we cannot find the one that matches the aspect ratio, ignore the requirement.
        if (optimalSize == null) {
            // TODO : Backup in case we don't get a size.
        }

        return optimalSize;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) camera.stopPreview();
    }

    public void setCamera(Camera camera) {
        Camera.Parameters cameraParameters = camera.getParameters();
        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(cameraParameters);
        this.camera = camera;
        cameraPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
        requestLayout();
    }
}
