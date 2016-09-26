package cz.zcu.fav.remotestimulatorcontrol.widget.swiperefreshlayout;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

@SuppressWarnings("unused")
public class SwipeRefreshLayoutBindings {

    @BindingAdapter({"refresh"})
    public static void bindOnRefresh(SwipeRefreshLayout layout, SwipeRefreshLayout.OnRefreshListener listener) {
        layout.setOnRefreshListener(listener);
    }

    @BindingAdapter({"color_accent_1", "color_accent_2", "color_primary"})
    public static void setSwipeColor(SwipeRefreshLayout layout, int colorAccent1, int colorAccent2, int colorPrimary) {
        layout.setColorSchemeColors(colorAccent2, colorAccent1, colorPrimary);
    }
}
