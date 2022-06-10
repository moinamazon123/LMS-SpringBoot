package com.maps.yolearn.bean.liveclass;

import java.util.List;

/**
 * @author PREMNATH
 */
public class BatchIdToStudents {

    private String batchId;
    private List<String> studentIdList;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public List<String> getStudentIdList() {
        return studentIdList;
    }

    public void setStudentIdList(List<String> studentIdList) {
        this.studentIdList = studentIdList;
    }

}
