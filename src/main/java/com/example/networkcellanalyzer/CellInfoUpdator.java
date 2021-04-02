package com.example.networkcellanalyzer;

import android.annotation.SuppressLint;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.concurrent.Executor;

public class CellInfoUpdator {
    public TelephonyManager telephonyManager;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    public CellInfoUpdator(TelephonyManager TM){
        this.telephonyManager=TM;
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
            }
        };
        TelephonyManager.CellInfoCallback callback = new TelephonyManager.CellInfoCallback() {
            @Override
            public void onCellInfo( List<CellInfo> cellInfo) {

            }
        };
        telephonyManager.requestCellInfoUpdate(executor, callback);
    }
}
