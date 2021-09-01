package com.jdt.locationhub.repository;

import android.os.StrictMode;
import android.util.Log;

import com.jdt.locationhub.exception.UsernameAlreadyInUseException;
import com.jdt.locationhub.model.Position;
import com.jdt.locationhub.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Singleton Class
 */
public class ServerSocket {
    //Thread internet permission
    private static final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();

    //Server Responses
    private static final String ERROR_RESPONSE = "ERR";
    private static final String OK_RESPONSE = "OK";

    //Connection Parameters
    private static final int SERVER_PORT = 5000;
    private static final String SERVER_IP_ADDRESS = "192.168.178.22";

    //Input and Output buffers
    private final Socket socket;
    private final InputStreamReader inputStreamReader;
    private final OutputStreamWriter outputStreamWriter;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    //Dataset
    private final List<User> userSet = new ArrayList<>();

    //-----------------------------------------------------------------------------------\\

    private static ServerSocket serverSocket;
    public static ServerSocket getServerSocket() {
        StrictMode.setThreadPolicy(policy);

        if (serverSocket == null) {
            try {
                serverSocket = new ServerSocket();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return serverSocket;
    }

    //-----------------------------------------------------------------------------------\\

    private ServerSocket() throws IOException {
        socket = new Socket(SERVER_IP_ADDRESS, SERVER_PORT);
        inputStreamReader = new InputStreamReader(socket.getInputStream());
        outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

        bufferedReader = new BufferedReader(inputStreamReader);
        bufferedWriter = new BufferedWriter(outputStreamWriter);
    }

    private String sendMessage(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.flush();
        return bufferedReader.readLine();
    }

    public void close() throws IOException {
        if (socket != null)
            socket.close();
        if (inputStreamReader != null)
            inputStreamReader.close();
        if (outputStreamWriter != null)
            outputStreamWriter.close();
        if (bufferedReader != null)
            bufferedReader.close();
        if (bufferedWriter != null)
            bufferedWriter.close();
    }

    //-----------------------------------------------------------------------------------\\

    public boolean login(String username) throws UsernameAlreadyInUseException, IOException {
        String response = sendMessage("SIGN_UP " + username);

        if (response.equals(ERROR_RESPONSE))
            throw new UsernameAlreadyInUseException();

        return response.equals(OK_RESPONSE);
    }

    public void sendClientPosition(Position p) {
        try {
            sendMessage("SEND_LOCATION " + p.serialize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO Update clients informations from AWS
    private void updateUsersLocation() {
        User u = new User("Davide",
                new Position.Builder().latitude(15).latitude(16).build());

        if (userSet.contains(u))
            userSet.get(userSet.indexOf(u)).setPosition(u.getPosition());
        else
            userSet.add(u);

        try {
            Log.d("SERVERTEST", sendMessage("GET_LOCATIONS "));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllConnectedUsers() {
        updateUsersLocation();

        return userSet;
    }

    public void setUserPrivacy(boolean b) {
        try {
            sendMessage("SET_PRIVACY " + (b ? 1 : 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

