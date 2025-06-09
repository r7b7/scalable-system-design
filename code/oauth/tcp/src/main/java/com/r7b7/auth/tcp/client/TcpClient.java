package com.r7b7.auth.tcp.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TcpClient {
    private final String SECRET = "mySecretToken";

    @PostConstruct
    public void startClient() {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 9999)) {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Instant start = Instant.now();

                out.write(SECRET + "\n");

                out.flush();

                String response = in.readLine();
                Duration timeElapsed = Duration.between(start, Instant.now());

                System.out.println("Server Response: " + response + " in "+ timeElapsed.toMillis() + "ms");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
