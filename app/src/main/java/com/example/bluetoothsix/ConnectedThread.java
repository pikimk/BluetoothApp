package com.example.bluetoothsix;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;


import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;


public class ConnectedThread extends Thread {
   private final BluetoothSocket mmSocket;
    private  InputStream mmInStream;
    public TextView textViewConsTemp,concentrationTextV ;

    private  OutputStream mmOutStream;
    Handler mHandler = new Handler();
    String tmpMessage = null;
    String getFinalMessage = null;
    double progressbarSum;
    String TAG = "TEST";
    boolean connStatus;
    Context c;
    NotificationManager manager;
    boolean setNotifyer = false;
    ProgressBar progressBar;
    double getFinalSum;


    public ConnectedThread (BluetoothSocket socket, TextView textViewCons, Context context,NotificationManager mng, ProgressBar prg,TextView concentrationTextVv){

       // gore  TextView concentrationTextVv

        concentrationTextV = concentrationTextVv;

        c = context;
        manager = mng;
        textViewConsTemp = textViewCons;
        mmSocket = socket;
        progressBar = prg;
       // InputStream tmpIn = null;
      //  OutputStream tmpOut = null;
        // Get the BluetoothSocket input and output streams

        try {
            mmInStream = mmSocket.getInputStream();
            // tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            // socket not created
            e.printStackTrace();
        }



        //  mmOutStream = tmpOut;
        connStatus = true;
    }

    public void run(){
        float msgNum = 0;
        double summaryNum = 0;
        double sumNum = 0;
        double finalSum = 0;
        int numCounter=1;



        while(true) {

            if(mmSocket.isConnected()) {


                try {
                    // Read from the
                    byte[] buffer = new byte[6];
                    int bytes = mmInStream.read(buffer);
                    String message = new String(buffer, 0,bytes);
                    getFinalMessage = message;
                    // message is in bytes form so reading them to obtain message


                    try{
                        msgNum = Float.parseFloat(message);
                        summaryNum = calculatePpm(numCounter,msgNum);
                       sumNum += summaryNum;
                       finalSum = sumNum/numCounter;
                       numCounter ++;



                       getFinalSum = finalSum;

                       new Handler(Looper.getMainLooper()).post(new Runnable() {
                           @Override
                           public void run() {
                               concentrationTextV.setText("Concentration in blood: "+String.format("%.3f",getFinalSum)+ " %");

                               textViewConsTemp.setText("Current ppm reading: "+getFinalMessage);
                           }
                       });


                       progressbarSum = getFinalSum*10;
                       Log.i("TEST",Double.toString(progressbarSum));
                        progressBar.setProgress(((int) Math.round(progressbarSum)));



                       Log.i(TAG,Double.toString(finalSum));



                        if(msgNum>10){
                            if (!setNotifyer){

                                        showNotification("High ppm Detected!","Warning");

                            }
                            setNotifyer = true;

                        }else{
                            setNotifyer = false;
                        }
                    }catch (NumberFormatException e){
                        Log.i(TAG,"Num eroor");
                        char[] chars = message.toCharArray();
                        for(char output :chars){
                            Log.i(TAG,Character.toString(output));
                            Log.i(TAG,"Number is"+Float.toString(msgNum));
                        }

                    }

                    tmpMessage = message;
                } catch (IOException e) {
                    // connection was lost and start your connection again
                    Log.e(TAG, "Disconnected catchme", e);
                    //((Activity)c).finish();

                    break;
                }
            }


            Log.i("TEST",tmpMessage);


        }
    }

    public double getGetFinalSum(){
        return getFinalSum;
    }

   public void inputStreamClose(){
       try {
           mmInStream.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

    public void showNotification(String content, String title){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            NotificationChannel channel = new NotificationChannel("channel1", "Channel 1",NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is channel 1");
            manager.createNotificationChannel(channel);
            long[] vibrate = {500,500,500,500,500,500,500,500,500};
            Notification notification = new NotificationCompat.Builder(c,"channel1")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setSound(uri)
                    .setContentText(content)
                    .setContentTitle(title)
                    .setVibrate(vibrate)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build();
            notificationManager.notify(1,notification);

        }else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            long[] vibrate = {500,500,500,500,500,500,500,500,500};
            Notification notification = new NotificationCompat.Builder(c,"channel1")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setSound(uri)
                    .setContentText(content)
                    .setContentTitle(title)
                    .setVibrate(vibrate)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build();
            notificationManager.notify(1,notification);

        }
    }

    public double calculatePpm(int t, float ppm){
        double timeInM = t/60;
        double COHBt;
        double A = 0.277083333;
        double AC = 0.238704282;
        double B = 0.020274129;
        double COHB0 = 0.001;
        double C = Math.exp(-((timeInM*A)/(5500*B)));
        double VcoB = 0.000141919;
        double volOfGas = ppm/10000;
        double Pico = (101.325/100)*volOfGas;

        COHBt = (1/A*(AC*COHB0))+((1-C)*VcoB) + ((1-C)*Pico);

        double concentration = (COHBt*100)*5;

       return concentration;




    }





}







