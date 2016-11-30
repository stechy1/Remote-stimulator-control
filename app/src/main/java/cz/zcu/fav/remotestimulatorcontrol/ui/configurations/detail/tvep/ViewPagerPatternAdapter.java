package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.tvep;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.ConfigurationTVEP;
import cz.zcu.fav.remotestimulatorcontrol.widget.wrappedviewpager.WrappedViewPager;

class ViewPagerPatternAdapter extends FragmentStatePagerAdapter {

    // region Constants
    // Logovací tag
    private static final String TAG = "TVEPOutputAdapter";
    // endregion

    // region Variables
    private final ConfigurationTVEP mConfiguration;
    private int mCurrentPosition = -1;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový viewPager adapter
     *
     * @param fm {@link FragmentManager}
     * @param configuration Konfigurace TVEP
     */
    ViewPagerPatternAdapter(FragmentManager fm, ConfigurationTVEP configuration) {
        super(fm);
        mConfiguration = configuration;
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
     * @param position Pozice
     */
    @Override
    public Fragment getItem(int position) {
        PatternFragment fragment = new PatternFragment();
        fragment.setConfiguration(mConfiguration);
        fragment.setPattern(mConfiguration.patternList.get(position));

        return fragment;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mConfiguration.patternList.size();
    }
}
