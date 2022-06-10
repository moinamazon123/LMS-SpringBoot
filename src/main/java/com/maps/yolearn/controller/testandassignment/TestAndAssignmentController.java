package com.maps.yolearn.controller.testandassignment;

import com.maps.yolearn.bean.analytics.TestBean;
import com.maps.yolearn.constants.Constants;
import com.maps.yolearn.model.analytics.TestReports;
import com.maps.yolearn.model.grade.Grade;
import com.maps.yolearn.model.testandassignment.TestAndAssignments;
import com.maps.yolearn.model.user.StudentAccount;
import com.maps.yolearn.service.EntityService;
import com.maps.yolearn.util.date.MyDateFormate;
import com.maps.yolearn.util.ftp.FTPFileUtility;
import com.maps.yolearn.util.primarykey.CustomPKGenerator;
import com.maps.yolearn.util.xml.XmlValidationForTest;
import org.apache.commons.net.ftp.FTPClient;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author KOTARAJA
 * @author PREMNATH
 */
@RestController
@RequestMapping(value = {"/testandassignment"})
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestAndAssignmentController {

    //    static String ftpAddr = Constants.FTP_ADDRESS;
//    static String ftpUserName = Constants.FTP_USERNAME;
//    static String password = Constants.FTP_PASSWORD;
//    static int portNumber = Constants.FTP_PORT;
    static String rootDir = "/";
    static String folder1 = "Temp";
    static String folder2 = "Test";
    static String folder3 = "Assignments";

    @Autowired
    private EntityService service;

    @Autowired
    private CustomPKGenerator pKGenerator;

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
        String durationinMinutes = mapBean.get("durationinMinutes");
        String scheduledDate = mapBean.get("scheduledDate");

        TestAndAssignments testAndAssignments = new TestAndAssignments();
        String fileId = (String) pKGenerator.generate(TestAndAssignments.class, "FILE");

        testAndAssignments.setFileId(fileId);
        testAndAssignments.setTitle(title);

        if (testFile.equals("")) {

        } else {
            testAndAssignments.setTestFile(testFile);
        }
        if (assignmentFile.equals("")) {

        } else {
            testAndAssignments.setAssignmentFile(assignmentFile);
        }

        testAndAssignments.setGradeId(gradeId);
        testAndAssignments.setSubjectId(subjectId);
        testAndAssignments.setSyllabusId(syllabusId);
        testAndAssignments.setAccessTo(accessTo);
        testAndAssignments.setDateOfCreation(date);
        if (durationinMinutes.equals("")) {
            testAndAssignments.setDurationinMinutes(0);
        } else {
            int i = Integer.parseInt(durationinMinutes);
            testAndAssignments.setDurationinMinutes(Integer.parseInt(durationinMinutes));
        }
        testAndAssignments.setScheduledDate(MyDateFormate.stringToDate(scheduledDate));

        int x = service.save(testAndAssignments);
        JSONObject json = new JSONObject();
        if (x > 0) {
            json.put("msg", "File Saved Successfully!");
        } else {
            json.put("msg", "Oops! File Not Saved!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getAllAssignMetadata"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getAllAssignMetadata(@RequestBody final Map<String, String> mapBean) {
        int pageNo = Integer.parseInt(mapBean.get("pageNo"));

        int maxResult = 10;
        if (mapBean.get("maxResult") != null) {
            maxResult = Integer.parseInt(mapBean.get("maxResult"));
        }

        ArrayList<JSONObject> list = new ArrayList<>();
        JSONObject json;

        /*getting count of TestAndAssignments objects*/
        int count = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM TestAndAssignments t WHERE t.assignmentFile IS NOT NULL"));

        /*getting object from lattest date to old-date order*/
        List<Object> listTestAndAssignObj = service.loadByLimit(String.format("%s", "FROM TestAndAssignments t WHERE t.assignmentFile IS NOT NULL ORDER BY t.dateOfCreation DESC"), (pageNo * maxResult), maxResult);
        for (Object object : listTestAndAssignObj) {
            TestAndAssignments testAndAssignments = (TestAndAssignments) object;

            /*grade name*/
            String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + testAndAssignments.getGradeId() + "'")).get(0);

            /*subject name*/
            String subjectName = (String) service.getObject(String.format("%s", "SELECT s.subjectName FROM Subject s WHERE s.subjectId = '" + testAndAssignments.getSubjectId() + "'")).get(0);

            /*syllabus name*/
            String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + testAndAssignments.getSyllabusId() + "'")).get(0);

            json = new JSONObject();
            json.put("fileId", testAndAssignments.getFileId());
            json.put("title", testAndAssignments.getTitle());
            json.put("assignmentFile", testAndAssignments.getAssignmentFile());
            json.put("testFile", testAndAssignments.getTestFile());
            json.put("gradeId", testAndAssignments.getGradeId());
            json.put("gradeName", gradeName);
            json.put("subjectId", testAndAssignments.getSubjectId());
            json.put("subjectName", subjectName);
            json.put("syllabusId", testAndAssignments.getSyllabusId());
            json.put("syllabusName", syllabusName);
            json.put("accessTo", testAndAssignments.getAccessTo());
            json.put("dateOfCreation", MyDateFormate.dateToString(testAndAssignments.getDateOfCreation()));

            Date sd = testAndAssignments.getScheduledDate();
            json.put("scheduledDate", sd);

            int dm = testAndAssignments.getDurationinMinutes();
            json.put("durationinMinutes", dm);
            if (dm > 0) {
                long enddateLong = sd.getTime() + (dm * 60000);
                Date endDate = new Date(enddateLong);
                json.put("endDate", endDate);
            } else {
                json.put("endDate", 0);
            }

            list.add(json);
        }
        json = new JSONObject();
        json.put("count", count);
        json.put("listOfAssignments", list);

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/getAllTestMetadata"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getAllTestMetadata(@RequestBody final Map<String, String> mapBean) {
        int pageNo = Integer.parseInt(mapBean.get("pageNo"));

        int maxResult = 10;
        if (mapBean.get("maxResult") != null) {
            maxResult = Integer.parseInt(mapBean.get("maxResult"));
        }

        ArrayList<JSONObject> list = new ArrayList<>();
        JSONObject json;

        /*getting count of TestAndAssignments objects*/
        int count = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM TestAndAssignments t WHERE t.testFile IS NOT NULL"));

        /*getting object from lattest date to old-date order*/
        List<Object> listTestAndAssignObj = service.loadByLimit(String.format("%s", "FROM TestAndAssignments t WHERE t.testFile IS NOT NULL ORDER BY t.dateOfCreation DESC"), (pageNo * maxResult), maxResult);
        for (Object object : listTestAndAssignObj) {
            TestAndAssignments testAndAssignments = (TestAndAssignments) object;

            /*grade name*/
            String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + testAndAssignments.getGradeId() + "'")).get(0);

            /*subject name*/
            String subjectName = (String) service.getObject(String.format("%s", "SELECT s.subjectName FROM Subject s WHERE s.subjectId = '" + testAndAssignments.getSubjectId() + "'")).get(0);

            /*syllabus name*/
            String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + testAndAssignments.getSyllabusId() + "'")).get(0);

            json = new JSONObject();
            json.put("fileId", testAndAssignments.getFileId());
            json.put("title", testAndAssignments.getTitle());
            json.put("assignmentFile", testAndAssignments.getAssignmentFile());
            json.put("testFile", testAndAssignments.getTestFile());
            json.put("gradeId", testAndAssignments.getGradeId());
            json.put("gradeName", gradeName);
            json.put("subjectId", testAndAssignments.getSubjectId());
            json.put("subjectName", subjectName);
            json.put("syllabusId", testAndAssignments.getSyllabusId());
            json.put("syllabusName", syllabusName);
            json.put("accessTo", testAndAssignments.getAccessTo());
            json.put("dateOfCreation", MyDateFormate.dateToString(testAndAssignments.getDateOfCreation()));

            Date sd = testAndAssignments.getScheduledDate();
            json.put("scheduledDate", sd);

            int dm = testAndAssignments.getDurationinMinutes();
            json.put("durationinMinutes", dm);
            if (dm > 0) {
                long enddateLong = sd.getTime() + (dm * 60000);
                Date endDate = new Date(enddateLong);
                json.put("endDate", endDate);
            } else {
                json.put("endDate", 0);
            }

            list.add(json);
        }
        json = new JSONObject();
        json.put("count", count);
        json.put("listOfTests", list);

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/uploadTest", method = RequestMethod.POST)
    public JSONObject uploadTest(@RequestParam("file") MultipartFile file) throws IOException, Exception {
        JSONObject json = new JSONObject();
        try {
            InputStream inputStream = file.getInputStream();
            InputStream inputStream1 = file.getInputStream();
            String fname = file.getOriginalFilename();
            String destPath = "/Temp/";

            XmlValidationForTest test = new XmlValidationForTest();
            String resp = test.validaXML(inputStream);

            if (resp.equals("")) {
                json.put("msg", "ready to upload");
                boolean b222 = new FTPFileUtility().isFTPFileAvailable(Constants.FTP_ADDRESS, Constants.FTP_PORT, Constants.FTP_USERNAME, Constants.FTP_PASSWORD, "/Test/", fname);
                if (b222) {
                    json.put("msg", "File already exists.");
                } else {
                    boolean b = new FTPFileUtility().uploadFile(Constants.FTP_ADDRESS, Constants.FTP_PORT, Constants.FTP_USERNAME, Constants.FTP_PASSWORD, inputStream1, destPath, fname);
                    if (b) {
                        String destinationPath = destPath + fname;

                        Long sizeIs = new FTPFileUtility().getfilesize(Constants.FTP_ADDRESS, Constants.FTP_USERNAME, Constants.FTP_PASSWORD, destinationPath);
                        if (sizeIs.intValue() > 0) {
                            String fname1 = fname;
                            String msg;
                            msg = new FTPFileUtility().moveFTP_files(Constants.FTP_ADDRESS, Constants.FTP_PORT, Constants.FTP_USERNAME, Constants.FTP_PASSWORD, rootDir, folder1, folder2, fname1);
                            json.put("msg", "Test Xml has been " + msg);
                        } else {
                            json.put("msg", "file has been corrupted and  upload new file ");
                        }
                    } else {
                        json.put("msg", "Something went wrong. Try Again!");
                    }
                }
            } else {
                json.put("msg", resp);
            }
        } catch (Exception e) {
            json.put("msg", "Something went wrong. Try Again!" + e.getMessage());
        }
        return json;
    }

    @RequestMapping(value = "/uploadAssignments", method = RequestMethod.POST)
    public JSONObject uploadAssignments(@RequestParam("file") MultipartFile file) throws IOException, Exception {
        JSONObject json = new JSONObject();
        InputStream inputStream = file.getInputStream();
        InputStream inputStream1 = file.getInputStream();
        String fname = file.getOriginalFilename();
        String destPath = "/Temp/";

        XmlValidationForTest test = new XmlValidationForTest();
        String resp = test.validaXML(inputStream);

        if (resp.equals("")) {
            boolean b111 = new FTPFileUtility().isFTPFileAvailable(Constants.FTP_ADDRESS, Constants.FTP_PORT, Constants.FTP_USERNAME, Constants.FTP_PASSWORD, "/Assignments/", fname);
            if (b111) {
                json.put("msg", "File already exists.");
            } else {
                boolean b = new FTPFileUtility().uploadFile(Constants.FTP_ADDRESS, Constants.FTP_PORT, Constants.FTP_USERNAME, Constants.FTP_PASSWORD, inputStream1, destPath, fname);
                if (b) {
                    String destinationPath = destPath + fname;

                    Long sizeIs = new FTPFileUtility().getfilesize(Constants.FTP_ADDRESS, Constants.FTP_USERNAME, Constants.FTP_PASSWORD, destinationPath);
                    if (sizeIs.intValue() > 0) {
                        String msg;
                        try {
                            msg = new FTPFileUtility().moveFTP_files(Constants.FTP_ADDRESS, Constants.FTP_PORT, Constants.FTP_USERNAME, Constants.FTP_PASSWORD, rootDir, folder1, folder3, fname);
                            json.put("msg", "Assignment Xml has been " + msg);
                        } catch (Exception e) {
                            json.put("msg", "Something went wrong. Try Again!" + e.getMessage());
                        }
                    } else {
                        json.put("msg", "file has been corrupted and  upload new file ");
                    }
                } else {
                    json.put("msg", "Something went wrong. Try Again!");
                }
            }
        } else {
            json.put("msg", resp);
        }
        return json;
    }

    @GetMapping(value = {"/fetchxml_test"})
    public void fetchxml_test(@RequestParam Map<String, String> map, HttpServletResponse response, HttpServletRequest request) {

        FTPClient fTPClient = new FTPClient();
        try (OutputStream outputStream = response.getOutputStream()) {
            String fileName = map.get("fileName");
            String pdfFileName = String.format("%s", "/Test/" + fileName);
            fTPClient.connect(Constants.FTP_ADDRESS);
            fTPClient.login(Constants.FTP_USERNAME, Constants.FTP_PASSWORD);
            fTPClient.enterLocalPassiveMode();
            response.setContentType("application/xml");
            response.addHeader("Content-disposition", "attachment; filename=" + fileName);
            fTPClient.retrieveFile(pdfFileName, outputStream);
        } catch (Exception exception) {

        } finally {
            if (fTPClient.isConnected()) {
                try {

                    fTPClient.logout();
                    fTPClient.disconnect();
                } catch (IOException ioe) {
                }
            }
        }

    }

    @GetMapping(value = {"/fetchxml_assignment"})
    public void fetchxml_assignment(@RequestParam Map<String, String> map, HttpServletResponse response,
                                    HttpServletRequest request
    ) {

        FTPClient fTPClient = new FTPClient();
        try (OutputStream outputStream = response.getOutputStream()) {
            String fileName = map.get("fileName");
            String pdfFileName = String.format("%s", "/Assignments/" + fileName);
            fTPClient.connect(Constants.FTP_ADDRESS);
            fTPClient.login(Constants.FTP_USERNAME, Constants.FTP_PASSWORD);
            fTPClient.enterLocalPassiveMode();
            response.setContentType("application/xml");
            response.addHeader("Content-disposition", "attachment; filename=" + fileName);
            boolean b = fTPClient.retrieveFile(pdfFileName, outputStream);
        } catch (Exception exception) {
            exception.getMessage();
        } finally {
            if (fTPClient.isConnected()) {
                try {
                    fTPClient.logout();
                    fTPClient.disconnect();
                } catch (IOException ioe) {
                }
            }
        }

    }

    @RequestMapping(method = RequestMethod.POST, value = "/TestReport")
    public @ResponseBody
    JSONObject TestReport(@RequestBody TestBean testBean) {
        JSONObject json = new JSONObject();

        Map<String, Object> map = new HashMap<>();
        map.put("accountId", testBean.getAccountId());
        map.put("testId", testBean.getTestId());

        List<Object> l = service.getObject(TestReports.class, map);
        if (l.size() > 0) {

            TestReports reports = (TestReports) l.get(0);
            reports.setQn1(testBean.getQn1());
            reports.setQn2(testBean.getQn2());
            reports.setQn3(testBean.getQn3());
            reports.setQn4(testBean.getQn4());
            reports.setQn5(testBean.getQn5());
            reports.setQn6(testBean.getQn6());
            reports.setQn7(testBean.getQn7());
            reports.setQn8(testBean.getQn8());
            reports.setQn9(testBean.getQn9());
            reports.setQn10(testBean.getQn10());
            reports.setQn11(testBean.getQn11());
            reports.setQn12(testBean.getQn12());
            reports.setQn13(testBean.getQn13());
            reports.setQn14(testBean.getQn14());
            reports.setQn15(testBean.getQn15());
            reports.setQn16(testBean.getQn16());
            reports.setQn17(testBean.getQn17());
            reports.setQn18(testBean.getQn18());
            reports.setQn19(testBean.getQn19());
            reports.setQn20(testBean.getQn20());
            reports.setScore(testBean.getScore());
            reports.setWriteAnswers(testBean.getWriteAnswers());
            reports.setWrongAnswers(testBean.getWrongAnswers());

            int x = service.update(reports);

            if (x > 0) {

                Map<String, Object> map11 = new HashMap<>();
                map11.put("accountId", testBean.getAccountId());
                map11.put("testId", testBean.getTestId());
                List<Object> l1 = service.getObject(TestReports.class, map11);
                TestReports reports1 = (TestReports) l1.get(0);

                int c = reports1.getCount();
                c = c + 1;

                reports1.setCount(c);
                int x12 = service.update(reports1);
                if (x12 > 0) {
                    json.put("msg", "updated");
                } else {
                    json.put("msg", "not updated");
                }

            } else {
                json.put("msg", "not updated");

            }

        } else {
            String testid = (String) pKGenerator.generate(TestReports.class, "REPORT_");
            TestReports testReports = new TestReports();
            testReports.setReportId(testid);
            testReports.setTestId(testBean.getTestId());
            testReports.setAccountId(testBean.getAccountId());
            testReports.setQn1(testBean.getQn1());
            testReports.setQn2(testBean.getQn2());
            testReports.setQn3(testBean.getQn3());
            testReports.setQn4(testBean.getQn4());
            testReports.setQn5(testBean.getQn5());
            testReports.setQn6(testBean.getQn6());
            testReports.setQn7(testBean.getQn7());
            testReports.setQn8(testBean.getQn8());
            testReports.setQn9(testBean.getQn9());
            testReports.setQn10(testBean.getQn10());
            testReports.setQn11(testBean.getQn11());
            testReports.setQn12(testBean.getQn12());
            testReports.setQn13(testBean.getQn13());
            testReports.setQn14(testBean.getQn14());
            testReports.setQn15(testBean.getQn15());
            testReports.setQn16(testBean.getQn16());
            testReports.setQn17(testBean.getQn17());
            testReports.setQn18(testBean.getQn18());
            testReports.setQn19(testBean.getQn19());
            testReports.setQn20(testBean.getQn20());
            testReports.setScore(testBean.getScore());
            testReports.setWriteAnswers(testBean.getWriteAnswers());
            testReports.setWrongAnswers(testBean.getWrongAnswers());
            testReports.setCount(1);
            try {
                String str = service.saveOrUpdate(testReports);
                if (!str.equals("")) {
                    json.put("msg", "Saved Test Report");
                } else {
                    json.put("msg", "Not saved or try again.");
                }
            } catch (Exception e) {
                json.put("msg", e.getMessage());
            }
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getStudentsTestWithFileId")
    public @ResponseBody
    List<Object> getStudentsTestWithFileId(@RequestBody TestBean testBean) {
        List<Object> list = new ArrayList<>();
        JSONObject json;
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("testId", testBean.getTestId());

            List<Object> l = service.getObject(TestReports.class, m);
            if (l.size() > 0) {
                for (Object object : l) {
                    json = new JSONObject();
                    TestReports reports = (TestReports) object;

                    json.put("totalScore", reports.getScore());
                    json.put("writeAnswers", reports.getWriteAnswers());
                    json.put("count", reports.getCount());
                    Map<String, Object> m2 = new HashMap<>();
                    m2.put("studentAccountId", reports.getAccountId());
                    List<Object> l1 = service.getObject(StudentAccount.class, m2);

                    if (l1.size() > 0) {
                        StudentAccount account = (StudentAccount) l1.get(0);
                        json.put("firsName", account.getFirstName());
                        Map<String, Object> m1 = new HashMap<>();
                        m1.put("gradeId", account.getGradeId());
                        List<Object> l2 = service.getObject(Grade.class, m1);
                        if (l2.size() > 0) {

                            Grade g = (Grade) l2.get(0);
                            json.put("gradeName", g.getGradeName());
                            list.add(json);
                        } else {

                            json = new JSONObject();
                            json.put("msg", "miss match grade id in grades");
                        }
                    } else {
                        json = new JSONObject();
                        json.put("msg", "miss match Student id in Student");
                    }
                }
            } else {
                json = new JSONObject();
                json.put("msg", "empty list");
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", e.getMessage());
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getStudentsTest")
    public @ResponseBody
    List<Object> getStudentsTest(@RequestBody TestBean testBean) {
        JSONObject json;
        List<Object> list = new ArrayList<>();
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("accountId", testBean.getAccountId());

            List<Object> l = service.getObject(TestReports.class, m);
            if (l.size() > 0) {
                for (Object object : l) {
                    json = new JSONObject();
                    TestReports reports = (TestReports) object;

                    json.put("totalScore", reports.getScore());
                    json.put("writeAnswers", reports.getWriteAnswers());
                    json.put("count", reports.getCount());
                    json.put("fileId", reports.getTestId());

                    Map<String, Object> m5 = new HashMap<>();
                    m5.put("fileId", reports.getTestId());
                    List<Object> l12 = service.getObject(TestAndAssignments.class, m5);
                    TestAndAssignments testAndAssignments = (TestAndAssignments) l12.get(0);
                    json.put("testFileName", testAndAssignments.getTestFile());
                    json.put("title", testAndAssignments.getTitle());

                    Map<String, Object> m2 = new HashMap<>();
                    m2.put("studentAccountId", testBean.getAccountId());
                    List<Object> l1 = service.getObject(StudentAccount.class, m2);
                    if (l1.size() > 0) {

                        StudentAccount account = (StudentAccount) l1.get(0);
                        json.put("firsName", account.getFirstName());
                        Map<String, Object> m1 = new HashMap<>();
                        m1.put("gradeId", account.getGradeId());
                        List<Object> l2 = service.getObject(Grade.class, m1);
                        if (l2.size() > 0) {

                            Grade g = (Grade) l2.get(0);
                            json.put("gradeName", g.getGradeName());
                            list.add(json);
                        } else {
                            json = new JSONObject();
                            json.put("msg", "miss match Grade id in Grade");
                        }
                    } else {
                        json = new JSONObject();
                        json.put("msg", "miss match Student id in Student");
                    }
                }
            } else {
                json = new JSONObject();
                json.put("msg", "empty list");
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", e.getMessage());
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/retriveTestReport")
    public @ResponseBody
    JSONObject RetriveTestReport(@RequestBody TestBean testBean) {
        JSONObject json = new JSONObject();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("accountId", testBean.getAccountId());
            map.put("testId", testBean.getTestId());
            List<Object> l = service.getObject(TestReports.class, map);
            if (l.size() > 0) {
                for (Object object : l) {
                    TestReports reports = (TestReports) object;
                    json.put("accountId", reports.getAccountId());
                    json.put("testId", reports.getTestId());
                    json.put("qn1", reports.getQn1());
                    json.put("qn2", reports.getQn2());
                    json.put("qn3", reports.getQn3());
                    json.put("qn4", reports.getQn4());
                    json.put("qn5", reports.getQn5());
                    json.put("qn6", reports.getQn6());
                    json.put("qn7", reports.getQn7());
                    json.put("qn8", reports.getQn8());
                    json.put("qn9", reports.getQn9());
                    json.put("qn10", reports.getQn10());
                    json.put("qn11", reports.getQn11());
                    json.put("qn12", reports.getQn12());
                    json.put("qn13", reports.getQn13());
                    json.put("qn14", reports.getQn14());
                    json.put("qn15", reports.getQn15());
                    json.put("qn16", reports.getQn16());
                    json.put("qn17", reports.getQn17());
                    json.put("qn18", reports.getQn18());
                    json.put("qn19", reports.getQn19());
                    json.put("qn20", reports.getQn20());
                    json.put("count", reports.getCount());
                    json.put("score", reports.getScore());
                    json.put("writeAnswers", reports.getWriteAnswers());
                    json.put("wrongAnswers", reports.getWrongAnswers());
                }
            } else {
                json.put("msg", "Miss Match Id Or Empty List");
            }
        } catch (Exception e) {
            json.put("msg", e.getMessage());
        }
        return json;
    }

    @RequestMapping(value = {"/getAssignAndTestUsingFileId"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getAssignAndTestUsingFileId(@RequestBody final Map<String, String> mapBean) {

        String fileId = mapBean.get("fileId");
        JSONObject json = null;
        try {
            String sql = "FROM  TestAndAssignments t WHERE t.fileId='" + fileId + "'";
            List<Object> l = service.getObject(sql);

            for (Object object : l) {
                TestAndAssignments testAndAssignments = (TestAndAssignments) object;
                json = new JSONObject();
                json.put("fileId", testAndAssignments.getFileId());
                json.put("title", testAndAssignments.getTitle());
                json.put("assignmentFile", testAndAssignments.getAssignmentFile());
                json.put("testFile", testAndAssignments.getTestFile());
                json.put("gradeId", testAndAssignments.getGradeId());
                json.put("subjectId", testAndAssignments.getSubjectId());
                json.put("syllabusId", testAndAssignments.getSyllabusId());
                json.put("accessTo", testAndAssignments.getAccessTo());
                json.put("durationinMinutes", testAndAssignments.getDurationinMinutes());

                json.put("dateOfCreation", MyDateFormate.dateToString(testAndAssignments.getDateOfCreation()));
            }
        } catch (Exception e) {

        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }
}
