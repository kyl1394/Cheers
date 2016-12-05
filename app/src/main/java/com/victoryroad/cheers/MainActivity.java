package com.victoryroad.cheers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.TimeZoneFormat;
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

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.victoryroad.cheers.dataclasses.UserDat;

import android.widget.DatePicker;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements LiveMapFragment.OnLocationUpdateListener {

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

    public static UserDat user;
    public static LatLng latLng;
    public static Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        myCalendar = Calendar.getInstance();
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
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.action_set_going_out_time:
                // Create new fragment and transaction
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.fragment_set_going_out_time);
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
        //TODO implement what to do when the location changes
    }

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
                case 1: return LiveMapFragment.newInstance("SecondFragment", "Instance 1");
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
