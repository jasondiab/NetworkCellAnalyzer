package com.example.networkcellanalyzer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;


public class realtimeactivity extends AppCompatActivity {
    protected TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtimeactivity2);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,//causes all permission prompts to not appear to the user
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        //necessary instance
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        CellInfoUpdator cellInfoUpdator = new CellInfoUpdator(telephonyManager);
    }

    //method called when clicking the refresher button
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void refresh(View view) {
        //linking fields in activity_main.xml
        TextView operatorText = findViewById(R.id.operatorText);
        TextView networkText = findViewById(R.id.networkText);
        TextView frequencyText = findViewById(R.id.frequencyText);
        TextView cellidText = findViewById(R.id.cellidText);
        TextView timeText = findViewById(R.id.timeText);
        TextView snrText = findViewById(R.id.snrText);
        TextView powerText = findViewById(R.id.powerText);

        //generating snapshot
        CaptureSnapshot captureSnapshot = new CaptureSnapshot(telephonyManager);
        String[] snapshot;
        Boolean Errors = true;
        snapshot = captureSnapshot.generateSnapshot();
        //setting texts in activity_main.xml
        operatorText.setText("Operator Name: " + snapshot[0]);
        if (snapshot[1].equals("-99999")){
            powerText.setText("Power in dBm: NOT FOUND");
        }
        else powerText.setText("Power in dBm: " + snapshot[1]);

        if (snapshot[2].equals("-99999")){
            snrText.setText("SNR in dB: NOT FOUND");
        }
        else snrText.setText("SNR in dB: " + snapshot[2]);

        String networkTemp = snapshot[3];
        networkText.setText("Network Type: " + networkTemp);
        frequencyText.setText("Channel Band: " + snapshot[4]);
        cellidText.setText("Cell ID/PCI: " + snapshot[5]);
        timeText.setText("Time Stamp: " +snapshot[6]);
    }
}