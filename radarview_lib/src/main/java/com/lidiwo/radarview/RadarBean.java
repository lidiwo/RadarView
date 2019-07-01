package com.lidiwo.radarview;

/**
 * *****************************************************
 *
 * @author：lidi
 * @date：2019/7/1 13:55
 * @Company：智能程序员
 * @Description： 记录顶脚文字的坐标范围
 * *****************************************************
 */
public class RadarBean {
    private float startX;
    private float startY;
    private float endX;
    private float endY;


    public RadarBean(float startX, float startY, float endX, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }
}
