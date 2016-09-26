package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.erp;

import android.databinding.ObservableArrayList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.ConfigurationERP;
import cz.zcu.fav.remotestimulatorcontrol.widget.wrappedviewpager.WrappedViewPager;

class ViewPagerOutputAdapter extends FragmentStatePagerAdapter {

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "ERPOutputAdapter";

    private final ObservableArrayList<ConfigurationERP.Output> outputs;
    private int mCurrentPosition = -1;

    ViewPagerOutputAdapter(FragmentManager fm, ObservableArrayList<ConfigurationERP.Output> outputs) {
        super(fm);
        this.outputs = outputs;
    }

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
        fragment.setOutput(outputs.get(position));
        return fragment;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return outputs.size();
    }
}
