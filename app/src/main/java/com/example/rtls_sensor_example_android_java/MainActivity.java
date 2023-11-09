package com.example.rtls_sensor_example_android_java;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("rtls_sensor_example_android_java");
    }

    final String TAG = "rtls-sensor";
    private boolean started = false;
    BluetoothManager manager;
    BluetoothAdapter adapter;
    BluetoothLeAdvertiser advertiser;

    BatteryManager batteryManager;

    private EditText inputMac;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchAlarm;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchMoving;
    private RadioGroup radioGroupChannel;
    private RadioGroup radioGroupTxPower;
    private RadioGroup radioGroupMode;
    private TextView content;

    private AdvertiseCallback advertiseCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = BluetoothAdapter.getDefaultAdapter();
        advertiser = adapter.getBluetoothLeAdvertiser();

        batteryManager = (BatteryManager) this.getSystemService(Context.BATTERY_SERVICE);

        inputMac = findViewById(R.id.inputMac);
        switchAlarm = findViewById(R.id.switchAlarm);
        switchMoving = findViewById(R.id.switchMoving);
        radioGroupChannel = findViewById(R.id.radioGroupChannel);
        radioGroupTxPower = findViewById(R.id.radioGroupTxPower);
        radioGroupMode = findViewById(R.id.radioGroupMode);
        content = findViewById(R.id.content);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"MissingPermission", "SetTextI18n"})
            @Override
            public void onClick(View view) {

                if (started) {
                    advertiser.stopAdvertising(advertiseCallback);
                    Button b = (Button) view;
                    started = false;
                    b.setText("Start");
                    content.setText("");
                } else {

                    String mac = inputMac.getText().toString();
                    short id = (short) Integer.parseInt(mac, 16);
                    byte alarm = (byte) (switchAlarm.isChecked() ? 1 : 0);
                    byte isStatic = (byte) (switchMoving.isChecked() ? 0 : 1);
                    byte channel = getChannel();
                    byte txPower = getTxPower();
                    byte mode = getMode();
                    byte battery = (byte) (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) / 10);

                    Log.d(TAG, "MAC: " + mac);
                    Log.d(TAG, "ID: " + id);
                    Log.d(TAG, "Alarm: " + alarm);
                    Log.d(TAG, "Channel: " + channel);
                    Log.d(TAG, "IsStatic: " + isStatic);
                    Log.d(TAG, "TxPower: " + txPower);
                    Log.d(TAG, "Mode: " + mode);
                    Log.d(TAG, "Battery: " + battery);

                    byte[] bs = coreaiot_generate_manufacturer_specific_data(id, alarm, battery, mode, txPower, channel, isStatic);
                    String str = byteArrayToHex(bs);
                    content.setText(str);

                    int manufacturer_id = coreaiot_get_manufacturer_id();

                    if (!adapter.isEnabled()) {
                        adapter.enable();
                    }

                    AdvertiseSettings settings = new AdvertiseSettings.Builder()
                            .setConnectable(false)
                            .setTimeout(0)
                            .setAdvertiseMode(mode)
                            .setTxPowerLevel(txPower)
                            .build();
                    AdvertiseData data = new AdvertiseData.Builder()
                            .addManufacturerData(
                                    manufacturer_id,
                                    bs
                            )
                            .build();

                    advertiseCallback = new AdvertiseCallback() {
                        @Override
                        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                            super.onStartSuccess(settingsInEffect);
                            Button b = (Button) view;
                            started = true;
                            b.setText("Stop");
                        }
                    };
                    advertiser.startAdvertising(settings, data, advertiseCallback);
                }
            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    private byte getChannel() {
        switch (radioGroupChannel.getCheckedRadioButtonId()) {
            case R.id.Channel38:
                return 1;
            case R.id.Channel39:
                return 2;
            default:
                return 0;
        }
    }

    @SuppressLint("NonConstantResourceId")
    private byte getTxPower() {
        switch (radioGroupTxPower.getCheckedRadioButtonId()) {
            case R.id.TX_POWER_ULTRA_LOW:
                return AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW;
            case R.id.TX_POWER_LOW:
                return AdvertiseSettings.ADVERTISE_TX_POWER_LOW;
            case R.id.TX_POWER_HIGH:
                return AdvertiseSettings.ADVERTISE_TX_POWER_HIGH;
            default:
                return AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM;
        }
    }

    @SuppressLint("NonConstantResourceId")
    private byte getMode() {
        switch (radioGroupMode.getCheckedRadioButtonId()) {
            case R.id.MODE_LOW_POWER:
                return AdvertiseSettings.ADVERTISE_MODE_LOW_POWER;
            case R.id.MODE_LOW_LATENCY:
                return AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY;
            default:
                return AdvertiseSettings.ADVERTISE_MODE_BALANCED;
        }
    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x ", b));
        return sb.toString();
    }
    public native byte[] coreaiot_generate_manufacturer_specific_data(short id, byte alarm, byte battery, byte advertise_mode,
                                                                      byte tx_power_level, byte channel, byte is_static);

    public native int coreaiot_get_manufacturer_id();
}