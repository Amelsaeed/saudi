package com.example.ahmedmagdy.theclinic.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilClass {

    // calculate age from birthday
    public static String calculateAgeFromDate(String date) {

        String[] sDate = date.split("_");

        int year = Integer.parseInt(sDate[0]);
        int month = Integer.parseInt(sDate[1]);
        int day = Integer.parseInt(sDate[2]);

        Calendar cln = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        cln.set(year, month, day);

        int age = today.get(Calendar.YEAR) - cln.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < cln.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == cln.get(Calendar.MONTH)) {
            if (today.get(Calendar.DAY_OF_MONTH) < cln.get(Calendar.DAY_OF_MONTH)) {
                age--;
            }
        }

        Integer ageInt = new Integer(age);

        return ageInt.toString();
    }

    public static String getInstanceDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);

        String instanceDate = year + "_" + month + "_" + day;
        return instanceDate;
    }


    // check if network is connected
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    // get the name of day from date
    public static String getDayNameFromDate(String date) throws ParseException {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy_MM_dd");
        Date dt = inFormat.parse(date);
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String dayName = outFormat.format(dt);
        return dayName;
    }
}
