package solarsitingucsc.smartsolarsiting.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
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

    public IntEditTextPreference(Context context) {
        super(context);
    }

    public IntEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.IntEditTextPreference);
        MIN_VALUE = a.getInteger(R.styleable.IntEditTextPreference_minValue, DEFAULT_MIN_VALUE);
        MAX_VALUE = a.getInteger(R.styleable.IntEditTextPreference_maxValue, DEFAULT_MAX_VALUE);
    }

    public IntEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.IntEditTextPreference);
        MIN_VALUE = a.getInteger(R.styleable.IntEditTextPreference_minValue, DEFAULT_MIN_VALUE);
        MAX_VALUE = a.getInteger(R.styleable.IntEditTextPreference_maxValue, DEFAULT_MAX_VALUE);
    }


    @Override
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedInt(-1));
    }

    //TODO:Clean up this portion of code/refactor
    @Override
    protected boolean persistString(String value) {
        boolean output;
        int intToPersist;
        EditText mEditText = this.getEditText();

        try {
            //Try to get an integer value of the string
            intToPersist =Integer.valueOf(value);
            System.out.println("intToPersist = "+intToPersist);
            System.out.println(MIN_VALUE);
            System.out.println(MAX_VALUE);

            //Check that the entered value is within range
            if(intToPersist < MIN_VALUE || intToPersist > MAX_VALUE){
                System.out.println("PersistedInt = "+getPersistedInt(-1));
                setTextValues(String.valueOf(getPersistedInt(-1)), mEditText);
                return false;
            }

            System.out.println("IntToPersist = "+intToPersist);
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
