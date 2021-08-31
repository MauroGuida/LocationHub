package com.jdt.locationhub.viewmodel;

import androidx.lifecycle.ViewModel;

import com.jdt.locationhub.exception.UsernameAlreadyInUseException;
import com.jdt.locationhub.exception.UsernameNotValidException;

public class LoginViewModel extends ViewModel {
    public boolean login(String username) throws UsernameNotValidException, UsernameAlreadyInUseException {
        if (!(username.length() >= 2 && username.trim().length() > 0 && username.length() <= 10 && username.matches("[a-zA-Z0-9]*")))
            throw new UsernameNotValidException();

        if (!isUsernameAvailable(username))
            throw new UsernameAlreadyInUseException();

        return true;
    }

    public boolean isUsernameAvailable(String Username) {
        return true;
    }
}
