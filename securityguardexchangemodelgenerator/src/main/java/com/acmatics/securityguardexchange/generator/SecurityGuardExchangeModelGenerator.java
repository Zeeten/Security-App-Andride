package com.acmatics.securityguardexchange.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class SecurityGuardExchangeModelGenerator {
    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.acmatics.securityguardexchange.dao");

        Entity questionType = schema.addEntity("QuestionType");
        questionType.addLongProperty("questionTypeId").primaryKey();
        questionType.addStringProperty("name");
        questionType.addStringProperty("description");

        Entity questionLevel = schema.addEntity("QuestionLevel");
        questionLevel.addLongProperty("questionLevelId").primaryKey();
        questionLevel.addStringProperty("name");
        questionLevel.addStringProperty("description");

        Entity question = schema.addEntity("Question");
        question.addLongProperty("questionId").primaryKey();
        question.addStringProperty("textKey");
        question.addStringProperty("descriptionKey");

        //foreign key question type id
        Property questionTypeId = question.addLongProperty("questionTypeId").getProperty();
        question.addToOne(questionType, questionTypeId);

        //foreign key question level id
        Property questionLevelId = question.addLongProperty("questionLevelId").getProperty();
        question.addToOne(questionLevel, questionLevelId);

        question.addBooleanProperty("imageFlag");

        Entity answerChoice = schema.addEntity("AnswerChoice");
        answerChoice.addLongProperty("answerChoiceId").primaryKey();
        answerChoice.addStringProperty("textKey");

        //foreign key question id
        Property questionId = answerChoice.addLongProperty("questionId").getProperty();
        answerChoice.addToOne(question, questionId);

        answerChoice.addBooleanProperty("isAnswerFlag");
        answerChoice.addBooleanProperty("imageFlag");

        //bi-directional mapping
        ToMany answerChoices = question.addToMany(answerChoice, questionId);
        answerChoices.setName("answerChoices");

        Entity userTest = schema.addEntity("UserTest");
        userTest.addLongProperty("userTestId").primaryKey();
        userTest.addIntProperty("maxDuration");

        Entity testQuestionMapping = schema.addEntity("TestQuestionMapping");
        testQuestionMapping.addLongProperty("testQuestionMappingId").primaryKey();
        Property testQuestionId = testQuestionMapping.addLongProperty("questionId").getProperty();
        Property userTestId = testQuestionMapping.addLongProperty("userTestId").getProperty();
        testQuestionMapping.addToOne(question, testQuestionId);
        testQuestionMapping.addToOne(userTest, userTestId);

        Entity testReport = schema.addEntity("TestReport");
        testReport.addLongProperty("testReportId").primaryKey();
        testReport.addDateProperty("testAttemptTime");
        testReport.addIntProperty("duration");
        testReport.addIntProperty("score");
        testReport.addIntProperty("attemptCount");
        Property reportUserTestId = testReport.addLongProperty("userTestId").getProperty();
        testReport.addToOne(userTest, reportUserTestId);

        Entity quizType = schema.addEntity("QuizType");
        quizType.addLongProperty("quizTypeId").primaryKey();
        quizType.addStringProperty("name");
        quizType.addStringProperty("description");

        Entity quiz = schema.addEntity("Quiz");
        quiz.addLongProperty("quizId").primaryKey();
        quiz.addDateProperty("quizDate");
        Property quizTypeId = quiz.addLongProperty("quizTypeId").getProperty();
        quiz.addStringProperty("quizName");
        quiz.addStringProperty("description");
        quiz.addIntProperty("maxDuration");
        quiz.addIntProperty("questionCount");
        quiz.addToOne(quizType, quizTypeId);

        Entity quizQuestion = schema.addEntity("QuizQuestion");
        quizQuestion.addLongProperty("quizQuestionId").primaryKey();
        Property quizId = quizQuestion.addLongProperty("quizId").getProperty();
        Property quizQuestionId = quizQuestion.addLongProperty("questionId").getProperty();
        quizQuestion.addToOne(question, quizQuestionId);
        quizQuestion.addToOne(quiz, quizId);

        Entity userQuizReport = schema.addEntity("UserQuizReport");
        userQuizReport.addLongProperty("userQuizReportId").primaryKey();
        userQuizReport.addLongProperty("userId");
        userQuizReport.addIntProperty("duration");
        userQuizReport.addIntProperty("score");
        userQuizReport.addIntProperty("rank");
        userQuizReport.addDateProperty("quizAttemptTime");
        Property reportQuizId = userQuizReport.addLongProperty("quizId").getProperty();
        userQuizReport.addToOne(quiz, reportQuizId);

        new DaoGenerator().generateAll(schema, "E:/securityguardexchange/sourcecode/securityguardexchange/app/src/main/java");

        //new DaoGenerator().generateAll(schema, "E:/workspaces/androidstudio-workspace/securityguardexchange/app/src/main/java");
    }
}
