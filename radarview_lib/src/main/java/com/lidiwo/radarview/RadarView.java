package com.lidiwo.radarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * *****************************************************
 *
 * @author：lidi
 * @date：2018/6/28 15:21
 * @Company：智能程序员
 * @Description： *****************************************************
 */
public class RadarView extends View {
    public static final int RADAR_MODE_POLYGON = 1;//多边形
    public static final int RADAR_MODE_CIRCLE = 2;//圆形

    private int mRadarLayer; //雷达层数
    private int mVertexTextColor; //雷达文字颜色
    private float mVertexTextSize; //雷达文字大小
    private float mVertexTextOffset; //定点文字距离最外层的偏移量
    private int mVertexTextEms; //几个文字开始换行
    private int mRadarMode;  //雷达模式 1.多边形 2.圆形  默认圆形
    private int mLayerLineColor;//雷达线的颜色
    private float mLayerLineWidth;//雷达线宽
    private boolean mLayerLineStyle;//雷达线样式 false 实线  true 虚线  默认是实线
    private float mLayerDottedLineSpacing; //如果是虚线时候，虚线间距
    private int mScoreShadeColor;//分数阴影的颜色

    private RadarAdapter mRadarAdapter;//数据适配器

    private Path mRadarPath;//数据分数多边形路径
    private Paint mRadarLinePaint;//雷达圆圈线画笔
    private Paint mLayerPaint;//雷达层画笔
    private Paint mSocorePaint;//雷达层分数画笔
    private TextPaint mVertexTextPaint;//顶脚文字的画笔

