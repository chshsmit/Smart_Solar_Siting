package solarsitingucsc.smartsolarsiting.View;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import solarsitingucsc.smartsolarsiting.Controller.SettingsActivity;

/**
 * Created by chrissmith on 4/25/18.
 */

public class SignoutDialogPreference extends DialogPreference {

    public SignoutDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void onClick(DialogInterface dialog, int which){
        if(which == DialogInterface.BUTTON_POSITIVE){
            //Signout the user and change to login screen
            SettingsActivity.changeToLoginScreen();
        } else if(which == DialogInterface.BUTTON_NEGATIVE){
            //The user pressed cancel so do nothing
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        persistBoolean(positiveResult);
    }
}
