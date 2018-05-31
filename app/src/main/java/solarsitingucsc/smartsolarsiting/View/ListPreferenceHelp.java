package solarsitingucsc.smartsolarsiting.View;

/**
 * Created by Nicki on 2018-05-07.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.support.v7.widget.TooltipCompat;

import android.view.View;

import solarsitingucsc.smartsolarsiting.Controller.SettingsActivity;
import solarsitingucsc.smartsolarsiting.R;

/**
 * ListPreferenceHelp should maintain the ListPreference functionality,
 * with a tooltip icon to provide instructions on usage.
 */

public class ListPreferenceHelp extends ListPreference {

    private String TOOLTIP_STR;

    public ListPreferenceHelp(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ListPreferenceHelp);
        TOOLTIP_STR = a.getString(R.styleable.ListPreferenceHelp_tipText);
    }

    /** Since the Preference base class handles the icon and summary (or summaryOn and summaryOff in TwoStatePreference)
     * we only need to handle the tooltip button here. Simply get it from the previously created layout, set the data
     * against it and hook up a listener to handle user interaction with the button.
     */
    @Override protected void onBindView(View view) {
        super.onBindView(view);

        View tooltipButton = view.findViewById(R.id.PrefTooltip);
        //tooltipButton.setOnClickListener(helpListener);
        TooltipCompat.setTooltipText(tooltipButton,TOOLTIP_STR);
        // TODO: Fix listener conflict / test further on other devices

    }

}
