package com.victoryroad.cheers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.victoryroad.cheers.dataclasses.CheckIn;
import com.victoryroad.cheers.dataclasses.Drink;
import com.victoryroad.cheers.dummy.DummyContent;
import com.victoryroad.cheers.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MyFeedFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    public MyDrinkCardRecyclerViewAdapter mAdapter;
    public static List<CheckIn> CheckIns = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyFeedFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MyFeedFragment newInstance(String param1, String param2) {
        MyFeedFragment fragment = new MyFeedFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mAdapter = new MyDrinkCardRecyclerViewAdapter(CheckIns, mListener);

        getDrinksForCurrentUser();

//        CheckIn dummy = new CheckIn("Rogue Dead Guy", new LatLng(49.314, 92.729), new Date());
//        CheckIn dummy1 = new CheckIn("Sam Adams Boston Lager", new LatLng(49.314, 92.729), new Date());
//        CheckIn dummy2 = new CheckIn("Margarita", new LatLng(49.314, 92.729), new Date());
//        CheckIn dummy3 = new CheckIn("Dirty Shirley", new LatLng(49.314, 92.729), new Date());
//        CheckIn dummy4 = new CheckIn("Rum & Coke", new LatLng(49.314, 92.729), new Date());
//
//        CheckIns.add(dummy);
//        CheckIns.add(dummy1);
//        CheckIns.add(dummy2);
//        CheckIns.add(dummy3);
//        CheckIns.add(dummy4);
    }

    private void getDrinksForCurrentUser() {
        String userId = MainActivity.user.getUserID();
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
//                            Date time = (new Gson()).fromJson(timeString, Date.class);

                                    CheckIn checkin = new CheckIn(drinkName, location, time);
                                    checkin.Categories = categories;
                                    CheckIns.add(checkin);

                                    mAdapter.notifyDataSetChanged();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drinkcard_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(CheckIn item);
    }
}
