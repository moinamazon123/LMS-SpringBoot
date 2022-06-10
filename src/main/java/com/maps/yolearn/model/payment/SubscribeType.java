/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maps.yolearn.model.payment;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Length.List;

import javax.persistence.*;
import java.util.Date;

/**
 * @author KOTARAJA
 */
@Entity
@Table(name = "SUBSCRIPTION_TYPE")
public class SubscribeType {

    @Id
    @Column(name = "SUBS_TYPE_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters"),
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String subsTypeId;

    private String description;
//    @Column(name = "BATCH_ID")
//    @List({
//        @Length(min = 2, message = "The field must be at least 2 characters")
//        ,
//    @Length(max = 15, message = "The field must be less than 15 characters")
//    })
//    private String batchId;

    @Column(name = "SYLLABUS_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters"),
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String syllabusId;

    @List({
            @Length(min = 1, message = "The field must be at least 1 characters"),
            @Length(max = 10, message = "The field must be less than 10 characters")
    })
    @Column(name = "PRICE")
    private String price;

    @Column(name = "DAYS")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters"),
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String days;

    @Column(name = "PRODUCT_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters"),
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String productId;

    @Column(name = "SUBSCRIPTION_NAME")
    private String subscriptionName;

    @Column(name = "VALID_FROM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;

    @Column(name = "VALID_TILL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validTill;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    @Column(name = "GRADE_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters"),
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String gradeId;

    @Column(name = "ORIGINAL_PRICE")
    private String originalPrice;

    @Column(name = "STATUS", nullable = false)
    private boolean status = true;

    @Column(name = "STATUS2", nullable = false)
    private int status2 = 0;


    //    @Column(name = "COMBO_ID")
//    @List({
//        @Length(min = 2, message = "The field must be at least 2 characters")
//        ,
//    @Length(max = 15, message = "The field must be less than 15 characters")
//    })
//    private String comboId;
//    @Column(name = "TYPE")
//    @List({
//        @Length(min = 2, message = "The field must be at least 2 characters")
//        ,
//    @Length(max = 15, message = "The field must be less than 15 characters")
//    })
//    private String type;
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getBatchId() {
//        return batchId;
//    }
//
//    public void setBatchId(String batchId) {
//        this.batchId = batchId;
//    }
    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

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

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getSubsTypeId() {
        return subsTypeId;
    }

    public void setSubsTypeId(String subsTypeId) {
        this.subsTypeId = subsTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSyllabusId() {
        return syllabusId;
    }

    public void setSyllabusId(String syllabusId) {
        this.syllabusId = syllabusId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getStatus2() {
        return status2;
    }

    public void setStatus2(int status2) {
        this.status2 = status2;
    }

    @Override
    public String toString() {
        return "SubscribeType{" + "subsTypeId=" + subsTypeId + ", description=" + description + ", syllabusId=" + syllabusId + ", price=" + price + ", days=" + days + ", productId=" + productId + ", subscriptionName=" + subscriptionName + ", validFrom=" + validFrom + ", validTill=" + validTill + ", dateOfCreation=" + dateOfCreation + ", gradeId=" + gradeId + ", originalPrice=" + originalPrice + ", status=" + status + ", status2=" + status2 + '}';
    }


}
