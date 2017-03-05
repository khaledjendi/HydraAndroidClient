package com.client.service;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by kal on 3/5/17.
 */

public class ServerConnection {
    private PrintWriter out = null;
    private Socket clientSocket = null;
    private String connStatus;
    private Handler handler;

    public String getConnStatus() {
        return connStatus;
    }

    public void setConnStatus(String connStatus) {
        this.connStatus = connStatus;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public ServerConnection() {
        handler = new Handler(Looper.getMainLooper());
    }

    public void connect(String host, String portStr) {
        try {
            int port;
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException ex) {
                connStatus = "Unable to connect!\nPort is not numeric value!";
                return;
            }
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(host, port), 1000);
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            connStatus = "done";
        } catch (UnknownHostException e) {
            clientSocket = null;
            connStatus = "Host: " + host + " is unknown or not available in the nework.";
        } catch (IOException e) {
            clientSocket = null;
            connStatus = "Couldn't get I/O for the connection to: " + host + ".";
        }
    }

    public void readFromServer(final TextView txtWord, final TextView infoStatus, final TextView txtTries, final TextView txtScore) {
        try {
            String line;
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while ((line = in.readLine()) != null) {
                if (line.equals("ok_stopped")) {
                    break;
                }
                final String[] msg = line.split(";");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtWord.setText(msg[1].contains("-") ? msg[1].replaceAll(".(?!$)", "$0 ") : msg[1]);
                        txtTries.setText(msg[2]);
                        txtScore.setText(msg[3]);
                        switch (msg[0]) {
                            case "start":
                                infoStatus.setText("The new game has been started successfully :)");
                                break;
                            case "win":
                                infoStatus.setText("Good job! you won!");
                                break;
                            case "wrong":
                                infoStatus.setText("Sorry, Your guess was wrong!");
                                break;
                            case "correct":
                                infoStatus.setText("good guess! Continue moving on!");
                                break;
                            case "big_win":
                                infoStatus.setText("Wow! Great guess! You knew the entire word :)");
                                break;
                            case "lose":
                                infoStatus.setText("Sorry! Game over :( Please try again");
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToServer(final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                out.println(msg.toLowerCase());
                out.flush();
            }
        }).start();
    }
}
