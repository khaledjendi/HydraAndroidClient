package com.client.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.client.R;

import java.io.IOException;

public class GameActivity extends AppCompatActivity {

        private static boolean ongoingGame = false;
        private Thread receiveThread;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_game);

            if(MainActivity.srvConn != null && MainActivity.srvConn.getClientSocket().isConnected()){
                ((TextView) findViewById(R.id.txtInfo)).setText("You are connected to the server successfully!");
                receiveThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.srvConn.readFromServer(((TextView) findViewById(R.id.txtWord)), ((TextView) findViewById(R.id.txtInfo)), ((TextView) findViewById(R.id.txtTries)), ((TextView) findViewById(R.id.txtScore)));
                    }
                });
                receiveThread.start();
            }
            else{
                MainActivity.srvConn = null;
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        public void onResetClicked(View v){
            if(MainActivity.srvConn != null && MainActivity.srvConn.getClientSocket().isConnected()){
                try {
                    MainActivity.srvConn.getClientSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            MainActivity.srvConn = null;
            ongoingGame = false;
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
        }

        public void onPlayClicked(View v){
            initializeGameVariables(false);
            MainActivity.srvConn.writeToServer("new_game");
            ongoingGame = true;
        }

        public void sendLetter(View v){
            if(ongoingGame){
                try{
                    String tries = ((TextView) findViewById(R.id.txtTries)).getText().toString();
                    String word = ((TextView) findViewById(R.id.txtWord)).getText().toString();
                    if (Integer.parseInt(tries) > 0 && word.contains("-")) {
                        Button b = (Button)v;
                        String msg = b.getText().toString();
                        if (msg != null && !msg.isEmpty()) {
                            MainActivity.srvConn.writeToServer(msg);
                            System.out.println(msg);
                        }
                    }
                    else {
                        showAlert("New Game Required", "Your either do not have remaining attempts or won the game! Please play a new game!\n\nGood Luck :)");
                    }

                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }

        private void showAlert(String title, String msg){
            AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
            builder.setMessage(msg).setTitle(title);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void sendWholeWord(View v){
            if (ongoingGame) {
                String tries = ((TextView) findViewById(R.id.txtTries)).getText().toString();
                String word = ((TextView) findViewById(R.id.txtWord)).getText().toString();
                if (Integer.parseInt(tries) > 0 && word.contains("-")) {
                    String msg = ((TextView) findViewById(R.id.txtWholeWord)).getText().toString();
                    if(msg != null && !msg.isEmpty()) {
                        if (msg != null && !msg.isEmpty()) {
                            MainActivity.srvConn.writeToServer(msg);
                            System.out.println("Guessed Whole Word: " + msg);
                            ((TextView) findViewById(R.id.txtWholeWord)).setText("");
                        }
                    }
                }
                else {
                    showAlert("New Game Required", "Your either do not have remaining attempts or won the game! Please play a new game!\n\nGood Luck :)");
                }
            }
        }

        private void initializeGameVariables(boolean initScore) {
            ((TextView) findViewById(R.id.txtWord)).setText("-");
            ((TextView) findViewById(R.id.txtTries)).setText("0");
            if (initScore) {
                ((TextView) findViewById(R.id.txtScore)).setText("0");
            }
            ((TextView) findViewById(R.id.txtInfo)).setText("");
        }
}
