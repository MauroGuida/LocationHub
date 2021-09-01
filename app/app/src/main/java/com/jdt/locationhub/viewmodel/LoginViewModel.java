package com.jdt.locationhub.viewmodel;

import androidx.lifecycle.ViewModel;

import com.jdt.locationhub.exception.UsernameAlreadyInUseException;
import com.jdt.locationhub.exception.UsernameNotValidException;
import com.jdt.locationhub.repository.ServerSocket;

import java.io.IOException;
import java.util.Objects;

public class LoginViewModel extends ViewModel {
    public boolean login(String username) throws UsernameNotValidException, UsernameAlreadyInUseException, IOException {
        if (!(username.length() >= 2 && username.trim().length() > 0 && username.length() <= 10 && username.matches("[a-zA-Z0-9]*")))
            throw new UsernameNotValidException();

        return Objects.requireNonNull(ServerSocket.getServerSocket()).login(username);
    }
}
