package com.acmatics.securityguardexchange.bean;

public class PracticeBean {
    private Long testId;
    private String testNumber;
    private String testDate;
    private String testAttempts;

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public String getTestAttempts() {
        return testAttempts;
    }

    public void setTestAttempts(String testAttempts) {
        this.testAttempts = testAttempts;
    }

    public String getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(String testNumber) {
        this.testNumber = testNumber;
    }


}
