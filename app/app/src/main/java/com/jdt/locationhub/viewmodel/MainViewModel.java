package com.jdt.locationhub.viewmodel;

import android.location.Address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jdt.locationhub.exception.NoInternetConnectionException;
import com.jdt.locationhub.model.Position;
import com.jdt.locationhub.model.User;
import com.jdt.locationhub.repository.ServerSocket;

import java.util.List;
import java.util.Objects;

public class MainViewModel extends ViewModel {
    private ServerSocket serverSocket;

    private String username;
    private final MutableLiveData<Position> userPosition = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPrivacyEnabled = new MutableLiveData<>();

    private final MutableLiveData<Float> clientsRange = new MutableLiveData<>();
    private final MutableLiveData<List<User>> connectedUsers = new MutableLiveData<>();

    //-----------------------------------------------------------------------------------\\

    public void init(String username) {
        this.username = username;
        userPosition.setValue(null);
        isPrivacyEnabled.setValue(true);
        clientsRange.setValue(500.0F);

        try {
            serverSocket = ServerSocket.getServerSocket();
        } catch (NoInternetConnectionException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------\\

    public void updateUsersPosition() {
        connectedUsers.setValue(serverSocket.getAllConnectedUsers(clientsRange.getValue()));
    }

    public LiveData<? extends List<User>> getAllUsersPosition() {
        return connectedUsers;
    }

    public void setClientsRange(float range) {
        clientsRange.setValue(range);
        updateUsersPosition();
    }

    public LiveData<Float> getClientsRange() {
        return clientsRange;
    }

    //-----------------------------------------------------------------------------------\\

    public void updateClientPosition(Address address) {
        Position position = new Position.Builder()
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .addressLine(address.getAddressLine(0))
                .locality(address.getLocality())
                .postalCode(address.getPostalCode())
                .countryName(address.getCountryName())
                .countryCode(address.getCountryCode())
                .build();

        userPosition.setValue(position);
        serverSocket.sendClientPosition(position);
    }

    public LiveData<Position> getUserPosition() {
        return userPosition;
    }

    public String getUsername() {
        return username;
    }

    //-----------------------------------------------------------------------------------\\

    public void setPrivacy(boolean b) {
        isPrivacyEnabled.setValue(b);
        serverSocket.setUserPrivacy(b);
    }

    public boolean getPrivacyStatus() {
        return Objects.requireNonNull(isPrivacyEnabled.getValue());
    }
}
