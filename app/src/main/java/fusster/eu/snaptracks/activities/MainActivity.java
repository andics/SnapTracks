package fusster.eu.snaptracks.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import fusster.eu.snaptracks.R;
import fusster.eu.snaptracks.SnapTracks;
import fusster.eu.snaptracks.SnaperinioNetworkinio;
import fusster.eu.snaptracks.fragments.FindingsFragment;
import fusster.eu.snaptracks.fragments.MapFragment;
import fusster.eu.snaptracks.fragments.SnapFragment;

public class MainActivity extends FragmentActivity implements SnaperinioNetworkinio.SnaperinioListener {

    private ViewPager mViewPager;

    private Fragment[] fragments = {new MapFragment(),
            new SnapFragment(), new FindingsFragment()};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SnapTracks.init();

        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        mViewPager.setCurrentItem(1);

    }

    @Override
    protected void onResume() {
        SnaperinioNetworkinio.addListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        SnaperinioNetworkinio.removeListener(this);
        super.onPause();
    }

    @Override
    public void onEvent(String data) {
        Toast.makeText(getBaseContext(), "Data from server: " + data, Toast.LENGTH_LONG);
    }
}
