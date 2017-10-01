package com.example.atila.vehiclebeaconprototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.estimote.coresdk.common.config.EstimoteSDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Allows the SDK to communicate to the cloud
        EstimoteSDK.initialize(getApplicationContext(), "vehiclebeaconprototype-j17", "907e4bf380bf666da2634cf7c8fbec02");

    }
}
