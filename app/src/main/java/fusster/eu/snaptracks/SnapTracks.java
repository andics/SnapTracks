package fusster.eu.snaptracks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ComBoro on 3/5/2017.
 */

public class SnapTracks {

    private static File appFolder, lastImage = null;
    private static Bitmap lastImageBitmap = null;
    public static final Object lock = new Object();

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    private SnapTracks() {
    } // empty private constructor

    public static File getAppFolder() {
        if (appFolder != null)
            return appFolder;

        appFolder = new File(Environment.getExternalStorageDirectory(), "SnapTracks");
        if (!appFolder.exists()) appFolder.mkdir();
        return appFolder;
    }

    public static void saveImage(final byte[] image, final Display display) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                lastImageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

                if (display.getRotation() == Surface.ROTATION_0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    lastImageBitmap = Bitmap.createBitmap(lastImageBitmap, 0, 0, lastImageBitmap.getWidth(), lastImageBitmap.getHeight(), matrix, true);
                }


                StringBuffer fileName = new StringBuffer("image_"); // Start of file name

                DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
                Date date = new Date();
                fileName.append(dateFormat.format(date)); // Append date
                fileName.append(".jpeg"); // Append file format

                lastImage = new File(getAppFolder(), fileName.toString());

                try {
                    FileOutputStream outputStream = new FileOutputStream(lastImage);
                    lastImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    Log.i("File saved", lastImage.getPath());
                } catch (IOException e) {
                    Log.e("IOException", "Error saving to app folder");
                    e.printStackTrace();
                }

                synchronized (lock) {
                    Log.println(Log.ASSERT, "sync", "puskame");
                    lock.notifyAll();
                }

            }
        });
    }

    public static File getLastImage() {
        return lastImage;
    }

    public static Bitmap getLastImageBitmap() {
        return lastImageBitmap;
    }


}
