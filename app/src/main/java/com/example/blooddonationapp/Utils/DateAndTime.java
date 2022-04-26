package com.example.blooddonationapp.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTime {

    public static String getDate(long timestamp){
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a, dd/MM/yyyy");
        String dateString = formatter.format(new Date(timestamp));
        return dateString;
    }
    public static String getTime(long timestamp){
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        String dateString = formatter.format(new Date(timestamp));
        return dateString;
    }

}
