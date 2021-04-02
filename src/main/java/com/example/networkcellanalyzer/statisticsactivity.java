package com.example.networkcellanalyzer;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

public class statisticsactivity extends AppCompatActivity {
    Button view;
    Button statbuttongen;
    DBHelper DB;
    public TextView textView;
    protected TelephonyManager telephonyManager;
    private SimpleDateFormat mSimpleDateFormat;
    private SimpleDateFormat SimpleDateFormat;

    private Calendar mCalendar;
    private Calendar m2Calendar;
    private Calendar start1;
    private Calendar start2;


    private Activity mActivity;
    private TextView mDate;
    private TextView m2Date;
    private TextView statviewer;
    private Integer startDate;
    private Integer endDate;





    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statisticsactivity);
        view = findViewById(R.id.btnView);
        statbuttongen = findViewById(R.id.statbuttongen);
        DB = new DBHelper(this);
        textView = findViewById(R.id.textView);

        // check for permissions again to avoid crashing the app
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            CellInfoUpdator cellInfoUpdator = new CellInfoUpdator(telephonyManager);
        }

        //prepare for the Date-time selector
        mActivity = this;
        mSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
        SimpleDateFormat = new SimpleDateFormat("yyMMddHHmm");
        mDate = (TextView) findViewById(R.id.contentMain);
        mDate.setOnClickListener(textListener);
        m2Date = (TextView) findViewById(R.id.contentMain2);
        m2Date.setOnClickListener(textListener2);






        // button to generate last 15 entries in the database
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = DB.getdata();
                if(res.getCount()==0){
                    Toast.makeText(statisticsactivity.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                if (res.getCount()>15) {
                    res.moveToPosition(res.getCount() - 16);
                }
                while(res.moveToNext()){
                    buffer.append("Time Stamp :"+res.getString(0)+"\n");
                    buffer.append("Operator :"+res.getString(1)+"\n");
                    buffer.append("Network Type :"+res.getString(2)+"\n");
                    if (res.getString(3).equals("-99999")){
                        buffer.append("Signal Power : NOT FOUND"+"\n");
                    }
                    else buffer.append("Signal Power :"+res.getString(3)+"\n");

                    if (res.getString(4).equals("-99999")){
                        buffer.append("SNR : NOT FOUND"+ "\n");

                    }
                    else buffer.append("SNR :"+res.getString(4)+"\n");

                    buffer.append("Frequency Band :"+res.getString(5)+"\n");
                    buffer.append("Cell ID :"+res.getString(6)+"\n\n");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(statisticsactivity.this);
                builder.setCancelable(true);
                builder.setTitle("15 Most Recent Entries");
                builder.setMessage(buffer.toString());
                builder.show();
            }
        });

        startDate = 0;
        endDate = 1;

        //button to generate statistics collected from the database
        statbuttongen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinkedList<Double> test0=DB.extractOperator(DB,startDate,endDate);
                LinkedList<Double> test1=DB.extractNetworkType(DB,startDate,endDate);
                Double test2=DB.extractSNR(DB,startDate,endDate);
                Double test3=DB.extractSignalPowerOF2g(DB,startDate,endDate);
                Double test4=DB.extractSignalPowerOF3g(DB,startDate,endDate);
                Double test5=DB.extractSignalPowerOF4g(DB,startDate,endDate);
                statviewer = findViewById(R.id.statview);
                String op = "> Average connectivity time per operator: Alfa:"+test0.get(1).toString()+"% TOUCH:"+test0.get(0).toString()+"%\n";
                String ntwrk = "> Average connectivity time per network type: 2G:"+test1.get(0).toString()+"% 3G:"+test1.get(1).toString()+"% 4G:"+test1.get(2).toString()+"%\n";
                String g2 = "> Average signal power 2G:"+test3.toString()+"dBm\n";
                String g3 = "> Average signal power 3G:"+test4.toString()+"dBm\n";
                String g4 = "> Average signal power 4G:"+test5.toString()+"dBm\n";
                String g5 = "> Average SNR:"+test2.toString()+"dB\n";
                statviewer.setText(op+ntwrk+g2+g3+g4+g5);
            }
        });
    }

    //Start the DatePickerDialog with users current time
    private final View.OnClickListener textListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCalendar = Calendar.getInstance();
            new DatePickerDialog(mActivity, mDateDataSet, mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    // store in our calendar variable and then start the TimePickerDialog immediately
    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(mActivity, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }
    };

    //After user decided on a time, save them into our calendar instance
    private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            mDate.setText(mSimpleDateFormat.format(mCalendar.getTime()));
            startDate = Integer.valueOf(SimpleDateFormat.format(mCalendar.getTime()));
        }
    };

    // same for second picker
    private final View.OnClickListener textListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            m2Calendar = Calendar.getInstance();
            new DatePickerDialog(mActivity, mDateDataSet2, m2Calendar.get(Calendar.YEAR),
                    m2Calendar.get(Calendar.MONTH), m2Calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    private final DatePickerDialog.OnDateSetListener mDateDataSet2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            m2Calendar.set(Calendar.YEAR, year);
            m2Calendar.set(Calendar.MONTH, monthOfYear);
            m2Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(mActivity, mTimeDataSet2, m2Calendar.get(Calendar.HOUR_OF_DAY), m2Calendar.get(Calendar.MINUTE), false).show();
        }
    };

    private final TimePickerDialog.OnTimeSetListener mTimeDataSet2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            m2Calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            m2Calendar.set(Calendar.MINUTE, minute);
            m2Date.setText(mSimpleDateFormat.format(m2Calendar.getTime()));
            endDate = Integer.valueOf(SimpleDateFormat.format(m2Calendar.getTime()));

        }
    };

}