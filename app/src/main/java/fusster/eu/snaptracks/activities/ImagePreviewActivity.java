package fusster.eu.snaptracks.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import fusster.eu.snaptracks.R;
import fusster.eu.snaptracks.SnapTracks;

public class ImagePreviewActivity extends Activity implements View.OnClickListener {

    int rotation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        Bitmap bitmap = SnapTracks.getLastImageBitmap().copy(Bitmap.Config.ARGB_8888, true);

        if (bitmap == null) {
            Log.println(Log.ASSERT, "bitmap", "null");
            return;
        }

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        imageView.setImageDrawable(bitmapDrawable);

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        rotation += 90;

        Matrix matrix = new Matrix();
        matrix.postRotate(rotation % 360);

        Bitmap bitmap = SnapTracks.getLastImageBitmap().copy(Bitmap.Config.ARGB_8888, true);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        imageView.setImageDrawable(bitmapDrawable);
    }
}
