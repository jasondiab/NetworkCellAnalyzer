package com.example.networkcellanalyzer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.LinkedList;

//Data Base
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "Userdata.db", null, 1);
    }

    //Constructor
    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Userdetails(Time Integer primary key, Operator String, NetworkType String ,SignalPower Integer,SNR Integer,FrequencyBand Integer,CellID String)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Userdetails");
    }

    //To insert data into DataBase
    public Boolean insertuserdata(Integer Time, String Operator, String NetworkType, Integer SignalPower, Integer SNR, Integer FrequencyBand, String CellID) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Time", Time);
        contentValues.put("Operator", Operator);
        contentValues.put("NetworkType", NetworkType);
        contentValues.put("SignalPower", SignalPower);
        contentValues.put("SNR", SNR);
        contentValues.put("FrequencyBand", FrequencyBand);
        contentValues.put("CellID", CellID);
        long result = DB.insert("Userdetails", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //To be able to Read Data
    public Cursor getdata() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Userdetails", null);
        return cursor;

    }

    //Average connectivity time per operator
    public LinkedList<Double> extractOperator(DBHelper DB, Integer lower, Integer upper) {
        Cursor res = DB.getdata();
        Double i,MTC,Alpha,android;
        i=0.0;
        MTC=0.0;
        Alpha=0.0;
        android=0.0;
        LinkedList<Double> we= new LinkedList<Double>();
        while (res.moveToNext()) {
            if (res.getInt(0) >= lower && res.getInt(0)<= upper) {
                if(res.getString(1).equals("touch")){
                    MTC=MTC+1.0;
                    i=i+1.0;
                }
                if(res.getString(1).equals("alfa")){
                    Alpha=Alpha+1.0;
                    i=i+1.0;
                }

            }
            if (res.getInt(0)>upper) {
                break;
            }
        }
        if (i==0){
            we.add(0.0);
            we.add(0.0);
            return (we);
        }
        we.add((MTC/i)*100);
        we.add((Alpha/i)*100);

        return we;
    }

    //Average connectivity time per network type
    public LinkedList<Double> extractNetworkType(DBHelper DB, Integer lower, Integer upper) {
        Cursor res = DB.getdata();
        LinkedList<Double> we= new LinkedList<Double>();
        Double i,TwoG,ThreeG,FourG;
        i=0.0;
        TwoG=0.0;
        ThreeG=0.0;
        FourG=0.0;
        while (res.moveToNext()) {
            if (res.getInt(0) >= lower && res.getInt(0)<= upper) {
                if(res.getString(2).equals("2G")){
                    TwoG=TwoG+1.0;
                    i=i+1.0;
                }
                if(res.getString(2).equals("3G")){
                    ThreeG=ThreeG+1.0;
                    i=i+1.0;
                }
                if(res.getString(2).equals("4G")){
                    FourG=FourG+1.0;
                    i=i+1.0;
                }
            }
            if (res.getInt(0)>upper) {
                break;
            }
        }
        if(i==0) {
            i=1.0;
        }
        we.add(((TwoG/i)*100));
        we.add(((ThreeG/i)*100));
        we.add(((FourG/i)*100));
        return we;
    }

    //Average SNR
    public Double extractSNR(DBHelper DB, Integer lower, Integer upper) {
        Cursor res = DB.getdata();
        Integer SNR=0;
        Integer i=0;
        while (res.moveToNext()) {
            if (res.getInt(0) >= lower && res.getInt(0)<= upper) {
                if(res.getString(2).equals("4G")){
                    if(!(res.getInt(4)<=-9999)){
                        SNR= SNR + res.getInt(4);
                        i=i+1;}
                }

            }
            if (res.getInt(0)>upper) {
                break;
            }
        }
        if(i==0) {
            return (0.0);
        }
        return ((SNR/i) +0.0);
    }

    //Average Signal Power per 2G network
    public Double extractSignalPowerOF2g(DBHelper DB, Integer lower, Integer upper) {
        Cursor res = DB.getdata();
        Double SigPower=0.0;
        Integer i=0;
        while (res.moveToNext()) {
            if (res.getInt(0) >= lower && res.getInt(0)<= upper) {
                if(res.getString(2).equals("2G")) {
                    if(!(res.getInt(3)<=-9999)){
                        SigPower=SigPower + res.getInt(3);
                        i=i+1;}
                }
            }
            if (res.getInt(0)>upper) {
                break;
            }
        }
        if(i==0) {
            return (0.0);
        }
        return (SigPower/i);
    }

    //Average Signal Power per 3G network
    public Double extractSignalPowerOF3g(DBHelper DB, Integer lower, Integer upper) {
        Cursor res = DB.getdata();
        Double SigPower=0.0;
        Integer i=0;
        while (res.moveToNext()) {
            if (res.getInt(0) >= lower && res.getInt(0)<= upper) {
                if(res.getString(2).equals("3G")) {
                    if(!(res.getInt(3)<=-9999)){
                        SigPower=SigPower +(res.getInt(3));
                        i=i+1;}
                }
            }
            if (res.getInt(0)>upper) {
                break;
            }
        }
        if(i==0) {
            return (0.0);
        }
        return (SigPower/i);
    }

    //Average Signal Power per 4G network
    public Double extractSignalPowerOF4g(DBHelper DB, Integer lower, Integer upper) {
        Cursor res = DB.getdata();
        Double SigPower=0.0;
        Integer i=0;
        while (res.moveToNext()) {
            if (res.getInt(0) >= lower && res.getInt(0)<= upper) {
                if(res.getString(2).equals("4G")) {
                    if(!(res.getInt(3)<=-9999)){
                        SigPower= SigPower +(res.getInt(3));
                        i=i+1;}
                }
            }
            if (res.getInt(0)>upper) {
                break;
            }
        }
        if(i==0) {
            return (0.0);
        }
        return (SigPower/i);
    }
}

