package com.maps.yolearn.controller.payment;

import com.maps.yolearn.bean.filter.FilterBean;
import com.maps.yolearn.bean.grade.GradeMetaData;
import com.maps.yolearn.bean.payment.PaymentCheckoutBean;
import com.maps.yolearn.bean.user.UsersMetaData;
import com.maps.yolearn.model.grade.Grade;
import com.maps.yolearn.model.payment.PackageReg;
import com.maps.yolearn.model.payment.PaymentCheckout;
import com.maps.yolearn.model.payment.SubscribeType;
import com.maps.yolearn.model.payment.UseSubscrption;
import com.maps.yolearn.model.user.Registration;
import com.maps.yolearn.model.user.StudentAccount;
import com.maps.yolearn.service.EntityService;
import com.maps.yolearn.util.date.MyDateFormate;
import com.maps.yolearn.util.filter.FilterUtility;
import com.maps.yolearn.util.mail.E_Mail_Sender_Account;
import com.maps.yolearn.util.mail.E_Mail_Sender_info;
import com.maps.yolearn.util.primarykey.CustomPKGenerator;
import com.maps.yolearn.util.sms.SendingMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.maps.yolearn.model.payment.*;

/**
 * @author KOTARAJA
 * @author PREMNATH
 * @author VINAYKUMAR
 */
