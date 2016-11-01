package com.victoryroad.cheers;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by krohlfing on 10/25/2016.
 */

public class FragmentSwitcher {
    public static void replaceFragmentWithAnimation(FragmentManager fragmentManager, Fragment fragment, String tag){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.alcoholList, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }
}
