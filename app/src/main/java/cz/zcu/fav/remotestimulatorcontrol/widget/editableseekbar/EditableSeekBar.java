package cz.zcu.fav.remotestimulatorcontrol.widget.editableseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import cz.zcu.fav.remotestimulatorcontrol.R;

public class EditableSeekBar extends RelativeLayout implements SeekBar.OnSeekBarChangeListener, TextWatcher, View.OnFocusChangeListener, ESB_EditText.OnEditTextListener {

    // Logovací tag
    @SuppressWarnings("unused")
    private static final String TAG = "EditableSeekBar";
    private static final int SEEKBAR_DEFAULT_MAX = 100;
    private static final int SEEKBAR_DEFAULT_MIN = 0;
    private static final int EDITTEXT_DEFAULT_WIDTH = 50;
    private static final int EDITTEXT_DEFAULT_FONT_SIZE = 18;
    private TextView esbTitle;
    private SeekBar esbSeekBar;
    private ESB_EditText esbEditText;
    private boolean selectOnFocus;
    private int currentValue = 0;
    private int minValue = 0;
    private int maxValue = 100;
    private boolean touching = false;
    private OnEditableSeekBarChangeListener mListener;

    public EditableSeekBar(Context context) {
        super(context);
    }

    public EditableSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(getContext(), R.layout.widget_editable_seekbar, this);

        setSaveEnabled(true);

        esbTitle = (TextView) findViewById(R.id.esbTitle);
        esbSeekBar = (SeekBar) findViewById(R.id.esbSeekBar);
        esbEditText = (ESB_EditText) findViewById(R.id.esbEditText);


