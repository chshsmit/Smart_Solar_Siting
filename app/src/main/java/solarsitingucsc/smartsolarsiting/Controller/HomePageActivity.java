package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import solarsitingucsc.smartsolarsiting.R;

public class HomePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mAuth = FirebaseAuth.getInstance();

        initializeToolbar();
        initializeListView();
        initializeBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPastSites();
    }

    //--------------------------------------------------------------------------------------------
    //Override functions
    //--------------------------------------------------------------------------------------------

    //We want to do nothing when the back button is pressed
    @Override
    public void onBackPressed() {

    }


    //--------------------------------------------------------------------------------------------
    //Toolbar functions
    //--------------------------------------------------------------------------------------------

    private void initializeToolbar() {
        //Toolbar setup
        Toolbar topToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(topToolBar);
    }


    //ToolBar function to setup res/menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        return true;
    }

    //Toolbar function for when the settings button is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.Settings) {
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

        private String siteNameLabel;
        private String siteDateLabel;
        private String sitePowerLabel;
        private StorageReference imageStorageReference;
        private boolean loaded;
        private String uriPath;

        ListElement() {
        }

        ListElement(String name, String date, String power) {
            siteNameLabel = name;
            siteDateLabel = date;
            sitePowerLabel = power;
            loaded = false;
        }

        private void setSiteImageBitmap(StorageReference ref) {
            imageStorageReference = ref;
        }

        private void setSiteImageUri(String uri) {
            this.uriPath = uri;
        }
    }

    //Adapts the siteList to previous_site_list_element.xml
    private class PreviousSiteAdapter extends ArrayAdapter<ListElement> {

        int resource;
        Context context;

        private PreviousSiteAdapter(Context _context, int _resource, List<ListElement> items) {
            super(_context, _resource, items);
            resource = _resource;
            context = _context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            LinearLayout newView;

            final ListElement newSiteElement = getItem(position);

            // Inflate a new view if necessary.
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                LayoutInflater vi = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (vi != null)
                    vi.inflate(resource, newView, true);
            } else {
                newView = (LinearLayout) convertView;
            }

            if (newSiteElement == null)
                return newView;

            // Fills in the view.
            TextView siteName = newView.findViewById(R.id.site_name);
            siteName.setText(newSiteElement.siteNameLabel);
            TextView siteDate = newView.findViewById(R.id.site_date);
            siteDate.setText(newSiteElement.siteDateLabel);
            TextView sitePower = newView.findViewById(R.id.site_power);
            sitePower.setText(newSiteElement.sitePowerLabel);
            final ImageView siteImage = newView.findViewById(R.id.site_image);
            if (newSiteElement.imageStorageReference != null) {
                newSiteElement.imageStorageReference.getDownloadUrl().addOnSuccessListener(
                        new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!newSiteElement.loaded) {
                            newSiteElement.setSiteImageUri(uri.toString());
                            newSiteElement.loaded = true;
                            siteAdapter.notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }

            // Set a listener for the whole list item.
            newView.setTag(newSiteElement.siteNameLabel);
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            if (newSiteElement.uriPath != null) {
                // Pass image to Picasso to download, show in ImageView and caching
                Picasso.with(context)
                        .load(newSiteElement.uriPath)
                        .placeholder(R.drawable.ic_cam)
                        .into(siteImage);
            }

            return newView;
        }
    }


    //TODO:Set empty list view

    private void initializeListView() {
        siteList = new ArrayList<>();
        siteAdapter = new PreviousSiteAdapter(this, R.layout.previous_site_list_element, siteList);
        ListView myListView = findViewById(R.id.previous_site_list_view);
        //myListView.setEmptyView( findViewById(R.id.empty_view) );
        myListView.setAdapter(siteAdapter);
        siteAdapter.notifyDataSetChanged();
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return "";
    }

    private void getBitmapAndAddSite(final String name, final String userID, final ListElement listElement) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(userID + name + ".jpg");
        listElement.setSiteImageBitmap(ref);
        siteAdapter.notifyDataSetChanged();
    }

    public void showPastSites() {
        DatabaseReference mSolarSitingRef =
                FirebaseDatabase.getInstance().getReference().child("pictures");
        ValueEventListener solarSitingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    try {
                        Map solarSiting = (Map) data.getValue();
                        if (solarSiting != null &&
                                solarSiting.get("userId").equals(getCurrentUserId())) {
                            String name = (String) solarSiting.get("name");
                            boolean exists = false;
                            for (ListElement l : siteList) {
                                if (l.siteNameLabel.equals(name)) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (exists)
                                break;
                            String userId = (String) solarSiting.get("userId");
                            String date = (String) solarSiting.get("date");
                            List powerList = (List) solarSiting.get("results");
                            String power = powerList.get(powerList.size() - 1) + "kW";
                            ListElement listElement = addSiteToListView(name, date, power);
                            getBitmapAndAddSite(name, userId, listElement);
                        }
                    } catch (ClassCastException e) {
                        //ignore
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mSolarSitingRef.addValueEventListener(solarSitingListener);
        siteAdapter.notifyDataSetChanged();
    }

    private ListElement addSiteToListView(String name, String date, String power) {
        ListElement listElement = new ListElement(name, date, power);
        siteList.add(listElement);
        Collections.sort(siteList, new Comparator<ListElement>() {
            @Override
            public int compare(ListElement listElement1, ListElement listElement2) {
                DateFormat df = DateFormat.getDateTimeInstance();
                try {
                    Date date1 = df.parse(listElement1.siteDateLabel);
                    Date date2 = df.parse(listElement2.siteDateLabel);
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        siteAdapter.notifyDataSetChanged();
        return listElement;
    }

    //--------------------------------------------------------------------------------------------
    //Bottom Navigation Functions
    //--------------------------------------------------------------------------------------------

    private void initializeBottomNavigation() {
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

    private void changeToSettings() {
        Intent settings = new Intent(HomePageActivity.this, SettingsActivity.class);
        startActivity(settings);
    }


    private void changeToMainActivity() {
        Intent next_activity = new Intent(HomePageActivity.this, MainActivity.class);
        startActivity(next_activity);
    }
}
