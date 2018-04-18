package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import solarsitingucsc.smartsolarsiting.R;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        initializeToolbar();
        initializeListView();
        initializeBottomNavigation();

        addSiteToListView();
    }

    //--------------------------------------------------------------------------------------------
    //Override functions
    //--------------------------------------------------------------------------------------------

    //We want to do nothing when the back button is pressed
    @Override
    public void onBackPressed(){

    }



    //--------------------------------------------------------------------------------------------
    //Toolbar functions
    //--------------------------------------------------------------------------------------------

    private void initializeToolbar(){
        //Toolbar setup
        Toolbar topToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(topToolBar);
    }


    //ToolBar function to setup res/menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        return true;
    }

    //Toolbar function for when the settings button is selected
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



    //--------------------------------------------------------------------------------------------
    //List view functions
    //--------------------------------------------------------------------------------------------

    //Adapter and list variables
    private PreviousSiteAdapter siteAdapter;
    private ArrayList<ListElement> siteList;

    //Constructor
    private class ListElement {
        ListElement() {}

        ListElement(String tl) {
            textLabel = tl;
        }

        public String textLabel;
    }


    //Adapts the siteList to previous_site_list_element.xml
    private class PreviousSiteAdapter extends ArrayAdapter<ListElement> {

        int resource;
        Context context;

        public PreviousSiteAdapter(Context _context, int _resource, List<ListElement> items) {
            super(_context, _resource, items);
            resource = _resource;
            context = _context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout newView;

            ListElement newSiteElement = getItem(position);

            // Inflate a new view if necessary.
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                LayoutInflater vi = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vi.inflate(resource,  newView, true);
            } else {
                newView = (LinearLayout) convertView;
            }

            // Fills in the view.
            TextView siteName = (TextView) newView.findViewById(R.id.site_name);
            siteName.setText(newSiteElement.textLabel);





            // Set a listener for the whole list item.
            newView.setTag(newSiteElement.textLabel);
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return newView;
        }
    }

    private void initializeListView(){
        siteList = new ArrayList<ListElement>();                          //This chunk of code sets up the adpater for the list view
        siteAdapter = new PreviousSiteAdapter(this, R.layout.previous_site_list_element, siteList);
        ListView myListView =(ListView) findViewById(R.id.previous_site_list_view);
        //myListView.setEmptyView( findViewById(R.id.empty_view) );
        myListView.setAdapter(siteAdapter);
        siteAdapter.notifyDataSetChanged();
    }

    public void addSiteToListView(){
        siteList.add(new ListElement("This is a test Element!"));
        siteList.add(new ListElement("This is the second test element"));
        siteAdapter.notifyDataSetChanged();

    }

    //--------------------------------------------------------------------------------------------
    //Bottom Navigation Functions
    //--------------------------------------------------------------------------------------------

    private void initializeBottomNavigation(){
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

    //--------------------------------------------------------------------------------------------
    //Functions to change activities
    //--------------------------------------------------------------------------------------------

    private void changeToSettings(){
        Intent settings = new Intent(HomePageActivity.this, SettingsActivity.class);
        startActivity(settings);
    }


    private void changeToMainActivity(){
        Intent next_activity = new Intent(HomePageActivity.this, MainActivity.class);
        startActivity(next_activity);
    }



}
