package com.jdt.locationhub.viewmodel;

import android.location.Address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jdt.locationhub.model.Position;
import com.jdt.locationhub.model.User;

import java.util.LinkedList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final List<MutableLiveData<User>> connectedUsers = new LinkedList<>();

    public void init(String username) {
        currentUser.setValue(new User(username));
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public List<? extends LiveData<User>> getAllClientsLatLong() {
        //TODO Recuperare i dati dal server
        connectedUsers.add(new MutableLiveData<>(
                new User("Davide", new Position.Builder().latitude(15.0).longitude(18.8).build())));

        return connectedUsers;
    }

    public void sendClientPosition(Address address) {
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
}
