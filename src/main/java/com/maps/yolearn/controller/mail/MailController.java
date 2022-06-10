package com.maps.yolearn.controller.mail;

import com.maps.yolearn.bean.liveclass.LiveMetaData;
import com.maps.yolearn.bean.mail.MailBean;
import com.maps.yolearn.bean.user.UsersMetaData;
import com.maps.yolearn.model.mail.MailBox;
import com.maps.yolearn.model.user.Otp;
import com.maps.yolearn.model.user.Registration;
import com.maps.yolearn.service.EntityService;
import com.maps.yolearn.util.date.MyDateFormate;
import com.maps.yolearn.util.mail.E_Mail_Sender_info;
import com.maps.yolearn.util.primarykey.CustomPKGenerator;
import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * @author KOTARAJA
 * @author PREMNATH
 */
@RestController
@RequestMapping(value = {"/mail"})
@CrossOrigin(origins = "*", maxAge = 3600)
public class MailController {

    E_Mail_Sender_info javaMail_Sender_Info = new E_Mail_Sender_info();
    @Autowired
    private EntityService service;
    @Autowired
    private CustomPKGenerator pKGenerator;

    public void deleteOtp(String emailId) {
        Map<String, Object> map = new HashMap<>();
        map.put("emailId", emailId);
        service.delete(Otp.class, map);
    }

    @RequestMapping(value = {"/sendotp"}, method = RequestMethod.POST)
    public @ResponseBody
    JSONObject sendOtp(@RequestBody final UsersMetaData bean) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        JSONObject json = new JSONObject();
        String mail = bean.getPrimaryEmail();
        String msg;

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("primaryEmail", mail);
            if (service.getObject(Registration.class, map).size() > 0) {
                msg = "you are registered with us, click here to Login.";
                json.put("msg", msg);
            } else {
//                if (isAddressValid(testData[0]) == true) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("emailId", mail);
                if (service.getObject(Otp.class, map1).size() > 0) {
                    deleteOtp(mail);
                }

                String characters = "1234567890";
                String otp = RandomStringUtils.random(6, characters);

                Otp o = new Otp();
                String otpid = (String) pKGenerator.generate(Otp.class, "OTP_");
                o.setOtpId(otpid);
                o.setEmailId(mail);
                o.setOtp(otp);
                o.setDateOfCreation(date);

                int save = service.save(o);
                if (save > 0) {

                    String subject = "OTP CONFIRMATION";

                    msg = "Hi,<br><br>"
                            + "Welcome to <b>YO</b>LEARN<br><br>"
                            + "Your verification code is “<font size='4'><b>" + otp + "</b></font>”.<br><br><br>"
                            + "<b style='background-color:yellow'>Do not forward or give this code to anyone.</b><br><br>"
                            + "For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
                            + "You received this message because this email address was used in registration process for <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                            + "<b>Yours Sincerely,</b><br>"
                            + "YOLEARN Team.";
                    Set<String> to1 = new HashSet<>();
                    to1.add(mail);
                    javaMail_Sender_Info.composeAndSend(subject, to1, msg);

                    msg = "An OTP has been sent to your mail.Check the spam folder for OTP ";
                    json.put("msg", msg);
                    json.put("otpid", otpid);
                } else {
                    json.put("msg", "something went wrong");
                }
//                } else {
//                    System.out.println("hiieeei");
//                    json.put("msg", "Coudn't find google account!");
//                }
            }
        } catch (MessagingException e) {
//            e.printStackTrace();
        }

        return json;
    }

    @RequestMapping(value = {"/sendotpwithoutValidation"}, method = RequestMethod.POST)
    public @ResponseBody
    JSONObject sendOtpWithoutValidation(@RequestBody final UsersMetaData bean) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        JSONObject json = new JSONObject();
        String mail = bean.getPrimaryEmail();
        mail = mail.toLowerCase();
        String msg;

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("primaryEmail", mail);

            String testData[] = {bean.getPrimaryEmail()};
