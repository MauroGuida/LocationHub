package com.jdt.locationhub.viewmodel;

import android.location.Address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jdt.locationhub.exception.NoInternetConnectionException;
import com.jdt.locationhub.model.Position;
import com.jdt.locationhub.model.User;
import com.jdt.locationhub.repository.ServerSocket;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private ServerSocket serverSocket;

    private String username;
    private final MutableLiveData<Position> thisClientPosition = new MutableLiveData<>();
    private final MutableLiveData<Boolean> PrivacyEnabled = new MutableLiveData<>();

    private final MutableLiveData<List<Float>> clientsDiscoveryRange = new MutableLiveData<>();
    private final MutableLiveData<List<User>> connectedClients = new MutableLiveData<>();

    //-----------------------------------------------------------------------------------\\

    public void init(String username) {
        this.username = username;
        thisClientPosition.setValue(null);
        PrivacyEnabled.setValue(true);

        List<Float> f = new ArrayList<>();
        f.add(0f);
        f.add(500f);
        clientsDiscoveryRange.setValue(f);

        try {
            serverSocket = ServerSocket.getServerSocket();
        } catch (NoInternetConnectionException e) {
            e.printStackTrace(); //Should never occur, Login already have instantiated a Connection Socket
        }
    }

    //-----------------------------------------------------------------------------------\\

    public void updateOtherClientsLocation() throws NoInternetConnectionException {
        List<User> users = serverSocket.getAllConnectedUsers();

        users.removeIf(u -> u.getDistance() > clientsDiscoveryRange.getValue().get(1) || u.getDistance() < clientsDiscoveryRange.getValue().get(0));

        connectedClients.setValue(users);
    }

    public LiveData<? extends List<User>> getAllClientsPosition() {
        return connectedClients;
    }

    public void setClientsDiscoveryRange(List<Float> range) {
        clientsDiscoveryRange.setValue(range);

        try {
            updateOtherClientsLocation();
        } catch (NoInternetConnectionException ignored) {}
    }

    public LiveData<List<Float>> getClientsDiscoveryRange() {
        return clientsDiscoveryRange;
    }

    //-----------------------------------------------------------------------------------\\

    public void updateThisClientPosition(Address address) throws NoInternetConnectionException {
        Position position = new Position.Builder()
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .addressLine(address.getAddressLine(0))
                .locality(address.getLocality())
                .postalCode(address.getPostalCode())
                .countryName(address.getCountryName())
                .countryCode(address.getCountryCode())
                .build();

        thisClientPosition.setValue(position);
        serverSocket.sendClientPosition(position);
    }

    public LiveData<Position> getThisClientPosition() {
        return thisClientPosition;
    }

    public String getUsername() {
        return username;
    }

    //-----------------------------------------------------------------------------------\\

    public void setPrivacyEnabled(boolean b) {
        PrivacyEnabled.setValue(b);
        serverSocket.setUserPrivacy(b);
    }

    public LiveData<Boolean> isPrivacyEnabled() {
        return PrivacyEnabled;
    }
}
