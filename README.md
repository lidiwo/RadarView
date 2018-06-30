# RadarView
 [ ![Download](https://api.bintray.com/packages/lidiwo/lidiwo/RadarView/images/download.svg) ](https://bintray.com/lidiwo/lidiwo/RadarView/_latestVersion)

## 功能说明
* 支持对各层雷达网添加背景
* 支持自定义雷达网层数
* 雷达网支持圆形和多边形
* 扇区（顶点）个数无限制
* 所有的文字、雷达网、线、数据区颜色和大小均可定制
* 雷达网线支持虚线
* 顶点文字支持换行
![](https://github.com/lidiwo/RadarView/blob/master/logo.jpg?raw=true)


## 使用说明
### 1.gradle
```groovy
     compile 'com.lidiwo:RadarView:1.0.0'
```

### 2. xml
```xml
        <com.lidiwo.radarview.RadarView
            android:id="@+id/rv_radar"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:layer_dotted_line_spacing="5dp"
            app:layer_line_color="@color/colorPrimary"
            app:layer_line_style="true"
            app:layer_line_width="1dp"
            app:radar_mode="circle"
            app:vertex_text_color="@color/colorAccent"
            app:vertex_text_ems="5"
            app:vertex_text_offset="10dp"
            app:vertex_text_size="10sp" />
```

### 3. 属性说明

xml | code | 说明
---|---|---
app:radar_layer | setRadarLayer | 雷达网层数
app:vertex_text_color | setVertexTextColor | 顶角文字的颜色
app:vertex_text_size | setVertexTextSize |顶角文字的大小
app:vertex_text_ems | setVertexTextEms | 顶角文字几个字换行
app:vertex_text_offset | setVertexTextOffset | 设置顶点文字距离最外圈的距离
app:radar_mode | setRadarMode | 雷达模式
app:layer_line_color | setLayerLineColor | 雷达线颜色
app:layer_line_width | setLayerLineWidth | 雷达线宽
app:layer_line_style | setLayerLineStyle | 雷达线样式
app:layer_dotted_line_spacing | setLayerDottedLineSpacing | 雷达虚线间距
app:score_shade_color | setScoreShadeColor | 分数阴影的颜色

注：各属性均有默认值

### 4. 使用说明
```java
 rv_radar.setRadarDataAdapter(new RadarAdapter() {
            @Override
            public String getVertexText(RadarView view, int position) {
            //返回顶角文字
                return mShowData.get(position).getText();
            }

            @Override
            public float getScore(RadarView view, int position) {
            //返回每项文字对应的评分
                return mShowData.get(position).getScore();
            }

            @Override
            public int getTotalCount() {
            //返回总文字数
                return mShowData.size();
            }

            @Override
            public int getLayerColor(RadarView view, int position) {
            //返回每层雷达颜色
                return layerColor.get(position);
            }
        });
```

# LICENSE
```
Copyright 2018, lidiwo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```