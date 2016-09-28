package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.mediaimport;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.IValidate;

import static cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail.mediaimport.MediaImportActivity.FLAG_PATH;

public class ObservableMedia extends BaseObservable implements IValidate {

    @Bindable
    private String path = "...";
    @Bindable
    private boolean valid;
    @Bindable
    private int validityFlag = FLAG_PATH;
    @Bindable
    boolean changed = false;

    /**
     * Nastaví validitu zadanému příznaku
     *
     * @param flag  Příznak
     * @param value True, pokud je příznak validní, jinak false
     */
    protected void setValidityFlag(int flag, boolean value) {
        int oldFlagValue = this.validityFlag;
        if (value) {
            validityFlag |= flag;
        } else {
            validityFlag &= ~flag;
        }

        if (validityFlag == oldFlagValue) {
            return;
        }

        notifyPropertyChanged(BR.validityFlag);

        if (validityFlag == 0) {
            setValid(true);
        }
    }

    /**
     * Vrátí true, pokud se změnil vnitřní stav objektu, jinak false
     *
     * @return True, pokud se změnil vnitřní stav objektu, jinak false
     */
    public boolean isChanged() {
        return changed;
    }

    @Override
    public int getValidityFlag() {
        return changed ? validityFlag : 0;
    }

    @Override
    public boolean isFlagValid(int flag) {
        return !((validityFlag & flag) == flag);
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
        notifyPropertyChanged(BR.valid);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        changed = true;
        notifyPropertyChanged(BR.path);

        if (path.isEmpty() || path.equals("...")) {
            setValid(false);
            setValidityFlag(FLAG_PATH, true);
        } else {
            setValidityFlag(FLAG_PATH, false);
        }
    }
}
