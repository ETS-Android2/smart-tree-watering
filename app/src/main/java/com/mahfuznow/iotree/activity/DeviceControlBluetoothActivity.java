package com.mahfuznow.iotree.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.material.snackbar.Snackbar;
import com.mahfuznow.iotree.R;
import com.mahfuznow.iotree.attribute.GattAttributes;
import com.mahfuznow.iotree.service.BluetoothLowEnergyService;
import com.mahfuznow.iotree.util.DeviceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeviceControlBluetoothActivity extends AppCompatActivity {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final static String TAG = DeviceScanBluetoothActivity.class.getSimpleName();
    Context context = DeviceControlBluetoothActivity.this;
    List<String> characteristics_list = new ArrayList<>();
    TextView tv_device_name, tv_device_mac_address, tv_connection_status, tv_wifi_ssid, tv_wifi_pass;
    String s_device_name, s_device_mac_address, s_connection_status, s_wifi_ssid, s_wifi_pass;
    Button btn_wifi_set;
    ArcProgress temperature_progress, moisture_progress;
    int int_temperature, int_moisture;
    SwitchCompat switch_pump;
    ProgressBar progress_switch_pump;
    Boolean pump_trig;
    AlertDialog alertDialog;
    TextView dialog_error_msg;
    EditText dialog_wifi_ssid;
    EditText dialog_wifi_pass;
    Button dialog_set;
    private BluetoothLowEnergyService mBluetoothLowEnergyService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLowEnergyService = ((BluetoothLowEnergyService.LocalBinder) service).getService();
            if (!mBluetoothLowEnergyService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                Toast.makeText(context, "Unable to initialize Bluetooth", Toast.LENGTH_LONG).show();
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLowEnergyService.connect(s_device_mac_address);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLowEnergyService = null;
        }
    };
    private boolean mConnected = false;
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLowEnergyService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLowEnergyService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLowEnergyService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());

                //enabling button to set or edit wifi info
                btn_wifi_set.setClickable(true);
                //enabling pump control button
                switch_pump.setClickable(true);

                //creating a list of characteristics list for read one by one as asynchronous
                characteristics_list.add(GattAttributes.WIFI_CHAR_UUID);
                characteristics_list.add(GattAttributes.SENSOR_CHAR_UUID);

                //calling asynchronous read characteristic
                mBluetoothLowEnergyService.readCharacteristic(GattAttributes.SERVICE_UUID, characteristics_list.get(characteristics_list.size() - 1));
            } else if (BluetoothLowEnergyService.ACTION_DATA_AVAILABLE.equals(action)) {
                String char_uuid = intent.getStringExtra(BluetoothLowEnergyService.CHAR_UUID);
                String received_data = intent.getStringExtra(BluetoothLowEnergyService.RECEIVED_DATA);
                Log.d(TAG, char_uuid + "=" + received_data);

                if (char_uuid != null && received_data != null) {
                    //deleting the previous item from list and call characteristic read for next one
                    characteristics_list.remove(characteristics_list.size() - 1);
                    if (characteristics_list.size() > 0) {
                        mBluetoothLowEnergyService.readCharacteristic(GattAttributes.SERVICE_UUID, characteristics_list.get(characteristics_list.size() - 1));
                    } else {
                        //list is empty
                        //Todo: update ui , remove black overlay loading, disconnect and connect again to receive data again
                        //mBluetoothLowEnergyService.disconnect();
                    }
                    //check individual read callback
                    if (char_uuid.equals(GattAttributes.WIFI_CHAR_UUID)) {
                        displayWifiData(received_data);
                    } else if (char_uuid.equals(GattAttributes.SENSOR_CHAR_UUID)) {
                        displaySensorData(received_data);
                    }
                }
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLowEnergyService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLowEnergyService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLowEnergyService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLowEnergyService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control_bluetooth);

        tv_device_name = findViewById(R.id.tv_device_name);
        tv_device_mac_address = findViewById(R.id.tv_device_mac_address);
        tv_connection_status = findViewById(R.id.tv_connection_status);
        tv_wifi_ssid = findViewById(R.id.tv_wifi_ssid);
        tv_wifi_pass = findViewById(R.id.tv_wifi_pass);

        btn_wifi_set = findViewById(R.id.btn_wifi_set);

        temperature_progress = findViewById(R.id.temperature_progress);
        moisture_progress = findViewById(R.id.moisture_progress);

        switch_pump = findViewById(R.id.switch_pump);
        progress_switch_pump = findViewById(R.id.progress_switch_pump);

        //getting extras from intent
        final Intent intent = getIntent();
        s_device_name = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        s_device_mac_address = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle(s_device_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception ignored) {
        }

        tv_device_name.setText(s_device_name);
        tv_device_mac_address.setText(s_device_mac_address);

        //starting service
        Intent gattServiceIntent = new Intent(this, BluetoothLowEnergyService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        btn_wifi_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });

        switch_pump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch_pump.setVisibility(View.GONE);
                progress_switch_pump.setVisibility(View.VISIBLE);
                mBluetoothLowEnergyService.writeCharacteristic(GattAttributes.SERVICE_UUID, GattAttributes.PUMP_CHAR_UUID, (b ? "true" : "false"));

                //Todo: following code should run on successfully finished execution of write characteristic
                switch_pump.setVisibility(View.VISIBLE);
                progress_switch_pump.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar.make(compoundButton, "Pump has been turned " + (b ? "ON" : "OFF"), Snackbar.LENGTH_SHORT);
                snackbar.setAction("Action", null);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                snackbar.show();
            }
        });
        String latitude = "25.7830";
        String longitude = "88.8983";
        displayWeatherData(latitude, longitude);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                s_connection_status = getString(resourceId);
                tv_connection_status.setText(s_connection_status);
            }
        });
    }

    public void displayWifiData(String received_data) {
        if (received_data != null) {
            String[] data = received_data.split(",");
            s_wifi_ssid = data[0];
            s_wifi_pass = data[1];

            tv_wifi_ssid.setText(s_wifi_ssid);
            tv_wifi_pass.setText(s_wifi_pass);
        }

    }

    public void displaySensorData(String received_data) {
        if (received_data != null) {
            String[] data = received_data.split(",");
            int_temperature = (int) Float.parseFloat(data[0]);
            int_moisture = Integer.parseInt(data[1]);
            pump_trig = Boolean.parseBoolean(data[2]);

            temperature_progress.setProgress(int_temperature);
            moisture_progress.setProgress(int_moisture);
            switch_pump.setChecked(pump_trig);
        }
    }

    public void displayWeatherData(String latitude, String longitude) {
        String weather_html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta http-equiv=”Content-type” CONTENT=”text/html; charset=utf-8″>\n" +
                "</head><body id=”weather-body” onload=”formatIframe()”>\n" +
                "<tr>\n" +
                "  <iframe id=\"forecast_embed\" type=\"text/html\" frameborder=\"0\" height=\"245\" width=\"100%\" src=\"http://forecast.io/embed/#lat="
                + latitude + "&lon=" + longitude + "&color=#02b3e4&font=Arial&units=ca\"> </iframe>\n" +
                "</tr>\n" +
                "</body>";
        WebView webview = findViewById(R.id.weather_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUseWideViewPort(true); // This is required to load full width of the page for vertical scrolling
        webview.loadData(weather_html, "text/html; charset=utf-8", "UTF-8");
    }

    private void clearUI() {
        //tv.setText(R.string.not_available);
    }

    public void showCustomDialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context, R.style.CustomAlertDialog);
        View dialog_view = LayoutInflater.from(context).inflate(R.layout.dialog_layout_set_wifi, null);
        builder.setView(dialog_view);
        builder.setCancelable(true);
        alertDialog = builder.create();

        dialog_error_msg = dialog_view.findViewById(R.id.error_msg);
        dialog_wifi_ssid = dialog_view.findViewById(R.id.wifi_ssid);
        dialog_wifi_pass = dialog_view.findViewById(R.id.wifi_pass);
        dialog_set = dialog_view.findViewById(R.id.set);

        dialog_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_wifi_ssid = dialog_wifi_ssid.getText().toString().trim();
                String s_wifi_pass = dialog_wifi_pass.getText().toString().trim();
                if (TextUtils.isEmpty(s_wifi_ssid)) {
                    dialog_wifi_ssid.requestFocus();
                    dialog_error_msg.setText(R.string.please_enter_ssid);
                } else if (TextUtils.isEmpty(s_wifi_pass)) {
                    dialog_wifi_pass.requestFocus();
                    dialog_error_msg.setText(R.string.please_enter_password);
                } else if (!TextUtils.isEmpty(s_wifi_ssid) && !TextUtils.isEmpty(s_wifi_pass)) {
                    dialog_error_msg.setText("");
                    //sending wifi info via ble characteristic write
                    mBluetoothLowEnergyService.writeCharacteristic(GattAttributes.SERVICE_UUID, GattAttributes.WIFI_CHAR_UUID, s_wifi_ssid + " " + s_wifi_pass);
                    //Todo: following code should run on successfully finished execution of write characteristic
                    alertDialog.dismiss();
                    //TODO: update ui here
                }
            }
        });
        alertDialog.show();
        //resizing dialog
        Point screen_size = DeviceUtils.getScreenSize(DeviceControlBluetoothActivity.this);
        Objects.requireNonNull(alertDialog.getWindow()).setLayout((int) (screen_size.x * .8), (int) (screen_size.y * .6));
        //dialog's background and layout gravity were declared in R.style.CustomAlertDialog
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLowEnergyService != null) {
            final boolean result = mBluetoothLowEnergyService.connect(s_device_mac_address);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLowEnergyService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLowEnergyService.connect(s_device_mac_address);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLowEnergyService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}