package com.example.bluetoothsix;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button buttonShow;
    ListView listViewDevices;
    BluetoothDevice btDevice;



   private String[] deviceName;
    private String[] deviceMacAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonShow = (Button) findViewById(R.id.buttonPaired);
        listViewDevices = (ListView)findViewById(R.id.listViewItems);

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            Toast.makeText(getApplicationContext(),"Bluetooth not supported on this device!",Toast.LENGTH_LONG).show();
            buttonShow.setEnabled(false);
        }





        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!btAdapter.isEnabled()){
                    btEnable();
                }else if(btAdapter.isEnabled()){

                    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                    int index = 0;
                    String[] name = new String[pairedDevices.size()];
                    String[] macAdress = new String[pairedDevices.size()];
                    if(pairedDevices.size()>0){
                        for (BluetoothDevice device : pairedDevices){
                            name[index] = device.getName();
                            macAdress[index] = device.getAddress();
                            index++;
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,name);
                        listViewDevices.setAdapter(adapter);
                        deviceName = name;
                        deviceMacAdress = macAdress;

                    }

                }
            }
        });

        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String deviceMac = deviceMacAdressAtIndex(position);
                btDevice = btAdapter.getRemoteDevice(deviceMac);

                        Intent i = new Intent(getApplicationContext(),Main2Activity.class);
                         i.putExtra("MAC",deviceMac);
                        startActivity(i);


                    }


        });


    }

    private void btEnable(){
        //enable bluetooth
        try {
            int REQUEST_ENABLE_BT = 1;
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, REQUEST_ENABLE_BT);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Bluetooth error!",Toast.LENGTH_SHORT).show();
        }
    }

    public String deviceNameAtIndex(int index){
        return deviceName[index];
    }

    public String deviceMacAdressAtIndex(int index){
        return  deviceMacAdress[index];
    }








}