//            if (isAddressValid(testData[0]) == true) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("emailId", mail);

            if (service.getObject(Otp.class, map1).size() > 0) {
                deleteOtp(mail);
            }

            String characters = "1234567890";
            String otp = RandomStringUtils.random(6, characters);

            Otp o = new Otp();
            String otpid = (String) pKGenerator.generate(Otp.class, "OTP_");
            o.setOtpId(otpid);
            o.setEmailId(mail);
            o.setOtp(otp);
            o.setDateOfCreation(date);

            service.save(o);

            String subject = "OTP CONFIRMATION";

            msg = "Hi,<br><br>"
                    + "Welcome to <b>YO</b>LEARN<br><br>"
                    + "Your verification code is “<font size='4'><b>" + otp + "</b></font>”.<br><br><br>"
                    + "<b style='background-color:yellow'>Do not forward or give this code to anyone.</b><br><br>"
                    + "For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
                    + "You received this message because this email address was used in registration process for <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                    + "<b>Yours Sincerely,</b><br>"
                    + "YOLEARN Team.";
            Set<String> to1 = new HashSet<>();
            to1.add(mail);
            javaMail_Sender_Info.composeAndSend(subject, to1, msg);

            msg = "An OTP has been sent to your mail. Check the spam folder for OTP ";
            json.put("msg", msg);
            json.put("otpid", otpid);

