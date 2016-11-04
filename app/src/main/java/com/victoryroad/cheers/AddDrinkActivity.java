package com.victoryroad.cheers;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.location.LocationListener;

import java.io.Console;

public class AddDrinkActivity
        extends AppCompatActivity
        implements AlcoholCategoriesFragment.OnFragmentInteractionListener,
        EmptyFragment.OnFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        AlcoholCategoriesFragment alcoholCategoriesFragment = new AlcoholCategoriesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.alcoholList, alcoholCategoriesFragment, "INNER_CATEGORY_FRAGMENT");
        transaction.commit();

        //Listen for changes in the back stack
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                } else {
                    AlcoholCategoriesFragment fragment = ((AlcoholCategoriesFragment) getSupportFragmentManager().findFragmentByTag("INNER_CATEGORY_FRAGMENT"));
                    if (fragment != null && fragment.mParam1.size() > 0) {
                        fragment.mParam1.remove(fragment.mParam1.size() - 1);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }
}
