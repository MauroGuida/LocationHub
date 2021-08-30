package com.jdt.locationhub.viewmodel;

import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    public boolean login(String username) {
        return username.length() >= 2 && username.trim().length() > 0 && username.length() <= 10 && username.matches("[a-zA-Z0-9]*");
    }
}
