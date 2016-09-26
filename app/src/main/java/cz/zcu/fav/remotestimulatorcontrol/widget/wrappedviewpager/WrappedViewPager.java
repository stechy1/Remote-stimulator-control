package cz.zcu.fav.remotestimulatorcontrol.widget.wrappedviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;


public class WrappedViewPager extends ViewPager {

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "WrappedViewPager";
    private View mCurrentView;

    public WrappedViewPager(Context context) {
        super(context);
    }

    public WrappedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mCurrentView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int height = 0;
        mCurrentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int h = mCurrentView.getMeasuredHeight();
        if (h > height) {
            height = h;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Přepočítá layout zadaného view
     *
     * @param view View
     */
    public void measureCurrentView(View view) {
        mCurrentView = view;
        requestLayout();
    }
}
