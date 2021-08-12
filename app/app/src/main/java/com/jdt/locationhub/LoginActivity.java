package com.jdt.locationhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.jdt.locationhub.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;

    private EditText usernameEditT;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        usernameEditT = findViewById(R.id.username_EditT_LoginActivity);
        loginButton = findViewById(R.id.login_Button_LoginActivity);

        loginButton.setOnClickListener(l -> {
            if (loginViewModel.login(usernameEditT.getText().toString())) {
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("USERNAME", usernameEditT.getText().toString());
                startActivity(i);
            }
        });
    }
}