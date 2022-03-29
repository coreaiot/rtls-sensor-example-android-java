package com.example.rtls_sensor_example_android_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("rtls_sensor_example_android_java");
    }

    private boolean started = false;
    BluetoothManager manager;
    BluetoothAdapter adapter;
    BluetoothLeAdvertiser advertiser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = BluetoothAdapter.getDefaultAdapter();
        advertiser = adapter.getBluetoothLeAdvertiser();
    }

    @SuppressLint("MissingPermission")
    public void onClick(View view) {
        if(started) {
            advertiser.stopAdvertising(new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                }
            });
            Button b = (Button) view;
            started = false;
            b.setText("Start");
        } else {
            short id = 0x4444;
            byte alarm = 0x00;
            byte battery = 0x07;
            byte advertise_mode = 0x01;
            byte tx_power_level = 0x02;
            byte channel = 0x00;
            byte[] bs = coreaiot_generate_manufacturer_specific_data(id, alarm, battery, advertise_mode, tx_power_level, channel);
            int manufacturer_id = coreaiot_get_manufacturer_id();

            if (!adapter.isEnabled()) {
                adapter.enable();
            }

            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setConnectable(false)
                    .setTimeout(0)
                    .setAdvertiseMode(advertise_mode)
                    .setTxPowerLevel(tx_power_level)
                    .build();
            AdvertiseData data = new AdvertiseData.Builder()
                    .addManufacturerData(
                            manufacturer_id,
                            bs
                    )
                    .build();
            advertiser.startAdvertising(settings, data, new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                    Button b = (Button) view;
                    started = true;
                    b.setText("Stop");
                }
            });
        }
    }

    public native byte[] coreaiot_generate_manufacturer_specific_data(short id, byte alarm, byte battery, byte advertise_mode,
                                                                      byte tx_power_level, byte channel);
    public native int coreaiot_get_manufacturer_id();
}