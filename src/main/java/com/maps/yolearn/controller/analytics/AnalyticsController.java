package com.maps.yolearn.controller.analytics;

import com.maps.yolearn.bean.analytics.RatingBean;
import com.maps.yolearn.bean.user.UsersMetaData;
import com.maps.yolearn.model.analytics.Rating;
import com.maps.yolearn.model.analytics.RecordedVideoAnalytics;
import com.maps.yolearn.model.liveclass.ClassScheduler;
import com.maps.yolearn.model.user.UserHistory;
import com.maps.yolearn.service.EntityService;
import com.maps.yolearn.util.comparator.UserHistoryComparator;
import com.maps.yolearn.util.primarykey.CustomPKGenerator;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author PREMNATH
 * @author KOTARAJA
 */
@RestController
@RequestMapping(value = {"/analytics"})
@CrossOrigin(origins = "*", maxAge = 3600)
public class AnalyticsController {

    @Autowired
    private EntityService service;

    @Autowired
    private CustomPKGenerator pKGenerator;

    @RequestMapping(value = {"/addRecordedVideoAnalytics"}, method = RequestMethod.POST)
    public ResponseEntity<?> addRecordedVideoAnalytics(@RequestBody Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        Timestamp date = new Timestamp(System.currentTimeMillis());
        String userRole = mapBean.get("userRole");
        String sessionId = mapBean.get("sessionId");
        String accountId = mapBean.get("accountId");
        String reasonDescription = mapBean.get("reasonDescription");

        try {
            if (!userRole.equals("Admin")) {
                RecordedVideoAnalytics videoAnalytics = new RecordedVideoAnalytics();
                String analyticsId = (String) pKGenerator.generate(RecordedVideoAnalytics.class, "ANLTCS");
                videoAnalytics.setAnalyticsId(analyticsId);
                videoAnalytics.setSessionId(sessionId);
                videoAnalytics.setAccountId(accountId);
                videoAnalytics.setReasonDescription(reasonDescription);
                videoAnalytics.setAccessDate(date);

                service.save(videoAnalytics);
                json.put("msg", "Analytics added successfully!");
            }
        } catch (Exception e) {
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getRecVidAnalyticsCountByDesc"}, method = RequestMethod.POST)
    public ResponseEntity<?> getRecVidAnalyticsCountByDesc(@RequestBody Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        String sessionId = mapBean.get("sessionId");
        String desc1 = "Better Understanding";
        String desc2 = "Revision";
        String desc3 = "Missed Classes";

        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", sessionId);

        try {
            List<Object> listRecordedVideoAnalyticsObj = service.getObject(RecordedVideoAnalytics.class, map);
            if (listRecordedVideoAnalyticsObj.size() > 0) {

                int desc1Count = 0;
                int desc2Count = 0;
                int desc3Count = 0;

                for (Object object : listRecordedVideoAnalyticsObj) {
                    RecordedVideoAnalytics videoAnalytics = (RecordedVideoAnalytics) object;
                    String reason = videoAnalytics.getReasonDescription();
                    if (reason.equals(desc1)) {
                        desc1Count += 1;
                    }
                    if (reason.equals(desc2)) {
                        desc2Count += 1;
                    }
                    if (reason.equals(desc3)) {
                        desc3Count += 1;
                    }
                }

                json.put("Better Understanding", desc1Count);
                json.put("Revision", desc2Count);
                json.put("Missed Classes", desc3Count);

            } else {
                json.put("msg", "No Record Found!");
            }

        } catch (Exception e) {
            json.put("msg", "Something went wrong!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/userAttendClassReport")
    public @ResponseBody
    JSONObject studentAttendClassReport(@RequestBody final UsersMetaData bean) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        JSONObject json = new JSONObject();

        UserHistory userHistory = new UserHistory();

        Map<String, Object> m = new HashMap<>();
        m.put("sessionId", bean.getSessionId());
        m.put("accountId", bean.getsAccountId());

        List<Object> l = service.getObject(UserHistory.class, m);
        if (l.size() > 0) {
            json.put("msg", "sessionId already exist");
        } else {
            String id = (String) pKGenerator.generate(UserHistory.class, "CLASS_HYST");
            userHistory.setClassHistory(id);
            userHistory.setSessionId(bean.getSessionId());
            userHistory.setAccountId(bean.getsAccountId());
            userHistory.setClassName(bean.getTitle());
            userHistory.setDateOfCreation(date);
            try {
                String str = service.saveOrUpdate(userHistory);
                if (!str.equals("")) {
                    json.put("msg", "saved user history");
                    json.put("sessionId", bean.getSessionId());
                } else {
                    json.put("msg", "Something went wrong. Please check your inputs or try again.");
                }
            } catch (Exception e) {
                json.put("msg", e.getMessage());
            }
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listOfsessionIdBystudentId")
    public @ResponseBody
    ResponseEntity<?> listOfsessionIdBystudentId(@RequestBody final Map<String, String> mapBean) {
        List<Object> list1 = new ArrayList<>();
        Map<String, Object> map2 = new HashMap<>();
        map2.put("accountId", mapBean.get("sAccountId"));

        List<Object> list = service.getObject(UserHistory.class, map2);
        if (list.size() > 0) {
            List<UserHistory> listUserHistory = new ArrayList<>();
            for (Object object : list) {
                UserHistory uh = (UserHistory) object;
                listUserHistory.add(uh);
            }

            Collections.sort(listUserHistory, new UserHistoryComparator());

            for (UserHistory history : listUserHistory) {
                JSONObject json = new JSONObject();
                json.put("sessionId", history.getSessionId());
                json.put("title", history.getClassName());
                list1.add(json);
            }
        }
        return new ResponseEntity<>(list1, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getRatingbyStudentIdAndSessionId")
    public @ResponseBody
    List<Object> getRating(@RequestBody RatingBean bean) {
        JSONObject json;
        Map<String, Object> m = new HashMap<>();
        m.put("sesionId", bean.getSesionId());

        List<Object> l = new ArrayList<>();

        List<Object> list = service.getObject(Rating.class, m);
        if (list.size() > 0) {
            for (Object object : list) {
                json = new JSONObject();
                Rating r = (Rating) object;
                json.put("comment", r.getComment());
                json.put("sesionId", r.getSesionId());
                json.put("studentAccountId", r.getStudentId());
                json.put("ratingId", r.getId());
                json.put("feedBack", r.getFeedBack());
                json.put("totalRating", r.getTotalRating());
                l.add(json);
            }
        } else {
            json = new JSONObject();
            json.put("msg", "Empty list");
            l.add(json);
        }
        return l;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/ratingInLiveClass")
    public @ResponseBody
    JSONObject ratingInLive(@RequestBody RatingBean bean) {
        JSONObject json = new JSONObject();
        Map<String, Object> m = new HashMap<>();
        m.put("sesionId", bean.getSesionId());
        m.put("studentId", bean.getStudentId());
        List<Object> list = service.getObject(Rating.class, m);
        if (list.size() > 0) {
            Rating rating = (Rating) list.get(0);
            rating.setTotalRating(bean.getTotalRating());
            rating.setFeedBack(bean.getFeedBack());
            rating.setComment(bean.getComment());
            rating.setStudentId(bean.getStudentId());
            rating.setSesionId(bean.getSesionId());
            int x = service.update(rating);
            if (x > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("sesionId", bean.getSesionId());
                List<Object> liRatingObj = service.getObject(Rating.class, map);
                List<Integer> liRating = new ArrayList<>();
                for (Object object : liRatingObj) {
                    Rating rat = (Rating) object;
                    liRating.add(rat.getTotalRating());
                }

                /*adding the ratings*/
                int sum = liRating.parallelStream().reduce(0, Integer::sum);

                /*calculating the avg rating*/
                float avgRating = (float) sum / liRating.size();

                /*update live class avgRating for that session*/
                Map<String, Object> map1 = new HashMap<>();
                map1.put("sessionId", bean.getSesionId());
                List<Object> liLiveClassObj = service.getObject(ClassScheduler.class, map1);
                ClassScheduler cs = (ClassScheduler) liLiveClassObj.get(0);
                cs.setAvgRating(avgRating);
                service.update(cs);

                json.put("msg", "Thanks For Giving Rating");
            } else {
                json.put("msg", "not updated");
            }
        } else {
            String rateId = (String) pKGenerator.generate(Rating.class, "RATING_");
            Rating r = new Rating();
            r.setId(rateId);
            r.setTotalRating(bean.getTotalRating());
            r.setFeedBack(bean.getFeedBack());
            r.setComment(bean.getComment());
            r.setStudentId(bean.getStudentId());
            r.setSesionId(bean.getSesionId());
            try {
                String str = service.saveOrUpdate(r);
                if (!str.equals("")) {

                    /*adding the ratings*/
                    int sum = bean.getTotalRating();

                    /*calculating the avg rating*/
                    float avgRating = (float) sum;

                    /*update live class avgRating for that session*/
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("sessionId", bean.getSesionId());
                    List<Object> liLiveClassObj = service.getObject(ClassScheduler.class, map1);
                    ClassScheduler cs = (ClassScheduler) liLiveClassObj.get(0);
                    cs.setAvgRating(avgRating);
                    service.update(cs);

                    json.put("msg", "Thanks For Giving Rating");
                } else {
                    json.put("msg", "Not saved or try again.");
                }
            } catch (Exception e) {
                json.put("msg", e.getMessage());
            }
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/studentClassesAnalytics")
    public @ResponseBody
    ResponseEntity<?> studentClassesAnalytics(@RequestBody final Map<String, String> mapBean) {
        String sAccountId = mapBean.get("sAccountId");
        JSONObject jSONObject = new JSONObject();

        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
            df.setTimeZone(tz);
            String current = df.format(new Date());

            String SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.scheduledDate < '" + current + "' "
                    + "AND  c.batchId  IN (SELECT s.batchId FROM "
                    + "StudentSubscription s where s.studentAccountId='" + sAccountId + "') AND c.accessTo IN ('Members', 'Both')";

            int countRecorded = (int) service.countObject(SQLcountRecorded);

            String completedClassesCount = "select count(*) "
                    + "from CLASS_SCHEDULER c where c.SESSION_ID in "
                    + "(SELECT p.SESSION_ID FROM PARTICIPANTS p where p.PARTIC_UNIQUENAME='" + sAccountId + "' and p.VIEW_PERCETAGE>90\n"
                    + "UNION\n"
                    + "SELECT rs.SESSION_ID FROM RECORDED_STATS rs where rs.UNIQUENAME='" + sAccountId + "' and rs.VIEW_PERCETAGE>90)";

            BigInteger Completedcount = (BigInteger) service.getObjectsByNativeSqlQuery(completedClassesCount).get(0);
            Integer cmplCount = ((Integer) Completedcount.intValue());

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
                    + "on c.SESSION_ID=res.SESSION_ID GROUP BY res.SESSION_ID";

            List<Object> objectsByNativeSqlQuery = service.getObjectsByNativeSqlQuery(inCompleteNativeSqlCOunt);
            int count2 = objectsByNativeSqlQuery.size();
            int attendedClassesCount = cmplCount + count2;
            jSONObject.put("recordedClassCount", countRecorded);
            jSONObject.put("attendedClassesCount", attendedClassesCount);

        } catch (Exception exception) {
            jSONObject.put("msg", "Something went wrong");
            exception.printStackTrace();
        }
        return new ResponseEntity<>(jSONObject, HttpStatus.OK);

    }

}
