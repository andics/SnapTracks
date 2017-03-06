package fusster.eu.snaptracks.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import fusster.eu.snaptracks.R;
import fusster.eu.snaptracks.SnapTracks;

public class ImagePreviewActivity extends Activity {

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
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        imageView.setImageDrawable(bitmapDrawable);

    }

}
