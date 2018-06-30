package com.lidiwo.radardemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lidiwo.radarview.RadarAdapter;
import com.lidiwo.radarview.RadarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<DemoBean > mShowData=new ArrayList<>();
    private List<Integer > layerColor=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RadarView rv_radar=findViewById(R.id.rv_radar);

        Collections.addAll(mShowData,new DemoBean("鹿鼎记",1),
                                     new DemoBean("雪山飞狐",3),
                                     new DemoBean("射雕英雄传",5),
                                     new DemoBean("神雕侠侣1神雕侠侣2",7),
                                     new DemoBean("倚天屠龙记",9),
                                     new DemoBean("笑傲江湖",2),
                                     new DemoBean("连城诀",4),
                                     new DemoBean("天龙八部1天龙八部2",6));


        Collections.addAll(layerColor,0xFFFF0000,0xFF00FF00,0xFF0000FF,0xFFFFFF00,0xFF00FFFF,0xFFFF00FF,0xFFF0F0F0,0xFF0F0F0F);

        rv_radar.setRadarDataAdapter(new RadarAdapter() {
            @Override
            public String getVertexText(RadarView view, int position) {
                return mShowData.get(position).getText();
            }

            @Override
            public float getScore(RadarView view, int position) {
                return mShowData.get(position).getScore();
            }

            @Override
            public int getTotalCount() {
                return mShowData.size();
            }

            @Override
            public int getLayerColor(RadarView view, int position) {
                return layerColor.get(position);

            }
        });

    }
}
