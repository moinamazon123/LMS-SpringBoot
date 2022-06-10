package com.maps.yolearn.controller.user;

import com.maps.yolearn.bean.liveclass.AttendanceBean;
import com.maps.yolearn.bean.mail.MailBean;
import com.maps.yolearn.bean.user.UsersMetaData;
import com.maps.yolearn.model.grade.Grade;
import com.maps.yolearn.model.grade.Syllabus;
import com.maps.yolearn.model.liveclass.ClassScheduler;
import com.maps.yolearn.model.payment.PaymentCheckout;
import com.maps.yolearn.model.payment.SubscribeType;
import com.maps.yolearn.model.testandassignment.TestAndAssignments;
import com.maps.yolearn.model.user.*;
import com.maps.yolearn.service.EntityService;
import com.maps.yolearn.util.date.MyDateFormate;
import com.maps.yolearn.util.filter.FilterUtility;
import com.maps.yolearn.util.ftp.FTPServer;
import com.maps.yolearn.util.lang.FirstCharLowerToUpper;
import com.maps.yolearn.util.mail.E_Mail_Sender_Account;
import com.maps.yolearn.util.mail.E_Mail_Sender_info;
import com.maps.yolearn.util.primarykey.CustomPKGenerator;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

//import com.maps.yolearn.model.payment.MultipleSubscriptionForstudent;

/**
 * @author KOTARAJA
 * @author PREMNATH
 * @author VINAYKUMAR
 */
@RestController
@RequestMapping(value = {"/users"})
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsersController {

    E_Mail_Sender_info javaMail_Sender_Info = new E_Mail_Sender_info();
    @Autowired
    private EntityService service;
    @Autowired
    private CustomPKGenerator pKGenerator;

    public JSONObject getRegistrationJSON(Registration registration) {
        JSONObject json = new JSONObject();
        json.put("accountId", registration.getAccountId());
        json.put("firstName", registration.getFirstName());
        json.put("lastName", registration.getLastName());
        json.put("fullName", String.format("%s", registration.getFirstName() + " " + registration.getLastName()));
        json.put("primaryEmail", registration.getPrimaryEmail());
        json.put("password", registration.getPassword());
        json.put("countryCode", registration.getCountryCode());
        json.put("mobileNum", registration.getMobileNum());
        json.put("userRole", registration.getUserRole());
        json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
        json.put("address", registration.getAddress());
        json.put("city", registration.getCity());
        json.put("mailSubscriptionStatus", registration.getMailSubscriptionStatus());
        return json;
    }

    public void deleteOtp(String emailId) {

        Map<String, Object> map = new HashMap<>();
        map.put("emailId", emailId);

        service.delete(Otp.class, map);
    }

    /**
     * *TEACHER REGISTRATION
     *
     * @param registration
     * @return JSONObject
     */
    public JSONObject teacherRegistration(Registration registration) {
        JSONObject json = new JSONObject();

        try {
            String testData[] = {registration.getPrimaryEmail()};
//            if (isAddressValid(testData[0]) == true) {
            int countEmail = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Registration r WHERE r.primaryEmail = '" + registration.getPrimaryEmail() + "'"));
            if (countEmail == 0) {
                int countMob = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Registration r WHERE r.mobileNum = " + registration.getMobileNum() + ""));
                if (countMob == 0) {
                    TeacherAccount teacherAccount = new TeacherAccount();
                    String teacherId = (String) pKGenerator.generate(TeacherAccount.class, "TEACHER");
                    teacherAccount.setTeacherAccountId(teacherId);
                    teacherAccount.setFirstName(registration.getFirstName());
                    teacherAccount.setLastName(registration.getLastName());
                    teacherAccount.setPrimaryEmail(registration.getPrimaryEmail());
                    teacherAccount.setPassword(registration.getPassword());
                    teacherAccount.setCountryCode(registration.getCountryCode());
                    teacherAccount.setMobileNum(registration.getMobileNum());
                    teacherAccount.setDateOfCreation(registration.getDateOfCreation());
                    teacherAccount.setAddress(registration.getAddress());
                    teacherAccount.setCity(registration.getCity());
                    teacherAccount.setStatus(false);
                    registration.setTeacherAccount(teacherAccount);
                    teacherAccount.setRegistration(registration);

                    int x = service.save(registration);
                    if (x > 0) {
                        String admin = "admin@yolearn.com";
                        String info = "info@yolearn.com";
                        String subject = "YOLEARN - Registration Confirmation";

                        /*sending email to the user*/
                        String emailMsg = "Hi <b>" + registration.getFirstName() + ",</b><br><br>"
                                + "Admin has registered you successfully.<br><br>"
                                + "<b>Login details:</b><br>"
                                + "<table>"
                                + "<tr><td>Email</td><td> : " + registration.getPrimaryEmail() + "</td></tr>"
                                + "<tr><td>Password</td><td> : " + registration.getPassword() + "</td></tr>"
                                + "</table></b><br>"
                                + "<small>For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
                                + "You received this message because this email address was used to register you in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                                + "<b>Yours Sincerely,</b><br>"
                                + "YOLEARN Team.<br><br>"
                                + "Thanks for choosing Yolearn";
                        Set<String> to1 = new HashSet<>();
                        to1.add(registration.getPrimaryEmail());
                        javaMail_Sender_Info.composeAndSend(subject, to1, emailMsg);

                        /*sending email to the admin*/
                        String subject1 = "Registration Confirmation(Teacher)";
                        String emailMsg1 = "Hi Admin,<br><br>"
                                + "New teacher has been registered successfully.<br><br>"
                                + "Name: <b>" + registration.getFirstName() + " " + registration.getLastName() + "<br><br>"
                                + "Login details:</b>"
                                + "<table>"
                                + "<tr><td>Email</td><td> : " + registration.getPrimaryEmail() + "</td></tr>"
                                + "<tr><td>Password</td><td> : " + registration.getPassword() + "</td></tr>"
                                + "</table><br><br>"
                                + "<b>Yours Sincerely,</b><br>"
                                + "YOLEARN Team.";

                        Set<String> to2 = new HashSet<>();
                        to2.add(admin);
                        to2.add(info);
                        javaMail_Sender_Info.composeAndSend(subject1, to2, emailMsg1);

                        json.put("accountId", registration.getAccountId());
                        json.put("tAccountId", teacherAccount.getTeacherAccountId());
                        json.put("firstName", registration.getFirstName());
                        json.put("lastName", registration.getLastName());
                        json.put("primaryEmail", registration.getPrimaryEmail());
                        json.put("password", registration.getPassword());
                        json.put("countryCode", registration.getCountryCode());
                        json.put("mobileNum", registration.getMobileNum());
                        json.put("userRole", registration.getUserRole());
                        json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
                        json.put("address", registration.getAddress());
                        json.put("city", registration.getCity());
                        json.put("status", teacherAccount.getStatus());
                        json.put("msg", "Congratulations! Registration Successful.");

                    } else {
                        json.put("msg", "Something went wrong. Please check your inputs or try again.");
                    }
                } else {
                    json.put("msg", "This mobile number is already registered. Try another.");
                }
            } else {
                json.put("msg", "you are registered with us, click here to Login.");
            }
//            } else {
//                json.put("msg", "Coudn't find google account!");
//            }
        } catch (Exception e) {
            json.put("msg", "Something went wrong. Please check your inputs or try again.");
        }
        return json;
    }

    /**
     * *ADMIN REGISTRATION
     *
     * @param registration
     * @return JSONObject
     */
    public JSONObject adminRegistration(Registration registration) {
        JSONObject json = new JSONObject();

        try {
            String testData[] = {registration.getPrimaryEmail()};
//            if (isAddressValid(testData[0]) == true) {
            int countEmail = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Registration r WHERE r.primaryEmail = '" + registration.getPrimaryEmail() + "'"));
            if (countEmail == 0) {
                int countMob = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Registration r WHERE r.mobileNum = " + registration.getMobileNum() + ""));
                if (countMob == 0) {
                    AdminAccount adminAccount = new AdminAccount();
                    String adminId = (String) pKGenerator.generate(AdminAccount.class, "ADMIN");
                    adminAccount.setAdminId(adminId);
                    adminAccount.setFirstName(registration.getFirstName());
                    adminAccount.setLastName(registration.getLastName());
                    adminAccount.setPrimaryEmail(registration.getPrimaryEmail());
                    adminAccount.setPassword(registration.getPassword());
                    adminAccount.setCountryCode(registration.getCountryCode());
                    adminAccount.setMobileNum(registration.getMobileNum());
                    adminAccount.setDateOfCreation(registration.getDateOfCreation());
                    adminAccount.setAddress(registration.getAddress());
                    adminAccount.setCity(registration.getCity());
                    adminAccount.setStatus(false);
                    adminAccount.setTeacherAccess(false);
                    adminAccount.setParentAccess(false);
                    adminAccount.setStudentAccess(false);
                    adminAccount.setDemoMemberAccess(false);
                    adminAccount.setClassRoomAccess(false);
                    adminAccount.setRecordedAccess(false);
                    adminAccount.setUpcomingAccess(false);
                    adminAccount.setAssignmentAccess(false);
                    adminAccount.setTestAccess(false);
                    adminAccount.setScheduleAccess(false);
                    adminAccount.setProdutAccess(false);
                    adminAccount.setSubscriptionAccess(false);
                    adminAccount.setBatchAccess(false);
                    adminAccount.setTalentAccess(false);
                    adminAccount.setAdminAccess(false);
                    registration.setAdminAccount(adminAccount);
                    adminAccount.setRegistration(registration);

                    int x = service.save(registration);
                    if (x > 0) {
                        String admin = "admin@yolearn.com";
                        String info = "info@yolearn.com";
                        String subject = "YOLEARN - Registration Confirmation";

                        /*sending email to the user*/
                        String emailMsg = "Hi <b>" + registration.getFirstName() + ",</b><br><br>"
                                + "Super Admin has registered you successfully.<br><br>"
                                + "<b>Login details:</b><br>"
                                + "<table>"
                                + "<tr><td>Email</td><td> : " + registration.getPrimaryEmail() + "</td></tr>"
                                + "<tr><td>Password</td><td> : " + registration.getPassword() + "</td></tr>"
                                + "</table></b><br>"
                                + "<small>For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
                                + "You received this message because this email address was used to register you in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                                + "<b>Yours Sincerely,</b><br>"
                                + "YOLEARN Team.<br><br>"
                                + "Thanks for choosing Yolearn";
                        Set<String> to1 = new HashSet<>();
                        to1.add(registration.getPrimaryEmail());
                        javaMail_Sender_Info.composeAndSend(subject, to1, emailMsg);

                        /*sending email to the admin*/
                        String subject1 = "Registration Confirmation(ADMIN)";
                        String emailMsg1 = "Hi Admin,<br><br>"
                                + "New Admin has been registered successfully.<br><br>"
                                + "Name: <b>" + registration.getFirstName() + " " + registration.getLastName() + "<br><br>"
                                + "Login details:</b>"
                                + "<table>"
                                + "<tr><td>Email</td><td> : " + registration.getPrimaryEmail() + "</td></tr>"
                                + "<tr><td>Password</td><td> : " + registration.getPassword() + "</td></tr>"
                                + "</table><br><br>"
                                + "<b>Yours Sincerely,</b><br>"
                                + "YOLEARN Team.";

                        Set<String> to2 = new HashSet<>();
                        to2.add(admin);
                        to2.add(info);
                        javaMail_Sender_Info.composeAndSend(subject1, to2, emailMsg1);

                        json.put("accountId", registration.getAccountId());
                        json.put("tAccountId", adminAccount.getAdminId());
                        json.put("firstName", registration.getFirstName());
                        json.put("lastName", registration.getLastName());
                        json.put("primaryEmail", registration.getPrimaryEmail());
                        json.put("password", registration.getPassword());
                        json.put("countryCode", registration.getCountryCode());
                        json.put("mobileNum", registration.getMobileNum());
                        json.put("userRole", registration.getUserRole());
                        json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
                        json.put("address", registration.getAddress());
                        json.put("city", registration.getCity());
                        json.put("status", adminAccount.getStatus());
                        json.put("msg", "Congratulations! Registration Successful.");

                    } else {
                        json.put("msg", "Something went wrong. Please check your inputs or try again.");
                    }
                } else {
                    json.put("msg", "This mobile number is already registered. Try another.");
                }
            } else {
                json.put("msg", "you are registered with us, click here to Login.");
            }
//            } else {
//                json.put("msg", "Coudn't find google account!");
//            }
        } catch (Exception e) {
            json.put("msg", "Something went wrong. Please check your inputs or try again.");
        }
        return json;
    }

    public int deleteGuest(String mail) {
        int count = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Guest g WHERE g.mail = '" + mail + "'"));

        int x;
        if (count > 0) {
            x = service.delete(String.format("%s", "DELETE FROM Guest g WHERE g.mail = '" + mail + "'"));
        } else {
            x = 0;
        }
        return x;
    }

    /**
     * *PARENT REGISTRATION
     *
     * @param registration
     * @return JSONObject
     */
    public JSONObject parentRegistration(Registration registration) {
        JSONObject json = new JSONObject();
        try {
            String testData[] = {registration.getPrimaryEmail()};

//            if (isAddressValid(testData[0]) == true) {
            int countEmail = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Registration r WHERE r.primaryEmail = '" + registration.getPrimaryEmail() + "'"));
            if (countEmail == 0) {
                int countMob = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Registration r WHERE r.mobileNum = " + registration.getMobileNum() + ""));
                if (countMob == 0) {
                    ParentAccount parentAccount = new ParentAccount();
                    String parentId = (String) pKGenerator.generate(ParentAccount.class, "PARENT");
                    parentAccount.setParentAccountId(parentId);
                    parentAccount.setFirstName(registration.getFirstName());
                    parentAccount.setLastName(registration.getLastName());
                    parentAccount.setPrimaryEmail(registration.getPrimaryEmail());
                    parentAccount.setPassword(registration.getPassword());
                    parentAccount.setCountryCode(registration.getCountryCode());
                    parentAccount.setMobileNum(registration.getMobileNum());
                    parentAccount.setDateOfCreation(registration.getDateOfCreation());
                    parentAccount.setAddress(registration.getAddress());
                    parentAccount.setCity(registration.getCity());
                    registration.setParentAccount(parentAccount);
                    parentAccount.setRegistration(registration);

                    int x = service.save(registration);
                    if (x > 0) {

                        String admin = "admin@yolearn.com";
                        String info = "info@yolearn.com";
                        String subject = "YOLEARN - Registration Confirmation";

                        /*sending email to the user*/
//                        String emailMsg = "Hi <b>" + registration.getFirstName() + ",</b><br><br>"
//                                //                                Your classes starts on 20th Jan 2020 and ends on 29th Feb 2020.
//                                + "Your registration has been done. Thanks for choosing yolearn.<br>"
//                                //                                + "Your classes starts on 20th Jan 2020 and ends on 29th Feb 2020.<br>"
//                                //                                + "<b>Class Timings:.</b><br>"
//                                //                                + "Respective class timings Weekly 6 days ( Monday to Saturday).<br><br>"
//                                + "<b>Login details to attend the classes:</b><br>"
//                                + "<table>"
//                                + "<tr><td>Email</td><td> : " + registration.getPrimaryEmail() + "</td></tr>"
//                                + "<tr><td>Password</td><td> : " + registration.getPassword() + "</td></tr>"
//                                + "</table><br><br>"
//                                + "<small>For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
//                                + "You received this message because this email address was used to register you in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
//                                + "Happy Learning!!!!!!!!!! <br><br>"
//                                + "<b>Yours Sincerely,</b><br>"
//                                + "YOLEARN Team.<br><br>"
//                                + "Thanks for choosing Yolearn";
                        String emailMsg = "Dear Parent,</b><br><br>"
                                //                                Your classes starts on 20th Jan 2020 and ends on 29th Feb 2020.
                                + "Thanks for choosing yolearn. You registered for the course successfully.<br>"
                                //                                + "Your classes starts on 20th Jan 2020 and ends on 29th Feb 2020.<br>"
                                //                                + "<b>Class Timings:.</b><br>"
                                //                                + "Respective class timings Weekly 6 days ( Monday to Saturday).<br><br>"
                                + "Parent login can be used by the parent to check child's tests, attendance reports, subscription details etc.<br>"
                                + "Whereas student login can be used by your child to attend the classes, assignments, tests etc...<br>"
                                + "Under the same parent login any number of students can be subscribed.<br>"
                                + "These classes will be conducted weekly 4days from Monday to Thursday. To check this month's schedule <br>"
                                + "plz login and check in the upcoming classes<br><br>"
                                + "Kindly go through the attachments to know<br>"
                                + "* yolearn - How it works and <br>"
                                + "*How to attend the class.<br><br>"
                                + "For any queries plz contact(0091) 7287885888.<br><br>"
                                + "We Wish your child a happy and safe learning.<br><br>"
                                + "<b>Best Regards,<b><br>"
                                + "YOLEARN Team.<br><br>";

                        Set<String> to1 = new HashSet<>();
                        to1.add(registration.getPrimaryEmail());

                        /**
                         * With Attachment started
                         */
                        String fileName = "attachments/yolearn_-_how_it_works1.pdf";
                        String fileName2 = "attachments/guide_to_attend_the_class.mp4";

                        Resource resource = new ClassPathResource(fileName);
                        InputStream input = resource.getInputStream();

                        Resource resource2 = new ClassPathResource(fileName2);
                        InputStream input2 = resource2.getInputStream();
                        E_Mail_Sender_Account e_Mail_Sender_Account = new E_Mail_Sender_Account();
                        File file = resource.getFile();
                        File file2 = resource2.getFile();
                        e_Mail_Sender_Account.composeAndSends(subject, to1, emailMsg, input, input2, file.getName(), file2.getName());

                        /**
                         * With attachment ended
                         */
//                        javaMail_Sender_Info.composeAndSend(subject, to1, emailMsg);

                        /*sending email to the admin*/
                        String subject1 = "Registration Confirmation(Parent)";
                        String emailMsg1 = "Hi Admin,<br><br>"
                                + "New parent has been registered successfully.<br><br>"
                                + "Name: <b>" + registration.getFirstName() + " " + registration.getLastName() + "<br><br>"
                                + "Login details:</b>"
                                + "<table>"
                                + "<tr><td>Email</td><td> : " + registration.getPrimaryEmail() + "</td></tr>"
                                + "<tr><td>Password</td><td> : " + registration.getPassword() + "</td></tr>"
                                + "</table><br><br>"
                                + "<b>Yours Sincerely,</b><br>"
                                + "YOLEARN Team.";

                        Set<String> to2 = new HashSet<>();
                        to2.add(admin);
                        to2.add(info);
                        javaMail_Sender_Info.composeAndSend(subject1, to2, emailMsg1);

                        json.put("accountId", registration.getAccountId());
                        json.put("pAccountId", parentAccount.getParentAccountId());
                        json.put("firstName", registration.getFirstName());
                        json.put("lastName", registration.getLastName());
                        json.put("primaryEmail", registration.getPrimaryEmail());
                        json.put("password", registration.getPassword());
                        json.put("countryCode", registration.getCountryCode());
                        json.put("mobileNum", registration.getMobileNum());
                        json.put("userRole", registration.getUserRole());
                        json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
                        json.put("address", registration.getAddress());
                        json.put("city", registration.getCity());
                        json.put("msg", "Congratulations! Registration Successful.");

                        deleteGuest(registration.getPrimaryEmail());

                    } else {
                        json.put("msg", "Something went wrong. Please check your inputs or try again.");
                    }
                } else {
                    json.put("msg", "This mobile number is already registered. Try another.");
                }
            } else {
                json.put("msg", "you are registered with us, click here to Login.");
            }
//            } else {
//                json.put("msg", "Coudn't find google account!");
//            }
        } catch (Exception e) {
            json.put("msg", "Something went wrong. Please check your inputs or try again.");
        }
        return json;
    }

    /**
     * *STUDENT REGISTRATION BY PARENT
     *
     * @param registration
     * @param mainAccountId
     * @param schoolName
     * @return JSONObject
     * @throws javax.mail.MessagingException
     */
    public JSONObject studentRegistrationByParent(Registration registration, String mainAccountId, String schoolName) throws MessagingException {
        JSONObject json = new JSONObject();
        try {
            String testData[] = {registration.getPrimaryEmail()};
//            if (isAddressValid(testData[0]) == true) {
            StudentAccount student = new StudentAccount();
            String studentId = (String) pKGenerator.generate(StudentAccount.class, "STUDENT");
            student.setStudentAccountId(studentId);
            student.setFirstName(registration.getFirstName());
            student.setLastName(registration.getLastName());
            student.setPrimaryEmail(registration.getPrimaryEmail());
            student.setPassword(registration.getPassword());
            student.setStatus(false);
            student.setSchoolName(schoolName);
            student.setDateOfCreation(registration.getDateOfCreation());
            student.setParentAccountId(mainAccountId);
            student.setAddress(registration.getAddress());
            student.setCity(registration.getCity());

            int x = service.save(student);
            if (x > 0) {

                json.put("accountId", student.getParentAccountId());
                json.put("sAccountId", student.getStudentAccountId());
                json.put("firstName", registration.getFirstName());
                json.put("lastName", registration.getLastName());
                json.put("primaryEmail", registration.getPrimaryEmail());
                json.put("password", registration.getPassword());
                json.put("status", student.getStatus());
                json.put("userRole", registration.getUserRole());
                json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
                json.put("address", registration.getAddress());
                json.put("city", registration.getCity());
                json.put("schoolName", student.getSchoolName());
                json.put("msg", "Congratulations! Registration Successful.");

            } else {
                json.put("msg", "Something went wrong. Please check your inputs or try again.");
            }
//            } else {
//                json.put("msg", "Coudn't find google account!");
//            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("msg", "Something went wrong. Please check your inputs or try again.");
        }
        return json;
    }

    public JSONObject guestSignUP(Registration registration) {
        JSONObject json = new JSONObject();
        try {
            int emailCount = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Registration r WHERE r.primaryEmail = '" + registration.getPrimaryEmail() + "'"));
            if (emailCount == 0) {
                int mobCount = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Registration r WHERE r.mobileNum = " + registration.getMobileNum() + ""));
                if (mobCount == 0) {
                    if (service.save(registration) > 0) {
                        String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + registration.getGradeId() + "'")).get(0);
                        json.put("gradeName", gradeName);
                        json.put("accountId", registration.getAccountId());
                        json.put("firstName", registration.getFirstName());
                        json.put("lastName", registration.getLastName());
                        json.put("primaryEmail", registration.getPrimaryEmail());
                        json.put("password", registration.getPassword());
                        json.put("userRole", registration.getUserRole());
                        json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
                        json.put("msg", "Congratulations! Registration Successful.");

                        String subject = "YOLEARN - Registration Confirmation For A Live Demo Class";

                        /*sending email to the user*/
                        String emailMsg = "Dear Parent,<br><br>"
                                + "Greetings!!!!<br><br>"
                                + "Welcome to Yolearn.<br><br>"
                                + "Thanks for registering  with us for a live demo class.<br><br>"
                                + "<b>Login details:</b><br>"
                                + "<table>"
                                + "<tr><td>Email</td><td> : " + registration.getPrimaryEmail() + "</td></tr>"
                                + "<tr><td>Password</td><td> : " + registration.getPassword() + "</td></tr>"
                                + "</table><br><br> "
                                + "<u><b>Guidelines to book a live demo class.</b></u><br><br>"
                                + "<b>Step 1</b><br><br>"
                                + "Login with your registered credentials.<br><br>"
                                + "<b>Step 2</b><br><br>"
                                + "Select the  \"Demo Upcoming\" from the left side menu.<br><br>"
                                + "<b>Step 3</b><br><br>"
                                + "Choose the required grade to book a live demo (from the top filter)<br><br>"
                                + "<b>Step 4</b><br><br>"
                                + "click on  \"Book a slot\" on anyone of the upcoming demo classes.<br><br><br>"
                                + "<u><b>Guidelines to attend a booked demo live class</b></u><br><br>"
                                + "<b>Step 1</b><br><br>"
                                + "On the scheduled date and time, login with your registered credentials.<br><br>"
                                + "<b>Step 2</b><br><br>"
                                + "Select the \"Demo Upcoming\" from the left side menu.<br><br>"
                                + "<b>Step 3</b><br><br>"
                                + "Click on \"Attend Class \" (after count down timer resets to 00:00:00)<br><br>"
                                + "<u><b>Dos and Don`ts</b></u><br><br>"
                                + "1)     Use anyone of the following latest browsers -  Google Chrome/ Firefox / Edge/ Safari.<br><br>"
                                + "2)     After logging into the classroom, if you have any problem with  audio - Video, then REFRESH by clicking on the left top corner Additional options sign (4 horizontal lines sign).<br><br>"
                                + "3)     If still problem persists, then logout and login again.<br><br><br>"
                                + "We wish you a happy and effective learning.<br><br>"
                                + "Thank you,<br><br>"
                                + "Best Regards,<br>"
                                + "YOLEARN Team.<br><br>"
                                + "Thanks for choosing Yolearn";
                        Set<String> to1 = new HashSet<>();
                        to1.add(registration.getPrimaryEmail());
                        javaMail_Sender_Info.composeAndSend(subject, to1, emailMsg);
                    } else {
                        json.put("msg", "Something went wrong. Please check your inputs or try again.");
                    }
                } else {
                    json.put("msg", "This mobile number is already registered. Try another.");
                }
            } else {
                json.put("msg", "you are registered with us, click here to Login.");
            }

        } catch (Exception e) {
            json.put("msg", "Something went wrong. Please check your inputs or try again.");
        }

        return json;
    }

    @RequestMapping(value = "/user_signup", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject userRegistration(@RequestBody final UsersMetaData bean) {
        JSONObject json = new JSONObject();

        Timestamp date = new Timestamp(System.currentTimeMillis());
        String emailId = bean.getPrimaryEmail().toLowerCase();
        String password = bean.getPassword();
        String firstName = FirstCharLowerToUpper.getString(bean.getFirstName());
        String lastName = FirstCharLowerToUpper.getString(bean.getLastName());
        String userRole = bean.getUserRole();

        try {
            Registration registration = new Registration();
            String userid = (String) pKGenerator.generate(Registration.class, "ACCO");
            registration.setAccountId(userid);
            registration.setFirstName(firstName);
            registration.setLastName(lastName);
            registration.setPrimaryEmail(emailId);
            registration.setPassword(password);
            registration.setUserRole(userRole);
            registration.setDateOfCreation(date);
            registration.setMailSubscriptionStatus(false);

            switch (registration.getUserRole()) {
                /*teacher sign-up*/
                case "Admin":
//                    registration.setCountryCode(Integer.parseInt(bean.getCountryCode()));
                    registration.setCountryCode(bean.getCountryCode());
                    registration.setMobileNum(Long.parseLong(bean.getMobileNum()));
                    registration.setAddress(bean.getAddress());
                    registration.setCity(bean.getCity());
                    json = adminRegistration(registration);
                    break;
                case "teacher":
//                    registration.setCountryCode(Integer.parseInt(bean.getCountryCode()));
                    registration.setCountryCode(bean.getCountryCode());
                    registration.setMobileNum(Long.parseLong(bean.getMobileNum()));
                    registration.setAddress(bean.getAddress());
                    registration.setCity(bean.getCity());
                    json = teacherRegistration(registration);
                    break;

                /*parent sign-up*/
                case "parent":
                    registration.setCountryCode(bean.getCountryCode());
                    registration.setMobileNum(Long.parseLong(bean.getMobileNum()));
                    registration.setAddress(bean.getAddress());
                    registration.setCity(bean.getCity());
                    json = parentRegistration(registration);
                    deleteOtp(emailId);
                    break;

                /*student sign-up*/
                case "student":
                    String schoolName = bean.getSchoolName();
                    String mainAccountId = bean.getAccountId();//PARENT REGISTRATION ID
                    registration.setAddress(bean.getAddress());
                    registration.setCity(bean.getCity());
                    json = studentRegistrationByParent(registration, mainAccountId, schoolName);
                    deleteOtp(emailId);
                    break;

                /*guest sign-up*/
                case "guest":
                    registration.setCountryCode(bean.getCountryCode());
                    registration.setMobileNum(Long.parseLong(bean.getMobileNum()));
                    registration.setGradeId(bean.getGradeId());
                    json = guestSignUP(registration);
                    deleteOtp(emailId);
                    break;
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return json;
    }

    @PostMapping(value = "/usersLogin")
    public JSONObject usersLogin(@RequestBody final UsersMetaData usersBean) {
        JSONObject json = new JSONObject();
        String loginMsg;
        String mailId = usersBean.getPrimaryEmail().trim();
        mailId = mailId.toLowerCase();
        String pass = usersBean.getPassword();

        Map<String, Object> map = new HashMap<>();
        map.put("primaryEmail", mailId);
        map.put("password", pass);

        try {
            List<Object> listObj = service.getObject(Registration.class, map);
            if (listObj.size() > 0) {
                Registration registration = (Registration) listObj.get(0);
                if (registration.getPrimaryEmail().equals(mailId) && registration.getPassword().equals(pass)) {
                    String userRole = registration.getUserRole();
                    switch (userRole) {
                        /*teacher login*/
                        case "teacher":
                            List<Object> listTeacherObj = service.getObject(TeacherAccount.class, map);
                            TeacherAccount teacherAccount = (TeacherAccount) listTeacherObj.get(0);
                            if (teacherAccount.getStatus()) {//if status is true then he is blocked
                                json.put("msg", "You are blocked. Please contact your administration.");
                            } else {
                                json.put("accountId", teacherAccount.getTeacherAccountId());
                                json.put("firstName", registration.getFirstName());
                                json.put("lastName", registration.getLastName());
                                json.put("primaryEmail", registration.getPrimaryEmail());
                                json.put("password", registration.getPassword());
                                json.put("countryCode", registration.getCountryCode());
                                json.put("mobileNum", registration.getMobileNum());
                                json.put("userRole", registration.getUserRole());
                                json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
                                json.put("address", registration.getAddress());
                                json.put("city", registration.getCity());
                                if (listOfImagesNameExistOrNot(teacherAccount.getTeacherAccountId() + ".jpg")) {
                                    json.put("image", "/UserImages/" + teacherAccount.getTeacherAccountId() + ".jpg");
//                                    json.put("image", "/adasysImages/" + teacherAccount.getTeacherAccountId() + ".jpg");
                                } else {
                                    json.put("image", null);
                                }
                                json.put("msg", "Login successful!");
                            }
                            break;

                        /*Admin login*/
                        case "Admin":
                            json.put("accountId", registration.getAccountId());
                            json.put("firstName", registration.getFirstName());
                            json.put("lastName", registration.getLastName());
                            json.put("primaryEmail", registration.getPrimaryEmail());
                            json.put("password", registration.getPassword());
                            json.put("countryCode", registration.getCountryCode());
                            json.put("mobileNum", registration.getMobileNum());
                            json.put("userRole", registration.getUserRole());
                            json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
                            json.put("address", registration.getAddress());
                            json.put("city", registration.getCity());
                            if (listOfImagesNameExistOrNot(registration.getAccountId() + ".jpg")) {

                                json.put("image", "/UserImages/" + registration.getAccountId() + ".jpg");
//                                json.put("image", "/adasysImages/" + registration.getAccountId() + ".jpg");
                            } else {
                                json.put("image", null);
                            }
                            json.put("msg", "Login successful!");
                            break;

                        case "guest":
                            service.delete(String.format("%s", "DELETE FROM StudentAccount s WHERE s.primaryEmail = '" + registration.getPrimaryEmail() + "' AND s.subscribeId IS NULL"));

                            String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + registration.getGradeId() + "'")).get(0);
                            json.put("gradeName", gradeName);
                            json.put("accountId", registration.getAccountId());
                            json.put("firstName", registration.getFirstName());
                            json.put("lastName", registration.getLastName());
                            json.put("primaryEmail", registration.getPrimaryEmail());
                            json.put("password", registration.getPassword());
                            json.put("countryCode", registration.getCountryCode());
                            json.put("mobileNum", registration.getMobileNum());
                            json.put("userRole", registration.getUserRole());
                            json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
                            json.put("msg", "Login successful!");

                            break;
                    }
                } else {
                    loginMsg = "Your account email or password is incorrect. If you don't remember your password, you can reset it.";
                    json.put("msg", loginMsg);
                }
            } else {
                loginMsg = "Your account email or password is incorrect. If you don't remember your password, you can reset it...";
                json.put("msg", loginMsg);
            }
        } catch (Exception e) {
            loginMsg = "Something went wrong. Try Again.";
            json.put("msg", loginMsg);
        }
        return json;
    }

    @RequestMapping(value = {"/loginAsParentOrStudent"}, method = RequestMethod.POST)
    public @ResponseBody
    JSONObject loginAsParentOrStudent(@RequestBody final Map<String, Object> mapbean) {
//        String mainAccountId = bean.getPrimaryEmail();
        String mainAccountId = (String) mapbean.get("primaryEmail");
        String pass = (String) mapbean.get("password");
        String studentAccId = (String) mapbean.get("accountId");

        JSONObject json = new JSONObject();
        String loginMsg;
        try {
            if (mainAccountId != null) {
                mainAccountId = mainAccountId.toLowerCase();

                /*login as parent*/
                Map<String, Object> map = new HashMap<>();
                map.put("primaryEmail", mainAccountId);
                map.put("password", pass);

                List<Object> listObj = service.getObject(ParentAccount.class, map);
                if (listObj.size() > 0) {
                    ParentAccount parentAccount = (ParentAccount) listObj.get(0);
                    if (parentAccount.getPrimaryEmail().equals(mainAccountId) && parentAccount.getPassword().equals(pass)) {

                        /*delete the students of this parent if that student is not having subsTypeId*/
                        service.delete(String.format("%s", "DELETE FROM StudentAccount s WHERE s.primaryEmail = '" + parentAccount.getPrimaryEmail() + "' AND s.subscribeId IS NULL"));

                        json.put("firstName", parentAccount.getFirstName());
                        json.put("lastName", parentAccount.getLastName());
                        json.put("primaryEmail", parentAccount.getPrimaryEmail());
                        json.put("password", parentAccount.getPassword());
                        json.put("countryCode", parentAccount.getCountryCode());
                        json.put("mobileNum", parentAccount.getMobileNum());
                        json.put("pAccountId", parentAccount.getParentAccountId());
                        json.put("dateOfCreation", MyDateFormate.dateToString(parentAccount.getDateOfCreation()));
                        json.put("address", parentAccount.getAddress());
                        json.put("city", parentAccount.getCity());
                        json.put("accountId", parentAccount.getRegistration().getAccountId());
                        json.put("mailStatus", parentAccount.getRegistration().getMailSubscriptionStatus());
                        json.put("userRole", "parent");
                        json.put("message", "Login successful!");

                        if (listOfImagesNameExistOrNot(parentAccount.getParentAccountId() + ".jpg")) {
                            json.put("image", "/UserImages/" + parentAccount.getParentAccountId() + ".jpg");
                        } else {
                            json.put("image", null);
                        }
                    } else {
                        loginMsg = "Your account email or password is incorrect. If you don't remember your password, you can reset it.";
                        json.put("msg", loginMsg);
                    }
                } else {
                    loginMsg = "Your account email or password is incorrect. If you don't remember your password, you can reset it.";
                    json.put("msg", loginMsg);
                }
            } else {
                /*login as student*/
                System.out.println("inside student block");
                StudentAccount studentAccount = (StudentAccount) service.getObject("FROM StudentAccount s WHERE s.studentAccountId = '" + studentAccId + "'").get(0);

                String gradeId = studentAccount.getGradeId();
                String syllabusId = studentAccount.getSyllabusId();

                String gradeName = getGradeName(gradeId);
                String syllabusName = getSyllabusName(syllabusId);

                if (studentAccount.getStudentAccountId().equals(studentAccId) && studentAccount.getPassword().equals(pass)) {
                    int x;
                    if (studentAccount.getStatus()) {//if status is true then he is blocked
                        x = 1;
                    } else {
                        x = 0;
                    }
                    int status = x;
                    switch (status) {
                        case 1:
                            json.put("msg", "You are blocked. Please contact your parent.");
                            break;

                        case 0:
                            /**
                             * SCENARIO 1: STUDENT ID IS PRESENT -> CHECK EXPIRE
                             * OR NOT -> (1.)IF EXPIRED THEN NO LOGIN (2.)IF NOT
                             * EXPIRED THEN LOGIN
                             *
                             * SCENARIO 2: STUDENT ID IS NOT AVAILABLE IN
                             * PAYMENT CHECKOUT -> NO LOGIN
                             */

                            String sql = "from SubscribeType st where st.subsTypeId in"
                                    + "(select ss.subscribeId from StudentSubscription ss "
                                    + "where ss.studentAccountId='" + studentAccId + "') ";
                            System.out.println("sql" + sql);
                            List<Object> SubscribeTypeObjects = this.service.getObject(sql);
                            int count1 = 0;
                            SubscribeType subscribeType = null;
//                            String[] activeSubId = null;
                            for (Object object : SubscribeTypeObjects) {
                                subscribeType = (SubscribeType) object;
                                Date date1 = subscribeType.getValidTill();
                                Date d1 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date1));//d2::: Thu Jan 24 00:00:00 IST 2019
                                Timestamp date2 = new Timestamp(System.currentTimeMillis());
                                Date d2 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date2));//d::: Thu Jan 24 00:00:00 IST 2019
                                long diffDate = d1.getTime() - d2.getTime();
                                if (diffDate >= 0) {
//                                    activeSubId[count1]=subscribeType.getSubsTypeId();
                                    count1++;
                                }

                            }

//                            Map<String, Object> map2 = new HashMap<>();
//                            map2.put("allotedStudentAccountId", studentAccId);
//                            List<Object> liPaymentCheckoutObj = service.getObject(PaymentCheckout.class, map2);
//                            if (liPaymentCheckoutObj.size() > 0) {
////                                SCENARIO 1 ->
//                                /*CHECK IF PACKAGE IS EXPIRED:*/
//                                PaymentCheckout paymentCheckout = (PaymentCheckout) liPaymentCheckoutObj.get(0);
//
////                  
//                                Date date1 = paymentCheckout.getValidTill();
//                                Date d1 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date1));//d2::: Thu Jan 24 00:00:00 IST 2019
//
//                                Timestamp date2 = new Timestamp(System.currentTimeMillis());
//                                Date d2 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date2));//d::: Thu Jan 24 00:00:00 IST 2019
//
//                                long diffDate = d1.getTime() - d2.getTime();
//                                if (diffDate >= 0) {
                            if (count1 > 0) {
                                /*IF NOT EXPIRED THEN LOGIN*/

                                json.put("firstName", studentAccount.getFirstName());
                                json.put("lastName", studentAccount.getLastName());
                                json.put("primaryEmail", studentAccount.getPrimaryEmail());
                                json.put("password", studentAccount.getPassword());
                                json.put("countryCode", studentAccount.getCountryCode());
                                json.put("accountId", studentAccount.getStudentAccountId());
                                json.put("status", studentAccount.getStatus());
                                json.put("dateOfCreation", MyDateFormate.dateToString(studentAccount.getDateOfCreation()));
                                json.put("schoolName", studentAccount.getSchoolName());
                                json.put("gradeName", gradeName);
                                json.put("gradeId", studentAccount.getGradeId());
                                json.put("syllabusId", studentAccount.getSyllabusId());
                                json.put("address", studentAccount.getAddress());
                                json.put("city", studentAccount.getCity());
                                json.put("subsTypeId", studentAccount.getSubscribeId());
//                                json.put("subsTypeId", activeSubId);
                                json.put("userRole", "student");
                                json.put("validTill", subscribeType.getValidTill().getTime());
                                json.put("validFrom", subscribeType.getValidFrom().getTime());
//                                    json.put("validTill", paymentCheckout.getValidTill().getTime());
//                                    json.put("validFrom", paymentCheckout.getValidFrom().getTime());
                                json.put("batchId", studentAccount.getBatchId());
                                json.put("syllabusName", syllabusName);
                                List<Object> objects = this.service.getObject("from StudentSubscription where studentAccountId='" + studentAccount.getStudentAccountId() + "'");
                                String[] ids = new String[objects.size()];
                                int count = 0;
                                for (Object object : objects) {
                                    StudentSubscription studentSubscription = (StudentSubscription) object;
                                    ids[count] = studentSubscription.getSyllabusId();
                                    count++;
                                }
                                json.put("syllabusIds", ids);
                                long diff = subscribeType.getValidTill().getTime() - subscribeType.getValidFrom().getTime();
//                                    long diff = paymentCheckout.getValidTill().getTime() - paymentCheckout.getValidFrom().getTime();
                                long days = diff / (24 * 60 * 60 * 1000);
                                json.put("noOfDaysLeft", days);

                                if (listOfImagesNameExistOrNot(studentAccount.getStudentAccountId() + ".jpg")) {
                                    json.put("image", "/UserImages/" + studentAccount.getStudentAccountId() + ".jpg");
                                } else {
                                    json.put("image", null);
                                }
                                json.put("msg", "Login successful!");
                            } else {
                                /*IF EXPIRED THEN NO LOGIN*/
                                json.put("msg", "Your subscription expired.");
                            }

//                            } else {
////                                SCENARIO 2 ->
//                                json.put("msg", "You are not subscribed to any package.");
//                            }
                            break;

                    }
                } else {
                    loginMsg = "Your account email or password is incorrect. If you don't remember your password, you can reset it.";
                    json.put("msg", loginMsg);
                }
