package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.fvep;

import android.databinding.ObservableArrayList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.ConfigurationFVEP;
import cz.zcu.fav.remotestimulatorcontrol.widget.wrappedviewpager.WrappedViewPager;

class ViewPagerOutputAdapter extends FragmentStatePagerAdapter {

    // region Constants
    // Logovací tag
    private static final String TAG = "FVEPOutputAdapter";
    // endregion

    // region Variables
    private final ObservableArrayList<ConfigurationFVEP.Output> mOutputs;
    private int mCurrentPosition = -1;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový viewPager adapter
     *
     * @param fm {@link FragmentManager}
     * @param outputs Kolekce fvep výstupů
     */
    ViewPagerOutputAdapter(FragmentManager fm, ObservableArrayList<ConfigurationFVEP.Output> outputs) {
        super(fm);
        mOutputs = outputs;
    }
    // endregion

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (position != mCurrentPosition) {
            Fragment fragment = (Fragment) object;
            WrappedViewPager pager = (WrappedViewPager) container;
            if (fragment != null && fragment.getView() != null) {
                mCurrentPosition = position;
                pager.measureCurrentView(fragment.getView());
            }
        }
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position Index outputu
     */
    @Override
    public Fragment getItem(int position) {
        OutputFragment fragment = new OutputFragment();
        fragment.setOutput(mOutputs.get(position));
        return fragment;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mOutputs.size();
    }
}
