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

import com.acmatics.securityguardexchange.dao.QuizQuestion;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "QUIZ_QUESTION".
*/
public class QuizQuestionDao extends AbstractDao<QuizQuestion, Long> {

    public static final String TABLENAME = "QUIZ_QUESTION";

    /**
     * Properties of entity QuizQuestion.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property QuizQuestionId = new Property(0, Long.class, "quizQuestionId", true, "QUIZ_QUESTION_ID");
        public final static Property QuizId = new Property(1, Long.class, "quizId", false, "QUIZ_ID");
        public final static Property QuestionId = new Property(2, Long.class, "questionId", false, "QUESTION_ID");
    };

    private DaoSession daoSession;


    public QuizQuestionDao(DaoConfig config) {
        super(config);
    }
    
    public QuizQuestionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"QUIZ_QUESTION\" (" + //
                "\"QUIZ_QUESTION_ID\" INTEGER PRIMARY KEY ," + // 0: quizQuestionId
                "\"QUIZ_ID\" INTEGER," + // 1: quizId
                "\"QUESTION_ID\" INTEGER);"); // 2: questionId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"QUIZ_QUESTION\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, QuizQuestion entity) {
        stmt.clearBindings();
 
        Long quizQuestionId = entity.getQuizQuestionId();
        if (quizQuestionId != null) {
            stmt.bindLong(1, quizQuestionId);
        }
 
        Long quizId = entity.getQuizId();
        if (quizId != null) {
            stmt.bindLong(2, quizId);
        }
 
        Long questionId = entity.getQuestionId();
        if (questionId != null) {
            stmt.bindLong(3, questionId);
        }
    }

    @Override
    protected void attachEntity(QuizQuestion entity) {
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
    public QuizQuestion readEntity(Cursor cursor, int offset) {
        QuizQuestion entity = new QuizQuestion( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // quizQuestionId
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // quizId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // questionId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, QuizQuestion entity, int offset) {
        entity.setQuizQuestionId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setQuizId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setQuestionId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(QuizQuestion entity, long rowId) {
        entity.setQuizQuestionId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(QuizQuestion entity) {
        if(entity != null) {
            return entity.getQuizQuestionId();
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
            SqlUtils.appendColumns(builder, "T0", daoSession.getQuestionDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getQuizDao().getAllColumns());
            builder.append(" FROM QUIZ_QUESTION T");
            builder.append(" LEFT JOIN QUESTION T0 ON T.\"QUESTION_ID\"=T0.\"QUESTION_ID\"");
            builder.append(" LEFT JOIN QUIZ T1 ON T.\"QUIZ_ID\"=T1.\"QUIZ_ID\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected QuizQuestion loadCurrentDeep(Cursor cursor, boolean lock) {
        QuizQuestion entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Question question = loadCurrentOther(daoSession.getQuestionDao(), cursor, offset);
        entity.setQuestion(question);
        offset += daoSession.getQuestionDao().getAllColumns().length;

        Quiz quiz = loadCurrentOther(daoSession.getQuizDao(), cursor, offset);
        entity.setQuiz(quiz);

        return entity;    
    }

    public QuizQuestion loadDeep(Long key) {
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
    public List<QuizQuestion> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<QuizQuestion> list = new ArrayList<QuizQuestion>(count);
        
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
    
    protected List<QuizQuestion> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<QuizQuestion> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
