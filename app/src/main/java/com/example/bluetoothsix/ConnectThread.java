package com.example.bluetoothsix;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class ConnectThread extends Thread {

    private final String SERVICE_ID = "00001101-0000-1000-8000-00805f9b34fb";

   // private final BluetoothSocket thisSocket;
    private BluetoothDevice thisDevice;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket btSocket;

    public ConnectThread(BluetoothDevice device) {



       BluetoothSocket tmp = null;
       thisDevice = device;
       if (thisDevice != null){
           Log.i("TEST","Device not ull");
       }

        try {
            btSocket = thisDevice.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_ID));

        } catch (IOException e) {

            Log.e("TEST", "Can't connect to service");
        }
     //   thisSocket = tmp;
    }


    public void run() {
        // Cancel discovery because it otherwise slows down the connection.

        btAdapter.cancelDiscovery();

        try {
            btSocket.connect();
            Log.d("TESTING", "Connected to shit");
        } catch (IOException connectException) {
            try {
                btSocket.close();
            } catch (IOException closeException) {
                Log.e("TEST", "Can't close socket");
            }
            return;
        }

     //   btSocket = thisSocket;
    }



    public void disconnect(){
        try {

            btSocket.close();
        } catch (IOException e) {
            Log.e("TEST", "Can't close socket");
        }
    }



    public boolean isConnected(){
       if(btSocket != null){
           if (btSocket.isConnected()){
               return true;

           }else return false;
       }else return false;

    }

    public void connectRetry(){
        if(btSocket!=null){
            try {
                btSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("BT","Trying to connect");
            }
        }
    }

    BluetoothSocket getBtSocket (){
        return btSocket;
    }








    }

