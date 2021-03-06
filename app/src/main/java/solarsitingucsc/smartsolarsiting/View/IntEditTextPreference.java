package solarsitingucsc.smartsolarsiting.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.support.v7.widget.TooltipCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import solarsitingucsc.smartsolarsiting.Controller.SettingsActivity;
import solarsitingucsc.smartsolarsiting.R;

/**
 * Created by chrissmith on 4/26/18.
 */

public class IntEditTextPreference extends EditTextPreference {

    public static final int DEFAULT_MAX_VALUE = 100;
    public static final int DEFAULT_MIN_VALUE = 0;

    private int MIN_VALUE;
    private int MAX_VALUE;
    private String TOOLTIP_STRING;

    //----------------------------------------------------------------------------------------------
    //Constructor Functions to obtain values from styles
    //----------------------------------------------------------------------------------------------

    public IntEditTextPreference(Context context) {
        super(context);
    }

    public IntEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.IntEditTextPreference);
        MIN_VALUE = a.getInteger(R.styleable.IntEditTextPreference_minValue, DEFAULT_MIN_VALUE);
        MAX_VALUE = a.getInteger(R.styleable.IntEditTextPreference_maxValue, DEFAULT_MAX_VALUE);
        TOOLTIP_STRING = a.getString(R.styleable.IntEditTextPreference_editTextTip);
    }

    public IntEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.IntEditTextPreference);
        MIN_VALUE = a.getInteger(R.styleable.IntEditTextPreference_minValue, DEFAULT_MIN_VALUE);
        MAX_VALUE = a.getInteger(R.styleable.IntEditTextPreference_maxValue, DEFAULT_MAX_VALUE);
        TOOLTIP_STRING = a.getString(R.styleable.IntEditTextPreference_editTextTip);
    }

    //----------------------------------------------------------------------------------------------
    //Functions to handle the clicking of the tooltip function and binding of the view
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        View tooltipButton = view.findViewById(R.id.PrefTooltip);
        //tooltipButton.setOnClickListener(helpListener);
        TooltipCompat.setTooltipText(tooltipButton,TOOLTIP_STRING);
        // TODO: Fix listener conflict / test further on other devices

    }

    //----------------------------------------------------------------------------------------------
    //Functions to save entered value and assure it is in the proper range
    //----------------------------------------------------------------------------------------------


    @Override
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedInt(-1));
    }

    @Override
    protected boolean persistString(String value) {
        boolean output;
        int intToPersist;
        EditText mEditText = this.getEditText();

        try {
            //Try to get an integer value of the string
            intToPersist =Integer.valueOf(value);

            //Check that the entered value is within range
            if(intToPersist < MIN_VALUE || intToPersist > MAX_VALUE){
                setTextValues(String.valueOf(getPersistedInt(-1)), mEditText);
                return false;
            }

            output = persistInt(intToPersist);

        } catch (NumberFormatException e){
            //If the user entered a non integer
            setTextValues(String.valueOf(getPersistedInt(-1)), mEditText);
            return false;
        }
        return output;
    }

    private void setTextValues(String persistedValue, EditText mEditText){
        this.setText(persistedValue);
        mEditText.setText(persistedValue);
    }
}