        float defaultEditTextWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EDITTEXT_DEFAULT_WIDTH, getResources().getDisplayMetrics());
        int defaultEditTextFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, EDITTEXT_DEFAULT_FONT_SIZE, getResources().getDisplayMetrics());

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.EditableSeekBar,
                0, 0);

        setTitle(a.getString(R.styleable.EditableSeekBar_esbTitle));
        esbTitle.setTextAppearance(getContext(), a.getResourceId(R.styleable.EditableSeekBar_esbTitleAppearance, 0));
        selectOnFocus = a.getBoolean(R.styleable.EditableSeekBar_esbSelectAllOnFocus, true);
        esbEditText.setSelectAllOnFocus(selectOnFocus);
        esbEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimensionPixelSize(R.styleable.EditableSeekBar_esbEditTextFontSize, defaultEditTextFontSize));

        int min = a.getInteger(R.styleable.EditableSeekBar_esbMin, SEEKBAR_DEFAULT_MIN);
        int max = a.getInteger(R.styleable.EditableSeekBar_esbMax, SEEKBAR_DEFAULT_MAX);

        setRange(min, max);

        setValue(a.getInteger(R.styleable.EditableSeekBar_esbValue, translateToRealValue(getRange() / 2)));
        setEditTextWidth(a.getDimension(R.styleable.EditableSeekBar_esbEditTextWidth, defaultEditTextWidth));

        a.recycle();

        esbSeekBar.setOnSeekBarChangeListener(this);
        esbEditText.addTextChangedListener(this);
        esbEditText.setOnFocusChangeListener(this);
        esbEditText.setOnKeyboardDismissedListener(this);

        esbSeekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    private void setEditTextWidth(float width) {
        ViewGroup.LayoutParams params = esbEditText.getLayoutParams();
        params.width = (int) width;

        esbEditText.setLayoutParams(params);
    }

    /**
     * Set callback listener for changes of EditableSeekBar.
     *
     * @param listener OnEditableSeekBarChangeListener
     */
    public void setOnEditableSeekBarChangeListener(OnEditableSeekBarChangeListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentValue = translateToRealValue(progress);

        if (fromUser) {
            setEditTextValue(currentValue);

            if (selectOnFocus) {
                esbEditText.selectAll();
            } else {
                esbEditText.setSelection(esbEditText.getText().length());
            }
        }

        if (mListener != null) {
            mListener.onProgressChange(seekBar, currentValue, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mListener != null) {
            mListener.onStartTrackingTouch(seekBar);
        }

        touching = true;

        esbEditText.requestFocus();

        if (selectOnFocus) {
            esbEditText.selectAll();
        } else {
            esbEditText.setSelection(esbEditText.getText().length());
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mListener != null) {
            mListener.onStopTrackingTouch(seekBar);
        }

        touching = false;

        currentValue = translateToRealValue(seekBar.getProgress());

        if (mListener != null) {
            mListener.onValueChange(currentValue);
        }
    }

    @Override
    public void onEditTextKeyboardDismissed() {
        checkValue();

        if (mListener != null) {
            mListener.onValueChange(currentValue);
        }
    }

    @Override
    public void onEditTextKeyboardDone() {
        checkValue();

        if (mListener != null) {
            mListener.onValueChange(currentValue);
        }

        hideKeyboard();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (touching) {
            return;
        }

        String string = s.toString();
        if (string.isEmpty() || !isNumber(string)) {
            return;
        }

        int value = Integer.parseInt(s.toString());

        if (value == currentValue) {
            return;
        }

        if (value > maxValue) {
            value = maxValue;
            setEditTextValue(value);

            if (selectOnFocus) {
                esbEditText.selectAll();
            } else {
                esbEditText.setSelection(esbEditText.getText().length());
            }

            if (mListener != null) {
                mListener.onValueTooHigh();
            }
        }

        if (value < minValue) {
            value = minValue;
            setEditTextValue(value);

            if (selectOnFocus) {
                esbEditText.selectAll();
            } else {
                esbEditText.setSelection(esbEditText.getText().length());
            }

            if (mListener != null) {
                mListener.onValueTooLow();
            }
        }

        if (value >= minValue && value <= maxValue) {
            currentValue = value;
            setSeekBarValue(translateFromRealValue(currentValue));

            if (mListener != null) {
                mListener.onValueChange(currentValue);
            }
        }
    }

    private void checkValue() {
        setEditTextValue(currentValue);
    }

    private boolean isNumber(String s) {
        return android.text.TextUtils.isDigitsOnly(s);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v instanceof EditText) {
            if (!hasFocus) {
                boolean sendValueChanged = esbEditText.getText().toString().isEmpty()
                        || !isNumber(esbEditText.getText().toString())
                        || !isInRange(Integer.parseInt(esbEditText.getText().toString()));

                if (sendValueChanged) {
                    checkValue();
                }

                if (mListener != null && sendValueChanged)
                    mListener.onValueChange(currentValue);

            } else {
                if (selectOnFocus) {
                    esbEditText.selectAll();
                } else {
                    esbEditText.setSelection(esbEditText.getText().length());
                }
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(esbEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setTitle(String title) {
        if (title != null && !title.isEmpty()) {
            esbTitle.setText(title);
            esbTitle.setVisibility(View.VISIBLE);
        } else {
            esbTitle.setVisibility(View.GONE);
        }
    }

    public void setTitleColor(int color) {
        esbTitle.setTextColor(color);
    }

    private void setEditTextValue(int value) {
        if (esbEditText != null) {
            esbEditText.setText(String.valueOf(value));
        }
    }

    private void setSeekBarValue(int value) {
        esbSeekBar.setProgress(value);
    }

    private int translateFromRealValue(int realValue) {
        return realValue < 0 ? Math.abs(realValue - minValue) : realValue - minValue;
    }

    private int translateToRealValue(int sbValue) {
        return minValue + sbValue;
    }

    public void setRange(int min, int max) {
        if (min > max) {
            minValue = SEEKBAR_DEFAULT_MIN;
            maxValue = SEEKBAR_DEFAULT_MAX;
        } else {
            minValue = min;
            maxValue = max;
        }

        esbSeekBar.setMax(getRange());

        if (!isInRange(currentValue)) {
            if (currentValue < minValue) {
                currentValue = minValue;
            }

            if (currentValue > maxValue) {
                currentValue = maxValue;
            }

            setValue(currentValue);
        }
    }

    public int getRange() {
        return maxValue < 0 ? Math.abs(maxValue - minValue) : maxValue - minValue;
    }

    private boolean isInRange(int value) {

        if (value < minValue) {
            if (mListener != null) {
                mListener.onValueTooLow();
            }

            return false;
        }

        if (value > maxValue) {
            if (mListener != null) {
                mListener.onValueTooHigh();
            }

            return false;
        }

        return true;
    }

    public int getValue() {
        return currentValue;
    }

    public void setValue(String value) {
        setValue(Integer.parseInt(value));
    }

    public void setValue(Integer value) {
        if (value == null) {
            return;
        }

        if (!isInRange(value)) {
            if (value < minValue) {
                value = minValue;
            }

            if (value > maxValue) {
                value = maxValue;
            }
        }

        currentValue = value;

        setEditTextValue(currentValue);
        setSeekBarValue(translateFromRealValue(currentValue));
    }

    public void setMaxValue(int max) {
        setRange(minValue, max);
    }

    public void setMinValue(int min) {
        setRange(min, maxValue);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.value = currentValue;
        ss.focus = selectOnFocus;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setValue(ss.value);
        selectOnFocus = ss.focus;
    }

    public interface OnEditableSeekBarProgressChanged {
        void onProgressChange(SeekBar seekBar, int progress, boolean fromUser);
    }

    public interface OnStartTrackingTouch {
        void onStartTrackingTouch(SeekBar seekBar);
    }

    public interface OnStopTrackingTouch {
        void onStopTrackingTouch(SeekBar seekBar);
    }

    public interface OnEnteredValueTooHigh {
        void onValueTooHigh();
    }

    public interface OnEnteredValueTooLow {
        void onValueTooLow();
    }

    public interface OnEditableSeekBarValueChanged {
        void onValueChange(int value);
    }

    public interface OnEditableSeekBarChangeListener extends OnEditableSeekBarProgressChanged,
            OnStartTrackingTouch,
            OnStopTrackingTouch,
            OnEnteredValueTooHigh,
            OnEnteredValueTooLow,
            OnEditableSeekBarValueChanged {
//        void onEditableSeekBarProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
//
//        void onStartTrackingTouch(SeekBar seekBar);
//
//        void onStopTrackingTouch(SeekBar seekBar);
//
//        void onEnteredValueTooHigh();
//
//        void onEnteredValueTooLow();
//
//        void onEditableSeekBarValueChanged(int value);
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int value;
        boolean focus;
        boolean animate;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            value = in.readInt();
            focus = in.readInt() == 1;
            animate = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(value);
            out.writeInt(focus ? 1 : 0);
            out.writeInt(animate ? 1 : 0);
        }
    }

}
