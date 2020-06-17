package com.mahfuznow.iotree.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahfuznow.iotree.R;
import com.mahfuznow.iotree.util.DeviceUtils;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Button btn_connect_via_bluetooth, btn_connect_via_internet;
    public static final int REQUEST_CODE_DEVICE_CONNECT = 100;
    Context context = MainActivity.this;
    AlertDialog alertDialog;
    TextView dialog_error_msg;
    EditText dialog_device_name;
    EditText dialog_device_password;
    Button dialog_connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_connect_via_bluetooth = findViewById(R.id.btn_connect_via_bluetooth);
        btn_connect_via_internet = findViewById(R.id.btn_connect_via_internet);

        btn_connect_via_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DeviceScanBluetoothActivity.class));
            }
        });
        btn_connect_via_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context, R.style.CustomAlertDialog);
                View dialog_view = LayoutInflater.from(context).inflate(R.layout.dialog_layout_connect_device, null);
                builder.setView(dialog_view);
                builder.setCancelable(true);
                alertDialog = builder.create();

                dialog_error_msg = dialog_view.findViewById(R.id.error_msg);
                dialog_device_name = dialog_view.findViewById(R.id.device_name);
                dialog_device_password = dialog_view.findViewById(R.id.device_password);
                dialog_connect = dialog_view.findViewById(R.id.connect);

                dialog_connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s_device_name = dialog_device_name.getText().toString().trim();
                        String s_device_password = dialog_device_password.getText().toString().trim();
                        if (TextUtils.isEmpty(s_device_name)) {
                            dialog_device_name.requestFocus();
                            dialog_error_msg.setText(R.string.pleas_enter_device_name);
                        } else if (TextUtils.isEmpty(s_device_password)) {
                            dialog_device_password.requestFocus();
                            dialog_error_msg.setText(R.string.please_enter_password);
                        } else if (!TextUtils.isEmpty(s_device_name) && !TextUtils.isEmpty(s_device_password)) {
                            dialog_error_msg.setText("");
                            Intent intent = new Intent(MainActivity.this, DeviceControlInternetActivity.class);
                            intent.putExtra("s_device_name", s_device_name);
                            intent.putExtra("s_device_password", s_device_password);
                            startActivityForResult(intent, REQUEST_CODE_DEVICE_CONNECT);
                        }
                    }
                });
                alertDialog.show();
                //resizing dialog
                Point screen_size = DeviceUtils.getScreenSize(MainActivity.this);
                Objects.requireNonNull(alertDialog.getWindow()).setLayout((int) (screen_size.x * .8), (int) (screen_size.y * .6));
                //dialog's background and layout gravity were declared in R.style.CustomAlertDialog
            }
        });

    }

    /* Called when returning from Firebase control activity */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_DEVICE_CONNECT) {
            if (resultCode == RESULT_CANCELED) {
                String msg = null;
                if (data != null) {
                    msg = data.getExtras().getString("msg");
                }
                dialog_error_msg.setText(msg);
            } else {
                alertDialog.dismiss();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
