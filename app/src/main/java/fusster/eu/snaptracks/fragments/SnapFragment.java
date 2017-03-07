package fusster.eu.snaptracks.fragments;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import fusster.eu.snaptracks.R;
import fusster.eu.snaptracks.SnapTracks;
import fusster.eu.snaptracks.activities.ImagePreviewActivity;
import fusster.eu.snaptracks.views.CameraPreviewSurfaceView;

public class SnapFragment extends Fragment {
    private Camera camera;
    private CameraPreviewSurfaceView cameraPreviewSurfaceView;
    private View cameraView;

    int backCameraId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backCameraId = getBackCameraId();
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
                if (camera != null) {
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                            SnapTracks.saveImage(data, display);
                        }
                    });

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (SnapTracks.lock) {
                                try {
                                    SnapTracks.lock.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            releaseAll();
                            Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
                            startActivity(intent);
                        }
                    }).start();
                } else {
                    Log.println(Log.ASSERT, "kakvo stava", "tuk ?");
                    previewCameraInView(cameraView);
                }
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
        previewCameraInView(getView());
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

}
