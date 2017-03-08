package fusster.eu.snaptracks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fusster.eu.snaptracks.fragments.FindingsFragment;

/**
 * Created by ComBoro on 3/5/2017.
 */

public class SnapTracks {

    private static File appFolder, lastImage = null;
    private static Bitmap lastImageBitmap = null;
    public static final Object lock = new Object();

    private static boolean imagesLoaded = false;

    public static BetterMap<Bitmap, File> photos = new BetterMap<>();

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

                    photos.put(compress(lastImageBitmap), lastImage);
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

    public static void loadImages() {
        if (imagesLoaded) return;
        imagesLoaded = true;

        File[] imageFiles = SnapTracks.getAppFolder().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String path = pathname.getPath();
                int lastDot = path.lastIndexOf('.');
                String extension = path.substring(lastDot + 1);
                return extension.equalsIgnoreCase("jpeg");
            }
        });

        for (File imageFile : imageFiles) {
            Bitmap bitmap = compress(BitmapFactory.decodeFile(imageFile.getPath()));

            photos.put(bitmap, imageFile);
        }
    }

    private static Bitmap compress(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(FindingsFragment.WIDTH_RATIO, FindingsFragment.HEIGHT_RATIO);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    public static BetterMap<Bitmap, File> getPhotos() {
        return photos;
    }

    public static Map.Entry<Bitmap, File> getEntry(int position) {
        return photos.getEntry(position);
    }

    public static String getPreviewName(File file) {
        String name = file.getName();
        return name.substring(6, name.length() - 5).replace('_', '/');
    }
}

class BetterMap<K, V> extends LinkedHashMap<K, V> {
    public Map.Entry<K, V> getEntry(int i) {
        // check if negetive index provided
        Set<Entry<K, V>> entries = entrySet();
        int j = 0;

        for (Map.Entry<K, V> entry : entries)
            if (j++ == i) return entry;

        return null;
    }
}
