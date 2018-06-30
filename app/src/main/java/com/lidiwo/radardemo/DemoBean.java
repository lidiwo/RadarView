package com.lidiwo.radardemo;

/**
 * *****************************************************
 *
 * @author：lidi
 * @date：2018/6/28 18:21
 * @Company：智能程序员
 * @Description： *****************************************************
 */
public class DemoBean {
    private String text;
    private float score;

    public DemoBean(String text, float score) {
        this.text = text;
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
