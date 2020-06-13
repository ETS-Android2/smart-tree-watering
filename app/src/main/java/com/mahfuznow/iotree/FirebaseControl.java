package com.mahfuznow.iotree;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class FirebaseControl extends AppCompatActivity {
    TextView tv_device_name, tv_device_mac_address;
    ArcProgress temperature_progress, humidity_progress;
    SwitchCompat switch_pump;
    ProgressBar progress_switch_pump;
    LinearLayout ll_overlay_progress;

    FirebaseDatabase database;
    DatabaseReference myRef;

    String sDeviceName, sDeviceMacAddress, sTemperature, sHumidity, sPump;
    private String TAG = FirebaseControl.class.getSimpleName();
    private Context context = FirebaseControl.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_control);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        tv_device_mac_address = findViewById(R.id.tv_device_mac_address);
        tv_device_name = findViewById(R.id.tv_device_name);
        temperature_progress = findViewById(R.id.temperature_progress);
        humidity_progress = findViewById(R.id.humidity_progress);
        switch_pump = findViewById(R.id.switch_pump);
        progress_switch_pump = findViewById(R.id.progress_switch_pump);
        ll_overlay_progress = findViewById(R.id.ll_overlay_progress);

        database = FirebaseDatabase.getInstance();
        sDeviceName = "Smart-Tree-01";
        myRef = database.getReference(sDeviceName);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                FirebaseModel firebaseModel = dataSnapshot.getValue(FirebaseModel.class);

                assert firebaseModel != null;
                //Log.d(TAG, "Value is: " + firebaseModel.getMac()+firebaseModel.getMoisture());
                tv_device_name.setText(sDeviceName);
                tv_device_mac_address.setText(firebaseModel.getMac());
                temperature_progress.setProgress((int) firebaseModel.getTemperature());
                humidity_progress.setProgress(firebaseModel.getMoisture());

                switch_pump.setChecked(firebaseModel.isPump_trig());

                //disabling overlay progress
                ll_overlay_progress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        switch_pump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                if (Connectivity.isConnected(context)) {
                    //
                    switch_pump.setVisibility(View.GONE);
                    progress_switch_pump.setVisibility(View.VISIBLE);
                    myRef.child("pump_trig").setValue(b)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    switch_pump.setVisibility(View.VISIBLE);
                                    progress_switch_pump.setVisibility(View.GONE);

                                    Snackbar snackbar = Snackbar.make(compoundButton, "Pump has been turned " + (b ? "ON" : "OFF"), Snackbar.LENGTH_SHORT);
                                    snackbar.setAction("Action", null);
                                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                                    snackbar.show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(compoundButton, "Failed", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null)
                                            .show();
                                    switch_pump.toggle();
                                }
                            })
                    ;
                } else {
                    switch_pump.toggle();
                    Snackbar.make(compoundButton, "No internet Connection", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null)
                            .show();

                }
            }
        });
    }


}