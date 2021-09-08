package com.jdt.locationhub.viewmodel;

import android.content.Context;
import android.location.Geocoder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jdt.locationhub.exception.NoInternetConnectionException;
import com.jdt.locationhub.exception.ServerResponseException;
import com.jdt.locationhub.model.Position;
import com.jdt.locationhub.model.User;
import com.jdt.locationhub.repository.ServerSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainViewModel extends ViewModel {
    private ServerSocket serverSocket;
    private Geocoder geocoder;

    private String username;
    private final MutableLiveData<Position> thisClientPosition = new MutableLiveData<>();
    private final MutableLiveData<Boolean> PrivacyEnabled = new MutableLiveData<>();

    private final MutableLiveData<List<Float>> clientsDiscoveryRange = new MutableLiveData<>();
    private final MutableLiveData<List<User>> connectedClients = new MutableLiveData<>();

    //-----------------------------------------------------------------------------------\\

    public void init(String username, Context context) {
        this.username = username;
        geocoder = new Geocoder(context, Locale.UK);

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

    public void closeConnection() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------\\

    public void updateOtherClientsLocation() throws NoInternetConnectionException, ServerResponseException {
        List<User> users = serverSocket.getAllConnectedUsers();

        users.removeIf(u ->
                (u.getDistance() > clientsDiscoveryRange.getValue().get(1) || u.getDistance() < clientsDiscoveryRange.getValue().get(0))
                || u.getDistance() == 0
                || (u.getPosition().getLatitude() == 0 && u.getPosition().getLongitude() == 0));

        users.forEach(u -> {
            try {
                u.getPosition().setAddressLine(geocoder.getFromLocation(u.getPosition().getLatitude(), u.getPosition().getLongitude(), 1).get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connectedClients.setValue(users);
    }

    public LiveData<? extends List<User>> getAllClientsPosition() {
        return connectedClients;
    }

    public void setClientsDiscoveryRange(List<Float> range) {
        clientsDiscoveryRange.setValue(range);

        try {
            updateOtherClientsLocation();
        } catch (NoInternetConnectionException | ServerResponseException ignored) {}
    }

    public LiveData<List<Float>> getClientsDiscoveryRange() {
        return clientsDiscoveryRange;
    }

    //-----------------------------------------------------------------------------------\\

    public void updateThisClientPosition(Double latitude, Double longitude) throws NoInternetConnectionException, ServerResponseException {
        Position position = new Position(latitude, longitude);

        //Sends the new client position to the server only if is changed and is valid
        if (!Objects.equals(thisClientPosition.getValue(), position) && !(latitude == 0 && longitude == 0)) {
            try {
                position.setAddressLine(geocoder.getFromLocation(latitude, longitude, 1).get(0).getAddressLine(0));
            } catch (IOException ignored) {
                position.setAddressLine("-----");
            }

            thisClientPosition.setValue(position);
            serverSocket.sendClientPosition(position);
        }
    }

    public LiveData<Position> getThisClientPosition() {
        return thisClientPosition;
    }

    public String getUsername() {
        return username;
    }

    //-----------------------------------------------------------------------------------\\

    public void setPrivacyEnabled(boolean b) throws ServerResponseException, NoInternetConnectionException {
        PrivacyEnabled.setValue(b);
        serverSocket.setUserPrivacy(b);
    }

    public LiveData<Boolean> isPrivacyEnabled() {
        return PrivacyEnabled;
    }
}
