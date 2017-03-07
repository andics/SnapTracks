package fusster.eu.snaptracks.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import fusster.eu.snaptracks.R;
import fusster.eu.snaptracks.SnapTracks;

/**
 * Created by ComBoro on 3/7/2017.
 */

public class FindingsGridViewAdapter extends BaseAdapter {

    private Context context;

    private BetterMap<Bitmap, String> contentsMap = new BetterMap<>();

    public FindingsGridViewAdapter(Context context) {
        this.context = context;

        File[] imageFiles = SnapTracks.getAppFolder().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String path = pathname.getPath();
                int lastDot = path.lastIndexOf('.');
                String extension = path.substring(lastDot + 1);
                return extension.equalsIgnoreCase("jpeg");
            }
        });

        long totalBytes = 0;

        for (File imageFile : imageFiles) {
            totalBytes += imageFile.length();
            Log.println(Log.ASSERT, "image path, bytes", imageFile.getPath() + " , " + imageFile.length());
            Log.println(Log.ASSERT, "total megabytes", "" + totalBytes / 1_000_000);
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
            String name = imageFile.getName().substring(6, imageFile.getName().length() - 5).replace('_', '/');
            contentsMap.put(bitmap, name);
        }
    }

    @Override
    public int getCount() {
        return contentsMap.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            gridView = layoutInflater.inflate(R.layout.findings_gridview_layout, null);

            TextView textView = (TextView) gridView.findViewById(R.id.findings_gridview_text);
            ImageView imageView = (ImageView) gridView.findViewById(R.id.findings_gridview_image);

            Map.Entry<Bitmap, String> entry = contentsMap.getEntry(position);

            textView.setText(entry.getValue());

            Bitmap bitmap = entry.getKey();
            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
            imageView.setImageDrawable(bitmapDrawable);
        } else {
            gridView = convertView;
        }

        return gridView;
    }
}

class BetterMap<K, V> extends LinkedHashMap<K, V> {
    public Map.Entry<K, V> getEntry(int i) {
        // check if negetive index provided
        Set<Map.Entry<K, V>> entries = entrySet();
        int j = 0;

        for (Map.Entry<K, V> entry : entries)
            if (j++ == i) return entry;

        return null;

    }
}
