package com.maps.yolearn.bean.filter;

import org.json.simple.JSONObject;

import java.util.List;

/**
 * @author PREMNATH
 */
public class FilterBean {

    private List<String> gradeFilter;
    private List<String> syllabusFilter;
    private List<String> subjectFilter;
    private List<String> freeclass;

    private List<String> chapterFilter;
    private List<String> batchFilter;

    private String pageNo;
    private String maxResult;

    private String keyword;

    private String gradeName;
    private String accessTo;
    private String batchId;
    private String teacherAccountId;
    private String teacher;
    private String sAccountId;
    private String studentAccountId;
    private String sessionId;
    private String scheduledDate;
    private String startDate;
    private String endDate;
    private String status;
    private String pageSize;
    private String text;
    private String syllabus;
    private String subject;
    private String dateOrder;
    private String titleOrder;
    private List<JSONObject> ids;
    private List<String> subScriptionFilter;

    public List<String> getFreeclass() {
        return freeclass;
    }

    public void setFreeclass(List<String> freeclass) {
        this.freeclass = freeclass;
    }

    public List<String> getGradeFilter() {
        return gradeFilter;
    }

    public void setGradeFilter(List<String> gradeFilter) {
        this.gradeFilter = gradeFilter;
    }

    public List<String> getSyllabusFilter() {
        return syllabusFilter;
    }

    public void setSyllabusFilter(List<String> syllabusFilter) {
        this.syllabusFilter = syllabusFilter;
    }

    public List<String> getSubjectFilter() {
        return subjectFilter;
    }

    public void setSubjectFilter(List<String> subjectFilter) {
        this.subjectFilter = subjectFilter;
    }

    public List<String> getChapterFilter() {
        return chapterFilter;
    }

    public void setChapterFilter(List<String> chapterFilter) {
        this.chapterFilter = chapterFilter;
    }

    public List<String> getBatchFilter() {
        return batchFilter;
    }

    public void setBatchFilter(List<String> batchFilter) {
        this.batchFilter = batchFilter;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(String maxResult) {
        this.maxResult = maxResult;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getAccessTo() {
        return accessTo;
    }

    public void setAccessTo(String accessTo) {
        this.accessTo = accessTo;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getTeacherAccountId() {
        return teacherAccountId;
    }

    public void setTeacherAccountId(String teacherAccountId) {
        this.teacherAccountId = teacherAccountId;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getsAccountId() {
        return sAccountId;
    }

    public void setsAccountId(String sAccountId) {
        this.sAccountId = sAccountId;
    }

    public String getStudentAccountId() {
        return studentAccountId;
    }

    public void setStudentAccountId(String studentAccountId) {
        this.studentAccountId = studentAccountId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<JSONObject> getIds() {
        return ids;
    }

    public void setIds(List<JSONObject> ids) {
        this.ids = ids;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
    }

    public String getTitleOrder() {
        return titleOrder;
    }

    public void setTitleOrder(String titleOrder) {
        this.titleOrder = titleOrder;
    }

    public List<String> getSubScriptionFilter() {
        return subScriptionFilter;
    }

    public void setSubScriptionFilter(List<String> subScriptionFilter) {
        this.subScriptionFilter = subScriptionFilter;
    }


    @Override
    public String toString() {
        return "FilterBean{" + "gradeFilter=" + gradeFilter + ", syllabusFilter=" + syllabusFilter + ", subjectFilter=" + subjectFilter + ", freeclass=" + freeclass + ", chapterFilter=" + chapterFilter + ", batchFilter=" + batchFilter + ", pageNo=" + pageNo + ", maxResult=" + maxResult + ", keyword=" + keyword + ", gradeName=" + gradeName + ", accessTo=" + accessTo + ", batchId=" + batchId + ", teacherAccountId=" + teacherAccountId + ", teacher=" + teacher + ", sAccountId=" + sAccountId + ", studentAccountId=" + studentAccountId + ", sessionId=" + sessionId + ", scheduledDate=" + scheduledDate + ", startDate=" + startDate + ", endDate=" + endDate + ", status=" + status + '}';
    }

}
