package cz.zcu.fav.remotestimulatorcontrol.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FragmentMainBinding;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.ConfigurationsFragment;
import cz.zcu.fav.remotestimulatorcontrol.ui.media.MediaFragment;
import cz.zcu.fav.remotestimulatorcontrol.ui.outputs.ProfileFragment;

public class MainFragment extends Fragment {

    // region Constants

    private static final String TAG = "MainFragment";

    private static final String SELECTED_FRAGMENT_ID = "selected_sub_fragment_id";

    private static final Map<Integer, Class> FRAGMENT_MAP = new HashMap<>(3);

    static {
        FRAGMENT_MAP.put(R.id.nav_bottom_experiments, ConfigurationsFragment.class);
        FRAGMENT_MAP.put(R.id.nav_bottom_profiles, ProfileFragment.class);
        FRAGMENT_MAP.put(R.id.nav_bottom_media, MediaFragment.class);
    }

    // endregion

    // region Variables

    private FragmentMainBinding mBinding;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return showFragment(item.getItemId());
        }
    };
    private int mFragmentId;

    // endregion

    /**
     * Zobrazí vybraný fragment
     *
     * @param fragmentId Id fragmentu
     */
    private boolean showFragment(int fragmentId) {
        if (!FRAGMENT_MAP.containsKey(fragmentId) || fragmentId == mFragmentId) {
            return false;
        }

        try {
            Fragment fragment = (Fragment) FRAGMENT_MAP.get(fragmentId).newInstance();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
            mFragmentId = fragmentId;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        mBinding.setController(this);
        mBinding.executePendingBindings();
        mBinding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mFragmentId = savedInstanceState.getInt(SELECTED_FRAGMENT_ID);
        } else {
            showFragment(R.id.nav_bottom_experiments);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        MenuItem item = mBinding.navigation.getMenu().findItem(mFragmentId);
        if (item != null) {
            item.setChecked(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_FRAGMENT_ID, mFragmentId);
        super.onSaveInstanceState(outState);
    }
}
