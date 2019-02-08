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

        if (date == null || !date.contains("_")){
            return "0";
        }
        String[] sDate = date.split("_");

        int year = Integer.parseInt(sDate[0]);
        int month = Integer.parseInt(sDate[1]);
        int day = Integer.parseInt(sDate[2]);

        Calendar cln = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        cln.set(year, month, day);
        String mAge = "0";


        int nYears = today.get(Calendar.YEAR) - cln.get(Calendar.YEAR);
        int nMonths = (today.get(Calendar.MONTH)+1) - cln.get(Calendar.MONTH);
        int nDays = today.get(Calendar.DAY_OF_MONTH) - cln.get(Calendar.DAY_OF_MONTH);
         if(nYears == 0){
             if (nMonths == 0){
               if (nDays>0) {
                   mAge = nDays + " days";
               }
             }else if (nMonths ==  1){
                 if (nDays>=0){
                     mAge ="1 month";
                 }else {
                     mAge = String.valueOf(30 - Math.abs(nDays)) + " days";
                 }
             }else if (nMonths > 1){
                 mAge = nMonths + " months";
             }
         }else if (nYears == 1){
             if (nMonths == 0){
                 mAge = "1 year";
             }else if (nMonths > 0){
                 mAge =  "1 year "  + nMonths + " months";
             }else {
                 mAge = (12 - Math.abs(nMonths)) + " months";
             }
         }else if (nYears > 1 && nYears < 3){
             if (nMonths == 0){
                 mAge =  nYears + " year";
             }else if (nMonths > 0){
                 mAge =  nYears + " years " + nMonths + " months";
             }else {
                 mAge = (nYears - 1) + " years " + (12 - Math.abs(nMonths)) + " months";
             }
         }else if (nYears >= 3){
           if (nMonths >= 0){
               mAge = nYears + "";
           }else {
               if (nYears == 3){
                   mAge = "2 years " + (12 - Math.abs(nMonths)) + " months";
               }else {
                   mAge = (nYears- 1) +" years";
               }
           }
         }


        return mAge;
    }

    public static String getInstanceDate() {
        Calendar calendar = Calendar.getInstance();
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(calendar.get(Calendar.MONTH)+1);
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        if (Integer.parseInt(month) < 10){
            month  = "0" + month;
        }
        String instanceDate = year + "_" + month + "_" + day;
        return instanceDate;
    }

    public static String dateFormat(String date){
        String[] splitedDate = date.split("_");
        String nDate = splitedDate[2] + "-" + splitedDate[1] + "-" + splitedDate[0];
        return nDate;
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
