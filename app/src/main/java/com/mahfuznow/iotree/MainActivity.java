package com.mahfuznow.iotree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mahfuznow.iotree.ble.DeviceScanActivity;

public class MainActivity extends AppCompatActivity {

    Button btn_connect_via_bluetooth,btn_connect_via_internet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_connect_via_bluetooth = findViewById(R.id.btn_connect_via_bluetooth);
        btn_connect_via_internet = findViewById(R.id.btn_connect_via_internet);

        btn_connect_via_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DeviceScanActivity.class));
            }
        });
    }
}
