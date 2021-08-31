package com.jdt.locationhub.viewmodel;

import android.location.Address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jdt.locationhub.model.Position;
import com.jdt.locationhub.model.User;
import com.jdt.locationhub.repository.ServerSocket;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<List<User>> connectedUsers = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPrivacyEnabled = new MutableLiveData<>();

    public void init(String username) {
        currentUser.setValue(new User(username));
        isPrivacyEnabled.setValue(false);
    }

    public void updateUsersPosition() {
        try {
            ServerSocket serverSocket = ServerSocket.getServerSocket();
            connectedUsers.setValue(serverSocket.getAllConnectedUsers());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LiveData<? extends List<User>> getAllUsersPosition() {
        return connectedUsers;
    }

    public void sendClientPosition(Address address) {
        if (!isPrivacyEnabled.getValue())
            currentUser.setValue(new User(currentUser.getValue().getUsername(), new Position.Builder()
                    .latitude(address.getLatitude())
                    .longitude(address.getLongitude())
                    .addressLine(address.getAddressLine(0))
                    .locality(address.getLocality())
                    .postalCode(address.getPostalCode())
                    .countryName(address.getCountryName())
                    .countryCode(address.getCountryCode())
                    .build()));
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void setPrivacy(boolean b) {
        isPrivacyEnabled.setValue(b);
    }

    public boolean getPrivacyStatus() {
        return isPrivacyEnabled.getValue();
    }
}
