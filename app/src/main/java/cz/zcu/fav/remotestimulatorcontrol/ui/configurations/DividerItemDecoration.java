package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cz.zcu.fav.remotestimulatorcontrol.R;

/**
 * Dekorativní třída pro oddělení jednotlivých položek v recycler view.
 * Vytvoří vodorovnou/svislou oddělovací čáru u každé položky.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    // region Variables
    private final Drawable mDivider;
    private final Orientation mOrientation;
    // endregion

    // region Constructors
    /**
     * Vytvoří novou dekorativní třídu pro oddělení jednotlivých položek v recycler view.
     * Ve výchozím stavu bude oriantace horizontální.
     *
     * @param context {@link Context}
     */
    public DividerItemDecoration(Context context) {
        this(context, Orientation.HORIZONTAL);
    }

    /**
     * Vytvoří novou dekorativní třídu pro oddělení jednotlivých položek v recycler view.
     *
     * @param context {@link Context}
     * @param orientation {@link Orientation}
     */
    public DividerItemDecoration(Context context, Orientation orientation) {
        mDivider = ResourcesCompat.getDrawable(context.getResources(), R.drawable.line_divider, null);
        mOrientation = orientation;
    }
    // endregion

    // region Private methods
    /**
     * Vykreslí vertikální oddělovací čáru.
     *
     * @param c {@link Canvas}
     * @param parent {@link RecyclerView}
     */
    private void drawVerticalDivider(Canvas c, RecyclerView parent) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int left = child.getLeft() + params.leftMargin;
            int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * Vykreslí horizontální oddělovací čáru.
     *
     * @param c {@link Canvas}
     * @param parent {@link RecyclerView}
     */
    private void drawHorizontalDivider(Canvas c, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
    // endregion

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == Orientation.HORIZONTAL) {
            drawHorizontalDivider(c, parent);
        } else {
            drawVerticalDivider(c, parent);
        }
    }

    /**
     * Orientace oddělovací čáry
     */
    public enum Orientation {
        HORIZONTAL, VERTICAL
    }
}