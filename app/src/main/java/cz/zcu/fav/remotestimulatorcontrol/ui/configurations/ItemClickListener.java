package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import android.view.View;

public interface ItemClickListener<T> {

    /**
     * Zavolá se při kliknutí na položku v recyclerView
     *
     * @param v    View
     * @param item Item
     */
    void onItemClick(View v, T item);

}
