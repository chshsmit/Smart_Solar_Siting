package solarsitingucsc.smartsolarsiting.Controller;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import solarsitingucsc.smartsolarsiting.R;

import java.util.List;

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

    private FirebaseAuth mFirebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        addPreferencesFromResource(R.xml.preferences);

        initializeToolBar();
        //Gives padding underneath the toolbar
        int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.activity_vertical_margin) , getResources().getDisplayMetrics());
        getListView().setPadding(horizontalMargin, topMargin, horizontalMargin, verticalMargin);

        //sets up preferences buttons
        instantiatePrefs();
    }


    //----------------------------------------------------------------------------------------------
    //ToolBar Setup
    //----------------------------------------------------------------------------------------------

    private void initializeToolBar(){
        getLayoutInflater().inflate(R.layout.settings_toolbar, (ViewGroup)findViewById(android.R.id.content));
        Toolbar toolbar = (Toolbar)findViewById(R.id.settings_toolbar);
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
    //Initializing preferences list
    //----------------------------------------------------------------------------------------------

    public void instantiatePrefs()
    {
        //sets up preferences buttons
        Preference cameraPref = (Preference) findPreference("changeCamera");
        cameraPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                changeFishEyeSettings();
                return true;
            }
        });

        Preference signoutPref = (Preference) findPreference("signout");
        signoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                signout();
                return true;
            }
        });


        Preference about = (Preference) findPreference("about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                about();
                return true;
            }
        });
    }


    //----------------------------------------------------------------------------------------------
    //Code for the camera preferences
    //----------------------------------------------------------------------------------------------

    private void changeFishEyeSettings(){


    }

    //----------------------------------------------------------------------------------------------
    //Signing out
    //----------------------------------------------------------------------------------------------

    private void signout(){
        AlertDialog.Builder signoutDialogBuilder = new AlertDialog.Builder(this);
        setUpSignoutDialog(signoutDialogBuilder);

        AlertDialog signoutDialog = signoutDialogBuilder.create();
        signoutDialog.show();

    }

    private void setUpSignoutDialog(AlertDialog.Builder alertDialogBuilder){
        alertDialogBuilder.setTitle("Are you sure you want to signout?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseAuth.signOut();
                        toastMessage("Signing out", SettingsActivity.this);
                        changeToLoginScreen();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //User pressed cancel so do nothing
            }
        });


    }

    private void changeToLoginScreen(){
        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
    }



    //----------------------------------------------------------------------------------------------
    //About Page
    //----------------------------------------------------------------------------------------------

    private void about(){

    }


    public static void toastMessage(String message, Context currentActivity)
    {
        Toast.makeText(currentActivity,message,Toast.LENGTH_SHORT).show();
    }

























}
