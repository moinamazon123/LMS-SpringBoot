package com.maps.yolearn.controller.admin;

import com.maps.yolearn.bean.filter.FilterBean;
import com.maps.yolearn.model.payment.PaymentCheckout;
import com.maps.yolearn.model.user.StudentAccount;
import com.maps.yolearn.service.EntityService;
import com.maps.yolearn.service.ExcelService;
import com.maps.yolearn.util.comparator.StudentAccountComparator;
import com.maps.yolearn.util.date.MyDateFormate;
import com.maps.yolearn.util.filter.FilterUtility;
import com.maps.yolearn.util.ftp.FTPServer;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author VINAYKUMAR
 */
@RestController
@RequestMapping(value = {"/admin"})
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private EntityService service;

    @PostMapping(value = {"/spread-sheet/invoice"})
    public Map<String, Object> generateInvoiceExcel(@RequestBody final FilterBean filter, HttpServletResponse httpResponse) {

        Map<String, Object> map = new HashMap<>();
        Date date = new Date();

        String filename = "Invoice_" + date.getTime() + ".xls";
        try {
            String statusCode = excelService.create_INVOICE_Excel(filename, filter, httpResponse);
            if (statusCode.equals("302")) {
                map.put("msg", "excel has been created!");
            } else if (statusCode.equals("204")) {
                map.put("msg", "no matching record found!");
            }
        } catch (Exception e) {
            map.put("msg", "something went wrong!");
        }
        return map;
    }

    @PostMapping(value = {"/spread-sheet/grade"})
    public Map<String, Object> generateStudentReportExcel(@RequestBody final Map<String, Object> mapbean, HttpServletResponse httpResponse) {
        Map<String, Object> map = new HashMap<>();
        Date date = new Date();
        try {
            Map<String, Object> dateRange = (Map<String, Object>) mapbean.get("dateRange");

            String filename = "Student_Report_" + date.getTime() + ".xls";
            String statusCode;

            String gradeId = (String) mapbean.get("gradeId");

            if (gradeId.equals("all")) {
                /*for all*/
                statusCode = excelService.create_STUDENT_REPORT_Excel(filename, dateRange, httpResponse);
            } else {
                /*for particular grade*/
                String gradeName = excelService.getGradeName(gradeId);
                statusCode = excelService.create_STUDENT_REPORT_Excel(filename, gradeName, dateRange, httpResponse);
            }

            if (statusCode.equals("302")) {
                map.put("msg", "excel has been created!");
            } else if (statusCode.equals("204")) {
                map.put("msg", "no matching record found!");
            }
        } catch (Exception e) {
            map.put("msg", "something went wrong!");
        }
        return map;
    }

    @PostMapping(value = {"/search-stu"})
    public JSONObject searchStudents(@RequestBody final FilterBean filter) {
        /*fixed search-variables*/
        int pageNo = Integer.parseInt(filter.getPageNo());
        int maxRes = 10;
        if (filter.getMaxResult() != null) {
            maxRes = Integer.parseInt(filter.getMaxResult());
        }

        String keyword = filter.getKeyword();

        JSONObject json;
        List<JSONObject> list = new ArrayList<>();
        int count = 0;
        try {
            /*getting studentAccountId list from parentAccountId in student-table */
            String searchIdQueryPARENT = "SELECT r.accountId FROM Registration r WHERE CONCAT(r.firstName, r.lastName, r.mobileNum) LIKE '%" + keyword + "%'";
            List<String> listParentId = (List<String>) (Object) service.getObject(searchIdQueryPARENT);

            List<String> listSAccountId1 = new ArrayList<>();
            if (listParentId.size() > 0) {
                listSAccountId1 = (List<String>) (Object) service.getObject(String.format("%s", "SELECT s.studentAccountId FROM StudentAccount s WHERE s.parentAccountId IN (" + new FilterUtility().getIN_CLAUSE_VALUES(listParentId) + ")"));
            }

            /*getting studentAccountId list by matching-key in student-table */
            List<String> listSAccountId2;
            if (listSAccountId1.size() > 0) {
                listSAccountId2 = (List<String>) (Object) service.getObject(String.format("%s", "SELECT s.studentAccountId FROM StudentAccount s WHERE CONCAT(s.firstName, s.lastName, s.primaryEmail) LIKE '%" + keyword + "%' AND s.studentAccountId NOT IN(" + new FilterUtility().getIN_CLAUSE_VALUES(listSAccountId1) + ")"));
            } else {
                listSAccountId2 = (List<String>) (Object) service.getObject(String.format("%s", "SELECT s.studentAccountId FROM StudentAccount s WHERE CONCAT(s.firstName, s.lastName, s.primaryEmail) LIKE '%" + keyword + "%'"));
            }

            /*adding listSAccountId1 to listAccountIds*/
            List<String> listSAccountId = new ArrayList<>();
            listSAccountId1.forEach(studentAccId -> {
                listSAccountId.add(studentAccId);
            });

            /*adding listSAccountId2 to listAccountIds*/
            listSAccountId2.forEach(studentAccId -> {
                listSAccountId.add(studentAccId);
            });

            /*getting final list of students*/
            List<StudentAccount> listStudents = new ArrayList<>();
            if (listSAccountId.size() > 0) {
                String studentList = "FROM StudentAccount s WHERE s.studentAccountId IN(" + new FilterUtility().getIN_CLAUSE_VALUES(listSAccountId) + ")";
                String countStudents = "SELECT COUNT(*) FROM StudentAccount s WHERE s.studentAccountId IN(" + new FilterUtility().getIN_CLAUSE_VALUES(listSAccountId) + ")";
                listStudents = (List<StudentAccount>) (Object) service.loadByLimit(String.format("%s", studentList), (pageNo * maxRes), maxRes);
                count = (int) service.countObject(countStudents);
            }

            if (listStudents.size() > 0) {
                /*sorting based on studentId*/
                Collections.sort(listStudents, new StudentAccountComparator());

                for (StudentAccount studentAccount : listStudents) {
                    json = new JSONObject();

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

                    Object[] loadParentProperties = service.loadProperties(String.format("%s", "SELECT p.firstName, p.lastName, p.mobileNum FROM ParentAccount p WHERE p.registration.accountId = '" + studentAccount.getParentAccountId() + "'")).get(0);
                    String parentFirstName = (String) loadParentProperties[0];
                    String parentLastName = (String) loadParentProperties[1];
                    long mobileNumber = (Long) loadParentProperties[2];

                    String gradeName = "";
                    if (studentAccount.getGradeId() != null) {
                        gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + studentAccount.getGradeId() + "'")).get(0);
                    }

                    String syllabusName = "";
                    if (studentAccount.getSyllabusId() != null) {
                        syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + studentAccount.getSyllabusId() + "'")).get(0);
                    }

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

                    list.add(json);
                }
            }
        } catch (Exception e) {
        }
        json = new JSONObject();
        json.put("count", count);
        json.put("listOfStudents", list);

        return json;
    }

    public boolean listOfImagesNameExistOrNot(String iname) {

        FTPServer fTPServer = new FTPServer();

        String ftpSourceFolder = "/UserImages/";

        List<String> listImageNames = fTPServer.listFTPFile(ftpSourceFolder);

        return listImageNames.contains(iname);
    }

    @PostMapping(value = {"/search-subs"})
    public JSONObject searchSubscription(@RequestBody final FilterBean filter) {

        /*fixed search-variables*/
        int pageNo = Integer.parseInt(filter.getPageNo());
        int maxRes = 10;
        if (filter.getMaxResult() != null) {
            maxRes = Integer.parseInt(filter.getMaxResult());
        }

        String keyword = filter.getKeyword();

        /*response list*/
        JSONObject json;

        List<JSONObject> list = new ArrayList<>();
        int count = 0;

        /*list parent-account ids in Paymentcheckout*/
        List<String> listAccountIds = new ArrayList<>();
        try {
            /*getting parent-account ids from registration*/
            List<String> listAccountId1 = (List<String>) (Object) service.getObject(String.format("%s", "SELECT r.accountId FROM Registration r WHERE CONCAT(r.firstName, r.lastName, r.mobileNum, r.primaryEmail) LIKE '%" + keyword + "%'"));

            /*getting parent-account ids by allotedTo ids*/
            List<String> listAccountId2 = new ArrayList<>();
            List<String> listStudentAccountId = (List<String>) (Object) service.getObject(String.format("%s", "SELECT s.studentAccountId FROM StudentAccount s WHERE CONCAT(s.firstName, s.lastName) LIKE '%" + keyword + "%'"));
            if (listStudentAccountId.size() > 0) {
                String parentAccIdByAllotedTo = "SELECT p.parentAccountId FROM PaymentCheckout p WHERE p.allotedStudentAccountId IN (" + new FilterUtility().getIN_CLAUSE_VALUES(listStudentAccountId) + ") AND p.parentAccountId NOT IN (" + new FilterUtility().getIN_CLAUSE_VALUES(listAccountId1) + ")";
                listAccountId2 = (List<String>) (Object) service.getObject(parentAccIdByAllotedTo);
            }

            /*adding listAccountId1 to listAccountIds*/
            listAccountId1.forEach(accountId -> {
                listAccountIds.add(accountId);
            });

            /*adding listAccountId2 to listAccountIds*/
            listAccountId2.forEach(accountId -> {
                listAccountIds.add(accountId);
            });

            /*getting PaymentCheckout list*/
            List<PaymentCheckout> checkoutList = new ArrayList<>();
            if (listAccountIds.size() > 0) {

                String paymentList = "FROM PaymentCheckout p WHERE p.parentAccountId IN (" + new FilterUtility().getIN_CLAUSE_VALUES(listAccountIds) + ")";
                String paymentListCount = "SELECT COUNT(*) FROM PaymentCheckout p WHERE p.parentAccountId IN (" + new FilterUtility().getIN_CLAUSE_VALUES(listAccountIds) + ")";

                checkoutList = (List<PaymentCheckout>) (Object) service.loadByLimit(String.format("%s", paymentList), (pageNo * maxRes), maxRes);
                count = (int) service.countObject(paymentListCount);
            }

            if (checkoutList.size() > 0) {
                for (PaymentCheckout checkout : checkoutList) {
                    Object[] subscribeTypeProperties = service.loadProperties(String.format("%s", "SELECT s.gradeId, s.syllabusId, s.days, s.description, s.price, s.subscriptionName FROM SubscribeType s WHERE s.subsTypeId = '" + checkout.getSubsctypeId() + "'")).get(0);
                    String gradeId = (String) subscribeTypeProperties[0];
                    String syllabusId = (String) subscribeTypeProperties[1];
                    String days = (String) subscribeTypeProperties[2];
                    String description = (String) subscribeTypeProperties[3];
                    String price = (String) subscribeTypeProperties[4];
                    String subscriptionName = (String) subscribeTypeProperties[5];

                    Object[] registrationProperties = service.loadProperties(String.format("%s", "SELECT r.address, r.city, r.firstName, r.lastName, r.mobileNum, r.primaryEmail, r.dateOfCreation FROM Registration r WHERE r.accountId = '" + checkout.getParentAccountId() + "'")).get(0);
                    String address = (String) registrationProperties[0];
                    String city = (String) registrationProperties[1];
                    String firstName = (String) registrationProperties[2];
                    String lastName = (String) registrationProperties[3];
                    long mobileNum = (long) registrationProperties[4];
                    String primaryEmail = (String) registrationProperties[5];
                    Date dateOfCreation = (Date) registrationProperties[6];

                    List<Object> listGradeObject = service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + gradeId + "'"));
                    String gradeName = (String) listGradeObject.get(0);

                    List<Object> listSyllabusObj = service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + syllabusId + "'"));
                    String syllabusName = (String) listSyllabusObj.get(0);

                    json = new JSONObject();
                    json.put("syllabusName", syllabusName);
                    json.put("gradeName", gradeName);
                    json.put("days", days);
                    json.put("description", description);
                    json.put("priceInSubscription", price);
                    json.put("subscriptionName", subscriptionName);

                    if (checkout.getValidFrom() != null && checkout.getValidTill() != null) {
                        json.put("validFrom", MyDateFormate.dateToString(checkout.getValidFrom()));
                        json.put("validTill", MyDateFormate.dateToString(checkout.getValidTill()));
                    } else {
                        json.put("validFrom", null);
                        json.put("validTill", null);
                    }

                    json.put("address", address);
                    json.put("city", city);
                    json.put("firstName", firstName);
                    json.put("lastName", lastName);
                    json.put("mobileNum", mobileNum);
                    json.put("primaryEmail", primaryEmail);
                    json.put("amount", checkout.getAmount());
                    json.put("billingAddress", checkout.getBillingAddress());
                    json.put("billingCity", checkout.getBillingCity());
                    json.put("billingCountry", checkout.getBillingCountry());
                    json.put("billingEmail", checkout.getBillingEmail());
                    json.put("billingName", checkout.getBillingName());
                    json.put("billingState", checkout.getBillingState());
                    json.put("billingTel", checkout.getBillingTel());
                    json.put("billingZip", checkout.getBillingZip());
                    json.put("cancel_url", checkout.getCancelURL());
                    json.put("currency", checkout.getCurrency());
                    json.put("language", checkout.getLanguage());
                    json.put("merchant_id", checkout.getMerchantId());
                    json.put("paymentCheckoutId", checkout.getPaymentCheckoutId());
                    json.put("tid", checkout.getTid());
                    json.put("redirect_url", checkout.getRedirectURL());
                    json.put("discount_value", checkout.getDiscount_value());
                    json.put("eci_value", checkout.getEci_value());
                    json.put("failure_message", checkout.getFailure_message());
                    json.put("mer_amount", checkout.getMer_amount());
                    json.put("offer_code", checkout.getOffer_code());
                    json.put("offer_type", checkout.getOffer_type());
                    json.put("order_status", checkout.getOrder_status());
                    json.put("orderId", checkout.getOrderId());
                    json.put("payment_mode", checkout.getPayment_mode());
                    json.put("responce_code", checkout.getResponce_code());
                    json.put("retry", checkout.getRetry());
                    json.put("status_code", checkout.getStatus_code());
                    json.put("status_message", checkout.getStatus_message());
                    json.put("subsctypeId", checkout.getSubsctypeId());
                    json.put("trans_date", checkout.getTrans_date());
                    json.put("vault", checkout.getVault());
                    json.put("parentAccountId", checkout.getParentAccountId());
                    json.put("dateOfCreation", MyDateFormate.dateToString(dateOfCreation));
                    json.put("allotedTo", checkout.getAllotedStudentAccountId());

                    list.add(json);
                }
            }

        } catch (Exception e) {
        }

        json = new JSONObject();
        json.put("count", count);
        json.put("listOfPayments", list);

        return json;
    }

    /**
     * Excel generator for admins
     *
     * @param mapbean
     * @param httpResponse
     * @return
     */

    @GetMapping(value = {"/spread-sheet/admins"})
    public Map<String, Object> generateAdminExcel(HttpServletResponse httpResponse) {
        Map<String, Object> map = new HashMap<>();
        Date date = new Date();
        try {

            String filename = "Admin_" + date.getTime() + ".xls";
            String statusCode;
            statusCode = excelService.create_ADMIN_Excel(filename, httpResponse);
            if (statusCode.equals("302")) {
                map.put("msg", "excel has been created!");
            } else {
                map.put("msg", "no matching record found!");
            }
        } catch (Exception e) {
            map.put("msg", "something went wrong!");
        }
        return map;
    }

    @GetMapping(value = {"/spread-sheet/parents"})
    public Map<String, Object> generateParentExcel(HttpServletResponse httpResponse) {
        Map<String, Object> map = new HashMap<>();
        Date date = new Date();
        try {

            String filename = "Parents_" + date.getTime() + ".xls";
            String statusCode;
            statusCode = excelService.create_PARENTS_Excel(filename, httpResponse);
            if (statusCode.equals("302")) {
                map.put("msg", "excel has been created!");
            } else {
                map.put("msg", "no matching record found!");
            }
        } catch (Exception e) {
            map.put("msg", "something went wrong!");
        }
        return map;
    }

    @GetMapping(value = {"/spread-sheet/teachers"})
    public Map<String, Object> generateTeachersExcel(HttpServletResponse httpResponse) {
        Map<String, Object> map = new HashMap<>();
        Date date = new Date();
        try {

            String filename = "Teachers_" + date.getTime() + ".xls";
            String statusCode;
            statusCode = excelService.create_TEACHERS_Excel(filename, httpResponse);
            if (statusCode.equals("302")) {
                map.put("msg", "excel has been created!");
            } else {
                map.put("msg", "no matching record found!");
            }
        } catch (Exception e) {
            map.put("msg", "something went wrong!");
        }
        return map;
    }

}
