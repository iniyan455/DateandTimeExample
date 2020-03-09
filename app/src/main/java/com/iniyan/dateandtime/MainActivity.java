package com.iniyan.dateandtime;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String TIME_SERVER = "time-a.nist.gov";
    public static String TAG = MainActivity.class.getName();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.txt);
        TextView text_eaadhaar_help_desc = findViewById(R.id.text_eaadhaar_help_desc);
        text_eaadhaar_help_desc.setText(
                Html.fromHtml(getResources().getString(R.string.eaadhaar_pass_desc)));



        //System Time Taking
        DateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
        Date dateobj = new Date();
        System.out.println(df.format(dateobj));


       long elapsedTime= SystemClock.uptimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(elapsedTime);

        Log.e(TAG,"SystemClock===="+calendar.getTime());

        Calendar systemcal = Calendar.getInstance();
        long systemTime=  System.currentTimeMillis();
        systemcal.setTimeInMillis(systemTime);
        Log.e(TAG,"System===="+systemcal.getTime());


        textView.setText("" + dateobj);
//        textView.setText(
//                getResources().getString(R.string.eaadhaar_pass_desc));

        Calendar cal = Calendar.getInstance();
//
//        Log.e(TAG, "==cal Date ==" + cal.getTime());
//        LocalDate localDate = LocalDate.now();
//        int year = localDate.getYear();
//        int month = localDate.getMonthValue();
//        int date = localDate.getDayOfMonth();
//        Log.e(TAG, "==Local Date ==" + localDate);


        Date systemDate = Calendar.getInstance().getTime(); // the current system time
        DateFormat df1 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.ENGLISH);
        String myDates = df1.format(systemDate);
        Log.e(TAG, "==myDates Date ==" + myDates);

        TelephonyManager tMgr = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String mPhoneNumber = tMgr.getLine1Number();
        Log.e(TAG, "==mPhoneNumber Date ==" + mPhoneNumber);



//Network Time Protocol (NTP)
        new GetTimeFromNetwork().execute();

        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();

        for (Account ac : accounts) {
            String acname = ac.name;
            String actype = ac.type;
            // Take your time to look at all available accounts
            System.out.println("Accounts : " + acname + ", " + actype);

            Log.e(TAG,"Accounts :"+acname+", "+actype);
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class GetTimeFromNetwork extends AsyncTask<String, Void, String> {

      ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Log.i("pre execute", "yes");

        }


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {
                NTPUDPClient timeClient = new NTPUDPClient();
                InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
                TimeInfo timeInfo = timeClient.getTime(inetAddress);
                //long returnTime = timeInfo.getReturnTime();   //local device time
                long returnTime = timeInfo.getMessage().getTransmitTimeStamp()
                        .getTime(); //server time
                Date time = new Date(returnTime);
              //  Log.i("time", "Time from " + TIME_SERVER + ": " + time);
                Log.e(TAG,"Time from " + TIME_SERVER + ": " + time);

            } catch (Exception e) {
                // TODO: handle exception
                Log.e("error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
          progressDialog.dismiss();

        }
    }


}
