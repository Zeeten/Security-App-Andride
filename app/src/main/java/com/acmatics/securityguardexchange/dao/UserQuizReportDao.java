package com.acmatics.securityguardexchange.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;

import com.acmatics.securityguardexchange.dao.UserQuizReport;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER_QUIZ_REPORT".
*/
public class UserQuizReportDao extends AbstractDao<UserQuizReport, Long> {

    public static final String TABLENAME = "USER_QUIZ_REPORT";

    /**
     * Properties of entity UserQuizReport.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property UserQuizReportId = new Property(0, Long.class, "userQuizReportId", true, "USER_QUIZ_REPORT_ID");
        public final static Property UserId = new Property(1, Long.class, "userId", false, "USER_ID");
        public final static Property Duration = new Property(2, Integer.class, "duration", false, "DURATION");
        public final static Property Score = new Property(3, Integer.class, "score", false, "SCORE");
        public final static Property Rank = new Property(4, Integer.class, "rank", false, "RANK");
        public final static Property QuizAttemptTime = new Property(5, java.util.Date.class, "quizAttemptTime", false, "QUIZ_ATTEMPT_TIME");
        public final static Property QuizId = new Property(6, Long.class, "quizId", false, "QUIZ_ID");
    };

    private DaoSession daoSession;


    public UserQuizReportDao(DaoConfig config) {
        super(config);
    }
    
    public UserQuizReportDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_QUIZ_REPORT\" (" + //
                "\"USER_QUIZ_REPORT_ID\" INTEGER PRIMARY KEY ," + // 0: userQuizReportId
                "\"USER_ID\" INTEGER," + // 1: userId
                "\"DURATION\" INTEGER," + // 2: duration
                "\"SCORE\" INTEGER," + // 3: score
                "\"RANK\" INTEGER," + // 4: rank
                "\"QUIZ_ATTEMPT_TIME\" INTEGER," + // 5: quizAttemptTime
                "\"QUIZ_ID\" INTEGER);"); // 6: quizId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_QUIZ_REPORT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, UserQuizReport entity) {
        stmt.clearBindings();
 
        Long userQuizReportId = entity.getUserQuizReportId();
        if (userQuizReportId != null) {
            stmt.bindLong(1, userQuizReportId);
        }
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(2, userId);
        }
 
        Integer duration = entity.getDuration();
        if (duration != null) {
            stmt.bindLong(3, duration);
        }
 
        Integer score = entity.getScore();
        if (score != null) {
            stmt.bindLong(4, score);
        }
 
        Integer rank = entity.getRank();
        if (rank != null) {
            stmt.bindLong(5, rank);
        }
 
        java.util.Date quizAttemptTime = entity.getQuizAttemptTime();
        if (quizAttemptTime != null) {
            stmt.bindLong(6, quizAttemptTime.getTime());
        }
 
        Long quizId = entity.getQuizId();
        if (quizId != null) {
            stmt.bindLong(7, quizId);
        }
    }

    @Override
    protected void attachEntity(UserQuizReport entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public UserQuizReport readEntity(Cursor cursor, int offset) {
        UserQuizReport entity = new UserQuizReport( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // userQuizReportId
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // duration
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // score
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // rank
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // quizAttemptTime
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6) // quizId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, UserQuizReport entity, int offset) {
        entity.setUserQuizReportId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setDuration(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setScore(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setRank(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setQuizAttemptTime(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setQuizId(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(UserQuizReport entity, long rowId) {
        entity.setUserQuizReportId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(UserQuizReport entity) {
        if(entity != null) {
            return entity.getUserQuizReportId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getQuizDao().getAllColumns());
            builder.append(" FROM USER_QUIZ_REPORT T");
            builder.append(" LEFT JOIN QUIZ T0 ON T.\"QUIZ_ID\"=T0.\"QUIZ_ID\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected UserQuizReport loadCurrentDeep(Cursor cursor, boolean lock) {
        UserQuizReport entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Quiz quiz = loadCurrentOther(daoSession.getQuizDao(), cursor, offset);
        entity.setQuiz(quiz);

        return entity;    
    }

    public UserQuizReport loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<UserQuizReport> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<UserQuizReport> list = new ArrayList<UserQuizReport>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<UserQuizReport> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<UserQuizReport> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
