package www.wemaketotem.org.totemopenhealth.tablayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import www.wemaketotem.org.totemopenhealth.R;

/**
 * Fragment of the Data page.
 * Displays the amount of FoG's that occured
 */
public class FragmentData extends Fragment {

    public static FragmentData newInstance() {
        return new FragmentData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_data,container,false);
    }
}
