package com.victoryroad.cheers;

import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.victoryroad.cheers.dataclasses.CheckIn;
import com.victoryroad.cheers.dataclasses.UserDat;
import com.victoryroad.cheers.dummy.DummyContent;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity implements LiveMapFragment.OnLocationUpdateListener, MyFeedFragment.OnListFragmentInteractionListener, DrinkFeedFragment.OnListFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private LiveMapFragment mLiveMapFragment;
    private MyFeedFragment mMyFeedFragment;
    private DrinkFeedFragment mDrinkFeedFragment;

    public static UserDat user;
    public static LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLiveMapFragment = LiveMapFragment.newInstance();
        mMyFeedFragment = MyFeedFragment.newInstance("ThirdFragment", "Param 2");
        mMyFeedFragment.mAdapter = new MyDrinkCardRecyclerViewAdapter(mMyFeedFragment.CheckIns, mMyFeedFragment.mListener);

        mDrinkFeedFragment = DrinkFeedFragment.newInstance("FirstFragment", "Param 2");
        mDrinkFeedFragment.mAdapter = new MyDrinkCardRecyclerViewAdapter(mDrinkFeedFragment.CheckIns, mDrinkFeedFragment.mListener);

        String userGson = getIntent().getStringExtra("User");
        user = (new Gson()).fromJson(userGson, UserDat.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_friends"));
        getDrinksForFeed();
        getDrinksForCurrentUser();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_add_drink:
                // Create new fragment and transaction
                Intent addDrinkIntent = new Intent(getApplicationContext(), AddDrinkActivity.class);
                addDrinkIntent.putExtra("UserID", user.getUserID());
                startActivity(addDrinkIntent);
                //finish(); // Possibly might cause issues?
                break;
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;

            case R.id.action_logout_button:
                LoginManager.getInstance().logOut();
//                mMyFeedFragment.CheckIns.clear();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationUpdate(Location loc) {
        MainActivity.latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    @Override
    public void onListFragmentInteraction(CheckIn item) {

    }

    private void getDrinksForCurrentUser() {
        String userId = Profile.getCurrentProfile().getId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Checkins");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    DataSnapshot child = (DataSnapshot) iter.next();
                    final String checkinKey = child.getKey();

                    DatabaseReference checkinRef = FirebaseDatabase.getInstance().getReference("Checkins").child(checkinKey);
                    checkinRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot checkinDataSnapshot) {
//                            CheckIn in = dataSnapshot.child(checkinKey).getValue(CheckIn.class);
//                            CheckIn in = (new Gson()).fromJson(dataSnapshot.child(checkinKey), CheckIn.class);
                            String drinkKey = checkinDataSnapshot.child("DrinkKey").getValue(String.class);

                            FirebaseDatabase.getInstance().getReference("Drinks").child(drinkKey).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String drinkName = dataSnapshot.child("Name").getValue(String.class);
                                    ArrayList<String> categories = new ArrayList<>();

                                    for (DataSnapshot category : dataSnapshot.child("Categories").getChildren()) {
                                        categories.add(category.getKey());
                                    }

                                    double lat = checkinDataSnapshot.child("Location").child("latitude").getValue(double.class);
                                    double lng = checkinDataSnapshot.child("Location").child("longitude").getValue(double.class);
                                    LatLng location = new LatLng(lat, lng);
                                    mLiveMapFragment.addMarker(drinkName, lng, lat);
                                    Date time = checkinDataSnapshot.child("Time").getValue(Date.class);
//                            Date time = (new Gson()).fromJson(timeString, Date.class);

                                    CheckIn checkin = new CheckIn(drinkName, location, time);
                                    checkin.Categories = categories;
                                    mMyFeedFragment.CheckIns.add(checkin);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDrinksForFeed() {
        String userId = Profile.getCurrentProfile().getId();

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + userId + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray arr = response.getJSONObject().getJSONArray("data");







                            for (int i = 0; i < arr.length(); i++) {
                                Map<String, String> map = new HashMap<>();
                                String id = arr.getJSONObject(i).get("id").toString();
                                String name = arr.getJSONObject(i).get("name").toString();

                                MyCallable getDataForCard = new GetDataForCard(map);

                                iterateCheckinsForUser(id, name);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }

    private void iterateCheckinsForUser(final String id, final String name) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(id).child("Checkins");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final String checkinKey = child.getKey();

                    DatabaseReference checkinRef = FirebaseDatabase.getInstance().getReference("Checkins").child(checkinKey);
                    checkinRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot checkinDataSnapshot) {
                            try {
                                getDataForCard(checkinDataSnapshot, id, name);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getDataForCard(final DataSnapshot snapshot, final String id, final String name) {
        String drinkKey = snapshot.child("DrinkKey").getValue(String.class);

        FirebaseDatabase.getInstance().getReference("Drinks").child(drinkKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String drinkName = dataSnapshot.child("Name").getValue(String.class);
                ArrayList<String> categories = new ArrayList<>();

                for (DataSnapshot category : dataSnapshot.child("Categories").getChildren()) {
                    categories.add(category.getKey());
                }

                double lat = snapshot.child("Location").child("latitude").getValue(double.class);
                double lng = snapshot.child("Location").child("longitude").getValue(double.class);
                LatLng location = new LatLng(lat, lng);
                mLiveMapFragment.addMarker(drinkName, lng, lat);
                Date time = snapshot.child("Time").getValue(Date.class);

                CheckIn checkin = new CheckIn(drinkName, location, time);
                checkin.Categories = categories;
                checkin.id = id;
                checkin.userName = name;
                mDrinkFeedFragment.CheckIns.add(checkin);
                mDrinkFeedFragment.mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class GetDataForCard extends MyCallable {
        public DataSnapshot checkinDataSnapshot;
        public HashMap<String, String> params;

        public GetDataForCard(Map<String, String> _params) {
            DataSnapshot checkinDataSnapshot = this.dataSnapshot;
            Map<String, String> params = _params;
        }

        @Override
        public Void call() throws Exception {


            return null;
        }
    };

    public static abstract class MyCallable<Void> implements Callable<Void> {
        DataSnapshot dataSnapshot;

        void callWithParam(DataSnapshot snapshot) throws Exception {
            dataSnapshot = snapshot;
            call();
        }
    };

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    mDrinkFeedFragment.mAdapter.notifyDataSetChanged();
                    return mDrinkFeedFragment;
                case 1:
                    return mLiveMapFragment;
                case 2:
                    mMyFeedFragment.mAdapter.notifyDataSetChanged();
                    return mMyFeedFragment;
                default: return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "DRINK FEED";
                case 1:
                    return "LIVE MAP";
                case 2:
                    return "MY DRINKS";
            }
            return null;
        }
    }
}
