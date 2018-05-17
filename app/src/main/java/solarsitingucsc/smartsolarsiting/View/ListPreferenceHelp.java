package solarsitingucsc.smartsolarsiting.View;

/**
 * Created by Nicki on 2018-05-07.
 */

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.support.v7.widget.TooltipCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import solarsitingucsc.smartsolarsiting.Controller.SettingsActivity;
import solarsitingucsc.smartsolarsiting.R;

/**
 * The PreferenceHelp will display a dialog, and will persist the
 * <code>true</code> when pressing the positive button and <code>false</code>
 * otherwise. It will persist to the android:key specified in xml-preference.
 */

/**
 * ListPreferenceHelp should maintain the ListPreference functionality,
 * with a tooltip icon to provide instructions on usage.
 */

public class ListPreferenceHelp extends ListPreference {

    public ListPreferenceHelp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*@Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        persistBoolean(positiveResult);
    }*/

    @Override protected void onBindView(View view) {
        super.onBindView(view);

        /*ToggleButton toggleButton = (ToggleButton) view.findViewById(R.id.toggle_togglebutton);
        toggleButton.setChecked(isChecked());
        toggleButton.setOnCheckedChangeListener(mListener);*/

        Button button = view.findViewById(R.id.pref_button);

    }

}