    private PointF mPointCenter;//控件的中心位置
    private float maxRadius;//记录圆最大半径
    private float maxLengthString = 0;//记录最长的字符串
    private float textHight = 0;//文字高度
    private float maxScore = 0;//记录最大分数值
    private List<PointF> textPointF = new ArrayList<>();

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarView);
        //解析自定义属性
        mRadarLayer = typedArray.getInt(R.styleable.RadarView_radar_layer, 5);
        mVertexTextColor = typedArray.getColor(R.styleable.RadarView_vertex_text_color, 0x00000000);
        mVertexTextSize = typedArray.getDimension(R.styleable.RadarView_vertex_text_size, sp2px(16));
        mVertexTextOffset = typedArray.getDimension(R.styleable.RadarView_vertex_text_offset, 0);
        mVertexTextEms = typedArray.getInt(R.styleable.RadarView_vertex_text_ems, 5);
        mRadarMode = typedArray.getInt(R.styleable.RadarView_radar_mode, RADAR_MODE_CIRCLE);
        mLayerLineColor = typedArray.getColor(R.styleable.RadarView_layer_line_color, 0x00000000);
        mLayerLineWidth = typedArray.getDimension(R.styleable.RadarView_layer_line_width, dp2px(1));
        mLayerLineStyle = typedArray.getBoolean(R.styleable.RadarView_layer_line_style, false);
        mLayerDottedLineSpacing = typedArray.getDimension(R.styleable.RadarView_layer_dotted_line_spacing, dp2px(5));

        mScoreShadeColor = typedArray.getColor(R.styleable.RadarView_score_shade_color, 0x00FF0000);

        typedArray.recycle();
    }

    private void init() {
        //初始化多边形路径
        mRadarPath = new Path();

        //初始化画笔
        mRadarLinePaint = new Paint();
        mLayerPaint = new Paint();
        mSocorePaint = new Paint();
        mVertexTextPaint = new TextPaint();

        //设置画笔抗锯齿
        mRadarLinePaint.setAntiAlias(true);
        mLayerPaint.setAntiAlias(true);
        mSocorePaint.setAntiAlias(true);
        mVertexTextPaint.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRadarAdapter != null) {
            //初始化画笔
            initPaint();
            //获取最大园的半径
            maxRadius();
            //绘制雷达
            drawRadar(canvas);
            //绘制数据
            drawData(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPointCenter = new PointF(w / 2, h / 2);
    }

    private void initPaint() {
        mRadarLinePaint.setStyle(Paint.Style.STROKE);
        mRadarLinePaint.setColor(mLayerLineColor);
        if (mLayerLineStyle) {
            mRadarLinePaint.setPathEffect(new DashPathEffect(new float[]{mLayerDottedLineSpacing, mLayerDottedLineSpacing}, 0));
        }

        mVertexTextPaint.setColor(mVertexTextColor);
        mVertexTextPaint.setTextSize(mVertexTextSize);
        mLayerPaint.setStyle(Paint.Style.FILL);
        mSocorePaint.setColor(mScoreShadeColor);

    }

    private void maxRadius() {
        int totalCount = mRadarAdapter.getTotalCount();
        if (totalCount == 0) {
            maxRadius = Math.min(mPointCenter.x, mPointCenter.y);
        } else {
            maxRadius = Math.min(mPointCenter.x, mPointCenter.y) - maxLengthString;
        }
    }


    private void drawRadar(Canvas canvas) {
        if (mRadarMode == RADAR_MODE_POLYGON) { //绘制多边形
            drawPolygon(canvas);
        } else if (mRadarMode == RADAR_MODE_CIRCLE) { //绘制圆形
            drawCircle(canvas);
        }
        //绘制数据线
        drawRadarLine(canvas);
    }

    private void drawPolygon(Canvas canvas) {
        if (mRadarAdapter != null && mRadarAdapter.getTotalCount() > 0) {
            float angleOffset = 360f / mRadarAdapter.getTotalCount();
            for (int i = mRadarLayer; i > 0; i--) {
                //计算每一层的半径 ,从最外层开始绘制
                float radius = (maxRadius - mVertexTextOffset) / mRadarLayer * i;
                //获取每一层的颜色
                int layerCorlor = mRadarAdapter.getLayerColor(this, i - 1);
                mLayerPaint.setColor(layerCorlor);

                mRadarPath.reset();
                for (int j = 0; j < mRadarAdapter.getTotalCount(); j++) {
                    float x = mPointCenter.x + radius * (float) Math.cos((angleOffset * j - 90) * Math.PI / 180f);
                    float y = mPointCenter.y + radius * (float) Math.sin((angleOffset * j - 90) * Math.PI / 180f);
                    if (j == 0) {
                        mRadarPath.moveTo(x, y);
                    } else {
                        mRadarPath.lineTo(x, y);
                    }
                }
                mRadarPath.close();
                if (mLayerLineWidth > 0) {
                    mRadarLinePaint.setStrokeWidth(mLayerLineWidth);
                    mRadarLinePaint.setStyle(Paint.Style.STROKE);
                    mRadarLinePaint.setStrokeWidth(mLayerLineWidth);
                    canvas.drawPath(mRadarPath, mRadarLinePaint);
                }
                mLayerPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(mRadarPath, mLayerPaint);
            }
        }
    }

    private void drawCircle(Canvas canvas) {
        for (int i = mRadarLayer; i > 0; i--) {
            //计算每一层的半径 ,从最外层开始绘制
            float radius = (maxRadius - mVertexTextOffset) / mRadarLayer * i;
            //获取每一层的颜色
            int layerCorlor = mRadarAdapter.getLayerColor(this, i - 1);
            mLayerPaint.setColor(layerCorlor);

            if (layerCorlor != Color.TRANSPARENT) {
                canvas.drawCircle(mPointCenter.x, mPointCenter.y, radius, mLayerPaint);
            }

            if (mLayerLineWidth > 0) {
                mRadarLinePaint.setStrokeWidth(mLayerLineWidth);
                canvas.drawCircle(mPointCenter.x, mPointCenter.y, radius, mRadarLinePaint);
            }
        }
    }

    private void drawRadarLine(Canvas canvas) {
        if (mRadarAdapter != null) {
            int totalCount = mRadarAdapter.getTotalCount();
            if (totalCount > 0) {
                float angleOffset = 360f / totalCount;
                //循环绘制交叉线，并且记录各项分数路径 、记录文字绘制位置
                mRadarPath.reset();
                textPointF.clear();

                for (int i = 0; i < totalCount; i++) {
                    float stopX = mPointCenter.x + (maxRadius - mVertexTextOffset) * (float) Math.cos((angleOffset * i - 90) * Math.PI / 180f);
                    float stopY = mPointCenter.y + (maxRadius - mVertexTextOffset) * (float) Math.sin((angleOffset * i - 90) * Math.PI / 180f);
                    canvas.drawLine(mPointCenter.x, mPointCenter.y, stopX, stopY, mRadarLinePaint);

                    float score = mRadarAdapter.getScore(this, i);
                    float x = mPointCenter.x + (maxRadius - mVertexTextOffset) * (score / maxScore) * (float) Math.cos((angleOffset * i - 90) * Math.PI / 180f);
                    float y = mPointCenter.y + (maxRadius - mVertexTextOffset) * (score / maxScore) * (float) Math.sin((angleOffset * i - 90) * Math.PI / 180f);
                    if (i == 0) {
                        mRadarPath.moveTo(x, y);
                    } else {
                        mRadarPath.lineTo(x, y);
                    }

                    String vertexText = mRadarAdapter.getVertexText(this, i);
                    float hight = (float) Math.ceil(vertexText.length() * 1.0f / mVertexTextEms) * textHight;
                    float pointX = mPointCenter.x + maxRadius * (float) Math.cos((angleOffset * i - 90) * Math.PI / 180f);
                    float pointY = mPointCenter.y + maxRadius * (float) Math.sin((angleOffset * i - 90) * Math.PI / 180f);

                    PointF pointF = coordinatesComputed(angleOffset * i - 90, pointX, pointY, hight);
                    textPointF.add(pointF);
                }
                mRadarPath.close();
            }
        }
    }

    // 计算文字坐标
    private PointF coordinatesComputed(float angle, float pointX, float pointY, float hight) {
        PointF pointF = new PointF();
        if (angle == -90) {
            pointF.x = pointX - maxLengthString / 2;
            pointF.y = pointY - hight;
        } else if (angle > -90 && angle < 0) {
            pointF.x = pointX;
            pointF.y = pointY - hight;
        } else if (angle == 0) {
            pointF.x = pointX;
            pointF.y = pointY - hight / 2;
        } else if (angle > 0 && angle < 90) {
            pointF.x = pointX;
            pointF.y = pointY;
        } else if (angle == 90) {
            pointF.x = pointX - maxLengthString / 2;
            pointF.y = pointY;
        } else if (angle > 90 && angle < 180) {
            pointF.x = pointX - maxLengthString;
            pointF.y = pointY;
        } else if (angle == 180) {
            pointF.x = pointX - maxLengthString;
            pointF.y = pointY - hight / 2;
        } else {
            pointF.x = pointX - maxLengthString;
            pointF.y = pointY - hight;
        }
        return pointF;
    }


    private void drawData(Canvas canvas) {
        //绘制各项分数多边形
        drawScore(canvas);
        //绘制顶角文字
        drawVertexText(canvas);
    }

    private void drawScore(Canvas canvas) {
        mSocorePaint.setAlpha(255);
        mSocorePaint.setStyle(Paint.Style.STROKE);
        mSocorePaint.setStrokeWidth(mLayerLineWidth);
        canvas.drawPath(mRadarPath, mSocorePaint);
        mSocorePaint.setStyle(Paint.Style.FILL);
        mSocorePaint.setAlpha(150);
        canvas.drawPath(mRadarPath, mSocorePaint);
    }


    private void drawVertexText(Canvas canvas) {
        if (mRadarAdapter != null && mRadarAdapter.getTotalCount() > 0) {
            for (int i = 0; i < mRadarAdapter.getTotalCount(); i++) {
                String vertexText = mRadarAdapter.getVertexText(this, i);
                if (!TextUtils.isEmpty(vertexText)) {
                    StaticLayout layout = new StaticLayout(vertexText, mVertexTextPaint, (int) Math.ceil(maxLengthString), Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
                    PointF pointF = textPointF.get(i);
                    canvas.save();
                    canvas.translate(pointF.x, pointF.y);
                    layout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    public void setRadarDataAdapter(RadarAdapter adapter) {
        this.mRadarAdapter = adapter;
        //获取最长文字、最大分数值
        initData(adapter);
        invalidate();
    }

    private void initData(RadarAdapter adapter) {
        if (adapter != null) {
            mVertexTextPaint.setTextSize(mVertexTextSize);

            int totalCount = mRadarAdapter.getTotalCount();
            if (totalCount == 0) {
                maxLengthString = mVertexTextPaint.measureText("");
                maxScore = mRadarLayer;//最大分数为雷达层数
            } else {
                for (int i = 0; i < totalCount; i++) {
                    String text = mRadarAdapter.getVertexText(this, i);
                    if (text != null) {
                        if (text.length() >= mVertexTextEms) {
                            float length = mVertexTextPaint.measureText(text, 0, mVertexTextEms);
                            if (length > maxLengthString) {
                                maxLengthString = length;
                            }
                        }

                        if (textHight == 0) {
                            Paint.FontMetrics fontMetrics = mVertexTextPaint.getFontMetrics();
                            textHight = fontMetrics.bottom - fontMetrics.top;
                        }

                    } else {
                        throw new NullPointerException();
                    }

                    float score = mRadarAdapter.getScore(this, i);
                    if (maxScore < score) {
                        maxScore = score;
                    }
                }
                if (maxLengthString == 0) {
                    maxLengthString = mVertexTextPaint.measureText("帝帝帝帝帝");
                }
            }
        }
    }

    private float dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    private float sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    /**
     * 设置雷达层数
     *
     * @param mRadarLayer
     */
    public void setRadarLayer(int mRadarLayer) {
        this.mRadarLayer = mRadarLayer;
        invalidate();
    }

    /**
     * 设置顶点文字颜色
     *
     * @param mVertexTextColor
     */
    public void setVertexTextColor(int mVertexTextColor) {
        this.mVertexTextColor = mVertexTextColor;
        invalidate();
    }

    /**
     * 设置顶点文字大小
     *
     * @param mVertexTextSize
     */
    public void setVertexTextSize(float mVertexTextSize) {
        this.mVertexTextSize = mVertexTextSize;
        initData(mRadarAdapter);
        invalidate();
    }

    /**
     * 设置顶点文字多少个字符换行
     *
     * @param mVertexTextEms
     */
    public void setVertexTextEms(int mVertexTextEms) {
        this.mVertexTextEms = mVertexTextEms;
        initData(mRadarAdapter);
        invalidate();
    }

    /**
     * 设置雷达模式 1 多边形 2圆形
     *
     * @param mRadarMode
     */
    public void setRadarMode(int mRadarMode) {
        this.mRadarMode = mRadarMode;
        invalidate();
    }

    /**
     * 设置每层颜色
     *
     * @param mLayerLineColor
     */
    public void setLayerLineColor(int mLayerLineColor) {
        this.mLayerLineColor = mLayerLineColor;
        invalidate();
    }

    /**
     * 设置绘制线宽
     *
     * @param mLayerLineWidth
     */
    public void setLayerLineWidth(float mLayerLineWidth) {
        this.mLayerLineWidth = mLayerLineWidth;
        invalidate();
    }

    /**
     * 设置绘制线的风格  false 实线  true 虚线  默认是实线
     *
     * @param mLayerLineStyle
     */
    public void setLayerLineStyle(boolean mLayerLineStyle) {
        this.mLayerLineStyle = mLayerLineStyle;
        invalidate();
    }

    /**
     * 设置绘制虚线时候的间距
     *
     * @param mLayerDottedLineSpacing
     */
    public void setLayerDottedLineSpacing(float mLayerDottedLineSpacing) {
        this.mLayerDottedLineSpacing = mLayerDottedLineSpacing;
        invalidate();
    }

    /**
     * 设置顶点文字距最外层的距离
     *
     * @param mVertexTextOffset
     */
    public void setVertexTextOffset(float mVertexTextOffset) {
        this.mVertexTextOffset = mVertexTextOffset;
        invalidate();
    }

    /**
     * 设置分数阴影颜色
     * @param mScoreShadeColor
     */

    public void setScoreShadeColor(int mScoreShadeColor) {
        this.mScoreShadeColor = mScoreShadeColor;
        invalidate();
    }
}
