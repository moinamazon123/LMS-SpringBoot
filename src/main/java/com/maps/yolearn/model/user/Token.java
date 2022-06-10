package com.maps.yolearn.model.user;

import javax.persistence.*;
import java.util.Date;

/**
 * @author KOTARAJA
 */
@Entity
@Table(name = "ZOOM_TOKEN")
public class Token {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "token")
    private String token;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }




}
