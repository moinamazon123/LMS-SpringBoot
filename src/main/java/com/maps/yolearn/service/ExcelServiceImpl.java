package com.maps.yolearn.service;

import com.maps.yolearn.bean.filter.FilterBean;
import com.maps.yolearn.bean.user.StudentExcel;
import com.maps.yolearn.dao.EntityDAO;
import com.maps.yolearn.model.payment.PaymentCheckout;
import com.maps.yolearn.model.user.AdminAccount;
import com.maps.yolearn.model.user.ParentAccount;
import com.maps.yolearn.model.user.StudentAccount;
import com.maps.yolearn.model.user.TeacherAccount;
import com.maps.yolearn.util.filter.FilterUtility;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author MAPS
 */
@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private EntityDAO dao;

    /**
     * @param remoteFilePath: example- "/Excel/Invoice/Invoice_backup.xls";
     * @param filter
     * @param response
     * @return String
     * @throws Exception
     */
    @Override
    public String create_INVOICE_Excel(String remoteFilePath, FilterBean filter, HttpServletResponse response) throws Exception {
        try {
            /*Getting paymentCheckout i.e. invoice details*/
            List<PaymentCheckout> listPaymentCheckout;

            String startDateStr = filter.getStartDate();
            String endDateStr = filter.getEndDate();
            String order_status = filter.getStatus();
            List<String> gradeFilter = filter.getGradeFilter();

            boolean noFilterApplied = ((startDateStr.equals("all")) && (endDateStr.equals("all"))) && (order_status.equals("all")) && (gradeFilter.get(0).equals("all"));

            /*filter applied*/
            if (!noFilterApplied) {

                String dateRegex = "[0-9]{4}[-]+[0-9]{2}[-]+[0-9]{2}";
                boolean dateBoolean = (startDateStr.matches(dateRegex) && endDateStr.matches(dateRegex));

                String statusArr[] = {"Success", "Aborted", "Failure", "Awaited", "Timeout"};
                List<String> statusList = Arrays.asList(statusArr);
                boolean order_statusBoolean = statusList.contains(order_status);

                boolean gradeBoolean = gradeFilter.get(0).contains("GRADE");

                if (dateBoolean && !order_statusBoolean && !gradeBoolean) {
                    /*only startDate-endDate filters is applied*/
                    Map<String, Object> map = new HashMap<>();//empty map
                    listPaymentCheckout = (List<PaymentCheckout>) dao.getObjectWithDate(PaymentCheckout.class, map, "dateOfCreation", startDateStr, endDateStr);
                } else if (!dateBoolean && order_statusBoolean && !gradeBoolean) {
                    /*only order_status filter is applied*/
                    listPaymentCheckout = (List<PaymentCheckout>) (Object) dao.getObject(String.format("%s", "FROM PaymentCheckout p WHERE p.order_status = '" + order_status + "'"));
                } else if (!dateBoolean && !order_statusBoolean && gradeBoolean) {
                    /*only grade filter is applied*/
                    List<String> listStudAccId = (List<String>) (Object) dao.getObject(String.format("%s", "SELECT s.studentAccountId FROM StudentAccount s WHERE s.gradeId = '" + gradeFilter.get(0) + "'"));

                    AtomicInteger counter = new AtomicInteger();
                    listStudAccId.forEach(id -> {
                        System.out.println(counter.getAndIncrement() + " " + id);
                    });

                    if (listStudAccId.size() > 0) {
                        listPaymentCheckout = (List<PaymentCheckout>) (Object) dao.getObject(String.format("%s", "FROM PaymentCheckout p WHERE p.allotedStudentAccountId IN (" + new FilterUtility().getIN_CLAUSE_VALUES(listStudAccId) + ")"));
                    } else {
                        listPaymentCheckout = new ArrayList();
                    }
                } else if (dateBoolean && order_statusBoolean && !gradeBoolean) {
                    /*only startDate-endDate and order_status filters are applied*/
                    Map<String, Object> map = new HashMap<>();
                    map.put("order_status", order_status);
                    listPaymentCheckout = (List<PaymentCheckout>) dao.getObjectWithDate(PaymentCheckout.class, map, "dateOfCreation", startDateStr, endDateStr);
                } else if (dateBoolean && !order_statusBoolean && gradeBoolean) {
                    /*only startDate-endDate and grade filters are applied*/
                    List<String> listStudAccId = (List<String>) (Object) dao.getObject(String.format("%s", "SELECT s.studentAccountId FROM StudentAccount s WHERE s.gradeId = '" + gradeFilter.get(0) + "'"));
                    if (listStudAccId.size() > 0) {
                        String SQL = "FROM PaymentCheckout p WHERE p.allotedStudentAccountId IN (" + new FilterUtility().getIN_CLAUSE_VALUES(listStudAccId) + ")";
                        String sqlDateRange;
                        if (startDateStr.equals(endDateStr)) {
                            sqlDateRange = String.format("%s", " AND DATE(p.dateOfCreation) = '" + startDateStr + "'");
                        } else {
                            sqlDateRange = String.format("%s", " AND DATE(p.dateOfCreation) >= '" + startDateStr + "' AND DATE(p.dateOfCreation) <= '" + endDateStr + "'");
                        }
                        SQL = String.format("%s", SQL + sqlDateRange);
                        listPaymentCheckout = (List<PaymentCheckout>) (Object) dao.getObject(String.format("%s", SQL));
                    } else {
                        listPaymentCheckout = new ArrayList();
                    }
                } else if (!dateBoolean && order_statusBoolean && gradeBoolean) {
                    /*only order_status and grade filters are applied*/
                    List<String> listStudAccId = (List<String>) (Object) dao.getObject(String.format("%s", "SELECT s.studentAccountId FROM StudentAccount s WHERE s.gradeId = '" + gradeFilter.get(0) + "'"));
                    if (listStudAccId.size() > 0) {
                        String SQL = "FROM PaymentCheckout p WHERE p.allotedStudentAccountId IN (" + new FilterUtility().getIN_CLAUSE_VALUES(listStudAccId) + ") AND p.order_status = '" + order_status + "'";
                        listPaymentCheckout = (List<PaymentCheckout>) (Object) dao.getObject(String.format("%s", SQL));
                    } else {
                        listPaymentCheckout = new ArrayList();
                    }
                } else if (dateBoolean && order_statusBoolean && gradeBoolean) {
                    /*all filters are applied*/
                    List<String> listStudAccId = (List<String>) (Object) dao.getObject(String.format("%s", "SELECT s.studentAccountId FROM StudentAccount s WHERE s.gradeId = '" + gradeFilter.get(0) + "'"));
                    if (listStudAccId.size() > 0) {
                        String SQL = "FROM PaymentCheckout p WHERE p.allotedStudentAccountId IN (" + new FilterUtility().getIN_CLAUSE_VALUES(listStudAccId) + ") AND p.order_status = '" + order_status + "'";
                        String sqlDateRange;
                        if (startDateStr.equals(endDateStr)) {
                            sqlDateRange = String.format("%s", " AND DATE(p.dateOfCreation) = '" + startDateStr + "'");
                        } else {
                            sqlDateRange = String.format("%s", " AND DATE(p.dateOfCreation) >= '" + startDateStr + "' AND DATE(p.dateOfCreation) <= '" + endDateStr + "'");
                        }
                        SQL = String.format("%s", SQL + sqlDateRange);
                        listPaymentCheckout = (List<PaymentCheckout>) (Object) dao.getObject(String.format("%s", SQL));
                    } else {
                        listPaymentCheckout = new ArrayList();
                    }
                } else {
                    listPaymentCheckout = new ArrayList();
                }
            } else {
                /*no filter is applied*/
                listPaymentCheckout = (List<PaymentCheckout>) (Object) dao.getObject(String.format("%s", "FROM PaymentCheckout p"));
            }

            if (listPaymentCheckout.size() > 0) {
                /*generate the excel*/
                invoiceExcel(remoteFilePath, listPaymentCheckout, response);
                return "302";//found
            } else {
                return "204";//no containts
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void invoiceExcel(String fileName, List<PaymentCheckout> listPaymentCheckout, HttpServletResponse response) throws MalformedURLException, IOException {

        try {

            try (HSSFWorkbook workbook = new HSSFWorkbook()) {
                HSSFSheet sheet = workbook.createSheet("INVOICE");

                /*create the header*/
                HSSFRow rowhead = sheet.createRow((short) 0);
                rowhead.createCell(0).setCellValue("SL. N.");
                rowhead.createCell(1).setCellValue("PAYMENT CHECKOUT ID");
                rowhead.createCell(2).setCellValue("INVOICE N.");
                rowhead.createCell(3).setCellValue("TRANSACTION N.");
                rowhead.createCell(4).setCellValue("BILLING NAME");
                rowhead.createCell(5).setCellValue("STUDENT NAME");
                rowhead.createCell(6).setCellValue("BILLING DATE");
                rowhead.createCell(7).setCellValue("PRICE");
                rowhead.createCell(8).setCellValue("GST");
                rowhead.createCell(9).setCellValue("NET AMOUNT");
                rowhead.createCell(10).setCellValue("STATUS");

                /*making the header cells bold*/
                CellStyle cellStyle = workbook.createCellStyle();
                HSSFFont rowHeadFont = workbook.createFont();
                rowHeadFont.setBold(true);
                cellStyle.setFont(rowHeadFont);

                /*setting the size of cells*/
                int n = sheet.getRow(0).getPhysicalNumberOfCells();
                for (int i = 0; i < n; i++) {
                    sheet.autoSizeColumn(i);
                }

                /*populating values*/
                int i = 1;
                int j = 1;
                for (PaymentCheckout paymentCheckout : listPaymentCheckout) {
                    HSSFRow row = sheet.createRow((short) (i++));
                    row.createCell(0).setCellValue(j++);
                    row.createCell(1).setCellValue(paymentCheckout.getPaymentCheckoutId());
                    row.createCell(2).setCellValue(paymentCheckout.getOrderId());
                    row.createCell(3).setCellValue(String.format("%s", paymentCheckout.getTid() + ""));
                    row.createCell(4).setCellValue(paymentCheckout.getBillingName());

                    String studentID = paymentCheckout.getAllotedStudentAccountId();

                    String studentFullName = "";
                    if (studentID.contains("STUDENT")) {
                        Object[] studentProperties = dao.loadProperties(String.format("%s", "SELECT s.firstName, s.lastName FROM StudentAccount s WHERE s.studentAccountId = '" + studentID + "'")).get(0);
                        studentFullName = String.format("%s", (String) studentProperties[0] + " " + (String) studentProperties[1]);
                    }

                    row.createCell(5).setCellValue(studentFullName);
                    row.createCell(6).setCellValue(paymentCheckout.getTrans_date());
                    row.createCell(7).setCellValue("INR 12711.00");//***
                    row.createCell(8).setCellValue("18%");//***
                    row.createCell(9).setCellValue("INR " + paymentCheckout.getMer_amount());
                    row.createCell(10).setCellValue(paymentCheckout.getOrder_status());
                }

                response.setContentType("application/vnd.ms-excel");
//                response.setHeader("Content-Disposition", String.format("%s", "inline;filename=" + fileName));
                response.setHeader("Content-Disposition", String.format("%s", "attachment;filename=" + fileName));
                response.setHeader("access-control-allow-origin", "*");

                /*saving the excel file*/
                ServletOutputStream outputStream = response.getOutputStream();
                workbook.write(outputStream);

            }
        } catch (IOException e) {
        }
    }

    @Override
    /*all students based on grade*/
    public String create_STUDENT_REPORT_Excel(String fileName, String gradeName, Map<String, Object> dateRange, HttpServletResponse response) throws IOException {
        try {

            String sql = String.format("%s", "FROM StudentAccount s WHERE s.gradeId IN (SELECT g.gradeId FROM Grade g WHERE g.gradeName = '" + gradeName + "')");

            String sqlDateRange = dateRangeSQL_substring(dateRange);
            if (!sqlDateRange.equals("")) {
                sql = String.format("%s", sql + " AND" + sqlDateRange);
            }

            List<Object> studentObjectList = dao.getObject(String.format("%s", sql));
            if (studentObjectList.size() > 0) {

                List<StudentExcel> studentExcelList = new ArrayList<>();

                studentObjectList.forEach((object) -> {
                    StudentAccount sa = (StudentAccount) object;
                    StudentExcel sExcel = new StudentExcel();
                    sExcel.setStudentId(sa.getStudentAccountId());
                    sExcel.setStudentName(String.format("%s", sa.getFirstName() + " " + sa.getLastName()));

                    Object[] loadParentProperties = dao.loadProperties(String.format("%s", "SELECT p.firstName, p.lastName, p.mobileNum, s.syllabusName FROM ParentAccount p, Syllabus s WHERE p.registration.accountId = '" + sa.getParentAccountId() + "' AND s.gradeId IN (SELECT g.gradeId FROM Grade g WHERE g.gradeName = '" + gradeName + "')")).get(0);
                    String fN = (String) loadParentProperties[0];
                    String lN = (String) loadParentProperties[1];
                    long mob = (Long) loadParentProperties[2];
//                    String syllabusName = (String) loadParentProperties[3];

                    String syllabusName = "";
                    if (sa.getSyllabusId() != null) {
                        syllabusName = (String) dao.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId ='" + sa.getSyllabusId() + "'")).get(0);
                    }
                    sExcel.setParentName(String.format("%s", fN + " " + lN));
                    sExcel.setMobileNum(mob);
                    sExcel.setProgramme(syllabusName);

                    sExcel.setDateOfRegistration(sa.getDateOfCreation());
                    sExcel.setGradeName(gradeName);
                    sExcel.setParentEmail(sa.getPrimaryEmail());
                    studentExcelList.add(sExcel);
                });

                /*gerenate the excel*/
                studentReportExcel(fileName, studentExcelList, response);

                return "302";//found
            } else {
                /*no student found in this grade*/
                return "204";//no containts
            }
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    /*all students*/
    public String create_STUDENT_REPORT_Excel(String fileName, Map<String, Object> dateRange, HttpServletResponse response) throws IOException {
        try {
            String sql = String.format("%s", "FROM StudentAccount s");

            String sqlDateRange = dateRangeSQL_substring(dateRange);
            if (!sqlDateRange.equals("")) {
                sql = String.format("%s", sql + " WHERE" + sqlDateRange);
            }

            List<Object> studentObjectList = dao.getObject(String.format("%s", sql));

            if (studentObjectList.size() > 0) {
                List<StudentExcel> studentExcelList = new ArrayList<>();

                studentObjectList.forEach((object) -> {
                    StudentAccount sa = (StudentAccount) object;
                    StudentExcel sExcel = new StudentExcel();
                    sExcel.setStudentId(sa.getStudentAccountId());
                    sExcel.setStudentName(String.format("%s", sa.getFirstName() + " " + sa.getLastName()));

                    Object[] loadParentProperties = dao.loadProperties(String.format("%s", "SELECT p.firstName, p.lastName, p.mobileNum FROM ParentAccount p WHERE p.registration.accountId = '" + sa.getParentAccountId() + "'")).get(0);
                    String fN = (String) loadParentProperties[0];
                    String lN = (String) loadParentProperties[1];
                    long mob = (Long) loadParentProperties[2];

                    String syllabusName = "";
                    if (sa.getSyllabusId() != null) {
                        syllabusName = (String) dao.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId ='" + sa.getSyllabusId() + "'")).get(0);
                    }

                    sExcel.setParentName(String.format("%s", fN + " " + lN));
                    sExcel.setMobileNum(mob);
                    sExcel.setProgramme(syllabusName);

                    sExcel.setDateOfRegistration(sa.getDateOfCreation());
                    String gradeName = "";
                    if (sa.getGradeId() != null) {
                        gradeName = getGradeName(sa.getGradeId());
                    }
                    sExcel.setGradeName(gradeName);
                    sExcel.setParentEmail(sa.getPrimaryEmail());
                    studentExcelList.add(sExcel);

                });

                /*gerenate the excel*/
                studentReportExcel(fileName, studentExcelList, response);

                return "302";//found
            } else {
                /*no student found in this grade*/
                return "204";//no containts
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private String dateRangeSQL_substring(Map<String, Object> dateRange) {
        String sqlDateRange = "";
        if (dateRange != null) {
            String startDate = (String) dateRange.get("startDate");
            String endDate = (String) dateRange.get("endDate");

            if (startDate.equals(endDate)) {
                sqlDateRange = String.format("%s", " DATE(s.dateOfCreation) = '" + startDate + "'");
            } else {
                sqlDateRange = String.format("%s", " DATE(s.dateOfCreation) >= '" + startDate + "' AND DATE(s.dateOfCreation) <= '" + endDate + "'");
            }
        }
        return sqlDateRange;
    }

    @Override
    public String getGradeName(String gradeId) {
        try {
            return (String) dao.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + gradeId + "'")).get(0);
        } catch (Exception e) {
            throw e;
        }
    }

    private void studentReportExcel(String fileName, List<StudentExcel> studentExcelList, HttpServletResponse response) throws IOException {

        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            HSSFSheet sheet = workbook.createSheet("STUDENT'S REPORT");

            /*create the header*/
            HSSFRow rowhead = sheet.createRow((short) 0);
            rowhead.createCell(0).setCellValue("SL. N.");
            rowhead.createCell(1).setCellValue("STUDENT ID");
            rowhead.createCell(2).setCellValue("STUDENT NAME");
            rowhead.createCell(3).setCellValue("PARENT NAME");
            rowhead.createCell(4).setCellValue("DATE OF REG.");
            rowhead.createCell(5).setCellValue("GRADE");
            rowhead.createCell(6).setCellValue("PROGRAMME");
            rowhead.createCell(7).setCellValue("PARENT PHONE");
            rowhead.createCell(8).setCellValue("EMAIL ID");

            /*making the header cells bold*/
            CellStyle cellStyle = workbook.createCellStyle();
            HSSFFont rowHeadFont = workbook.createFont();
            rowHeadFont.setBold(true);
            cellStyle.setFont(rowHeadFont);

            /*setting size of the cells*/
            int n = sheet.getRow(0).getPhysicalNumberOfCells();
            for (int i = 0; i < n; i++) {
                sheet.autoSizeColumn(i);
            }

            /*populating values*/
            CreationHelper createHelper = workbook.getCreationHelper();
            int i = 1;
            int j = 1;
            for (StudentExcel student : studentExcelList) {
                HSSFRow row = sheet.createRow((short) (i++));
                row.createCell(0).setCellValue(j++);
                row.createCell(1).setCellValue(student.getStudentId());
                row.createCell(2).setCellValue(student.getStudentName());
                row.createCell(3).setCellValue(student.getParentName());

                CellStyle cellStyleDATE = workbook.createCellStyle();
                cellStyleDATE.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

                row.createCell(4).setCellValue(student.getDateOfRegistration());
                row.getCell(4).setCellStyle(cellStyleDATE);

                row.createCell(5).setCellValue(student.getGradeName());
                row.createCell(6).setCellValue(student.getProgramme());
                row.createCell(7).setCellValue(student.getMobileNum());
                row.createCell(8).setCellValue(student.getParentEmail());
            }

            response.setContentType("application/vnd.ms-excel");
//                response.setHeader("Content-Disposition", String.format("%s", "inline;filename=" + fileName));
            response.setHeader("Content-Disposition", String.format("%s", "attachment;filename=" + fileName));
            response.setHeader("access-control-allow-origin", "*");

            /*saving the excel file*/
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);

        }

    }

    @Override
    public String create_ADMIN_Excel(String fileName, HttpServletResponse response) throws IOException {

        List<Object> jSONObjects = new ArrayList<>();
        JSONObject json;
        try {

            String sql = "FROM AdminAccount ";
            List<Object> l = dao.getObject(String.format("%s", sql));
            if (l.size() > 0) {
                for (Object object : l) {
                    json = new JSONObject();
                    AdminAccount adminAccount = (AdminAccount) object;

//                    json.put("userRole", "admin");
//                    json.put("firstName", adminAccount.getFirstName());
//                    json.put("lastName", adminAccount.getLastName());
//                    json.put("primaryEmail", adminAccount.getPrimaryEmail());
//                    json.put("password", adminAccount.getPassword());
//                    json.put("countryCode", adminAccount.getCountryCode());
//                    json.put("mobileNum", adminAccount.getMobileNum());
//                    json.put("accountId", adminAccount.getRegistration().getAccountId());
//                    json.put("dateOfCreation", MyDateFormate.dateToString(adminAccount.getDateOfCreation()));
//                    json.put("address", adminAccount.getAddress());
//                    json.put("status", adminAccount.getStatus());
//                    json.put("fullName", String.format("%s", adminAccount.getFirstName() + " " + adminAccount.getLastName()));
//                    json.put("city", adminAccount.getCity());
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
//                    Gson gson = new Gson();
//                    Object object1 = gson.fromJson(json.toString(), Object.class);
                    jSONObjects.add(adminAccount);
                }
//                System.out.println(jSONObjects);
                System.out.println(response);

                adminExcel(fileName, jSONObjects, response);
            } else {
                json = new JSONObject();
                json.put("msg", "Empty list");
                jSONObjects.add(json);
            }
        } catch (Exception e) {

        }

        return "302";

    }

    private void adminExcel(String fileName, List<Object> adminAccounts, HttpServletResponse response) throws IOException {

        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            HSSFSheet sheet = workbook.createSheet("ADMINS");

            /*create the header*/
            HSSFRow rowhead = sheet.createRow((short) 0);
            rowhead.createCell(0).setCellValue("SL. N.");
            rowhead.createCell(1).setCellValue("ADMIN_ID");
            rowhead.createCell(2).setCellValue("FIRSTNAME");
            rowhead.createCell(3).setCellValue("LASTNAME");
            rowhead.createCell(4).setCellValue("PRIMARY_EMAIL");
            rowhead.createCell(5).setCellValue("COUNTRY_CODE");
            rowhead.createCell(6).setCellValue("MOBILE_NUMBER");
            rowhead.createCell(7).setCellValue("DATE_OF_CREATION");
            rowhead.createCell(8).setCellValue("ADDRESS");
            rowhead.createCell(9).setCellValue("CITY");
            rowhead.createCell(10).setCellValue("STATUS");
            rowhead.createCell(11).setCellValue("PARENT_ACCESS");

            rowhead.createCell(12).setCellValue("TEACHER_ACCESS");
            rowhead.createCell(13).setCellValue("STUDENT_ACCESS");
            rowhead.createCell(14).setCellValue("DEMO_MEMBERS_ACCESS");

            rowhead.createCell(15).setCellValue("CLASS_ROOM_ACCESS");
            rowhead.createCell(16).setCellValue("SCHEDULE_ACCESS");
            rowhead.createCell(17).setCellValue("PRODUCT_ACCESS");

            rowhead.createCell(18).setCellValue("SUBSCRIPTION_ACCESS");
            rowhead.createCell(19).setCellValue("BATCH_ACCESS");
            rowhead.createCell(20).setCellValue("TALENT_ACCESS");
            rowhead.createCell(21).setCellValue("RECORDED_ACCESS");

            rowhead.createCell(22).setCellValue("UPCOMING_ACCESS");
            rowhead.createCell(23).setCellValue("ASSIGNMENT_ACCESS");
            rowhead.createCell(24).setCellValue("TEST_ACCESS");
            rowhead.createCell(25).setCellValue("ADMIN_ACCESS");

            /*making the header cells bold*/
            CellStyle cellStyle = workbook.createCellStyle();
            HSSFFont rowHeadFont = workbook.createFont();
            rowHeadFont.setBold(true);
            cellStyle.setFont(rowHeadFont);

            /*setting size of the cells*/
            int n = sheet.getRow(0).getPhysicalNumberOfCells();
            for (int i = 0; i < n; i++) {
                sheet.autoSizeColumn(i);
            }

            /*populating values*/
            CreationHelper createHelper = workbook.getCreationHelper();
            int i = 1;
            int j = 1;
            for (Object object : adminAccounts) {
                AdminAccount adminAccount = (AdminAccount) object;
                HSSFRow row = sheet.createRow((short) (i++));
                row.createCell(0).setCellValue(j++);
                row.createCell(1).setCellValue(adminAccount.getAdminId());
                row.createCell(2).setCellValue(adminAccount.getFirstName());
                row.createCell(3).setCellValue(adminAccount.getLastName());
                row.createCell(4).setCellValue(adminAccount.getPrimaryEmail());
                row.createCell(5).setCellValue(adminAccount.getCountryCode());
                row.createCell(6).setCellValue(adminAccount.getMobileNum());
                CellStyle cellStyleDATE = workbook.createCellStyle();
                cellStyleDATE.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
                row.createCell(7).setCellValue(adminAccount.getDateOfCreation());
                row.getCell(7).setCellStyle(cellStyleDATE);
                row.createCell(8).setCellValue(adminAccount.getAddress());
                row.createCell(9).setCellValue(adminAccount.getCity());
                row.createCell(10).setCellValue(adminAccount.getStatus());
                row.createCell(11).setCellValue(adminAccount.getParentAccess());
                row.createCell(12).setCellValue(adminAccount.getTeacherAccess());
                row.createCell(13).setCellValue(adminAccount.getStudentAccess());
                row.createCell(14).setCellValue(adminAccount.getDemoMemberAccess());
                row.createCell(15).setCellValue(adminAccount.getClassRoomAccess());
                row.createCell(16).setCellValue(adminAccount.getScheduleAccess());
                row.createCell(17).setCellValue(adminAccount.getProdutAccess());
                row.createCell(18).setCellValue(adminAccount.getSubscriptionAccess());
                row.createCell(19).setCellValue(adminAccount.getBatchAccess());
                row.createCell(20).setCellValue(adminAccount.getTalentAccess());
                row.createCell(21).setCellValue(adminAccount.getRecordedAccess());
                row.createCell(22).setCellValue(adminAccount.getUpcomingAccess());
                row.createCell(23).setCellValue(adminAccount.getAssignmentAccess());
                row.createCell(24).setCellValue(adminAccount.getTestAccess());
                row.createCell(25).setCellValue(adminAccount.getAdminAccess());

            }

            response.setContentType("application/vnd.ms-excel");
//                response.setHeader("Content-Disposition", String.format("%s", "inline;filename=" + fileName));
            response.setHeader("Content-Disposition", String.format("%s", "attachment;filename=" + fileName));
            response.setHeader("access-control-allow-origin", "*");

            /*saving the excel file*/
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    @Override
    public String create_PARENTS_Excel(String fileName, HttpServletResponse response) throws IOException {

        List<Object> jSONObjects = new ArrayList<>();
        JSONObject json;
        try {
            String sql = "FROM ParentAccount ";
            List<Object> l = dao.getObject(String.format("%s", sql));
            if (l.size() > 0) {
                for (Object object : l) {
                    ParentAccount parentAccount = (ParentAccount) object;
                    jSONObjects.add(parentAccount);
                }

                parentsExcel(fileName, jSONObjects, response);
            } else {
                json = new JSONObject();
                json.put("msg", "Empty list");
                jSONObjects.add(json);
            }
        } catch (Exception e) {

        }

        return "302";
    }

    private void parentsExcel(String fileName, List<Object> parentAccounts, HttpServletResponse response) throws IOException {

        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            HSSFSheet sheet = workbook.createSheet("PARENTS");
            /*create the header*/
            HSSFRow rowhead = sheet.createRow((short) 0);
            rowhead.createCell(0).setCellValue("SL. N.");
            rowhead.createCell(1).setCellValue("PARENT_ID");
            rowhead.createCell(2).setCellValue("FIRST_NAME");
            rowhead.createCell(3).setCellValue("LAST_NAME");
            rowhead.createCell(4).setCellValue("PRIMARY_EMAIL");
            rowhead.createCell(5).setCellValue("COUNTRY_CODE");
            rowhead.createCell(6).setCellValue("MOBILE_NUMBER");
            rowhead.createCell(7).setCellValue("DATE_OF_CREATION");
            rowhead.createCell(8).setCellValue("ADDRESS");
            rowhead.createCell(9).setCellValue("CITY");

            /*making the header cells bold*/
            CellStyle cellStyle = workbook.createCellStyle();
            HSSFFont rowHeadFont = workbook.createFont();
            rowHeadFont.setBold(true);
            cellStyle.setFont(rowHeadFont);

            /*setting size of the cells*/
            int n = sheet.getRow(0).getPhysicalNumberOfCells();
            for (int i = 0; i < n; i++) {
                sheet.autoSizeColumn(i);
            }

            /*populating values*/
            CreationHelper createHelper = workbook.getCreationHelper();
            int i = 1;
            int j = 1;
            for (Object object : parentAccounts) {
                ParentAccount parentAccount = (ParentAccount) object;
                HSSFRow row = sheet.createRow((short) (i++));
                row.createCell(0).setCellValue(j++);
                row.createCell(1).setCellValue(parentAccount.getParentAccountId());
                row.createCell(2).setCellValue(parentAccount.getFirstName());
                row.createCell(3).setCellValue(parentAccount.getLastName());
                row.createCell(4).setCellValue(parentAccount.getPrimaryEmail());
                row.createCell(5).setCellValue(parentAccount.getCountryCode());
                row.createCell(6).setCellValue(parentAccount.getMobileNum());
                CellStyle cellStyleDATE = workbook.createCellStyle();
                cellStyleDATE.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
                row.createCell(7).setCellValue(parentAccount.getDateOfCreation());
                row.getCell(7).setCellStyle(cellStyleDATE);
                row.createCell(8).setCellValue(parentAccount.getAddress());
                row.createCell(9).setCellValue(parentAccount.getCity());
            }

            response.setContentType("application/vnd.ms-excel");
//                response.setHeader("Content-Disposition", String.format("%s", "inline;filename=" + fileName));
            response.setHeader("Content-Disposition", String.format("%s", "attachment;filename=" + fileName));
            response.setHeader("access-control-allow-origin", "*");

            /*saving the excel file*/
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    @Override
    public String create_TEACHERS_Excel(String fileName, HttpServletResponse response) throws IOException {
        List<Object> jSONObjects = new ArrayList<>();
        JSONObject json;
        try {
            String sql = "FROM TeacherAccount ";
            List<Object> l = dao.getObject(String.format("%s", sql));
            if (l.size() > 0) {
                for (Object object : l) {
                    TeacherAccount teacherAccount = (TeacherAccount) object;
                    jSONObjects.add(teacherAccount);
                }

                teachersExcel(fileName, jSONObjects, response);
            } else {
                json = new JSONObject();
                json.put("msg", "Empty list");
                jSONObjects.add(json);
            }
        } catch (Exception e) {

        }

        return "302";
    }

    private void teachersExcel(String fileName, List<Object> teacherAccounts, HttpServletResponse response) throws IOException {

        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            HSSFSheet sheet = workbook.createSheet("TEACHERS");
            /*create the header*/
            HSSFRow rowhead = sheet.createRow((short) 0);
            rowhead.createCell(0).setCellValue("SL. N.");
            rowhead.createCell(1).setCellValue("TEACHER_ID");
            rowhead.createCell(2).setCellValue("FIRSTNAME");
            rowhead.createCell(3).setCellValue("LASTNAME");
            rowhead.createCell(4).setCellValue("PRIMARY_EMAIL");
            rowhead.createCell(5).setCellValue("COUNTRY_CODE");
            rowhead.createCell(6).setCellValue("MOBILE_NUMBER");
            rowhead.createCell(7).setCellValue("DATE_OF_CREATION");
            rowhead.createCell(8).setCellValue("ADDRESS");
            rowhead.createCell(9).setCellValue("CITY");
            rowhead.createCell(10).setCellValue("STATUS");

            /*making the header cells bold*/
            CellStyle cellStyle = workbook.createCellStyle();
            HSSFFont rowHeadFont = workbook.createFont();
            rowHeadFont.setBold(true);
            cellStyle.setFont(rowHeadFont);

            /*setting size of the cells*/
            int n = sheet.getRow(0).getPhysicalNumberOfCells();
            for (int i = 0; i < n; i++) {
                sheet.autoSizeColumn(i);
            }

            /*populating values*/
            CreationHelper createHelper = workbook.getCreationHelper();
            int i = 1;
            int j = 1;
            for (Object object : teacherAccounts) {
                TeacherAccount teacherAccount = (TeacherAccount) object;
                HSSFRow row = sheet.createRow((short) (i++));
                row.createCell(0).setCellValue(j++);
                row.createCell(1).setCellValue(teacherAccount.getTeacherAccountId());
                row.createCell(2).setCellValue(teacherAccount.getFirstName());
                row.createCell(3).setCellValue(teacherAccount.getLastName());
                row.createCell(4).setCellValue(teacherAccount.getPrimaryEmail());
                row.createCell(5).setCellValue(teacherAccount.getCountryCode());
                row.createCell(6).setCellValue(teacherAccount.getMobileNum());
                CellStyle cellStyleDATE = workbook.createCellStyle();
                cellStyleDATE.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
                row.createCell(7).setCellValue(teacherAccount.getDateOfCreation());
                row.getCell(7).setCellStyle(cellStyleDATE);
                row.createCell(8).setCellValue(teacherAccount.getAddress());
                row.createCell(9).setCellValue(teacherAccount.getCity());
                row.createCell(10).setCellValue(teacherAccount.getStatus());
            }

            response.setContentType("application/vnd.ms-excel");
//                response.setHeader("Content-Disposition", String.format("%s", "inline;filename=" + fileName));
            response.setHeader("Content-Disposition", String.format("%s", "attachment;filename=" + fileName));
            response.setHeader("access-control-allow-origin", "*");

            /*saving the excel file*/
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

}
