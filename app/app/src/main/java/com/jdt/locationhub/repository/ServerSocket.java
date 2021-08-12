package com.jdt.locationhub.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerSocket {
    private static final int SERVER_PORT = 5000;
    private static final String SERVER_IP_ADDRESS = "192.168.1.10";

    private final Socket socket;
    private final InputStreamReader inputStreamReader;
    private final OutputStreamWriter outputStreamWriter;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    public ServerSocket() throws IOException {
        socket = new Socket(SERVER_IP_ADDRESS, SERVER_PORT);
        inputStreamReader = new InputStreamReader(socket.getInputStream());
        outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

        bufferedReader = new BufferedReader(inputStreamReader);
        bufferedWriter = new BufferedWriter(outputStreamWriter);
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

}

