package com.client.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.client.R;
import com.client.service.ServerConnection;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mProgress;
    public static ServerConnection srvConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void onConnectClicked(View view){
        if(mProgress.getVisibility() != View.VISIBLE) {
            mProgress.setVisibility(View.VISIBLE);
            String sIP, sPort;
            sIP = ((EditText)findViewById(R.id.srvIp)).getText().toString();
            sPort = ((EditText)findViewById(R.id.port)).getText().toString();

            new SocketConnection().execute(sIP, sPort);
        }
    }

    private class SocketConnection extends AsyncTask<String, Void, ServerConnection> {

        @Override
        protected ServerConnection doInBackground(String... configs) {
            String sIP = configs[0];
            String sPort = configs[1];

            srvConn = new ServerConnection();
            srvConn.connect(sIP, sPort);

            return srvConn;
        }

        @Override
        protected void onPostExecute(ServerConnection serverConn) {
            mProgress.setVisibility(View.GONE);
            if(serverConn != null && serverConn.getClientSocket() != null && serverConn.getClientSocket().isConnected()){
                ((TextView) findViewById(R.id.infoStatus)).setText("You are connected to the server successfully!");
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
            else {
                if(serverConn != null && serverConn.getConnStatus() != null && !serverConn.getConnStatus().isEmpty()){
                    ((TextView) findViewById(R.id.infoStatus)).setText("Connection Error: "+serverConn.getConnStatus());
                }
                serverConn = null;
            }
        }
    }
}
