package fusster.eu.snaptracks.fragments;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fusster.eu.snaptracks.CameraPreviewSurfaceView;
import fusster.eu.snaptracks.R;

public class SnapFragment extends Fragment {
    private Camera camera;
    private CameraPreviewSurfaceView cameraPreviewSurfaceView;
    private View cameraView;

    private Camera.PictureCallback pictureCallback;

    int backCameraId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backCameraId = getBackCameraId();
        pictureCallback = getPictureCallback();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_snap, container, false);

        boolean opened = previewCameraInView(view);

        if (opened == false) {
            Log.e("SnapFragment", "Failed to open camera");
            return view;
        }

        Button captureButton = (Button) view.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
            }
        });

        return view;
    }

    private int getBackCameraId() {
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }
        return this.backCameraId;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseAll();
    }

    private boolean previewCameraInView(View view) {
        releaseAll();
        camera = Camera.open(backCameraId);
        cameraView = view;
        boolean opened = (camera != null);

        if (opened) {
            cameraPreviewSurfaceView = new CameraPreviewSurfaceView(getActivity().getBaseContext(), camera, view);
            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.camera_preview);
            frameLayout.addView(cameraPreviewSurfaceView);
            cameraPreviewSurfaceView.startCameraPreview();
        }

        return opened;
    }

    private void releaseAll() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (cameraPreviewSurfaceView != null) {
            cameraPreviewSurfaceView.destroyDrawingCache();
            cameraPreviewSurfaceView.camera = null;
        }
    }

    private Camera.PictureCallback getPictureCallback() {
        return new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                File pictureFile = getOutputMediaFile();
                if (pictureFile == null) {
                    Toast.makeText(getActivity(), "Image retrieval failed.", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();

                    // Restart the camera preview.
                    previewCameraInView(cameraView);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SnapTracks");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Camera Guide", "Required media storage does not exist");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        Toast.makeText(getContext(), "Picture saved", Toast.LENGTH_LONG).show();

        return mediaFile;
    }

}
