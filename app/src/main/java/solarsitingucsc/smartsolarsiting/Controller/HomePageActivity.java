package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.Serializable;

import solarsitingucsc.smartsolarsiting.R;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //Toolbar setup
        Toolbar topToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(topToolBar);


        instantiateBottomNavigation();
    }

    private void instantiateBottomNavigation(){
        BottomNavigationView bottomNavigation = findViewById(R.id.newSolarSite);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.Solar_Site:
                        //transition to main activity
                        changeToMainActivity();
                        break;
                }
                return true;
            }
        });

    }


//    //ToolBar function to setup res/menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.Settings)
        {
            changeToSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeToSettings(){
        Intent settings = new Intent(HomePageActivity.this, SettingsActivity.class);
        startActivity(settings);
    }


    private void changeToMainActivity(){
        Intent next_activity = new Intent(HomePageActivity.this, MainActivity.class);
        startActivity(next_activity);
    }



}
