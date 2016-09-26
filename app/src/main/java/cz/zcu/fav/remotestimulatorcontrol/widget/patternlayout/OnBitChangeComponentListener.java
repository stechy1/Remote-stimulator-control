package cz.zcu.fav.remotestimulatorcontrol.widget.patternlayout;


import android.databinding.InverseBindingListener;

class OnBitChangeComponentListener implements PatternWidget.OnBitChangeListener {

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "BitComponentChange";
    private final PatternWidget.OnBitChangeListener mBitChange;
    private final InverseBindingListener mAttrChanged;

    OnBitChangeComponentListener(PatternWidget.OnBitChangeListener bitChangeListener, InverseBindingListener attrChanged) {
        mBitChange = bitChangeListener;
        mAttrChanged = attrChanged;
    }

    /**
     * Reaguje na změnu hodnoty bitů
     *
     * @param oldValue Stará hodnota
     * @param newValue Nová hodnota
     */
    @Override
    public void onBitChange(int oldValue, int newValue) {
        if (mBitChange != null) {
            mBitChange.onBitChange(oldValue, newValue);
        }

        if (mAttrChanged != null) {
            mAttrChanged.onChange();
        }
    }
}
