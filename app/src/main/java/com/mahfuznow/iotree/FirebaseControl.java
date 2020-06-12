package com.mahfuznow.iotree;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class FirebaseControl extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_control);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
    }
}