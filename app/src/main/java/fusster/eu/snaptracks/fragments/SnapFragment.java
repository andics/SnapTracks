package fusster.eu.snaptracks.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
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
import java.io.FileOutputStream;
import java.io.IOException;

import fusster.eu.snaptracks.R;
import fusster.eu.snaptracks.SnapTracks;
import fusster.eu.snaptracks.views.CameraPreviewSurfaceView;

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

        if (camera == null) {
            Log.e("NULLcam", "Losho");
        }

        Button captureButton = (Button) view.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null)
                    camera.takePicture(null, null, pictureCallback);
                else previewCameraInView(cameraView);
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

        if (camera == null) {
            Log.e("CAMERA", "NULL");
        }

        cameraView = view;
        boolean opened = (camera != null);

        if (opened) {
            cameraPreviewSurfaceView = new CameraPreviewSurfaceView(getActivity().getBaseContext(), camera, view);
            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.camera_preview);
            frameLayout.addView(cameraPreviewSurfaceView);
            cameraPreviewSurfaceView.startCameraPreview();
        } else {
            Log.e("not", "opened");
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
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                String fileName = "image_" + System.currentTimeMillis() + ".jpeg";

                File dest = new File(SnapTracks.getAppFolder(), fileName);

                try {
                    FileOutputStream outputStream = new FileOutputStream(dest);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    Toast.makeText(getActivity(), "File saved: " + dest.getPath(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e("IOEXception", "Error saving to sd card");
                }

                previewCameraInView(cameraView);
            }
        };
    }

}
