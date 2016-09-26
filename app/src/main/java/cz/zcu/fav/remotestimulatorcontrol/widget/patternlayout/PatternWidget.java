package cz.zcu.fav.remotestimulatorcontrol.widget.patternlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.util.PixelUtil;

public class PatternWidget extends FlexboxLayout {

    // region Constants
    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "PatternWidget";
    // Minimální počet bitů
    public static final int MIN_BIT_COUNT = 1;
    // Maximální počet bitů
    public static final int MAX_BIT_COUNT = 32;
    // Výchozí počet bitů na jeden řádek
    public static final int DEF_BIT_PER_ROW = 8;
    // endregion

    // region Variables
    private final List<CheckBox> mBitList = new ArrayList<>(MAX_BIT_COUNT);
    private int mBitCount;
    private int mValue;
    private OnBitChangeListener mListener;
    // endregion

    // region Constructors
    public PatternWidget(Context context) {
        super(context);
    }

    public PatternWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PatternWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setFlexWrap(FLEX_WRAP_WRAP);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PatternWidget);
        mBitCount = a.getInt(R.styleable.PatternWidget_bit_count, 0);
        mValue = a.getInt(R.styleable.PatternWidget_value, 0);

        a.recycle();
        rearangeBits();
        updateView();
    }
    // endregion

    // region Private methods

    /**
     * Upraví počet checkboxů tak, aby odpovídal vlastnosti {@link #mBitCount}
     */
    private void rearangeBits() {
        int count = mBitList.size();

        if (mBitCount == count) {
            return;
        }

        Context context = getContext();

        if (mBitCount > count) {
            for (int i = count; i < mBitCount; i++) {
                final int tmp = i;
                CheckBox checkBox = new CheckBox(context);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        setBit(tmp, isChecked);
                    }
                });
                mBitList.add(checkBox);
                addView(checkBox);
                FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(checkBox.getLayoutParams());
                lp.minWidth = PixelUtil.dpToPixel(context, 12);
                checkBox.setLayoutParams(lp);
            }
        } else {
            for (int i = --count; i >= mBitCount; i--) {
                mBitList.remove(i);
                removeViewAt(i);
            }
            requestLayout();
        }
    }

    /**
     * Aktualizuje všechny checkboxy a nastaví jim správnou hodnotu
     */
    private void updateView() {
        for (int i = 0; i < mBitCount; i++) {
            updateCheckbox(i, getBit(i));
            /*CheckBox checkBox = mBitList.get(i);
            checkBox.setChecked(getBit(i) == 1);*/
        }
    }

    /**
     * Aktualizuje checkbox
     *
     * @param index Index checkboxu
     * @param val   Hodnota. 0 = false, jinak true
     */
    private void updateCheckbox(int index, int val) {
        mBitList.get(index).setChecked(val == 1);
    }
    // endregion

    // region Public methods

    /**
     * Nastaví bit na konkrétní pozici
     *
     * @param index Index zleva. Začíná se od nuly
     * @param value True, pokuk je hodnota aktivní, jinak false
     */
    public void setBit(int index, boolean value) {
        setBit(index, value ? 1 : 0);
    }

    /**
     * Nastaví bit na konkrétní pozici
     *
     * @param index Index zleva. Začíná se od nuly
     * @param value Hodnota. Přípustná je pouze 0 a 1. Cokoliv nenulového je bráno jako 1
     */
    public void setBit(int index, int value) {
        if (getBit(index) == value) {
            return;
        }

        int oldValue = this.mValue;
        if (value == 0) {
            this.mValue &= ~(1 << mBitCount - index - 1);
        } else {
            this.mValue |= (1 << mBitCount - index - 1);
        }

        updateCheckbox(index, value);
        if (mListener != null) {
            mListener.onBitChange(oldValue, this.mValue);
        }
    }

    /**
     * Vrátí bit na konkrétní pozici
     *
     * @param index Pozice bitu
     * @return Hodnotu bitu na zadané pozici. Může vrátit pouze 0 nebo 1
     */
    public int getBit(int index) {
        return (mValue >> (mBitCount - index - 1)) & 1;
    }
    // endregion

    // region Getters & Setters

    /**
     * Vrátí počet bitů v patternu
     *
     * @return počet bitů
     */
    public int getBitCount() {
        return mBitCount;
    }

    /**
     * Nastaví počet bitů patternu
     * Po nastavení se upraví počet checkboxů
     *
     * @param bitCount Počet bitů
     */
    public void setBitCount(int bitCount) {
        this.mBitCount = bitCount;
        rearangeBits();
    }

    /**
     * Vrátí hodnotu reprezentující celý pattern
     * Dekadicky nemá žádný význam, důležitá je binární podoba čisla
     *
     * @return Hodnota patternu
     */
    public int getValue() {
        return mValue;
    }

    /**
     * Nastaví hodnotu patternu
     * Důležitá je binární podoba čísla
     * 1 - aktivní, 0 - neaktivní
     *
     * @param value     Hodnota patternu
     * @param propagate True, pokud se má změna hodnoty propagovat i do listeneru
     */
    public void setValue(int value, boolean propagate) {
        if (this.mValue == value) {
            return;
        }

        int oldValue = mValue;
        this.mValue = value;

        updateView();

        if (!propagate) {
            return;
        }

        if (mListener != null) {
            mListener.onBitChange(oldValue, value);
        }
    }

    /**
     * Nastaví listener na změnu bitů
     *
     * @param listener Listener
     */
    public void setOnBitChangeListener(OnBitChangeListener listener) {
        mListener = listener;
    }
    // endregion

    /**
     * Rozhraní pro zachycení změny v bitu
     */
    public interface OnBitChangeListener {
        /**
         * Reaguje na změnu hodnoty bitů
         *
         * @param oldValue Stará hodnota
         * @param newValue Nová hodnota
         */
        void onBitChange(int oldValue, int newValue);
    }

}
