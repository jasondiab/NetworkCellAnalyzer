package com.example.networkcellanalyzer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.networkcellanalyzer.ui.main.SectionsPagerAdapter;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.telephony.TelephonyManager.NETWORK_TYPE_1xRTT;
import static android.telephony.TelephonyManager.NETWORK_TYPE_CDMA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EDGE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_0;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_A;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_B;
import static android.telephony.TelephonyManager.NETWORK_TYPE_GPRS;
import static android.telephony.TelephonyManager.NETWORK_TYPE_GSM;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSDPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSPAP;
import static android.telephony.TelephonyManager.NETWORK_TYPE_IDEN;
import static android.telephony.TelephonyManager.NETWORK_TYPE_LTE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_UMTS;

public class MainActivity extends AppCompatActivity {
    private TelephonyManager telephonyManager;
    DBHelper DB;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // request permissions when the user opens the app
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,//causes all permission prompts to not appear to the user
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        DB = new DBHelper(this);

        //timer which refreshes every 40 second to add the data to the database
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

            telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                CellInfoUpdator cellInfoUpdator = new CellInfoUpdator(telephonyManager);
            }
            //generating snapshot and inserting data in database
            CaptureSnapshot captureSnapshot = new CaptureSnapshot(telephonyManager);
            String[] snapshot;
            Boolean Errors = true;
            snapshot = captureSnapshot.generateSnapshot();
            String op = snapshot[0];
            String pwr = snapshot[1];
            Integer ipwr = Integer.valueOf(pwr);
            String snr = snapshot[2];
            Integer isnr = Integer.valueOf(snr);
            String ntwrk = snapshot[3];
            String chnl = snapshot[4];
            Integer ichnl = Integer.valueOf(chnl);
            String id = snapshot[5];
            String time = snapshot[7];
            Integer itime = Integer.valueOf(time);
            DB.insertuserdata(itime, op, ntwrk,ipwr, isnr, ichnl, id);
            }
        }, 0, 40000); //40000 milliseconds=40second
    }
}
