package com.victoryroad.cheers;

import android.*;
import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.TimeZoneFormat;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import com.facebook.login.LoginManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.victoryroad.cheers.dataclasses.CheckIn;
import com.victoryroad.cheers.dataclasses.CustomGMapInfoWindowAdapter;
import com.victoryroad.cheers.dataclasses.UserDat;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.concurrent.TimeUnit;

import static com.victoryroad.cheers.R.id.container;
import static com.victoryroad.cheers.R.id.match_global_nicknames;
import static com.victoryroad.cheers.R.id.timeSelector;

import org.apache.log4j.chainsaw.Main;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import static android.graphics.BitmapFactory.decodeStream;

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
    public static Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermissionsForApp();

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
        mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_friends"));
        getDrinksForFeed();
        getDrinksForCurrentUser();

        myCalendar = Calendar.getInstance();
    }

    private void getPermissionsForApp() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, }, 8);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    static int hour, min;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_add_drink:
                LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}

                if(!gps_enabled && !network_enabled) {
                    // notify user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage(MainActivity.this.getResources().getString(R.string.gps_network_not_enabled));
                    dialog.setPositiveButton(MainActivity.this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            MainActivity.this.startActivity(myIntent);
                            //get gps
                        }
                    });
                    dialog.setNegativeButton(MainActivity.this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub

                        }
                    });
                    dialog.show();
                } else {
                    // Create new fragment and transaction
                    Intent addDrinkIntent = new Intent(getApplicationContext(), AddDrinkActivity.class);
                    addDrinkIntent.putExtra("UserID", user.getUserID());
                    startActivity(addDrinkIntent);
                }
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
            case R.id.action_set_going_out_time:
                // Create new fragment and transaction
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.fragment_set_going_out_time);

                // Set default going out date
                final EditText dateSelector = (EditText) dialog.findViewById(R.id.dateSelector);
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                String folderName = formatter.format(today);
                dateSelector.setText(folderName);
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(dateSelector);
                    }

                };

                dateSelector.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new DatePickerDialog(MainActivity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                dialog.show();

                // Get Current Time
                final int mHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                final int mMinute = myCalendar.get(Calendar.MINUTE);
                final EditText timeSelector = (EditText) dialog.findViewById(R.id.timeSelector);
                final EditText returnSelector = (EditText) dialog.findViewById(R.id.returnSelector);

                // Set default going out time to current time
                final String time24Format = "H:mm"; //In which you need put here
                final DateFormat sdf = new SimpleDateFormat(time24Format, Locale.US);
                final DateFormat time12Format = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
                Date curDate = Calendar.getInstance().getTime();
                String goTime = time12Format.format(curDate);
                timeSelector.setText(goTime);

                timeSelector.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        view.setIs24HourView(false);
                                        String myFormat = "H:mm"; //In which you need put here
                                        DateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                        try {
                                            Date date = sdf.parse(hourOfDay + ":" + minute);
                                            timeSelector.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(date));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                });

                // Set default return time to current time
                final String returnTime24Format = "H:mm"; //In which you need put here
                final DateFormat return_sdf = new SimpleDateFormat(returnTime24Format, Locale.US);
                final DateFormat returnTime12Format = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
                Date curReturnDate = Calendar.getInstance().getTime();
                String returnTime = returnTime12Format.format(curReturnDate);
                returnSelector.setText(returnTime);

                // Get return time
                returnSelector.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        view.setIs24HourView(false);

                                        try {
                                            hour = hourOfDay;
                                            min = minute;
                                            Date date = sdf.parse(hourOfDay + ":" + minute);
                                            returnSelector.setText(time12Format.format(date));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                });

                // Cancel button action
                Button cancelButton;
                cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                Button saveButton;
                saveButton = (Button) dialog.findViewById(R.id.save_time);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, min);
                        calendar.set(Calendar.SECOND, 0);

                        Intent intent = new Intent(getApplicationContext(), Notification_receiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                        dialog.dismiss();
                    }
                });

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateLabel(EditText edittext) {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onLocationUpdate(Location loc) {
        MainActivity.latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    @Override
    public void onListFragmentInteraction(CheckIn item) {

    }

    private void getDrinksForCurrentUser() {
        final String userId = Profile.getCurrentProfile().getId();
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
                                    Date time = checkinDataSnapshot.child("Time").getValue(Date.class);

                                    CheckIn checkin = new CheckIn(drinkName, location, time);
                                    checkin.Categories = categories;
                                    checkin.id = userId;
                                    checkin.userName = Profile.getCurrentProfile().getName();
                                    checkin.setProfilePic(userId);
                                    mMyFeedFragment.CheckIns.add(checkin);

                                    CustomGMapInfoWindowAdapter adapter = new CustomGMapInfoWindowAdapter(mLiveMapFragment.getContext(), checkin);
                                    mLiveMapFragment.myMap.setInfoWindowAdapter(adapter);
                                    mLiveMapFragment.myMap.setOnMarkerClickListener(adapter);
                                    mLiveMapFragment.addMarker(checkin, location);
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
                final LatLng location = new LatLng(lat, lng);
//                mLiveMapFragment.addMarker(drinkName, lng, lat);
                Date time = snapshot.child("Time").getValue(Date.class);

                final CheckIn checkin = new CheckIn(drinkName, location, time);
                checkin.Categories = categories;
                checkin.id = id;
                checkin.userName = name;

                checkin.setProfilePic(id);

                mDrinkFeedFragment.CheckIns.add(checkin);
                mDrinkFeedFragment.mAdapter.notifyDataSetChanged();
                mLiveMapFragment.addMarker(checkin, location);
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
