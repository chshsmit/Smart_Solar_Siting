package solarsitingucsc.smartsolarsiting.View;

/**
 * Created by Nicki on 2018-05-07.
 */

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * The PreferenceHelp will display a dialog, and will persist the
 * <code>true</code> when pressing the positive button and <code>false</code>
 * otherwise. It will persist to the android:key specified in xml-preference.
 */

public class PreferenceHelp extends DialogPreference {

    public PreferenceHelp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        persistBoolean(positiveResult);
    }

}
