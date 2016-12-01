package com.victoryroad.cheers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.victoryroad.cheers.dataclasses.Settings;

import java.util.ArrayList;

import java.util.ArrayList;

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

    private OnLocationUpdateListener mListener;
    private Circle home;
    private ArrayList<Marker> markers = new ArrayList<>();

    GoogleMap myMap;
    MapView mMapView;
    private View rootView;

    public LiveMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LiveMapFragment.
     */
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
        //if (getArguments() != null) {
            //Do nothing
        //}
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

        addMarker("Test", 42.0283, -93.648);
    }

    /**
     * Redraws the circle where the home is at.
     */
    private void redrawHomeLocation() {
        LatLng homeLocation = Settings.getSettingsFor(this.getContext()).getHomeLocation();

        if(homeLocation != null && myMap != null) {
            if(home != null) {
                this.removeMarker(homeLocation);
                home.remove();
            }
            int circleColor = ContextCompat.getColor(this.getContext(), R.color.colorAccent);
            int alpha = 64; // out of 256

            int fill = (alpha<<24) | (0x00FFFFFF&circleColor);
            int border = (((int)(alpha * 1.2))<<24) | (0x00FFFFFF&circleColor);

            //If home location set, draw it on the map
            home = myMap.addCircle(new CircleOptions()
                    .center(homeLocation)
                    .radius(Settings.getSettingsFor(this.getContext()).getHomeLocationRadius())
                    .fillColor(fill)
                    .strokeWidth(Settings.getSettingsFor(this.getContext()).getHomeLocationRadius() / 10)
                    .strokeColor(border)
                    .clickable(true));

            addMarker("Home", homeLocation, BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        }
    }

    @Override
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

    /**
     *
     * Creates a new marker to put on the map
     *
     * @param title the title for the marker
     * @param longitude the longitude where the marker is at
     * @param latitude the latitude the marker is at
     */
    @Deprecated
    public void addMarker(String title, double longitude, double latitude) {
        markers.add(
                myMap.addMarker(
                        new MarkerOptions()
                                .title(title)
                                .position(new LatLng(latitude, longitude))
                )
        );
    }

    public void addMarker(String title, LatLng location) {
        markers.add(
                myMap.addMarker(
                        new MarkerOptions()
                                .title(title)
                                .position(location)
                )
        );
    }

    public void addMarker(String title, LatLng location, BitmapDescriptor icon) {
        markers.add(
                myMap.addMarker(
                        new MarkerOptions()
                                .title(title)
                                .position(location)
                                .icon(icon)
                )
        );
    }

    /**
     * Removes a Marker on the map by finding it in the list using its title
     *
     * @param markerTitle the title of the marker to be removed
     * @return true if the marker was found. False, otherwise.
     */
    public boolean removeMarker(String markerTitle) {
        for(Marker m: markers) {
            if(m.getTitle().equalsIgnoreCase(markerTitle)) {
                markers.remove(m);
                m.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a Marker on the map by finding it in the list using its location
     *
     * @param markerLocation the location of the marker to be removed
     * @return true if the marker was found. False, otherwise.
     */
    public boolean removeMarker(LatLng markerLocation) {
        for(Marker m: markers) {
            if(m.getPosition().equals(markerLocation)) {
                markers.remove(m);
                m.remove();
                return true;
            }
        }
        return false;
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
        void onLocationUpdate(Location loc);
    }
}