@RestController
@RequestMapping(value = {"/payment"})
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    E_Mail_Sender_Account javaMail_Sender_Account = new E_Mail_Sender_Account();
    E_Mail_Sender_info javaMail_Sender_Info = new E_Mail_Sender_info();
    @Autowired
    private EntityService service;
    @Autowired
    private CustomPKGenerator pKGenerator;

    @RequestMapping(method = RequestMethod.POST, value = "/listOfSubscription")
    public @ResponseBody
    ResponseEntity<?> listOfSubscription(@RequestBody final Map<String, String> mapBean) {
        int pageNo = Integer.parseInt(mapBean.get("pageNo"));

        int maxResult = 10;
        if (mapBean.get("maxResult") != null) {
            maxResult = Integer.parseInt(mapBean.get("maxResult"));
        }
        JSONObject json;
        List<JSONObject> list = new ArrayList<>();
        int count = 0;
        try {
            count = (int) service.countObject(String.format("%s", "SELECT COUNT(*) FROM SubscribeType"));
            List<Object> listObject = service.loadByLimit(String.format("%s", "FROM SubscribeType"), (pageNo * maxResult), maxResult);
            for (Object object : listObject) {
                SubscribeType subscribeType = (SubscribeType) object;
                json = new JSONObject();
                json.put("days", subscribeType.getDays());
                json.put("description", subscribeType.getDescription());
                json.put("price", subscribeType.getPrice());
                json.put("subscriptonId", subscribeType.getSubsTypeId());
                json.put("packageId", subscribeType.getProductId());
                json.put("validTill", MyDateFormate.dateToString(subscribeType.getValidTill()));
                json.put("validFrom", MyDateFormate.dateToString(subscribeType.getValidFrom()));
                json.put("gradeId", subscribeType.getGradeId());

                String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + subscribeType.getGradeId() + "'")).get(0);
                json.put("gradeName", gradeName);

                list.add(json);
            }
        } catch (Exception e) {
        }
        json = new JSONObject();
        json.put("count", count);
        json.put("listOfSubscription", list);
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listOfSubscriptionByGradeId")
    public @ResponseBody
    JSONArray listOfSubscriptionByGradeId(@RequestBody GradeMetaData metaData) {
        JSONArray array = new JSONArray();
        JSONObject json = null;

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("gradeId", metaData.getGradeId());//GRADE000001

            List<Object> listObject4 = service.getObject(SubscribeType.class, map);
            if (listObject4.size() > 0) {
                for (Object object : listObject4) {
                    SubscribeType subscribeType = (SubscribeType) object;
                    json = new JSONObject();
                    json.put("days", subscribeType.getDays());
                    json.put("description", subscribeType.getDescription());
                    json.put("price", subscribeType.getPrice());
                    json.put("validFrom", MyDateFormate.dateToString(subscribeType.getValidFrom()));
                    json.put("validTill", MyDateFormate.dateToString(subscribeType.getValidTill()));
                    json.put("packageId", subscribeType.getProductId());
                    json.put("subscTypeId", subscribeType.getSubsTypeId());
                    array.add(json);

                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("packageId", subscribeType.getProductId());//GRADE000001
                    List<Object> listObject5 = service.getObject(PackageReg.class, map1);
                    if (listObject5.size() > 0) {
                        for (Object object1 : listObject5) {
                            PackageReg packageReg = (PackageReg) object1;
                            json = new JSONObject();
                            json.put("dateOfCreation", MyDateFormate.dateToString(packageReg.getDateOfCreation()));
                            json.put("packageType", packageReg.getPackageType());
                            json.put("packageId", packageReg.getPackageId());
                            json.put("packages", json);
                        }
                    } else {
                        json = new JSONObject();
                        json.put("msg", "packageId miss matching");
                    }
                }
            } else {
                array.add(json);
                json = new JSONObject();
                json.put("msg", "Miss  match gradeId in SubscriptioType");
            }
        } catch (Exception e) {
            array.add(json);
            json = new JSONObject();
            json.put("msg", "Error " + e.getMessage());
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/studentIdBysyvScriptionList")
    public @ResponseBody
    List<Object> studentIdBysyvScriptionList(@RequestBody final UsersMetaData bean) {

        JSONObject jSONObject;
        List<Object> list = new ArrayList<>();
        StudentAccount studentAccount;
        SubscribeType subscribeType;
        Grade grade;
        PackageReg packageReg;

        try {
            Map<String, Object> m = new HashMap<>();
            m.put("studentAccountId", bean.getStudentAccountId());

            List<Object> listobjects1 = service.getObject(StudentAccount.class, m);
            if (listobjects1.size() > 0) {
                for (Object object : listobjects1) {
                    jSONObject = new JSONObject();
                    studentAccount = (StudentAccount) object;
                    studentAccount.getSubscribeId();

                    Map<String, Object> m1 = new HashMap<>();
                    m1.put("subsTypeId", studentAccount.getSubscribeId());

                    List<Object> listobjects11 = service.getObject(SubscribeType.class, m1);
                    if (listobjects11.size() > 0) {
                        for (Object object1 : listobjects11) {
                            subscribeType = (SubscribeType) object1;
                            jSONObject.put("days", subscribeType.getDays());
                            jSONObject.put("description", subscribeType.getDescription());
                            jSONObject.put("price", subscribeType.getPrice());
                            jSONObject.put("gradeId", subscribeType.getGradeId());
                            jSONObject.put("packageId", subscribeType.getProductId());
                            jSONObject.put("validFrom", MyDateFormate.dateToString(subscribeType.getValidFrom()));
                            jSONObject.put("validTill", MyDateFormate.dateToString(subscribeType.getValidTill()));

                            Map<String, Object> m11 = new HashMap<>();
                            m11.put("packageId", subscribeType.getProductId());

                            List<Object> listobjects111 = service.getObject(PackageReg.class, m11);
                            if (listobjects111.size() > 0) {
                                for (Object object2 : listobjects111) {
                                    packageReg = (PackageReg) object2;
                                    jSONObject.put("packageTypr", packageReg.getPackageType());
                                    jSONObject.put("dateOfCreation", MyDateFormate.dateToString(packageReg.getDateOfCreation()));
                                    Map<String, Object> m111 = new HashMap<>();
                                    m111.put("gradeId", subscribeType.getGradeId());
                                    List<Object> listobjects112 = service.getObject(Grade.class, m111);
                                    if (listobjects112.size() > 0) {
                                        grade = (Grade) listobjects112.get(0);
                                        jSONObject.put("gradeName", grade.getGradeName());
                                        list.add(jSONObject);
                                    } else {
                                        jSONObject.put("msg", "miss match garadeId in grade class");
                                        list.add(jSONObject);
                                    }
                                }
                            } else {
                                jSONObject = new JSONObject();
                                jSONObject.put("msg", "miss match packageId in PackageReg");
                                list.add(jSONObject);
                            }
                        }
                    } else {
                        jSONObject = new JSONObject();
                        jSONObject.put("msg", "miss match subsTypeId in SubscribeType");
                        list.add(jSONObject);
                    }
                }
            } else {
                jSONObject = new JSONObject();
                jSONObject.put("msg", "no student is available in this record");
                list.add(jSONObject);
            }
        } catch (Exception e) {
            jSONObject = new JSONObject();
            jSONObject.put("msg", "Error " + e.getMessage());
            list.add(jSONObject);
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getSubscriptionbyAccountId")
    public @ResponseBody
    List<Object> getSubscriptionbyAccountId(@RequestBody UsersMetaData bean) {
        JSONObject json;
        UseSubscrption useSubscrption;
        Grade grade;
        String subscriptionId;
        List<Object> list = new ArrayList<>();

        try {
            String accountId = bean.getAccountId();
            Map<String, Object> map = new HashMap<>();
            map.put("accountId", accountId);
            List<Object> listobjects = service.getObject(UseSubscrption.class, map);
            if (listobjects.size() > 0) {
                for (Object listobject : listobjects) {
                    json = new JSONObject();
                    useSubscrption = (UseSubscrption) listobject;
                    subscriptionId = useSubscrption.getSubsType();
                    json.put("subsctypeId", useSubscrption.getSubsType());

                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("subsTypeId", subscriptionId);

                    List<Object> listobjects1 = service.getObject(SubscribeType.class, map1);
                    if (listobjects1.size() > 0) {
                        for (Object object : listobjects1) {
                            SubscribeType subscribeType = (SubscribeType) object;
                            json.put("validFrom", MyDateFormate.dateToString(subscribeType.getValidFrom()));
                            json.put("validTill", MyDateFormate.dateToString(subscribeType.getValidTill()));
                            json.put("price", subscribeType.getPrice());
                            json.put("description", subscribeType.getDescription());
                            json.put("days", subscribeType.getDays());
                            json.put("gradeId", subscribeType.getGradeId());
                            json.put("packageId", subscribeType.getProductId());
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("gradeId", subscribeType.getGradeId());
                            List<Object> listobjects2 = service.getObject(Grade.class, map2);
                            if (listobjects2.size() > 0) {
                                for (Object object1 : listobjects2) {
                                    grade = (Grade) object1;
                                    json.put("gradeName", grade.getGradeName());
                                    list.add(json);
                                }
                            } else {
                                json.put("msg", "Miss match at gradeId in  Grade class");
                                list.add(json);
                            }
                        }
                    } else {
                        json = new JSONObject();
                        json.put("msg", "Miss match at subsTypeId in  SubscribeType class");
                        list.add(json);
                    }
                }
            } else {
                json = new JSONObject();
                json.put("msg", "Miss match at accountId in  UseSubscrption class");
                list.add(json);
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Error " + e.getMessage());
            list.add(json);
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/saveSubscrptionToUserSubscription")
    public @ResponseBody
    JSONObject saveSubscrptionToUserSubscription(@RequestBody UsersMetaData bean) {
        JSONObject json = new JSONObject();

        UseSubscrption useSubscrption = new UseSubscrption();
        String subId = (String) pKGenerator.generate(UseSubscrption.class, "SUBS_");
        useSubscrption.setSubscribeID(subId);
        useSubscrption.setGradeId(bean.getGradeId());
        useSubscrption.setSubsType(bean.getSubsTypeId());
        useSubscrption.setAccountId(bean.getAccountId());

        try {
            String str = service.saveOrUpdate(useSubscrption);
            if (!str.equals("")) {
                json.put("msg", "saved");
            } else {
                json.put("msg", "not saved !");
            }
        } catch (Exception e) {
            json.put("msg", "something went wrong!" + e.getMessage());
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createProducts")
    public @ResponseBody
    JSONObject createProducts(@RequestBody GradeMetaData bean) {
        JSONObject json = new JSONObject();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        Date dateOfCreation = MyDateFormate.stringToDate(String.format("%s", df.format(new Date())));

        SubscribeType subscribeType = new SubscribeType();
        String subscriptionId = (String) pKGenerator.generate(SubscribeType.class, "SUBST");
        subscribeType.setDescription(bean.getDescription());
        subscribeType.setPrice(bean.getPrice());
        subscribeType.setSubsTypeId(subscriptionId);
        subscribeType.setValidFrom(MyDateFormate.stringToDateOnlyDate(bean.getValidFrom()));
        subscribeType.setValidTill(MyDateFormate.stringToDateOnlyDate(bean.getValidTill()));
        subscribeType.setSubscriptionName(bean.getSubscriptionName());
        subscribeType.setGradeId(bean.getGradeId());
        subscribeType.setSyllabusId(bean.getSyllabusId());
        subscribeType.setDateOfCreation(dateOfCreation);
        subscribeType.setOriginalPrice(bean.getOriginalPrice());
        try {
            String str = service.saveOrUpdate(subscribeType);
            if (!str.equals("")) {
                json.put("subscriptionId", subscriptionId);
                json.put("msg", "saved");
                json.put("subscriptionName", bean.getSubscriptionName());
                json.put("validFrom", bean.getValidFrom());
                json.put("validTill", bean.getValidTill());
                json.put("price", bean.getPrice());
                json.put("description", bean.getDescription());
                json.put("gradeId", bean.getGradeId());
                json.put("syllabusId", bean.getSyllabusId());
                json.put("dateOfCreation", dateOfCreation);
                json.put("originalPrice", bean.getOriginalPrice());

            } else {
                json.put("msg", "not saved !");
            }
        } catch (Exception e) {
            json.put("msg", "something went wrong!" + e.getMessage());
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateProduct")
    public @ResponseBody
    JSONObject updateProduct(@RequestBody final GradeMetaData bean) {
        JSONObject json;
        String productId = bean.getSubscriptionId();

        Map<String, Object> map = new HashMap<>();
        map.put("subsTypeId", productId);

        List<Object> listOfTeacherObj = service.getObject(SubscribeType.class, map);
        if (listOfTeacherObj.size() > 0) {
            json = new JSONObject();
            SubscribeType subscribeType = (SubscribeType) listOfTeacherObj.get(0);
            subscribeType.setDescription(bean.getDescription());
            subscribeType.setSubscriptionName(bean.getSubscriptionName());
            subscribeType.setPrice(bean.getPrice());
            subscribeType.setValidFrom(MyDateFormate.stringToDateOnlyDate(bean.getValidFrom()));
            subscribeType.setValidTill(MyDateFormate.stringToDateOnlyDate(bean.getValidTill()));
            subscribeType.setGradeId(bean.getGradeId());
            subscribeType.setSyllabusId(bean.getSyllabusId());
            try {
                int x11 = service.update(subscribeType);
                if (x11 > 0) {
                    json.put("msg", "updated product");
                    json.put("validTill", subscribeType.getValidTill());
                    json.put("validFrom", subscribeType.getValidFrom());
                    json.put("gardeId", subscribeType.getGradeId());
                    json.put("syllabusId", subscribeType.getSyllabusId());
                } else {
                    json.put("msg", "not updated product");
                }
            } catch (Exception e) {
                json = new JSONObject();
                json.put("msg", "Error " + e.getMessage());
            }
        } else {
            json = new JSONObject();
            json.put("msg", "miss match at subsTypeId in SubscribeType class");
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listOfSubscriptionWitoutId")
    public @ResponseBody
    List<Object> listOfSubscriptionWitoutId() {
        List<Object> list = new ArrayList<>();
        JSONObject json;
        try {
            List<Object> listObject = service.getObject(SubscribeType.class);

            if (listObject.size() > 0) {
                for (Object object : listObject) {
                    SubscribeType type = (SubscribeType) object;
                    json = new JSONObject();
                    json.put("days", type.getDays());
                    json.put("description", type.getDescription());
                    json.put("price", type.getPrice());
                    json.put("subscriptionId", type.getSubsTypeId());
                    json.put("packageId", type.getProductId());
                    json.put("syllabusId", type.getSyllabusId());
                    json.put("gradeId", type.getGradeId());
                    json.put("validTill", type.getValidTill());
                    json.put("validFrom", type.getValidFrom());
                    json.put("subscriptionName", type.getSubscriptionName());

                    String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + type.getSyllabusId() + "'")).get(0);
                    String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + type.getGradeId() + "'")).get(0);
                    json.put("syllabusName", syllabusName);
                    json.put("gradeName", gradeName);

                    list.add(json);
                }
            } else {
                json = new JSONObject();
                json.put("msg", "Empty list");
                list.add(json);
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Error " + e.getMessage());
            e.printStackTrace();
            list.add(json);
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listOfSubscriptionWithFilters")
    public @ResponseBody
    JSONObject listOfSubscriptionWithFilters(@RequestBody final FilterBean filterBean) {
        List<Object> list = new ArrayList<>();
        JSONObject json;
        JSONObject jSONObject = new JSONObject();
        List<String> gradeFilters = filterBean.getGradeFilter();
        List<String> syllabusFilters = filterBean.getSyllabusFilter();
        String status = filterBean.getStatus();
        String dateOrder = filterBean.getDateOrder();
        String sqlQuery = new FilterUtility().productsFilters(gradeFilters, syllabusFilters, status, dateOrder);
        long totalResultCount = this.service.countObject("select count(*) FROM SubscribeType s ");
        long noOfFilterResultCount = this.service.countObject("select count(*) " + sqlQuery);

        try {

            List<Object> listObject = this.service.getObject(sqlQuery);
//            List<Object> listObject = service.getObject(SubscribeType.class);
//
            if (listObject.size() > 0) {
                for (Object object : listObject) {
                    SubscribeType type = (SubscribeType) object;
                    json = new JSONObject();
                    json.put("days", type.getDays());
                    json.put("description", type.getDescription());
                    json.put("price", type.getPrice());
                    json.put("subscriptionId", type.getSubsTypeId());
                    json.put("packageId", type.getProductId());
                    json.put("syllabusId", type.getSyllabusId());
                    json.put("gradeId", type.getGradeId());
                    json.put("validTill", type.getValidTill());
                    json.put("validFrom", type.getValidFrom());
                    json.put("subscriptionName", type.getSubscriptionName());
                    json.put("dateOfCreation", type.getDateOfCreation());

                    String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + type.getSyllabusId() + "'")).get(0);
                    String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + type.getGradeId() + "'")).get(0);
                    json.put("syllabusName", syllabusName);
                    json.put("gradeName", gradeName);

                    list.add(json);
                }
            } else {
                json = new JSONObject();
                json.put("msg", "Empty list");
                list.add(json);
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", "Error " + e.getMessage());
            e.printStackTrace();
            list.add(json);
        }

        jSONObject.put("productsList", list);
        jSONObject.put("noOfFilterResultCount", noOfFilterResultCount);
        jSONObject.put("count", totalResultCount);

        return jSONObject;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listOfSubscriptionWithId")
    public @ResponseBody
    JSONObject listOfSubscriptionWithId(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        try {
            List<Object> listObject = service.getObject(String.format("%s", "FROM SubscribeType s WHERE s.subsTypeId = '" + mapBean.get("subscriptionId") + "'"));
            SubscribeType type = (SubscribeType) listObject.get(0);
            json.put("days", type.getDays());
            json.put("description", type.getDescription());
            json.put("price", type.getPrice());
            json.put("subscriptionId", type.getSubsTypeId());
            json.put("packageId", type.getProductId());
            json.put("validTill", type.getValidTill());
            json.put("validFrom", type.getValidFrom());
            json.put("subscriptionName", type.getSubscriptionName());

            String syllabusName = (String) service.getObject(String.format("%s", "SELECT s.syllabusName FROM Syllabus s WHERE s.syllabusId = '" + type.getSyllabusId() + "'")).get(0);
            json.put("syllabusName", syllabusName);
            String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + type.getGradeId() + "'")).get(0);
            json.put("gradeName", gradeName);

        } catch (Exception e) {
            json.put("msg", "Something went wrong. Try Again!");
        }
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/deleteProduct")
    public @ResponseBody
    JSONObject deleteProduct(@RequestBody final Map<String, String> bean) {
        JSONObject json = new JSONObject();
        String subscriptionId = bean.get("subscriptionId");
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("subsTypeId", subscriptionId);
            int x = service.delete(SubscribeType.class, map);
            if (x > 0) {
                json.put("msg", "subscription deleted ");
            }
        } catch (Exception e) {
            json.put("msg", "Error " + e.getMessage());
        }
        return json;
    }

    @RequestMapping(value = {"/paymentCheckout"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> paymentCheckout(@RequestBody final PaymentCheckoutBean bean) {
        JSONObject json = new JSONObject();
        Date date = new Date();

        String tId = bean.getTid();
        String merchantId = bean.getMerchant_id();
        String orderId = bean.getOrder_id();
        String amount = bean.getAmount();
        String subsctypeId = bean.getSubsctypeId();
        String parentAccountId = bean.getParentAccountId();
        boolean skipPayment = bean.isSkipPayment();
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        List<Object> listObj = service.getObject(PaymentCheckout.class, map);
        if (listObj.size() > 0) {
            json.put("msg", "Duplicate OrderId!");
        } else {
            PaymentCheckout paymentCheckout = new PaymentCheckout();
            String paymentCheckoutId = (String) pKGenerator.generate(PaymentCheckout.class, "PAYCHECK");
            paymentCheckout.setPaymentCheckoutId(paymentCheckoutId);
            paymentCheckout.setTid(Long.parseLong(tId));
            paymentCheckout.setMerchantId(Long.parseLong(merchantId));
            paymentCheckout.setOrderId((orderId));
            paymentCheckout.setAmount(Double.parseDouble(amount));
            paymentCheckout.setSubsctypeId(subsctypeId);
            paymentCheckout.setCurrency("INR");
            paymentCheckout.setRedirectURL("null");
            paymentCheckout.setBank_ref_no("null");
            if (skipPayment) {
                paymentCheckout.setOrder_status("Success");
            } else {
                paymentCheckout.setOrder_status("Aborted");
            }
            paymentCheckout.setFailure_message("null");
            paymentCheckout.setPayment_mode("null");
            paymentCheckout.setCard_name("null");
            paymentCheckout.setStatus_code("null");
            paymentCheckout.setStatus_message("null");
            paymentCheckout.setVault("null");
            paymentCheckout.setOffer_type("null");
            paymentCheckout.setOffer_code("null");
            paymentCheckout.setDiscount_value("0.0");
            paymentCheckout.setMer_amount("0.0");
            paymentCheckout.setEci_value("null");
            paymentCheckout.setRetry("null");
            paymentCheckout.setBilling_notes("null");
            paymentCheckout.setResponce_code("null");
            paymentCheckout.setTrans_date(MyDateFormate.dateToString1(date));
            paymentCheckout.setBin_country("null");
            paymentCheckout.setCancelURL("null");
            paymentCheckout.setParentAccountId(parentAccountId);
            paymentCheckout.setLanguage("null");
            paymentCheckout.setBillingName("null");
            paymentCheckout.setBillingAddress("null");
            paymentCheckout.setBillingCity("null");
            paymentCheckout.setBillingState("null");
            paymentCheckout.setBillingZip(0);
            paymentCheckout.setBillingCountry("null");
            paymentCheckout.setBillingTel(0L);
            paymentCheckout.setBillingEmail("null");
            paymentCheckout.setDateOfCreation(date);
            paymentCheckout.setRemainingDays(0);
            paymentCheckout.setAllotedStudentAccountId("null");
            String x = service.saveOrUpdate(paymentCheckout);
            if (x.length() > 0) {
                json.put("msg", "saved successfully!");
                json.put("paymentCheckoutId", paymentCheckout.getPaymentCheckoutId());
                json.put("tid", paymentCheckout.getTid());
                json.put("merchant_id", paymentCheckout.getMerchantId());
                json.put("order_id", paymentCheckout.getOrderId());
                json.put("amount", paymentCheckout.getAmount());
                json.put("currency", paymentCheckout.getCurrency());
                json.put("redirect_url", paymentCheckout.getRedirectURL());
                json.put("cancel_url", paymentCheckout.getCancelURL());
                json.put("language", paymentCheckout.getLanguage());
                json.put("dateOfCreation", MyDateFormate.dateToString(paymentCheckout.getDateOfCreation()));
            } else {
                json.put("msg", "Something went wrong. Try Again!");
            }
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getallPayments")
    public @ResponseBody
    ResponseEntity<?> getallPayments(@RequestBody final FilterBean filter) {

        int pageNo = Integer.parseInt(filter.getPageNo());
        int maxRes = 10;
        if (filter.getMaxResult() != null) {
            maxRes = Integer.parseInt(filter.getMaxResult());
        }

        String status = filter.getStatus();
        if (status.equals("all")) {
            status = null;
        }

        String startDate = (String) filter.getStartDate();
        if (startDate.equals("all")) {
            startDate = null;
        }

        String endDate = (String) filter.getEndDate();
        if (endDate.equals("all")) {
            endDate = null;
        }

        List<String> gradeFilter = filter.getGradeFilter();
        if (gradeFilter.get(0).equals("all")) {
            gradeFilter = null;
        }

        List<String> studentIds = new ArrayList<>();
        if (gradeFilter != null) {
            studentIds = getStudentIds(gradeFilter.get(0));
        }

        List<String> programFilters = filter.getSubScriptionFilter();
        if (programFilters.get(0).equals("all")) {
            programFilters = null;
        }
        List<String> syllabusFilters = filter.getSyllabusFilter();
        if (syllabusFilters.get(0).equals("all")) {
            syllabusFilters = null;
        }

        Map<String, String> mapFilterSQL = new FilterUtility().listOfPaymentFilter(status, startDate, endDate, studentIds);

        FilterUtility filterUtility = new FilterUtility();
        String paymentCheckoutFilterSqlQuerySimplified = filterUtility.paymentCheckoutFilterSqlQuerySimplified(status, startDate, endDate, status, studentIds, programFilters, syllabusFilters);

        List<Object> list = new ArrayList<>();
        JSONObject json;
//        int count = 0;
        long paymentCheckoutTotalCount = 0;
        long PaymentCheckoutResultsCount = 0;
        try {


            String totalCount = "select count(*) FROM PaymentCheckout p where p.paymentCheckoutId is not null";
            paymentCheckoutTotalCount = this.service.countObject(totalCount);

            String resultCount = "select count(*) " + paymentCheckoutFilterSqlQuerySimplified;
            PaymentCheckoutResultsCount = this.service.countObject(resultCount);


//            count = (int) service.countObject(String.format("%s", mapFilterSQL.get("SQLcountPaymentCheckout")));

//            List<Object> listPaymentCheckoutObject = service.loadByLimit(String.format("%s", mapFilterSQL.get("SQLlistPaymentCheckout")), (pageNo * maxRes), maxRes);
            List<Object> listPaymentCheckoutObject = service.loadByLimit(String.format("%s", paymentCheckoutFilterSqlQuerySimplified), (pageNo * maxRes), maxRes);

            if (listPaymentCheckoutObject.size() > 0) {
                for (Object object : listPaymentCheckoutObject) {
                    PaymentCheckout checkout = (PaymentCheckout) object;
                    System.out.println(checkout.getSubsctypeId());
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
                    ;
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
                    if (!"null".equals(checkout.getAllotedStudentAccountId()) && !"undefined".equals(checkout.getAllotedStudentAccountId())) {
                        Object object1 = this.service.getObject("FROM StudentAccount s WHERE s.studentAccountId='" + checkout.getAllotedStudentAccountId() + "'").get(0);
                        StudentAccount studentAccount = (StudentAccount) object1;
                        json.put("studentFirstName", studentAccount.getFirstName());
                        json.put("studentFirstName", studentAccount.getLastName());
                        json.put("studentFullName", studentAccount.getFirstName() + " " + studentAccount.getLastName());
                    } else {
                        json.put("studentFirstName", "studentFirstNameNotAvailable");
                        json.put("studentFirstName", "studentLastNameNotAvailable");
                        json.put("studentFullName", "studentNameNotAvailable");
                    }
                    list.add(json);
                }
            } else {
                json = new JSONObject();
                json.put("msg", "No Payment found!");
                list.add(json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        json = new JSONObject();
        json.put("count", paymentCheckoutTotalCount);
        json.put("no of records displaying on this page", PaymentCheckoutResultsCount);
        json.put("listOfPayments", list);

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    public List<String> getStudentIds(String gradeId) {
        String sql = "SELECT s.studentAccountId FROM StudentAccount s WHERE s.gradeId = '" + gradeId + "'";
        List<String> studentIds = (List<String>) (Object) service.getObject(sql);

        return studentIds;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getdetailsOfPayementByAccountId")
    public @ResponseBody
    List<Object> getdetailsOfPayementByAccountId(@RequestBody PaymentCheckoutBean bean) {
        List<Object> list = new ArrayList<>();
        JSONObject json;
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("parentAccountId", bean.getParentAccountId());
            List<Object> l = service.getObject(PaymentCheckout.class, m);
            if (l.size() > 0) {
                for (Object object : l) {
                    Map<String, Object> m1 = new HashMap<>();
                    m1.put("accountId", bean.getParentAccountId());
                    List<Object> ls = service.getObject(Registration.class, m1);
                    if (ls.size() > 0) {
                        Registration account = (Registration) ls.get(0);
                        PaymentCheckout checkout = (PaymentCheckout) object;
                        Map<String, Object> m13 = new HashMap<>();
                        m13.put("subsTypeId", checkout.getSubsctypeId());
                        List<Object> ls3 = service.getObject(SubscribeType.class, m13);
                        if (ls3.size() > 0) {
                            SubscribeType subscribeType = (SubscribeType) ls3.get(0);
                            json = new JSONObject();
                            json.put("days", subscribeType.getDays());
                            json.put("description", subscribeType.getDescription());
                            json.put("priceInSubscription", subscribeType.getPrice());
                            json.put("subscriptionName", subscribeType.getSubscriptionName());
                            json.put("validFrom", MyDateFormate.dateToString(subscribeType.getValidFrom()));
                            json.put("validTill", MyDateFormate.dateToString(subscribeType.getValidTill()));
                            json.put("address", account.getAddress());
                            json.put("city", account.getCity());
                            json.put("firstName", account.getFirstName());
                            json.put("lastName", account.getLastName());
                            json.put("mobileNum", account.getMobileNum());
                            json.put("primaryEmail", account.getPrimaryEmail());
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
                            json.put("dateOfCreation", MyDateFormate.dateToString(account.getDateOfCreation()));

                            json.put("parentAccountId", checkout.getParentAccountId());
                            list.add(json);

                        } else {
                            json = new JSONObject();
                            json.put("msg", "Empty list subscription ");
                            list.add(json);
                        }
                    } else {
                        json = new JSONObject();
                        json.put("msg", "Empty list registration");
                        list.add(json);
                    }
                }

            } else {
                json = new JSONObject();
                json.put("msg", "Empty list");
                list.add(json);
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", e.getMessage());
            list.add(json);
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getdetailsOfPayementByParentUsingOrder")
    public @ResponseBody
    List<Object> getdetailsOfPayementByParent(@RequestBody PaymentCheckoutBean bean) {
        List<Object> list = new ArrayList<>();
        JSONObject json;
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("orderId", bean.getOrder_id());
            m.put("parentAccountId", bean.getParentAccountId());
            List<Object> l = service.getObject(PaymentCheckout.class, m);
            if (l.size() > 0) {

                for (Object object : l) {

                    PaymentCheckout checkout = (PaymentCheckout) object;
                    Map<String, Object> m1 = new HashMap<>();
                    m1.put("accountId", bean.getParentAccountId());
                    List<Object> ls = service.getObject(Registration.class, m1);
                    if (ls.size() > 0) {
                        Registration account = (Registration) ls.get(0);
                        json = new JSONObject();
                        json.put("address", account.getAddress());
                        json.put("city", account.getCity());
                        json.put("firstName", account.getFirstName());
                        json.put("lastName", account.getLastName());
                        json.put("mobileNum", account.getMobileNum());
                        json.put("primaryEmail", account.getPrimaryEmail());
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
                        json.put("dateOfCreation", MyDateFormate.dateToString(account.getDateOfCreation()));

                        json.put("parentAccountId", checkout.getParentAccountId());
                        list.add(json);
                    } else {
                        json = new JSONObject();
                        json.put("msg", "Empty list ");
                        list.add(json);
                    }
                }

            } else {
                json = new JSONObject();
                json.put("msg", "Empty list ");
                list.add(json);
            }
        } catch (Exception e) {
            json = new JSONObject();
            json.put("msg", e.getMessage());
            list.add(json);
        }
        return list;
    }

    @RequestMapping(value = {"/subscriptionStatusByParentAccoId"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> subscriptionStatusByParentAccoId(@RequestBody PaymentCheckoutBean bean) {
        List<JSONObject> list = new ArrayList<>();
        JSONObject json;
        String parentAccountId = bean.getParentAccountId();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("parentAccountId", parentAccountId);
        List<Object> liPaymentCheckoutObject = service.getObject(PaymentCheckout.class, map1);
        if (liPaymentCheckoutObject.size() > 0) {
            for (Object object : liPaymentCheckoutObject) {
                PaymentCheckout checkout = (PaymentCheckout) object;
                if (checkout.getOrder_status().equals("Success")) {

                    json = new JSONObject();
                    json.put("trans_date", checkout.getTrans_date());
                    json.put("subsTypeId", checkout.getSubsctypeId());
                    json.put("subsTypeId", checkout.getSubsctypeId());
                    json.put("orderId", checkout.getOrderId());
                    json.put("allotedStudentId", checkout.getAllotedStudentAccountId());

                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("subsTypeId", checkout.getSubsctypeId());
                    SubscribeType subscribeType = (SubscribeType) service.getObject(SubscribeType.class, map2).get(0);
                    json.put("priceInSubscription", subscribeType.getPrice());
                    json.put("subscriptionName", subscribeType.getSubscriptionName());
                    json.put("validFrom", subscribeType.getValidFrom());
                    json.put("validTill", subscribeType.getValidTill());

                    list.add(json);
                }
            }
        } else {
            json = new JSONObject();
            json.put("msg", "no subscription");
            list.add(json);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = {"/allotSubscriptionTo"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> allotSubscriptionTo(@RequestBody final Map<String, String> mapBean) {
        JSONObject json = new JSONObject();
        String parentAccountId = mapBean.get("parentAccountId");
        String studentAccountId = mapBean.get("studentAccountId");
        String orderId = mapBean.get("orderId");
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("parentAccountId", parentAccountId);
            map.put("orderId", orderId);
            PaymentCheckout paymentCheckout = (PaymentCheckout) service.getObject(PaymentCheckout.class, map).get(0);
            paymentCheckout.setAllotedStudentAccountId(studentAccountId);
            if (service.update(paymentCheckout) > 0) {

                Map<String, Object> map2 = new HashMap<>();
                map2.put("subsTypeId", paymentCheckout.getSubsctypeId());
                SubscribeType subscribeType = (SubscribeType) service.getObject(SubscribeType.class, map2).get(0);

                if (subscribeType.getSubscriptionName().equals("Training")) {
                    Calendar c = new GregorianCalendar();
                    c.add(Calendar.DATE, 31);
                    Date d = c.getTime();
                    paymentCheckout.setValidFrom(new Date());
                    paymentCheckout.setValidTill(d);

                } else {
                    paymentCheckout.setValidFrom(subscribeType.getValidFrom());
                    paymentCheckout.setValidTill(subscribeType.getValidTill());
                }

                if (service.update(paymentCheckout) > 0) {

                    Object[] studentProperties = service.loadProperties(String.format("%s", "SELECT s.firstName, s.lastName, s.primaryEmail, s.password FROM StudentAccount s WHERE s.studentAccountId = '" + studentAccountId + "'")).get(0);
                    String firstName = (String) studentProperties[0];
                    String lastName = (String) studentProperties[1];
                    String primaryEmail = (String) studentProperties[2];
                    String pass = (String) studentProperties[3];

                    String admin = "admin@yolearn.com";
                    String info = "info@yolearn.com";
                    String subject = "YOLEARN Registration Confirmation";

                    /*sending email to the user*/
                    String emailMsg = "Hi <b>" + firstName + ",</b><br><br>"
                            + "Your registration has been done.<br><br>"
                            + "<b>Login details:</b><br>"
                            + "<table>"
                            + "<tr><td>Email</td><td> : " + primaryEmail + "</td></tr>"
                            + "<tr><td>Password</td><td> : " + pass + "</td></tr>"
                            + "<tr><td>Program</td><td> : " + subscribeType.getSubscriptionName() + "</td></tr>"
                            + "</table><br><br>"
                            + "<small>For any queries, please send email to: <a href='mailto:info@yolearn.com'>info@yolearn.com</a><br><br>"
                            + "You received this message because this email address was used to register you in <b>YO</b>LEARN. If that is incorrect, please ignore this message.</small><br><br>"
                            + "<b>Yours Sincerely,</b><br>"
                            + "YOLEARN Team.<br><br>"
                            + "Thanks for choosing Yolearn";

                    Set<String> to1 = new HashSet<>();
                    to1.add(primaryEmail);
                    javaMail_Sender_Info.composeAndSend(subject, to1, emailMsg);

                    /*sending email to the admin*/
                    String subject1 = "Registration Confirmation(Student)";
                    String emailMsg1 = "Hi Admin,<br><br>"
                            + "New student has been registered successfully.<br><br>"
                            + "Name: <b>" + firstName + " " + lastName + "<br><br>"
                            + "Login details:</b>"
                            + "<table>"
                            + "<tr><td>Email</td><td> : " + primaryEmail + "</td></tr>"
                            + "<tr><td>Password</td><td> : " + pass + "</td></tr>"
                            + "</table><br><br>"
                            + "<b>Yours Sincerely,</b><br>"
                            + "YOLEARN Team.<br><br>"
                            + "Thanks for choosing Yolearn";

                    Set<String> to2 = new HashSet<>();
                    to2.add(admin);
                    to2.add(info);
                    javaMail_Sender_Info.composeAndSend(subject1, to2, emailMsg1);
                    json.put("msg", "student alloted to subscription");
                }

            } else {
                json.put("msg", "something went wrong");
            }
        } catch (MessagingException e) {
            json.put("msg", "something went wrong");
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/senttomail")
    public @ResponseBody
    JSONObject senttomail(@RequestBody PaymentCheckoutBean bean) throws IOException, MessagingException {
        JSONObject jsono = new JSONObject();
        try {
            if (bean.getOrder_status().equals("Success")) {
                Map<String, Object> m = new HashMap<>();
                m.put("orderId", bean.getOrder_id());
                m.put("order_status", bean.getOrder_status());

                List<Object> list = service.getObject(PaymentCheckout.class, m);

                if (list.size() > 0) {

                    PaymentCheckout account = (PaymentCheckout) list.get(0);
                    Map<String, Object> m11 = new HashMap<>();
                    m11.put("subsTypeId", account.getSubsctypeId());
                    List<Object> listss = service.getObject(SubscribeType.class, m11);
                    if (listss.size() > 0) {

                        SubscribeType subscribeType = (SubscribeType) listss.get(0);
                        Map<String, Object> m111 = new HashMap<>();
                        m111.put("gradeId", subscribeType.getGradeId());
                        List<Object> listss1 = service.getObject(Grade.class, m111);
                        if (listss1.size() > 0) {
                            Grade g = (Grade) listss1.get(0);
                            String discou = account.getDiscount_value();
                            String merche = account.getMer_amount();
                            String[] merch = merche.split("\\.");
                            String[] disc = discou.split("\\.");
                            String ismerc = merch[0];
                            String isdisc = disc[0];
                            int a = Integer.parseInt(ismerc);
                            int b1 = Integer.parseInt(isdisc);
                            int c = a - b1;
                            Long l = account.getBillingTel();
                            String number = Long.toString(l);
                            SendingMessage class2 = new SendingMessage();

                            class2.SMSSender("269441A7NKDGP0wO5c9b1726", number, "Congratulations " + account.getBillingName() + "," + "\n Thank you for your payment. We have received your payement of Rs." + c + ".0 successfully." + "\n  Your Transaction ID is: " + account.getTid() + "\n    " + "\n Thank You-YOLEARN" + "\n Visit https://yolearn.com", "YOLEAR", "91", "0", "4");
                            // please do not uncomment below commented code  
//                            String remoteFilePathss = String.format("%s", "/Billing/invoice/" + bean.getOrder_id() + ".pdf");
//
//                            String fileName = "Yolearn_Invoice.pdf";
//
//                            FTPFileUtility fTPServer = new FTPFileUtility();
//                            InputStream in = fTPServer.retrieveFile(remoteFilePathss, ftpAddr, portNumber, ftpUserName, password);
                            String subject = "Your Order# " + account.getOrderId() + "  on https://yolearn.com is successful.";

                            String msg = "Dear Sir/Madam " + account.getBillingName() + "," + "<br><br>"
                                    + "Thank you for your order from https://yolearn.com .We have received your payement of <b>Rs." + c + ".00</b> successfully. <br><br>"
                                    + "<b>Order Details:</b>"
                                    + "<table>"
                                    + "<tr><td>Invoice Number</td><td> : " + account.getOrderId() + "</td></tr>"
                                    + "<tr><td>Invoice Date</td><td> : " + account.getTrans_date() + "</td></tr>"
                                    + "<tr><td>Package Name</td><td> : " + subscribeType.getSubscriptionName() + "</td></tr>"
                                    + "<tr><td>Grade</td><td> : " + g.getGradeName() + "</td></tr>"
                                    + "</table><br><br>"
                                    + "<b><br> Thank You & Regards,</b><br>"
                                    + "<b>YO</b>LEARN Team.<br><br>"
                                    + "Thanks for choosing yolearn";

                            Set<String> to1 = new HashSet<>();
                            to1.add(account.getBillingEmail());

                            javaMail_Sender_Account.composeAndSend(subject, to1, msg);
                            jsono.put("msg", "mail sent");

                            String subject1 = "Your Order# " + account.getOrderId() + "  on https://yolearn.com is successful.";

                            String msg1 = "Dear Admin ," + "<br><br>"
                                    + "Thank you for your payment. We have received your payement of <b>Rs." + c + ".00</b> successfully. <br><br>"
                                    + "<b>Order Details:</b>"
                                    + "<table>"
                                    + "<tr><td>Invoice Number</td><td> : " + account.getOrderId() + "</td></tr>"
                                    + "<tr><td>Invoice Date</td><td> : " + account.getTrans_date() + "</td></tr>"
                                    + "<tr><td>Product</td><td> : Online Virtual Class Room(" + subscribeType.getSubscriptionName() + ")</td></tr>"
                                    + "<tr><td>Grade</td><td> : " + g.getGradeName() + "</td></tr>"
                                    + "</table><br><br>"
                                    + "<b><br> Thank You & Regards,</b><br>"
                                    + "<b>YO</b>LEARN Team.<b><br>"
                                    + "Thanks for choosing yolearn";

                            String adminMail = "admin@yolearn.com";
                            Set<String> to11 = new HashSet<>();
                            to11.add(adminMail);

                            javaMail_Sender_Account.composeAndSend(subject1, to11, msg1);
                            jsono.put("msg", "mail sent");
                        } else {
                            jsono.put("msg", "empty list in gradeId");
                        }
                    } else {

                        jsono.put("msg", "empty list in substype");
                    }
                } else {
                    jsono.put("msg", "Empty list in payement type!");
                }

            }
        } catch (NumberFormatException | MessagingException e) {
            jsono.put("msg", e.getMessage());
        }
        return jsono;
    }

    @RequestMapping(value = {"/getAllSubscriptionTypesByGradeId"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getAllSubscriptionTypesByGradeId(@RequestBody final Map<String, String> mapBean) {
        List<JSONObject> list = new ArrayList<>();
        String gradeId = mapBean.get("gradeId");
        try {
            String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + gradeId + "'")).get(0);

            List<Object> listSubscObject = service.getObject(String.format("%s", "FROM SubscribeType s WHERE s.gradeId = '" + gradeId + "' and s.status=true ORDER BY s.dateOfCreation DESC"));
            if (listSubscObject.size() > 0) {
                for (Object object : listSubscObject) {
                    SubscribeType subscribeType = (SubscribeType) object;
                    JSONObject json = new JSONObject();
                    json.put("subscriptonId", subscribeType.getSubsTypeId());
                    json.put("packageId", subscribeType.getProductId());
                    json.put("days", subscribeType.getDays());
                    json.put("subscriptionName", subscribeType.getSubscriptionName());
                    json.put("validTill", MyDateFormate.dateToString(subscribeType.getValidTill()));
                    json.put("validFrom", MyDateFormate.dateToString(subscribeType.getValidFrom()));
                    json.put("price", subscribeType.getPrice());
                    json.put("description", subscribeType.getDescription());
                    json.put("gradeId", subscribeType.getGradeId());
                    json.put("dateOfCreation", subscribeType.getDateOfCreation());
                    json.put("gradeName", gradeName);
                    json.put("originalPrice", subscribeType.getOriginalPrice());
                    json.put("status", subscribeType.isStatus());
                    if (subscribeType.getStatus2() != 2) {
                        list.add(json);
                    }

                }
            } else {
                JSONObject json = new JSONObject();
                json.put("msg", "No product found to grade - " + gradeName);
                list.add(json);
            }
        } catch (Exception e) {
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * URLS on PRoducts May 26 2020
     *
     * @param mapBean
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/createProducts2")
    public @ResponseBody
    JSONObject createProductsSecondURL(@RequestBody GradeMetaData bean) {
        JSONObject json = new JSONObject();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        Date dateOfCreation = MyDateFormate.stringToDate(String.format("%s", df.format(new Date())));

        SubscribeType subscribeType = new SubscribeType();
        String subscriptionId = (String) pKGenerator.generate(SubscribeType.class, "SUBST");
        subscribeType.setDescription(bean.getDescription());
        subscribeType.setPrice(bean.getPrice());
        subscribeType.setSubsTypeId(subscriptionId);
        subscribeType.setValidFrom(MyDateFormate.stringToDateOnlyDate(bean.getValidFrom()));
        subscribeType.setValidTill(MyDateFormate.stringToDateOnlyDate(bean.getValidTill()));
        subscribeType.setSubscriptionName(bean.getSubscriptionName());
        subscribeType.setGradeId(bean.getGradeId());
        subscribeType.setSyllabusId(bean.getSyllabusId());
        subscribeType.setDateOfCreation(dateOfCreation);
        subscribeType.setOriginalPrice(bean.getOriginalPrice());
        subscribeType.setStatus2(2);
        try {
            String str = service.saveOrUpdate(subscribeType);
            if (!str.equals("")) {
                json.put("subscriptionId", subscriptionId);
                json.put("msg", "saved");
                json.put("subscriptionName", bean.getSubscriptionName());
                json.put("validFrom", bean.getValidFrom());
                json.put("validTill", bean.getValidTill());
                json.put("price", bean.getPrice());
                json.put("description", bean.getDescription());
                json.put("gradeId", bean.getGradeId());
                json.put("syllabusId", bean.getSyllabusId());
                json.put("dateOfCreation", dateOfCreation);
                json.put("originalPrice", bean.getOriginalPrice());

            } else {
                json.put("msg", "not saved !");
            }
        } catch (Exception e) {
            json.put("msg", "something went wrong!" + e.getMessage());
        }
        return json;
    }

    @RequestMapping(value = {"/getProductsByStaus"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> getAllProductsByStatus(@RequestBody final Map<String, String> mapBean) {
        List<JSONObject> list = new ArrayList<>();

        String gradeId = mapBean.get("gradeId");
        String status2 = mapBean.get("status2");
        try {
            String gradeName = (String) service.getObject(String.format("%s", "SELECT g.gradeName FROM Grade g WHERE g.gradeId = '" + gradeId + "'")).get(0);
            List<Object> listSubscObject = service.getObject(String.format("%s", "FROM SubscribeType s WHERE s.gradeId = '" + gradeId + "' and s.status2=" + status2 + " ORDER BY s.dateOfCreation DESC"));
            if (listSubscObject.size() > 0) {
                for (Object object : listSubscObject) {
                    SubscribeType subscribeType = (SubscribeType) object;
                    JSONObject json = new JSONObject();
                    json.put("subscriptonId", subscribeType.getSubsTypeId());
                    json.put("packageId", subscribeType.getProductId());
                    json.put("days", subscribeType.getDays());
                    json.put("subscriptionName", subscribeType.getSubscriptionName());
                    json.put("validTill", MyDateFormate.dateToString(subscribeType.getValidTill()));
                    json.put("validFrom", MyDateFormate.dateToString(subscribeType.getValidFrom()));
                    json.put("price", subscribeType.getPrice());
                    json.put("description", subscribeType.getDescription());
                    json.put("gradeId", subscribeType.getGradeId());
                    json.put("dateOfCreation", subscribeType.getDateOfCreation());
                    json.put("gradeName", gradeName);
                    json.put("originalPrice", subscribeType.getOriginalPrice());
                    json.put("status", subscribeType.isStatus());
                    json.put("status2", subscribeType.getStatus2());
                    list.add(json);

                }
            } else {
                JSONObject json = new JSONObject();
                json.put("msg", "No product found to grade - " + gradeName);
                list.add(json);
            }
        } catch (Exception exception) {
            JSONObject json = new JSONObject();
            json.put("msg", "Something went wrong");
            list.add(json);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