//                } else {
//                    loginMsg = "Your account email or password is incorrect. If you don't remember your password, you can reset it.";
//                    json.put("msg", loginMsg);
//                }
            }
        } catch (Exception e) {
        }

        return json;
    }

    //    public String getGradeName(String accountId) {
    public String getGradeName(String gradeId) {
        try {
            List<Object> list = service.getObject("SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + gradeId + "'");
            String gradeName = (String) list.get(0);
            return gradeName;
        } catch (Exception e) {
            throw e;
        }
    }

    public String getSyllabusName(String syllabusId) {
        try {
            List<Object> list = service.getObject("SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + syllabusId + "'");
            String syllabusName = (String) list.get(0);
            return syllabusName;
        } catch (Exception e) {
            throw e;
        }
    }

    @RequestMapping(value = {"/parentOrGuestLogin"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> parentOrGuestLogin(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();

        String primaryEmail = mapBean.get("primaryEmail");
        String password = mapBean.get("password");

        Map<String, Object> map = new HashMap<>();
        map.put("primaryEmail", primaryEmail);
        map.put("password", password);

        List<Object> listObj = service.getObject(Registration.class, map);
        if (listObj.size() > 0) {
            Registration registration = (Registration) listObj.get(0);
            if (registration.getPrimaryEmail().equals(primaryEmail) && registration.getPassword().equals(password)) {
                json.put("accountId", registration.getAccountId());
                json.put("firstName", registration.getFirstName());
                json.put("lastName", registration.getLastName());
                json.put("primaryEmail", registration.getPrimaryEmail());
                json.put("password", registration.getPassword());
                json.put("countryCode", registration.getCountryCode());
                json.put("mobileNum", registration.getMobileNum());
                json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
                json.put("address", registration.getAddress());
                json.put("city", registration.getCity());
                json.put("userRole", registration.getUserRole());
                json.put("mailSubscriptionStatus", registration.getMailSubscriptionStatus());
                json.put("message", "Login successful!");

            } else {
                json.put("msg", "Your account email or password is incorrect. If you don't remember your password, you can reset it.");
            }
        } else {
            json.put("msg", "Your account email or password is incorrect. If you don't remember your password, you can reset it.");
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/updateGuestToParent"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> updateGuestToParent(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        String accountId = mapBean.get("accountId");
        try {
            int x0 = service.update(String.format("%s", "UPDATE Registration r SET r.userRole = 'parent' WHERE r.accountId = '" + accountId + "'"));
            if (x0 > 0) {

                Registration registration = (Registration) service.getObject(String.format("%s", "FROM Registration r WHERE r.accountId = '" + accountId + "'")).get(0);

                String s = "SELECT COUNT(*) FROM ParentAccount p WHERE p.primaryEmail = '" + registration.getPrimaryEmail() + "'";
                int c = (int) service.countObject(s);
                if (c == 0) {
                    ParentAccount parentAccount = new ParentAccount();
                    String parentId = (String) pKGenerator.generate(ParentAccount.class, "PARENT");
                    parentAccount.setParentAccountId(parentId);
                    parentAccount.setFirstName(registration.getFirstName());
                    parentAccount.setLastName(registration.getLastName());
                    parentAccount.setPrimaryEmail(registration.getPrimaryEmail());
                    parentAccount.setPassword(registration.getPassword());
                    parentAccount.setCountryCode(registration.getCountryCode());
                    parentAccount.setMobileNum(registration.getMobileNum());
                    parentAccount.setDateOfCreation(registration.getDateOfCreation());
                    parentAccount.setAddress(registration.getAddress());
                    parentAccount.setCity(registration.getCity());

                    parentAccount.setRegistration(registration);

                    service.save(parentAccount);

                    json.put("msg", "Guest updated to parent!");
                }
            }

        } catch (Exception e) {
            json.put("msg", "Something wentwrong!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/assignSubsTypeIdToStudent"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> assignSubsTypeIdToStudent(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        String sAccountId = mapBean.get("sAccountId");
        String subsTypeId = mapBean.get("subsTypeId");
        try {

            Object[] loadSubscribeTypeProperties = service.loadProperties(String.format("%s", "SELECT s.syllabusId, s.gradeId FROM SubscribeType s WHERE s.subsTypeId = '" + subsTypeId + "'")).get(0);
            String syllabusId = (String) loadSubscribeTypeProperties[0];
            String gradeId = (String) loadSubscribeTypeProperties[1];

            int x = service.update(String.format("%s", "UPDATE StudentAccount s SET s.subscribeId = '" + subsTypeId + "', s.gradeId = '" + gradeId + "', s.syllabusId = '" + syllabusId + "' WHERE s.studentAccountId = '" + sAccountId + "'"));
            if (x > 0) {
                json.put("msg", "Subscription has been assigned to the student!");
                StudentSubscription studentSubscription = new StudentSubscription();
                Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
                studentSubscription.setDateOfCreation(timestamp);
                studentSubscription.setGradeId(gradeId);
                studentSubscription.setBatchId(null);
                studentSubscription.setStudentAccountId(sAccountId);
                studentSubscription.setSyllabusId(syllabusId);
                studentSubscription.setSubscribeId(subsTypeId);
                this.service.save(studentSubscription);
                if (subsTypeId.equals("SUBST000011")) {
                    studentSubscription.setDateOfCreation(timestamp);
                    studentSubscription.setGradeId(gradeId);
                    studentSubscription.setStudentAccountId(sAccountId);
                    studentSubscription.setSubscribeId("SUBST000016");
                    studentSubscription.setBatchId("BATCH000021");
                    studentSubscription.setSyllabusId("SYLL000020");
                    this.service.save(studentSubscription);
                }
                if (subsTypeId.equals("SUBST000012")) {
                    studentSubscription.setDateOfCreation(timestamp);
                    studentSubscription.setGradeId(gradeId);
                    studentSubscription.setStudentAccountId(sAccountId);
                    studentSubscription.setSubscribeId("SUBST000017");
                    studentSubscription.setBatchId("BATCH000022");
                    studentSubscription.setSyllabusId("SYLL000021");
                    this.service.save(studentSubscription);
                }
                if (subsTypeId.equals("SUBST000013")) {
                    studentSubscription.setDateOfCreation(timestamp);
                    studentSubscription.setGradeId(gradeId);
                    studentSubscription.setStudentAccountId(sAccountId);
                    studentSubscription.setSubscribeId("SUBST000018");
                    studentSubscription.setBatchId("BATCH000023");
                    studentSubscription.setSyllabusId("SYLL000022");
                    this.service.save(studentSubscription);
                }
                if (subsTypeId.equals("SUBST000014")) {
                    studentSubscription.setDateOfCreation(timestamp);
                    studentSubscription.setGradeId(gradeId);
                    studentSubscription.setStudentAccountId(sAccountId);
                    studentSubscription.setSubscribeId("SUBST000019");
                    studentSubscription.setBatchId("BATCH000024");
                    studentSubscription.setSyllabusId("SYLL000023");
                    this.service.save(studentSubscription);
                }
                if (subsTypeId.equals("SUBST000015")) {
                    studentSubscription.setDateOfCreation(timestamp);
                    studentSubscription.setGradeId(gradeId);
                    studentSubscription.setStudentAccountId(sAccountId);
                    studentSubscription.setSubscribeId("SUBST000020");
                    studentSubscription.setBatchId("BATCH000025");
                    studentSubscription.setSyllabusId("SYLL000024");
                    this.service.save(studentSubscription);
                }

            }

        } catch (Exception e) {
            json.put("msg", "Something went wrong!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }
//    @RequestMapping(value = {"/assignSubsTypeIdToStudent"}, method = RequestMethod.POST)
//    public @ResponseBody
//    ResponseEntity<?> assignSubsTypeIdToStudent(@RequestBody final Map<String, String> mapBean) {
//        JSONObject json = new JSONObject();
//        String sAccountId = mapBean.get("sAccountId");
//        String subsTypeId = mapBean.get("subsTypeId");
//        try {
//
//            Object[] loadSubscribeTypeProperties = service.loadProperties(String.format("%s", "SELECT s.syllabusId, s.gradeId FROM SubscribeType s WHERE s.subsTypeId = '" + subsTypeId + "'")).get(0);
//            String syllabusId = (String) loadSubscribeTypeProperties[0];
//            String gradeId = (String) loadSubscribeTypeProperties[1];
//            String multiplesubId = (String) pKGenerator.generate(MultipleSubscriptionForstudent.class, "MULTISUB");
//            MultipleSubscriptionForstudent multipleSubscriptionForstudent = new MultipleSubscriptionForstudent();
//            multipleSubscriptionForstudent.setMultipleSubId(multiplesubId);
//            multipleSubscriptionForstudent.setGradeId(gradeId);
//            multipleSubscriptionForstudent.setStudentAccountId(sAccountId);
//            multipleSubscriptionForstudent.setSubscribeId(subsTypeId);
//            multipleSubscriptionForstudent.setSyllabusId(syllabusId);
//
//            int x = service.save(multipleSubscriptionForstudent);
//
////            int x = service.update(String.format("%s", "UPDATE StudentAccount s SET s.subscribeId = '" + subsTypeId + "', s.gradeId = '" + gradeId + "', s.syllabusId = '" + syllabusId + "' WHERE s.studentAccountId = '" + sAccountId + "'"));
//            if (x > 0) {
//                json.put("msg", "Subscription has been assigned to the student!");
//            }
//
//        } catch (Exception e) {
//            json.put("msg", "Something went wrong!");
//        }
//
//        return new ResponseEntity<>(json, HttpStatus.OK);
//    }

    @PostMapping(value = {"/logout"})
    public @ResponseBody
    ResponseEntity<?> logout(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        try {
            request.getSession().invalidate();
            json.put("msg", "Logout Successful!");
        } catch (Exception e) {
            json.put("msg", e.getCause().getMessage());
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    /**
     * EDIT ADMIN'S DETAIL
     *
     * @param bean
     * @return
     */
    public JSONObject editDetails_admin(Map<String, Object> bean) {
        JSONObject json = new JSONObject();
        String accountId = (String) bean.get("accountId");//ACCO000001
        String fname = (String) bean.get("firstName");
        String lname = (String) bean.get("lastName");
        String email = (String) bean.get("primaryEmail");
        long mobile = (long) bean.get("mobile");

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("accountId", accountId);

            List<Object> listRegObject = service.getObject(Registration.class, map);
            Registration registration = (Registration) listRegObject.get(0);
            registration.setFirstName(fname);
            registration.setLastName(lname);
            registration.setPrimaryEmail(email);
            registration.setMobileNum(mobile);

            int x = service.update(registration);
            if (x > 0) {
                /*sending email to the admin*/
                String subject2 = "Account Updated";
                String emailMsg2 = "Hi Admin,<br><br>"
                        + "Your account has been updated successfully.<br><br>"
                        + "<b>Login details:</b><br>"
                        + "<table>"
                        + "<tr><td>Email</td><td> : " + registration.getPrimaryEmail() + "</td></tr>"
                        + "<tr><td>Password</td><td> : " + registration.getPassword() + "</td></tr>"
                        + "</table><br><br>"
                        + "<b>Yours Sincerely,</b><br>"
                        + "YOLEARN Team.<br><br>"
                        + "Thanks for choosing Yolearn";
                Set<String> to2 = new HashSet<>();
                to2.add("admin@yolearn.com");
                javaMail_Sender_Info.composeAndSend(subject2, to2, emailMsg2);

                json.put("accountId", registration.getAccountId());
                json.put("firstName", registration.getFirstName());
                json.put("lastName", registration.getLastName());
                json.put("primaryEmail", registration.getPrimaryEmail());
                json.put("password", registration.getPassword());
                json.put("countryCode", registration.getCountryCode());
                json.put("mobileNum", registration.getMobileNum());
                json.put("userRole", registration.getUserRole());
                json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
                json.put("address", registration.getAddress());
                json.put("city", registration.getCity());
                json.put("msg", "Account details updated!");
            } else {
                json.put("msg", "Account details not updated! This mobile/email is already registered.");
            }
        } catch (Exception e) {
        }
        return json;
    }

    /**
     * EDIT TEACHER'S DETAIL
     *
     * @param bean
     * @return
     */
    public JSONObject editDetails_teacher(Map<String, Object> bean) {
        JSONObject json = null;
        String teacherId = (String) bean.get("accountId");//TEACHER000001
        String fname = (String) bean.get("firstName");
        String lname = (String) bean.get("lastName");
        String email = (String) bean.get("primaryEmail");
        long mobile = (long) bean.get("mobile");

        try {
            String testData[] = {email};
//            if (isAddressValid(testData[0]) == true) {

            Map<String, Object> map = new HashMap<>();
            map.put("teacherAccountId", teacherId);
            List<Object> listOfTeacherObj = service.getObject(TeacherAccount.class, map);
            TeacherAccount teacherAccount = (TeacherAccount) listOfTeacherObj.get(0);
            String accountId = teacherAccount.getRegistration().getAccountId();

            Map<String, Object> map1 = new HashMap<>();
            map1.put("accountId", accountId);
            List<Object> listOfRegObj = service.getObject(Registration.class, map1);

            Registration registration = (Registration) listOfRegObj.get(0);
            int x1 = service.update(registration);
            if (x1 > 0) {
                teacherAccount.setFirstName(fname);
                teacherAccount.setLastName(lname);
                teacherAccount.setPrimaryEmail(email);
                teacherAccount.setMobileNum(mobile);
                teacherAccount.setRegistration(registration);
                registration.setTeacherAccount(teacherAccount);
                registration.setFirstName(fname);
                registration.setLastName(lname);
                registration.setPrimaryEmail(email);
                registration.setMobileNum(mobile);
                int x = service.update(teacherAccount);

                /*update teacher name in class_scheduler if this teacher details are there*/
                Map<String, Object> mapClassScheduler = new HashMap<>();
                mapClassScheduler.put("presenterUniqueName", teacherId);
                List<Object> listClassSchedObject = service.getObject(ClassScheduler.class, mapClassScheduler);

                if (listClassSchedObject.size() > 0) {
                    for (Object object : listClassSchedObject) {
                        ClassScheduler cs = (ClassScheduler) object;
                        cs.setPresenterDisplayName(fname);
                        service.update(cs);
                    }
                }

                /*now sending email*/
                if (x > 0) {
                    json = new JSONObject();
                    /*sending email to the user*/
                    String subject1 = "YOLEARN - Account Updated";
                    String emailMsg1 = "Hi <b>" + teacherAccount.getFirstName() + ",</b><br><br>"
                            + "Your account has been updated successfully.<br><br>"
                            + "<b>Login details:</b><br>"
                            + "<table>"
                            + "<tr><td>Email</td><td> : " + teacherAccount.getPrimaryEmail() + "</td></tr>"
                            + "<tr><td>Password</td><td> : " + teacherAccount.getPassword() + "</td></tr>"
                            + "</table><br><br>"
                            + "<small>For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
                            + "You received this message because this email address was used to register you in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                            + "<b>Yours Sincerely,</b><br>"
                            + "YOLEARN Team.<br><br>"
                            + "Thanks for choosing Yolearn";
                    Set<String> to1 = new HashSet<>();
                    to1.add(teacherAccount.getPrimaryEmail());
                    javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg1);

                    /*sending email to the admin*/
                    String subject2 = "Account Updated(Teacher)";
                    String emailMsg2 = "Hi Admin,<br><br>"
                            + "<b>" + teacherAccount.getFirstName() + "'s</b> account has been updated successfully.<br><br>"
                            + "<b>Login details:</b><br>"
                            + "<table>"
                            + "<tr><td>Email</td><td> : " + teacherAccount.getPrimaryEmail() + "</td></tr>"
                            + "<tr><td>Password</td><td> : " + teacherAccount.getPassword() + "</td></tr>"
                            + "</table><br><br>"
                            + "<b>Yours Sincerely,</b><br>"
                            + "YOLEARN Team.";

                    Set<String> to2 = new HashSet<>();
                    to2.add("admin@yolearn.com");
                    javaMail_Sender_Info.composeAndSend(subject2, to2, emailMsg2);

                    json.put("accountId", teacherAccount.getTeacherAccountId());
                    json.put("firstName", registration.getFirstName());
                    json.put("lastName", registration.getLastName());
                    json.put("primaryEmail", registration.getPrimaryEmail());
                    json.put("password", registration.getPassword());
                    json.put("countryCode", registration.getCountryCode());
                    json.put("mobileNum", registration.getMobileNum());
                    json.put("userRole", registration.getUserRole());
                    json.put("dateOfCreation", MyDateFormate.dateToString(registration.getDateOfCreation()));
                    json.put("address", registration.getAddress());
                    json.put("city", registration.getCity());
                    json.put("msg", "Account details updated!");
                } else {
                    json = new JSONObject();
                    json.put("msg", "Account details not updated! This mobile/email is already registered.");
                }
            } else {
                json = new JSONObject();
//                    json.put("msg", "Something went wrong. Try Again!");
                json.put("msg", "Account details not updated! This mobile/email is already registered.");
            }
//            } else {
//                json.put("msg", "Coudn't find google account!");
//            }
        } catch (Exception e) {
        }
        return json;
    }

    /**
     * EDIT PARENT'S DETAIL
     *
     * @param bean
     * @return
     */
    public JSONObject editDetails_parents(Map<String, Object> bean) {
        JSONObject json = null;
        String parentId = (String) bean.get("accountId");//PARENT000001
        String fname = (String) bean.get("firstName");
        String lname = (String) bean.get("lastName");
        String email = (String) bean.get("primaryEmail");
        long mobile = (long) bean.get("mobile");
        try {
            String testData[] = {email};
//            if (isAddressValid(testData[0]) == true) {

            Map<String, Object> map = new HashMap<>();
            map.put("parentAccountId", parentId);
            List<Object> listOfParentObj = service.getObject(ParentAccount.class, map);
            ParentAccount parentAccount = (ParentAccount) listOfParentObj.get(0);
            String accountId = parentAccount.getRegistration().getAccountId();

            Map<String, Object> map1 = new HashMap<>();
            map1.put("accountId", accountId);
            List<Object> listOfRegObj = service.getObject(Registration.class, map1);
            Registration registration = (Registration) listOfRegObj.get(0);
            int x1 = service.update(registration);
            if (x1 > 0) {
                parentAccount.setFirstName(fname);
                parentAccount.setLastName(lname);
                parentAccount.setPrimaryEmail(email);
                parentAccount.setMobileNum(mobile);
                parentAccount.setRegistration(registration);
                registration.setParentAccount(parentAccount);
                registration.setFirstName(fname);
                registration.setLastName(lname);
                registration.setPrimaryEmail(email);
                registration.setMobileNum(mobile);
                int x = service.update(parentAccount);
                if (x > 0) {
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("parentAccountId", accountId);
                    List<Object> listStudentObj = service.getObject(StudentAccount.class, map2);
                    if (listStudentObj.size() > 0) {
                        for (Object object : listStudentObj) {
                            StudentAccount studentAccount = (StudentAccount) object;
                            studentAccount.setPrimaryEmail(email);
                            service.update(studentAccount);
                        }
                        json = new JSONObject();

                        /*sending email to the user*/
                        String subject1 = "YOLEARN - Account Updated";
                        String emailMsg1 = "Hi <b>" + parentAccount.getFirstName() + ",</b><br><br>"
                                + "Your account has been updated successfully.<br><br>"
                                + "<b>Login details:</b><br>"
                                + "<table>"
                                + "<tr><td>Email</td><td> : " + parentAccount.getPrimaryEmail() + "</td></tr>"
                                + "<tr><td>Password</td><td> : " + parentAccount.getPassword() + "</td></tr>"
                                + "</table><br><br>"
                                + "<small>For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
                                + "You received this message because this email address was used to register you in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                                + "<b>Yours Sincerely,</b><br>"
                                + "YOLEARN Team.<br><br>"
                                + "Thanks for choosing Yolearn";
                        Set<String> to1 = new HashSet<>();
                        to1.add(parentAccount.getPrimaryEmail());
                        javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg1);

                        json.put("firstName", parentAccount.getFirstName());
                        json.put("lastName", parentAccount.getLastName());
                        json.put("primaryEmail", parentAccount.getPrimaryEmail());
                        json.put("password", parentAccount.getPassword());
                        json.put("countryCode", parentAccount.getCountryCode());
                        json.put("mobileNum", parentAccount.getMobileNum());
                        json.put("pAccountId", parentAccount.getParentAccountId());
                        json.put("dateOfCreation", MyDateFormate.dateToString(parentAccount.getDateOfCreation()));
                        json.put("address", parentAccount.getAddress());
                        json.put("city", parentAccount.getCity());
                        json.put("accountId", parentAccount.getRegistration().getAccountId());
                        json.put("userRole", "parent");
                        json.put("msg", "Account details updated!");
                    }
                } else {
                    json = new JSONObject();
                    json.put("msg", "Account details not updated! This mobile/email is already registered.");
                }
            } else {
                json = new JSONObject();
                json.put("msg", "Account details not updated! This mobile/email is already registered.");
            }
//            } else {
//                json.put("msg", "Coudn't find google account!");
//            }
        } catch (Exception e) {
        }
        return json;
    }

    /**
     * EDIT STUDENTS'S DETAIL
     *
     * @param bean
     * @return
     */
    public JSONObject editDetails_student(Map<String, Object> bean) {
        JSONObject json = new JSONObject();
        String studentId = (String) bean.get("accountId");//STUDENT000001
        String fname = (String) bean.get("firstName");
        String lname = (String) bean.get("lastName");
        String email = (String) bean.get("primaryEmail");

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("studentAccountId", studentId);
            List<Object> listOfStudentObj = service.getObject(StudentAccount.class, map);
            StudentAccount studentAccount = (StudentAccount) listOfStudentObj.get(0);
            studentAccount.setFirstName(fname);
            studentAccount.setLastName(lname);
            int x = service.update(studentAccount);
            if (x > 0) {
                /*sending email to the user*/
                String subject1 = "YOLEARN - Account Updated";
                String emailMsg1 = "Hi <b>" + studentAccount.getFirstName() + "</b>,<br><br>"
                        + "Your name has been updated successfully!<br><br>"
                        + "<b>Yours Sincerely,</b><br>"
                        + "YOLEARN Team.<br><br>"
                        + "Thanks for choosing Yolearn";
                Set<String> to1 = new HashSet<>();
                to1.add(email);
                javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg1);

                /*getting grade*/
                Map<String, Object> mapGrade = new HashMap<>();
                mapGrade.put("gradeId", studentAccount.getGradeId());
                Grade grade = (Grade) service.getObject(Grade.class, mapGrade).get(0);

                json.put("firstName", studentAccount.getFirstName());
                json.put("lastName", studentAccount.getLastName());
                json.put("primaryEmail", studentAccount.getPrimaryEmail());
                json.put("password", studentAccount.getPassword());
                json.put("countryCode", studentAccount.getCountryCode());
                json.put("accountId", studentAccount.getStudentAccountId());
                json.put("status", studentAccount.getStatus());
                json.put("dateOfCreation", MyDateFormate.dateToString(studentAccount.getDateOfCreation()));
                json.put("schoolName", studentAccount.getSchoolName());
                json.put("gradeName", grade.getGradeName());
                json.put("address", studentAccount.getAddress());
                json.put("city", studentAccount.getCity());
                json.put("msg", "Account details updated!");
            } else {
                json.put("msg", "Account details not updated!");
            }
        } catch (Exception e) {
        }
        return json;
    }

    @RequestMapping(value = {"/changeCredentials"}, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    ResponseEntity<?> changeCredentials(@RequestBody final Map<String, String> bean) {
        ResponseEntity<?> responseEntity;

        String fname = bean.get("firstName");
        String lname = bean.get("lastName");
        String mob = bean.get("mobile");
        String firstName = FirstCharLowerToUpper.getString(fname);
        String lastName = FirstCharLowerToUpper.getString(lname);
        String accountId = bean.get("accountId");
        String email = bean.get("primaryEmail");
        email = email.toLowerCase();
        String userRole = bean.get("userRole");

        /*creating Map<String, Object> map*/
        Map<String, Object> map = new HashMap<>();
        map.put("accountId", accountId);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("primaryEmail", email);

        JSONObject json = null;
        switch (userRole) {
            case "Admin":
                Long mobileA = Long.parseLong(mob);
                map.put("mobile", mobileA);
                json = editDetails_admin(map);
                break;

            case "teacher":
                Long mobileT = Long.parseLong(mob);
                map.put("mobile", mobileT);
                json = editDetails_teacher(map);
                break;

            case "parent":
                Long mobileP = Long.parseLong(mob);
                map.put("mobile", mobileP);
                json = editDetails_parents(map);
                break;

            case "student":
                json = editDetails_student(map);
                break;
        }
        responseEntity = new ResponseEntity<>(json, HttpStatus.OK);
        return responseEntity;
    }

    /**
     * CHANGE ADMIN'S PASSWORD
     *
     * @param bean
     * @return
     */
    public JSONObject changePassword_admin(Map<String, Object> bean) {
        JSONObject json = new JSONObject();
        String accountId = (String) bean.get("accountId");//ACCO000001
        String oldPassword = (String) bean.get("oldPassword");
        String newPassword = (String) bean.get("newPassword");

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("accountId", accountId);
            List<Object> listRegObject = service.getObject(Registration.class, map);
            Registration registration = (Registration) listRegObject.get(0);

            if (registration.getPassword().equals(oldPassword)) {
                registration.setPassword(newPassword);
                int x = service.update(registration);
                if (x > 0) {
                    /*sending email to the admin*/
                    String subject2 = "Password Updated";
                    String emailMsg2 = "Hi Admin,<br><br>"
                            + "Your account password has been updated successfully.<br><br>"
                            + "<b>Login details:</b><br>"
                            + "<table>"
                            + "<tr><td>Email</td><td> : " + registration.getPrimaryEmail() + "</td></tr"
                            + "<tr><td>Password</td><td> : " + registration.getPassword() + "</td></tr>"
                            + "</table><br><br>"
                            + "<b>Yours Sincerely,</b><br>"
                            + "YOLEARN Team.";

                    Set<String> to2 = new HashSet<>();
                    to2.add("admin@yolearn.com");
                    javaMail_Sender_Info.composeAndSend(subject2, to2, emailMsg2);
                    json.put("msg", "Password updated!");
                } else {
                    json.put("msg", "Password not updated!");
                }
            } else {
                json.put("msg", "Wrong Password!");
            }
        } catch (Exception e) {
        }
        return json;
    }

    /**
     * CHANGE TEACHER'S PASSWORD
     *
     * @param bean
     * @return
     */
    public JSONObject changePassword_teacher(Map<String, Object> bean) {
        JSONObject json = new JSONObject();
        String teacherId = (String) bean.get("accountId");//TEACHER000001
        String oldPassword = (String) bean.get("oldPassword");
        String newPassword = (String) bean.get("newPassword");

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("teacherAccountId", teacherId);
            List<Object> listOfTeacherObj = service.getObject(TeacherAccount.class, map);
            TeacherAccount teacherAccount = (TeacherAccount) listOfTeacherObj.get(0);
            String accountId = teacherAccount.getRegistration().getAccountId();

            Map<String, Object> map1 = new HashMap<>();
            map1.put("accountId", accountId);

            List<Object> listOfRegObj = service.getObject(Registration.class, map1);
            Registration registration = (Registration) listOfRegObj.get(0);
            if (registration.getPassword().equals(oldPassword)) {
                int x1 = service.update(registration);
                if (x1 > 0) {
                    teacherAccount.setPassword(newPassword);
                    teacherAccount.setRegistration(registration);
                    registration.setTeacherAccount(teacherAccount);
                    registration.setPassword(newPassword);
                    int x = service.update(teacherAccount);
                    if (x > 0) {
                        /*sending email to the user*/
                        String subject1 = "YOLEARN - Password Updated";
                        String emailMsg1 = "Hi <b>" + teacherAccount.getFirstName() + ",</b><br><br>"
                                + "Your password has been updated successfully.<br><br>"
                                + "<b>Login details:</b><br>"
                                + "<table>"
                                + "<tr><td>Email</td><td> : " + teacherAccount.getPrimaryEmail() + "</td></tr>"
                                + "<tr><td>Password</td><td> : " + teacherAccount.getPassword() + "</td></tr>"
                                + "</table><br><br>"
                                + "<small>For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
                                + "You received this message because this email address was used to register you in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                                + "<b>Yours Sincerely,</b><br>"
                                + "YOLEARN Team.<br><br>"
                                + "Thanks for choosing Yolearn";
                        Set<String> to1 = new HashSet<>();
                        to1.add(teacherAccount.getPrimaryEmail());
                        javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg1);

                        /*sending email to the admin*/
                        String subject2 = "Password Updated(Teacher)";
                        String emailMsg2 = "Hi Admin,<br><br>"
                                + "<b>" + teacherAccount.getFirstName() + "'s</b> password has been updated successfully.<br><br>"
                                + "<b>Login details:</b><br><br>"
                                + "<table>"
                                + "<tr><td>Email</td><td> : " + teacherAccount.getPrimaryEmail() + "</td></tr>"
                                + "<tr><td>Password</td><td> : " + teacherAccount.getPassword() + "</td></tr>"
                                + "</table><br><br>"
                                + "<b>Yours Sincerely,</b><br>"
                                + "YOLEARN Team.";

                        Set<String> to2 = new HashSet<>();
                        to2.add("admin@yolearn.com");
                        javaMail_Sender_Info.composeAndSend(subject2, to2, emailMsg2);
                        json.put("msg", "Password updated!");
                    } else {
                        json.put("msg", "Password not updated!");
                    }
                } else {
                    json.put("message", "Something went wrong. Try Again!");
                }
            } else {
                json.put("msg", "Wrong Password!");
            }
        } catch (Exception e) {
        }
        return json;
    }

    /**
     * CHANGE PARENT'S PASSWORD
     *
     * @param bean
     * @return
     */
    public JSONObject changePassword_parent(Map<String, Object> bean) {
        JSONObject json = new JSONObject();
        String parentId = (String) bean.get("accountId");//PARENT000001
        String oldPassword = (String) bean.get("oldPassword");
        String newPassword = (String) bean.get("newPassword");

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("parentAccountId", parentId);
            List<Object> listOfParentObj = service.getObject(ParentAccount.class, map);
            ParentAccount parentAccount = (ParentAccount) listOfParentObj.get(0);
            String accountId = parentAccount.getRegistration().getAccountId();

            Map<String, Object> map1 = new HashMap<>();
            map1.put("accountId", accountId);
            List<Object> listOfRegObj = service.getObject(Registration.class, map1);
            Registration registration = (Registration) listOfRegObj.get(0);
            if (registration.getPassword().equals(oldPassword)) {
                int x1 = service.update(registration);
                if (x1 > 0) {
                    parentAccount.setPassword(newPassword);
                    parentAccount.setRegistration(registration);
                    registration.setParentAccount(parentAccount);
                    registration.setPassword(newPassword);
                    int x = service.update(parentAccount);
                    if (x > 0) {
                        /*sending email to the user*/
                        String subject1 = "YOLEARN - Password Updated";
                        String emailMsg1 = "Hi <b>" + parentAccount.getFirstName() + ",</b><br><br>"
                                + "Your password has been updated successfully.<br><br>"
                                + "<b>Login details:</b><br>"
                                + "<table>"
                                + "<tr><td>Email</td><td> : " + parentAccount.getPrimaryEmail() + "</td></tr>"
                                + "<tr><td>Password</td><td> : " + parentAccount.getPassword() + "</td></tr>"
                                + "</table><br><br>"
                                + "<small>For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
                                + "You received this message because this email address was used to register you in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                                + "<b>Yours Sincerely,</b><br>"
                                + "YOLEARN Team.<br><br>"
                                + "Thanks for choosing Yolearn";
                        Set<String> to1 = new HashSet<>();
                        to1.add(parentAccount.getPrimaryEmail());
                        javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg1);
                        json.put("msg", "Password updated!");
                    } else {
                        json.put("msg", "Password not updated!");
                    }
                } else {
                    json.put("message", "Something went wrong. Try Again!");
                }
            } else {
                json.put("msg", "Wrong Password!");
            }
        } catch (Exception e) {
        }
        return json;
    }

    /**
     * CHANGE STUDENT'S PASSWORD
     *
     * @param bean
     * @return
     */
    public JSONObject changePassword_student(Map<String, Object> bean) {
        JSONObject json = new JSONObject();
        String studentId = (String) bean.get("accountId");//STUDENT000001
        String oldPassword = (String) bean.get("oldPassword");
        String newPassword = (String) bean.get("newPassword");

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("studentAccountId", studentId);
            List<Object> listOfStudentObj = service.getObject(StudentAccount.class, map);
            StudentAccount studentAccount = (StudentAccount) listOfStudentObj.get(0);
            if (studentAccount.getPassword().equals(oldPassword)) {
                studentAccount.setPassword(newPassword);
                int x = service.update(studentAccount);
                if (x > 0) {
                    /*sending email to the user*/
                    String subject1 = "YOLEARN - Password Updated";
                    String emailMsg1 = "Hi <b>" + studentAccount.getFirstName() + ",</b><br><br>"
                            + "Your password has been updated successfully!<br><br>"
                            + "<b>Login Details:</b><br>"
                            + "<table>"
                            + "<tr><td>Email</td><td> : " + studentAccount.getPrimaryEmail() + "</td></tr>"
                            + "<tr><td>Password</td><td> : " + studentAccount.getPassword() + "</td></tr>"
                            + "</table><br><br>"
                            + "<b>Yours Sincerely,</b><br>"
                            + "YOLEARN Team.<br><br>"
                            + "Thanks for choosing Yolearn";
                    Set<String> to1 = new HashSet<>();
                    to1.add(studentAccount.getPrimaryEmail());
                    javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg1);
                    json.put("msg", "Password updated!");
                } else {
                    json.put("msg", "Password not updated!");
                }
            } else {
                json.put("msg", "Wrong Password!");
            }
        } catch (Exception e) {
        }
        return json;
    }

    @RequestMapping(value = {"/changePassword"}, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    ResponseEntity<?> changePassword(@RequestBody final Map<String, String> bean) {

        ResponseEntity<?> responseEntity;

        String accountId = bean.get("accountId");
        String oldPassword = bean.get("oldPassword");
        String newPassword = bean.get("newPassword");
        String userRole = bean.get("userRole");

        /*creating Map<String, Object> map*/
        Map<String, Object> map = new HashMap<>();
        map.put("accountId", accountId);
        map.put("oldPassword", oldPassword);
        map.put("newPassword", newPassword);

        JSONObject json = null;
        switch (userRole) {

            case "Admin":
                json = changePassword_admin(map);
                break;

            case "teacher":
                json = changePassword_teacher(map);
                break;

            case "parent":
                json = changePassword_parent(map);
                break;

            case "student":
                json = changePassword_student(map);
                break;
        }

        responseEntity = new ResponseEntity<>(json, HttpStatus.OK);

        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/allTypeUsersListCount")
    public @ResponseBody
    JSONObject allTypeUsersListCount() {
        JSONObject json = new JSONObject();

        List<Object> listObject = service.getObject(TeacherAccount.class
        );
        int countTeacher = listObject.size();
        json.put("numberOfTeachers", countTeacher);

        List<Object> listObject1 = service.getObject(StudentAccount.class
        );
        int countStudent = listObject1.size();
        json.put("numberOfStudents", countStudent);

        List<Object> listObject2 = service.getObject(ParentAccount.class
        );
        int countParent = listObject2.size();
        json.put("numberOfParent", countParent);

        List<Object> listObject3 = service.getObject(Registration.class
        );
        int countUsers = listObject3.size();
        json.put("numbetOfUsers", countUsers);

        return json;
    }

    public boolean listOfImagesNameExistOrNot(String iname) {

        FTPServer fTPServer = new FTPServer();

        String ftpSourceFolder = "/UserImages/";

        List<String> listImageNames = fTPServer.listFTPFile(ftpSourceFolder);

        return listImageNames.contains(iname);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listOfTeachers")
    public @ResponseBody
    List<Object> listOfTeachers() {
        List<Object> list = new ArrayList<>();
        Map<String, Object> map = null;
//        Map<String, Object> map = new HashMap<>();
        /*getting object from lattest date to old-date order*/
//        ORDER BY c.scheduledDate ASC
        List<Object> object = service.getObject("FROM TeacherAccount c  ORDER BY c.dateOfCreation DESC ");

        for (Object objects : object) {
            map = new HashMap<>();
            TeacherAccount teacherAccount = (TeacherAccount) objects;
            map.put("accountId", teacherAccount.getTeacherAccountId());
            map.put("firstName", teacherAccount.getFirstName());
            map.put("lastName", teacherAccount.getLastName());
            map.put("fullName", teacherAccount.getFirstName() + " " + teacherAccount.getLastName());
            map.put("primaryEmail", teacherAccount.getPrimaryEmail());
            map.put("password", teacherAccount.getPassword());
            map.put("countryCode", teacherAccount.getCountryCode());
            map.put("mobileNum", teacherAccount.getMobileNum());
            map.put("dateOfCreation", MyDateFormate.dateToString(teacherAccount.getDateOfCreation()));
            map.put("address", teacherAccount.getAddress());
            map.put("city", teacherAccount.getCity());
            map.put("status", teacherAccount.getStatus());
            map.put("userRole", teacherAccount.getRegistration().getUserRole());

            if (listOfImagesNameExistOrNot(teacherAccount.getTeacherAccountId() + ".jpg")) {
                map.put("image", "/UserImages/" + teacherAccount.getTeacherAccountId() + ".jpg");
            } else {
                map.put("image", null);
            }
            list.add(map);
        }

        return list;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listOfTeachersSearchAndPagination")
    public @ResponseBody
    JSONObject listOfTeachers(@RequestBody final Map<String, Object> input) {
        List<Object> list = new ArrayList<>();
        JSONObject jSONObject = new JSONObject();
        Map<String, Object> map = null;

        String text = (String) input.get("text");
        String dateOrder = (String) input.get("dateOrder");
        int pageNo = (Integer) input.get("pageNo");
        int maxRes = 10;
        if (input.get("maxResult") != null) {
            maxRes = (Integer) input.get("maxResult");
        }

        String sqlQuery = "FROM TeacherAccount t";
        long teachesrTotalCount = this.service.countObject("select count(*) " + sqlQuery);

        if (text != null) {
            sqlQuery = String.format("%s", sqlQuery + " where (t.firstName LIKE '" + text + "%'"
                    + " or t.lastName LIKE '" + text + "%' or t.primaryEmail LIKE '" + text + "%' or t.mobileNum LIKE '" + text + "%'");
        }
        sqlQuery = sqlQuery + ") ORDER BY t.dateOfCreation " + dateOrder + "";

        long teachesrResultCount = this.service.countObject("select count(*) " + sqlQuery);

        List<Object> object = service.loadByLimit(sqlQuery, (pageNo * maxRes), maxRes);
        for (Object objects : object) {
            map = new HashMap<>();
            TeacherAccount teacherAccount = (TeacherAccount) objects;
            map.put("accountId", teacherAccount.getTeacherAccountId());
            map.put("firstName", teacherAccount.getFirstName());
            map.put("lastName", teacherAccount.getLastName());
            map.put("fullName", teacherAccount.getFirstName() + " " + teacherAccount.getLastName());
            map.put("primaryEmail", teacherAccount.getPrimaryEmail());
            map.put("password", teacherAccount.getPassword());
            map.put("countryCode", teacherAccount.getCountryCode());
            map.put("mobileNum", teacherAccount.getMobileNum());
            map.put("dateOfCreation", MyDateFormate.dateToString(teacherAccount.getDateOfCreation()));
            map.put("address", teacherAccount.getAddress());
            map.put("city", teacherAccount.getCity());
            map.put("status", teacherAccount.getStatus());
            map.put("userRole", teacherAccount.getRegistration().getUserRole());

            if (listOfImagesNameExistOrNot(teacherAccount.getTeacherAccountId() + ".jpg")) {
                map.put("image", "/UserImages/" + teacherAccount.getTeacherAccountId() + ".jpg");
            } else {
                map.put("image", null);
            }
            list.add(map);

        }
        jSONObject.put("count", teachesrTotalCount);
        jSONObject.put("no of records in this page", teachesrResultCount);
        jSONObject.put("teachersList", list);
        return jSONObject;
    }

    //    @RequestMapping(method = RequestMethod.GET, value = "/listOfTeachers")
//    public @ResponseBody
//    List<Object> listOfTeachers() {
//        System.out.println("calling list of teac");
//        List<Object> list = new ArrayList<>();
//
//        /*getting object from lattest date to old-date order*/
//        List<Object> listObject = service.getObjectsInDescOrder(TeacherAccount.class, "dateOfCreation");
//        for (Object object : listObject) {
//            JSONObject json = new JSONObject();
//            TeacherAccount teacherAccount = (TeacherAccount) object;
//            json.put("accountId", teacherAccount.getTeacherAccountId());
//            json.put("firstName", teacherAccount.getFirstName());
//            json.put("lastName", teacherAccount.getLastName());
//            json.put("fullName", teacherAccount.getFirstName() + " " + teacherAccount.getLastName());
//            json.put("primaryEmail", teacherAccount.getPrimaryEmail());
//            json.put("password", teacherAccount.getPassword());
//            json.put("countryCode", teacherAccount.getCountryCode());
//            json.put("mobileNum", teacherAccount.getMobileNum());
//            json.put("dateOfCreation", MyDateFormate.dateToString(teacherAccount.getDateOfCreation()));
//            json.put("address", teacherAccount.getAddress());
//            json.put("city", teacherAccount.getCity());
//            json.put("status", teacherAccount.getStatus());
//            json.put("userRole", teacherAccount.getRegistration().getUserRole());
//
//            if (listOfImagesNameExistOrNot(teacherAccount.getTeacherAccountId() + ".jpg")) {
//                json.put("image", "/UserImages/" + teacherAccount.getTeacherAccountId() + ".jpg");
//            } else {
//                json.put("image", null);
//            }
//
//            list.add(json);
//        }
//
//        return list;
//    }
    @RequestMapping(method = RequestMethod.POST, value = "/deleteTeacher")
    public @ResponseBody
    JSONObject deleteTeacher(@RequestBody final UsersMetaData bean) {

        JSONObject json = new JSONObject();

        String teacherAccountId = bean.getTeacherAccountId();

        try {
            Map<String, Object> map0 = new HashMap<>();
            map0.put("teacherAccountId", teacherAccountId);
            List<Object> listTeacherObj = service.getObject(TeacherAccount.class, map0);

            TeacherAccount teacherAccount = (TeacherAccount) listTeacherObj.get(0);
            String accountId = teacherAccount.getRegistration().getAccountId();

            map0 = new HashMap<>();
            map0.put("accountId", accountId);

            /*DELETING TEACHER'S ACCOUNT*/
            int del = service.delete(Registration.class, map0);
            if (del > 0) {

//                /*DELETING CLASS-SCHEDULER RECORD FOR THIS TEACHER*/
//                Map<String, Object> map1 = new HashMap<>();
//                map1.put("teacherId", teacherAccountId);
//                service.delete(ClassScheduler.class, map1);

                /*DELETING TEACHER'S HISTORY*/
                Map<String, Object> map2 = new HashMap<>();
                map2.put("accountId", teacherAccountId);
                service.delete(UserHistory.class, map2);

                /*DELETING TEACHER'S PROFILE PIC*/
                String fileName = "/UserImages/" + teacherAccountId + ".jpg";

                new FTPServer().deleteFTPFile(fileName);

                json.put("message", "Teacher has been deleted!");
            } else {
                json.put("message", "Something went wrong. Try Again!");
            }

        } catch (Exception e) {
        }

        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listOfStudents")
    public @ResponseBody
    JSONObject listOfStudents(@RequestBody final Map<String, Object> filter) {

        JSONObject response = new JSONObject();
        List<Object> studentsDetails = new ArrayList<>();

        List<String> gradeFilters = (List<String>) (Object) filter.get("gradeFilter");
        String dateOrder = (String) filter.get("dateOrder");
        List<String> programFilters = (List<String>) (Object) filter.get("ProgramFilter");
        String text = (String) filter.get("text");
        String gradeOrder = (String) filter.get("gradeOrder");

        /*fixed filter-variables*/
        int pageNo = (Integer) filter.get("pageNo");
        int maxRes = 10;
        if (filter.get("maxResult") != null) {
            maxRes = (Integer) filter.get("maxResult");
        }

        /*optional filter-variables*/
        Map<String, Object> dateRange = (Map<String, Object>) filter.get("dateRange");

        /*getting sql queries for 'count of students' and 'list of students'*/
        Map<String, String> mapFilterSQL = new FilterUtility().listOfStudentsFilter(gradeFilters, dateRange, dateOrder, text, programFilters, gradeOrder);

        /*getting total count of students*/
        int count = (int) service.countObject(String.format("%s", mapFilterSQL.get("SQLcountStudentAccount")));

        /*getting object from lattest date to old-date order*/
        List<Object> listObject = service.loadByLimit(String.format("%s", mapFilterSQL.get("SQLlistStudentAccount")), (pageNo * maxRes), maxRes);

        long noOfRecordsRetrieved = this.service.countObject("select count(*) " + mapFilterSQL.get("SQLlistStudentAccount"));
        System.out.println("noOfRecordsRetrieved " + noOfRecordsRetrieved);

        for (Object object : listObject) {
            JSONObject json = new JSONObject();
            StudentAccount studentAccount = (StudentAccount) object;

            json.put("userRole", "student");
            json.put("firstName", studentAccount.getFirstName());
            json.put("lastName", studentAccount.getLastName());
            json.put("fullName", studentAccount.getFirstName() + " " + studentAccount.getLastName());

            json.put("primaryEmail", studentAccount.getPrimaryEmail());
            json.put("password", studentAccount.getPassword());
            json.put("countryCode", studentAccount.getCountryCode());
            json.put("accountId", studentAccount.getStudentAccountId());
            json.put("status", studentAccount.getStatus());
            json.put("dateOfCreation", MyDateFormate.dateToString(studentAccount.getDateOfCreation()));
            json.put("dateOfCreationFormat", studentAccount.getDateOfCreation());
            json.put("schoolName", studentAccount.getSchoolName());

            json.put("gradeId", studentAccount.getGradeId());
            json.put("syllabusId", studentAccount.getSyllabusId());
            json.put("address", studentAccount.getAddress());
            json.put("city", studentAccount.getCity());
            json.put("subsTypeId", studentAccount.getSubscribeId());
            json.put("batchId", studentAccount.getBatchId());

            Object[] loadParentProperties = service.loadProperties(String.format("%s", "SELECT p.firstName, p.lastName, p.mobileNum, g.gradeName, s.syllabusName FROM "
                    + "ParentAccount p, Grade g, Syllabus s WHERE "
                    + "p.registration.accountId = '" + studentAccount.getParentAccountId() + "' AND "
                    + "g.gradeId = '" + studentAccount.getGradeId() + "' AND "
                    + "s.syllabusId = '" + studentAccount.getSyllabusId() + "'")).get(0);
            String parentFirstName = (String) loadParentProperties[0];
            String parentLastName = (String) loadParentProperties[1];
            long mobileNumber = (Long) loadParentProperties[2];
            String gradeName = (String) loadParentProperties[3];
            String syllabusName = (String) loadParentProperties[4];

            json.put("parentFirstName", parentFirstName);
            json.put("parentLastName", parentLastName);
            json.put("parentFullName", String.format("%s", parentFirstName + " " + parentLastName));
            json.put("mobileNumber", mobileNumber);
            json.put("gradeName", gradeName);
            json.put("syllabusName", syllabusName);

            if (listOfImagesNameExistOrNot(studentAccount.getStudentAccountId() + ".jpg")) {
                json.put("image", "/UserImages/" + studentAccount.getStudentAccountId() + ".jpg");
            } else {
                json.put("image", null);
            }
            studentsDetails.add(json);
        }
        response.put("no of records displaying in this page", noOfRecordsRetrieved);
        response.put("count", count);
        response.put("listOfStudents", studentsDetails);

        return response;
    }

    @RequestMapping(value = {"/deleteStudentsWithNoSubscritionPackage"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> deleteStudentsWithNoSubscritionPackage(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        String parentRegAccountId = mapBean.get("accountId");
        try {
            int x = service.delete(String.format("%s", "DELETE FROM StudentAccount s WHERE s.parentAccountId = '" + parentRegAccountId + "' AND s.subscribeId IS NULL"));
            if (x > 0) {
                json.put("msg", "Unsubscribed students deleted!");
            }
        } catch (Exception e) {
            json.put("msg", "Something went wrong!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    //    @RequestMapping(value = "/getAllStudentsOfParentMulSubscription", method = RequestMethod.POST)
//    public @ResponseBody
//    List<Object> getAllStudentsOfParentMulSubscription(@RequestBody
//            final Map<String, String> map) {
//        List<Object> list = new ArrayList<>();
//        String parentEmail = map.get("primaryEmail");
//        try {
//            List<ParentAccount> listParentObj = (List<ParentAccount>) (Object) service.getObject("FROM ParentAccount p WHERE p.primaryEmail = '" + parentEmail + "'");
//            if (listParentObj.size() > 0) {
//                ParentAccount parentAccount = listParentObj.get(0);
//                JSONObject parentJson = new JSONObject();
//                parentJson.put("userRole", "parent");
//                parentJson.put("firstName", parentAccount.getFirstName());
//                parentJson.put("lastName", parentAccount.getLastName());
//                parentJson.put("primaryEmail", parentAccount.getPrimaryEmail());
//                parentJson.put("password", parentAccount.getPassword());
//                parentJson.put("countryCode", parentAccount.getCountryCode());
//                parentJson.put("mobileNum", parentAccount.getMobileNum());
//                parentJson.put("pAccountId", parentAccount.getParentAccountId());
//                parentJson.put("dateOfCreation", MyDateFormate.dateToString(parentAccount.getDateOfCreation()));
//                parentJson.put("address", parentAccount.getAddress());
//                parentJson.put("city", parentAccount.getCity());
//                list.add(parentJson);
//                String sql = "SELECT GRADE_ID,STUDENT_ID,SUBSCRIPTION_ID,SYLLABUS_ID,BATCH_ID from student_subscriptions where STUDENT_ID in(SELECT ALLOTED_TO FROM `PAYMENT_CHECKOUT` where ACCOUNT_ID='" + parentAccount.getRegistration().getAccountId() + "' and ALLOTED_TO is not null)";
//                List<Object> studentSubscriptions = this.service.getObjectsByNativeSqlQuery(sql);
//                String gradeID;
//                String studentID;
//                String subid;
//                String syllabusID;
//                String batchID;
//                for (Object studentSubscription : studentSubscriptions) {
//                    Object[] objects = (Object[]) studentSubscription;
//                    gradeID = (String) objects[0];
//                    studentID = (String) objects[1];
//                    subid = (String) objects[2];
//                    syllabusID = (String) objects[3];
//                    batchID = (String) objects[4];
////                    String sql2 = "FROM StudentAccount s WHERE s.parentAccountId = '" + parentAccount.getRegistration().getAccountId() + "'";
//                    String sql2 = "FROM StudentAccount s WHERE s.studentAccountId = '" + studentID + "'";
//                    Object object = this.service.getObject(sql2).get(0);
//                    StudentAccount studentAccount = (StudentAccount) object;
////                List<StudentAccount> listStudentObject = (List<StudentAccount>) (Object) service.getObject("FROM StudentAccount s WHERE s.parentAccountId = '" + parentAccount.getRegistration().getAccountId() + "'");
////                for (StudentAccount studentAccount : listStudentObject) {
//                    JSONObject studentJson = new JSONObject();
////                    String gradeId = studentAccount.getGradeId();
//                    List<PaymentCheckout> liPaymentCheckoutObj = (List<PaymentCheckout>) (Object) service.getObject("FROM PaymentCheckout p WHERE p.allotedStudentAccountId = '" + studentID + "'");
//
//                    if (liPaymentCheckoutObj.size() > 0) {
//                        /*CHECK IF PACKAGE IS EXPIRED:*/
//                        PaymentCheckout paymentCheckout = liPaymentCheckoutObj.get(0);
//
//                        Date date1 = paymentCheckout.getValidTill();
//                        Date d1 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date1));//d2::: Thu Jan 24 00:00:00 IST 2019
//
//                        Timestamp date2 = new Timestamp(System.currentTimeMillis());
//                        Date d2 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date2));//d::: Thu Jan 24 00:00:00 IST 2019
//
//                        long diffDate = d1.getTime() - d2.getTime();
//
//                        if (diffDate >= 0) {
//                            studentJson = getStudentJson(studentAccount, gradeID);
////                            studentJson.put("validTill", paymentCheckout.getValidTill().getTime());
//                            studentJson.put("validFrom", paymentCheckout.getValidFrom().getTime());
//                            System.out.println("valid from paymentche  "+paymentCheckout.getValidFrom());
//                            studentJson.put("syllabusId", syllabusID);
//                            studentJson.put("subsTypeId", subid);
//                            studentJson.put("gradeId", gradeID);
//                            studentJson.put("batchId", batchID);
//                            String syllabusName = getSyllabusName(syllabusID);
//                            studentJson.put("syllabusName", syllabusName);
//
//                            long diff = paymentCheckout.getValidTill().getTime() - paymentCheckout.getValidFrom().getTime();
//                            long days = diff / (24 * 60 * 60 * 1000);
//                            studentJson.put("noOfDaysLeft", days);
//
//                        } else {
//                            studentJson = getStudentJson(studentAccount, gradeID);
//
//                            studentJson.put("msg", studentID + ": subscription expired.");
//                        }
//
//
//                        /*GETTING PRODUCT NAME*/
////                        String subscriptionName = (String) service.getObject(String.format("%s", "SELECT s.subscriptionName FROM SubscribeType s WHERE s.subsTypeId = '" + subid + "'")).get(0);
//                        Object[] get = (Object[]) service.getObject(String.format("%s", "SELECT s.subscriptionName,s.validTill,s.validFrom FROM SubscribeType s WHERE s.subsTypeId = '" + subid + "'")).get(0);
//                        studentJson.put("productName", get[0]);
//                        studentJson.put("validTill", get[1]);
//                        System.out.println("validfrom subscribe  "+get[2]);
//                        
//                        
//                        
//                        
//                        
//                        
//                        
//
//                    } else {
//                        studentJson = getStudentJson(studentAccount, gradeID);
//
//                        studentJson.put("msg", studentID + ": not subscribed to any package.");
//                    }
//                    list.add(studentJson);
//
//                }
//            } else {
//                JSONObject json = new JSONObject();
//                json.put("msg", "No parent found!");
//                list.add(json);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//    =====================================================================
    @RequestMapping(value = "/getAllStudentsOfParentMulSubscription", method = RequestMethod.POST)
    public @ResponseBody
    List<Object> getAllStudentsOfParentMulSubscription(@RequestBody final Map<String, String> map) {
        List<Object> list = new ArrayList<>();
        String parentEmail = map.get("primaryEmail");
        try {
            List<ParentAccount> listParentObj = (List<ParentAccount>) (Object) service.getObject("FROM ParentAccount p WHERE p.primaryEmail = '" + parentEmail + "'");
            if (listParentObj.size() > 0) {
                ParentAccount parentAccount = listParentObj.get(0);
                JSONObject parentJson = new JSONObject();
                parentJson.put("userRole", "parent");
                parentJson.put("firstName", parentAccount.getFirstName());
                parentJson.put("lastName", parentAccount.getLastName());
                parentJson.put("primaryEmail", parentAccount.getPrimaryEmail());
                parentJson.put("password", parentAccount.getPassword());
                parentJson.put("countryCode", parentAccount.getCountryCode());
                parentJson.put("mobileNum", parentAccount.getMobileNum());
                parentJson.put("pAccountId", parentAccount.getParentAccountId());
                parentJson.put("dateOfCreation", MyDateFormate.dateToString(parentAccount.getDateOfCreation()));
                parentJson.put("address", parentAccount.getAddress());
                parentJson.put("city", parentAccount.getCity());
                list.add(parentJson);
//                String sql = "SELECT GRADE_ID,STUDENT_ID,SUBSCRIPTION_ID,SYLLABUS_ID,BATCH_ID from student_subscriptions where STUDENT_ID in"
//                        + "(SELECT ALLOTED_TO FROM `PAYMENT_CHECKOUT` where ACCOUNT_ID='" + parentAccount.getRegistration().getAccountId() + "' and ALLOTED_TO is not null)";
                String sql = "SELECT GRADE_ID,STUDENT_ID,SUBSCRIPTION_ID,SYLLABUS_ID,BATCH_ID from student_subscriptions where STUDENT_ID in"
                        + "(SELECT STUDENT_ID FROM `STUDENT_ACCOUNT` where PARENT_ID='" + parentAccount.getRegistration().getAccountId() + "')";
                List<Object> studentSubscriptions = this.service.getObjectsByNativeSqlQuery(sql);
                String gradeID;
                String studentID;
                String subid;
                String syllabusID;
                String batchID;
                for (Object studentSubscription : studentSubscriptions) {
                    Object[] objects = (Object[]) studentSubscription;
                    gradeID = (String) objects[0];
                    studentID = (String) objects[1];
                    subid = (String) objects[2];
                    syllabusID = (String) objects[3];
                    batchID = (String) objects[4];
                    String sql2 = "FROM StudentAccount s WHERE s.studentAccountId = '" + studentID + "'";
                    Object object = this.service.getObject(sql2).get(0);
                    StudentAccount studentAccount = (StudentAccount) object;

                    JSONObject studentJson = new JSONObject();

                    List<Object> object1 = this.service.getObject("FROM SubscribeType s WHERE s.subsTypeId = '" + subid + "'");
                    if (object1.size() > 0) {
                        /*CHECK IF PACKAGE IS EXPIRED:*/
                        SubscribeType subscribeType = (SubscribeType) object1.get(0);

                        Date date1 = subscribeType.getValidTill();
                        Date d1 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date1));//d2::: Thu Jan 24 00:00:00 IST 2019

                        Timestamp date2 = new Timestamp(System.currentTimeMillis());
                        Date d2 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date2));//d::: Thu Jan 24 00:00:00 IST 2019

                        long diffDate = d1.getTime() - d2.getTime();

//                        if (diffDate >= 0) {
                        studentJson = getStudentJson(studentAccount, gradeID);
//                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("mm-dd-yyyy");
//                            simpleDateFormat.format(subscribeType.getValidFrom());
//                            studentJson.put("validFrom", simpleDateFormat.format(subscribeType.getValidFrom()));
                        studentJson.put("validFrom", subscribeType.getValidFrom());
                        studentJson.put("syllabusId", syllabusID);
                        studentJson.put("subsTypeId", subid);
                        studentJson.put("gradeId", gradeID);
                        studentJson.put("batchId", batchID);
                        String syllabusName = getSyllabusName(syllabusID);

                        if (diffDate >= 0) {
                            studentJson.put("syllabusName", syllabusName);

                        } else {
                            studentJson.put("syllabusName", syllabusName + ": subscription expired.");
                        }
                        studentJson.put("productName", subscribeType.getSubscriptionName());
                        studentJson.put("validTill", subscribeType.getValidTill());

//                            studentJson.put("validTill", simpleDateFormat.format(subscribeType.getValidTill()));
                        long diff = subscribeType.getValidTill().getTime() - subscribeType.getValidFrom().getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        studentJson.put("noOfDaysLeft", days);

//                        } else {
//                            studentJson = getStudentJson(studentAccount, gradeID);
//
//                            studentJson.put("msg", studentID + ": subscription expired.");
//                        }
                    } else {
                        studentJson = getStudentJson(studentAccount, gradeID);

                        studentJson.put("msg", studentID + ": not subscribed to any package.");
                    }
                    list.add(studentJson);

                }
            } else {
                JSONObject json = new JSONObject();
                json.put("msg", "No parent found!");
                list.add(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    //    =====================================================================
    @RequestMapping(value = "/getAllStudentsOfParent", method = RequestMethod.POST)
    public @ResponseBody
    List<Object> getAllStudentsOfParentByParentMail(@RequestBody final Map<String, String> map) {

        List<Object> list = new ArrayList<>();

        String parentEmail = map.get("primaryEmail");
        try {
            List<ParentAccount> listParentObj = (List<ParentAccount>) (Object) service.getObject("FROM ParentAccount p WHERE p.primaryEmail = '" + parentEmail + "'");

            if (listParentObj.size() > 0) {
                ParentAccount parentAccount = listParentObj.get(0);
                JSONObject parentJson = new JSONObject();
                parentJson.put("userRole", "parent");
                parentJson.put("firstName", parentAccount.getFirstName());
                parentJson.put("lastName", parentAccount.getLastName());
                parentJson.put("primaryEmail", parentAccount.getPrimaryEmail());
                parentJson.put("password", parentAccount.getPassword());
                parentJson.put("countryCode", parentAccount.getCountryCode());
                parentJson.put("mobileNum", parentAccount.getMobileNum());
                parentJson.put("pAccountId", parentAccount.getParentAccountId());
                parentJson.put("dateOfCreation", MyDateFormate.dateToString(parentAccount.getDateOfCreation()));
                parentJson.put("address", parentAccount.getAddress());
                parentJson.put("city", parentAccount.getCity());

                list.add(parentJson);

                List<StudentAccount> listStudentObject = (List<StudentAccount>) (Object) service.getObject("FROM StudentAccount s WHERE s.parentAccountId = '" + parentAccount.getRegistration().getAccountId() + "'");
                for (StudentAccount studentAccount : listStudentObject) {
                    JSONObject studentJson;
                    String gradeId = studentAccount.getGradeId();

                    List<PaymentCheckout> liPaymentCheckoutObj = (List<PaymentCheckout>) (Object) service.getObject("FROM PaymentCheckout p WHERE p.allotedStudentAccountId = '" + studentAccount.getStudentAccountId() + "'");

                    if (liPaymentCheckoutObj.size() > 0) {
                        /*CHECK IF PACKAGE IS EXPIRED:*/
                        PaymentCheckout paymentCheckout = liPaymentCheckoutObj.get(0);

                        Date date1 = paymentCheckout.getValidTill();
                        Date d1 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date1));//d2::: Thu Jan 24 00:00:00 IST 2019

                        Timestamp date2 = new Timestamp(System.currentTimeMillis());
                        Date d2 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date2));//d::: Thu Jan 24 00:00:00 IST 2019

                        long diffDate = d1.getTime() - d2.getTime();

                        if (diffDate >= 0) {
                            studentJson = getStudentJson(studentAccount, gradeId);
                            studentJson.put("validTill", paymentCheckout.getValidTill().getTime());
                            studentJson.put("validFrom", paymentCheckout.getValidFrom().getTime());

                            long diff = paymentCheckout.getValidTill().getTime() - paymentCheckout.getValidFrom().getTime();
                            long days = diff / (24 * 60 * 60 * 1000);
                            studentJson.put("noOfDaysLeft", days);

                        } else {
                            studentJson = getStudentJson(studentAccount, gradeId);

                            studentJson.put("msg", studentAccount.getStudentAccountId() + ": subscription expired.");
                        }

                        /*GETTING PRODUCT NAME*/
                        String subscriptionName = (String) service.getObject(String.format("%s", "SELECT s.subscriptionName FROM SubscribeType s WHERE s.subsTypeId = '" + paymentCheckout.getSubsctypeId() + "'")).get(0);
                        studentJson.put("productName", subscriptionName);
                    } else {
                        studentJson = getStudentJson(studentAccount, gradeId);

                        studentJson.put("msg", studentAccount.getStudentAccountId() + ": not subscribed to any package.");
                    }
                    list.add(studentJson);
                }
            } else {
                JSONObject json = new JSONObject();
                json.put("msg", "No parent found!");
                list.add(json);
            }
        } catch (Exception e) {
        }

        return list;
    }

    public JSONObject getStudentJson(StudentAccount studentAccount, String gradeId) {
        JSONObject json = new JSONObject();

        String gradeName = getGradeName(gradeId);

        json.put("gradeName", gradeName);

//        json.put("syllabusName", syllabusName);
        if (listOfImagesNameExistOrNot(studentAccount.getStudentAccountId() + ".jpg")) {
            json.put("image", "/UserImages/" + studentAccount.getStudentAccountId() + ".jpg");
        } else {
            json.put("image", null);
        }
        json.put("firstName", studentAccount.getFirstName());
        json.put("lastName", studentAccount.getLastName());
//        json.put("firstName", studentAccount.getFirstName());
        json.put("primaryEmail", studentAccount.getPrimaryEmail());
        json.put("password", studentAccount.getPassword());
        json.put("countryCode", studentAccount.getCountryCode());
        json.put("sAccountId", studentAccount.getStudentAccountId());
        json.put("status", studentAccount.getStatus());
        json.put("dateOfCreation", MyDateFormate.dateToString(studentAccount.getDateOfCreation()));
        json.put("schoolName", studentAccount.getSchoolName());
//        json.put("gradeId", studentAccount.getGradeId());
//        json.put("syllabusId", studentAccount.getSyllabusId());
//        json.put("batchId", studentAccount.getBatchId());
        json.put("address", studentAccount.getAddress());
        json.put("city", studentAccount.getCity());
//        json.put("subsTypeId", studentAccount.getSubscribeId());
        json.put("userRole", "student");
        return json;
    }

    @RequestMapping(value = {"/deleteStudent"}, method = RequestMethod.POST)
    public @ResponseBody
    JSONObject deleteStudent(@RequestBody final Map<String, String> map) {

        JSONObject json = new JSONObject();

        Map<String, Object> map1 = new HashMap<>();
        map1.put("studentAccountId", map.get("accountId"));

        try {

            if (service.delete(StudentAccount.class, map1) > 0) {

                String fileName = "/UserImages/" + map.get("accountId") + ".jpg";

                new FTPServer().deleteFTPFile(fileName);

                json.put("msg", "Student Deleted Successfully!");

            } else {

                json.put("msg", "Something went wrong. Try Again!");

            }

        } catch (Exception e) {
            json.put("msg", "Something went wrong. Try Again!");
        }

        return json;
    }

    @RequestMapping(value = {"/getUsersDetailsByEmail"}, method = RequestMethod.POST)
    public @ResponseBody
    JSONObject getUsersDetailsByEmail(@RequestBody final Map<String, String> map) {
        JSONObject json = new JSONObject();

        String primaryEmail = map.get("primaryEmail");

        Map<String, Object> emailMap = new HashMap<>();
        emailMap.put("primaryEmail", primaryEmail);

        try {
            List<Object> listRegObj = service.getObject(Registration.class, emailMap);

            if (listRegObj.size() > 0) {

                Registration registration = (Registration) listRegObj.get(0);
                json = getRegistrationJSON(registration);

                if (registration.getUserRole().equals("teacher")) {
                    TeacherAccount teacherAccount = (TeacherAccount) service.getObject(TeacherAccount.class, emailMap).get(0);
                    json.put("teacherAccountId", teacherAccount.getTeacherAccountId());
                } else if (registration.getUserRole().equals("parent")) {
                    /*delete the students of this parent if that student is not having subsTypeId*/
                    service.delete(String.format("%s", "DELETE FROM StudentAccount s WHERE s.parentAccountId = '" + registration.getAccountId() + "' AND s.subscribeId IS NULL"));
                }

            } else {
                json = new JSONObject();
                json.put("msg", "No user found with this email.");
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return json;
    }

    //    http://localhost:8989/yolearn/users/uploadUserImage/STUDENT000001
    @RequestMapping(value = "/uploadUserImage/{uid}", method = RequestMethod.POST)
    public JSONObject uploadUserImage(@PathVariable("uid") final String uid, @RequestParam("file") MultipartFile file) throws IOException {

        /*first delete the image if exists*/
        new FTPServer().deleteFTPFile("/UserImages/" + uid + ".jpg");
//        new FTPServer().deleteFTPFile("/adasysImages/" + uid + ".jpg");

        InputStream inputStream = file.getInputStream();

        String fname = file.getOriginalFilename();

        /*it will split the original file name to BASE NAME & EXTENSION */
        String[] tokens = fname.split("\\.(?=[^\\.]+$)");

        String fileName = uid + "." + tokens[1];

        String destPath = "/UserImages/";

        boolean b = new FTPServer().uploadFile(inputStream, destPath, fileName);

        JSONObject json = new JSONObject();

        if (b) {
            json.put("msg", "Your Profile Image has been uploaded!");
        } else {
            json.put("msg", "Something went wrong. Try Again!");
        }

        return json;
    }

    @RequestMapping(value = {"/deleteUserImage"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> deleteUserImage(@RequestBody final Map<String, String> mapBean) {

        JSONObject json = new JSONObject();

        String accountId = mapBean.get("accountId");

        String ftpPath = "/UserImages/";

        /*CHECK IF THE FILE IS AVAILABLE. IF AVAILABLE THEN DELETE*/
        if (new FTPServer().isFTPFileAvailable(ftpPath, accountId + ".jpg")) {

            String fileName = "/UserImages/" + accountId + ".jpg";

            int x = new FTPServer().deleteFTPFile(fileName);
            if (x == 250) {
                json.put("msg", "Profile Picture deleted successfully!");
            } else {
                json.put("msg", "Profile Picture not deleted!");
            }

        } else {
            json.put("msg", "Profile Picture is not available!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getDateWiseGuest"}, method = RequestMethod.GET)
    public @ResponseBody
    List<Object> getCurrentDateGuest() {

        List<Object> listJson = new ArrayList<>();

        /*getting list of objects from database*/
        List<Object> listGuestObj = service.getObject(Guest.class);

        for (int i = 0; i <= 14; i++) {

            /*getting all dates from today to 14-days before*/
            LocalDate localDate = LocalDate.now().minusDays(i);
            java.util.Date date = java.sql.Date.valueOf(localDate);

            String dateInString = MyDateFormate.dateToString(date);

            /*getting count of users from date*/
            int countUser = 0;
            for (Object object : listGuestObj) {
                Guest guest = (Guest) object;
                Date dateOfCreation = guest.getDateOfCreation();
                String dateOfCreationStr = MyDateFormate.dateToString(dateOfCreation);

                /*increasing the countUser by when dateInString mathches to dateOfCreationStr*/
                if (dateInString.equals(dateOfCreationStr)) {
                    countUser += 1;
                }
            }

            /*setting countUser and date to JSONObject*/
            JSONObject json = new JSONObject();
            json.put("countUser", countUser);
            json.put("date", dateInString);

            /*adding json to list*/
            listJson.add(json);
        }

        return listJson;
    }

    @RequestMapping(value = {"/getDateWiseStudent"}, method = RequestMethod.GET)
    public @ResponseBody
    List<Object> getCurrentDateStudent() {

        List<Object> listJson = new ArrayList<>();

        /*getting list of objects from database*/
        List<Object> listStudentObj = service.getObject(StudentAccount.class);

        for (int i = 0; i <= 14; i++) {

            /*getting all dates from today to 14-days before*/
            LocalDate localDate = LocalDate.now().minusDays(i);
            java.util.Date date = java.sql.Date.valueOf(localDate);

            String dateInString = MyDateFormate.dateToString(date);

            /*getting count of users from date*/
            int countUser = 0;
            for (Object object : listStudentObj) {
                StudentAccount studentAccount = (StudentAccount) object;
                Date dateOfCreation = studentAccount.getDateOfCreation();
                String dateOfCreationStr = MyDateFormate.dateToString(dateOfCreation);

                /*increasing the countUser by when dateInString mathches to dateOfCreationStr*/
                if (dateInString.equals(dateOfCreationStr)) {
                    countUser += 1;
                }
            }

            /*setting countUser and date to JSONObject*/
            JSONObject json = new JSONObject();
            json.put("countUser", countUser);
            json.put("date", dateInString);

            /*adding json to list*/
            listJson.add(json);

        }

        return listJson;
    }

    @RequestMapping(value = {"/getDateWiseParent"}, method = RequestMethod.GET)
    public @ResponseBody
    List<Object> getCurrentDateParent() {

        List<Object> listJson = new ArrayList<>();

        /*getting list of objects from database*/
        List<Object> listParentObj = service.getObject(ParentAccount.class);

        for (int i = 0; i <= 14; i++) {

            /*getting all dates from today to 14-days before*/
            LocalDate localDate = LocalDate.now().minusDays(i);
            java.util.Date d = java.sql.Date.valueOf(localDate);

            String dateInString = MyDateFormate.dateToString(d);

            /*getting count of users from date*/
            int countUser = 0;
            for (Object object : listParentObj) {
                ParentAccount parentAccount = (ParentAccount) object;
                Date dateOfCreation = parentAccount.getDateOfCreation();
                String dateOfCreationStr = MyDateFormate.dateToString(dateOfCreation);

                /*increasing the countUser by when dateInString mathches to dateOfCreationStr*/
                if (dateInString.equals(dateOfCreationStr)) {
                    countUser += 1;
                }
            }

            /*setting countUser and date to JSONObject*/
            JSONObject json = new JSONObject();
            json.put("countUser", countUser);
            json.put("date", dateInString);

            /*adding json to list*/
            listJson.add(json);

        }

        return listJson;
    }

    @RequestMapping(value = "/saveTestAndAssignmentMetaData", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> saveTestAndAssignmentMetaData(@RequestBody final Map<String, String> mapBean) {

        Timestamp date = new Timestamp(System.currentTimeMillis());

        String title = mapBean.get("title");
        String assignmentFile = mapBean.get("assignmentFile");
        String testFile = mapBean.get("testFile");
        String gradeId = mapBean.get("gradeId");
        String subjectId = mapBean.get("subjectId");
        String syllabusId = mapBean.get("syllabusId");
        String accessTo = mapBean.get("accessTo");

        TestAndAssignments testAndAssignments = new TestAndAssignments();
        String fileId = (String) pKGenerator.generate(TestAndAssignments.class, "FILE");

        testAndAssignments.setFileId(fileId);
        testAndAssignments.setTitle(title);
        testAndAssignments.setAssignmentFile(assignmentFile);
        testAndAssignments.setTestFile(testFile);
        testAndAssignments.setGradeId(gradeId);
        testAndAssignments.setSubjectId(subjectId);
        testAndAssignments.setSyllabusId(syllabusId);
        testAndAssignments.setAccessTo(accessTo);
        testAndAssignments.setDateOfCreation(date);

        int x = service.save(testAndAssignments);
        JSONObject json = new JSONObject();
        if (x > 0) {
            json.put("msg", "File Saved Successfully!");
        } else {
            json.put("msg", "Oops! File Not Saved!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listParents")
    public @ResponseBody
    ResponseEntity<?> listParents(@RequestBody final Map<String, String> mapBean) {
        int pageNo = Integer.parseInt(mapBean.get("pageNo"));

        int maxResult = 10;
        if (mapBean.get("maxResult") != null) {
            maxResult = Integer.parseInt(mapBean.get("maxResult"));
        }
        String dateOrder = mapBean.get("dateOrder");
        String text = mapBean.get("text");

        int offset = (pageNo * maxResult);
//        int offset = (pageNo * maxResult) - maxResult;
        int limit = maxResult;
        long noOfRecordsCount = 0;
        JSONObject json;
        ArrayList<Object> list = new ArrayList<>();
        int count = 0;
        try {
            count = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM ParentAccount"));

//            List<Object> objects = service.loadByLimit(String.format("%s", "FROM ParentAccount p ORDER BY p.dateOfCreation DESC"), 
//                    (pageNo * maxResult), maxResult);
            String sqlQuery = "FROM ParentAccount p";

            if (text != null) {
                sqlQuery = sqlQuery + " where p.primaryEmail LIKE '" + text + "%' or p.firstName LIKE '" + text + "%'";
            }
            noOfRecordsCount = noOfRecordsCount + this.service.countObject("select count (*) " + sqlQuery);

            sqlQuery = sqlQuery + " ORDER BY p.dateOfCreation " + dateOrder + "";

            List<Object> objects = this.service.loadByLimit(sqlQuery, offset, limit);
            System.out.println("size " + objects.size());
            for (Object object : objects) {
                ParentAccount parentAccount = (ParentAccount) object;
                json = new JSONObject();
                json.put("address", parentAccount.getAddress());
                json.put("city", parentAccount.getCity());
                json.put("countryCode", parentAccount.getCountryCode());
                json.put("dateOfCreation", MyDateFormate.dateToString(parentAccount.getDateOfCreation()));
                json.put("firstName", parentAccount.getFirstName());
                json.put("lastName", parentAccount.getLastName());
                json.put("fullName", parentAccount.getFirstName() + " " + parentAccount.getLastName());
                json.put("mobileNum", parentAccount.getMobileNum());
                json.put("accountId", parentAccount.getParentAccountId());
                json.put("mainAccountId", parentAccount.getRegistration().getAccountId());
                json.put("password", parentAccount.getPassword());
                json.put("primaryEmail", parentAccount.getPrimaryEmail());
                json.put("userRole", parentAccount.getRegistration().getUserRole());

                if (listOfImagesNameExistOrNot(parentAccount.getRegistration().getAccountId() + ".jpg")) {
                    json.put("image", "/UserImages/" + parentAccount.getRegistration().getAccountId() + ".jpg");
                } else {
                    json.put("image", null);
                }

                list.add(json);
            }

        } catch (Exception e) {
        }

        json = new JSONObject();
        json.put("count", count);
        json.put("listOfParents", list);
        json.put("no of records desplaying on this page", noOfRecordsCount);

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getStudentsByGradeId"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getStudentsByGradeId(@RequestBody Map<String, String> mapBean) {
        String gradeId = mapBean.get("gradeId");

        List<Object> list = new ArrayList<>();
        JSONObject json;

        Map<String, Object> map = new HashMap<>();
        map.put("gradeId", gradeId);

        try {
            List<Object> listStudentObject = service.getObject(StudentAccount.class, map);
            if (listStudentObject.size() > 0) {
                for (Object object : listStudentObject) {
                    StudentAccount studentAccount = (StudentAccount) object;

                    /*Grade Name*/
                    Map<String, Object> mapGrade = new HashMap<>();
                    mapGrade.put("gradeId", studentAccount.getGradeId());
                    List<Object> listGradeObj = service.getObject(Grade.class, mapGrade);
                    Grade g = (Grade) listGradeObj.get(0);

                    /*List of sessionIds for which student is blocked*/
                    json = new JSONObject();
                    json.put("userRole", "student");
                    json.put("firstName", studentAccount.getFirstName());
                    json.put("lastName", studentAccount.getLastName());
                    json.put("primaryEmail", studentAccount.getPrimaryEmail());
                    json.put("password", studentAccount.getPassword());
                    json.put("countryCode", studentAccount.getCountryCode());
                    json.put("accountId", studentAccount.getStudentAccountId());
                    json.put("status", studentAccount.getStatus());
                    json.put("dateOfCreation", MyDateFormate.dateToString(studentAccount.getDateOfCreation()));
                    json.put("schoolName", studentAccount.getSchoolName());
                    json.put("gradeName", g.getGradeName());
                    json.put("gradeId", studentAccount.getGradeId());
                    json.put("syllabusId", studentAccount.getSyllabusId());
                    json.put("address", studentAccount.getAddress());
                    json.put("city", studentAccount.getCity());

                    list.add(json);
                }
            } else {
                json = new JSONObject();
                json.put("msg", "No Student Found in this Grade!");
                list.add(json);
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Something went wrong. Try Again!");
            list.add(json);
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateTeacher")
    public @ResponseBody
    JSONObject updateTeacher(@RequestBody final UsersMetaData bean) {
        JSONObject json = new JSONObject();

        String teacherId = bean.getAccountId(); //TEACHER000001

        Map<String, Object> map = new HashMap<>();
        map.put("teacherAccountId", teacherId);

        List<Object> listOfTeacherObj = service.getObject(TeacherAccount.class, map);
        TeacherAccount teacherAccount = (TeacherAccount) listOfTeacherObj.get(0);
        String accounttId = teacherAccount.getRegistration().getAccountId();
        Boolean status = teacherAccount.getStatus();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("accountId", accounttId);

        List<Object> listOfRegistrationObj = service.getObject(Registration.class, map1);

        Registration registration = (Registration) listOfRegistrationObj.get(0);

        int x1 = service.update(registration);

        if (x1 > 0) {

//            String testData[] = {bean.getPrimaryEmail()};
//            if (isAddressValid(testData[0]) == true) {
            teacherAccount.setFirstName(FirstCharLowerToUpper.getString(bean.getFirstName()));
            teacherAccount.setLastName(FirstCharLowerToUpper.getString(bean.getLastName()));

            teacherAccount.setPrimaryEmail(bean.getPrimaryEmail());
            teacherAccount.setPassword(bean.getPassword());
            teacherAccount.setCountryCode(bean.getCountryCode());
            teacherAccount.setMobileNum(Long.parseLong(bean.getMobileNum()));
            teacherAccount.setAddress(bean.getAddress());
            teacherAccount.setCity(bean.getCity());
            teacherAccount.setRegistration(registration);
            //Boolean status1 = teacherAccount.getStatus();

            if (status == false) {
                teacherAccount.setStatus(bean.getStatus());
            } else {
                teacherAccount.setStatus(bean.getStatus());
            }

            registration.setTeacherAccount(teacherAccount);
            registration.setFirstName(bean.getFirstName());
            registration.setLastName(bean.getLastName());
            registration.setPrimaryEmail(bean.getPrimaryEmail());
            registration.setPassword(bean.getPassword());
            registration.setCountryCode(bean.getCountryCode());
            registration.setMobileNum(Long.parseLong(bean.getMobileNum()));
            registration.setCity(bean.getCity());
            registration.setAddress(bean.getAddress());

            int x = service.update(teacherAccount);

            if (x > 0) {
                json.put("msg", "updated teacher");
            } else {
                json.put("msg", "Email  is already Exist");
            }
//            } else {
//                json.put("msg", "Coudn't find google account");
//            }
        } else {
            json.put("msg", "Something went wrong. Try Again!");
        }

        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateStudent")
    public @ResponseBody
    JSONObject updateStudent(@RequestBody final UsersMetaData bean) {

        JSONObject json = new JSONObject();
        String studentId = bean.getsAccountId();//STUDENT000001
        Map<String, Object> map = new HashMap<>();
        map.put("studentAccountId", studentId);

        List<Object> listOfStudentObj = service.getObject(StudentAccount.class, map);

        if (listOfStudentObj.size() > 0) {
            StudentAccount studentAccount = (StudentAccount) listOfStudentObj.get(0);
            studentAccount.setFirstName(FirstCharLowerToUpper.getString(bean.getFirstName()));
            studentAccount.setLastName(FirstCharLowerToUpper.getString(bean.getLastName()));
            studentAccount.setPassword(bean.getPassword());
            studentAccount.setCountryCode(bean.getCountryCode());
            studentAccount.setAddress(bean.getAddress());
            studentAccount.setCity(bean.getCity());
            studentAccount.setSchoolName(bean.getSchoolName());
            studentAccount.setGradeId(bean.getGradeId());
            studentAccount.setSyllabusId(bean.getSyllabusId());

            int x = service.update(studentAccount);
            if (x > 0) {
                json.put("sAccountId", studentAccount.getStudentAccountId());
                json.put("msg", "updated Student");
            }
        } else {
            json.put("msg", "student not found");
        }

        return json;
    }

    //    @RequestMapping(method = RequestMethod.POST, value = "/listGuest")
//    public @ResponseBody
//    ResponseEntity<?> listGuest(@RequestBody final Map<String, String> mapBean) {
//        int pageNo = Integer.parseInt(mapBean.get("pageNo"));
//
//        int maxResult = 10;
//        if (mapBean.get("maxResult") != null) {
//            maxResult = Integer.parseInt(mapBean.get("maxResult"));
//        }
//
//        JSONObject json;
//        ArrayList<Object> list = new ArrayList<>();
//        int count = 0;
//        try {
//            count = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Guest"));
//
//            List<Object> listObj = service.loadByLimit(String.format("%s", "FROM Guest g ORDER by g.dateOfCreation DESC"), (pageNo * maxResult), maxResult);
//            for (Object object : listObj) {
//                Guest guest = (Guest) object;
//                json = new JSONObject();
//                json.put("primaryEmail", guest.getMail());
//                json.put("count", count);
//                json.put("phoneNumber", guest.getPhoneNumber());
//                json.put("fullName", "Guest");
//                json.put("accountId", guest.getGuestId());
//                json.put("dateOfCreation", MyDateFormate.dateToString(guest.getDateOfCreation()));
//                list.add(json);
//            }
//        } catch (Exception e) {
//        }
//        json = new JSONObject();
//        json.put("count", count);
//        json.put("listOfGuests", list);
//
//        return new ResponseEntity<>(json, HttpStatus.OK);
//    }
    @RequestMapping(method = RequestMethod.POST, value = "/getDetailsOfStudentId")
    public @ResponseBody
    JSONObject getdetailsOfStudentId(@RequestBody AttendanceBean bean) {

        JSONObject json = new JSONObject();
        Map<String, Object> map = new HashMap<>();
        map.put("studentAccountId", bean.getStudentAccountId());
        List<Object> list = service.getObject(StudentAccount.class, map);
        if (list.size() > 0) {
            StudentAccount account = (StudentAccount) list.get(0);
            json.put("dateOfCreation", account.getDateOfCreation());
            json.put("firstName", account.getFirstName());
            json.put("password", account.getPassword());
            json.put("status", account.getStatus());
            json.put("address", account.getAddress());
            json.put("city", account.getCity());
            json.put("lastName", account.getLastName());
            json.put("primaryEmail", account.getPrimaryEmail());
        } else {
            json.put("msg", "Empty list");
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/adminForgotPassword")
    public @ResponseBody
    JSONObject adminForgotPassword(@RequestBody MailBean bean) throws MessagingException {
        JSONObject json;
        try {
//            String admin = "admin@yolearn.com";
            String adminId = bean.getAdminAccountId();//ACCO000001

            Map<String, Object> map = new HashMap<>();
            map.put("accountId", adminId);

            List<Object> listOfRegistrationObj = service.getObject(Registration.class, map);
            if (listOfRegistrationObj.size() > 0) {
                Registration adminIds = (Registration) listOfRegistrationObj.get(0);

                adminIds.setPassword(bean.getPassword());
                int x = service.update(adminIds);

                if (x > 0) {

                    json = new JSONObject();
                    json.put("msg", "updated password");

                } else {
                    json = new JSONObject();
                    json.put("msg", "Something went wrong. Try Again!");
                }

            } else {
                json = new JSONObject();
                json.put("msg", "Miss match Id or Empty list");
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Error " + e.getMessage());
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/studentForgotPassword")
    public @ResponseBody
    JSONObject studentForgotPassword(@RequestBody MailBean bean) throws MessagingException, AddressException, IOException {
        JSONObject json;
        try {
            String admin = "admin@yolearn.com";
            String studentAccountId = bean.getStudentAccountId();//STUDENT000001

            Map<String, Object> map = new HashMap<>();
            map.put("studentAccountId", studentAccountId);

            List<Object> listOfStudentAccountObj = service.getObject(StudentAccount.class, map);
            if (listOfStudentAccountObj.size() > 0) {
                StudentAccount studentAccount = (StudentAccount) listOfStudentAccountObj.get(0);
                studentAccount.setPassword(bean.getPassword());
                int x = service.update(studentAccount);

                if (x > 0) {
                    json = new JSONObject();

                    /*sending email to the user*/
                    boolean b = studentAccount.getRegistration().getMailSubscriptionStatus();
                    if (!b) {
                        String subject1 = "YOLEARN - Password Updated";
                        String emailMsg1 = "Dear Parent,<br><br>"
                                + "Your student <b>" + studentAccount.getFirstName() + "</b> has changed his password.<br><br>"
                                + "<b>Login Details:</b><br>"
                                + "<table>"
                                + "<tr><td>Email</td><td> : " + studentAccount.getPrimaryEmail() + "</td></tr>"
                                + "<tr><td>Password</td><td> : " + studentAccount.getPassword() + "</td></tr>"
                                + "</table><br><br>"
                                + "<small>For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
                                + "You received this message because this email address was used to register you in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                                + "<b>Yours Sincerely,</b><br>"
                                + "YOLEARN Team.<br><br>"
                                + "Thanks for choosing Yolearn";
                        Set<String> to1 = new HashSet<>();
                        to1.add(studentAccount.getPrimaryEmail());
                        javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg1);
                    }
                    json.put("msg", "please check your mail");

                    /*sending email to the admin*/
                    String subject2 = "Password Updated(Student)";
                    String emailMsg2 = "Hi Admin,<br><br>"
                            + "<b>" + studentAccount.getFirstName() + "</b> has updated his/her password successfully.<br><br>"
                            + "<b>Login details:</b><br>"
                            + "<table>"
                            + "<tr><td>Email</td><td> : " + studentAccount.getPrimaryEmail() + "</td></tr>"
                            + "<tr><td>Password</td><td> : " + studentAccount.getPassword() + "</td></tr>"
                            + "</table><br><br>"
                            + "<b>Yours Sincerely,</b><br>"
                            + "YOLEARN Team.";

                    Set<String> to2 = new HashSet<>();

                    to2.add(admin);

                    javaMail_Sender_Info.composeAndSend(subject2, to2, emailMsg2);
                    json.put("msg", "updated password");

                } else {
                    json = new JSONObject();
                    json.put("msg", "Something went wrong. Try Again!");
                }

            } else {
                json = new JSONObject();
                json.put("msg", "miss match at StudentAccountId in Student class ");
            }

        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Error " + e.getMessage());
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/teacherForgotPassword")
    public @ResponseBody
    JSONObject teacherForgotPassword(@RequestBody MailBean bean) throws MessagingException, AddressException, IOException {

        JSONObject json;
        try {
            String admin = "admin@yolearn.com";
            String teacherId = bean.getTeacherAccountId();//TEACHER000001

            Map<String, Object> map = new HashMap<>();
            map.put("teacherAccountId", teacherId);

            List<Object> listOfTeacherObj = service.getObject(TeacherAccount.class, map);
            if (listOfTeacherObj.size() > 0) {

                TeacherAccount teacherAccount = (TeacherAccount) listOfTeacherObj.get(0);
                String accounttId = teacherAccount.getRegistration().getAccountId();

                Map<String, Object> map1 = new HashMap<>();
                map1.put("accountId", accounttId);

                List<Object> listOfRegistrationObj = service.getObject(Registration.class, map1);
                if (listOfRegistrationObj.size() > 0) {

                    Registration registration = (Registration) listOfRegistrationObj.get(0);

                    int x1 = service.update(registration);

                    if (x1 > 0) {
                        teacherAccount.setPassword(bean.getPassword());
                        teacherAccount.setRegistration(registration);
                        registration.setTeacherAccount(teacherAccount);
                        registration.setPassword(bean.getPassword());
                        int x = service.update(teacherAccount);
                        if (x > 0) {
                            json = new JSONObject();

                            /*sending email to the user*/
                            boolean b = teacherAccount.getRegistration().getMailSubscriptionStatus();
                            if (!b) {
                                String subject1 = "YOLEARN - Password Updated";
                                String emailMsg1 = "Hi <b>" + teacherAccount.getFirstName() + "</b><br><br>"
                                        + "Your password has been updated successfully!<br><br>"
                                        + "<b>Login details:</b><br>"
                                        + "<table>"
                                        + "<tr><td>Email</td><td> : " + teacherAccount.getPrimaryEmail() + "</td></tr>"
                                        + "<tr><td>Password</td><td> : " + teacherAccount.getPassword() + "</td></tr>"
                                        + "</table><br><br>"
                                        + "<b>Yours Sincerely,</b><br>"
                                        + "YOLEARN Team.<br><br>"
                                        + "Thanks for choosing Yolearn";
                                Set<String> to1 = new HashSet<>();
                                to1.add(teacherAccount.getPrimaryEmail());
                                javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg1);
                            }
                            json.put("msg", "please check your mail");

                            /*sending email to the admin*/
                            String subject2 = "Password Updated(Teacher)";
                            String emailMsg2 = "Hi Admin,<br><br>"
                                    + "<b>" + teacherAccount.getFirstName() + "</b> has updated his/her password successfully.<br><br>"
                                    + "<b>Login details:</b><br>"
                                    + "<table>"
                                    + "<tr><td>Email</td><td> : " + teacherAccount.getPrimaryEmail() + "</td></tr>"
                                    + "<tr><td>Password</td><td> : " + teacherAccount.getPassword() + "</td></tr>"
                                    + "</table><br><br>"
                                    + "<b>Yours Sincerely,</b><br>"
                                    + "YOLEARN Team.<br><br>";

                            Set<String> to2 = new HashSet<>();

                            to2.add(admin);

                            javaMail_Sender_Info.composeAndSend(subject2, to2, emailMsg2);
                            json.put("msg", "updated password");
                        } else {
                            json = new JSONObject();
                            json.put("msg", "not updated password");
                        }
                    } else {
                        json = new JSONObject();
                        json.put("msg", "Something went wrong. Try Again!");
                    }
                } else {
                    json = new JSONObject();
                    json.put("msg", "miss match at accountId in Registration class ");
                }
            } else {
                json = new JSONObject();
                json.put("msg", "miss match at parentAccountId in ParentAccount class ");
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Error " + e.getMessage());
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/parentForgotPassword")
    public @ResponseBody
    JSONObject parentForgotPassword(@RequestBody MailBean bean) throws MessagingException, AddressException, IOException {

        JSONObject json;
        try {
            String admin = "admin@yolearn.com";
            String parentId = bean.getParentAccountId();//PARENT000001

            Map<String, Object> map = new HashMap<>();
            map.put("parentAccountId", parentId);

            List<Object> listOfParentAccountObj = service.getObject(ParentAccount.class, map);
            if (listOfParentAccountObj.size() > 0) {

                ParentAccount parentAccount = (ParentAccount) listOfParentAccountObj.get(0);
                String accounttId = parentAccount.getRegistration().getAccountId();

                Map<String, Object> map1 = new HashMap<>();
                map1.put("accountId", accounttId);

                List<Object> listOfRegistrationObj = service.getObject(Registration.class, map1);
                if (listOfRegistrationObj.size() > 0) {

                    Registration registration = (Registration) listOfRegistrationObj.get(0);

                    int x1 = service.update(registration);

                    if (x1 > 0) {
                        parentAccount.setPassword(bean.getPassword());
                        parentAccount.setRegistration(registration);
                        registration.setParentAccount(parentAccount);
                        registration.setPassword(bean.getPassword());
                        int x = service.update(parentAccount);
                        if (x > 0) {
                            json = new JSONObject();

                            boolean b = parentAccount.getRegistration().getMailSubscriptionStatus();
                            if (!b) {
                                /*sending email to the user*/
                                String subject1 = "YOLEARN - Password Updated";
                                String emailMsg1 = "Hi <b>" + parentAccount.getFirstName() + "<b><br><br>"
                                        + "Your password has been updated successfully.<br><br>"
                                        + "<b>Login Details:</b><br>"
                                        + "<table>"
                                        + "<tr><td>Email</td><td> : " + parentAccount.getPrimaryEmail() + "</td></tr>"
                                        + "<tr><td>Password</td><td> : " + parentAccount.getPassword() + "</td></tr>"
                                        + "</table><br><br>"
                                        + "<b>Yours Sincerely,</b><br>"
                                        + "YOLEARN Team.<br><br>"
                                        + "Thanks for choosing Yolearn";
                                Set<String> to1 = new HashSet<>();
                                to1.add(parentAccount.getPrimaryEmail());
                                javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg1);
                            }
                            json.put("msg", "please check your mail");

                            /*sending email to the admin*/
                            String subject2 = "Password Updated(Parent)";
                            String emailMsg2 = "Hi Admin,<br>"
                                    + "<b>" + parentAccount.getFirstName() + "<b> has updated his/her password successfully.<br><br>"
                                    + "<b>Login details:</b><br><br>"
                                    + "<table>"
                                    + "<tr><td>Email</td><td> : " + parentAccount.getPrimaryEmail() + "</td></tr>"
                                    + "<tr><td>Password</td><td> : " + parentAccount.getPassword() + "</td></tr>"
                                    + "</table><br><br>"
                                    + "<b>Yours Sincerely,</b><br>"
                                    + "YOLEARN Team.";

                            Set<String> to2 = new HashSet<>();
                            to2.add(admin);
                            javaMail_Sender_Info.composeAndSend(subject2, to2, emailMsg2);
                            json.put("msg", "updated password");
                        } else {
                            json = new JSONObject();
                            json.put("msg", "not updated password");
                        }
                    } else {
                        json = new JSONObject();
                        json.put("msg", "Something went wrong. Try Again!");
                    }
                } else {
                    json = new JSONObject();
                    json.put("msg", "miss match at accountId in Registration class ");
                }
            } else {
                json = new JSONObject();
                json.put("msg", "miss match at parentAccountId in ParentAccount class ");
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Error " + e.getMessage());
        }
        return json;
    }

    @RequestMapping(value = {"/updateStudentStatus"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> updateStudentStatus(@RequestBody final UsersMetaData bean) {

        JSONObject json = new JSONObject();
        String studentId = bean.getsAccountId();//STUDENT000001
        Map<String, Object> map = new HashMap<>();
        map.put("studentAccountId", studentId);

        List<Object> listOfStudentObj = service.getObject(StudentAccount.class, map);

        if (listOfStudentObj.size() > 0) {
            StudentAccount studentAccount = (StudentAccount) listOfStudentObj.get(0);
            boolean b = studentAccount.getStatus();
            if (b == true) {
                b = false;
                studentAccount.setStatus(b);
            } else {
                b = true;
                studentAccount.setStatus(b);
            }

            int x = service.update(studentAccount);
            if (x > 0) {
                json.put("msg", "updated Student");
            }
        } else {
            json.put("msg", "student not found");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    //    @RequestMapping(method = RequestMethod.POST, value = "/guestRegister")
//    public @ResponseBody
//    JSONObject guestRegister(@RequestBody MailBean bean) {
//        List<Object> l = new ArrayList<>();
//        JSONObject json = new JSONObject();
//        String m;
//        Guest guest = new Guest();
//        List<Object> list = service.getObject(Guest.class);
//        if (list.size() > 0) {
//            for (Object object : list) {
//
//                Guest g = (Guest) object;
//                m = g.getMail();
//                l.add(m);
//            }
//
//            if (l.contains(bean.getSenderMail())) {
//
////                json.put("msg", "not saved because alreday this mail is exist.");
//            } else {
//
//                Timestamp date = new Timestamp(System.currentTimeMillis());
//                String id = (String) pKGenerator.generate(Guest.class, "GUEST_");
//                guest.setGuestId(id);
//                guest.setMail(bean.getSenderMail());
//                guest.setPhoneNumber(bean.getPhoneNumber());
//                guest.setDateOfCreation(date);
//                guest.setGradeName(bean.getGrade());
//
//                Map<String, Object> mapGrade = new HashMap<>();
//                mapGrade.put("gradeName", bean.getGrade());
//                List<Object> liGradeObject = service.getObject(Grade.class, mapGrade);
//                if (liGradeObject.size() > 0) {
//                    Grade g = (Grade) liGradeObject.get(0);
//                    guest.setGradeId(g.getGradeId());
//                }
//
//                if (service.save(guest) > 0) {
//                    json.put("guestId", id);
//                    json.put("senderMail", bean.getSenderMail());
//                    json.put("phoneNumber", bean.getPhoneNumber());
//                    json.put("msg", "Congratulations! Registration Successful.");
//                } else {
//                    json.put("msg", "Something went wrong. Please check your inputs or try again.");
//                }
//            }
//
//        } else {
//            Timestamp date = new Timestamp(System.currentTimeMillis());
//            String id = (String) pKGenerator.generate(Guest.class, "GUEST_");
//            guest.setGuestId(id);
//            guest.setMail(bean.getSenderMail());
//
//            guest.setPhoneNumber(bean.getPhoneNumber());
//            guest.setDateOfCreation(date);
//            guest.setGradeName(bean.getGrade());
//            Map<String, Object> mapGrade = new HashMap<>();
//            mapGrade.put("gradeName", bean.getGrade());
//            List<Object> liGradeObject = service.getObject(Grade.class, mapGrade);
//            if (liGradeObject.size() > 0) {
//                Grade g = (Grade) liGradeObject.get(0);
//                guest.setGradeId(g.getGradeId());
//            }
//
//            if (service.save(guest) > 0) {
//                json.put("guestId", id);
//                json.put("senderMail", bean.getSenderMail());
//                json.put("phoneNumber", bean.getPhoneNumber());
//                json.put("msg", "Congratulations! Registration Successful.");
//            } else {
//                json.put("msg", "Something went wrong. Please check your inputs or try again.");
//            }
//
//        }
//
//        return json;
//    }
    @RequestMapping(value = {"/listOfStudentsBySyllabusIdandNobatchassigned"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> listOfStudentsBySyllabusIdandNobatchassigned(@RequestBody final Map<String, String> mapBean) {
        String syllabusId = mapBean.get("syllabusId");
        List<JSONObject> list = new ArrayList();
        JSONObject json;
        try {
            String sql = "SELECT s.STUDENT_ID,s.ADDRESS,s.CITY,s.FIRST_NAME,s.LAST_NAME,s.COUNTRY_CODE,ss.SYLLABUS_ID,ss.GRADE_ID,ss.SUBSCRIPTION_ID,ss.MUL_SUB_ID\n"
                    + "FROM STUDENT_ACCOUNT s\n"
                    + "INNER JOIN student_subscriptions ss ON s.STUDENT_ID = ss.STUDENT_ID where ss.SYLLABUS_ID='" + syllabusId + "' and ss.BATCH_ID is null";
            List<Object> objects = this.service.getObjectsByNativeSqlQuery(sql);
//            List<Object> listStudentObject = service.getObject(String.format("%s", "FROM StudentAccount s WHERE s.syllabusId = '" + syllabusId + "' AND s.batchId IS NULL"));
//            listStudentObject = service.getObject(sql);
            if (objects.size() > 0) {
                for (Object object : objects) {
                    json = new JSONObject();
                    Object[] obj = (Object[]) object;
                    json.put("sAccountId", obj[0]);
                    json.put("address", obj[1]);
                    json.put("city", obj[2]);
                    json.put("firstName", obj[3]);
                    json.put("lastName", obj[4]);
                    json.put("countryCode", obj[5]);
                    json.put("syllabusId", obj[6]);
                    json.put("gradeId", obj[7]);
                    json.put("multipleSub_id", obj[9]);
//                    json.put("syllabusId", studentAccount.getSyllabusId());
//                    StudentAccount studentAccount = (StudentAccount) object;
//                    json = new JSONObject();
//                    json.put("subsTypeId", studentAccount.getSubscribeId());
//                    json.put("userRole", "student");
//                    json.put("firstName", studentAccount.getFirstName());
//                    json.put("lastName", studentAccount.getLastName());
//                    json.put("fullName", studentAccount.getFirstName() + " " + studentAccount.getLastName());
//                    json.put("primaryEmail", studentAccount.getPrimaryEmail());
//                    json.put("password", studentAccount.getPassword());
//                    json.put("countryCode", studentAccount.getCountryCode());
//                    json.put("sAccountId", studentAccount.getStudentAccountId());
//                    json.put("status", studentAccount.getStatus());
//                    json.put("dateOfCreation", MyDateFormate.dateToString(studentAccount.getDateOfCreation()));
//                    json.put("address", studentAccount.getAddress());
//                    json.put("city", studentAccount.getCity());
//                    json.put("schoolName", studentAccount.getSchoolName());
//                    json.put("gradeId", studentAccount.getGradeId());
//                    json.put("syllabusId", studentAccount.getSyllabusId());

                    String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + obj[7] + "'")).get(0);
                    String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + obj[6] + "'")).get(0);

                    json.put("gradeName", gradeName);
                    json.put("syllabusName", syllabusName);

                    list.add(json);
                }
            } else {
                json = new JSONObject();
                json.put("msg", "No student Found");
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Something went wrong.");
        }

//        return new ResponseEntity<>(list, HttpStatus.OK);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = {"/listOfStudentsByBatchId"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> listOfStudentsByBatchId(@RequestBody final Map<String, String> mapBean) {
        List<JSONObject> list = new ArrayList<>();
        JSONObject json;
        String batchId = mapBean.get("batchId");
        try {

            String sql = "SELECT s.STUDENT_ID,s.FIRST_NAME,s.LAST_NAME,ss.SYLLABUS_ID,ss.GRADE_ID,ss.MUL_SUB_ID\n"
                    + "FROM STUDENT_ACCOUNT s\n"
                    + "INNER JOIN student_subscriptions ss ON s.STUDENT_ID = ss.STUDENT_ID where  ss.BATCH_ID='" + batchId + "'";
//            String sql = "SELECT s.STUDENT_ID,s.FIRST_NAME,s.LAST_NAME,ss.SYLLABUS_ID,ss.GRADE_ID,ss.MUL_SUB_ID\n"
//                    + "FROM student_account s\n"
//                    + "INNER JOIN student_subscriptions ss ON s.STUDENT_ID = ss.STUDENT_ID where  ss.BATCH_ID='" + batchId + "'";
//            List<Object> listStudentObj = service.getObject(String.format("%s", "FROM StudentAccount s WHERE s.batchId = '" + batchId + "'"));

            List<Object> objects = this.service.getObjectsByNativeSqlQuery(sql);

            if (objects.size() > 0) {
                for (Object object : objects) {
//                    StudentAccount studentAccount = (StudentAccount) object;
//                    json = new JSONObject();
                    json = new JSONObject();
                    Object[] obj = (Object[]) object;
                    json.put("sAccountId", obj[0]);
                    json.put("firstName", obj[1]);
                    json.put("lastName", obj[2]);
                    json.put("syllabusId", obj[3]);
                    json.put("gradeId", obj[4]);
                    json.put("multipleSub_id", obj[5]);

//                    json.put("subsTypeId", studentAccount.getSubscribeId());
//                    json.put("userRole", "student");
//                    json.put("firstName", studentAccount.getFirstName());
//                    json.put("lastName", studentAccount.getLastName());
//                    json.put("fullName", studentAccount.getFirstName() + " " + studentAccount.getLastName());
//                    json.put("primaryEmail", studentAccount.getPrimaryEmail());
//                    json.put("password", studentAccount.getPassword());
//                    json.put("countryCode", studentAccount.getCountryCode());
//                    json.put("sAccountId", studentAccount.getStudentAccountId());
//                    json.put("status", studentAccount.getStatus());
//                    json.put("dateOfCreation", MyDateFormate.dateToString(studentAccount.getDateOfCreation()));
//                    json.put("address", studentAccount.getAddress());
//                    json.put("city", studentAccount.getCity());
//                    json.put("schoolName", studentAccount.getSchoolName());
//                    json.put("gradeId", studentAccount.getGradeId());
//                    json.put("syllabusId", studentAccount.getSyllabusId());
                    String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + obj[4] + "'")).get(0);
                    String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + obj[3] + "'")).get(0);

                    json.put("gradeName", gradeName);
                    json.put("syllabusName", syllabusName);
                    list.add(json);
                }
            } else {
                json = new JSONObject();
                json.put("msg", "No Students assigned to this batch");
                list.add(json);
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Something went wrong.");
            list.add(json);
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getAllGuestOfDemo"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getAllGuestOfDemo(@RequestBody final Map<String, String> mapBean) {
        List<JSONObject> list = new ArrayList<>();
        JSONObject jsonOfList = new JSONObject();
        int pageNo = Integer.parseInt(mapBean.get("pageNo"));
        int maxResult = 10;
        if (mapBean.get("maxResult") != null) {
            maxResult = Integer.parseInt(mapBean.get("maxResult"));
        }
        try {
            int count = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Registration r WHERE r.userRole = 'guest'"));
            if (count > 0) {
                List<Object> listDemoGuest = service.loadByLimit(String.format("%s", "FROM Registration r WHERE r.userRole = 'guest' ORDER BY r.dateOfCreation"), (pageNo * maxResult), maxResult);
                for (Object object : listDemoGuest) {
                    Registration registration = (Registration) object;
                    JSONObject json = getRegistrationJSON(registration);

                    String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + registration.getGradeId() + "'")).get(0);
                    json.put("gradeName", gradeName);
                    json.put("count", count);

                    list.add(json);
                }
            } else {
                JSONObject json = new JSONObject();
                json.put("msg", "No Guest for demo.");
                list.add(json);
            }
            jsonOfList.put("guestDemo", list);
            jsonOfList.put("count", count);
        } catch (Exception e) {
        }

        return new ResponseEntity<>(jsonOfList, HttpStatus.OK);
    }

    @RequestMapping(value = {"/guestRegister"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> guestRegister(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();

        String email = mapBean.get("email");
        String phoneNumber = mapBean.get("phoneNumber");
        Timestamp date = new Timestamp(System.currentTimeMillis());
        try {

            Guest guest = new Guest();
            String id = (String) pKGenerator.generate(Guest.class, "GUEST");
            guest.setGuestId(id);
            guest.setEmail(email);
            guest.setPhoneNumber(Long.parseLong(phoneNumber));
            guest.setDateOfCreation(date);

            if (service.save(guest) > 0) {
                json.put("msg", "Guest registered!");
            } else {
                json.put("msg", "Guest not registered!");
            }

        } catch (NumberFormatException e) {
            json.put("msg", "Something went wrong. Try Again!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getalladmin"}, method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<?> getalladmin() {
        List<JSONObject> jSONObjects = new ArrayList<>();
        JSONObject json;
        try {

            String sql = "FROM AdminAccount a where a.registration.accountId <> 'ACCO000001'";
            List<Object> l = service.getObject(String.format("%s", sql));
            if (l.size() > 0) {
                for (Object object : l) {
                    json = new JSONObject();
                    AdminAccount adminAccount = (AdminAccount) object;

                    json.put("userRole", "admin");
                    json.put("firstName", adminAccount.getFirstName());
                    json.put("lastName", adminAccount.getLastName());
                    json.put("primaryEmail", adminAccount.getPrimaryEmail());
                    json.put("password", adminAccount.getPassword());
                    json.put("countryCode", adminAccount.getCountryCode());
                    json.put("mobileNum", adminAccount.getMobileNum());
                    json.put("accountId", adminAccount.getRegistration().getAccountId());
                    json.put("dateOfCreation", MyDateFormate.dateToString(adminAccount.getDateOfCreation()));
                    json.put("address", adminAccount.getAddress());
                    json.put("status", adminAccount.getStatus());
                    json.put("fullName", String.format("%s", adminAccount.getFirstName() + " " + adminAccount.getLastName()));
                    json.put("city", adminAccount.getCity());
//                    json.put("parentAccess", adminAccount.getParentAccess());
//                    json.put("teacherAccess", adminAccount.getTeacherAccess());
//                    json.put("studentAccess", adminAccount.getStudentAccess());
//                    json.put("demoMemberAccess", adminAccount.getDemoMemberAccess());
//                    json.put("classRoomAccess", adminAccount.getClassRoomAccess());
//                    json.put("scheduleAccess", adminAccount.getScheduleAccess());
//                    json.put("produtAccess", adminAccount.getProdutAccess());
//                    json.put("subscriptionAccess", adminAccount.getSubscriptionAccess());
//                    json.put("batchAccess", adminAccount.getBatchAccess());
//                    json.put("talentAccess", adminAccount.getTalentAccess());
//                    json.put("recordedAccess", adminAccount.getRecordedAccess());
//                    json.put("upcomingAccess", adminAccount.getUpcomingAccess());
//                    json.put("assignmentAccess", adminAccount.getAssignmentAccess());
//                    json.put("testAccess", adminAccount.getTestAccess());

                    if (listOfImagesNameExistOrNot(adminAccount.getAdminId() + ".jpg")) {
                        json.put("image", "/UserImages/" + adminAccount.getAdminId() + ".jpg");
                    } else {
                        json.put("image", null);
                    }
                    jSONObjects.add(json);
                }
            } else {
                json = new JSONObject();
                json.put("msg", "Empty list");
                jSONObjects.add(json);
            }
        } catch (Exception e) {

        }
        return new ResponseEntity<>(jSONObjects, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateAdminAccess")
    public @ResponseBody
    JSONObject updateAdminAccess(@RequestBody final UsersMetaData bean) {

        JSONObject json = new JSONObject();

        String adminId = bean.getAccountId(); //ADMIN000001
        Map<String, Object> map = new HashMap<>();
        map.put("accountId", adminId);
        List<Object> listOfTeacherObj1 = service.getObject(Registration.class, map);

        Registration registration = (Registration) listOfTeacherObj1.get(0);
        String id = registration.getAdminAccount().getAdminId();

        Map<String, Object> map1 = new HashMap<>();
        map1.put("adminId", id);
        List<Object> listOfTeacherObj = service.getObject(AdminAccount.class, map1);
        AdminAccount adminAccount = (AdminAccount) listOfTeacherObj.get(0);
        Boolean teacherAccess = bean.getTeacherAccess();
        Boolean parentAccess = bean.getParentAccess();
        Boolean studentAccess = bean.getStudentAccess();
        Boolean demoMemberAccess = bean.getDemoMemberAccess();
        Boolean classRoomAccess = bean.getClassRoomAccess();
        Boolean recordedAccess = bean.getRecordedAccess();
        Boolean upcomingAccess = bean.getUpcomingAccess();
        Boolean assignmentAccess = bean.getAssignmentAccess();
        Boolean testAccess = bean.getTestAccess();
        Boolean scheduleAccess = bean.getScheduleAccess();
        Boolean productAccess = bean.getProdutAccess();
        Boolean subscriptionAccess = bean.getSubscriptionAccess();
        Boolean batchAccess = bean.getBatchAccess();
        Boolean talentAccess = bean.getTalentAccess();
        Boolean adminAccess = bean.getAdminAccess();

        if (teacherAccess == false) {
            adminAccount.setTeacherAccess(bean.getTeacherAccess());
        } else {
            adminAccount.setTeacherAccess(bean.getTeacherAccess());
        }

        if (studentAccess == false) {
            adminAccount.setStudentAccess(bean.getStudentAccess());
        } else {
            adminAccount.setStudentAccess(bean.getStudentAccess());
        }
        if (demoMemberAccess == false) {
            adminAccount.setDemoMemberAccess(bean.getDemoMemberAccess());
        } else {
            adminAccount.setDemoMemberAccess(bean.getDemoMemberAccess());
        }
        if (parentAccess == false) {
            adminAccount.setParentAccess(bean.getParentAccess());
        } else {
            adminAccount.setParentAccess(bean.getParentAccess());
        }
        if (classRoomAccess == false) {
            adminAccount.setClassRoomAccess(bean.getClassRoomAccess());
        } else {
            adminAccount.setClassRoomAccess(bean.getClassRoomAccess());
        }
        if (recordedAccess == false) {
            adminAccount.setRecordedAccess(bean.getRecordedAccess());
        } else {
            adminAccount.setRecordedAccess(bean.getRecordedAccess());
        }
        if (upcomingAccess == false) {
            adminAccount.setUpcomingAccess(bean.getUpcomingAccess());
        } else {
            adminAccount.setUpcomingAccess(bean.getUpcomingAccess());
        }
        if (assignmentAccess == false) {
            adminAccount.setAssignmentAccess(bean.getAssignmentAccess());
        } else {
            adminAccount.setAssignmentAccess(bean.getAssignmentAccess());
        }
        if (testAccess == false) {
            adminAccount.setTestAccess(bean.getTestAccess());
        } else {
            adminAccount.setTestAccess(bean.getTestAccess());
        }

        if (scheduleAccess == false) {
            adminAccount.setScheduleAccess(bean.getScheduleAccess());
        } else {
            adminAccount.setScheduleAccess(bean.getScheduleAccess());
        }
        if (productAccess == false) {
            adminAccount.setProdutAccess(bean.getProdutAccess());
        } else {
            adminAccount.setProdutAccess(bean.getProdutAccess());
        }
        if (subscriptionAccess == false) {
            adminAccount.setSubscriptionAccess(bean.getSubscriptionAccess());
        } else {
            adminAccount.setSubscriptionAccess(bean.getSubscriptionAccess());
        }
        if (batchAccess == false) {
            adminAccount.setBatchAccess(bean.getBatchAccess());
        } else {
            adminAccount.setBatchAccess(bean.getBatchAccess());
        }
        if (talentAccess == false) {
            adminAccount.setTalentAccess(bean.getTalentAccess());
        } else {
            adminAccount.setTalentAccess(bean.getTalentAccess());
        }

        if (adminAccess == false) {
            adminAccount.setAdminAccess(bean.getAdminAccess());
        } else {
            adminAccount.setAdminAccess(bean.getAdminAccess());
        }
        int x = service.update(adminAccount);

        if (x > 0) {
            json.put("msg", "updated admin");
        } else {
            json.put("msg", "not updated admin");
        }

        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateAdmin")
    public @ResponseBody
    JSONObject updateAdmin(@RequestBody final UsersMetaData bean) {

        JSONObject json = new JSONObject();

        String adminId = bean.getAccountId(); //ADMIN000001
        Map<String, Object> map1 = new HashMap<>();
        map1.put("accountId", adminId);

        List<Object> listOfRegistrationObj = service.getObject(Registration.class, map1);
        Registration registration = (Registration) listOfRegistrationObj.get(0);
        String adminIdfrom = registration.getAdminAccount().getAdminId();

        Map<String, Object> map = new HashMap<>();
        map.put("adminId", adminIdfrom);
        List<Object> listOfadmin = service.getObject(AdminAccount.class, map);
        AdminAccount adminAccount = (AdminAccount) listOfadmin.get(0);
        Boolean status = adminAccount.getStatus();

        int x1 = service.update(registration);

        if (x1 > 0) {

            String testData[] = {bean.getPrimaryEmail()};

//            if (isAddressValid(testData[0]) == true) {
            adminAccount.setFirstName(FirstCharLowerToUpper.getString(bean.getFirstName()));
            adminAccount.setLastName(FirstCharLowerToUpper.getString(bean.getLastName()));

            adminAccount.setPrimaryEmail(bean.getPrimaryEmail());
            adminAccount.setPassword(bean.getPassword());
            adminAccount.setCountryCode(bean.getCountryCode());
            adminAccount.setMobileNum(Long.parseLong(bean.getMobileNum()));
            adminAccount.setAddress(bean.getAddress());
            adminAccount.setCity(bean.getCity());
            adminAccount.setRegistration(registration);
            //Boolean status1 = teacherAccount.getStatus();

            if (status == false) {
                adminAccount.setStatus(bean.getStatus());
            } else {
                adminAccount.setStatus(bean.getStatus());
            }

            registration.setAdminAccount(adminAccount);
            registration.setFirstName(bean.getFirstName());
            registration.setLastName(bean.getLastName());
            registration.setPrimaryEmail(bean.getPrimaryEmail());
            registration.setPassword(bean.getPassword());
            registration.setCountryCode(bean.getCountryCode());
            registration.setMobileNum(Long.parseLong(bean.getMobileNum()));
            registration.setCity(bean.getCity());
            registration.setAddress(bean.getAddress());

            int x = service.update(adminAccount);

            if (x > 0) {
                json.put("msg", "updated admin");
            } else {
                json.put("msg", "Email  is already Exist");
            }
//            } else {
//                json.put("msg", "Coudn't find google account");
//            }
        } else {
            json.put("msg", "Something went wrong. Try Again!");
        }

        return json;
    }

    @RequestMapping(value = {"/getAdminById"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getAdminById(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        try {
            String accountId = mapBean.get("accountId");
            Map<String, Object> m = new HashMap<>();
            m.put("accountId", accountId);
            List<Object> list = service.getObject(Registration.class, m);
            if (list.size() > 0) {
                Registration registration = (Registration) list.get(0);
                String adminId = registration.getAdminAccount().getAdminId();
                Map<String, Object> m1 = new HashMap<>();
                m1.put("adminId", adminId);
                List<Object> listadmin = service.getObject(AdminAccount.class, m1);
                if (listadmin.size() > 0) {
                    AdminAccount adminAccount = (AdminAccount) listadmin.get(0);
                    json.put("fullName", String.format("%s", adminAccount.getFirstName() + " " + adminAccount.getLastName()));
                    json.put("parentAccess", adminAccount.getParentAccess());
                    json.put("teacherAccess", adminAccount.getTeacherAccess());
                    json.put("studentAccess", adminAccount.getStudentAccess());
                    json.put("demoMemberAccess", adminAccount.getDemoMemberAccess());
                    json.put("classRoomAccess", adminAccount.getClassRoomAccess());
                    json.put("scheduleAccess", adminAccount.getScheduleAccess());
                    json.put("produtAccess", adminAccount.getProdutAccess());
                    json.put("subscriptionAccess", adminAccount.getSubscriptionAccess());
                    json.put("batchAccess", adminAccount.getBatchAccess());
                    json.put("talentAccess", adminAccount.getTalentAccess());
                    json.put("recordedAccess", adminAccount.getRecordedAccess());
                    json.put("upcomingAccess", adminAccount.getUpcomingAccess());
                    json.put("assignmentAccess", adminAccount.getAssignmentAccess());
                    json.put("testAccess", adminAccount.getTestAccess());
                    json.put("adminAccess", adminAccount.getAdminAccess());
                } else {
                    json.put("msg", "Empty list");
                }
            } else {
                json.put("msg", "Empty list.");
            }
        } catch (Exception e) {
            json.put("msg", e.getMessage());
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getAllSubjectsByStudentAccountId/{syllabusId}"}, method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<?> getAllSubjectsBySyllabusI(@PathVariable("syllabusId") String syllabusId) {
        List<JSONObject> list = new ArrayList<>();
        try {
            String sql = "SELECT SUBJECT_ID,SUBJECT_NAME FROM `SUBJECT_REGISTER`sr where sr.SYLLABUS_ID ='" + syllabusId + "'";
            List<Object> objects = this.service.getObjectsByNativeSqlQuery(sql);
            for (Object object1 : objects) {
                Object[] objects1 = (Object[]) object1;
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("subjectId", objects1[0]);
                jSONObject.put("subjectName", objects1[1]);
                list.add(jSONObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getSyllabusIdName/{studentAccountId}"}, method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<?> getSyllabusIdName(@PathVariable("studentAccountId") String studentAccountId) {
        List<JSONObject> list = new ArrayList<>();
//        JSONArray list = new JSONArray();
//        JSONArray jArray = null;

        try {
//        String nativeSqlQuery="select sr.SYLLABUS_ID,sr.SYLLABUS_NAME from SYLLABUS_REGISTER sr where sr.SYLLABUS_ID in "
//                + "(select SYLLABUS_ID st from SUBSCRIPTION_TYPE st where st.SUBS_TYPE_ID in"
//                + "(SELECT ss.SUBSCRIPTION_ID from student_subscriptions ss WHERE ss.STUDENT_ID='"+studentAccountId+"' ) "
//                + "and DATEDIFF( st.VALID_TILL, CURRENT_DATE )>=0)";
//        
//        
//             System.out.println("native sql "+nativeSqlQuery);
//          List<Object> objects = this.service.getObjectsByNativeSqlQuery(nativeSqlQuery);
//                    for (Object object1 : objects) {
//                        Object[] objects1 = (Object[]) object1;
//                        JSONObject jSONObject = new JSONObject();
//                        jSONObject.put("syllabusId", objects1[0]);
//                        jSONObject.put("syllabusName", objects1[1]);
//                        list.add(jSONObject);
//                    }
//Sql query to insert new products in student_subscription table
//INSERT INTO student_subscriptions 
//          ( BATCH_ID,
//           DATE_OF_CREATION,
//           GRADE_ID,
//           STUDENT_ID,
//           SUBSCRIPTION_ID,
//           SYLLABUS_ID
//          
//          )
//     SELECT null
//          , CURRENT_TIMESTAMP,
//          GRADE_ID,
//         STUDENT_ID,
//          "SUBST000035",
//          "SYLL000039"
//  FROM student_subscriptions WHERE SYLLABUS_ID='SYLL000019'
//      FROM student_subscriptions WHERE SYLLABUS_ID='SYLL000019'
            String sql = "from SubscribeType st where st.subsTypeId in"
                    + "(select ss.subscribeId from StudentSubscription ss "
                    + "where ss.studentAccountId='" + studentAccountId + "') ";
            List<Object> SubscribeTypeObjects = this.service.getObject(sql);
            SubscribeType subscribeType = null;
            String[] activeSubId = null;
            for (Object object : SubscribeTypeObjects) {
                subscribeType = (SubscribeType) object;
                Date date1 = subscribeType.getValidTill();
                Date d1 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date1));//d2::: Thu Jan 24 00:00:00 IST 2019
                Timestamp date2 = new Timestamp(System.currentTimeMillis());
                Date d2 = MyDateFormate.parseDate1(MyDateFormate.dateToString1(date2));//d::: Thu Jan 24 00:00:00 IST 2019
                long diffDate = d1.getTime() - d2.getTime();
                if (diffDate >= 0) {

                    String sql2 = "from Syllabus where syllabusId ="
                            + "'" + subscribeType.getSyllabusId() + "'";
                    List<Object> objects = this.service.getObject(sql2);
                    for (Object object1 : objects) {
                        Syllabus syllabus = (Syllabus) object1;
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put("syllabusId", syllabus.getSyllabusId());
                        jSONObject.put("syllabusName", syllabus.getSyllabusName());
                        list.add(jSONObject);
                        String froc = "FREE ONLINE CLASSES ";
//                        String olympiadCrashCourse = "OLYMPIAD CRASH COURSE ";
                        if ("SYLL000015".equals(syllabus.getSyllabusId())) {
                            JSONObject jSONObject1 = new JSONObject();
                            jSONObject1.put("syllabusId", "SYLL000020");
                            jSONObject1.put("syllabusName", froc);

//                            JSONObject jSONObject2 = new JSONObject();
//                            jSONObject2.put("syllabusId", "SYLL000031");
//                            jSONObject2.put("syllabusName", olympiadCrashCourse);
                            list.add(jSONObject1);
//                            list.add(jSONObject2);
                        }
                        if ("SYLL000016".equals(syllabus.getSyllabusId())) {
                            JSONObject jSONObject1 = new JSONObject();
                            jSONObject1.put("syllabusId", "SYLL000021");
                            jSONObject1.put("syllabusName", froc);
//                            JSONObject jSONObject2 = new JSONObject();
//                            jSONObject2.put("syllabusId", "SYLL000032");
//                            jSONObject2.put("syllabusName", olympiadCrashCourse);

                            list.add(jSONObject1);
//                            list.add(jSONObject2);

                        }
                        if ("SYLL000017".equals(syllabus.getSyllabusId())) {
                            JSONObject jSONObject1 = new JSONObject();
                            jSONObject1.put("syllabusId", "SYLL000022");
                            jSONObject1.put("syllabusName", froc);

//                            JSONObject jSONObject2 = new JSONObject();
//                            jSONObject2.put("syllabusId", "SYLL000033");
//                            jSONObject2.put("syllabusName", olympiadCrashCourse);
                            list.add(jSONObject1);
//                            list.add(jSONObject2);
                        }
                        if ("SYLL000018".equals(syllabus.getSyllabusId())) {
                            JSONObject jSONObject1 = new JSONObject();
                            jSONObject1.put("syllabusId", "SYLL000023");
                            jSONObject1.put("syllabusName", froc);

//                            JSONObject jSONObject2 = new JSONObject();
//                            jSONObject2.put("syllabusId", "SYLL000034");
//                            jSONObject2.put("syllabusName", olympiadCrashCourse);
                            list.add(jSONObject1);
//                            list.add(jSONObject2);
                        }
                        if ("SYLL000019".equals(syllabus.getSyllabusId())) {
                            JSONObject jSONObject1 = new JSONObject();
                            jSONObject1.put("syllabusId", "SYLL000024");
                            jSONObject1.put("syllabusName", froc);

//                            JSONObject jSONObject2 = new JSONObject();
//                            jSONObject2.put("syllabusId", "SYLL000035");
//                            jSONObject2.put("syllabusName", olympiadCrashCourse);
                            list.add(jSONObject1);
//                            list.add(jSONObject2);
                        }

                    }
                }
            }

            Set<JSONObject> set = new HashSet<>(list);
            list.clear();
            list.addAll(set);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * Testing url for attachments
     *
     * @throws IOException
     */
//    @GetMapping(value = "/attachments")
//    public void getAttachment() throws IOException {
//        String fileName = "attachments/yolearn_-_how_it_works1.pdf";
//        String fileName2 = "attachments/guide_to_attend_the_class.mp4";
//
//        Resource resource = new ClassPathResource(fileName);
//        InputStream input = resource.getInputStream();
//
//        Resource resource2 = new ClassPathResource(fileName2);
//        InputStream input2 = resource2.getInputStream();
//        E_Mail_Sender_Account e_Mail_Sender_Account = new E_Mail_Sender_Account();
//        File file = resource.getFile();
//        File file2 = resource2.getFile();
//        String emailMsg = "Dear Parent,</b><br><br>"
//                //                                Your classes starts on 20th Jan 2020 and ends on 29th Feb 2020.
//                + "Thanks for choosing yolearn. You registered for the course successfully.<br>"
//                //                                + "Your classes starts on 20th Jan 2020 and ends on 29th Feb 2020.<br>"
//                //                                + "<b>Class Timings:.</b><br>"
//                //                                + "Respective class timings Weekly 6 days ( Monday to Saturday).<br><br>"
//                + "Parent login can be used by the parent to check child's tests, attendance reports, subscription details etc.<br>"
//                + "Whereas student login can be used by your child to attend the classes, assignments, tests etc...<br>"
//                + "Under the same parent login any number of students can be subscribed.<br>"
//                + "These classes will be conducted weekly 4days from Monday to Thursday. To check this month's schedule <br>"
//                + "plz login and check in the upcoming classes<br><br>"
//                + "Kindly go through the attachments to know<br>"
//                + "* yolearn - How it works and <br>"
//                + "*How to attend the class.<br><br>"
//                + "For any queries plz contact(0091) 7287885888.<br><br>"
//                + "We Wish your child a happy and safe learning.<br><br>"
//                + "<b>Best Regards,<b><br>"
//                + "YOLEARN Team.<br><br>";
//        Set<String> set = new HashSet();
//        set.add("vinaykumarma6@gmail.com");
////            e_Mail_Sender_Account.composeAndSends("subject", set, "message", input, file.getName());
//        e_Mail_Sender_Account.composeAndSends("subject", set, emailMsg, input, input2, file.getName(), file2.getName());
//
//        System.out.println("File Found : " + file.exists());
//
//    }
    @RequestMapping(value = {"/send-mail-to-no-payment"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> sendMailToNoPayment(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        try {
            String studentAccountId = mapBean.get("studentAccountId");
            String subsTypeId = mapBean.get("subsTypeId");

            Map<String, Object> map2 = new HashMap<>();
            map2.put("subsTypeId", subsTypeId);
            SubscribeType subscribeType = (SubscribeType) service.getObject(SubscribeType.class, map2).get(0);
            if (subscribeType != null) {
                Object[] studentProperties = service.loadProperties(String.format("%s", "SELECT s.firstName, s.lastName, s.primaryEmail, s.password FROM StudentAccount s WHERE s.studentAccountId = '" + studentAccountId + "'")).get(0);
                String firstName = (String) studentProperties[0];
                String lastName = (String) studentProperties[1];
                String primaryEmail = (String) studentProperties[2];
                String pass = (String) studentProperties[3];

                String subject = "YOLEARN Registration Confirmation";
                String fullName = firstName + " " + lastName;
                /*sending email to the user*/
                String emailMsg = "Hi <b>" + fullName + ",</b><br><br>"
                        + "Your registration has been done.<br><br>"
                        + "<b>Login details:</b><br>"
                        + "<table>"
                        + "<tr><td>Email</td><td> : " + primaryEmail + "</td></tr>"
                        + "<tr><td>Student Password</td><td> : " + pass + "</td></tr>"
                        + "<tr><td>Program</td><td> : " + subscribeType.getSubscriptionName() + "</td></tr>"
                        + "<tr><td>Program Summary</td><td> : " + subscribeType.getDescription() + "</td></tr>"
                        + "</table><br><br>"
                        + "<small>For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
                        + "You received this message because this email address was used to register you in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                        + "<b>Yours Sincerely,</b><br>"
                        + "YOLEARN Team.<br><br>"
                        + "Thanks for choosing Yolearn";

                Set<String> to1 = new HashSet<>();
                to1.add(primaryEmail);
                javaMail_Sender_Info.composeAndSend(subject, to1, emailMsg);
                json.put("msg", "Email sent");
            } else {

                json.put("msg", "No subscription found");

            }

        } catch (MessagingException exception) {
            json.put("msg", "something went wrong");
            exception.printStackTrace();
        } catch (Exception exception2) {
            json.put("msg", "something went wrong");
            exception2.printStackTrace();
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    /**
     *
     */
    @RequestMapping(method = RequestMethod.POST, value = "/listOfStudents2")
    public @ResponseBody
    JSONObject listOfStudentsWithNativeSqlQuery(@RequestBody final Map<String, Object> filter) {

        JSONObject response = new JSONObject();
        List<Object> studentsDetails = new ArrayList<>();

        List<String> gradeFilters = (List<String>) (Object) filter.get("gradeFilter");
        String dateOrder = (String) filter.get("dateOrder");
        List<String> programFilters = (List<String>) (Object) filter.get("ProgramFilter");
        String text = (String) filter.get("text");
//        String gradeOrder = (String) filter.get("gradeOrder");
        String gradeOrder = "asc";
        /*optional filter-variables*/
        Map<String, Object> dateRange = (Map<String, Object>) filter.get("dateRange");
        String startDate = (String) dateRange.get("startDate");
        String endDate = (String) dateRange.get("endDate");

        /*fixed filter-variables*/
        int pageNo = (Integer) filter.get("pageNo");
        int maxRes = 10;
        if (filter.get("maxResult") != null) {
            maxRes = (Integer) filter.get("maxResult");
        }
        FilterUtility filterUtility = new FilterUtility();

        String sqlCountQuery = "select count(*) FROM STUDENT_ACCOUNT s  inner join GRADE_TAB g on s.`GRADE_ID`=g.`GRADE_ID` ";

        String sqlQuery = "SELECT distinct s.`STUDENT_ID`,s.`ADDRESS`,s.`BATCH_ID`,s.`CITY`,s.`COUNTRY_CODE`,s.`DATE_OF_CREATION`,\n"
                + "s.`FIRST_NAME`,g.`GRADE_ID`,s.`LAST_NAME`,s.`PARENT_ID`,s.`PASSWORD`,s.`PRIMARY_EMAIL`,s.`SCHOOL_NAME`\n"
                + ",s.`STATUS`,s.`SUBSCRIPTION_ID`,s.`SYLLABUS_ID`,s.`ACCOUNT_ID`\n"
                + "  FROM STUDENT_ACCOUNT s  inner join GRADE_TAB g on s.`GRADE_ID`=g.`GRADE_ID`";

        if (gradeFilters != null) {
            sqlQuery = String.format("%s", sqlQuery + " where s.`GRADE_ID` in (" + filterUtility.getIN_CLAUSE_VALUES(gradeFilters) + ")");
            sqlCountQuery = String.format("%s", sqlCountQuery + " where s.`GRADE_ID` in (" + filterUtility.getIN_CLAUSE_VALUES(gradeFilters) + ")");

        }
        if (programFilters != null) {
            System.out.println("inside program filters");

            sqlQuery = String.format("%s", sqlQuery + " AND s.`SYLLABUS_ID`in (" + filterUtility.getIN_CLAUSE_VALUES(programFilters) + ")");
            sqlCountQuery = String.format("%s", sqlCountQuery + " AND s.`SYLLABUS_ID`in (" + filterUtility.getIN_CLAUSE_VALUES(programFilters) + ")");
        }

        if ("all".equals(startDate) && !"all".equals(endDate)) {
            sqlQuery = sqlQuery + " and DATE(s.DATE_OF_CREATION) <  '" + endDate + "'";
            sqlCountQuery = sqlCountQuery + " and DATE(s.DATE_OF_CREATION) <  '" + endDate + "'";
        }
        if (!"all".equals(startDate) && "all".equals(endDate)) {
            sqlQuery = sqlQuery + " and DATE(s.DATE_OF_CREATION) between  '" + startDate + "' AND current_date() ";
            sqlCountQuery = sqlCountQuery + " and DATE(s.DATE_OF_CREATION) between  '" + startDate + "' AND current_date() ";
        }

        if (!"all".equals(startDate) && !"all".equals(endDate)) {
            sqlQuery = sqlQuery + " and DATE(s.DATE_OF_CREATION) between  '" + startDate + "' AND '" + endDate + "' ";
            sqlCountQuery = sqlCountQuery + " and DATE(s.DATE_OF_CREATION) between  '" + startDate + "' AND '" + endDate + "' ";
        }

        if (text != null) {
            sqlQuery = String.format("%s", sqlQuery + " and (s.FIRST_NAME LIKE '" + text + "%'"
                    + " or s.LAST_NAME LIKE '" + text + "%' or s.PRIMARY_EMAIL LIKE '" + text + "%' or s.SCHOOL_NAME LIKE '" + text + "%'");
            sqlCountQuery = String.format("%s", sqlCountQuery + " and (s.FIRST_NAME LIKE '" + text + "%'"
                    + " or s.LAST_NAME LIKE '" + text + "%' or s.PRIMARY_EMAIL LIKE '" + text + "%' or s.SCHOOL_NAME LIKE '" + text + "%')");
        }

        sqlQuery = sqlQuery + ") order by s.DATE_OF_CREATION " + dateOrder + " ";
////        sqlQuery = sqlQuery + ") order by cast(g.`GRADE_NAME` as unsigned) " + gradeOrder + ",s.DATE_OF_CREATION " + dateOrder + " ";
//        sqlQuery = sqlQuery + ") order by s.DATE_OF_CREATION " + dateOrder + ",cast(g.`GRADE_NAME` as unsigned) " + gradeOrder + "";

        sqlQuery = sqlQuery + " limit " + pageNo + "," + maxRes + "";

        System.out.println("sqlQuery  " + sqlQuery);

        List<Object> listOfStudentsNative = this.service.getObjectsByNativeSqlQuery(sqlQuery);
        /*getting sql queries for 'count of students' and 'list of students'*/
        Map<String, String> mapFilterSQL = new FilterUtility().listOfStudentsFilter(gradeFilters, dateRange, dateOrder, text, programFilters, gradeOrder);

        /*getting total count of students*/
        int count = (int) service.countObject(String.format("%s", mapFilterSQL.get("SQLcountStudentAccount")));

        long noOfRecordsRetrieved = this.service.countObject("select count(*) " + mapFilterSQL.get("SQLlistStudentAccount"));

        for (Object object : listOfStudentsNative) {
            JSONObject json = new JSONObject();
            Object[] objects = (Object[]) object;
            json.put("userRole", "student");
            json.put("firstName", objects[6]);
            json.put("lastName", objects[8]);
            json.put("fullName", objects[6].toString() + " " + objects[8].toString());

            json.put("primaryEmail", objects[11]);
            json.put("password", objects[10]);
            json.put("countryCode", objects[4]);
            json.put("accountId", objects[0]);
            json.put("status", objects[13]);
            json.put("dateOfCreation", MyDateFormate.dateToString((Date) objects[5]));
            json.put("dateOfCreationFormat", objects[5]);
            json.put("schoolName", objects[12]);

            json.put("gradeId", objects[7]);
            json.put("syllabusId", objects[15]);
            json.put("address", objects[1]);
            json.put("city", objects[3]);
            json.put("subsTypeId", objects[14]);
            json.put("batchId", objects[2]);

            Object[] loadParentProperties = service.loadProperties(String.format("%s", "SELECT p.firstName, p.lastName, p.mobileNum, g.gradeName, s.syllabusName FROM "
                    + "ParentAccount p, Grade g, Syllabus s WHERE "
                    + "p.registration.accountId = '" + objects[9] + "' AND "
                    + "g.gradeId = '" + objects[7] + "' AND "
                    + "s.syllabusId = '" + objects[15] + "'")).get(0);
            String parentFirstName = (String) loadParentProperties[0];
            String parentLastName = (String) loadParentProperties[1];
            long mobileNumber = (Long) loadParentProperties[2];
            String gradeName = (String) loadParentProperties[3];
            String syllabusName = (String) loadParentProperties[4];

            json.put("parentFirstName", parentFirstName);
            json.put("parentLastName", parentLastName);
            json.put("parentFullName", String.format("%s", parentFirstName + " " + parentLastName));
            json.put("mobileNumber", mobileNumber);
            json.put("gradeName", gradeName);
            json.put("syllabusName", syllabusName);

            if (listOfImagesNameExistOrNot(objects[0] + ".jpg")) {
                json.put("image", "/UserImages/" + objects[0] + ".jpg");
            } else {
                json.put("image", null);
            }
            studentsDetails.add(json);
        }
        response.put("no of records displaying in this page", noOfRecordsRetrieved);
        response.put("count", count);
        response.put("listOfStudents", studentsDetails);

        return response;
    }
}
