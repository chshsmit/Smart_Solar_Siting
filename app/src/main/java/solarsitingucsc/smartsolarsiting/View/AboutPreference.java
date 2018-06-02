package solarsitingucsc.smartsolarsiting.View;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import solarsitingucsc.smartsolarsiting.Controller.SettingsActivity;

/**
 * Created by chrissmith on 5/24/18.
 */

public class AboutPreference extends Preference {

    public AboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onClick() {
        System.out.println("Clicked About");
        SettingsActivity.changeToAbout();
    }
}
