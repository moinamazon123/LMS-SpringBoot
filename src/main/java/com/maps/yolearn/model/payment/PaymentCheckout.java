package com.maps.yolearn.model.payment;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author PREMNATH
 */
@Entity
@Table(name = "PAYMENT_CHECKOUT")
public class PaymentCheckout implements Serializable {

    @Id
    @Column(name = "PAY_CHECKOUTID")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String paymentCheckoutId;

    @Column(name = "TID")
    private Long tid;

    @Column(name = "MERCHANTID")
    private Long merchantId;

    @Column(name = "ORDERID")
    private String orderId;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "SUBSCTYPE_ID")
    private String subsctypeId;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "REDIRECTURL")
    private String redirectURL;
    @Column(name = "BANK_REF_NUMBER")
    private String bank_ref_no;
    @Column(name = "ORDER_STATUS")
    private String order_status;
    @Column(name = "FAILURE_MESSAGE")
    private String failure_message;
    @Column(name = "PAYMENT_MODE")
    private String payment_mode;
    @Column(name = "CARD_NAME")
    private String card_name;
    @Column(name = "STATUS_CODE")
    private String status_code;
    @Column(name = "STATUS_MESSAGE")
    private String status_message;
    @Column(name = "VAULT")
    private String vault;
    @Column(name = "OFFER_TYPE")
    private String offer_type;
    @Column(name = "OFFER_CODE")
    private String offer_code;
    @Column(name = "DISCOUNT_VALUE")
    private String discount_value;
    @Column(name = "MER_AMOUNT")
    private String mer_amount;
    @Column(name = "ECI_VALUE")
    private String eci_value;
    @Column(name = "RETRY")
    private String retry;
    @Column(name = "BILLING_NOTE")
    private String billing_notes;
    @Column(name = "RESPONCE_CODE")
    private String responce_code;
    @Column(name = "TRANS_DATE")
    private String trans_date;
    @Column(name = "BIN_COUNTRY")
    private String bin_country;

    @Column(name = "CANCELURL")
    private String cancelURL;

    @Column(name = "ACCOUNT_ID")
    private String parentAccountId;

    @Column(name = "LANGUAGE")
    private String language;

    @Column(name = "BILL_NAME")
    private String billingName;

    @Column(name = "BILL_ADDRESS")
    private String billingAddress;

    @Column(name = "BILL_CITY")
    private String billingCity;

    @Column(name = "BILL_STATE")
    private String billingState;

    @Column(name = "BILL_ZIP")
    private int billingZip;

    @Column(name = "BILL_COUNTRY")
    private String billingCountry;

    @Column(name = "BILL_TEL")
    private Long billingTel;

    @Column(name = "BILL_EMAIL")
    private String billingEmail;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    @Column(name = "REMAINING_DAYS")
    private int remainingDays;

    @Column(name = "ALLOTED_TO")
    private String allotedStudentAccountId;

    @Column(name = "VALID_FROM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;

    @Column(name = "VALID_TILL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validTill;

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTill() {
        return validTill;
    }

    public void setValidTill(Date validTill) {
        this.validTill = validTill;
    }

    public String getSubsctypeId() {
        return subsctypeId;
    }

    public void setSubsctypeId(String subsctypeId) {
        this.subsctypeId = subsctypeId;
    }

    public String getBank_ref_no() {
        return bank_ref_no;
    }

    public void setBank_ref_no(String bank_ref_no) {
        this.bank_ref_no = bank_ref_no;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getFailure_message() {
        return failure_message;
    }

    public void setFailure_message(String failure_message) {
        this.failure_message = failure_message;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public String getVault() {
        return vault;
    }

    public void setVault(String vault) {
        this.vault = vault;
    }

    public String getOffer_type() {
        return offer_type;
    }

    public void setOffer_type(String offer_type) {
        this.offer_type = offer_type;
    }

    public String getOffer_code() {
        return offer_code;
    }

    public void setOffer_code(String offer_code) {
        this.offer_code = offer_code;
    }

    public String getDiscount_value() {
        return discount_value;
    }

    public void setDiscount_value(String discount_value) {
        this.discount_value = discount_value;
    }

    public String getMer_amount() {
        return mer_amount;
    }

    public void setMer_amount(String mer_amount) {
        this.mer_amount = mer_amount;
    }

    public String getEci_value() {
        return eci_value;
    }

    public void setEci_value(String eci_value) {
        this.eci_value = eci_value;
    }

    public String getRetry() {
        return retry;
    }

    public void setRetry(String retry) {
        this.retry = retry;
    }

    public String getBilling_notes() {
        return billing_notes;
    }

    public void setBilling_notes(String billing_notes) {
        this.billing_notes = billing_notes;
    }

    public String getResponce_code() {
        return responce_code;
    }

    public void setResponce_code(String responce_code) {
        this.responce_code = responce_code;
    }

    public String getTrans_date() {
        return trans_date;
    }

    public void setTrans_date(String trans_date) {
        this.trans_date = trans_date;
    }

    public String getBin_country() {
        return bin_country;
    }

    public void setBin_country(String bin_country) {
        this.bin_country = bin_country;
    }

    public String getParentAccountId() {
        return parentAccountId;
    }

    public void setParentAccountId(String parentAccountId) {
        this.parentAccountId = parentAccountId;
    }

    public String getPaymentCheckoutId() {
        return paymentCheckoutId;
    }

    public void setPaymentCheckoutId(String paymentCheckoutId) {
        this.paymentCheckoutId = paymentCheckoutId;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getCancelURL() {
        return cancelURL;
    }

    public void setCancelURL(String cancelURL) {
        this.cancelURL = cancelURL;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingState() {
        return billingState;
    }

    public void setBillingState(String billingState) {
        this.billingState = billingState;
    }

    public int getBillingZip() {
        return billingZip;
    }

    public void setBillingZip(int billingZip) {
        this.billingZip = billingZip;
    }

    public String getBillingCountry() {
        return billingCountry;
    }

    public void setBillingCountry(String billingCountry) {
        this.billingCountry = billingCountry;
    }

    public Long getBillingTel() {
        return billingTel;
    }

    public void setBillingTel(Long billingTel) {
        this.billingTel = billingTel;
    }

    public String getBillingEmail() {
        return billingEmail;
    }

    public void setBillingEmail(String billingEmail) {
        this.billingEmail = billingEmail;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public int getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }

    public String getAllotedStudentAccountId() {
        return allotedStudentAccountId;
    }

    public void setAllotedStudentAccountId(String allotedStudentAccountId) {
        this.allotedStudentAccountId = allotedStudentAccountId;
    }

}
