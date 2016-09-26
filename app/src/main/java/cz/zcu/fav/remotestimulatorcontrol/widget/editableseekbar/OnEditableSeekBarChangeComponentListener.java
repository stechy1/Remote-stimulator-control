package cz.zcu.fav.remotestimulatorcontrol.widget.editableseekbar;

import android.databinding.InverseBindingListener;
import android.widget.SeekBar;


public class OnEditableSeekBarChangeComponentListener implements EditableSeekBar.OnEditableSeekBarChangeListener {

    private final EditableSeekBar.OnEditableSeekBarProgressChanged onProgressChanged;
    private final EditableSeekBar.OnStartTrackingTouch start;
    private final EditableSeekBar.OnStopTrackingTouch stop;
    private final EditableSeekBar.OnEnteredValueTooHigh valueTooHigh;
    private final EditableSeekBar.OnEnteredValueTooLow valueTooLow;
    private final EditableSeekBar.OnEditableSeekBarValueChanged onValueChange;
    private final InverseBindingListener attrChanged;

    public OnEditableSeekBarChangeComponentListener(
            EditableSeekBar.OnEditableSeekBarProgressChanged onProgressChanged,
            EditableSeekBar.OnStartTrackingTouch start,
            EditableSeekBar.OnStopTrackingTouch stop,
            EditableSeekBar.OnEnteredValueTooHigh valueTooHigh,
            EditableSeekBar.OnEnteredValueTooLow valueTooLow,
            EditableSeekBar.OnEditableSeekBarValueChanged onValueChange,
            InverseBindingListener attrChanged) {
        this.onProgressChanged = onProgressChanged;
        this.start = start;
        this.stop = stop;
        this.valueTooHigh = valueTooHigh;
        this.valueTooLow = valueTooLow;
        this.onValueChange = onValueChange;
        this.attrChanged = attrChanged;
    }

    @Override
    public void onProgressChange(SeekBar seekBar, int progress, boolean fromUser) {
        if (onProgressChanged != null) {
            onProgressChanged.onProgressChange(seekBar, progress, fromUser);
        }

        if (attrChanged != null) {
            attrChanged.onChange();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (start != null) {
            start.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (stop != null) {
            stop.onStopTrackingTouch(seekBar);
        }
    }

    @Override
    public void onValueTooHigh() {
        if (valueTooHigh != null) {
            valueTooHigh.onValueTooHigh();
        }
    }

    @Override
    public void onValueTooLow() {
        if (valueTooLow != null) {
            valueTooLow.onValueTooLow();
        }
    }

    @Override
    public void onValueChange(int value) {
        if (onValueChange != null) {
            onValueChange.onValueChange(value);
        }

        if (attrChanged != null) {
            attrChanged.onChange();
        }
    }
}
