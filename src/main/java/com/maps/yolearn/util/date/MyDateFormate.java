package com.maps.yolearn.util.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author PREMNATH
 */
public class MyDateFormate {

    public static Date stringToDate(String dateStr) {
        Date d = null;
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = format.parse(dateStr);
        } catch (ParseException e) {
        }
        return d;
    }

//    public static Date stringToDateISO(String dateStr) {
//        Date d = null;
//        try {
//            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
//            d = format.parse(dateStr);
//        } catch (ParseException e) {
//        }
//        return d;
//    }

    //    public static Date stringToDate(String dateStr) {
//
//        Date d = null;
//
//        try {
//
////            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
//            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//            d = format.parse(dateStr);
//            System.out.println("date : " + d);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return d;
//    }
//    
    public static Date stringToDateOnlyDate(String dateStr) {

        Date d = null;

        try {

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            d = format.parse(dateStr);
//            System.out.println("date : " + d);

        } catch (ParseException e) {
        }

        return d;
    }

    public static String dateToString(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date d = date;
//        System.out.println(d.toString()); // Wed Dec 04 00:00:00 CST 2013

        String stringDate = dateFormat.format(d);

        return stringDate;
    }

    public static String dateToString1(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = date;

        String stringDate = dateFormat.format(d);

        return stringDate;
    }

    public static String dateToString2(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = date;
//        System.out.println(d.toString()); // Wed Dec 04 00:00:00 CST 2013

        String stringDate = dateFormat.format(d);

        return stringDate;
    }

    public static String getTimeStringFromDate(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date d = date;
//        System.out.println(d.toString()); // Wed Dec 04 00:00:00 CST 2013

        String stringTime = dateFormat.format(d);

        return stringTime;
    }

    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseDate1(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    //    public static Date ISTtoGMT(String input) {
//
////        String input = "2018-11-19 13:30:00";
//        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        Date t = null;
//        try {
//            t = ft.parse(input);
//            System.out.println("IST: " + t);
//
//            /*IST TO GMT*/
//            long timePeriod = t.getTime() - 19800000;
//
//            t = new Date(timePeriod);
//
//        } catch (ParseException e) {
//
//        }
//
//        return t;
//    }
    public static String getTimeBasedOnTimeZone(long time) {

        Date fromDate = new Date();
        fromDate.setTime(time);
//        System.out.println("fromDate: " + fromDate);

        /*Converting to Indian timezone*/

        SimpleDateFormat sdf = new SimpleDateFormat("E MMM d HH:mm:ss z yyyy");
        TimeZone tz = TimeZone.getTimeZone("Asia/Kolkata");

        sdf.setTimeZone(tz);
        String d = sdf.format(fromDate); //will return a string rep of a date with the included format

//        System.out.println("d: " + d);

        return d;
    }

    public static boolean checkBetween(Date dateToCheck, Date startDate, Date endDate) {
        return !(dateToCheck.after(endDate) || dateToCheck.before(startDate));
    }

    public static Date stringToDate_1(String dateStr) throws ParseException {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);

        LocalDateTime date = LocalDateTime.parse(makeMilliSecToZero(dateStr), inputFormatter);

        String formattedDate = outputFormatter.format(date);

        System.out.println("formattedDate " + formattedDate);
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(formattedDate);
        return d;
    }

    public static String makeMilliSecToZero(String sDate) {
        String[] tokens = sDate.split("\\.(?=[^\\.]+$)");
        return String.format("%s", tokens[0] + ".000Z");
    }

}
