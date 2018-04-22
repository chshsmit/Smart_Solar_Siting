package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import solarsitingucsc.smartsolarsiting.R;
import solarsitingucsc.smartsolarsiting.View.SettingsFragment;

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
        setContentView(R.layout.activity_settings);
        //addPreferencesFromResource(R.xml.preferences);


        //Displaying the preference fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.preference_content, new SettingsFragment())
                .commit();

        //Initializing the toolbar
        initializeToolBar();

        //sets up preferences buttons
        //instantiatePrefs();
    }



//
//    //----------------------------------------------------------------------------------------------
//    //ToolBar Setup
//    //----------------------------------------------------------------------------------------------

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
    //Initializing preferences list
    //----------------------------------------------------------------------------------------------

//
//
//
//    //----------------------------------------------------------------------------------------------
//    //Code for the camera preferences
//    //----------------------------------------------------------------------------------------------
//
//    private void changeFishEyeSettings(){
//
//
//    }
//
//    //----------------------------------------------------------------------------------------------
//    //Signing out
//    //----------------------------------------------------------------------------------------------
//
//    private void signout(){
//        AlertDialog.Builder signoutDialogBuilder = new AlertDialog.Builder(this);
//        setUpSignoutDialog(signoutDialogBuilder);
//
//        AlertDialog signoutDialog = signoutDialogBuilder.create();
//        signoutDialog.show();
//
//    }
//
//    private void setUpSignoutDialog(AlertDialog.Builder alertDialogBuilder){
//        alertDialogBuilder.setTitle("Are you sure you want to signout?");
//
//        //User pressed yes so sign out and return to the login screen
//        alertDialogBuilder.setPositiveButton("Yes",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mFirebaseAuth.signOut();
//                        toastMessage("Signed out", SettingsActivity.this);
//                        changeToLoginScreen();
//                    }
//                });
//
//        //User pressed cancel so do nothing
//        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//
//
//    }
//
//    private void changeToLoginScreen(){
//        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
//        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(login);
//    }
//
//
//
//    //----------------------------------------------------------------------------------------------
//    //About Page
//    //----------------------------------------------------------------------------------------------
//
//    private void about(){
//
//    }
//
//
//    public static void toastMessage(String message, Context currentActivity)
//    {
//        Toast.makeText(currentActivity,message,Toast.LENGTH_SHORT).show();
//    }

























}
