package fusster.eu.snaptracks.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import at.markushi.ui.CircleButton;
import fusster.eu.snaptracks.R;
import fusster.eu.snaptracks.SnapTracks;

public class ImagePreviewActivity extends Activity implements View.OnClickListener {

    public static final String SEND_BUTTON = "fusster.eu.snaptracks.activities.ImagePreviewActivity.SEND_BUTTON";
    public static Bitmap bitmap = null;
    public static byte[] imageData = null;

    private int rotation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        Bitmap bitmap = null;

        if (ImagePreviewActivity.bitmap != null) {
            bitmap = ImagePreviewActivity.bitmap;
        } else {
            bitmap = SnapTracks.getLastImageBitmap().copy(Bitmap.Config.ARGB_8888, true);
            ImagePreviewActivity.bitmap = bitmap;
        }

        if (bitmap == null) {
            Log.println(Log.ASSERT, "bitmap", "null");
            return;
        }

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        imageView.setImageDrawable(bitmapDrawable);

        CircleButton sendButton = (CircleButton) findViewById(R.id.send_button);

        if (getIntent().getBooleanExtra(SEND_BUTTON, true)) {
            sendButton.setOnClickListener(this);
        } else {
            ((ViewGroup) sendButton.getParent()).removeView(sendButton);
        }

        CircleButton rotateButton = (CircleButton) findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotation += 90;

                Bitmap bitmap = ImagePreviewActivity.bitmap;

                if (bitmap == null) {
                    bitmap = SnapTracks.getLastImageBitmap();
                }

                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                int toRotate = rotation % 360;

                if (toRotate != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(toRotate);

                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(bitmap, 0, 0, null);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                imageView.setImageDrawable(bitmapDrawable);
            }
        });

    }

    @Override
    public void onClick(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream fileInputStream = new FileInputStream(SnapTracks.getLastImage());

                    Socket socket = new Socket(InetAddress.getByName("iordi.csgourge.com"), 25565);
                    OutputStream outputStream = socket.getOutputStream();

                    byte[] buffer = new byte[1024 * 100];

                    int len;

                    while ((len = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }

                    socket.close();

                    Timer timer = new Timer();

                    final String orig = new BufferedReader(new InputStreamReader(new URL("http://85.11.165.109:25569/post.php").openStream())).readLine().trim();

                    timer.schedule(new TimerTask() {
                        String text = "niht good main froind";

                        @Override
                        public void run() {
                            try {
                                URL url = new URL("http://85.11.165.109:25569/post.php");
                                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                                String newText = in.readLine().trim();

                                if (newText.equals(text) && !newText.equals(orig)) {
                                    Toast.makeText(ImagePreviewActivity.this, "The animal is: " + text, Toast.LENGTH_SHORT).show();

                                    super.cancel();
                                }

                                text = newText;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0, 2000);
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }).start();

        Toast.makeText(ImagePreviewActivity.this, "Image sent", Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ImagePreviewActivity.bitmap = null;
    }
}
