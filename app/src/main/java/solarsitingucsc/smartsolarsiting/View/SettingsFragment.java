package solarsitingucsc.smartsolarsiting.View;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import solarsitingucsc.smartsolarsiting.R;

/**
 * Created by chrissmith on 4/21/18.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Loading preferences from XML resource
        addPreferencesFromResource(R.xml.preferences);
    }


}
