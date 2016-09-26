package cz.zcu.fav.remotestimulatorcontrol.widget.patternlayout;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;

@InverseBindingMethods({
        @InverseBindingMethod(type = PatternWidget.class, attribute = "value"),
})
@SuppressWarnings("unused")
public class PatternBindingAdapter {

    @BindingAdapter("bit_count")
    public static void setBitCount(PatternWidget view, int bitCount) {
        view.setBitCount(bitCount);
    }

    @BindingAdapter("value")
    public static void setValue(PatternWidget view, int value) {
        view.setValue(value, true);
    }

    @BindingAdapter(value = {"onBitChange", "valueAttrChanged"}, requireAll = false)
    public static void setOnValueChange(PatternWidget view, PatternWidget.OnBitChangeListener bitChangeListener, final InverseBindingListener attrChanged) {
        if (bitChangeListener == null && attrChanged == null) {
            view.setOnBitChangeListener(null);
        } else {
            view.setOnBitChangeListener(new OnBitChangeComponentListener(bitChangeListener, attrChanged));
        }
    }
}