//            } else {
//                json.put("msg", "Coudn't find google account!");
//            }
        } catch (MessagingException e) {
        }

        return json;
    }

    @RequestMapping(value = "/confirmOtp", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject confirmOtp(@RequestBody final Map<String, String> map) {
        JSONObject json = new JSONObject();

        String emailId = map.get("primaryEmail");
        String otpId = map.get("otpId");
        String otp = map.get("otp");

        Map<String, Object> map0 = new HashMap<>();
        map0.put("emailId", emailId);
        map0.put("otpId", otpId);
        map0.put("otp", otp);

        if (service.getObject(Otp.class, map0).size() > 0) {
            deleteOtp(emailId);
            json.put("msg", "OTP confirmed!");
        } else {
            deleteOtp(emailId);
            json.put("msg", "OTP not confirmed!");
        }
        return json;
    }

    /* http://localhost:8080/yolearn/users/mailToStudentsAboutScheduledClass?time=458214789654 */
    @RequestMapping(method = RequestMethod.POST, value = "/mailToStudentsAboutScheduledClass")
    public @ResponseBody
    ResponseEntity<?> mailToStudentsAboutScheduledClass(
            @RequestParam("time") long time,
            @RequestBody final Map<String, String> mapBean) throws ParseException {
        JSONObject json = new JSONObject();

        String batchId = mapBean.get("batchId");
        String title = mapBean.get("title");
        String subid = mapBean.get("subjectId");
        String mailingDateString = mapBean.get("mailingDate");
        String scheduledDateString = MyDateFormate.getTimeBasedOnTimeZone(time);
        Date currentDate = new Date();
//        Date mailingDate = MyDateFormate.parseDate(mailingDateString);
        long mailingDateLong = Long.parseLong(mailingDateString);
        Date mailingDate = new Date(mailingDateLong);
        Object get = service.getObject(String.format("%s", "SELECT  s.gradeId FROM Batch s WHERE s.batchId = '" + batchId + "'")).get(0);
        System.out.println("get  " + get);
        /*getting set of mails(to whom mail should be sent) from the db*/
        List<Object[]> listStudentObject = service.loadProperties(String.format("%s", "SELECT s.primaryEmail, s.firstName, s.lastName FROM StudentAccount s WHERE s.gradeId = '" + get + "'"));
        if (listStudentObject.size() > 0) {
            Map<String, Object> mapEmailAndStudentName = new HashMap<>();

            for (Object[] object : listStudentObject) {
                String primaryEmail = (String) object[0];
                String studentFName = (String) object[1];
                String studentLName = (String) object[2];
                System.out.println("primaryEmail " + primaryEmail);
                mapEmailAndStudentName.put(primaryEmail, String.format("%s", studentFName + " " + studentLName));
            }

            /*getting subject name from db*/
            Map<String, Object> mapSub = new HashMap();
            mapSub.put("subjectId", subid);

//            List<Object> listSubjectObj = service.getObject(Subject.class, mapSub);
//            Subject subject = (Subject) listSubjectObj.get(0);
//            String sub = subject.getSubjectName();

            /*getting gradeName from batch*/
//            String gradeId = (String) service.getObject(String.format("%s", "SELECT b.gradeId FROM Batch b WHERE b.batchId = '" + batchId + "'")).get(0);
//
//            String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + gradeId + "'")).get(0);

            /*sending mail to students on scheduled time*/
//            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//
//            long delay_millis = mailingDate.getTime() - currentDate.getTime();
//
//            scheduledExecutorService.schedule(new Scheduler(gradeName, sub, title, scheduledDateString, mapEmailAndStudentName), delay_millis, TimeUnit.MILLISECONDS);

//        json.put("mailing date", "mail has been scheduled at " + mailingDate);
            json.put("msg", "mail has been scheduled at " + MyDateFormate.getTimeBasedOnTimeZone(mailingDateLong));

        } else {
            json.put("msg", "no student found with the assigned batch.");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    //    /* http://localhost:8080/yolearn/users/mailToStudentsAboutScheduledClass?time=458214789654 */
//    @RequestMapping(method = RequestMethod.POST, value = "/mailToStudentsAboutScheduledClass")
//    public @ResponseBody
//    ResponseEntity<?> mailToStudentsAboutScheduledClass(
//            @RequestParam("time") long time,
//            @RequestBody final Map<String, String> mapBean) throws ParseException {
//
//        String gradeId = mapBean.get("gradeId");
//        String title = mapBean.get("title");
//        String subid = mapBean.get("subjectId");
//        String mailingDateString = mapBean.get("mailingDate");
//
//        String scheduledDateString = MyDateFormate.getTimeBasedOnTimeZone(time);
//
//        Date currentDate = new Date();
//
//        Date mailingDate = MyDateFormate.parseDate(mailingDateString);
//
//        /*getting set of mails(to whom mail should be sent) from the db*/
//        Map<String, Object> mapGrade = new HashMap<>();
//        mapGrade.put("gradeId", gradeId);
//
//        Set<String> studentMailSet = new HashSet<>();
//        List<Object> listStudentObject = service.getObject(StudentAccount.class, mapGrade);
//        for (Object object : listStudentObject) {
//            StudentAccount sa = (StudentAccount) object;
//            studentMailSet.add(sa.getPrimaryEmail());
//        }
//
//        /*getting subject name from db*/
//        Map<String, Object> mapSub = new HashMap();
//        mapSub.put("subjectId", subid);
//
//        List<Object> listSubjectObj = service.getObject(Subject.class, mapSub);
//        Subject subject = (Subject) listSubjectObj.get(0);
//        String sub = subject.getSubjectName();
//
//        /*getting gradeName*/
//        Grade grade = (Grade) service.getObject(Grade.class, mapGrade).get(0);
//        String gradeName = grade.getGradeName();
//
//        /*sending mail to students on scheduled time*/
//        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//
//        long delay_millis = mailingDate.getTime() - currentDate.getTime();
//
//        scheduledExecutorService.schedule(new Scheduler(gradeName, sub, title, scheduledDateString, studentMailSet), delay_millis, TimeUnit.MILLISECONDS);
//
//        JSONObject json = new JSONObject();
//        json.put("mailing date", "mail has been scheduled at " + mailingDate);
//
//        return new ResponseEntity<>(json, HttpStatus.OK);
//    }
//    
    @RequestMapping(method = RequestMethod.GET, value = "/getMaileBox")
    public @ResponseBody
    List<Object> getMaileBox() {
        JSONObject json;
        List<Object> list = new ArrayList<>();
        try {
            List<Object> listObj = service.getObject(MailBox.class);
            if (listObj.size() > 0) {
                for (Object object : listObj) {
                    MailBox mailBox1 = (MailBox) object;
                    json = new JSONObject();
                    json.put("mailId", mailBox1.getMailId());
                    json.put("name", mailBox1.getName());
                    json.put("senderMail", mailBox1.getSenderMail());
                    json.put("subject", mailBox1.getSubject());
                    json.put("status", mailBox1.getStatus());
                    json.put("body", mailBox1.getBody());
                    json.put("date", MyDateFormate.dateToString1(mailBox1.getDateOfCreation()));
                    list.add(json);
                }
            } else {
                json = new JSONObject();
                json.put("msg", "EMPTY MAIL BOX");
                list.add(json);
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Error " + e.getMessage());
            list.add(json);
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateMailBox")
    public @ResponseBody
    JSONObject updateMailBox(@RequestBody LiveMetaData bean) {
        JSONObject json;
        String id = bean.getMailId();

        Map<String, Object> map = new HashMap<>();
        map.put("mailId", id);
        List<Object> listObj = service.getObject(MailBox.class, map);

        MailBox mailBox = (MailBox) listObj.get(0);
        boolean status = mailBox.getStatus();
        if (status == false) {
            mailBox.setStatus(bean.getStatus());
        }

        int x = service.update(mailBox);
        if (x > 0) {
            json = new JSONObject();
            json.put("msg", "status updated");
        } else {
            json = new JSONObject();
            json.put("msg", "try again!");
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateMailSubscription")
    public @ResponseBody
    JSONObject updateMailSubscription(@RequestBody MailBean bean) {
        JSONObject jsono;
        try {
            String accountId = bean.getAccountId();
            boolean status = bean.getStatus();

            Map<String, Object> map1 = new HashMap<>();
            map1.put("accountId", accountId);
            List<Object> listOfReg = service.getObject(Registration.class, map1);
            if (listOfReg.size() > 0) {
                Registration registration = (Registration) listOfReg.get(0);
                registration.setMailSubscriptionStatus(status);
                int x1 = service.update(registration);
                if (x1 > 0) {
                    jsono = new JSONObject();
                    jsono.put("msg", "updated");
                } else {
                    jsono = new JSONObject();
                    jsono.put("msg", "not updated");
                }
            } else {
                jsono = new JSONObject();
                jsono.put("msg", "Empty list in Registration");
            }
        } catch (Exception e) {
            jsono = new JSONObject();
            jsono.put("msg", e.getMessage());
        }
        return jsono;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/mailBox")
    public @ResponseBody
    JSONObject mailBox(@RequestBody MailBean bean) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        JSONObject json = new JSONObject();
        String supportMail = "support@yolearn.com";
        try {
            String mail = (String) pKGenerator.generate(MailBox.class, "MAIL_");
            String testData[] = {bean.getSenderMail()};
//            if (isAddressValid(testData[0]) == true) {
            MailBox mailBox = new MailBox();
            mailBox.setMailId(mail);
            mailBox.setName(bean.getName());
            mailBox.setSenderMail(bean.getSenderMail());
            mailBox.setSubject(bean.getSubject());
            mailBox.setStatus(false);
            mailBox.setBody(bean.getBody());
            mailBox.setDateOfCreation(date);

            if (service.save(mailBox) > 0) {
                json.put("mailId", mail);
                json.put("msg", "saved");
                json.put("name", bean.getName());
                json.put("senderMail", bean.getSenderMail());
                json.put("subject", bean.getSubject());
                json.put("status", false);
                json.put("body", bean.getBody());
                String subject1 = "Enquiry";
                String emailMsg = "Dear Sir/Madam,<br><br>"
                        + bean.getBody() + "<br><br>"
                        + "Yours Sincerely,<br><br>"
                        + "<b>" + bean.getName() + "<br>"
                        + bean.getSenderMail() + "</b>";
                Set<String> to1 = new HashSet<>();
                to1.add(supportMail);
                javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg);

            } else {
                json.put("msg", "not saved !");
            }

//            } else {
//                json.put("msg", "Coudn't find google account!");
//            }
        } catch (MessagingException e) {
        }

        return json;
    }
}
