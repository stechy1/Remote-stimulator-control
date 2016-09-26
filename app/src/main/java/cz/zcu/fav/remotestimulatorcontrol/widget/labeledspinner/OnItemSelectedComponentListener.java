package cz.zcu.fav.remotestimulatorcontrol.widget.labeledspinner;

import android.databinding.InverseBindingListener;
import android.view.View;
import android.widget.AdapterView;

class OnItemSelectedComponentListener implements AdapterView.OnItemSelectedListener {
    private final LabelledSpinner.OnItemSelected mSelected;
    private final LabelledSpinner.OnNothingSelected mNothingSelected;
    private final InverseBindingListener mAttrChanged;

    OnItemSelectedComponentListener(LabelledSpinner.OnItemSelected selected,
                                    LabelledSpinner.OnNothingSelected nothingSelected, InverseBindingListener attrChanged) {
        this.mSelected = selected;
        this.mNothingSelected = nothingSelected;
        this.mAttrChanged = attrChanged;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mSelected != null) {
            mSelected.onItemSelected(parent, view, position, id);
        }

        if (mAttrChanged != null) {
            mAttrChanged.onChange();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (mNothingSelected != null) {
            mNothingSelected.onNothingSelected(parent);
        }

        if (mAttrChanged != null) {
            mAttrChanged.onChange();
        }
    }
}
