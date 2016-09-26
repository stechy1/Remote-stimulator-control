package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.tvep;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.ConfigurationTVEP;
import cz.zcu.fav.remotestimulatorcontrol.widget.wrappedviewpager.WrappedViewPager;

class ViewPagerPatternAdapter extends FragmentStatePagerAdapter {

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "TVEPOutputAdapter";

    private final ConfigurationTVEP configuration;
    private int mCurrentPosition = -1;

    ViewPagerPatternAdapter(FragmentManager fm, ConfigurationTVEP configuration) {
        super(fm);

        this.configuration = configuration;
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
     * @param position Pozice
     */
    @Override
    public Fragment getItem(int position) {
        PatternFragment fragment = new PatternFragment();
        fragment.setConfiguration(configuration);
        fragment.setPattern(configuration.patternList.get(position));

        return fragment;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return configuration.patternList.size();
    }
}
