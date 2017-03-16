package cz.zcu.fav.remotestimulatorcontrol.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import cz.zcu.fav.remotestimulatorcontrol.BR;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.IValidate;
import cz.zcu.fav.remotestimulatorcontrol.util.BitUtils;

/**
 * Základní třída pro všechny modely, které jsou pozorovatelné a validovatelné
 */
public abstract class BaseModel extends BaseObservable implements IValidate {

    // Příznak validity jednotlivých parametrů
    @Bindable
    protected int validityFlag;
    // Příznak validity celého modelu
    @Bindable
    protected boolean valid = true;
    // Příznak, zda-li se změníl stav konfigurace od posledního načteníprotected boolean changed;
    @Bindable
    protected boolean changed;

    /**
     * Nastaví validitu zadanému příznaku
     *
     * @param flag  Příznak
     * @param value True, pokud je příznak validní, jinak false
     */
    protected void setValidityFlag(int flag, boolean value) {
        int oldFlagValue = validityFlag;
        validityFlag = BitUtils.setBit(validityFlag, flag, value);

        if (validityFlag == oldFlagValue) {
            return;
        }

        notifyPropertyChanged(BR.validityFlag);
        changed = true;

        if (validityFlag == 0) {
            setValid(true);
        }
    }

    @Override
    public int getValidityFlag() {
        return changed ? validityFlag : 0;
    }

    @Override
    public boolean isFlagValid(int flag) {
        return !BitUtils.isBitSet(validityFlag, flag);
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

    @Override
    public boolean isChanged() {
        return changed;
    }
}
