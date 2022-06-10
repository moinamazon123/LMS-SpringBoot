package com.maps.yolearn.util.task;

import com.maps.yolearn.util.mail.E_Mail_Sender_info;

import javax.mail.MessagingException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author PREMNATH
 */
public class Scheduler implements Runnable {

    String gradeName;
    String sub;
    String title;
    //    Date scheduledDate;
    String scheduledDate;
    //    Set<String> toMail;
    Map<String, Object> map;

    E_Mail_Sender_info javaMail_Sender_Info = new E_Mail_Sender_info();

    //    public Scheduler(String gradeName, String sub, String title, String scheduledDate, Set<String> toMail, String studentName) {
    public Scheduler(String gradeName, String sub, String title, String scheduledDate, Map<String, Object> map) {

        this.gradeName = gradeName;
        this.sub = sub;
        this.title = title;
        this.scheduledDate = scheduledDate;
//        this.toMail = toMail;
        this.map = map;

    }

    //    @Override
//    public void run() {
//
//        String subject = "YOLEARN - Upcoming class";
//        String emailBody = "Dear Parent,<br><br>"
//                + "Live class has been scheduled. Please make sure your child to attend the class.<br><br>"
//                + "<b>Class Details:</b>"
//                + "<table>"
//                + "<tr><td>Grade Name</td><td> : " + gradeName + "</td></tr>"
//                + "<tr><td>Subject Name</td><td> : " + sub + "</td></tr>"
//                + "<tr><td>Title</td><td> : " + title + "</td></tr>"
//                + "<tr><td>Schedule Date</td><td> : <b>" + scheduledDate + "</b></td></tr>"
//                + "</table></b><br>"
//                + "<small>You received this message because this email address is registered in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
//                + "<b>Yours Sincerely,</b><br>"
//                + "YOLEARN Team.";
//
//        try {
//            javaMail_Sender_Info.composeAndSend(subject, toMail, emailBody);
//        } catch (MessagingException ex) {
//            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
    @Override
    public void run() {

        Set<String> keys = map.keySet();
        for (String emailAsKey : keys) {
            String studentName = (String) map.get(emailAsKey);

            String subject = "YOLEARN - Upcoming class";
            String emailBody = "Dear Parent,<br><br>"
                    + "Live class has been scheduled. Please make sure your child to attend the class.<br><br>"
                    + "<b>Class Details:</b>"
                    + "<table>"
                    + "<tr><td>Student Name</td><td> : " + studentName + "</td></tr>"
                    + "<tr><td>Grade Name</td><td> : " + gradeName + "</td></tr>"
                    + "<tr><td>Subject Name</td><td> : " + sub + "</td></tr>"
                    + "<tr><td>Title</td><td> : " + title + "</td></tr>"
                    + "<tr><td>Schedule Date</td><td> : <b>" + scheduledDate + "</b></td></tr>"
                    + "</table></b><br>"
                    + "<small>You received this message because this email address is registered in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                    + "<b>Yours Sincerely,</b><br>"
                    + "YOLEARN Team.";

            try {
                javaMail_Sender_Info.composeAndSend(subject, emailAsKey, emailBody);
            } catch (MessagingException ex) {
                Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}
