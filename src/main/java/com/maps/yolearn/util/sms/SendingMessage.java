package com.maps.yolearn.util.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * @author KOTARAJA
 */
public class SendingMessage {

    public String SMSSender(String authkey, String mobiles, String message, String sender, String country, String flashsms, String route) {
        String rsp;
        try {
            // Construct The Post Data
            String data = URLEncoder.encode("authkey", "UTF-8") + "=" + URLEncoder.encode(authkey, "UTF-8");
            data += "&" + URLEncoder.encode("sender", "UTF-8") + "=" + URLEncoder.encode(sender, "UTF-8");
            data += "&" + URLEncoder.encode("country", "UTF-8") + "=" + URLEncoder.encode(country, "UTF-8");
            //   data += "&" + URLEncoder.encode("DCS", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8");
            data += "&" + URLEncoder.encode("flashsms", "UTF-8") + "=" + URLEncoder.encode(flashsms, "UTF-8");
            data += "&" + URLEncoder.encode("mobiles", "UTF-8") + "=" + URLEncoder.encode(mobiles, "UTF-8");
            data += "&" + URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");
            data += "&" + URLEncoder.encode("route", "UTF-8") + "=" + URLEncoder.encode(route, "UTF-8");

            String url1 = "http://api.msg91.com/api/sendhttp.php";
            url1 += "?" + data;
            URL url = new URL(url1);
//            System.out.println("url" + url);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            System.out.println("reader" + reader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb = sb.append(line);
            }
            rsp = sb.toString();
        } catch (IOException e) {
            rsp = e.getMessage();
        }
        return rsp;
    }

    public String SMSSender(String authkey, String type) {
        String rsp;
        try {
            // Construct The Post Data
            String data = URLEncoder.encode("authkey", "UTF-8") + "=" + URLEncoder.encode(authkey, "UTF-8");
            data += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");

            String url1 = "http://control.msg91.com/api/balance.php";
            url1 += "?" + data;
            URL url = new URL(url1);
//            System.out.println("url" + url);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            System.out.println("reader" + reader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb = sb.append(line);
            }
            rsp = sb.toString();
        } catch (IOException e) {
            rsp = e.getMessage();
        }
        return rsp;
    }

}
