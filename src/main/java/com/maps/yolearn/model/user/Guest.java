package com.maps.yolearn.model.user;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author KOTARAJA
 */
@Entity
@Table(name = "GUEST_REGISTER")
public class Guest implements Serializable {

    @Id
    @Column(name = "GUEST_ID")
    private String guestId;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE_NUMBER")
    private long phoneNumber;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

}
