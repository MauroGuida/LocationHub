package com.jdt.locationhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jdt.locationhub.fragment.HomeFragment;
import com.jdt.locationhub.fragment.PeopleFragment;
import com.jdt.locationhub.fragment.SettingsFragment;
import com.jdt.locationhub.viewmodel.MainViewModel;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private MainViewModel mainViewModel;

    Handler locationHandler = new Handler();
    Runnable locationFetcher = new Runnable() {
        @Override
        public void run() {
            fetchClientLocation(); //Updates Client this position
            mainViewModel.updateUsersPosition(); //Updates Users position
            locationHandler.postDelayed(this, 10000); //Repeat this process every 10 Seconds
        }
    };

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
                    selectedFragment = PeopleFragment.newInstance(null, null);
                    break;
                case R.id.nav_settings:
                    selectedFragment = SettingsFragment.newInstance(null, null);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragmentContainer, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }

    @SuppressLint("MissingPermission")
    private void fetchClientLocation() {
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
                mainViewModel.sendClientPosition(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}