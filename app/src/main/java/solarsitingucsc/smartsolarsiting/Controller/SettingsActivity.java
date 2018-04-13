package solarsitingucsc.smartsolarsiting.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import solarsitingucsc.smartsolarsiting.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Toolbar setup
        Toolbar topToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(topToolBar);
    }

}
