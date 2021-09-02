package com.jdt.locationhub.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jdt.locationhub.R;
import com.jdt.locationhub.model.Position;
import com.jdt.locationhub.model.User;
import com.jdt.locationhub.viewmodel.MainViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private MainViewModel mainViewModel;

    private MapView mapView;
    private GoogleMap map;

    private Marker thisClientPositionMarker;
    private Map<String, Marker> clientsPositionMarkers;

    private TextView usernameTextV;
    private TextView locationTextV;
    private TextView addressTextV;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment home.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) { }
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mapView = v.findViewById(R.id.worldMap_HomeFragment);
        usernameTextV = v.findViewById(R.id.username_TextV_FragmentHome);
        locationTextV = v.findViewById(R.id.location_TextV_FragmentHome);
        addressTextV = v.findViewById(R.id.address_TextV_FragmentHome);

        usernameTextV.setText(mainViewModel.getUsername());

        mainViewModel.getUserPosition().observe(getViewLifecycleOwner(), this::updateUserPositionMarker);

        mapView.onCreate(null);
        mapView.getMapAsync(this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        initMap();
    }

    private void initMap() {
        //Create an invisible Azure marker for client position
        thisClientPositionMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(0,0))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(getResources().getString(R.string.MeOnTheMap))
                .visible(false));

        //Fetch the last known position, if available, and set the Client Azure marker
        updateUserPositionMarker(mainViewModel.getUserPosition().getValue());

        //Create a Map that contains a marker for each connected client
        clientsPositionMarkers = new HashMap<>();

        //Place a Red marker for each client connected to the server and remove the disconnected one
        mainViewModel.getAllUsersPosition().observe(getViewLifecycleOwner(), (Observer<List<User>>) users -> {
            users.forEach(user ->
                    setOnMapPoint(new LatLng(user.getPosition().getLatitude(), user.getPosition().getLongitude()), user.getUsername()));


            for (String markerName : clientsPositionMarkers.keySet()) {
                if (users.stream().noneMatch(u -> u.getUsername().equals(markerName)))
                    removeMapPointByName(markerName);
            }
        });
    }

    private void updateUserPositionMarker(Position position) {
        //Updates location information on the screen if available
        if (position != null && map != null && thisClientPositionMarker != null) {
            locationTextV.setText(getResources().getString(R.string.LatLon, String.valueOf(position.getLatitude()), String.valueOf(position.getLongitude())));
            addressTextV.setText(position.getAddressLine());

            //Refresh client position on the map
            thisClientPositionMarker.setPosition(new LatLng(position.getLatitude(), position.getLongitude()));
            if (!thisClientPositionMarker.isVisible()) {
                thisClientPositionMarker.setVisible(true);
                //Center the camera on the Client position
                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(position.getLatitude(), position.getLongitude())));
            }
        }
    }

    //Set an user Red Mark on the map or replace his position if already exists
    public void setOnMapPoint(LatLng point, String name, boolean center) {
        if (map == null || (point.latitude == 0 && point.longitude == 0)) return;

        if (clientsPositionMarkers.containsKey(name))
            Objects.requireNonNull(clientsPositionMarkers.get(name)).setPosition(point);
        else
            clientsPositionMarkers.put(name, map.addMarker(new MarkerOptions().position(point).title(name)));

        if (center) map.animateCamera(CameraUpdateFactory.newLatLng(point));
    }

    public void setOnMapPoint(LatLng point, String name) {
        setOnMapPoint(point, name, false);
    }

    public void removeMapPointByName(String name) {
        Marker marker = clientsPositionMarkers.remove(name);

        if (marker != null)
            marker.remove();
    }
}