package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.sorting;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityConfigurationSortingBinding;

import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.ConfigurationsActivity.FLAG_SORT_MEDIA;
import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.ConfigurationsActivity.FLAG_SORT_NAME;
import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.ConfigurationsActivity.FLAG_SORT_TYPE;

public class ConfigurationSortingActivity extends AppCompatActivity {

    // region Constants
    public static final String SORTING_FLAG = "SortingFlag";
    // endregion

    // region Variables
    public final ObservableInt sortingFlag = new ObservableInt();
    // endregion

    // region Private methods
    /**
     * Nastaví správný příznak
     *
     * @param flag Příznak
     * @param value True, pokud jse aktivní, jinak false
     */
    private void setFlag(int flag, boolean value) {
        int oldValue = sortingFlag.get();
        if (value) {
            oldValue |= flag;
        } else {
            oldValue &= ~flag;
        }

        sortingFlag.set(oldValue);
    }
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        int flag;
        if (savedInstanceState != null) {
            flag = savedInstanceState.getInt(SORTING_FLAG);
        } else {
            Intent intent = getIntent();
            flag = intent.getIntExtra(SORTING_FLAG, 0);
        }
        sortingFlag.set(flag);

        ActivityConfigurationSortingBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_configuration_sorting);

        mBinding.setController(this);
        mBinding.setSortingFlag(sortingFlag);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SORTING_FLAG, sortingFlag.get());

        super.onSaveInstanceState(outState);
    }

    // region Public methods
    // Handler pro checkboxy
    public void onChecked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.checkBoxSortName:
                setFlag(FLAG_SORT_NAME, checked);
                break;
            case R.id.checkBoxSortType:
                setFlag(FLAG_SORT_TYPE, checked);
                break;
            case R.id.checkBoxSortMedia:
                setFlag(FLAG_SORT_MEDIA, checked);
                break;
        }
    }

    public void onCancel(View view) {
        finish();
    }

    public void onSubmit(View view) {
        Intent intent = new Intent();
        intent.putExtra(SORTING_FLAG, sortingFlag.get());

        setResult(RESULT_OK, intent);
        finish();
    }
    // endregion
}
