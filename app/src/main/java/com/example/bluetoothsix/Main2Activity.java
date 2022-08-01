package com.example.bluetoothsix;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.quicksettings.Tile;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;

public class Main2Activity extends AppCompatActivity {

    ConnectThread upperConnect;
    BluetoothSocket btSocket;
    TextView textView,connectionStatusTextView, concentrationTextView;
    ConnectedThread connectedThread;
    Thread  whileThread;
    NotificationManager manager;
    ProgressBar progressBar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
             manager = getSystemService(NotificationManager.class);
        }


       textView = (TextView)findViewById(R.id.textViewConnected);
       connectionStatusTextView = (TextView)findViewById(R.id.connectionStatus);
       progressBar = (ProgressBar)findViewById(R.id.progressBar2);
       concentrationTextView = (TextView)findViewById(R.id.textViewConcentration);


        Intent i = getIntent();

        String mac = i.getStringExtra("MAC");
        Log.i("TEST",mac);

        final int KEEP_ACTIVITY_TRACK = 1;

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        final BluetoothDevice device = btAdapter.getRemoteDevice(mac);
        final ConnectThread connectThread = new ConnectThread(device);
        connectThread.start();


        upperConnect = connectThread;


      whileThread =  new Thread(new Runnable() {
            int index = 0;
            @Override
            public void run() {
                Looper.prepare();
                connectionStatusTextView.setText("Connection Status: Not connected!");
                int index = 0;
                while(!upperConnect.isConnected()){
                    try {

                        Thread.sleep(500);
                        Log.i("TEST","Thread sleeep");
                        if (index >= 8){
                            finish();
                            index=0;

                        }
                        index++;

                    } catch (InterruptedException e) {
                        e.printStackTrace();

                        Log.i("TEST","Thread sleep exeption");
                        break;
                    }

                }


                if (upperConnect.isConnected()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectionStatusTextView.setText("Connection Status: Connected");
                            Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();

                        }
                    });

                    btSocket = connectThread.getBtSocket();
                    connectedThread = new ConnectedThread(btSocket,textView,Main2Activity.this,manager,progressBar,concentrationTextView);
                    connectedThread.start();
                   // t.start();
                    whileThread.interrupt();
                }

            }
        });


        whileThread.start();




        /* t = new Thread(){
            @Override
            public void run() {

                while(!isInterrupted()){

                    try{
                        Thread.sleep(200);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                 //   textView.setText("Current ppm reading: " + connectedThread.getMessage());


                            }
                        });
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };*/




    }

    @Override
    public void onBackPressed() {

        if (upperConnect.isConnected()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure?, Connection will be terminated!");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {


                    connectedThread.inputStreamClose();
                    upperConnect.disconnect();
                    //t.interrupt();
                    whileThread.interrupt();
                    Main2Activity.super.onBackPressed();
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        }else {
          //  t.interrupt();
            whileThread.interrupt();
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {


        whileThread.interrupt();
       // t.interrupt();
        super.onDestroy();
    }








}




