package com.lidiwo.radarview;

/**
 * *****************************************************
 *
 * @author：lidi
 * @date：2018/6/28 15:37
 * @Company：智能程序员
 * @Description： *****************************************************
 */
public interface RadarAdapter {
    String getVertexText(RadarView view, int position);

    float getScore(RadarView view, int position);

    int getTotalCount();

    int getLayerColor(RadarView view, int position);
}
