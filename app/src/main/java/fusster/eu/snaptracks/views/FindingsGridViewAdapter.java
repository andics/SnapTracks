package fusster.eu.snaptracks.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Map;

import fusster.eu.snaptracks.R;
import fusster.eu.snaptracks.SnapTracks;
import fusster.eu.snaptracks.fragments.FindingsFragment;

/**
 * Created by ComBoro on 3/7/2017.
 */

public class FindingsGridViewAdapter extends BaseAdapter {

    private Context context;
    private Display display;

    public FindingsGridViewAdapter(Context context, Display display) {
        this.context = context;
        this.display = display;
    }

    @Override
    public int getCount() {
        return ((Map) SnapTracks.getPhotos()).size();
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

            int sWidth = display.getWidth(), sHeight = display.getHeight();

            int width = Math.round(FindingsFragment.WIDTH_RATIO * sWidth),
                    height = Math.round(FindingsFragment.HEIGHT_RATIO * sHeight);

            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            imageView.setLayoutParams(layoutParams);

            Map.Entry<Bitmap, File> entry = SnapTracks.getEntry(position);

            textView.setText(SnapTracks.getPreviewName(entry.getValue()));

            Bitmap bitmap = entry.getKey();
            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
            imageView.setImageDrawable(bitmapDrawable);
        } else {
            gridView = convertView;
        }

        return gridView;
    }
}

