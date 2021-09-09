package com.jdt.locationhub.repository;

import android.os.StrictMode;

import com.jdt.locationhub.BuildConfig;
import com.jdt.locationhub.exception.NoInternetConnectionException;
import com.jdt.locationhub.exception.ServerResponseException;
import com.jdt.locationhub.exception.UsernameAlreadyInUseException;
import com.jdt.locationhub.model.Position;
import com.jdt.locationhub.model.User;
import com.jdt.locationhub.tool.StringParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton Class
 */
public class ServerSocket {
    //Thread internet permission
    private static final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();

    //Server Responses
    private static final String ERROR_RESPONSE = "ERR";
    private static final String OK_RESPONSE = "OK";

    //Server messages
    private static final String GET_LOCATIONS = "GET_LOCATIONS";
    private static final String SIGN_UP = "SIGN_UP ";
    private static final String SEND_LOCATION = "SEND_LOCATION ";
    private static final String SET_PRIVACY = "SET_PRIVACY ";

    //Connection Parameters
    private static final int SERVER_PORT = Integer.parseInt(BuildConfig.serverPort);
    private static final String SERVER_IP_ADDRESS = BuildConfig.serverIp;

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
    public static ServerSocket getServerSocket() throws NoInternetConnectionException {
        StrictMode.setThreadPolicy(policy);

        if (serverSocket == null) {
            try {
                serverSocket = new ServerSocket();
            } catch (IOException e) {
                e.printStackTrace();
                throw new NoInternetConnectionException();
            }
        }

        return serverSocket;
    }

    //-----------------------------------------------------------------------------------\\

    private ServerSocket() throws IOException {
        socket = new Socket(SERVER_IP_ADDRESS, SERVER_PORT);
        socket.setSoTimeout(15*1000);
        socket.setKeepAlive(true);

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

    private void updateUsersLocation() throws IOException, ServerResponseException {
        String response = sendMessage(GET_LOCATIONS);

        if (response == null || response.isEmpty() || !response.startsWith(OK_RESPONSE))
            throw new ServerResponseException();

        userSet.clear();
        userSet.addAll(StringParser.usersParser(response));
    }

    //-----------------------------------------------------------------------------------\\

    public void login(String username) throws UsernameAlreadyInUseException, IOException {
        String response = sendMessage(SIGN_UP + username);

        if (response == null || response.isEmpty() || !response.equals(OK_RESPONSE))
            throw new UsernameAlreadyInUseException();
    }

    public void sendClientPosition(Position p) throws NoInternetConnectionException, ServerResponseException {
        try {
            String response = sendMessage(SEND_LOCATION + p.getLatitude() + " " + p.getLongitude());

            if (response == null || response.isEmpty() || !response.equals(OK_RESPONSE))
                throw new ServerResponseException();

        } catch (IOException e) {
            throw new NoInternetConnectionException();
        }
    }

    public List<User> getAllConnectedUsers() throws NoInternetConnectionException, ServerResponseException {
        try {
            updateUsersLocation();
        } catch (IOException e) {
            throw new NoInternetConnectionException();
        }

        return userSet;
    }

    public void setUserPrivacy(boolean b) throws ServerResponseException, NoInternetConnectionException {
        try {
            String response = sendMessage(SET_PRIVACY + (b ? 1 : 0));

            if (response == null || response.isEmpty() || !response.equals(OK_RESPONSE))
                throw new ServerResponseException();

        } catch (IOException e) {
            throw new NoInternetConnectionException();
        }
    }
}

