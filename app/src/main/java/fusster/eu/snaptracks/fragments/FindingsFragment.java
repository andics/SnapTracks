package fusster.eu.snaptracks.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import fusster.eu.snaptracks.R;
import fusster.eu.snaptracks.SnapTracks;
import fusster.eu.snaptracks.activities.ImagePreviewActivity;
import fusster.eu.snaptracks.views.FindingsGridViewAdapter;

/**
 * Created by ComBoro on 2/12/2017.
 */

public class FindingsFragment extends Fragment {

    public static final float WIDTH_RATIO = 0.45f, HEIGHT_RATIO = 0.3f;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_findings, container, false);

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        FindingsGridViewAdapter findingsGridViewAdapter = new FindingsGridViewAdapter(getContext(), display);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setAdapter(findingsGridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = SnapTracks.getPreviewName(SnapTracks.getEntry(position).getValue());

                ImagePreviewActivity.bitmap = BitmapFactory.decodeFile(SnapTracks.getEntry(position).getValue().getPath());

                Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
                intent.putExtra(ImagePreviewActivity.SEND_BUTTON, false);
                startActivity(intent);
            }
        });
        return view;
    }
}
