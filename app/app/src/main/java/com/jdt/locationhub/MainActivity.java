package com.jdt.locationhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jdt.locationhub.exception.NoInternetConnectionException;
import com.jdt.locationhub.exception.ServerResponseException;
import com.jdt.locationhub.fragment.HomeFragment;
import com.jdt.locationhub.fragment.PeopleFragment;
import com.jdt.locationhub.fragment.SettingsFragment;
import com.jdt.locationhub.viewmodel.MainViewModel;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private MainViewModel mainViewModel;

    private AlertDialog gpsNotEnabled;
    private AlertDialog networkNotEnabled;

    //-----------------------------------------------------------------------------------\\

    private final Handler locationHandler = new Handler();
    private final Runnable locationFetcher = new Runnable() {
        @Override
        public void run() {
            boolean locationEnabled = isLocationEnabled();
            boolean networkEnabled = isNetworkEnabled();

            if (locationEnabled && networkEnabled) {
                fetchThisClientLocation(); //Updates this Client position
                fetchOtherClientsLocation(); //Updates other Clients position
            } else if(!locationEnabled)
                showGpsNotEnabledDialog();
            else
                showNetworkErrorDialog();

            locationHandler.postDelayed(this, 10000); //Repeat this process every 10 Seconds
        }
    };

    //-----------------------------------------------------------------------------------\\

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate a new ViewModel for Main activity and initialize it
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.init(getIntent().getStringExtra("USERNAME"));

        //Create fused Location client and fetch client location every 10 seconds
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationHandler.post(locationFetcher);

        //When no saved state is found goes to the Home Page
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragmentContainer, HomeFragment.newInstance())
                    .commit();

        //Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = HomeFragment.newInstance();
                    break;
                case R.id.nav_people:
                    selectedFragment = PeopleFragment.newInstance();
                    break;
                case R.id.nav_settings:
                    selectedFragment = SettingsFragment.newInstance();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragmentContainer, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        //Creates a GPS not enabled dialog
        gpsNotEnabled = new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.gps_network_not_enabled))
                .setPositiveButton(getResources().getString(R.string.open_location_settings), (dialogInterface, i) -> {
                    if (!isLocationEnabled())
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton(getResources().getString(R.string.cancel), (dialogInterface, i) -> {
                    if (!isLocationEnabled())
                        finishAffinity();
                })
                .setOnDismissListener(dialogInterface -> {
                    if (!isLocationEnabled())
                        showGpsNotEnabledDialog();
                })
                .create();

        //Creates a Network not enabled Dialog
        networkNotEnabled = new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.networkError))
                .setNegativeButton(getResources().getString(R.string.cancel), (dialogInterface, i) ->
                        finishAffinity())
                .setOnDismissListener(dialogInterface ->
                        finishAffinity())
                .create();
    }

    @Override
    protected void onDestroy() {
        mainViewModel.closeConnection();
        super.onDestroy();
    }

    //-----------------------------------------------------------------------------------\\

    @SuppressLint("MissingPermission")
    private void fetchThisClientLocation() {
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        }).addOnSuccessListener(location -> {
            //Retrieve location information
            try {
                Address address = new Geocoder(this).getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                mainViewModel.updateThisClientPosition(address);
            } catch (IOException | NoInternetConnectionException | ServerResponseException e) {
                showNetworkErrorDialog();
            }
        });
    }

    private void fetchOtherClientsLocation() {
        try {
            mainViewModel.updateOtherClientsLocation();
        } catch (NoInternetConnectionException e) {
            showNetworkErrorDialog();
        }
    }

    private boolean isLocationEnabled() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ignored) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ignored) {}

        return gps_enabled || network_enabled;
    }

    private void showGpsNotEnabledDialog() {
        if (!gpsNotEnabled.isShowing())
            gpsNotEnabled.show();
    }

    //-----------------------------------------------------------------------------------\\

    @SuppressLint("MissingPermission")
    private boolean isNetworkEnabled() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void showNetworkErrorDialog() {
        if (!networkNotEnabled.isShowing())
            networkNotEnabled.show();
    }
}