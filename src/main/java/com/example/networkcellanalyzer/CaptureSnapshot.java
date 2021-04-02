package com.example.networkcellanalyzer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.CellIdentity;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.telephony.TelephonyManager.NETWORK_TYPE_1xRTT;
import static android.telephony.TelephonyManager.NETWORK_TYPE_CDMA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EDGE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_0;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_A;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_B;
import static android.telephony.TelephonyManager.NETWORK_TYPE_GPRS;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSDPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSPAP;
import static android.telephony.TelephonyManager.NETWORK_TYPE_IDEN;
import static android.telephony.TelephonyManager.NETWORK_TYPE_LTE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_NR;
import static android.telephony.TelephonyManager.NETWORK_TYPE_UMTS;

public class CaptureSnapshot {
    public TelephonyManager telephonyManager;
    public List<CellInfo> cellInfoList;
    public CellInfo cellInfo;
    public SignalStrength signalStrength;
    public ServiceState serviceState;
    public CellIdentity cellIdentity;
    public CellSignalStrengthLte cellSignalStrengthLte;
    public CellSignalStrengthWcdma cellSignalStrengthWcdma;
    public CellSignalStrengthGsm cellSignalStrengthGsm;
    public PhoneStateListener phoneStateListener;
    public String networkType;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.P)
    public CaptureSnapshot(TelephonyManager TM) {
        while (true) {
            try {
                //necessary instances
                this.telephonyManager = TM;
                this.cellInfoList = telephonyManager.getAllCellInfo();
                this.cellInfo = cellInfoList.get(0);
                this.signalStrength = telephonyManager.getSignalStrength();
                this.serviceState = telephonyManager.getServiceState();
                this.cellIdentity = cellInfo.getCellIdentity();
                for (final CellInfo infoLte : cellInfoList) {
                    if (infoLte instanceof CellInfoLte && infoLte.isRegistered()) {
                        this.cellSignalStrengthLte = ((CellInfoLte) infoLte).getCellSignalStrength();
                    }
                }
                for (final CellInfo infoGsm : cellInfoList) {
                    if (infoGsm instanceof CellInfoGsm && infoGsm.isRegistered()) {
                        this.cellSignalStrengthGsm = ((CellInfoGsm) infoGsm).getCellSignalStrength();
                    }
                }
                for (final CellInfo infoWcdma : cellInfoList) {
                    if (infoWcdma instanceof CellInfoWcdma && infoWcdma.isRegistered()) {
                        this.cellSignalStrengthWcdma = ((CellInfoWcdma) infoWcdma).getCellSignalStrength();
                    }
                }
                break;
            } catch (Exception e) {
                System.out.println("Nope");
            }
        }
    }
    //THIS IS THE INTERESTING FUNCTION TO CALL OUTSIDE to obtain all the data
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String[] generateSnapshot() {
        CellInfoUpdator cellInfoUpdator = new CellInfoUpdator(telephonyManager);
        this.networkType = networkType();
        String[] snapshot;
        snapshot = new String[]{telephonyManager.getNetworkOperatorName(),
                getPower(),
                getLteSnr(),
                networkType(),
                String.valueOf(serviceState.getChannelNumber()),
                getCellId(),
                getTimeStamp(),
                getTimeStampint()
        };
        return snapshot;
    }



    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)

    //returns network type, either 2g or 3g or 4g
    public String networkType() {
        try {
            switch (telephonyManager.getDataNetworkType()) {
                case NETWORK_TYPE_EDGE:
                case NETWORK_TYPE_GPRS:
                case NETWORK_TYPE_CDMA:
                case NETWORK_TYPE_IDEN:
                case NETWORK_TYPE_1xRTT:
                    return ("2G");
                case NETWORK_TYPE_UMTS:
                case NETWORK_TYPE_HSDPA:
                case NETWORK_TYPE_HSPA:
                case NETWORK_TYPE_HSPAP:
                case NETWORK_TYPE_EVDO_0:
                case NETWORK_TYPE_EVDO_A:
                case NETWORK_TYPE_EVDO_B:
                    return ("3G");
                case NETWORK_TYPE_LTE:
                    return ("4G");
                case NETWORK_TYPE_NR:
                    return ("5G");
                default:
                    return ("Unknown");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return "NOT FOUND";
        }
    }

    //returns cell identity details in function of what networkType() returned
    public String getCellId() {
        try {
            switch (networkType) {
                case "2G":
                    return CellIdGSM();
                case "3G":
                    return CellIdWCDMA();
                case "4G":
                    return CellIdLTE();
                default:
                    return "Unknown";
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return "NOT FOUND";
        }
    }

    public String CellIdLTE() {
        try {
            String string = cellIdentity.toString();
            int mci = string.indexOf("mCi");
            int mpci = string.indexOf("mPci");
            //int mtac = string.indexOf("mTac");
            return string.substring(mci + 4, mpci - 1);//"PCI:"+string.substring(mpci+5,mtac-1)
        } catch (Exception e) {
            System.out.println(e.toString());
            return "NOT FOUND";
        }

    }

    public String CellIdGSM() {
        try {
            String string = cellIdentity.toString();
            int mcid = string.indexOf("mCid");
            int marfcn = string.indexOf("mArfcn");
            return string.substring(mcid + 5, marfcn - 1);
        } catch (Exception e) {
            System.out.println(e.toString());
            return "NOT FOUND";
        }
    }

    public String CellIdWCDMA() {
        try {
            String string = cellIdentity.toString();
            int mcid = string.indexOf("mCid");
            int mpsc = string.indexOf("mPsc");
            return string.substring(mcid + 5, mpsc - 1);
        } catch (Exception e) {
            System.out.println(e.toString());
            return "NOT FOUND";
        }
    }

    //timeStamp
    public String getTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
            String formattedDate = dateFormat.format(new Date()).toString();
            return formattedDate;
        } catch (Exception e) {
            System.out.println(e.toString());
            return "ERROR";
        }
    }
    public String getTimeStampint() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmm");
            String formattedDate = dateFormat.format(new Date());
            String str = formattedDate.replaceAll("\\D+","");
            return str;
        } catch (Exception e) {
            System.out.println(e.toString());
            return "-99999";
        }
    }


    //power in dbm for 2g 3g 4g
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String getPower() {
        CellInfoUpdator cellInfoUpdator = new CellInfoUpdator(telephonyManager);
        try {
            System.out.println(cellInfo);
            switch (networkType) {
                case "2G":
                    return String.valueOf(cellSignalStrengthGsm.getDbm());
                case "3G":
                    return String.valueOf(cellSignalStrengthWcdma.getDbm());
                case "4G":
                    return String.valueOf(cellSignalStrengthLte.getDbm());
                default:
                    return "-99999";
            }
        } catch (Exception e){
            System.out.println(e.toString());
            return "-99999";
        }
    }

    //SNR only for LTE
    public String getLteSnr(){
        try {
            if (networkType == "4G") {
                String string = signalStrength.toString();
                int rssnr = string.indexOf("rssnr");
                int cqi = string.indexOf("cqi");
                return string.substring(rssnr + 6, cqi - 1);
            } else {
                return "-99999";
            }
        } catch(Exception e) {
            System.out.println(e.toString());
            return "-99999";
        }
    }
}

