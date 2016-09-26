package cz.zcu.fav.remotestimulatorcontrol.widget.repairededittext;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.util.AttributeSet;
import android.widget.EditText;

public class RepairedEditText extends EditText {

    public RepairedEditText(Context context) {
        super(context);
    }

    public RepairedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RepairedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void setSpan_internal(Object span, int start, int end, int flags) {
        final int textLength = getText().length();
        getText().setSpan(span, start, Math.min(end, textLength), flags);
    }

    protected void setCursorPosition_internal(int start, int end) {
        final int textLength = getText().length();
        Selection.setSelection(getText(), Math.min(start, textLength), Math.min(end, textLength));
    }
}
