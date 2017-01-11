package www.wemaketotem.org.totemopenhealth;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import www.wemaketotem.org.totemopenhealth.tablayout.FragmentCue;
import www.wemaketotem.org.totemopenhealth.tablayout.FragmentData;
import www.wemaketotem.org.totemopenhealth.tablayout.FragmentDevice;
import www.wemaketotem.org.totemopenhealth.tablayout.FragmentScan;
import www.wemaketotem.org.totemopenhealth.tablayout.SectionsPagerAdapter;
import www.wemaketotem.org.totemopenhealth.tablayout.TabItem;

/**
 * Main application of the app
 */
public class Application extends AppCompatActivity implements Observer{

    private ViewPager mViewPager;
    private static final String[] INITIAL_PERMS=
            {
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,    // maybe not necessary, need to check!
                    //android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.CAMERA
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkBLEPermissions(this);
        setUpPager();
    }

    public void checkBLEPermissions(Activity activity)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            int hasPermissionLocation = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
            int hasPermissionCamera = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if(hasPermissionLocation == PackageManager.PERMISSION_GRANTED && hasPermissionCamera == PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            ActivityCompat.requestPermissions(activity, INITIAL_PERMS, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch(requestCode)
        {
            case 1:
            {
                for(int i : grantResults)
                {
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED)
                        Log.w("Permissions", "Permission Denied");
                }
            }
        }
    }


    /**
     * creates the view pager and sets the adapter.
     */
    private void setUpPager() {
        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager(), getTabs());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * Creates the tabs of the pager
     * @return Array list with al the tab items
     */
    private ArrayList<TabItem> getTabs() {
        ArrayList<TabItem> tabItems = new ArrayList<>();
        FragmentScan fragmentScan = FragmentScan.newInstance();
        FragmentData fragmentData = FragmentData.newInstance();
        FragmentDevice fragmentDevice = FragmentDevice.newInstance();
        FragmentCue fragmentCue = FragmentCue.newInstance();

        fragmentScan.registerObserver(this);
        fragmentScan.registerObserver(fragmentDevice);

        tabItems.add(new TabItem("Scan", fragmentScan));
        tabItems.add(new TabItem("Data", fragmentData));
        tabItems.add(new TabItem("Cue", fragmentCue));
        tabItems.add(new TabItem("Device", fragmentDevice));
        return tabItems;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Go to the device tab on connect
     */
    @Override
    public void deviceConnected() {
        mViewPager.setCurrentItem(4);
    }


    /**
     * Go to the scan tab on disconnect
     */
    @Override
    public void deviceDisconnected() {
        Log.d("main", "show first tab");
        mViewPager.setCurrentItem(0);
    }
}
