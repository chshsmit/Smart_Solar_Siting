package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import solarsitingucsc.smartsolarsiting.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static  FirebaseAuth mFirebaseAuth;
    private static Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mContext = this.getApplicationContext();
        setContentView(R.layout.activity_settings);

        //Displaying the preference fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.preference_content, new SettingsFragment())
                .commit();

        //Initializing the toolbar
        initializeToolBar();

    }

    //----------------------------------------------------------------------------------------------
    //This is the settings fragment that gets prefs from preferences.xml
    //----------------------------------------------------------------------------------------------

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            //Loading preferences from XML resource
            addPreferencesFromResource(R.xml.preferences);

            bindAllPreferencesToSummary();
        }

        public void bindAllPreferencesToSummary(){

            bindPreferenceSummaryToValue(findPreference("sys_mod_type"));
            bindPreferenceSummaryToValue(findPreference("sys_arr_type"));
            bindPreferenceSummaryToValue(findPreference("system_capacity"));
            bindPreferenceSummaryToValue(findPreference("sys_losses"));
            bindPreferenceSummaryToValue(findPreference("sys_tilt"));
            bindPreferenceSummaryToValue(findPreference("sys_azimuth"));
            bindPreferenceSummaryToValue(findPreference("sys_dataset"));
            bindPreferenceSummaryToValue(findPreference("change_vertical"));
            bindPreferenceSummaryToValue(findPreference("change_horizontal"));
        }
    }

    //----------------------------------------------------------------------------------------------
    //These functions set the summary value for each preference based on the user's current
    //setting values
    //----------------------------------------------------------------------------------------------


    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        if(preference instanceof ListPreference){
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getInt(preference.getKey(), -1));
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener
            = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof EditTextPreference) {

                try{
                    //This line will throw an exception if the text entered is not
                    //able to be parsed as an int
                    int intValue = Integer.valueOf(stringValue);

                    switch(preference.getKey()){

                        case "system_capacity":
                            System.out.println("Sys_Cap");
                            if(valueNotInRange(intValue, R.integer.sys_cap_min, R.integer.sys_cap_max)){
                                break;
                            }
                            preference.setSummary(mContext.getString(R.string.sys_cap_summ)
                                    +" "+stringValue+ "kW" );
                            break;

                        case "sys_losses":
                            if(valueNotInRange(intValue, R.integer.sys_losses_min, R.integer.sys_losses_max)){
                                break;
                            }
                            preference.setSummary(mContext.getString(R.string.sys_losses_summ)
                                    +" "+stringValue+ "%" );
                            break;

                        case "sys_tilt":
                            if(valueNotInRange(intValue, R.integer.ZERO, R.integer.sys_tilt_max)){
                                break;
                            }
                            preference.setSummary(mContext.getString(R.string.sys_tilt_summ)
                                    +" "+stringValue+ (char) 0x00B0 );
                            break;

                        case "sys_azimuth":
                            if(valueNotInRange(intValue, R.integer.ZERO, R.integer.sys_azimuth_max)){
                                break;
                            }
                            preference.setSummary(mContext.getString(R.string.sys_azimuth_summ)
                                    +" "+stringValue+ (char) 0x00B0 );
                            break;

                        case "change_horizontal":
                            System.out.println("Horizontal");
                            if(valueNotInRange(intValue, R.integer.ZERO, R.integer.fov_max)){
                                break;
                            }
                            preference.setSummary(mContext.getString(R.string.hor_FOV_summ)
                                    +" "+stringValue+ (char) 0x00B0 );
                            break;

                        case "change_vertical":
                            System.out.println("Vertical");
                            if(valueNotInRange(intValue, R.integer.ZERO, R.integer.fov_max)){
                                break;
                            }
                            preference.setSummary(mContext.getString(R.string.vert_FOV_summ)
                                    +" "+stringValue+ (char) 0x00B0 );
                            break;

                    }
                } catch(NumberFormatException e){
                    System.out.println("Caught the exception");
                    toastMessage("You must input a number", mContext);
                }

            }

            return true;
        }
    };


    //This function returns true if the given intValue is not in the proper range
    private static boolean valueNotInRange(int intValue, int minId, int maxId){
        Resources resources = mContext.getResources();
        if(intValue < resources.getInteger(minId) ||
                intValue > resources.getInteger(maxId)){
            toastMessage("Value not in range", mContext);
            return true;
        }
        return false;
    }


    //----------------------------------------------------------------------------------------------
    //ToolBar Setup
    //----------------------------------------------------------------------------------------------

    private void initializeToolBar(){
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_arrow_back));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //Signing out
    //----------------------------------------------------------------------------------------------


    public static void changeToLoginScreen(){
        Intent login = new Intent(mContext, LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        toastMessage("Signed Out", mContext);
        mFirebaseAuth.signOut();
        mContext.startActivity(login);
    }


    //TODO: Add an about page
    //----------------------------------------------------------------------------------------------
    //About Page
    //----------------------------------------------------------------------------------------------

    public static void changeToAbout(){
        Intent about = new Intent(mContext, AboutPage.class);
        mContext.startActivity(about);
    }

    //Toast utility Function
    public static void toastMessage(String message, Context currentActivity)
    {
        Toast.makeText(currentActivity,message,Toast.LENGTH_SHORT).show();
    }
}
