package com.maps.yolearn.bean.filter;

import java.util.List;

/**
 * @author MAPS
 */
public class CalenderBean {

    private List<String> freeclass;
    private String batchId;
    private String studentAccountId;
    private String year;
    private String month;

    public List<String> getFreeclass() {
        return freeclass;
    }

    public void setFreeclass(List<String> freeclass) {
        this.freeclass = freeclass;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getStudentAccountId() {
        return studentAccountId;
    }

    public void setStudentAccountId(String studentAccountId) {
        this.studentAccountId = studentAccountId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

}
