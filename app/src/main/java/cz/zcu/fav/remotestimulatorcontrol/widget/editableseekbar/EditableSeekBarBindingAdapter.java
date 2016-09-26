package cz.zcu.fav.remotestimulatorcontrol.widget.editableseekbar;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;

@InverseBindingMethods({
        @InverseBindingMethod(type = EditableSeekBar.class, attribute = "value")
})
@SuppressWarnings("unused")
public class EditableSeekBarBindingAdapter {

    @BindingAdapter("value")
    public static void setProgress(EditableSeekBar view, int value) {
        if (value != view.getValue()) {
            view.setValue(value);
        }
    }

    @BindingAdapter(value = {"onProgressChanged", "onStartTrackingTouch", "onStopTrackingTouch", "onValueTooHigh", "onValueTooLow", "onValueChanged", "progressAttrChanged"}, requireAll = false)
    public static void setOnSeekBarChangeListener(EditableSeekBar view,
                                                  final EditableSeekBar.OnEditableSeekBarProgressChanged onProgressChanged,
                                                  final EditableSeekBar.OnStartTrackingTouch onStartTrackingTouch,
                                                  final EditableSeekBar.OnStopTrackingTouch onStopTrackingTouch,
                                                  final EditableSeekBar.OnEnteredValueTooHigh onEnteredValueTooHigh,
                                                  final EditableSeekBar.OnEnteredValueTooLow onEnteredValueTooLow,
                                                  final EditableSeekBar.OnEditableSeekBarValueChanged onValueChange,
                                                  final InverseBindingListener attrChanged) {
        if (onProgressChanged == null && onStartTrackingTouch == null && onStopTrackingTouch == null && onEnteredValueTooHigh == null && onEnteredValueTooLow == null && onValueChange == null) {
            view.setOnEditableSeekBarChangeListener(null);
        } else {
            view.setOnEditableSeekBarChangeListener(new OnEditableSeekBarChangeComponentListener(
                    onProgressChanged,
                    onStartTrackingTouch,
                    onStopTrackingTouch,
                    onEnteredValueTooHigh,
                    onEnteredValueTooLow,
                    onValueChange,
                    attrChanged));
        }
    }

    @BindingAdapter({"esbValue"})
    public static void setValue(EditableSeekBar view, int value) {
        view.setValue(value);
    }

    @BindingAdapter(value = {"esbMin", "esbMax"}, requireAll = false)
    public static void setRange(EditableSeekBar view, int min, int max) {
        // Prevence před nechtěnným nastavení hodnoty
        if (min != 0) {
            view.setMinValue(min);
        }
        // Prevence před nechtěnným nastavení hodnoty
        if (max != 0) {
            view.setMaxValue(max);
        }
    }
}
