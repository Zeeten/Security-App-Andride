package com.acmatics.securityguardexchange.dao;

import com.acmatics.securityguardexchange.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "TEST_REPORT".
 */
public class TestReport {

    private Long testReportId;
    private java.util.Date testAttemptTime;
    private Integer duration;
    private Integer score;
    private Integer attemptCount;
    private Long userTestId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient TestReportDao myDao;

    private UserTest userTest;
    private Long userTest__resolvedKey;


    public TestReport() {
    }

    public TestReport(Long testReportId) {
        this.testReportId = testReportId;
    }

    public TestReport(Long testReportId, java.util.Date testAttemptTime, Integer duration, Integer score, Integer attemptCount, Long userTestId) {
        this.testReportId = testReportId;
        this.testAttemptTime = testAttemptTime;
        this.duration = duration;
        this.score = score;
        this.attemptCount = attemptCount;
        this.userTestId = userTestId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTestReportDao() : null;
    }

    public Long getTestReportId() {
        return testReportId;
    }

    public void setTestReportId(Long testReportId) {
        this.testReportId = testReportId;
    }

    public java.util.Date getTestAttemptTime() {
        return testAttemptTime;
    }

    public void setTestAttemptTime(java.util.Date testAttemptTime) {
        this.testAttemptTime = testAttemptTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public Long getUserTestId() {
        return userTestId;
    }

    public void setUserTestId(Long userTestId) {
        this.userTestId = userTestId;
    }

    /** To-one relationship, resolved on first access. */
    public UserTest getUserTest() {
        Long __key = this.userTestId;
        if (userTest__resolvedKey == null || !userTest__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserTestDao targetDao = daoSession.getUserTestDao();
            UserTest userTestNew = targetDao.load(__key);
            synchronized (this) {
                userTest = userTestNew;
            	userTest__resolvedKey = __key;
            }
        }
        return userTest;
    }

    public void setUserTest(UserTest userTest) {
        synchronized (this) {
            this.userTest = userTest;
            userTestId = userTest == null ? null : userTest.getUserTestId();
            userTest__resolvedKey = userTestId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
