
package com.mahfuznow.iotree.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mahfuznow.iotree.R;
import com.mahfuznow.iotree.adapter.ScanDeviceListAdapter;
import com.mahfuznow.iotree.util.GpsUtils;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanBluetoothActivity extends AppCompatActivity {

    private final static String TAG = DeviceScanBluetoothActivity.class.getSimpleName();
    public BluetoothAdapter mBluetoothAdapter;
    public boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_LOC = 2;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private boolean isGPS;

    ListView listView;
    private ScanDeviceListAdapter mScanDeviceListAdapter;
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mScanDeviceListAdapter.addDevice(device);
                            mScanDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            final BluetoothDevice device = mScanDeviceListAdapter.getDevice(i);
            if (device == null) return;
            //final Intent intent = new Intent(this, DeviceControlActivity.class);
            final Intent intent = new Intent(DeviceScanBluetoothActivity.this, DeviceControlBluetoothActivity.class);
            intent.putExtra(DeviceControlBluetoothActivity.EXTRAS_DEVICE_NAME, device.getName());
            intent.putExtra(DeviceControlBluetoothActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan_bluetooth);
        listView = findViewById(R.id.list_view);

        try {
            getSupportActionBar().setTitle(R.string.title_select_your_device);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch (Exception e) {
            //Log.d(TAG,e.getMessage());
        }

        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //request location permission
        Log.d(TAG, "Request Location Permissions:");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
        //enable location
        //this opens setting for enabling gps
//        Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        this.startActivityForResult(enableLocationIntent, REQUEST_ENABLE_LOC);

        //this creates a dialog to turn on gps
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            case R.id.menu_scan:
                mScanDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    //check whether user grant those bluetooth and location enable requests or not
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_ENABLE_LOC) {
                isGPS = true; // flag maintain before get location
        }
        // User choose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        // User choose not to enable location.
        if (requestCode == REQUEST_ENABLE_LOC && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Initializes list view adapter.
        mScanDeviceListAdapter = new ScanDeviceListAdapter(DeviceScanBluetoothActivity.this);
        listView.setAdapter(mScanDeviceListAdapter);
        listView.setOnItemClickListener(listener);
        listView.setItemsCanFocus(true);

        scanLeDevice(true);
    }

    //granting location permission
    private static int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mScanDeviceListAdapter.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if(requestCode == PERMISSION_REQUEST_CODE)
        {
            //Do something based on grantResults
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "coarse location permission granted");
            }
            else
            {
                Log.d(TAG, "coarse location permission denied");
            }
        }
    }
}
