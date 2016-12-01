package com.victoryroad.cheers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.victoryroad.cheers.dataclasses.Settings;

import static com.google.android.gms.wearable.DataMap.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LiveMapFragment.OnLocationUpdateListener} interface
 * to handle interaction events.
 * Use the {@link LiveMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveMapFragment extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_REQUEST = 5;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnLocationUpdateListener mListener;
    GoogleMap myMap;
    MapView mMapView;
    private View rootView;


    private SupportMapFragment map;

    public LiveMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LiveMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LiveMapFragment newInstance() {
        LiveMapFragment fragment = new LiveMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        //Add no args. None needed
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Do nothing
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.fragment_live_map, container, false);
            MapsInitializer.initialize(this.getActivity());
            mMapView = (MapView) rootView.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);
        } catch (InflateException e) {
            Log.e(TAG, "Inflate exception");
        }
        return rootView;
    }

    public void onButtonPressed(Location loc) {
        if (mListener != null) {
            mListener.onLocationUpdate(loc);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLocationUpdateListener) {
            mListener = (OnLocationUpdateListener) context;
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

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        redrawHomeLocation();
        mMapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        myMap.getUiSettings().setMyLocationButtonEnabled(true);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST );

            } else {
                myMap.setMyLocationEnabled(true);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        GoogleMap.OnMyLocationChangeListener listener = new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location location) {

                //MainActivity.latLng = new LatLng(location.getLatitude(), location.getLongitude());
                //mMarker = mMap.addMarker(new MarkerOptions().position(loc));
                if(myMap != null) {
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0f));

                    //Replace the Listener to do nothing
                    myMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {
                            onButtonPressed(location);
                        }
                    });
                }

                mListener.onLocationUpdate(location);
            }
        };

        myMap.setOnMyLocationChangeListener(listener);

        redrawHomeLocation();
    }

    private void redrawHomeLocation() {
        LatLng homeLocation = Settings.getSettingsAndSetPreferenceContext(this.getContext()).getHomeLocation();

        if(homeLocation != null && myMap != null) {
            int circleColor = ContextCompat.getColor(this.getContext(), R.color.colorAccent);
            int alpha = 64; // out of 256

            int fill = (alpha<<24) | (0x00FFFFFF&circleColor);
            int border = (((int)(alpha * 1.2))<<24) | (0x00FFFFFF&circleColor);

            Log.w("LiveMap", ":" + Integer.toHexString(circleColor));

            //If home location set, draw it on the map
            Circle home = myMap.addCircle(new CircleOptions()
                    .center(homeLocation)
                    .radius(50)
                    .fillColor(fill)
                    .strokeWidth(5)
                    .strokeColor(border)
                    .clickable(false));
            //Don't know what to do with it. Probably nothing
            Toast.makeText(this.getContext(), "Circle drawn at " + homeLocation.toString(), Toast.LENGTH_LONG).show();

        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch(requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)

                    break;
        }
    }

    //TODO use this function
    public void addMarker(String title, double longitude, double latitude) {
        myMap.addMarker(new MarkerOptions().title(title).position(new LatLng(longitude, latitude)));
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
    public interface OnLocationUpdateListener {
        // TODO: Update argument type and name
        // We aren't using this. When we add funcitonality to only load markers that the user can see, we will use this.
        void onLocationUpdate(Location loc);
    }
}
