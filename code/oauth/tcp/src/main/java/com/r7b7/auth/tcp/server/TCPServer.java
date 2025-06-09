package com.r7b7.auth.tcp.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TCPServer {
    private final String SECRET = "mySecretToken";

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(9999)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    String received = in.readLine();
                    if (SECRET.equals(received)) {
                        out.write("AUTH_OK\n");
                        out.flush();
                        // Handle business logic
                    } else {
                        out.write("AUTH_FAILED\n");
                        out.flush();
                        socket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
