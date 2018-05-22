package solarsitingucsc.smartsolarsiting.View;

/**
 * Created by Nicki on 2018-05-07.
 */

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.support.v7.widget.TooltipCompat;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.widget.LinearLayout;

import android.widget.ImageButton;
import android.widget.CompoundButton;
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
    private final Listener mListener = new Listener();
    private ExternalListener mExternalListener;

    public ListPreferenceHelp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*@Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        persistBoolean(positiveResult);
    }*/

    /** Inflates a custom layout for this preference, taking advantage of views with ids that are already
     * being used in the Preference base class.
     */
    @Override protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View prefView = inflater.inflate(R.layout.listpreference_help_layout, parent, false);
        LinearLayout mWidgetContainer = (LinearLayout) prefView.findViewById(android.R.id.widget_frame);

        ImageButton button = new ImageButton(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        button.setLayoutParams(params);
        button.setOnClickListener(mListener);
        button.setBackgroundResource(R.drawable.ic_info_black_24dp);
        mWidgetContainer.addView(button);

        return prefView;
    }

    @Override protected void onBindView(View view) {
        super.onBindView(view);

        //ImageButton button = view.findViewById(R.id.pref_button);
        //button.setOnClickListener(mListener);

    }

    /** This gets called when the preference (as a whole) is selected by the user. The TwoStatePreference
     * implementation changes the actual state of this preference, which we don't want, since we're handling
     * preference clicks with our 'external' listener. Hence, don't call super.onClick(), but the onPreferenceClick
     * of our listener. */
    @Override protected void onClick() {
        if (mExternalListener != null) mExternalListener.onPreferenceClick();
    }

    /** Simple interface that defines an external listener that can be notified when the preference has been
     * been clicked. This may be useful e.g. to navigate to a new activity from your PreferenceActivity, or
     * display a dialog. */
    public static interface ExternalListener {
        void onPreferenceClick();
    }

    /** Sets an external listener for this preference*/
    public void setExternalListener(ExternalListener listener) {
        mExternalListener = listener;
    }

    /** Listener to open tooltip on click */
    private class Listener implements CompoundButton.OnClickListener {
        @Override public void onClick(View btnView) {

        }
    }

}
