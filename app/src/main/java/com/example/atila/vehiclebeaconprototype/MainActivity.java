package com.example.atila.vehiclebeaconprototype;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.estimote.coresdk.cloud.api.CloudCallback;
import com.estimote.coresdk.cloud.api.EstimoteCloud;
import com.estimote.coresdk.cloud.model.BeaconInfo;
import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.common.exception.EstimoteCloudException;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.observation.utils.Proximity;
import com.estimote.coresdk.recognition.packets.EstimoteLocation;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Creates the beacon manager
    //public final BeaconManager beaconManager = new BeaconManager(getApplicationContext());
    private TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.textView1);
        //midlertidig
        final BeaconManager beaconManager = new BeaconManager(getApplicationContext());
        //Allows the SDK to communicate to the cloud
        EstimoteSDK.initialize(getApplicationContext(), "vehiclebeaconprototype-j17", "907e4bf380bf666da2634cf7c8fbec02");

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                beaconManager.startLocationDiscovery();
            }
        });

        beaconManager.setLocationListener(new BeaconManager.LocationListener() {
            @Override
            public void onLocationsFound(List<EstimoteLocation> beacons) {


                String beaconId = "[7c8259db97a28a6609b5da060954ef11]";

                for (EstimoteLocation beacon : beacons) {

                    //Log.d("LocationListener", "Nearby beacons: " + beacon.id.toString()+" - "+RegionUtils.computeProximity(beacon).toString());
                    if (beacon.id.toString().equals(beaconId) && RegionUtils.computeProximity(beacon) == Proximity.IMMEDIATE) {
                        Log.d("Green beacon", "Found it!");
                        showNotification("Hello world", "Looks like you're near a beacon.");
                        EstimoteCloud.getInstance().fetchBeaconDetails(beacon.id, new CloudCallback<BeaconInfo>() {
                            @Override
                            public void success(BeaconInfo beaconInfo) {
                                textView1.setText("Vehicle with regno "+String.valueOf(beaconInfo.name)+" was found!");
                            }

                            @Override
                            public void failure(EstimoteCloudException serverException) {
                                Log.d("Green beacon", "No name!");
                            }
                        });
                    }

                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //beaconManager.disconnect();
    }

    private boolean notificationAlreadyShown = false;

    public void showNotification(String title, String message) {
        if (notificationAlreadyShown) { return; }

        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        notificationAlreadyShown = true;
    }

}
