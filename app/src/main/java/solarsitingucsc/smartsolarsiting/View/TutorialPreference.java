package solarsitingucsc.smartsolarsiting.View;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import solarsitingucsc.smartsolarsiting.Controller.SettingsActivity;

/**
 * Created by chrissmith on 6/10/18.
 */

public class TutorialPreference extends Preference {

    public TutorialPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        System.out.println("Playing tutorial video");
        SettingsActivity.playTutorialVideo();
    }
}
