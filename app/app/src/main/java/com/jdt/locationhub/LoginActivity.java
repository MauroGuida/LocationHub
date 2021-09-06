package com.jdt.locationhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.jdt.locationhub.exception.NoInternetConnectionException;
import com.jdt.locationhub.exception.UsernameAlreadyInUseException;
import com.jdt.locationhub.exception.UsernameNotValidException;
import com.jdt.locationhub.tool.DoneOnEditorActionListener;
import com.jdt.locationhub.viewmodel.LoginViewModel;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    private boolean locationPermissionGranted = false;

    private LoginViewModel loginViewModel;

    private TextInputLayout usernameEditT;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        usernameEditT = findViewById(R.id.username_TextInputL_LoginActivity);
        loginButton = findViewById(R.id.login_Button_LoginActivity);

        //Check and Request Location permission
        getLocationPermission();

        loginButton.setOnClickListener(l -> login());
        usernameEditT.getEditText().setOnEditorActionListener(new DoneOnEditorActionListener());
    }

    private void login() {
        try {
            if (locationPermissionGranted) {
                loginViewModel.login(usernameEditT.getEditText().getText().toString());
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("USERNAME", usernameEditT.getEditText().getText().toString());
                startActivity(i);
            } else
                Toast.makeText(this, getResources().getString(R.string.locationPermissionNotGranted), Toast.LENGTH_LONG).show();
        } catch (UsernameNotValidException e) {
            usernameEditT.setError(getResources().getString(R.string.usernameNotValid));
        } catch (UsernameAlreadyInUseException e) {
            usernameEditT.setError(getResources().getString(R.string.usernameAlreadyInUse));
        } catch (IOException | NoInternetConnectionException e) {
            Toast.makeText(this, getResources().getString(R.string.networkError), Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Request location permission.
     * The result of the permission request is handled
     * by a callback, onRequestPermissionsResult.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
    }
}