package cz.zcu.fav.remotestimulatorcontrol.widget.labeledspinner;

import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.widget.AbsListView;
import android.widget.Spinner;

@BindingMethods({
        @BindingMethod(type = LabelledSpinner.class, attribute = "onItemClick", method = "setOnItemClickListener"),
        @BindingMethod(type = LabelledSpinner.class, attribute = "onItemLongClick", method = "setOnItemLongClickListener"),
})
@InverseBindingMethods({
        @InverseBindingMethod(type = AbsListView.class, attribute = "selectedItemPosition"),
})
@SuppressWarnings("unused")
public class LabelledSpinnerBindingAdapter {

    @BindingAdapter("selectedItemPosition")
    public static void setSelectedItemPosition(LabelledSpinner view, int position) {
        Spinner spinner = view.getSpinner();
        if (spinner.getSelectedItemPosition() != position) {
            spinner.setSelection(position);
        }
    }

    @BindingAdapter(value = {"onItemSelected", "onNothingSelected",
            "selectedItemPositionAttrChanged"}, requireAll = false)
    public static void setOnItemSelectedListener(LabelledSpinner view,
                                                 final LabelledSpinner.OnItemSelected selected,
                                                 final LabelledSpinner.OnNothingSelected nothingSelected,
                                                 final InverseBindingListener attrChanged) {
        Spinner spinner = view.getSpinner();
        if (selected == null && nothingSelected == null && attrChanged == null) {
            spinner.setOnItemSelectedListener(null);
        } else {
            spinner.setOnItemSelectedListener(
                    new OnItemSelectedComponentListener(selected, nothingSelected, attrChanged));
        }
    }

}