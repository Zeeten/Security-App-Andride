package com.acmatics.securityguardexchange.bean;

import java.util.List;

/**
 * Created by kaira on 12/4/2015.
 */
public class QuizQuestionBean {
    private String quizQuestion;
    private List<QuizAnswerBean> quizAnswer;

    public List<QuizAnswerBean> getQuizAnswer() {
        return quizAnswer;
    }

    public void setQuizAnswer(List<QuizAnswerBean> quizAnswer) {
        this.quizAnswer = quizAnswer;
    }


    public String getQuizQuestion() {
        return quizQuestion;
    }

    public void setQuizQuestion(String quizQuestion) {
        this.quizQuestion = quizQuestion;
    }


}
