package com.maps.yolearn.controller.liveclass;

import com.maps.yolearn.bean.filter.CalenderBean;
import com.maps.yolearn.bean.filter.FilterBean;
import com.maps.yolearn.bean.liveclass.BatchBean;
import com.maps.yolearn.bean.liveclass.BatchIdToStudents;
import com.maps.yolearn.bean.liveclass.LiveMetaData;
import com.maps.yolearn.model.liveclass.*;
import com.maps.yolearn.service.EntityService;
import com.maps.yolearn.util.date.MyDateFormate;
import com.maps.yolearn.util.filter.FilterUtility;
import com.maps.yolearn.util.mail.E_Mail_Sender_info;
import com.maps.yolearn.util.primarykey.CustomPKGenerator;
import com.maps.yolearn.util.sms.SendingMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author KOTARAJA
 * @author PREMNATH
 * @author VINAYKUMAR
 */
@RestController
@RequestMapping(value = {"/liveclass"})
@CrossOrigin(origins = "*", maxAge = 3600)


public class LiveClassController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LiveClassController.class);
    E_Mail_Sender_info javaMail_Sender_Info = new E_Mail_Sender_info();
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EntityService service;
    @Autowired
    private CustomPKGenerator pKGenerator;
    @Value("${zoom.jwt.token}")
    private String zoomToken;
    @Value("${zoom.api.create.meeting}")
    private String creatingMeetingURL;

    public JSONObject getClassSchedulerJson(ClassScheduler classScheduler) {
        JSONObject json = new JSONObject();
        json.put("extend", classScheduler.getCanExtend());
        json.put("classId", classScheduler.getClassID());
        json.put("endTime", classScheduler.getDurationinMinutes());
        json.put("forceExitParticipants", classScheduler.getForceExitParticipants());
        json.put("guestUrl", classScheduler.getGuestUrl());
        json.put("isRecordedSessionViewable", classScheduler.getIsRecordedSessionViewable());
        json.put("prepareUrl", classScheduler.getPrepareUrl());
        json.put("presentUrl", classScheduler.getPresentUrl());
        json.put("presenterDisplayName", classScheduler.getPresenterDisplayName());
        json.put("presenterUniqueName", classScheduler.getPresenterUniqueName());
        json.put("replayUrl", classScheduler.getReplayUrl());
        json.put("restartSession", classScheduler.getRestartSession());
        json.put("scheduledDateWithformate", MyDateFormate.dateToString2(classScheduler.getScheduledDate()));
        json.put("scheduledDate", classScheduler.getScheduledDate().getTime());
        json.put("sessionId", classScheduler.getSessionId());
        json.put("title", classScheduler.getTitle());
        json.put("accessTo", classScheduler.getAccessTo());
        json.put("avgRating", classScheduler.getAvgRating());
        json.put("chapterId", classScheduler.getChapterId());
        json.put("chapterName", classScheduler.getChapterName());
        json.put("gradeName", classScheduler.getGradeName());
        json.put("gradeId", classScheduler.getGradeId());
        json.put("subjectName", classScheduler.getSubjectName());
        json.put("subjectId", classScheduler.getSubjectId());
        json.put("syllabusName", classScheduler.getSyllabusName());
        json.put("syllabusId", classScheduler.getSyllabusId());
        json.put("noOfSeats", classScheduler.getNoOfSeats());
        json.put("teacherId", classScheduler.getTeacherId());
        json.put("batchId", classScheduler.getBatchId());
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createLiveSession")
    public @ResponseBody
    JSONObject createLiveSession(@RequestBody LiveMetaData bean) {
        JSONObject jsono = new JSONObject();
        try {
            //TimeZone tz = TimeZone.getTimeZone("IST");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //sdf.setTimeZone(tz);
            String current = sdf.format(new Date());
//            Date current = MyDateFormate.stringToDate(currents);

//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
//            df.setTimeZone(tz);
//            Date current = MyDateFormate.stringToDateISO(String.format("%s", df.format(new Date())));
            ClassScheduler classScheduler = new ClassScheduler();
            String classId = (String) pKGenerator.generate(ClassScheduler.class, "CLASS_");
            classScheduler.setClassID(classId);
            classScheduler.setCanExtend(bean.getCanExtend());
            classScheduler.setDurationinMinutes(bean.getDurationinMinutes());
            classScheduler.setForceExitParticipants(bean.getForceExitParticipants());
            classScheduler.setIsRecordedSessionViewable(bean.getIsRecordedSessionViewable());
            classScheduler.setPresenterDisplayName(bean.getPresenterDisplayName());
            classScheduler.setPresenterUniqueName(bean.getPresenterUniqueName());
            classScheduler.setRestartSession(bean.getRestartSession());
            classScheduler.setScheduledDate(MyDateFormate.stringToDate(bean.getScheduledDate()));

            classScheduler.setEndDate(MyDateFormate.stringToDate(bean.getEndDate()));
            classScheduler.setTeacherId((bean.getTeacherId()));
            classScheduler.setTitle(bean.getTitle());
            classScheduler.setSessionId(bean.getSessionId());
            classScheduler.setSubscriptionId(bean.getSubscriptionId());
            classScheduler.setPrepareUrl(bean.getPrepareUrl());
            classScheduler.setPresentUrl(bean.getPresentUrl());
            classScheduler.setGuestUrl(bean.getGuestUrl());
            classScheduler.setReplayUrl(bean.getReplayUrl());
            classScheduler.setGradeId(bean.getGradeId());
            classScheduler.setSyllabusId(bean.getSyllabusId());
            classScheduler.setSubjectId(bean.getSubjectId());
            classScheduler.setAccessTo(bean.getAccessTo());
            classScheduler.setAvgRating(0);
            classScheduler.setNoOfSeats(20);
            classScheduler.setChapterId(bean.getChapterId());
            classScheduler.setBatchId(bean.getBatchId());

            String chapterName = (String) service.getObject(String.format("%s", "SELECT c.chapterName FROM Chapter c WHERE c.chapterId = '" + bean.getChapterId() + "'")).get(0);
            classScheduler.setChapterName(chapterName);

            String subjectName = (String) service.getObject(String.format("%s", "SELECT s.subjectName FROM Subject s WHERE s.subjectId = '" + bean.getSubjectId() + "'")).get(0);
            classScheduler.setSubjectName(subjectName);

            String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + bean.getSyllabusId() + "'")).get(0);
            classScheduler.setSyllabusName(syllabusName);

            String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + bean.getGradeId() + "'")).get(0);
            classScheduler.setGradeName(gradeName);

            List<Object> classSchedulerList_ByTeacherId = service.getObject(String.format("%s", "FROM ClassScheduler c WHERE c.scheduledDate > '" + current + "' AND c.teacherId = '" + bean.getTeacherId() + "'"));

            if (bean.getBatchId().length() > 0) {
                List<Object> classSchedulerList_ByBatchId = service.getObject(String.format("%s", "FROM ClassScheduler c WHERE c.scheduledDate > '" + current + "'  AND c.batchId = '" + bean.getBatchId() + "'"));

                if (checkTimeConflict(classSchedulerList_ByTeacherId, bean).contains(true) || checkTimeConflict(classSchedulerList_ByBatchId, bean).contains(true)) {
                    jsono.put("msg", "Slot is not available for this timing.");
                } else {
                    jsono = createClass(bean, subjectName, classScheduler, classId);
                }
            } else {
                if (checkTimeConflict(classSchedulerList_ByTeacherId, bean).contains(true)) {
                    jsono.put("msg", "Slot is not available for this timing.");
                } else {
                    jsono = createClass(bean, subjectName, classScheduler, classId);
                }
            }

        } catch (ParseException | MessagingException e) {
            jsono.put("msg", "somthing went wrong.try Again !" + e.getMessage());
        }

        return jsono;
    }

    public JSONObject createClass(LiveMetaData bean, String subjectName, ClassScheduler classScheduler, String classId) throws ParseException, MessagingException {
        JSONObject jsono = new JSONObject();

        String admin = "admin@yolearn.com";
        String info = "info@yolearn.com";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = sdf.parse(bean.getScheduledDate());
        Long longDate = date.getTime();
        String scheduledDateString = MyDateFormate.getTimeBasedOnTimeZone(longDate);

        String s = "SELECT t.registration.mailSubscriptionStatus, t.firstName, t.primaryEmail, t.mobileNum FROM TeacherAccount t WHERE t.teacherAccountId = '" + bean.getTeacherId() + "'";
        List<Object[]> teacherAccountProperties = service.loadProperties(s);
        if (teacherAccountProperties.size() > 0) {
            Object[] objects = teacherAccountProperties.get(0);
            boolean b = (boolean) objects[0];
            String teacherFName = (String) objects[1];
            String teacherPrimaryEmail = (String) objects[2];
            Long teacherMobileNum = (Long) objects[3];

            String save = service.saveOrUpdate(classScheduler);
            if (save.length() > 0) {
                if (!b) {

                    /*sending mail to teacher*/
                    String subject1 = "YOLEARN - Class Confirmation";
                    String emailMsg = "Hi <b>" + teacherFName + "</b><br><br>"
                            + "Your class has been scheduled successfully!<br><br>"
                            + "<b>Class Details:</b><br><br>"
                            + "<table>"
                            + "<tr><td>Subject Name</td><td> : " + subjectName + "</td></tr>"
                            + "<tr><td>Title</td><td> : " + bean.getTitle() + "</td></tr>"
                            + "<tr><td>Schedule Date</td><td> : " + scheduledDateString + "</td></tr>"
                            + "</table><br><br>"
                            + "<b>Yours Sincerely,</b><br>"
                            + "YOLEARN Team.<br><br>"
                            + "Thanks for choosing Yolearn";
                    Set<String> to1 = new HashSet<>();
                    to1.add(teacherPrimaryEmail);
                    javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg);
                }

                /*sending mail to admin and info*/
                String subject2 = "Class Confirmation From YoLearn";
                String emailMsg2 = "Hi,<br><br>"
                        + "Live class has been scheduled successfully from <b>YO</b>LEARN!<br><br>"
                        + "<b>Live Class Details:</b><br>"
                        + "<table>"
                        + "<tr><td>Subject Name</td><td> : " + subjectName + "</td></tr>"
                        + "<tr><td>Title</td><td> : " + bean.getTitle() + "</td></tr>"
                        + "<tr><td>Schedule Date</td><td> : " + scheduledDateString + "</td></tr>"
                        + "</table><br><br>"
                        + "<b>Yours Sincerely,</b><br>"
                        + "YOLEARN Team.";

                String number = Long.toString(teacherMobileNum);
                SendingMessage class2 = new SendingMessage();
                class2.SMSSender("269441A7NKDGP0wO5c9b1726", number, "Hi " + teacherFName + "," + "\n Your class has been scheduled successfully!" + "\n  Class Details: " + "\n Subject Name - " + subjectName + "\n title - " + bean.getTitle() + "\n Schedule Date - " + scheduledDateString + "\n Thank You-YOLEARN", "YOLEAR", "91", "0", "4");

                Set<String> to12 = new HashSet<>();
                to12.add(admin);
                to12.add(info);
                javaMail_Sender_Info.composeAndSend(subject2, to12, emailMsg2);

                jsono.put("liveClassID", classId);
                jsono.put("teacherId", bean.getTeacherId());
                jsono.put("sessionId", bean.getSessionId());
                jsono.put("subsTypeId", bean.getSubscriptionId());
                jsono.put("fileName", bean.getFileName());
                jsono.put("accessTo", bean.getAccessTo());
                jsono.put("msg", "class scheduled succesfully");

            } else {
                jsono.put("msg", "class schedule failed");
            }
        } else {
            jsono.put("msg", "miss match teacherAccountId in teacherAccount class !");
        }
        return jsono;
    }

    public List<Boolean> checkTimeConflict(List<Object> list, LiveMetaData bean) {
        List<Boolean> bList1 = new ArrayList<>();

        if (list.size() > 0) {
            for (Object objects : list) {

                ClassScheduler cs = (ClassScheduler) objects;

                Date start_db = cs.getScheduledDate();
                Date end_db = cs.getEndDate();

                Date start_Input = MyDateFormate.stringToDate(bean.getScheduledDate());
                Date end_Input = MyDateFormate.stringToDate(bean.getEndDate());

                boolean checkStart_inputDate = MyDateFormate.checkBetween(start_Input, start_db, end_db);
                boolean checkEnd_inputDate = MyDateFormate.checkBetween(end_Input, start_db, end_db);

                bList1.add(checkStart_inputDate);
                bList1.add(checkEnd_inputDate);
            }
        }

        return bList1;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateLiveSession")
    public @ResponseBody
    JSONObject updateLiveSession(@RequestBody final LiveMetaData bean) {
        JSONObject json = new JSONObject();
        String admin = "admin@yolearn.com";
        String info = "info@yolearn.com";

        try {
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
// Added by Moin for date schedule fix at backend
            SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = sdf.parse(bean.getScheduledDate());
            String scheduledDateString = sdf1.format(date);
            // Date schDate = sdf1.parse();


            /** Date date = sdf.parse(bean.getScheduledDate());
             Long longDate = date.getTime();
             String scheduledDateString = MyDateFormate.getTimeBasedOnTimeZone(longDate);**/

            List<Object> ClassSchedulerobj = service.getObject("FROM ClassScheduler c WHERE c.sessionId = '" + bean.getSessionId() + "'");
            if (ClassSchedulerobj.size() > 0) {
                ClassScheduler classScheduler = (ClassScheduler) ClassSchedulerobj.get(0);

                List<Object[]> classSchedulerProperties = service.loadProperties(String.format("%s", "SELECT c.scheduledDate, c.endDate FROM ClassScheduler c WHERE c.teacherId = '" + classScheduler.getTeacherId() + "'"));

                if (classSchedulerProperties.size() > 0) {
                    List<Boolean> bList = new ArrayList<>();
                    classSchedulerProperties.forEach((objects) -> {
                        Date start = (Date) objects[0];
                        Date end = (Date) objects[1];

                        Date Ui = MyDateFormate.stringToDate(bean.getScheduledDate());
                        Date EndUi = MyDateFormate.stringToDate(bean.getEndDate());

                        boolean b = MyDateFormate.checkBetween(Ui, start, end);
                        boolean b1 = MyDateFormate.checkBetween(EndUi, start, end);

                        bList.add(b);
                        bList.add(b1);
                    });

                    if (bList.contains(true)) {
                        json.put("msg", "Teacher is already scheduled with this timing.");
                    } else {
                        classScheduler.setDurationinMinutes(bean.getDurationinMinutes());
                        classScheduler.setPresenterDisplayName(bean.getPresenterDisplayName());
                        classScheduler.setPresenterUniqueName(bean.getPresenterUniqueName());
                        classScheduler.setScheduledDate(MyDateFormate.stringToDate(bean.getScheduledDate()));

                        classScheduler.setEndDate(MyDateFormate.stringToDate(bean.getEndDate()));
                        classScheduler.setTeacherId((bean.getTeacherId()));
                        classScheduler.setTitle(bean.getTitle());
                        classScheduler.setCanExtend(bean.getCanExtend());
                        classScheduler.setIsRecordedSessionViewable(bean.getIsRecordedSessionViewable());
                        classScheduler.setAccessTo(bean.getAccessTo());
                        classScheduler.setChapterId(bean.getChapterId());
                        classScheduler.setSubjectId(bean.getSubjectId());
                        classScheduler.setSyllabusId(bean.getSyllabusId());
                        classScheduler.setGradeId(bean.getGradeId());
                        classScheduler.setBatchId(bean.getBatchId());

                        String chapterName = (String) service.getObject(String.format("%s", "SELECT c.chapterName FROM Chapter c WHERE c.chapterId = '" + bean.getChapterId() + "'")).get(0);
                        classScheduler.setChapterName(chapterName);

                        String subjectName = (String) service.getObject(String.format("%s", "SELECT s.subjectName FROM Subject s WHERE s.subjectId = '" + bean.getSubjectId() + "'")).get(0);
                        classScheduler.setSubjectName(subjectName);

                        String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + bean.getSyllabusId() + "'")).get(0);
                        classScheduler.setSyllabusName(syllabusName);

                        String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + bean.getGradeId() + "'")).get(0);
                        classScheduler.setGradeName(gradeName);

                        String s = "SELECT t.registration.mailSubscriptionStatus, t.firstName, t.primaryEmail, t.mobileNum FROM TeacherAccount t WHERE t.teacherAccountId = '" + bean.getTeacherId() + "'";
                        List<Object[]> teacherAccountProperties = service.loadProperties(s);

                        if (teacherAccountProperties.size() > 0) {
                            Object[] objects = teacherAccountProperties.get(0);
                            boolean b = (boolean) objects[0];
                            String teacherFName = (String) objects[1];
                            String teacherPrimaryEmail = (String) objects[2];
                            Long teacherMobileNum = (Long) objects[3];

                            int x = service.update(classScheduler);
                            if (x > 0) {
                                if (!b) {
                                    String subject1 = "YOLEARN - Update Class";
                                    String emailMsg = "Hi <b>" + teacherFName + "</b><br><br>"
                                            + "Your class has been updated successfully!<br><br>"
                                            + "<b>Class Details:</b><br>"
                                            + "<table>"
                                            + "<tr><td>Subject Name</td><td> : " + subjectName + "</td></tr>"
                                            + "<tr><td>Title</td><td> : " + bean.getTitle() + "</td></tr>"
                                            + "<tr><td>Schedule Date</td><td> : " + MyDateFormate.dateToString(classScheduler.getScheduledDate()) + "</td></tr>"
                                            + "</table><br><br>"
                                            + "<b>Yours Sincerely,</b><br>"
                                            + "YOLEARN Team.<br><br>"
                                            + "Thanks for choosing Yolearn";
                                    Set<String> to1 = new HashSet<>();
                                    to1.add(teacherPrimaryEmail);
                                    javaMail_Sender_Info.composeAndSend(subject1, to1, emailMsg);//sending mail teacher
                                }
                                String subject2 = "Update  Class From YoLearn";
                                String emailMsg1 = "Hi,<br><br>"
                                        + "Live Class  has been updated by admin successfully!<br><br>"
                                        + "<b>Updated Class Details:</b><br>"
                                        + "<table>"
                                        + "<tr><td>Subject Name</td><td> : " + subjectName + "</td></tr>"
                                        + "<tr><td>Title</td><td> : " + bean.getTitle() + "</td></tr>"
                                        + "<tr><td>Schedule Date</td><td> : " + MyDateFormate.dateToString(classScheduler.getScheduledDate()) + "</td></tr>"
                                        + "</table><br><br>"
                                        + "<b>Yours Sincerely,</b><br>"
                                        + "YOLEARN Team.<br><br>"
                                        + "Thanks for choosing Yolearn";
                                SendingMessage class2 = new SendingMessage();
                                String number = Long.toString(teacherMobileNum);
                                class2.SMSSender("269441A7NKDGP0wO5c9b1726", number, "Hi " + teacherFName + "," + "\n Your class has been updated successfully!" + "\n  Class Details: " + "\n Subject Name - " + subjectName + "\n title - " + bean.getTitle() + "\n Schedule Date - " + scheduledDateString + "\n Thank You-YOLEARN", "YOLEAR", "91", "0", "4");

                                Set<String> to11 = new HashSet<>();
                                to11.add(admin);
                                to11.add(info);
                                javaMail_Sender_Info.composeAndSend(subject2, to11, emailMsg1);// sending mail to admin and info
                                json.put("msg", "updated class sucessfully");

                            } else {
                                json.put("msg", "not updated class ");
                            }
                        } else {
                            json.put("msg", "teacherAccountId is mis matching TeacherAccount class ");
                        }
                    }
                } else {
                    json.put("msg", "Class schedular is mis matching TeacherAccount class ");
                }
            } else {
                json.put("msg", "Session Id not present ");
            }
        } catch (ParseException e) {
            json.put("msg", "Error " + e.getMessage());
        } catch (MessagingException ex) {
            Logger.getLogger(LiveClassController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getallschedular")
    public @ResponseBody
    JSONObject getallschedular(@RequestBody final FilterBean filter) {
        String accessTo = filter.getAccessTo();
        if (accessTo.equals("all")) {
            accessTo = null;
        }
        String dateOrder = filter.getDateOrder();
        String presenterDisplayName = filter.getTeacher();
        if (presenterDisplayName.equals("all")) {
            presenterDisplayName = null;
        }

        List<String> gradeFilters = filter.getGradeFilter();
        if (gradeFilters.get(0).equals("all")) {
            gradeFilters = null;
        }

        List<String> syllabusFilters = filter.getSyllabusFilter();
        if (syllabusFilters.get(0).equals("all")) {
            syllabusFilters = null;
        }

        List<String> subjectFilters = filter.getSubjectFilter();
        if (subjectFilters.get(0).equals("all")) {
            subjectFilters = null;
        }

        List<String> chapterFilters = filter.getChapterFilter();
        if (chapterFilters.get(0).equals("all")) {
            chapterFilters = null;
        }

        String startDate = (String) filter.getStartDate();
        if (startDate.equals("all")) {
            startDate = null;
        }

        String endDate = (String) filter.getEndDate();
        if (endDate.equals("all")) {
            endDate = null;
        }

        String text = filter.getText();

        int pageNo = Integer.parseInt(filter.getPageNo());
        int maxResult = 10;
        if (filter.getMaxResult() != null) {
            maxResult = Integer.parseInt(filter.getMaxResult());
        }

        ClassScheduler classScheduler;

        JSONObject j = new JSONObject();

        ArrayList<Object> recordedClassList = new ArrayList<>();
        ArrayList<Object> upcomingClassList = new ArrayList<>();
        ArrayList<Object> liveClassList = new ArrayList<>();

        int countRecorded = 0;
        int countUpcoming = 0;
        int countLive = 0;
        int recordedTotalCount = 0;
        int upcomingTotalCount = 0;
        int liveTotalCount = 0;
        //TimeZone tz = TimeZone.getTimeZone("IST");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
        //df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
//        Date currentDate = MyDateFormate.stringToDate(nowAsISO);

        try {
            Map<String, String> mapFilterSQL = new FilterUtility().getAllschedularFilter(accessTo, presenterDisplayName, gradeFilters, syllabusFilters, subjectFilters, chapterFilters, startDate, endDate, nowAsISO, dateOrder, text);
            countRecorded = (int) service.countObject(mapFilterSQL.get("SQLcountRecorded"));
            List<Object> listRecordedObj = service.loadByLimit(mapFilterSQL.get("SQLRecorded"), (pageNo * maxResult), maxResult);
            List<Object> listUpcomingObj = service.getObject(mapFilterSQL.get("SQLlistUpcoming"));
            List<Object> listLiveObj = service.getObject(mapFilterSQL.get("SQLlistLive"));

            countUpcoming = (int) service.countObject(mapFilterSQL.get("SQLUpcomingCount"));
            countLive = (int) service.countObject(mapFilterSQL.get("SQLiveCount"));

            recordedTotalCount = (int) service.countObject(mapFilterSQL.get("SQLRecordedTotalCount"));
            upcomingTotalCount = (int) service.countObject(mapFilterSQL.get("SQLUpcomingTotalCount"));
            liveTotalCount = (int) service.countObject(mapFilterSQL.get("SQLiveTotalCount"));

            /*list of recorded classes*/
            if (listRecordedObj.size() > 0) {
                for (Object object : listRecordedObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);

                    int countRating = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Rating r WHERE r.sesionId = '" + classScheduler.getSessionId() + "'"));
                    if (countRating > 0) {
                        json.put("totalNumOfStudentForRating", countRating);
                    } else {
                        json.put("totalNumOfStudentForRating", 0);
                    }

                    recordedClassList.add(json);
                }
            }

            /*list of upcoming classes*/
            if (listUpcomingObj.size() > 0) {
                for (Object object : listUpcomingObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);
                    upcomingClassList.add(json);
                }
            }

            /*list of live classes*/
            if (listLiveObj.size() > 0) {
                for (Object object : listLiveObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);
                    liveClassList.add(json);
                }
            }
        } catch (Exception e) {
        }
        j.put("no of upcomingClasses displaying in this page", countUpcoming);
        j.put("upcomingClassList", upcomingClassList);
        j.put("upcomingClassTotalCount", upcomingTotalCount);

        j.put("no of recordedClasses displaying in this page", countRecorded);
        j.put("recordedClassList", recordedClassList);
        j.put("recordedClassTotalCount", recordedTotalCount);

        j.put("no of LiveClasses displaying in this page", countLive);
        j.put("liveClassList", liveClassList);
        j.put("liveClassTotalCount", liveTotalCount);

        return j;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upcomingClassesForAdminWithFilter")
    public @ResponseBody
    JSONObject upcomingClassesForAdminWithFilter(@RequestBody final FilterBean filter) {

        String accessTo = filter.getAccessTo();
        if (accessTo.equals("all")) {
            accessTo = null;
        }
        String dateOrder = filter.getDateOrder();
        String presenterDisplayName = filter.getTeacher();
        if (presenterDisplayName.equals("all")) {
            presenterDisplayName = null;
        }

        List<String> gradeFilters = filter.getGradeFilter();
        if (gradeFilters.get(0).equals("all")) {
            gradeFilters = null;
        }

        List<String> syllabusFilters = filter.getSyllabusFilter();
        if (syllabusFilters.get(0).equals("all")) {
            syllabusFilters = null;
        }

        List<String> subjectFilters = filter.getSubjectFilter();
        if (subjectFilters.get(0).equals("all")) {
            subjectFilters = null;
        }

        List<String> chapterFilters = filter.getChapterFilter();
        if (chapterFilters.get(0).equals("all")) {
            chapterFilters = null;
        }

        String startDate = (String) filter.getStartDate();
        if (startDate.equals("all")) {
            startDate = null;
        }

        String endDate = (String) filter.getEndDate();
        if (endDate.equals("all")) {
            endDate = null;
        }

        int pageNo = Integer.parseInt(filter.getPageNo());
        int maxResult = 10;
        if (filter.getMaxResult() != null) {
            maxResult = Integer.parseInt(filter.getMaxResult());
        }

        ArrayList<Object> upcomingClassList = new ArrayList<>();

        ClassScheduler classScheduler;

        JSONObject j = new JSONObject();
        FilterUtility filterUtility = new FilterUtility();
        //TimeZone tz = TimeZone.getTimeZone("IST");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
        //df.setTimeZone(tz);
        String currentDate = df.format(new Date());
        long TotalCount = 0;
        long retrievedResultCount = 0;
        try {

            String SQLlistUpcoming = "FROM ClassScheduler c WHERE c.scheduledDate >= '" + currentDate + "'";

            if (gradeFilters != null) {
                SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.gradeId IN (" + filterUtility.getIN_CLAUSE_VALUES(gradeFilters) + ")");
            }

            if (syllabusFilters != null) {
                SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.syllabusId IN (" + filterUtility.getIN_CLAUSE_VALUES(syllabusFilters) + ")");
            }

            if (subjectFilters != null) {
                SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.subjectId IN (" + filterUtility.getIN_CLAUSE_VALUES(subjectFilters) + ")");
            }

            if (chapterFilters != null) {
                SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.chapterId IN (" + filterUtility.getIN_CLAUSE_VALUES(chapterFilters) + ")");
            }
            if (presenterDisplayName != null) {
                SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.presenterDisplayName = '" + presenterDisplayName + "'");
            }

            if (startDate == null && endDate != null) {
                SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " and DATE(c.scheduledDate) <  '" + endDate + "'");
            }
            if (startDate != null && endDate == null) {
                SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " and DATE(c.scheduledDate) between  '" + startDate + "' AND current_date() ");
            }

            if (startDate != null && endDate != null) {
                SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " and DATE(c.scheduledDate) between  '" + startDate + "' AND '" + endDate + "' ");
            }

            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " ORDER BY c.scheduledDate " + dateOrder + "");

            System.out.println("SQLlistUpcoming " + SQLlistUpcoming);

            TotalCount = this.service.countObject("select count(*) FROM ClassScheduler c WHERE c.scheduledDate >= '" + currentDate + "'");

            retrievedResultCount = this.service.countObject("select count(*) " + SQLlistUpcoming);

            List<Object> listUpcomingObj = service.loadByLimit(SQLlistUpcoming, (pageNo * maxResult), maxResult);

            /*list of upcoming classes*/
            if (listUpcomingObj.size() > 0) {
                for (Object object : listUpcomingObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);
                    upcomingClassList.add(json);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        j.put("count", TotalCount);
        j.put("no of records displaying in this page", retrievedResultCount);
        j.put("upcomingClassList", upcomingClassList);

        return j;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getallschedularForCalender")
    public @ResponseBody
    List<Object> getallschedularForCalender(@RequestBody Map<String, String> mapBean) {
        JSONObject json;
        ArrayList<Object> list = new ArrayList<>();
        try {
            String sql = "FROM ClassScheduler c  where YEAR(c.scheduledDate) = " + mapBean.get("year") + " AND MONTH(c.scheduledDate) = " + mapBean.get("month") + " ORDER BY c.scheduledDate DESC";
            System.out.println(sql);
            List<Object> listObj = service.getObject(sql);
            if (listObj.size() > 0) {
                for (Object object : listObj) {
                    ClassScheduler classScheduler = (ClassScheduler) object;
                    json = getClassSchedulerJson(classScheduler);
                    list.add(json);
                }
            } else {
                json = new JSONObject();
                json.put("msg", "something went wrong");
                list.add(json);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return list;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getPresentUrlByTeacherId")
    public @ResponseBody
    JSONObject getPresentUrlByTeacherId(@RequestBody final FilterBean filter) {
        String teacherId = filter.getTeacherAccountId();

        JSONObject json = new JSONObject();

        ArrayList<Object> recordedClassList = new ArrayList<>();
        ArrayList<Object> upcomingClassList = new ArrayList<>();
        ArrayList<Object> liveClassList = new ArrayList<>();

        List<String> gradeFilters = filter.getGradeFilter();
        List<String> syllabusFilters = filter.getSyllabusFilter();
        List<String> subjectFilters = filter.getSubjectFilter();
        List<String> chapterFilters = filter.getChapterFilter();
        List<String> batchFilters = filter.getBatchFilter();

        String text = filter.getText();

        int pageNo = Integer.parseInt(filter.getPageNo());
        int maxResult = 10;
        if (filter.getMaxResult() != null) {
            maxResult = Integer.parseInt(filter.getMaxResult());
        }

        int countRecorded = 0;
        int countUpcoming = 0;
        int countLive = 0;
        int recordedTotalCount = 0;
        int upcomingTotalCount = 0;
        int liveTotalCount = 0;

        //TimeZone tz = TimeZone.getTimeZone("IST");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        // df.setTimeZone(tz);
        Date current = MyDateFormate.stringToDate(String.format("%s", df.format(new Date())));
        String currentDate = df.format(new Date());
        try {
            Map<String, String> mapFilterSQL = new FilterUtility().getPresentUrlByTeacherIdFilter(teacherId, gradeFilters, syllabusFilters, subjectFilters, chapterFilters, batchFilters, currentDate, text);

            countRecorded = (int) service.countObject(mapFilterSQL.get("SQLcountRecorded"));
            List<Object> listRecordedObj = service.loadByLimit(mapFilterSQL.get("SQLRecorded"), (pageNo * maxResult), maxResult);
            List<Object> listUpcomingObj = service.getObject(mapFilterSQL.get("SQLlistUpcoming"));
            List<Object> listLiveObj = service.getObject(mapFilterSQL.get("SQLlistLive"));

            countUpcoming = (int) service.countObject(mapFilterSQL.get("SQLUpcomingCount"));
            countLive = (int) service.countObject(mapFilterSQL.get("SQLiveCount"));

            recordedTotalCount = (int) service.countObject(mapFilterSQL.get("SQLRecordedTotalCount"));
            upcomingTotalCount = (int) service.countObject(mapFilterSQL.get("SQLUpcomingTotalCount"));
            liveTotalCount = (int) service.countObject(mapFilterSQL.get("SQLiveTotalCount"));

            ClassScheduler classScheduler;

            /*list of recorded classes*/
            if (listRecordedObj.size() > 0) {
                for (Object object : listRecordedObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject jsonRecorded = getClassSchedulerJson(classScheduler);

                    Object[] teacherAccountObjArray = service.loadProperties(String.format("%s", "SELECT t.firstName, t.lastName FROM TeacherAccount t WHERE t.teacherAccountId = '" + teacherId + "'")).get(0);
                    String firstName = (String) teacherAccountObjArray[0];
                    String lastName = (String) teacherAccountObjArray[1];

                    jsonRecorded.put("firstName", firstName);
                    jsonRecorded.put("lastName", lastName);

                    int countRating = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Rating r WHERE r.sesionId = '" + classScheduler.getSessionId() + "'"));
                    if (countRating > 0) {
                        jsonRecorded.put("totalNumOfStudentForRating", countRating);
                    } else {
                        jsonRecorded.put("totalNumOfStudentForRating", 0);
                    }

                    recordedClassList.add(jsonRecorded);
                }
            }

            /*list of upcoming classes*/
            if (listUpcomingObj.size() > 0) {
                for (Object object : listUpcomingObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject jsonUpcoming = getClassSchedulerJson(classScheduler);
                    upcomingClassList.add(jsonUpcoming);
                }
            }

            /*list of live classes*/
            if (listLiveObj.size() > 0) {
                for (Object object : listLiveObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject jsonLive = getClassSchedulerJson(classScheduler);
                    liveClassList.add(jsonLive);
                }
            }

        } catch (Exception e) {
        }
//        json.put("countRecordedClass", countRecorded);
//        json.put("recordedClassList", recordedClassList);
//        json.put("upcomingClassList", upcomingClassList);
//        json.put("liveClassList", liveClassList);
        json.put("no of upcomingClasses displaying in this page", countUpcoming);
        json.put("upcomingClassList", upcomingClassList);
        json.put("upcomingClassTotalCount", upcomingTotalCount);

        json.put("no of recordedClasses displaying in this page", countRecorded);
        json.put("recordedClassList", recordedClassList);
        json.put("recordedClassTotalCount", recordedTotalCount);

        json.put("no of LiveClasses displaying in this page", countLive);
        json.put("liveClassList", liveClassList);
        json.put("liveClassTotalCount", liveTotalCount);

        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getPresentUrlByTeacherIdothers")
    public @ResponseBody
    List<JSONObject> getPresentUrlByTeacherIdothers(@RequestBody final FilterBean filter) {
        String teacherId = filter.getTeacherAccountId();
        List<JSONObject> list = new ArrayList<>();
        JSONObject json = null;

        String role = "others (Admin & teacher)";
        List<Object[]> teacherAccountObjArray = service.loadProperties(String.format("%s", "SELECT u.sessionId , u.guestUrl,u.title,u.presenterUniqueName, u.replayUrl ,u.teacherId ,u.scheduledDate, u.endDate ,u.presentUrl ,u.prepareUrl FROM ClassScheduler u WHERE u.accessTo = '" + role + "' AND u.teacherId<>'" + teacherId + "'"));
        if (teacherAccountObjArray.size() > 0) {
            for (Object[] objects : teacherAccountObjArray) {
                json = new JSONObject();

                json.put("sessionId", (String) objects[0]);
                json.put("guestUrl", (String) objects[1]);
                json.put("title", (String) objects[2]);
                json.put("presenterUniqueName", (String) objects[3]);
                json.put("replayUrl", (String) objects[4]);
                json.put("teacherId", (String) objects[5]);
                json.put("scheduledDate", MyDateFormate.dateToString((Date) objects[6]));
                json.put("endDate", MyDateFormate.dateToString((Date) objects[7]));
                json.put("presentUrl", (String) objects[8]);
                json.put("prepareUrl", (String) objects[9]);

                list.add(json);
            }
        } else {
            json = new JSONObject();
            json.put("msg", "empty list");
            list.add(json);
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getPresentUrlByTeacherIdForCalender")
    public @ResponseBody
    ResponseEntity<?> getPresentUrlByTeacherIdForCalender(@RequestBody Map<String, String> mapBean) {
        String teacherId = mapBean.get("teacherAccountId");
        List<Object> list = new ArrayList<>();
        JSONObject json;
        try {
            List<Object> listClassSchObject = service.getObject(String.format("%s", "FROM ClassScheduler c WHERE c.teacherId = '" + teacherId + "' and YEAR(c.scheduledDate) = " + mapBean.get("year") + " AND MONTH(c.scheduledDate) = " + mapBean.get("month") + " ORDER BY c.scheduledDate DESC"));
            if (listClassSchObject.size() > 0) {
                for (Object object : listClassSchObject) {
                    Object[] teacherAccountObjArray = service.loadProperties(String.format("%s", "SELECT t.firstName, t.lastName FROM TeacherAccount t WHERE t.teacherAccountId = '" + teacherId + "'")).get(0);
                    String firstName = (String) teacherAccountObjArray[0];
                    String lastName = (String) teacherAccountObjArray[1];

                    ClassScheduler classScheduler = (ClassScheduler) object;
                    json = getClassSchedulerJson(classScheduler);
                    json.put("firstName", firstName);
                    json.put("lastName", lastName);

                    int totalNumOfStudentForRating = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Rating r WHERE r.sesionId = '" + classScheduler.getSessionId() + "'"));

                    if (totalNumOfStudentForRating > 0) {
                        json.put("totalNumOfStudentForRating", totalNumOfStudentForRating);
                    } else {
                        json.put("totalNumOfStudentForRating", 0);
                    }

                    list.add(json);
                }
            } else {
                list = new ArrayList<>();
                json = new JSONObject();
                json.put("msg", "no classes has been  assigned to you");
                list.add(json);
            }
        } catch (Exception e) {
            list = new ArrayList<>();
            json = new JSONObject();
            json.put("msg", "Something went wrong");
            list.add(json);
        }
        json = new JSONObject();
        json.put("listOfUrl", list);

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/deleteLiveClass")
    public @ResponseBody
    JSONObject deleteLiveClass(@RequestBody final LiveMetaData bean) {
        JSONObject json = new JSONObject();
        try {
            int x0 = service.delete(String.format("%s", "DELETE FROM ClassScheduler c WHERE c.sessionId = '" + bean.getSessionId() + "'"));
            if (x0 > 0) {
                int x1 = service.delete(String.format("%s", "DELETE FROM Rating r WHERE r.sesionId = '" + bean.getSessionId() + "'"));
                if (x1 > 0) {
                    service.delete(String.format("%s", "DELETE FROM UserHistory u WHERE u.sessionId = '" + bean.getSessionId() + "'"));
                    json.put("msg", "live class deleted ");
                } else {
                    json.put("msg", "not deleted");
                }
            } else {
                json.put("msg", "not deleted");
            }
        } catch (Exception e) {
            json.put("msg", "Error " + e.getMessage());
        }

        deleteZoomMeeting(bean.getSessionId());
        return json;
    }

    private void deleteZoomMeeting(String sessionId) {
        log.info("Under Zoom meeting delete ", sessionId);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", zoomToken);
        String URL = "https://api.zoom.us/v2/meetings/" + sessionId;

        HttpEntity<?> request = new HttpEntity<Object>(headers);

        restTemplate.exchange(URL, HttpMethod.DELETE, request, String.class);
        log.info("Delete success");

    }

    @RequestMapping(method = RequestMethod.POST, value = "/deleteMultipleClasses")
    public @ResponseBody
    JSONObject deleteMultipleClasses(@RequestBody Map<String, Object> map) {
        JSONObject json = new JSONObject();

        List<String> sessionIds = (List<String>) (Object) map.get("sessionIds");

        try {
            if (sessionIds != null && sessionIds.size() > 0) {

                List<String> listClassSchedulerDeleteSQL = new ArrayList<>();
                List<String> listRatingDeleteSQL = new ArrayList<>();
                List<String> listUserHistoryDeleteSQL = new ArrayList<>();

                for (String session : sessionIds) {
                    listClassSchedulerDeleteSQL.add(String.format("%s", "DELETE FROM ClassScheduler c WHERE c.sessionId = '" + session + "'"));
                    listRatingDeleteSQL.add(String.format("%s", "DELETE FROM Rating r WHERE r.sesionId = '" + session + "'"));
                    listUserHistoryDeleteSQL.add(String.format("%s", "DELETE FROM UserHistory u WHERE u.sessionId = '" + session + "'"));
                }

                service.delete(listClassSchedulerDeleteSQL);
                service.delete(listRatingDeleteSQL);
                service.delete(listUserHistoryDeleteSQL);

                json.put("msg", sessionIds.size() + " classes have been deleted!");
            } else {
                json.put("msg", "No record deleted!");
            }
        } catch (Exception e) {
            json.put("msg", "Error " + e.getMessage());
        }

        return json;
    }
//    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @RequestMapping(value = {"/listOfClassSchedulerByBatchId"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> listOfClassSchedulerByBatchId(@RequestBody final FilterBean filter) {
        String scheduledDate = "";
        if (filter.getScheduledDate() != null) {
            scheduledDate = filter.getScheduledDate();
        }

        String batchId = filter.getBatchId();

        String presenterDisplayName = filter.getTeacher();
        List<String> syllabusFilter = filter.getSyllabusFilter();
        List<String> subjectFilters = filter.getSubjectFilter();
        List<String> freeclass = filter.getFreeclass();
        List<String> chapterFilters = filter.getChapterFilter();

        int pageNo = Integer.parseInt(filter.getPageNo());
        int maxResult = 10;
        if (filter.getMaxResult() != null) {
            maxResult = Integer.parseInt(filter.getMaxResult());
        }

        JSONObject j = new JSONObject();
        ArrayList<Object> recordedClassList = new ArrayList<>();
        ArrayList<Object> upcomingClassList = new ArrayList<>();
        ArrayList<Object> liveClassList = new ArrayList<>();

        String text = filter.getText();

        String startDate = filter.getStartDate();
        String enddate = filter.getEndDate();

        String dateOrder = filter.getDateOrder();
        String titleOrder = filter.getTitleOrder();

        //TimeZone tz = TimeZone.getTimeZone("IST");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
        //df.setTimeZone(tz);
//        Date current = MyDateFormate.stringToDate(String.format("%s", df.format(new Date())));
        String current = df.format(new Date());
        int countRecorded = 0;
        try {
            Map<String, String> SQLFilter = new FilterUtility().listOfClassSchedulerByBatchIdFilter(presenterDisplayName,
                    syllabusFilter,
                    subjectFilters, chapterFilters, batchId, scheduledDate, freeclass, current, text, startDate, enddate, dateOrder, titleOrder);
            System.out.println(SQLFilter.get("SQLcountRecorded"));
            countRecorded = (int) service.countObject(SQLFilter.get("SQLcountRecorded"));
            List<Object> listRecordedObj = service.loadByLimit(SQLFilter.get("SQLRecorded"), (pageNo * maxResult), maxResult);
            List<Object> listUpcomingObj = service.getObject(SQLFilter.get("SQLlistUpcoming"));
            List<Object> listLiveObj = service.getObject(SQLFilter.get("SQLlistLive"));

            ClassScheduler classScheduler;

            /*list of recorded classes*/
            if (listRecordedObj.size() > 0) {
                for (Object object : listRecordedObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);

                    int countRating = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Rating r WHERE r.sesionId = '" + classScheduler.getSessionId() + "'"));
                    if (countRating > 0) {
                        json.put("totalNumOfStudentForRating", countRating);
                    } else {
                        json.put("totalNumOfStudentForRating", 0);
                    }

                    recordedClassList.add(json);
                }
            }

            /*list of upcoming classes*/
            if (listUpcomingObj.size() > 0) {
                for (Object object : listUpcomingObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);
                    upcomingClassList.add(json);
                }
            }

            /*list of live classes*/
            if (listLiveObj.size() > 0) {
                for (Object object : listLiveObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);
                    liveClassList.add(json);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        System.out.println("countRecorded  " + countRecorded);
        System.out.println("recordedClassList  " + recordedClassList.size());
        System.out.println("upcomingClassList  " + upcomingClassList.size());
        System.out.println("liveClassList  " + liveClassList.size());

        j.put("countRecordedClass", countRecorded);
        j.put("recordedClassList", recordedClassList);
        j.put("upcomingClassList", upcomingClassList);
        j.put("liveClassList", liveClassList);

        return new ResponseEntity<>(j, HttpStatus.OK);
    }

    @RequestMapping(value = {"/listOfClassSchedulerByBatchIdForCalender"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> listOfClassSchedulerByBatchIdForCalender(@RequestBody CalenderBean mapBean) {
        String batchId = mapBean.getBatchId();
        ArrayList<Object> list = new ArrayList<>();
        FilterUtility filterUtility = new FilterUtility();
        try {
            String sql = "FROM ClassScheduler c where c.batchId in (SELECT s.batchId from StudentSubscription s where s.studentAccountId='" + mapBean.getStudentAccountId() + "') and YEAR(c.scheduledDate) = " + mapBean.getYear() + " AND MONTH(c.scheduledDate) = " + mapBean.getMonth() + "";
            System.out.println("query " + sql);
            List<Object> listRecordedObj = service.getObject(sql);
            JSONObject json;
            if (listRecordedObj.size() > 0) {
                for (Object object : listRecordedObj) {
                    ClassScheduler classScheduler = (ClassScheduler) object;
                    json = getClassSchedulerJson(classScheduler);
                    list.add(json);
                }
            } else {
                json = new JSONObject();
                json.put("msg", "No class found!");
                list.add(json);
            }

//            List<String> freeclass = mapBean.getFreeclass();
//            Object[] listSubscribeTypeProperties = service.loadProperties(String.format("%s", "SELECT b.syllabusId, b.gradeId FROM Batch b WHERE b.batchId = '" + batchId + "'")).get(0);
//            String syllabusId = (String) listSubscribeTypeProperties[0];
//            String gradeId = (String) listSubscribeTypeProperties[1];
//            Object syllabusiIdForscorewell = service.getObject(String.format("%s", "SELECT b.syllabusId FROM Batch b WHERE   b.batchId  IN (" + filterUtility.getIN_CLAUSE_VALUES(freeclass) + ")")).get(0);
//            String SQL = "FROM ClassScheduler c WHERE c.gradeId = '" + gradeId + "' AND c.syllabusId IN ('" + syllabusId + "' , '" + syllabusiIdForscorewell + "') AND (c.accessTo = 'Members' OR c.accessTo = 'Both') ORDER BY c.scheduledDate DESC";
//            List<Object> listRecordedObj = service.getObject(SQL);
//            JSONObject json;
//            if (listRecordedObj.size() > 0) {
//                for (Object object : listRecordedObj) {
//                    ClassScheduler classScheduler = (ClassScheduler) object;
//
//                    json = getClassSchedulerJson(classScheduler);
//                    list.add(json);
//                }
//            } else {
//                json = new JSONObject();
//                json.put("msg", "No class found!");
//                list.add(json);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listOfliveclassBygradeName")
    public @ResponseBody
    JSONObject listOfliveclassBygradeName(@RequestBody final FilterBean filter) {
        JSONObject j = new JSONObject();
        ArrayList<Object> recordedClassList = new ArrayList<>();
        ArrayList<Object> upcomingClassList = new ArrayList<>();
        ArrayList<Object> liveClassList = new ArrayList<>();

        List<String> syllabusFilters = filter.getSyllabusFilter();
        List<String> subjectFilters = filter.getSubjectFilter();
        List<String> chapterFilters = filter.getChapterFilter();
        List<String> batchFilters = filter.getBatchFilter();

        int pageNo = Integer.parseInt(filter.getPageNo());
        int maxResult = 10;
        if (filter.getMaxResult() != null) {
            maxResult = Integer.parseInt(filter.getMaxResult());
        }

        String gradeName = filter.getGradeName();

        //TimeZone tz = TimeZone.getTimeZone("IST");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
        //df.setTimeZone(tz);
//        Date currentDate = MyDateFormate.stringToDate(String.format("%s", df.format(new Date())));
        String currentDate = df.format(new Date());
        int countRecorded = 0;
        try {
            Map<String, String> mapFilterSQL = new FilterUtility().listOfliveclassBygradeNameFilter(gradeName, syllabusFilters, subjectFilters, chapterFilters, batchFilters, currentDate);

            countRecorded = (int) service.countObject(mapFilterSQL.get("SQLcountRecorded"));
            List<Object> listRecordedObj = service.loadByLimit(mapFilterSQL.get("SQLRecorded"), (pageNo * maxResult), maxResult);
            List<Object> listUpcomingObj = service.getObject(mapFilterSQL.get("SQLlistUpcoming"));
            List<Object> listLiveObj = service.getObject(mapFilterSQL.get("SQLlistLive"));

            ClassScheduler classScheduler;

            /*list of recorded classes*/
            if (listRecordedObj.size() > 0) {
                for (Object object : listRecordedObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);

                    int countRating = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Rating r WHERE r.sesionId = '" + classScheduler.getSessionId() + "'"));
                    if (countRating > 0) {
                        json.put("totalNumOfStudentForRating", countRating);
                    } else {
                        json.put("totalNumOfStudentForRating", 0);
                    }

                    recordedClassList.add(json);
                }
            }

            /*list of upcoming classes*/
            if (listUpcomingObj.size() > 0) {
                for (Object object : listUpcomingObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);
                    upcomingClassList.add(json);
                }
            }

            /*list of live classes*/
            if (listLiveObj.size() > 0) {
                for (Object object : listLiveObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);
                    liveClassList.add(json);
                }
            }

        } catch (Exception e) {
        }
        j.put("countRecordedClass", countRecorded);
        j.put("recordedClassList", recordedClassList);
        j.put("upcomingClassList", upcomingClassList);
        j.put("liveClassList", liveClassList);

        return j;
    }

    @RequestMapping(value = {"/blockStudentByAdmin_LiveClass"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> blockStudentByAdmin_LiveClass(@RequestBody Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        Timestamp date = new Timestamp(System.currentTimeMillis());
        String studentAccountId = mapBean.get("studentAccountId");
        String sessionId = mapBean.get("sessionId");
        try {
            int count = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM StudentBlockedByAdmin s WHERE s.studentAccountId = '" + studentAccountId + "' AND s.sessionId = '" + sessionId + "'"));
            if (count > 0) {
                //UNBLOCK
                service.delete(String.format("%s", "DELETE FROM StudentBlockedByAdmin s WHERE s.studentAccountId = '" + studentAccountId + "' AND s.sessionId = '" + sessionId + "'"));
                json.put("msg", "Student has been unblocked!");
            } else {
                //BLOCK
                StudentBlockedByAdmin blockedByAdmin = new StudentBlockedByAdmin();
                String id = (String) pKGenerator.generate(StudentBlockedByAdmin.class, "BLOCK");
                blockedByAdmin.setBlockId(id);
                blockedByAdmin.setStudentAccountId(studentAccountId);
                blockedByAdmin.setSessionId(sessionId);
                blockedByAdmin.setDateOfCreation(date);

                service.save(blockedByAdmin);
                json.put("blockId", id);
                json.put("studentAccountId", studentAccountId);
                json.put("sessionId", sessionId);
                json.put("msg", "Student has been blocked!");
            }
        } catch (Exception e) {
            json.put("msg", "Something went wrong. Try Again!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/listOfBlockedStudentIdBySessionId"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> listOfBlockedStudentIdBySessionId(@RequestBody Map<String, String> mapBean) {
        List<String> list = new ArrayList<>();

        String sessionId = mapBean.get("sessionId");
        try {
            List<Object> listStudentBlockedByAdminObj = service.getObject(String.format("%s", "SELECT s.studentAccountId FROM StudentBlockedByAdmin s WHERE s.sessionId = '" + sessionId + "'"));
            if (listStudentBlockedByAdminObj.size() > 0) {
                for (Object object : listStudentBlockedByAdminObj) {
                    String studentAccountId = (String) object;
                    list.add(studentAccountId);
                }
            }
        } catch (Exception e) {
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = {"/checkStudentBlockedOrNot"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> checkStudentBlockedOrNot(@RequestBody Map<String, String> mapBean) {
        JSONObject json = new JSONObject();

        String studentAccountId = mapBean.get("studentAccountId");
        String sessionId = mapBean.get("sessionId");

        try {
            int count = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM StudentBlockedByAdmin s WHERE s.studentAccountId = '" + studentAccountId + "' AND s.sessionId = '" + sessionId + "'"));
            if (count > 0) {
                json.put("msg", "blocked");
            } else {
                json.put("msg", "unblocked");
            }
        } catch (Exception e) {
            json.put("msg", "Something went wrong. Try Again!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/getLiveClassBasedOnSessionId", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getLiveClassBasedOnSessionId(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        String sessionId = mapBean.get("sessionId");

        List<Object[]> classSchedulerProperties = service.loadProperties(String.format("%s", "SELECT c.classID, c.avgRating FROM ClassScheduler c WHERE c.sessionId = '" + sessionId + "'"));

        if (classSchedulerProperties.size() > 0) {
            Object[] object = classSchedulerProperties.get(0);
            json.put("classId", object[0]);
            json.put("avgRating", object[1]);

            String s = "SELECT COUNT(*) FROM Rating r WHERE r.sesionId = '" + sessionId + "'";
            int count = (int) service.countObject(s);
            if (count > 0) {
                json.put("totalNumOfStudentForRating", count);
            } else {
                json.put("totalNumOfStudentForRating", 0);
            }
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/getClassSchedulerBySessionId", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getClassSchedulerBySessionId(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = null;
        String sessionId = mapBean.get("sessionId");

        List<Object> listClassSchedulerObject = service.getObject(String.format("%s", "FROM ClassScheduler c WHERE c.sessionId = '" + sessionId + "'"));

        if (listClassSchedulerObject.size() > 0) {
            ClassScheduler classScheduler = (ClassScheduler) listClassSchedulerObject.get(0);
            json = getClassSchedulerJson(classScheduler);

            String s = "SELECT COUNT(*) FROM Rating r WHERE r.sesionId = '" + sessionId + "'";
            int count = (int) service.countObject(s);
            if (count > 0) {
                json.put("totalNumOfStudentForRating", count);
            } else {
                json.put("totalNumOfStudentForRating", 0);
            }
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/missedClasses"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> missedClasses(@RequestBody final FilterBean filter) {
        FilterUtility filterUtility = new FilterUtility();
        List<Object> incompletedClassScheduler = new ArrayList<>();
        List<Object> missedClassScheduler = new ArrayList<>();
        List<String> freeclass = filter.getFreeclass();
        Set<String> incompletedClassSchedulerSET;
        Set<String> missedClassSchedulerSET;
        JSONObject jsonOfList = new JSONObject();
        String sAccountId = filter.getsAccountId();
        String presenterDisplayName = filter.getTeacher();
        List<String> subjectFilter = filter.getSubjectFilter();
        List<String> syllabusFilter = filter.getSyllabusFilter();

        String startDate = filter.getStartDate();
        String enddate = filter.getEndDate();
        String pageNo = filter.getPageNo();
        String pageSize = filter.getPageSize();
        int offset = Integer.parseInt(pageNo) * Integer.parseInt(pageSize);
        int limit = Integer.parseInt(pageSize);

        String text = filter.getText();

        String dateOrder = filter.getDateOrder();
        String titleOrder = filter.getTitleOrder();

        try {
//
//            /*getting metadata for missed classes*/
////            Object[] sAccountproperties = service.loadProperties(String.format("%s", "SELECT s.batchId, s.syllabusId, s.gradeId FROM StudentSubscription s WHERE s.studentAccountId = 'STUDENT000173'")).get(0);
//            List<Object[]> loadProperties = service.loadProperties(String.format("%s", "SELECT s.batchId, s.syllabusId, s.gradeId FROM "
//                    + "StudentSubscription s WHERE s.studentAccountId = '" + sAccountId + "' and s.batchId is not null"));
//            System.out.println();
//            for (Object[] sAccountproperty : loadProperties) {
//
//                String batchId = (String) sAccountproperty[0];
//                String syllabusId = (String) sAccountproperty[1];
//                String gradeId = (String) sAccountproperty[2];
//                Object syllabusiIdForscorewell = null;
//                List<Object> liClassSchedObject1 = null;
//                if (freeclass.size() > 0) {
//                    System.out.println("i should not come");
//                    List<Object> syllabusiIdForscorewellss = service.getObject(String.format("%s", "SELECT c.syllabusId FROM ClassScheduler c WHERE   c.batchId  IN (" + filterUtility.getIN_CLAUSE_VALUES(freeclass) + ")"));
//                    if (syllabusiIdForscorewellss.size() > 0) {
//                        syllabusiIdForscorewell = service.getObject(String.format("%s", "SELECT c.syllabusId FROM ClassScheduler c WHERE   c.batchId  IN (" + filterUtility.getIN_CLAUSE_VALUES(freeclass) + ")")).get(0);
//                        liClassSchedObject1 = service.getObject(String.format("%s", "SELECT c.sessionId FROM ClassScheduler c WHERE c.gradeId = '" + gradeId + "' AND c.syllabusId IN( '" + syllabusId + "' ,'" + syllabusiIdForscorewell + "') AND c.batchId  IN ('" + batchId + "', " + filterUtility.getIN_CLAUSE_VALUES(freeclass) + ")"));
//                    } else {
//                        liClassSchedObject1 = service.getObject(String.format("%s", "SELECT c.sessionId FROM ClassScheduler c WHERE c.gradeId = '" + gradeId + "' AND c.syllabusId = '" + syllabusId + "' AND c.batchId = '" + batchId + "'"));
//                    }
//                } else {
//                    System.out.println("am coming");
//                    liClassSchedObject1 = service.getObject(String.format("%s", "SELECT c.sessionId FROM ClassScheduler c WHERE c.gradeId = '" + gradeId + "' AND c.syllabusId = '" + syllabusId + "' AND c.batchId  = '" + batchId + "'"));
//
//                }
//
//                /*list of all sessiods wrt batchId*/
////            System.out.println(syllabusiIdForscorewell);
//                List<Object> liUserHistorySessionId = service.getObject(String.format("%s", "SELECT u.sessionId FROM UserHistory u WHERE u.accountId = '" + sAccountId + "'"));
//
//                List<String> incompletedSessionId = new ArrayList<>();
//                List<String> missedSessionId = new ArrayList<>();
//                List<String> s1 = new ArrayList<>();
//                liClassSchedObject1
//                        .stream()
//                        .map((object) -> (String) object)
//                        .forEachOrdered((sId) -> {
//                            if (liUserHistorySessionId.contains(sId)) {
//                                /*not missed class (ie. incompletedSessionId class)*/
//                                incompletedSessionId.add(sId);
//
//                            } else {
//                                /*it is missed class*/
//                                missedSessionId.add(sId);
//
//                                /*get the list all the classes from rcorded video analytics*/
//                                List<Object> listSessionIdFromRecordedVA = service.getObject(String.format("%s", "SELECT r.sessionId FROM RecordedVideoAnalytics r WHERE r.accountId = '" + sAccountId + "'"));
//
//                                missedSessionId
//                                        .stream()
//                                        .map((o) -> ((String) o))
//                                        .filter((sessionId) -> (listSessionIdFromRecordedVA.contains(sessionId)))
//                                        .forEachOrdered((sessionId) -> {
//                                            s1.add(sessionId);
//                                        });
//
//                            }
//                        });
//
//                if (s1.size() > 0) {
//                    missedSessionId.removeAll(s1);
//                }
//
//                /*getting the sessionId of student which he has attended through recorded viedos*/
//                List<Object> listSessionIdFromRecordedVA3 = service.getObject(String.format("%s", "SELECT r.sessionId FROM RecordedVideoAnalytics r WHERE r.accountId = '" + sAccountId + "'"));
//                listSessionIdFromRecordedVA3
//                        .stream()
//                        .map((object) -> (String) object)
//                        .forEachOrdered((sId) -> {
//                            incompletedSessionId.add(sId);
//                        });
//
//                /*getting OBJ from Live Participants And Recorded Participants*/
//                List<Object> listIncomletedSessionIdFromParticipants = service.getObject(String.format("%s", "FROM Participants p WHERE p.participantUniqueName = '" + sAccountId + "'"));
//                List<Object> listIncomletedSessionIdFromRecordedStats = service.getObject(String.format("%s", "FROM LearnTronRecordedStats p WHERE p.uniqueName = '" + sAccountId + "'"));
//
//                /*completed by Recorded Participants*/
//                List<String> s2 = new ArrayList();
//
//                listIncomletedSessionIdFromRecordedStats
//                        .stream()
//                        .map((o) -> (LearnTronRecordedStats) o)
//                        .filter((lIncompleted) -> (lIncompleted.getViewPercentage() > 90f))
//                        .filter((lIncompleted) -> (incompletedSessionId.contains(lIncompleted.getSessionId())))
//                        .forEachOrdered((lIncompleted) -> {
//                            s2.add(lIncompleted.getSessionId());
//                        });
//
//                if (s2.size() > 0) {
//                    incompletedSessionId.removeAll(s2);
//                }
//
//                /*completed by Live Participants*/
//                List<String> s3 = new ArrayList();
//
//                listIncomletedSessionIdFromParticipants
//                        .stream()
//                        .map((o) -> (Participants) o)
//                        .filter((pIncompleted) -> (pIncompleted.getViewPercentage() > 90f))
//                        .filter((pIncompleted) -> (incompletedSessionId.contains(pIncompleted.getSessionId())))
//                        .forEachOrdered((pIncompleted) -> {
//                            s3.add(pIncompleted.getSessionId());
//                        });
//
//                if (s3.size() > 0) {
//                    incompletedSessionId.removeAll(s3);
//                }
//
//                incompletedClassSchedulerSET = new LinkedHashSet<>(incompletedSessionId);
//                missedClassSchedulerSET = new LinkedHashSet<>(missedSessionId);
//
//                /*incompleted ClassScheduler list*/
//                incompletedClassSchedulerSET.forEach((sessionId) -> {
//                    String scheduledDate = "";
//                    if (filter.getScheduledDate() != null) {
//                        scheduledDate = filter.getScheduledDate();
//                    }
//
//                    String sqlClassScheduler = "FROM ClassScheduler c WHERE c.sessionId = '" + sessionId + "'";
//                    if (presenterDisplayName != null) {
//                        sqlClassScheduler = sqlClassScheduler + " AND c.presenterDisplayName = '" + presenterDisplayName + "'";
//                    }
//                    if (subjectFilter != null) {
//                        sqlClassScheduler = sqlClassScheduler + " AND c.subjectId = '" + subjectFilter.get(0) + "'";
//                    }
//
//                    if (syllabusFilter != null) {
//                        sqlClassScheduler = sqlClassScheduler + " AND c.syllabusId = '" + syllabusFilter.get(0) + "'";
//                    }
//
//                    if (startDate == null && enddate != null) {
//                        sqlClassScheduler = sqlClassScheduler + " AND c.scheduledDate <  '" + enddate + "'";
//                    }
//                    if (startDate != null && enddate == null) {
//                        sqlClassScheduler = sqlClassScheduler + " AND DATE(c.scheduledDate) between  '" + startDate + "' AND current_date() ";
//                    }
//                    if (startDate != null && enddate != null) {
//                        sqlClassScheduler = sqlClassScheduler + " AND DATE(c.scheduledDate) between  '" + startDate + "' AND '" + enddate + "' ";
//                    }
////                    if (pageNo != null && pageSize != null) {
////                        sqlClassScheduler = sqlClassScheduler + "limit " + offset + "," + limit + " ;";
////                    }
//
//                    System.out.println("SQL INC " + sqlClassScheduler);
//
////                    if (!"".equals(scheduledDate)) {
////                        sqlClassScheduler = sqlClassScheduler + " AND DATE(c.scheduledDate) = '" + scheduledDate + "'";
////                    }
//                    List<Object> listObj = service.getObject(String.format("%s", sqlClassScheduler));
////                    List<Object> listObj = service.loadByLimit(sqlClassScheduler,offset,limit);
//                    System.out.println("listObjSize " + listObj.size());
//                    if (listObj.size() > 0) {
//                        ClassScheduler c = (ClassScheduler) listObj.get(0);
//                        JSONObject json = getClassSchedulerJson(c);
//
//                        List<Object> liViewPer1 = service.getObject(String.format("%s", "SELECT l.viewPercentage FROM LearnTronRecordedStats l WHERE l.sessionId = '" + sessionId + "' AND l.uniqueName = '" + sAccountId + "'"));
//                        float f1 = 0.0f;
//                        if (liViewPer1.size() > 0) {
//                            f1 = (float) liViewPer1.get(0);
//                        }
//
//                        List<Object> liViewPer2 = service.getObject(String.format("%s", "SELECT p.viewPercentage FROM Participants p WHERE p.sessionId = '" + sessionId + "' AND p.participantUniqueName = '" + sAccountId + "'"));
//                        float f2 = 0.0f;
//                        if (liViewPer2.size() > 0) {
//                            f2 = (float) liViewPer2.get(0);
//                        }
//
//                        if (f1 > f2) {
//                            json.put("maxViewPercentage", f1);
//                        } else {
//                            json.put("maxViewPercentage", f2);
//                        }
//
//                        incompletedClassScheduler.add(json);
//                    }
//                });


            /*missed ClassScheduler list*/
            String missedClassCount = " select count(*)FROM ClassScheduler c WHERE  c.scheduledDate < current_date() and c.batchId in"
                    + " (select s.batchId from StudentSubscription s where s.studentAccountId='" + sAccountId + "') and c.accessTo in ('Members', 'Both') "
                    + "and c.sessionId not in"
                    + " (select u.sessionId FROM UserHistory u WHERE u.accountId ='" + sAccountId + "')";

            if (!"all".equals(filter.getSyllabus())) {
                missedClassCount = missedClassCount + " and  c.syllabusId= '" + filter.getSyllabus() + "'";
            }
            if (!"all".equals(filter.getSubject())) {
                missedClassCount = missedClassCount + " and  c.subjectId= '" + filter.getSubject() + "'";
            }

            if ("all".equals(startDate) && !"all".equals(enddate)) {
                missedClassCount = missedClassCount + " and DATE(c.scheduledDate) <  '" + enddate + "'";
            }
            if (!"all".equals(startDate) && "all".equals(enddate)) {
                missedClassCount = missedClassCount + " and DATE(c.scheduledDate) between  '" + startDate + "' AND current_date() ";
            }

            if (!"all".equals(startDate) && !"all".equals(enddate)) {
                missedClassCount = missedClassCount + " and DATE(c.scheduledDate) between  '" + startDate + "' AND '" + enddate + "' ";
            }
            if (text != null) {
                missedClassCount = missedClassCount + " and c.title LIKE '" + text + "%'";
            }

            int missedClassesCount = (int) service.countObject(missedClassCount);

            boolean dateFlag = false;
            boolean titleFlag = false;

            String nativeSqlForMissedClasses = " FROM ClassScheduler c WHERE  c.scheduledDate < current_date() and c.batchId in"
                    + " (select s.batchId from StudentSubscription s where s.studentAccountId='" + sAccountId + "') and c.accessTo in ('Members', 'Both') "
                    + "and c.sessionId not in"
                    + " (select u.sessionId FROM UserHistory u WHERE u.accountId ='" + sAccountId + "')";

            if (!"all".equals(filter.getSyllabus())) {
                nativeSqlForMissedClasses = nativeSqlForMissedClasses + " and  c.syllabusId= '" + filter.getSyllabus() + "'";
            }
            if (!"all".equals(filter.getSubject())) {
                nativeSqlForMissedClasses = nativeSqlForMissedClasses + " and  c.subjectId= '" + filter.getSubject() + "'";
            }

            if ("all".equals(startDate) && !"all".equals(enddate)) {

                nativeSqlForMissedClasses = nativeSqlForMissedClasses + " and DATE(c.scheduledDate) <  '" + enddate + "'";
            }
            if (!"all".equals(startDate) && "all".equals(enddate)) {

                nativeSqlForMissedClasses = nativeSqlForMissedClasses + " and DATE(c.scheduledDate) between  '" + startDate + "' AND current_date() ";
            }

            if (!"all".equals(startDate) && !"all".equals(enddate)) {

                nativeSqlForMissedClasses = nativeSqlForMissedClasses + " and DATE(c.scheduledDate) between  '" + startDate + "' AND '" + enddate + "' ";
            }
            if (text != null) {

                nativeSqlForMissedClasses = nativeSqlForMissedClasses + " and c.title LIKE '" + text + "%'";
            }

            nativeSqlForMissedClasses = nativeSqlForMissedClasses + " order by c.scheduledDate " + dateOrder + ",c.title " + titleOrder + " ";

            System.out.println("nativeSqlForMissedClasses " + nativeSqlForMissedClasses);

            List<Object> missedClassesList = this.service.loadByLimit(nativeSqlForMissedClasses, offset, limit);

            if (missedClassesList.size() > 0) {
                for (Object object : missedClassesList) {
                    ClassScheduler c = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(c);
                    missedClassScheduler.add(json);
                }

            }

//
//
//                missedClassSchedulerSET
//                        .stream()
//                        .forEach((sessionId) -> {
//
//                            String scheduledDate = "";
//                            if (filter.getScheduledDate() != null) {
//                                scheduledDate = filter.getScheduledDate();
//                            }
//
//                            String sqlClassScheduler = "FROM ClassScheduler c WHERE c.sessionId = '" + sessionId + "'"
//                                    + " AND c.accessTo = 'Members' AND c.scheduledDate< current_timestamp()";
//
//                            if (presenterDisplayName != null) {
//                                sqlClassScheduler = sqlClassScheduler + " AND c.presenterDisplayName = '" + presenterDisplayName + "'";
//                            }
//
//                            if (syllabusFilter != null) {
//
//                                sqlClassScheduler = sqlClassScheduler + " AND c.syllabusId ='" + syllabusFilter.get(0) + "'";
//                            }
//                            if (subjectFilter != null) {
//                                sqlClassScheduler = sqlClassScheduler + " AND c.subjectId = '" + subjectFilter.get(0) + "'";
//                            }
//
//                            if (startDate == null && enddate != null) {
//                                sqlClassScheduler = sqlClassScheduler + " AND c.scheduledDate <  '" + enddate + "'";
//                            }
//                            if (startDate != null && enddate == null) {
//                                sqlClassScheduler = sqlClassScheduler + " AND DATE(c.scheduledDate) between  '" + startDate + "' AND current_date() ";
//                            }
//                            if (startDate != null && enddate != null) {
//                                sqlClassScheduler = sqlClassScheduler + " AND DATE(c.scheduledDate) between  '" + startDate + "' AND '" + enddate + "' ";
//                            }
////                            if (!"".equals(scheduledDate)) {
////                                sqlClassScheduler = sqlClassScheduler + " AND DATE(c.scheduledDate) = '" + scheduledDate + "'";
////                            }
////                            System.out.println(sqlClassScheduler + "---------------------------------------------------------------");
//                            List<Object> listObj = service.getObject(String.format("%s", sqlClassScheduler));
//
//                            if (listObj.size() > 0) {
//                                ClassScheduler c = (ClassScheduler) listObj.get(0);
//                                JSONObject json = getClassSchedulerJson(c);
//                                json.put("maxViewPercentage", 0);
//
//                                missedClassScheduler.add(json);
//                            }
//                        });
//            jsonOfList.put("inCompletedClasses", incompletedClassScheduler);
            System.out.println("missedClassesCount " + missedClassesCount);
            System.out.println("missedClassScheduler " + missedClassScheduler);
            jsonOfList.put("missedClassesCount", missedClassesCount);
            jsonOfList.put("missedClasses", missedClassScheduler);

//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(jsonOfList, HttpStatus.OK);
    }

    @RequestMapping(value = {"/incompletedClasses"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> incompletedClasses(@RequestBody final FilterBean filter) {

        List<Object> incompletedClassScheduler = new ArrayList<>();

        JSONObject jsonOfList = new JSONObject();
        String sAccountId = filter.getsAccountId();
        String presenterDisplayName = filter.getTeacher();
        List<String> subjectFilter = filter.getSubjectFilter();
        List<String> syllabusFilter = filter.getSyllabusFilter();

        String startDate = filter.getStartDate();
        String enddate = filter.getEndDate();
        String pageNo = filter.getPageNo();
        String pageSize = filter.getPageSize();
        int offset = Integer.parseInt(pageNo) * Integer.parseInt(pageSize);
        int limit = Integer.parseInt(pageSize);

        String text = filter.getText();

        String dateOrder = filter.getDateOrder();
        String titleOrder = filter.getTitleOrder();
        try {

            /**
             * incomplete class count
             */
            String incompletedClassesCount = "select count(*) "
                    + "from CLASS_SCHEDULER c where c.SESSION_ID in "
                    + "(SELECT p.SESSION_ID FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE<90\n"
                    + "UNION\n"
                    + "SELECT rs.SESSION_ID FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' and rs.VIEW_PERCETAGE<90)";
            String inCompleteNativeSqlCOunt = "select c.CLASS_ID,c.ACCESS_TO,c.AVG_RATING,c.BATCH_ID,c.CAN_EXTEND, "
                    + "c.CHAPTER_ID,c.CHAPTER_NAME,c.DURATION_MINUTES,c.END_DATE,c.FORCE_EXIT_PARTICIPANTS, "
                    + "c.GRADE_ID,c.GRADE_NAME,c.GUEST_URL,c.IS_RECORDED_SESSION_VIEABLE,c.NO_OF_SEATS, "
                    + "c.PREPARE_URL,c.PRESENT_URL,c.PRESENTER_DISPALY_NAME,c.PRESENTER_UNIQUE_NAME,c.REPLAY_URL, "
                    + "c.RESTART_SESSION,c.SCHEDULE_DATE,c.SESSION_ID,c.SUBJECT_ID,c.SUBJECT_NAME, "
                    + "c.SYLLABUS_ID,c.SYLLABUS_NAME,c.TEACHER_ID,c.TITLE,max(res.VIEW_PERCETAGE) "
                    + "from CLASS_SCHEDULER c INNER JOIN (select result.* FROM(SELECT p.SESSION_ID,p.VIEW_PERCETAGE FROM PARTICIPANTS p "
                    + "where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE<90 UNION "
                    + "SELECT rs.SESSION_ID,rs.VIEW_PERCETAGE FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' "
                    + "and rs.VIEW_PERCETAGE<90) as result  where result.SESSION_ID not in(SELECT p.SESSION_ID FROM PARTICIPANTS p "
                    + "where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE>90 UNION "
                    + "SELECT rs.SESSION_ID FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' and rs.VIEW_PERCETAGE>90)) as res "
                    + "on c.SESSION_ID=res.SESSION_ID";

            if (!"all".equals(filter.getSyllabus())) {
                inCompleteNativeSqlCOunt = inCompleteNativeSqlCOunt + " and  c.SYLLABUS_ID= '" + filter.getSyllabus() + "'";
            }
            if (!"all".equals(filter.getSubject())) {
                inCompleteNativeSqlCOunt = inCompleteNativeSqlCOunt + " and  c.SUBJECT_ID= '" + filter.getSubject() + "'";
            }

            if ("all".equals(startDate) && !"all".equals(enddate)) {
                inCompleteNativeSqlCOunt = inCompleteNativeSqlCOunt + " and DATE(c.SCHEDULE_DATE) <  '" + enddate + "'";
            }
            if (!"all".equals(startDate) && "all".equals(enddate)) {
                inCompleteNativeSqlCOunt = inCompleteNativeSqlCOunt + " and DATE(c.SCHEDULE_DATE) between  '" + startDate + "' AND CURRENT_DATE ";
            }

            if (!"all".equals(startDate) && !"all".equals(enddate)) {
                inCompleteNativeSqlCOunt = inCompleteNativeSqlCOunt + " and DATE(c.SCHEDULE_DATE) between  '" + startDate + "' AND '" + enddate + "' ";
            }
            if (text != null) {
                inCompleteNativeSqlCOunt = inCompleteNativeSqlCOunt + " and c.title LIKE '" + text + "%'";
            }

            inCompleteNativeSqlCOunt = inCompleteNativeSqlCOunt + " GROUP BY res.SESSION_ID";

            List<Object> objectsByNativeSqlQuery = service.getObjectsByNativeSqlQuery(inCompleteNativeSqlCOunt);

            int count2 = objectsByNativeSqlQuery.size();

            /**
             * incomplete class list
             */
//          String sql=  "SELECT p.SESSION_ID FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE<90\n"
//                    + "UNION\n"
//                    + "SELECT rs.SESSION_ID FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' and rs.VIEW_PERCETAGE<90";
//
//              List<Object> incompletedClassesList = this.service.getObjectsByNativeSqlQuery(inCompleteNativeSql);
//            String inCompleteNativeSql = "select CLASS_ID,ACCESS_TO,AVG_RATING,BATCH_ID,CAN_EXTEND, "
//                    + "CHAPTER_ID,CHAPTER_NAME,DURATION_MINUTES,END_DATE,FORCE_EXIT_PARTICIPANTS, "
//                    + "GRADE_ID,GRADE_NAME,GUEST_URL,	IS_RECORDED_SESSION_VIEABLE,NO_OF_SEATS, "
//                    + "PREPARE_URL,PRESENT_URL,PRESENTER_DISPALY_NAME,PRESENTER_UNIQUE_NAME,REPLAY_URL, "
//                    + "RESTART_SESSION,SCHEDULE_DATE,c.SESSION_ID,SUBJECT_ID,SUBJECT_NAME, "
//                    + "SYLLABUS_ID,SYLLABUS_NAME,TEACHER_ID,TITLE,p.VIEW_PERCETAGE "
//                    + "from CLASS_SCHEDULER c INNER JOIN ( "
//                    + " SELECT p.SESSION_ID FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE<90 "
//                    + "UNION\n"
//                    + "SELECT rs.SESSION_ID FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' and rs.VIEW_PERCETAGE<90)";
//            String inCompleteNativeSql = "select c.CLASS_ID,c.ACCESS_TO,c.AVG_RATING,c.BATCH_ID,c.CAN_EXTEND, "
//                    + "c.CHAPTER_ID,c.CHAPTER_NAME,c.DURATION_MINUTES,c.END_DATE,c.FORCE_EXIT_PARTICIPANTS, "
//                    + "c.GRADE_ID,c.GRADE_NAME,c.GUEST_URL,c.IS_RECORDED_SESSION_VIEABLE,c.NO_OF_SEATS, "
//                    + "c.PREPARE_URL,c.PRESENT_URL,c.PRESENTER_DISPALY_NAME,c.PRESENTER_UNIQUE_NAME,c.REPLAY_URL, "
//                    + "c.RESTART_SESSION,c.SCHEDULE_DATE,c.SESSION_ID,c.SUBJECT_ID,c.SUBJECT_NAME, "
//                    + "c.SYLLABUS_ID,c.SYLLABUS_NAME,c.TEACHER_ID,c.TITLE,maxview "
//                    + "from CLASS_SCHEDULER c INNER JOIN ( "
//                    + " SELECT p.SESSION_ID,max(p.VIEW_PERCETAGE) as maxview FROM PARTICIPANTS p where "
//                    + "p.PARTIC_UNIQUENAME='"+sAccountId+"' and p.SESSION_ID not in "
//                    + "(SELECT p.SESSION_ID FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='"+sAccountId+"'and p.VIEW_PERCETAGE>90) "
//                    + "GROUP BY p.SESSION_ID "
//                    + "UNION "
//                    + "SELECT rs.SESSION_ID,max(rs.VIEW_PERCETAGE) FROM RECORDED_STATS rs "
//                    + "where rs.UNIQUENAME='"+sAccountId+"' and rs.SESSION_ID not in"
//                    + "(SELECT rs.SESSION_ID FROM RECORDED_STATS rs where rs.UNIQUENAME='"+sAccountId+"'and rs.VIEW_PERCETAGE>90) "
//                    + "GROUP BY rs.SESSION_ID)as result  on c.SESSION_ID=result.SESSION_ID ";
            String inCompleteNativeSql = "select c.CLASS_ID,c.ACCESS_TO,c.AVG_RATING,c.BATCH_ID,c.CAN_EXTEND, "
                    + "c.CHAPTER_ID,c.CHAPTER_NAME,c.DURATION_MINUTES,c.END_DATE,c.FORCE_EXIT_PARTICIPANTS, "
                    + "c.GRADE_ID,c.GRADE_NAME,c.GUEST_URL,c.IS_RECORDED_SESSION_VIEABLE,c.NO_OF_SEATS, "
                    + "c.PREPARE_URL,c.PRESENT_URL,c.PRESENTER_DISPALY_NAME,c.PRESENTER_UNIQUE_NAME,c.REPLAY_URL, "
                    + "c.RESTART_SESSION,c.SCHEDULE_DATE,c.SESSION_ID,c.SUBJECT_ID,c.SUBJECT_NAME, "
                    + "c.SYLLABUS_ID,c.SYLLABUS_NAME,c.TEACHER_ID,c.TITLE,max(res.VIEW_PERCETAGE) "
                    + "from CLASS_SCHEDULER c INNER JOIN (select result.* FROM(SELECT p.SESSION_ID,p.VIEW_PERCETAGE FROM PARTICIPANTS p "
                    + "where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE<90 UNION "
                    + "SELECT rs.SESSION_ID,rs.VIEW_PERCETAGE FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' "
                    + "and rs.VIEW_PERCETAGE<90) as result  where result.SESSION_ID not in(SELECT p.SESSION_ID FROM PARTICIPANTS p "
                    + "where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE>90 UNION "
                    + "SELECT rs.SESSION_ID FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' and rs.VIEW_PERCETAGE>90)) as res "
                    + "on c.SESSION_ID=res.SESSION_ID";

//            String inCompleteNativeSql = "select c.CLASS_ID,c.ACCESS_TO,c.AVG_RATING,c.BATCH_ID,c.CAN_EXTEND, "
//                    + "c.CHAPTER_ID,c.CHAPTER_NAME,c.DURATION_MINUTES,c.END_DATE,c.FORCE_EXIT_PARTICIPANTS, "
//                    + "c.GRADE_ID,c.GRADE_NAME,c.GUEST_URL,c.IS_RECORDED_SESSION_VIEABLE,c.NO_OF_SEATS, "
//                    + "c.PREPARE_URL,c.PRESENT_URL,c.PRESENTER_DISPALY_NAME,c.PRESENTER_UNIQUE_NAME,c.REPLAY_URL, "
//                    + "c.RESTART_SESSION,c.SCHEDULE_DATE,c.SESSION_ID,c.SUBJECT_ID,c.SUBJECT_NAME, "
//                    + "c.SYLLABUS_ID,c.SYLLABUS_NAME,c.TEACHER_ID,c.TITLE,max(result.VIEW_PERCETAGE) "
//                    + "from CLASS_SCHEDULER c INNER JOIN ( "
//                    + " SELECT p.SESSION_ID,p.VIEW_PERCETAGE FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE<90 "
//                    + "UNION\n"
//                    + "SELECT rs.SESSION_ID,rs.VIEW_PERCETAGE FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' and rs.VIEW_PERCETAGE<90)"
//                    + "as result on c.SESSION_ID=result.SESSION_ID";
//            select  c.*,max(result.VIEW_PERCETAGE) from CLASS_SCHEDULER c INNER JOIN
//(SELECT p.SESSION_ID,p.VIEW_PERCETAGE FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='STUDENT000029'
//and p.VIEW_PERCETAGE<90
//UNION
//SELECT rs.SESSION_ID,rs.VIEW_PERCETAGE FROM RECORDED_STATS rs where rs.UNIQUENAME=
//'STUDENT000029' and rs.VIEW_PERCETAGE<90)
// as result group by result.SESSION_ID
//ORDER BY `result`.`SESSION_ID` ASC
            if (!"all".equals(filter.getSyllabus())) {
                inCompleteNativeSql = inCompleteNativeSql + " and  c.SYLLABUS_ID= '" + filter.getSyllabus() + "'";
            }
            if (!"all".equals(filter.getSubject())) {
                inCompleteNativeSql = inCompleteNativeSql + " and  c.SUBJECT_ID= '" + filter.getSubject() + "'";
            }

            if ("all".equals(startDate) && !"all".equals(enddate)) {
                inCompleteNativeSql = inCompleteNativeSql + " and DATE(c.SCHEDULE_DATE) <  '" + enddate + "'";
            }
            if (!"all".equals(startDate) && "all".equals(enddate)) {
                inCompleteNativeSql = inCompleteNativeSql + " and DATE(c.SCHEDULE_DATE) between  '" + startDate + "' AND CURRENT_DATE ";
            }

            if (!"all".equals(startDate) && !"all".equals(enddate)) {
                inCompleteNativeSql = inCompleteNativeSql + " and DATE(c.SCHEDULE_DATE) between  '" + startDate + "' AND '" + enddate + "' ";
            }
            if (text != null) {
                inCompleteNativeSql = inCompleteNativeSql + " and c.title LIKE '" + text + "%'";
            }
            inCompleteNativeSql = inCompleteNativeSql + " GROUP BY res.SESSION_ID";

            inCompleteNativeSql = inCompleteNativeSql + " order by c.SCHEDULE_DATE " + dateOrder + ",c.title " + titleOrder + " ";

            inCompleteNativeSql = inCompleteNativeSql + " limit " + offset + "," + limit + "";

            System.out.println("inCompleteNativeSql " + inCompleteNativeSql);

            List<Object> incompletedClassesList = this.service.getObjectsByNativeSqlQuery(inCompleteNativeSql);

            if (incompletedClassesList.size() > 0) {
                for (Object object : incompletedClassesList) {
                    Object[] objects = (Object[]) object;
                    JSONObject json = new JSONObject();
                    json.put("classId", objects[0]);
                    json.put("accessTo", objects[1]);
                    json.put("avgRating", objects[2]);
                    json.put("batchId", objects[3]);
                    json.put("extend", objects[4]);
                    json.put("chapterId", objects[5]);
                    json.put("chapterName", objects[6]);
                    json.put("endTime", objects[7]);
                    json.put("endDate", objects[8]);
                    json.put("forceExitParticipants", objects[9]);
                    json.put("gradeId", objects[10]);
                    json.put("gradeName", objects[11]);
                    json.put("guestUrl", objects[12]);
                    json.put("isRecordedSessionViewable", objects[13]);
                    json.put("noOfSeats", objects[14]);
                    json.put("prepareUrl", objects[15]);
                    json.put("presentUrl", objects[16]);
                    json.put("presenterDisplayName", objects[17]);
                    json.put("presenterUniqueName", objects[18]);
                    json.put("replayUrl", objects[19]);
                    json.put("restartSession", objects[20]);
                    Date date = (Date) objects[21];
                    json.put("scheduledDateWithformate", MyDateFormate.dateToString2(date));
                    json.put("scheduledDate", objects[21]);
                    json.put("sessionId", objects[22]);
                    json.put("subjectId", objects[23]);
                    json.put("subjectName", objects[24]);
                    json.put("syllabusId", objects[25]);
                    json.put("syllabusName", objects[26]);
                    json.put("teacherId", objects[27]);
                    json.put("title", objects[28]);
                    json.put("viewPercentage", objects[29]);
                    incompletedClassScheduler.add(json);
                }

            }

            jsonOfList.put("inCompletedClasses", incompletedClassScheduler);
            jsonOfList.put("inCompletedClassesCount", count2);
        } catch (Exception e) {
            jsonOfList.put("msg", "something went wrong");
            e.printStackTrace();

        }

        return new ResponseEntity<>(jsonOfList, HttpStatus.OK);

    }

    /**
     * Completed classe new url
     *
     * @return
     */
    @RequestMapping(value = {"/completedClasses"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> completedClasses(@RequestBody final FilterBean filter) {

        List<Object> completedClassScheduler = new ArrayList<>();

        JSONObject jsonOfList = new JSONObject();
        String sAccountId = filter.getsAccountId();
        String presenterDisplayName = filter.getTeacher();
        List<String> subjectFilter = filter.getSubjectFilter();
        List<String> syllabusFilter = filter.getSyllabusFilter();

        String startDate = filter.getStartDate();
        String enddate = filter.getEndDate();
        String pageNo = filter.getPageNo();
        String pageSize = filter.getPageSize();
        int offset = Integer.parseInt(pageNo) * Integer.parseInt(pageSize);
        int limit = Integer.parseInt(pageSize);

        String text = filter.getText();

        String dateOrder = filter.getDateOrder();
        String titleOrder = filter.getTitleOrder();

        try {

            /**
             * complete class count
             */
            String completedClassesCount = "select count(*) "
                    + "from CLASS_SCHEDULER c where c.SESSION_ID in "
                    + "(SELECT p.SESSION_ID FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE>90\n"
                    + "UNION\n"
                    + "SELECT rs.SESSION_ID FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' and rs.VIEW_PERCETAGE>90)";

            if (!"all".equals(filter.getSyllabus())) {
                completedClassesCount = completedClassesCount + " and  c.SYLLABUS_ID= '" + filter.getSyllabus() + "'";
            }
            if (!"all".equals(filter.getSubject())) {
                completedClassesCount = completedClassesCount + " and  c.SUBJECT_ID= '" + filter.getSubject() + "'";
            }

            if ("all".equals(startDate) && !"all".equals(enddate)) {
                completedClassesCount = completedClassesCount + " and DATE(c.SCHEDULE_DATE) <  '" + enddate + "'";
            }
            if (!"all".equals(startDate) && "all".equals(enddate)) {
                completedClassesCount = completedClassesCount + " and DATE(c.SCHEDULE_DATE) between  '" + startDate + "' AND CURRENT_DATE ";
            }

            if (!"all".equals(startDate) && !"all".equals(enddate)) {
                completedClassesCount = completedClassesCount + " and DATE(c.SCHEDULE_DATE) between  '" + startDate + "' AND '" + enddate + "' ";
            }
            if (text != null) {
                completedClassesCount = completedClassesCount + " and c.title LIKE '" + text + "%'";
            }

            Object count = service.getObjectsByNativeSqlQuery(completedClassesCount).get(0);

            /**
             * complete class list
             */
//          String sql=  "SELECT p.SESSION_ID FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE<90\n"
//                    + "UNION\n"
//                    + "SELECT rs.SESSION_ID FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' and rs.VIEW_PERCETAGE<90";
//
//              List<Object> incompletedClassesList = this.service.getObjectsByNativeSqlQuery(inCompleteNativeSql);
//            String inCompleteNativeSql = "select CLASS_ID,ACCESS_TO,AVG_RATING,BATCH_ID,CAN_EXTEND, "
//                    + "CHAPTER_ID,CHAPTER_NAME,DURATION_MINUTES,END_DATE,FORCE_EXIT_PARTICIPANTS, "
//                    + "GRADE_ID,GRADE_NAME,GUEST_URL,	IS_RECORDED_SESSION_VIEABLE,NO_OF_SEATS, "
//                    + "PREPARE_URL,PRESENT_URL,PRESENTER_DISPALY_NAME,PRESENTER_UNIQUE_NAME,REPLAY_URL, "
//                    + "RESTART_SESSION,SCHEDULE_DATE,c.SESSION_ID,SUBJECT_ID,SUBJECT_NAME, "
//                    + "SYLLABUS_ID,SYLLABUS_NAME,TEACHER_ID,TITLE,p.VIEW_PERCETAGE "
//                    + "from CLASS_SCHEDULER c INNER JOIN ( "
//                    + " SELECT p.SESSION_ID FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE<90 "
//                    + "UNION\n"
//                    + "SELECT rs.SESSION_ID FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' and rs.VIEW_PERCETAGE<90)";
            String completeNativeSql = "select c.CLASS_ID,c.ACCESS_TO,c.AVG_RATING,c.BATCH_ID,c.CAN_EXTEND, "
                    + "c.CHAPTER_ID,c.CHAPTER_NAME,c.DURATION_MINUTES,c.END_DATE,c.FORCE_EXIT_PARTICIPANTS, "
                    + "c.GRADE_ID,c.GRADE_NAME,c.GUEST_URL,c.IS_RECORDED_SESSION_VIEABLE,c.NO_OF_SEATS, "
                    + "c.PREPARE_URL,c.PRESENT_URL,c.PRESENTER_DISPALY_NAME,c.PRESENTER_UNIQUE_NAME,c.REPLAY_URL, "
                    + "c.RESTART_SESSION,c.SCHEDULE_DATE,c.SESSION_ID,c.SUBJECT_ID,c.SUBJECT_NAME, "
                    + "c.SYLLABUS_ID,c.SYLLABUS_NAME,c.TEACHER_ID,c.TITLE,max(result.VIEW_PERCETAGE) "
                    + "from CLASS_SCHEDULER c INNER JOIN ("
                    + " SELECT p.SESSION_ID,p.VIEW_PERCETAGE FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE>90 "
                    + "UNION\n"
                    + "SELECT rs.SESSION_ID,rs.VIEW_PERCETAGE FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' and rs.VIEW_PERCETAGE>90)"
                    + "as result on c.SESSION_ID=result.SESSION_ID";

//            select  c.*,max(result.VIEW_PERCETAGE) from CLASS_SCHEDULER c INNER JOIN
//(SELECT p.SESSION_ID,p.VIEW_PERCETAGE FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='STUDENT000029'
//and p.VIEW_PERCETAGE<90
//UNION
//SELECT rs.SESSION_ID,rs.VIEW_PERCETAGE FROM RECORDED_STATS rs where rs.UNIQUENAME=
//'STUDENT000029' and rs.VIEW_PERCETAGE<90)
// as result group by result.SESSION_ID
//ORDER BY `result`.`SESSION_ID` ASC
            if (!"all".equals(filter.getSyllabus())) {
                completeNativeSql = completeNativeSql + " and  c.SYLLABUS_ID= '" + filter.getSyllabus() + "'";
            }
            if (!"all".equals(filter.getSubject())) {
                completeNativeSql = completeNativeSql + " and  c.SUBJECT_ID= '" + filter.getSubject() + "'";
            }

            if ("all".equals(startDate) && !"all".equals(enddate)) {
                completeNativeSql = completeNativeSql + " and DATE(c.SCHEDULE_DATE) <  '" + enddate + "'";
            }
            if (!"all".equals(startDate) && "all".equals(enddate)) {
                completeNativeSql = completeNativeSql + " and DATE(c.SCHEDULE_DATE) between  '" + startDate + "' AND CURRENT_DATE ";
            }

            if (!"all".equals(startDate) && !"all".equals(enddate)) {
                completeNativeSql = completeNativeSql + " and DATE(c.SCHEDULE_DATE) between  '" + startDate + "' AND '" + enddate + "' ";
            }
            if (text != null) {
                completeNativeSql = completeNativeSql + " and c.title LIKE '" + text + "%'";
            }
            completeNativeSql = completeNativeSql + " group by result.SESSION_ID";

            completeNativeSql = completeNativeSql + " order by c.SCHEDULE_DATE " + dateOrder + ",c.title " + titleOrder + " ";

            completeNativeSql = completeNativeSql + " limit " + offset + "," + limit + "";

            System.out.println("completeNativeSql " + completeNativeSql);

            List<Object> completedClassesList = this.service.getObjectsByNativeSqlQuery(completeNativeSql);

            if (completedClassesList.size() > 0) {
                for (Object object : completedClassesList) {
                    Object[] objects = (Object[]) object;
                    JSONObject json = new JSONObject();
                    json.put("classId", objects[0]);
                    json.put("accessTo", objects[1]);
                    json.put("avgRating", objects[2]);
                    json.put("batchId", objects[3]);
                    json.put("extend", objects[4]);
                    json.put("chapterId", objects[5]);
                    json.put("chapterName", objects[6]);
                    json.put("endTime", objects[7]);
                    json.put("endDate", objects[8]);
                    json.put("forceExitParticipants", objects[9]);
                    json.put("gradeId", objects[10]);
                    json.put("gradeName", objects[11]);
                    json.put("guestUrl", objects[12]);
                    json.put("isRecordedSessionViewable", objects[13]);
                    json.put("noOfSeats", objects[14]);
                    json.put("prepareUrl", objects[15]);
                    json.put("presentUrl", objects[16]);
                    json.put("presenterDisplayName", objects[17]);
                    json.put("presenterUniqueName", objects[18]);
                    json.put("replayUrl", objects[19]);
                    json.put("restartSession", objects[20]);
                    Date date = (Date) objects[21];
                    json.put("scheduledDateWithformate", MyDateFormate.dateToString2(date));
                    json.put("scheduledDate", objects[21]);
                    json.put("sessionId", objects[22]);
                    json.put("subjectId", objects[23]);
                    json.put("subjectName", objects[24]);
                    json.put("syllabusId", objects[25]);
                    json.put("syllabusName", objects[26]);
                    json.put("teacherId", objects[27]);
                    json.put("title", objects[28]);
                    json.put("viewPercentage", objects[29]);
                    completedClassScheduler.add(json);
                }
            }

            jsonOfList.put("completedClasses", completedClassScheduler);
            jsonOfList.put("completedClassesCount", count);
        } catch (Exception e) {
            jsonOfList.put("msg", "something went wrong");
            e.printStackTrace();

        }

        return new ResponseEntity<>(jsonOfList, HttpStatus.OK);

    }

    /**
     * original /save_Live_classDetails
     *
     * @param mapBean
     * @return
     */
    @RequestMapping(value = {"/save_Live_classDetails"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> save_Live_classDetails(@RequestBody final Map<String, String> mapBean
    ) {
        JSONObject json = new JSONObject();
        String studentAccountId = mapBean.get("studentAccountId");
        String sql = "SELECT u.sessionId FROM UserHistory u WHERE u.accountId = '" + studentAccountId + "'";
        System.out.println("sql " + sql);
        List<Object> list = service.getObject(sql);

        if (list.size() > 0) {
            for (Object object : list) {
                if (object instanceof String) {
                    String sessionId = (String) object;
                    System.out.println("session id " + sessionId);
                    String sql1 = "SELECT COUNT(*) FROM LearnTronLiveStats l WHERE l.sessionId = '" + sessionId + "' and participantUniqueName='" + studentAccountId + "'";
                    int count = (int) service.countObject(sql1);
                    System.out.println("count " + count);
                    if (count == 0) {
                        LearnTronLiveStats learnTronLiveStats = new LearnTronLiveStats();
                        String id = (String) pKGenerator.generate(LearnTronLiveStats.class, "STAT");
                        learnTronLiveStats.setStatId(id);
                        learnTronLiveStats.setSessionId(sessionId);
                        learnTronLiveStats.setParticipantUniqueName(studentAccountId);

                        System.out.println("learnTronLiveStats " + learnTronLiveStats);
                        System.out.println("studentAccountId " + studentAccountId);
                        System.out.println("sessionId " + sessionId);

                        JSONObject j = getLiveSessionAnalytics(learnTronLiveStats, studentAccountId, sessionId);
                        System.out.println("msg " + j);
                        json.put("msg", j.get("msg"));
                    }
                }
            }
        } else {
            json.put("msg", "No Record found!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    public JSONObject getLiveSessionAnalytics(LearnTronLiveStats learnTronLiveStats, String studentAccountId, String sessionId) {
        JSONObject json = new JSONObject();
        System.out.println("this is calling");
        try {
            service.save(learnTronLiveStats);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Learntron-Api-Token", "6zeyiZJr7mlEiABFuVSuZhIr4xKxrOCYUqqjS1YuNQQN1TG188rzexKU66NrhnWw");

            String URL = "https://liveapiindia.learntron.net/api/learntron/getLiveSessionAnalytics/" + sessionId;

            HttpEntity entity = new HttpEntity("parameters", headers);

            ResponseEntity<JSONArray> responseEntity = restTemplate.exchange(URL, HttpMethod.GET, entity, JSONArray.class);
            System.out.println("response entity " + responseEntity);
            /*getting JSONArray body from responseEntity*/
            JSONArray jarr1 = responseEntity.getBody();
            for (Object object : jarr1) {
                LinkedHashMap map = (LinkedHashMap) object;

                String instructorFirstName = (String) map.get("instructorFirstName");
                String instructorLastName = (String) map.get("instructorLastName");

                /*getting original start and end time of the classSchedular*/
                String startTime_0 = (String) map.get("startTime");
                String endTime_0 = (String) map.get("endTime");

                Date dStart_0 = MyDateFormate.stringToDate_1(startTime_0);
                Date dEnd_0 = MyDateFormate.stringToDate_1(endTime_0);

                /*calculating the time duration*/
                long timeduration_0 = dEnd_0.getTime() - dStart_0.getTime();
                int timedurationINT_0 = (int) timeduration_0;

                LearnTronLiveStatsDetails learnTronLiveStatsDetails = new LearnTronLiveStatsDetails();
                String id = (String) pKGenerator.generate(LearnTronLiveStatsDetails.class, "STATDETAIL");
                learnTronLiveStatsDetails.setStatdetailId(id);
                learnTronLiveStatsDetails.setSessionId(sessionId);
                learnTronLiveStatsDetails.setInstructorFirstName(instructorFirstName);
                learnTronLiveStatsDetails.setInstructorLastName(instructorLastName);
                learnTronLiveStatsDetails.setParticipantUniqueName(studentAccountId);
                learnTronLiveStatsDetails.setTimeDuration(timedurationINT_0);
                learnTronLiveStatsDetails.setLearnTronLiveStats(learnTronLiveStats);
                service.save(learnTronLiveStatsDetails);
                ArrayList alistParticipants = (ArrayList) map.get("participants");
                if (alistParticipants.size() > 0) {
                    for (Object obj : alistParticipants) {
                        LinkedHashMap linkedHashMap = (LinkedHashMap) obj;
                        String participantUniqueName = (String) linkedHashMap.get("participantUniqueName");
                        String participantDisplayName = (String) linkedHashMap.get("participantDisplayName");
                        if (participantUniqueName.equals(studentAccountId)) {
                            ArrayList alist_1 = (ArrayList) linkedHashMap.get("durationInfos");
                            int addingtimeduration = 0;
                            for (Object object1 : alist_1) {
                                LinkedHashMap linkedHashMap_1 = (LinkedHashMap) object1;
                                String startTime = (String) linkedHashMap_1.get("startTime");
                                String endTime = (String) linkedHashMap_1.get("endTime");

                                Date dStart = MyDateFormate.stringToDate_1(startTime);
                                Date dEnd = MyDateFormate.stringToDate_1(endTime);

                                int timeduration = (int) (dEnd.getTime() - dStart.getTime());
                                addingtimeduration += timeduration;
                            }

                            int totalDuration = learnTronLiveStatsDetails.getTimeDuration();
                            float percentage = 0.0f;
                            if (addingtimeduration > 0) {
                                percentage = (addingtimeduration * 100.0f / totalDuration);
                                if (percentage >= 100.0f) {
                                    percentage = 100.0f;
                                }
                            }

                            Participants participants = new Participants();
                            String id_ = (String) pKGenerator.generate(Participants.class, "PART");
                            participants.setParticipantId(id_);
                            participants.setSessionId(sessionId);
                            participants.setParticipantUniqueName(participantUniqueName);
                            participants.setParticipantDisplayName(participantDisplayName);
                            participants.setViewDuration(addingtimeduration);
                            participants.setViewPercentage(percentage);
                            participants.setLearnTronLiveStatsDetails(learnTronLiveStatsDetails);
                            service.save(participants);
                            json.put("msg", "LearnTron stats data saved!");
                        }
                    }
                }
            }

        } catch (ParseException | RestClientException e) {
            json.put("msg", "Something went wrong.");
        }
        return json;
    }

    //    @RequestMapping(value = {"/save_Live_classDetails"}, method = RequestMethod.POST)
//    public @ResponseBody
//    ResponseEntity<?> save_Live_classDetails(@RequestBody
//            final Map<String, String> mapBean
//    ) {
//        JSONObject json = new JSONObject();
//        String studentAccountId = mapBean.get("studentAccountId");
//        String sql = "SELECT u.sessionId FROM UserHistory u WHERE u.accountId = '" + studentAccountId + "'";
//        System.out.println("sql " + sql);
//        List<Object> list = service.getObject(sql);
//
//        if (list.size() > 0) {
//            for (Object object : list) {
//                if (object instanceof String) {
//                    String sessionId = (String) object;
//                    System.out.println("session id " + sessionId);
//                    String sql1 = "SELECT COUNT(*) FROM LearnTronLiveStats l WHERE l.sessionId = '" + sessionId + "' "
//                            + "and participantUniqueName='" + studentAccountId + "'";
////                    String sql1 = "SELECT COUNT(*) FROM Participants l WHERE l.sessionId = '" + sessionId + "' "
////                            + "and participantUniqueName='" + studentAccountId + "'";
//                    int count = (int) service.countObject(sql1);
//                    System.out.println("count " + count);
//                    if (count == 0) {
//                        LearnTronLiveStats learnTronLiveStats = new LearnTronLiveStats();
//                        String id = (String) pKGenerator.generate(LearnTronLiveStats.class, "STAT");
//                        learnTronLiveStats.setStatId(id);
//                        learnTronLiveStats.setSessionId(sessionId);
//                        learnTronLiveStats.setParticipantUniqueName(studentAccountId);
//
//                        System.out.println("learnTronLiveStats " + learnTronLiveStats);
//                        System.out.println("studentAccountId " + studentAccountId);
//                        System.out.println("sessionId " + sessionId);
//
//                        JSONObject j = getLiveSessionAnalytics(learnTronLiveStats, studentAccountId, sessionId);
//                        System.out.println("msg " + j);
//                        json.put("msg", j.get("msg"));
//                    }
//                }
//            }
//        } else {
//            json.put("msg", "No Record found!");
//        }
//
//        return new ResponseEntity<>(json, HttpStatus.OK);
//    }
//
//    public JSONObject getLiveSessionAnalytics(LearnTronLiveStats learnTronLiveStats, String studentAccountId, String sessionId) {
//        JSONObject json = new JSONObject();
//        System.out.println("this is calling");
//        try {
//            service.save(learnTronLiveStats);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Learntron-Api-Token", "6zeyiZJr7mlEiABFuVSuZhIr4xKxrOCYUqqjS1YuNQQN1TG188rzexKU66NrhnWw");
//
//            String URL = "https://liveapiindia.learntron.net/api/learntron/getLiveSessionAnalytics/" + sessionId;
////            String URL = "http://liveapiindia.learntron.net/api/learntron/getLiveSessionAnalytics/" + sessionId;
//
//            HttpEntity entity = new HttpEntity("parameters", headers);
//
//            ResponseEntity<JSONArray> responseEntity = restTemplate.exchange(URL, HttpMethod.GET, entity, JSONArray.class);
//            System.out.println("response entity " + responseEntity);
//            /*getting JSONArray body from responseEntity*/
//            JSONArray jarr1 = responseEntity.getBody();
//            for (Object object : jarr1) {
//                LinkedHashMap map = (LinkedHashMap) object;
//
//                String instructorFirstName = (String) map.get("instructorFirstName");
//                String instructorLastName = (String) map.get("instructorLastName");
//
//                /*getting original start and end time of the classSchedular*/
//                String startTime_0 = (String) map.get("startTime");
//                String endTime_0 = (String) map.get("endTime");
//
//                System.out.println("startTime_0 " + startTime_0);
//                System.out.println("endTime_0 " + endTime_0);
//
////                SimpleDateFormat format = new SimpleDateFormat(
////                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
////                format.setTimeZone(TimeZone.getTimeZone("IST"));
////                Date date = format.parse(startTime_0);
////                System.out.println("date time " + date.getTime());
////                SimpleDateFormat format2 = new SimpleDateFormat(
////                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
////                format.setTimeZone(TimeZone.getTimeZone("IST"));
////                Date date2 = format2.parse(startTime_0);
////                System.out.println("date time " + date2.getTime());
////                long differebce = date2.getTime() - date.getTime();
////                System.out.println("differebce time " + differebce);
//
//                Date dStart_0 = MyDateFormate.stringToDate_1(startTime_0);
//                Date dEnd_0 = MyDateFormate.stringToDate_1(endTime_0);
////                long timeduration_0 = date.getTime() - date2.getTime();
//                /*calculating the time duration*/
//                long timeduration_0 = dEnd_0.getTime() - dStart_0.getTime();
//                int timedurationINT_0 = (int) timeduration_0;
//
//                LearnTronLiveStatsDetails learnTronLiveStatsDetails = new LearnTronLiveStatsDetails();
//                String id = (String) pKGenerator.generate(LearnTronLiveStatsDetails.class, "STATDETAIL");
//                learnTronLiveStatsDetails.setStatdetailId(id);
//                learnTronLiveStatsDetails.setSessionId(sessionId);
//                learnTronLiveStatsDetails.setInstructorFirstName(instructorFirstName);
//                learnTronLiveStatsDetails.setInstructorLastName(instructorLastName);
//                learnTronLiveStatsDetails.setParticipantUniqueName(studentAccountId);
//                learnTronLiveStatsDetails.setTimeDuration(timedurationINT_0);
//                learnTronLiveStatsDetails.setLearnTronLiveStats(learnTronLiveStats);
//                service.save(learnTronLiveStatsDetails);
//                ArrayList alistParticipants = (ArrayList) map.get("participants");
//                if (alistParticipants.size() > 0) {
//                    for (Object obj : alistParticipants) {
//                        LinkedHashMap linkedHashMap = (LinkedHashMap) obj;
//                        String participantUniqueName = (String) linkedHashMap.get("participantUniqueName");
//                        String participantDisplayName = (String) linkedHashMap.get("participantDisplayName");
//                        if (participantUniqueName.equals(studentAccountId)) {
//                            ArrayList alist_1 = (ArrayList) linkedHashMap.get("durationInfos");
//                            int addingtimeduration = 0;
//                            for (Object object1 : alist_1) {
//                                LinkedHashMap linkedHashMap_1 = (LinkedHashMap) object1;
//                                String startTime = (String) linkedHashMap_1.get("startTime");
//                                String endTime = (String) linkedHashMap_1.get("endTime");
//                                System.out.println("startTime " + startTime);
//                                System.out.println("endTime " + endTime);
//                                Date dStart = MyDateFormate.stringToDate_1(startTime);
//                                Date dEnd = MyDateFormate.stringToDate_1(endTime);
//
//                                int timeduration = (int) (dEnd.getTime() - dStart.getTime());
//                                addingtimeduration += timeduration;
//                            }
//
//                            int totalDuration = learnTronLiveStatsDetails.getTimeDuration();
//                            float percentage = 0.0f;
//                            if (addingtimeduration > 0) {
//                                percentage = (addingtimeduration * 100.0f / totalDuration);
//                                if (percentage >= 100.0f) {
//                                    percentage = 100.0f;
//                                }
//                            }
//
//                            Participants participants = new Participants();
//                            String id_ = (String) pKGenerator.generate(Participants.class, "PART");
//                            participants.setParticipantId(id_);
//                            participants.setSessionId(sessionId);
//                            participants.setParticipantUniqueName(participantUniqueName);
//                            participants.setParticipantDisplayName(participantDisplayName);
//                            participants.setViewDuration(addingtimeduration);
//                            participants.setViewPercentage(percentage);
//                            participants.setLearnTronLiveStatsDetails(learnTronLiveStatsDetails);
//                            service.save(participants);
//                            json.put("msg", "LearnTron stats data saved!");
//                        }
//                    }
//                }
//            }
//
//        } catch (ParseException | RestClientException e) {
//
//            json.put("msg", "Something went wrong.");
//        }
//        return json;
//    }
    @RequestMapping(value = {"/save_Recorded_classDetails"})
    public @ResponseBody
    ResponseEntity<?> save_Recorded_classDetails(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        String studentAccountId = mapBean.get("studentAccountId");
        String sessionId = mapBean.get("sessionId");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Learntron-Api-Token", "6zeyiZJr7mlEiABFuVSuZhIr4xKxrOCYUqqjS1YuNQQN1TG188rzexKU66NrhnWw");

            String URL = "https://liveapiindia.learntron.net/api/learntron/getSessionRecordingViews/" + sessionId;

            HttpEntity entity = new HttpEntity("parameters", headers);

            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(URL, HttpMethod.GET, entity, JSONObject.class);

            JSONObject jsonRespEntity = responseEntity.getBody();

            /*get total recorded duration*/
            int recordingDuration = (int) jsonRespEntity.get("recordingDuration");
            if (recordingDuration > 0) {

                ArrayList alistRecordingViews = (ArrayList) jsonRespEntity.get("recordingViews");
                String uniqueName = "";
                String displayName = "";
                ArrayList<Integer> alistEnd = new ArrayList<>();
                for (Object alistRecordingView : alistRecordingViews) {
                    LinkedHashMap map = (LinkedHashMap) alistRecordingView;

                    if (((String) map.get("uniqueName")).equals(studentAccountId)) {
                        uniqueName = (String) map.get("uniqueName");
                        displayName = (String) map.get("displayName");

                        String viewedMetaData = (String) map.get("viewedMetaData");

                        String s = viewedMetaData.split("\\:(?=[^\\:]+$)")[1];
                        String endStr = s.split("\\}(?=[^\\}]+$)")[0];
                        int end = Integer.parseInt(endStr.split("\\.(?=[^\\.]+$)")[0]);

                        alistEnd.add(end);
                    }
                }
                int endDuration;
                if (!uniqueName.equals("")) {
                    if (alistEnd.size() > 0) {
                        endDuration = Collections.max(alistEnd);
                    } else {
                        endDuration = 0;
                    }

                    float percentage = 0.0f;
                    if (endDuration > 0) {
                        percentage = (endDuration * 100.0f / recordingDuration);
                        if (percentage >= 100.0f) {
                            percentage = 100.0f;
                        }
                    }

                    LearnTronRecordedStats learnTronRecordedStats = new LearnTronRecordedStats();
                    String id = (String) pKGenerator.generate(LearnTronRecordedStats.class, "RECSTAT");
                    learnTronRecordedStats.setRecordedStatsId(id);
                    learnTronRecordedStats.setSessionId(sessionId);
                    learnTronRecordedStats.setUniqueName(studentAccountId);
                    learnTronRecordedStats.setDisplayName(displayName);
                    learnTronRecordedStats.setRecordingDuration(recordingDuration);
                    learnTronRecordedStats.setEndDuration(endDuration);
                    learnTronRecordedStats.setViewPercentage(percentage);

                    Map<String, Object> map = new HashMap<>();
                    map.put("sessionId", sessionId);
                    map.put("uniqueName", uniqueName);

                    List<Object> list = service.getObject(LearnTronRecordedStats.class, map);
                    if (list.isEmpty()) {
                        service.save(learnTronRecordedStats);
                    } else {
                        LearnTronRecordedStats learnTronRecordedStats1 = (LearnTronRecordedStats) list.get(0);
                        int endDuration1 = learnTronRecordedStats1.getEndDuration();
                        if (endDuration1 < endDuration) {
                            learnTronRecordedStats1.setEndDuration(endDuration);
                            learnTronRecordedStats1.setViewPercentage(percentage);
                            service.update(learnTronRecordedStats1);
                        }
                    }
                    json.put("msg", "Updated!");
                }
            } else {
                json.put("msg", "No Recording Found!");
            }

        } catch (NumberFormatException | RestClientException e) {
            json.put("msg", "Something went wrong.");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getClassViewDetailsByStudentIdAndSessionId"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getClassViewDetailsByStudentIdAndSessionId(@RequestBody final FilterBean filter) {
//        String scheduledDate = "";
//        if (filter.getScheduledDate() != null) {
//            scheduledDate = filter.getScheduledDate();
//        }
        List<JSONObject> ids = filter.getIds();
        JSONArray jSONArray = new JSONArray();
        for (JSONObject jSONObjects : ids) {
            JSONObject json = new JSONObject();
            String sessionId = (String) jSONObjects.get("sessionId");
            String participantUniqueName = filter.getStudentAccountId();
//        String sessionId = filter.getSessionId();
            String presenterDisplayName = filter.getTeacher();
            List<String> subjectFilter = filter.getSubjectFilter();
            List<String> syllabusFilter = filter.getSyllabusFilter();
            String startDate = filter.getStartDate();
            String enddate = filter.getEndDate();
            Map<String, String> map = new HashMap<>();
            List<Float> liViewPercentage_1_LiveStats = new ArrayList<>();
            float maxViewPercentage_RecStats = 0.0f;
            List<Float> liViewPercentage_1_RecStats = new ArrayList<>();
            List<Float> liViewPercentage = new ArrayList<>();
            float maxViewPercentage_LiveStats;
            float maxViewPercentage = 0.0f;
            /*getting records(maxViewPercentage_LiveStats) from live stats*/
            String sql1 = "SELECT p.viewPercentage FROM Participants p WHERE p.participantUniqueName = '" + participantUniqueName + "' "
                    + "AND p.sessionId = '" + sessionId + "'";
            List<Object> liViewPercentage_LiveStats = service.getObject(sql1);
            if (liViewPercentage_LiveStats.size() > 0) {
                System.out.println("inside first query");
                for (Object object : liViewPercentage_LiveStats) {
                    float viewPercentage_LiveStats = (float) object;
                    liViewPercentage_1_LiveStats.add(viewPercentage_LiveStats);
                }
                maxViewPercentage_LiveStats = Collections.max(liViewPercentage_1_LiveStats);
                liViewPercentage.add(maxViewPercentage_LiveStats);
                map.put(maxViewPercentage_LiveStats + "", "liveStats");
            }
            /*getting records(maxViewPercentage_RecStats) from Rec stats*/
            String sql2 = "SELECT l.viewPercentage FROM LearnTronRecordedStats l WHERE l.uniqueName = '" + participantUniqueName + "' "
                    + "AND l.sessionId = '" + sessionId + "'";
            System.out.println("sql2 " + sql2);
            List<Object> liViewPercentage_RecStats = service.getObject(sql2);
            if (liViewPercentage_RecStats.size() > 0) {
                System.out.println("inside second query");
                for (Object object : liViewPercentage_RecStats) {
                    float viewPercentage_RecStats = (float) object;
                    liViewPercentage_1_RecStats.add(viewPercentage_RecStats);
                }
                maxViewPercentage_RecStats = Collections.max(liViewPercentage_1_RecStats);
            }
            liViewPercentage.add(maxViewPercentage_RecStats);
            map.put(maxViewPercentage_RecStats + "", "recStats");
            /*getting maxViewPercentage*/
            maxViewPercentage = Collections.max(liViewPercentage);

            /*getting stat (RecStat or LiveStat)*/
            TreeSet<String> set = new TreeSet<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String k = entry.getKey();
                set.add(k);
            }
            String key = "";
            for (String string : set) {
                if (string.equals(maxViewPercentage + "")) {
                    key = string;
                }
            }
            String stat = map.get(key);
            System.out.println("maxViewPercentage " + maxViewPercentage);
            /*getting classSchedular based on sessionId*/
            if (maxViewPercentage >= 90) {
                String sqlClassScheduler = "FROM ClassScheduler c WHERE c.sessionId = '" + sessionId + "'";

                if (presenterDisplayName != null) {
                    sqlClassScheduler = sqlClassScheduler + " AND c.presenterDisplayName = '" + presenterDisplayName + "'";
                }

                if (syllabusFilter != null) {
                    sqlClassScheduler = sqlClassScheduler + " AND c.syllabusId = '" + syllabusFilter.get(0) + "'";
                }

                if (subjectFilter != null) {
                    sqlClassScheduler = sqlClassScheduler + " AND c.subjectId = '" + subjectFilter.get(0) + "'";
                }

                if (startDate == null && enddate != null) {
                    sqlClassScheduler = sqlClassScheduler + " AND c.scheduledDate <  '" + enddate + "'";
                }
                if (startDate != null && enddate == null) {
                    sqlClassScheduler = sqlClassScheduler + " AND DATE(c.scheduledDate) between  '" + startDate + "' AND current_date() ";
                }
                if (startDate != null && enddate != null) {
                    sqlClassScheduler = sqlClassScheduler + " AND DATE(c.scheduledDate) between  '" + startDate + "' AND '" + enddate + "' ";
                }
//                if (!"".equals(scheduledDate)) {
//                    sqlClassScheduler = sqlClassScheduler + " AND DATE(c.scheduledDate) = '" + scheduledDate + "'";
//                }
                System.out.println("****************" + sqlClassScheduler);
                List<Object> listObj = service.getObject(String.format("%s", sqlClassScheduler));
                if (listObj.size() > 0) {
                    ClassScheduler classScheduler = (ClassScheduler) listObj.get(0);
                    json = getClassSchedulerJson(classScheduler);
                    json.put("maxViewPercentage", maxViewPercentage);
                    json.put("stat", stat);
                    String sql3 = "SELECT COUNT(*) FROM Rating r WHERE r.sesionId = '" + sessionId + "'";
                    int count = (int) service.countObject(sql3);
                    if (count > 0) {
                        json.put("totalNumOfStudentForRating", count);
                    } else {
                        json.put("totalNumOfStudentForRating", 0);
                    }
                } else {
                    json.put("msg", "No Record Found!");
                }
            } else {
                json.put("msg", "View duration is below 90%.");
            }
//            if(!json.containsKey("msg")){
//
//            }
            jSONArray.add(json);
        }
//        } else {
//            System.out.println("22222222222222222222222222");
//            json.put("msg", "No Record Found!");
//        }
        return new ResponseEntity<>(jSONArray, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getClassViewStatsBelowFiftyPercentage"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getClassViewStatsBelowFiftyPercentage(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        String participantUniqueName = mapBean.get("studentAccountId");
        String sessionId = mapBean.get("sessionId");

        /*getting records(maxViewPercentage_LiveStats) from live stats*/
        String sql1 = "SELECT p.viewPercentage FROM Participants p WHERE p.participantUniqueName = '" + participantUniqueName + "' AND p.sessionId = '" + sessionId + "'";
        List<Object> liViewPercentage_LiveStats = service.getObject(sql1);
        if (liViewPercentage_LiveStats.size() > 0) {
            Map<String, String> map = new HashMap<>();

            List<Float> liViewPercentage = new ArrayList<>();
            float maxViewPercentage_LiveStats;
            List<Float> liViewPercentage_1_LiveStats = new ArrayList<>();
            for (Object object : liViewPercentage_LiveStats) {
                float viewPercentage_LiveStats = (float) object;
                liViewPercentage_1_LiveStats.add(viewPercentage_LiveStats);
            }
            maxViewPercentage_LiveStats = Collections.max(liViewPercentage_1_LiveStats);
            liViewPercentage.add(maxViewPercentage_LiveStats);
            map.put(maxViewPercentage_LiveStats + "", "liveStats");

            /*getting records(maxViewPercentage_RecStats) from Rec stats*/
            String sql2 = "SELECT l.viewPercentage FROM LearnTronRecordedStats l WHERE l.uniqueName = '" + participantUniqueName + "' AND l.sessionId = '" + sessionId + "'";
            List<Object> liViewPercentage_RecStats = service.getObject(sql2);
            float maxViewPercentage_RecStats = 0.0f;
            if (liViewPercentage_RecStats.size() > 0) {
                List<Float> liViewPercentage_1_RecStats = new ArrayList<>();
                for (Object object : liViewPercentage_RecStats) {
                    float viewPercentage_RecStats = (float) object;
                    liViewPercentage_1_RecStats.add(viewPercentage_RecStats);
                }
                maxViewPercentage_RecStats = Collections.max(liViewPercentage_1_RecStats);
            }
            liViewPercentage.add(maxViewPercentage_RecStats);
            map.put(maxViewPercentage_RecStats + "", "recStats");

            /*getting maxViewPercentage*/
            float maxViewPercentage = Collections.max(liViewPercentage);

            /*getting stat (RecStat or LiveStat)*/
            TreeSet<String> set = new TreeSet<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String k = entry.getKey();
                set.add(k);
            }
            String key = "";
            for (String string : set) {
                if (string.equals(maxViewPercentage + "")) {
                    key = string;
                }
            }

            String stat = map.get(key);

            /*getting classSchedular based on sessionId*/
            if (maxViewPercentage > 50) {
                json.put("msg", "View duration is above 50%.");
            } else {
                Map<String, Object> mapClassSch = new HashMap<>();
                mapClassSch.put("sessionId", sessionId);
                ClassScheduler classScheduler = (ClassScheduler) service.getObject(ClassScheduler.class, mapClassSch).get(0);
                json = getClassSchedulerJson(classScheduler);
                json.put("maxViewPercentage", maxViewPercentage);
                json.put("stat", stat);

                String sql3 = "SELECT COUNT(*) FROM Rating r WHERE r.sesionId = '" + sessionId + "'";
                int count = (int) service.countObject(sql3);
                if (count > 0) {
                    json.put("totalNumOfStudentForRating", count);
                } else {
                    json.put("totalNumOfStudentForRating", 0);
                }

            }

        } else {
            json.put("msg", "No Record Found!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/createBatch"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> createBatch(@RequestBody final BatchBean bean) {
        JSONObject json = new JSONObject();
        try {
            Batch batch = new Batch();
            batch.setBatchId(String.format("%s", (String) pKGenerator.generate(Batch.class, "BATCH")));
            batch.setBatchName(bean.getBatchName());
            batch.setSyllabusId(bean.getSyllabusId());
            batch.setGradeId(bean.getGradeId());
            batch.setDescription(bean.getDescription());

            // TimeZone tz = TimeZone.getTimeZone("IST");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
            // df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());
            Date current = MyDateFormate.stringToDate(nowAsISO);
            batch.setDateOfCreation(current);

            if (service.save(batch) > 0) {
                json.put("msg", "New batch created.");
                json.put("batchId", batch.getBatchId());
                json.put("batchName", batch.getBatchName());
                json.put("syllabusId", bean.getSyllabusId());
                json.put("gradeId", bean.getGradeId());
                json.put("description", bean.getDescription());
                json.put("dateOfCreation", nowAsISO);

                String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + bean.getGradeId() + "'")).get(0);
                String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + bean.getSyllabusId() + "'")).get(0);

                json.put("gradeName", gradeName);
                json.put("syllabusName", syllabusName);
            } else {
                json.put("msg", "New batch not created.");
            }
        } catch (Exception e) {
            json.put("msg", "Something went wrong. Try again.");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/listBatch"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> listBatch(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        int pageNo = Integer.parseInt(mapBean.get("pageNo"));

        int maxResult = 10;
        if (mapBean.get("maxResult") != null) {
            maxResult = Integer.parseInt(mapBean.get("maxResult"));
        }

        List<JSONObject> list = new ArrayList<>();
        int count = 0;
        try {
            count = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Batch b"));
            List<Object> listBatch = service.loadByLimit(String.format("%s", "FROM Batch b ORDER BY b.dateOfCreation DESC"), (pageNo * maxResult), maxResult);
            for (Object object : listBatch) {
                Batch b = (Batch) object;
                JSONObject j = new JSONObject();
                j.put("batchId", b.getBatchId());
                j.put("batchName", b.getBatchName());
                j.put("description", b.getDescription());
                j.put("dateOfCreation", b.getDateOfCreation());

                String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + b.getSyllabusId() + "'")).get(0);
                String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + b.getGradeId() + "'")).get(0);
                j.put("syllabusName", syllabusName);
                j.put("gradeName", gradeName);

                int countOfAssignedStudent = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM StudentSubscription s WHERE s.batchId = '" + b.getBatchId() + "'"));
//                int countOfAssignedStudent = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM StudentAccount s WHERE s.batchId = '" + b.getBatchId() + "'"));
                j.put("countOfAssignedStudent", countOfAssignedStudent);
//                j.put("seatLeft", (20 - countOfAssignedStudent));
                j.put("seatLeft", (1000 - countOfAssignedStudent));

                list.add(j);
            }
        } catch (Exception e) {
            JSONObject j = new JSONObject();
            j.put("msg", "Something went wrong. Try again!");
        }
        json.put("count", count);
        json.put("batches", list);

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getBatch"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getBatch(@RequestBody final Map<String, String> mapBean) {
        String batchId = mapBean.get("batchId");
        JSONObject json = new JSONObject();
        try {
            List<Object> listBatchObject = service.getObject(String.format("%s", "FROM Batch b WHERE b.batchId = '" + batchId + "'"));
            if (listBatchObject.size() > 0) {
                Batch batch = (Batch) listBatchObject.get(0);
                json.put("batchId", batch.getBatchId());
                json.put("batchName", batch.getBatchName());
                json.put("description", batch.getDescription());
                json.put("dateOfCreation", batch.getDateOfCreation());
                json.put("gradeId", batch.getGradeId());
                json.put("syllabusId", batch.getSyllabusId());

                String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + batch.getSyllabusId() + "'")).get(0);
                String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + batch.getGradeId() + "'")).get(0);
                json.put("syllabusName", syllabusName);
                json.put("gradeName", gradeName);
            } else {
                json.put("msg", "Batch not found!");
            }
        } catch (Exception e) {
            json.put("msg", "Something went wrong!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getBatchByGradeIdAndSyllabusId"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getBatchByGradeIdAndSyllabusId(@RequestBody final Map<String, String> mapBean) {

        JSONObject jSONObject = new JSONObject();
        int pageNo = Integer.parseInt(mapBean.get("pageNo"));

        int maxResult = 10;
        if (mapBean.get("maxResult") != null) {
            maxResult = Integer.parseInt(mapBean.get("maxResult"));
        }
        String gradeId = mapBean.get("gradeId");
        String syllabusId = mapBean.get("syllabusId");
        String dateOrder = mapBean.get("dateOrder");
        List<JSONObject> list = new ArrayList<>();
        JSONObject json;
        long totalCount = 0;
        long retrievedResultCount = 0;
        try {

            String SqlQuery = "FROM Batch b where b.batchId is not null";

            if (!"all".equals(gradeId)) {
                SqlQuery = SqlQuery + " AND b.gradeId = '" + gradeId + "'";
            }
            if (!"all".equals(syllabusId)) {
                SqlQuery = SqlQuery + " AND b.syllabusId = '" + syllabusId + "'";
            }

            SqlQuery = SqlQuery + " order by b.dateOfCreation " + dateOrder + "";

//            String SqlQuery = "FROM Batch b WHERE b.syllabusId = '" + syllabusId + "' AND b.gradeId = '" + gradeId + "' order by b.dateOfCreation " + dateOrder + "";
            System.out.println("SqlQuery " + SqlQuery);

            totalCount = this.service.countObject("select count(*) FROM Batch b");

            retrievedResultCount = this.service.countObject("select count(*) " + SqlQuery);

            List<Object> listBatchObject = service.loadByLimit(String.format("%s", SqlQuery), (pageNo * maxResult), maxResult);

//            List<Object> listBatchObject = service.getObject(String.format("%s", "FROM Batch b WHERE b.syllabusId = '" + syllabusId + "' AND b.gradeId = '" + gradeId + "'"));
            if (listBatchObject.size() > 0) {
                for (Object object : listBatchObject) {
                    Batch batch = (Batch) object;
                    json = new JSONObject();
                    json.put("batchId", batch.getBatchId());
                    json.put("batchName", batch.getBatchName());
                    json.put("description", batch.getDescription());
                    json.put("dateOfCreation", batch.getDateOfCreation());
                    json.put("gradeId", batch.getGradeId());
                    json.put("syllabusId", batch.getSyllabusId());

                    String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + batch.getSyllabusId() + "'")).get(0);
                    String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + batch.getGradeId() + "'")).get(0);
                    json.put("syllabusName", syllabusName);
                    json.put("gradeName", gradeName);

                    int countOfAssignedStudent = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM StudentSubscription s WHERE s.batchId = '" + batch.getBatchId() + "'"));
//                int countOfAssignedStudent = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM StudentAccount s WHERE s.batchId = '" + b.getBatchId() + "'"));
                    json.put("countOfAssignedStudent", countOfAssignedStudent);
//                j.put("seatLeft", (20 - countOfAssignedStudent));
                    json.put("seatLeft", (1000 - countOfAssignedStudent));
                    list.add(json);
                }

            } else {
                json = new JSONObject();
                json.put("msg", "No Batch found.");
                list.add(json);
            }

        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Something went wrong.");
            list.add(json);
        }

        jSONObject.put("count", totalCount);
        jSONObject.put("no of records displaying in this page", retrievedResultCount);
        jSONObject.put("batchList", list);

        return new ResponseEntity<>(jSONObject, HttpStatus.OK);
    }

    @RequestMapping(value = {"/assignBatchIdToStudent"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> assignBatchIdToStudent(@RequestBody final BatchIdToStudents batchIdToStudents) {
        JSONObject json = new JSONObject();
        String batchId = batchIdToStudents.getBatchId();
        List<String> listStudentId = batchIdToStudents.getStudentIdList();
        try {
            if (listStudentId.size() > 0) {
                int c = 0;
                for (String mulSubID : listStudentId) {
                    c += service.update(String.format("%s", "UPDATE StudentSubscription s SET s.batchId = '" + batchId + "' WHERE s.mulSubscribeID = '" + mulSubID + "'"));
                }
                if (c > 0) {
                    json.put("msg", "Batch has been assigned to " + c + " students.");
                }
            } else {
                json.put("msg", "Please assign atleast one student to this batch.");
            }

        } catch (Exception e) {
            json.put("msg", "Something went wrong.");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/removeBatchFromStudents"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> removeBatchFromStudents(@RequestBody final BatchIdToStudents batchIdToStudents) {
        JSONObject json = new JSONObject();
        List<String> listStudentId = batchIdToStudents.getStudentIdList();
        try {
            if (listStudentId.size() > 0) {
                int c = 0;
                for (String studentId : listStudentId) {
                    c += service.update(String.format("%s", "UPDATE StudentSubscription s SET s.batchId = NULL WHERE s.mulSubscribeID = '" + studentId + "'"));
                }
                if (c > 0) {
                    json.put("msg", c + " students have been removed from this batch.");
                }
            } else {
                json.put("msg", "Please select atleast one student to remove from this batch.");
            }
        } catch (Exception e) {
            json.put("msg", "Something went wrong.");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getallschedularForGuest")
    public @ResponseBody
    JSONObject getallschedularForGuest(@RequestBody final FilterBean filter) {
        JSONObject j = new JSONObject();

        ArrayList<Object> recordedClassList = new ArrayList<>();
        ArrayList<Object> upcomingClassList = new ArrayList<>();
        ArrayList<Object> liveClassList = new ArrayList<>();

        String accessTo = filter.getAccessTo();
        String gradeName = filter.getGradeName();

        List<String> syllabusFilters = filter.getSyllabusFilter();
        List<String> subjectFilters = filter.getSubjectFilter();
        List<String> chapterFilters = filter.getChapterFilter();

        int pageNo = Integer.parseInt(filter.getPageNo());
        int maxResult = 10;
        if (filter.getMaxResult() != null) {
            maxResult = Integer.parseInt(filter.getMaxResult());
        }

        int countRecorded = 0;

        // TimeZone tz = TimeZone.getTimeZone("IST");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
        //  df.setTimeZone(tz);
        Date current = MyDateFormate.stringToDate(String.format("%s", df.format(new Date())));
        String currentDate = df.format(new Date());
        try {
            Map<String, String> mapFilterSQL = new FilterUtility().getallschedularForGuestFilter(gradeName, accessTo, syllabusFilters, subjectFilters, chapterFilters, currentDate);

            countRecorded = (int) service.countObject(mapFilterSQL.get("SQLcountRecorded"));
            List<Object> listRecordedObj = service.loadByLimit(mapFilterSQL.get("SQLRecorded"), (pageNo * maxResult), maxResult);
            List<Object> listUpcomingObj = service.getObject(mapFilterSQL.get("SQLlistUpcoming"));
            List<Object> listLiveObj = service.getObject(mapFilterSQL.get("SQLlistLive"));

            ClassScheduler classScheduler;

            /*list of recorded classes*/
            if (listRecordedObj.size() > 0) {
                for (Object object : listRecordedObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);

                    int countRating = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Rating r WHERE r.sesionId = '" + classScheduler.getSessionId() + "'"));
                    if (countRating > 0) {
                        json.put("totalNumOfStudentForRating", countRating);
                    } else {
                        json.put("totalNumOfStudentForRating", 0);
                    }

                    recordedClassList.add(json);
                }
            }

            /*list of upcoming classes*/
            if (listUpcomingObj.size() > 0) {
                for (Object object : listUpcomingObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);
                    upcomingClassList.add(json);
                }
            }

            /*list of live classes*/
            if (listLiveObj.size() > 0) {
                for (Object object : listLiveObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);
                    liveClassList.add(json);
                }
            }

        } catch (Exception e) {
        }
        j.put("countRecordedClass", countRecorded);
        j.put("recordedClassList", recordedClassList);
        j.put("upcomingClassList", upcomingClassList);
        j.put("liveClassList", liveClassList);

        return j;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getallschedularByGradeNameForCalender")
    public @ResponseBody
    JSONObject getallschedularByGradeNameForCalender(@RequestBody final Map<String, String> mapBean) {
        JSONObject j = new JSONObject();

        ArrayList<JSONObject> list = new ArrayList<>();

        String gradeName = mapBean.get("gradeName");

        int count = 0;

        try {
            String SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.gradeName='" + gradeName + "'";
            count = (int) service.countObject(SQLcountRecorded);

            String SQLRecorded = "FROM ClassScheduler c WHERE c.gradeName='" + gradeName + "' and YEAR(c.scheduledDate) = " + mapBean.get("year") + " AND MONTH(c.scheduledDate) = " + mapBean.get("month") + " ORDER BY c.scheduledDate DESC";
            List<Object> listRecordedObj = service.getObject(SQLRecorded);

            ClassScheduler classScheduler;

            if (listRecordedObj.size() > 0) {
                for (Object object : listRecordedObj) {
                    classScheduler = (ClassScheduler) object;
                    JSONObject json = getClassSchedulerJson(classScheduler);

                    int countRating = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM Rating r WHERE r.sesionId = '" + classScheduler.getSessionId() + "'"));
                    if (countRating > 0) {
                        json.put("totalNumOfStudentForRating", countRating);
                    } else {
                        json.put("totalNumOfStudentForRating", 0);
                    }

                    list.add(json);
                }
            }

        } catch (Exception e) {
        }
        j.put("count", count);
        j.put("listOfClassSchedular", list);

        return j;
    }

    @RequestMapping(value = {"/createDemoMetadata"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> createDemoMetadata(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();

        // TimeZone tz = TimeZone.getTimeZone("IST");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
        // df.setTimeZone(tz);
        Date dateOfCreation = MyDateFormate.stringToDate(String.format("%s", df.format(new Date())));

        String accountId = mapBean.get("accountId");
        String sessionId = mapBean.get("sessionId");
        try {
            Demo demo = new Demo();
            String demoId = (String) pKGenerator.generate(Demo.class, "DEMO");
            demo.setDemoId(demoId);
            demo.setAccountId(accountId);
            demo.setSessionId(sessionId);
            demo.setDateOfCreation(dateOfCreation);
            if (service.save(demo) > 0) {
                int noOfSeats = (int) service.getObject(String.format("%s", "SELECT c.noOfSeats FROM ClassScheduler c WHERE c.sessionId = '" + sessionId + "'")).get(0);
                if (noOfSeats > 0) {
                    int updatedSeats = (noOfSeats - 1);
                    service.update(String.format("%s", "UPDATE ClassScheduler c SET c.noOfSeats = '" + updatedSeats + "' WHERE c.sessionId = '" + sessionId + "'"));
                    json.put("msg", "Demo metadata captured!");
                    json.put("demoId", demo.getDemoId());
                    json.put("accountId", demo.getAccountId());
                    json.put("sessionId", demo.getSessionId());
                    json.put("noOfSeats", updatedSeats);
                } else {
                    json.put("msg", "All seats have been filled up!");
                }
            }

        } catch (Exception e) {
            json.put("msg", "Something went wrong. Try Again!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getDemoMetadataByAccountId"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getDemoMetadataByAccountId(@RequestBody final Map<String, String> mapBean) {
        List<JSONObject> list = new ArrayList<>();

        String guestAccountId = mapBean.get("accountId");
        try {

            List<Object> demoList = service.getObject(String.format("%s", "FROM Demo d WHERE d.accountId = '" + guestAccountId + "' ORDER BY d.dateOfCreation DESC"));
            if (demoList.size() > 0) {
                for (Object object : demoList) {
                    Demo demo = (Demo) object;
                    JSONObject json = new JSONObject();
                    json.put("demoId", demo.getDemoId());
                    json.put("accountId", demo.getAccountId());
                    json.put("sessionId", demo.getSessionId());
                    json.put("date", demo.getDateOfCreation());
                    List<Object> demoListbysessionId = service.getObject(String.format("%s", "SELECT d.subjectId FROM ClassScheduler d WHERE d.sessionId = '" + demo.getSessionId() + "'"));
                    for (Object object1 : demoListbysessionId) {

                        json.put("subjectId", object1);
                        list.add(json);
                    }

                }
            }
        } catch (Exception e) {
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = {"/createZoomMeeting"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> createZoomMeeting(@RequestBody ZoomRequest zoomMeetingRequest) {

        ResponseEntity<JSONObject> responseEntity = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        headers.setContentType(MediaType.APPLICATION_JSON);
        //  headers.set("Authorization", zoomToken);
        headers.set("Authorization", zoomToken);
        String URL = creatingMeetingURL;

        log.info("Schedule date time {}", zoomMeetingRequest.getStart_time());

        ZoomRequest zoomRequest = new ZoomRequest();
        zoomRequest.setAgenda(zoomMeetingRequest.getAgenda());
        zoomRequest.setAudio(zoomMeetingRequest.getAudio());
        zoomRequest.setDefault_password(zoomMeetingRequest.isDefault_password());
        zoomRequest.setDuration(zoomMeetingRequest.getDuration());
        zoomRequest.setPre_schedule(zoomMeetingRequest.isPre_schedule());
        zoomRequest.setSettings(zoomMeetingRequest.getSettings());
        zoomRequest.setStart_time(zoomMeetingRequest.getStart_time());
        zoomRequest.setTopic(zoomMeetingRequest.getTopic());
        zoomRequest.setJoin_before_host(zoomMeetingRequest.isJoin_before_host());
        zoomRequest.setAuto_recording(zoomMeetingRequest.getAuto_recording());
        zoomRequest.setEmail_notification(true);
        zoomRequest.setIn_meeting(false);
        zoomRequest.setJoin_before_host(false);


        HttpEntity<ZoomRequest> request = new HttpEntity<>(zoomRequest, headers);
        log.info("Calling Zoom API to create Meeting with data {}", zoomRequest);
        try {
            responseEntity = restTemplate.postForEntity(URL, request, JSONObject.class);
        } catch (HttpClientErrorException e) {
            log.error("Zoom Token Expired!");
            return new ResponseEntity<>("Zoom Token Expired", HttpStatus.UNAUTHORIZED);
        }
        log.info("Getting Response from Zoom meeting ", responseEntity);
        return responseEntity;


    }

    @GetMapping("/isValidToken")
    public boolean  getTokenValid(){

        boolean flag = false;
        ResponseEntity<JSONObject> responseEntity = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        headers.setContentType(MediaType.APPLICATION_JSON);
        //  headers.set("Authorization", zoomToken);
        headers.set("Authorization", zoomToken);
        String URL = creatingMeetingURL;
        ZoomRequest zoomRequest = new ZoomRequest();
        zoomRequest.setTopic("Testing Token");
        HttpEntity<ZoomRequest> request = new HttpEntity<>(zoomRequest, headers);

        try {
            responseEntity = restTemplate.postForEntity(URL, request, JSONObject.class);
            flag = true;
        } catch (HttpClientErrorException e) {
            log.error("Zoom Token Expired!");
            flag =false;
        }

        return flag;
    }

    @PostMapping("/generateToken")
    public @ResponseBody
    ResponseEntity<?> generateZoomRequest(@RequestBody TokenRequest tokenRequest) {

        ResponseEntity<JSONObject> responseEntity = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TokenRequest> request = new HttpEntity<>(tokenRequest, headers);
        try {
            responseEntity = restTemplate.postForEntity("http://localhost:3000/userinfo", request, JSONObject.class);
        }catch (HttpClientErrorException e) {
                log.error("Zoom Token Expired!");
                return new ResponseEntity<>("Zoom Token Expired", HttpStatus.UNAUTHORIZED);
            }
            log.info("Getting From Zoom", responseEntity);
            return responseEntity;

    }

}
