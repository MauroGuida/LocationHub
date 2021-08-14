package com.jdt.locationhub.repository;

import androidx.lifecycle.MutableLiveData;

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

/**
 * Singleton Class
 */
public class ServerSocket {
    private static ServerSocket serverSocket;
    public static ServerSocket getServerSocket() throws IOException {
        if (serverSocket == null)
            serverSocket = new ServerSocket();

        return serverSocket;
    }

    //Connection Parameters
    private static final int SERVER_PORT = 5000;
    private static final String SERVER_IP_ADDRESS = "192.168.1.10";

    //Input and Output buffers
    private final Socket socket;
    private final InputStreamReader inputStreamReader;
    private final OutputStreamWriter outputStreamWriter;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    //Dataset
    private final List<User> userSet = new ArrayList<>();

    //TODO Server deploy
    private ServerSocket() throws IOException {
        socket = null; //new Socket(SERVER_IP_ADDRESS, SERVER_PORT);
        inputStreamReader = null; //new InputStreamReader(socket.getInputStream());
        outputStreamWriter = null; //new OutputStreamWriter(socket.getOutputStream());

        bufferedReader = null; //new BufferedReader(inputStreamReader);
        bufferedWriter = null; //new BufferedWriter(outputStreamWriter);
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

    public String sendMessage(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.flush();
        return bufferedReader.readLine();
    }

    //TODO Update clients informations from AWS
    public void updateMutableUserDataset() {
        userSet.add(new User("Davide",
                new Position.Builder().latitude(15).latitude(16).build()));
    }

    public List<User> getAllConnectedUsers() {
        updateMutableUserDataset();

        return userSet;
    }
}

