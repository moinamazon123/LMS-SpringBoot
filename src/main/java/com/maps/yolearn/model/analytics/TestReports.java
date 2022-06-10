
package com.maps.yolearn.model.analytics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author KOTARAJA
 */
@Entity
@Table(name = "TEST_REPORT")
public class TestReports {

    @Id
    @Column(name = "REPORT_ID")
    private String reportId;

    @Column(name = "TEST_ID")
    private String testId;

    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @Column(name = "Q1")
    private String qn1;

    @Column(name = "Q2")
    private String qn2;

    @Column(name = "Q3")
    private String qn3;

    @Column(name = "Q4")
    private String qn4;

    @Column(name = "Q5")
    private String qn5;

    @Column(name = "Q6")
    private String qn6;

    @Column(name = "Q7")
    private String qn7;

    @Column(name = "Q8")
    private String qn8;

    @Column(name = "Q9")
    private String qn9;

    @Column(name = "Q10")
    private String qn10;

    @Column(name = "Q11")
    private String qn11;

    @Column(name = "Q12")
    private String qn12;

    @Column(name = "Q13")
    private String qn13;

    @Column(name = "Q14")
    private String qn14;

    @Column(name = "Q15")
    private String qn15;

    @Column(name = "Q16")
    private String qn16;

    @Column(name = "Q17")
    private String qn17;

    @Column(name = "Q18")
    private String qn18;

    @Column(name = "Q19")
    private String qn19;

    @Column(name = "Q20")
    private String qn20;
    @Column(name = "SCORE")
    private String score;
    @Column(name = "WRITE_ANSWERS")
    private String writeAnswers;
    @Column(name = "WRONG_ANSWERS")
    private String wrongAnswers;
    @Column(name = "COUNT")
    private int count;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getWriteAnswers() {
        return writeAnswers;
    }

    public void setWriteAnswers(String writeAnswers) {
        this.writeAnswers = writeAnswers;
    }

    public String getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(String wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getQn1() {
        return qn1;
    }

    public void setQn1(String qn1) {
        this.qn1 = qn1;
    }

    public String getQn2() {
        return qn2;
    }

    public void setQn2(String qn2) {
        this.qn2 = qn2;
    }

    public String getQn3() {
        return qn3;
    }

    public void setQn3(String qn3) {
        this.qn3 = qn3;
    }

    public String getQn4() {
        return qn4;
    }

    public void setQn4(String qn4) {
        this.qn4 = qn4;
    }

    public String getQn5() {
        return qn5;
    }

    public void setQn5(String qn5) {
        this.qn5 = qn5;
    }

    public String getQn6() {
        return qn6;
    }

    public void setQn6(String qn6) {
        this.qn6 = qn6;
    }

    public String getQn7() {
        return qn7;
    }

    public void setQn7(String qn7) {
        this.qn7 = qn7;
    }

    public String getQn8() {
        return qn8;
    }

    public void setQn8(String qn8) {
        this.qn8 = qn8;
    }

    public String getQn9() {
        return qn9;
    }

    public void setQn9(String qn9) {
        this.qn9 = qn9;
    }

    public String getQn10() {
        return qn10;
    }

    public void setQn10(String qn10) {
        this.qn10 = qn10;
    }

    public String getQn11() {
        return qn11;
    }

    public void setQn11(String qn11) {
        this.qn11 = qn11;
    }

    public String getQn12() {
        return qn12;
    }

    public void setQn12(String qn12) {
        this.qn12 = qn12;
    }

    public String getQn13() {
        return qn13;
    }

    public void setQn13(String qn13) {
        this.qn13 = qn13;
    }

    public String getQn14() {
        return qn14;
    }

    public void setQn14(String qn14) {
        this.qn14 = qn14;
    }

    public String getQn15() {
        return qn15;
    }

    public void setQn15(String qn15) {
        this.qn15 = qn15;
    }

    public String getQn16() {
        return qn16;
    }

    public void setQn16(String qn16) {
        this.qn16 = qn16;
    }

    public String getQn17() {
        return qn17;
    }

    public void setQn17(String qn17) {
        this.qn17 = qn17;
    }

    public String getQn18() {
        return qn18;
    }

    public void setQn18(String qn18) {
        this.qn18 = qn18;
    }

    public String getQn19() {
        return qn19;
    }

    public void setQn19(String qn19) {
        this.qn19 = qn19;
    }

    public String getQn20() {
        return qn20;
    }

    public void setQn20(String qn20) {
        this.qn20 = qn20;
    }

}
