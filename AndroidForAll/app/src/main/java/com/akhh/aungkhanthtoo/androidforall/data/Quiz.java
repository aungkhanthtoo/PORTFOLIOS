package com.akhh.aungkhanthtoo.androidforall.data;

/**
 * Created by Lenovo on 2/8/2018.
 */

public class Quiz {

    private String title;
    private String question;
    private String explanation;
    private int answer;
    private long id;

    public Quiz(long id, String title, String question, String explanation, int answer) {
        this.id = id;
        this.title = title;
        this.question = question;
        this.explanation = explanation;
        this.answer = answer;
    }

    public String getTitle() {
        return title;
    }

    public String getQuestion() {
        return question;
    }

    public String getExplanation() {
        return explanation;
    }

    public int getAnswer() {
        return answer;
    }

    public long getId() {
        return id;
    }
}
