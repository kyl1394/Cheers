package com.victoryroad.cheers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlcoholCategoriesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlcoholCategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlcoholCategoriesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    public ArrayList<String> mParam1 = new ArrayList<>();
    private String mParam2;

    private String category;

    private OnFragmentInteractionListener mListener;
    private ArrayList<String> listItems=new ArrayList<String>();

    private ListView listView;
    private ArrayAdapter mAdapter;

    public AlcoholCategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlcoholCategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlcoholCategoriesFragment newInstance(ArrayList<String> param1, String param2) {
        AlcoholCategoriesFragment fragment = new AlcoholCategoriesFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getStringArrayList(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void addToCategoryList(String str) {
        mAdapter.add(str);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mParam1.add(listView.getItemAtPosition(position).toString());
                Fragment fragment = AlcoholCategoriesFragment.newInstance(mParam1, "");

                FragmentSwitcher.replaceFragmentWithAnimation(getFragmentManager(), fragment, "INNER_CATEGORY_FRAGMENT");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_alcohol_categories, container, false);

        DatabaseReference firebaseDbRef = FirebaseDatabase.getInstance().getReference("Categories");
        if (mParam1 != null) {
            for (int i = 0; i < mParam1.size(); i++) {
                firebaseDbRef = firebaseDbRef.child(mParam1.get(i));
            }
        }
        listView = (ListView) rootView.findViewById(R.id.alcoholCategoriesListView);

        List<String> initialList = new ArrayList<String>(); //load these
        mAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item, initialList);
        listView.setAdapter(mAdapter);

        firebaseDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    DataSnapshot child = (DataSnapshot) iter.next();
                    String data = "";
                    if (mParam1.size() == 0)
                        data = child.getKey();
                    else
                        data = child.getValue().toString();

                    addToCategoryList(data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (mParam1.size() > 0) {
            rootView.setFocusableInTouchMode(true);
            rootView.requestFocus();
            rootView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (mParam1.size() > 0)
                            mParam1.remove(mParam1.size() - 1);

                        getFragmentManager().popBackStack();

                        return true;
                    }
                    return false;
                }
            });
        }


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
