package fusster.eu.snaptracks.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import fusster.eu.snaptracks.R;
import fusster.eu.snaptracks.views.FindingsGridViewAdapter;

/**
 * Created by ComBoro on 2/12/2017.
 */

public class FindingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_findings, container, false);

        FindingsGridViewAdapter findingsGridViewAdapter = new FindingsGridViewAdapter(getContext());
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setAdapter(findingsGridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Clicked: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
