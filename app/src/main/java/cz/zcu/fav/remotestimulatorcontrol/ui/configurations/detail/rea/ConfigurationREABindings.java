package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.rea;

import android.databinding.BindingAdapter;
import android.support.v7.widget.AppCompatRadioButton;

import cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.ConfigurationREA;

@SuppressWarnings("unused")
public class ConfigurationREABindings {

    @BindingAdapter({"value", "flag"})
    public static void setCompatRadioButtonChecked(AppCompatRadioButton view, ConfigurationREA.Gender value , ConfigurationREA.Gender flag) {
        view.setChecked(value == flag);
    }

    @BindingAdapter({"value", "flag"})
    public static void setCompatRadioButtonChecked(AppCompatRadioButton view, ConfigurationREA.OnFail value , ConfigurationREA.OnFail flag) {
        view.setChecked(value == flag);
    }

}
