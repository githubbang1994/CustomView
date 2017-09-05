package cn.dongha.ido.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.veryfit.multi.util.DebugLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import cn.dongha.ido.R;
import cn.dongha.ido.common.utils.AppSharedPreferencesUtils;
import cn.dongha.ido.common.utils.NumUtil;
import cn.dongha.ido.common.utils.ScreenUtils;
import cn.dongha.ido.common.utils.TimeUtil;
import cn.dongha.ido.common.utils.WeekUtil;
import cn.dongha.ido.ui.detail.PageTypeEnum;
import cn.dongha.ido.ui.detail.WeekMonthYearEnum;

/**
 * @author: sslong
 * @package: com.veryfit.
 * @description: ${TODO}{一句话描述该类的作用}
 * @date: 2016/5/30 09:45
 */
public class DetailChart extends View {
    // 上个页面传过来的数据
    private PageData pageData;
    private WeekMonthYearEnum weekMonthYear;
    private PageTypeEnum pageType;

    // 画笔
    private Paint testPaint, titlePaint, symbolPaint, circlePaint, linePaint, shaderPaint, onlinePaint, bottomPaint, selectPoint;
    private Paint kongPaint;
    private float kongPadding = 3;//空心
    private float realPading = 10;//实心

    // View的宽、高
    private int w, h;

    // 目标图片
    private Bitmap cup;
    private NinePatchDrawable popupLeft, popupRight, popupCenter, popup;

    // 点击选中的圆形区域
    private Rect selectRect;

    // 步数
    private int[] steps, sleeps, sleeps2, lessSleep, rates;
    private ArrayList<Dot> stepsDot, sleepsDot, sleeps2Dot, lessSleepDot, ratesDot;

    // 目标
    private String goal;

    // 最大心率值
    private int MAX_HEART_RATE;

    // 图例颜色
    private int sportCicleColor = 0xef5543;
    private int[] circleColor = new int[]{0xFF21EEF3, 0xFFFFFFFF};

    // X轴坐标
    private String[] xLables;

    // x轴坐标间距
    private float xLableSpace;

    // Y轴的坐标
    private int[] yScale = new int[5];
    // 依次是：标题、图例、图表、坐标的高度
    private int[] yHeight = new int[4];

    // 字体大小
    private float textSize;

    // 偏移量
    private int xOffset;

    private float touchX = 0;

    private int sportColor;

    private boolean isReDraw = false;

    public DetailChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DetailChart);

        textSize = a.getDimension(R.styleable.DetailChart_textSize, 17);

        a.recycle();

        // 目标奖杯
        cup = BitmapFactory.decodeResource(getResources(), R.drawable.cup);
        popupLeft = (NinePatchDrawable) getResources().getDrawable(R.drawable.popup_left);
        popupRight = (NinePatchDrawable) getResources().getDrawable(R.drawable.popup_right);
        popupCenter = (NinePatchDrawable) getResources().getDrawable(R.drawable.popup);
        selectRect = new Rect();

        // 画笔
        initPaint();

        sportColor = Color.WHITE;

    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        testPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        symbolPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        onlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        kongPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        xOffset = cup.getWidth();

        this.w = w;
        this.h = h;

        // 计算Y轴方向的坐标
        calculateY();

        // 计算高度,依次是：标题、图例、图表、坐标
        calculateHeight();

    }

    /**
     * 计算Y轴方向的坐标
     */
    private void calculateY() {
        yScale[0] = 0;                          // 图表竖坐标起点【标题上限】
        yScale[1] = yScale[0] + h / 12;         // 标题下限【图例上限】
        yScale[2] = yScale[1] + h / 12;         // 图例下限【图表上限】
        yScale[3] = yScale[2] + h * 9 / 12;     // 图表下限【坐标上限】
        yScale[4] = yScale[3] + h / 12;         // 坐标下限
    }

    /**
     * 计算高度,依次是：标题、图例、图表、坐标
     */
    private void calculateHeight() {
        yHeight[0] = yScale[1] - yScale[0];
        yHeight[1] = yScale[2] - yScale[1];
        yHeight[2] = yScale[3] - yScale[2];
        yHeight[3] = yScale[4] - yScale[3];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画坐标 高度占比：h / 10
        testPaint.setColor(Color.WHITE);
        testPaint.setStrokeWidth(1);

        // 根据weekMonthYear获取周、月、年的坐标值
        switch (weekMonthYear) {
            case WEEK:
                int week = TimeUtil.getDayOfCurrentWeek();
                xLables = new String[week];
                xLables = WeekUtil.getWeeksByWeekStartDay(getContext(), AppSharedPreferencesUtils.getInstance().getWeekStartIndex());
                break;
            case MONTH:
                // 方法一
//                int month = Integer.parseInt(pageData.title.substring(pageData.title.lastIndexOf("/") + 1));
                //方法二
                String[] yearMonths = pageData.allTitle.split("-");
                String[] yearMonth = yearMonths[0].split("/");
                int month = TimeUtil.getDaysByYearMonth(Integer.parseInt(yearMonth[0]), Integer.parseInt(yearMonth[1]));
                xLables = new String[month];
                for (int i = 0; i < month; i++) {
                    xLables[i] = (i + 1) + "";
                }
                break;
            case YEAR:
                int year = TimeUtil.getDayMonthOfCurrentYear();
                xLables = new String[year];
                for (int i = 0; i < year; i++) {
                    xLables[i] = (i + 1) + "";
                }
                break;
        }
        // 根据xLables获取坐标间距
        xLableSpace = (float) (w - 2 * xOffset) / (xLables.length - 1);

        // 根据周、月、年获取当前选中的值
        if (!isReDraw) {
            int index = WeekUtil.getOffsetIndex(weekMonthYear);
            touchX = xOffset + index * xLableSpace;
        }

        // 画标题 高度占比：h / 10
        drawTitle(canvas);

        // 画图例 高度占比：h / 10
        drawSymbol(canvas);

        // 画坐标值
        drawBottom(canvas);

        // 画图表 高度占比：h * 7 / 10
        testPaint.setAlpha(80);
        canvas.drawLine(0, yScale[3], w, yScale[3], testPaint);
        testPaint.setAlpha(255);
       drawChart(canvas);


    }

    /**
     * 获取圈圈半径
     *
     * @return
     */
    private float getCircleRadius() {
        float radius = 0;
        switch (weekMonthYear) {
            case WEEK:
                radius = 5 * xLableSpace / 40;
                break;
            case MONTH:
                radius = 20 * xLableSpace / 40;
                break;
            case YEAR:
                radius = 10 * xLableSpace / 40;
                break;
        }
        return radius;
    }

    /**
     * 画选中的圈圈
     *
     * @param canvas
     */
    private void drawSelectCircle(Canvas canvas, float[] dotX, float[] dotY, String tips, float touchX, int[]... data) {
        selectPoint.setStrokeWidth(2);
        selectPoint.setTextAlign(Paint.Align.CENTER);
        selectPoint.setStyle(Paint.Style.STROKE);
        switch (tips) {
            case "SPORT":
                selectPoint.setColor(sportColor);
                break;
            case "SLEEP":
                selectPoint.setColor(Color.WHITE);
                break;
            default:
                selectPoint.setColor(Color.WHITE);
                break;
        }
        for (int i = 0; i < dotX.length; i++) {
            if (touchX <= (10 * dotX[i] / 10) || touchX <= (13 * dotX[0] / 10)) {
                float radius = getCircleRadius();
                // 画圈圈
                if ((data[0])[i]==0)return;
                canvas.drawCircle(dotX[i], dotY[i], radius, selectPoint);

                // 设置popup范围
//                canvas.restore();

                float minValue;

                switch (tips) {
                    case "SLEEP":
                        if (i <= 1) {
                            minValue = dotX[i];
                        } else {
                            minValue = dotX[1];
                        }
//                        if (touchX <= (13 * minValue / 10)) {
//                            popup = popupLeft;
//                            selectRect.left = (int) dotX[i];
//                            selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
//                            selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth();
//                            selectRect.bottom = (int) dotY[i] - (int) radius;
//                        } else if (touchX > (13 * minValue / 10) && touchX < (11 * dotX[dotX.length - 3] / 10)) {
//                            popup = popupCenter;
//                            selectRect.left = (int) dotX[i] - (int) radius - popup.getIntrinsicWidth() / 2 - (int) radius;
//                            selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
//                            selectRect.right = (int) dotX[i] - (int) radius + popup.getIntrinsicWidth() - (int) radius;
//                            selectRect.bottom = (int) dotY[i] - (int) radius;
//
//                        } else {
//                            popup = popupRight;
//                            selectRect.left = (int) dotX[i] - popup.getIntrinsicWidth();
//                            selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
//                            selectRect.right = (int) dotX[i];
//                            selectRect.bottom = (int) dotY[i] - (int) radius;
//                        }

                        if (touchX <= w/2) {
                            popup = popupLeft;
                            selectRect.left = (int) dotX[i];
                            selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
                            selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth();
                            selectRect.bottom = (int) dotY[i] - (int) radius;
                        } /*else if (touchX > (13 * minValue / 10) && touchX < (11 * dotX[dotX.length - 3] / 10)) {
                            popup = popupCenter;
                            selectRect.left = (int) dotX[i] - (int) radius - popup.getIntrinsicWidth() / 2 - (int) radius;
                            selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
                            selectRect.right = (int) dotX[i] - (int) radius + popup.getIntrinsicWidth() - (int) radius;
                            selectRect.bottom = (int) dotY[i] - (int) radius;

                        }*/ else {
                            popup = popupRight;
                            selectRect.left = (int) dotX[i] - popup.getIntrinsicWidth();
                            selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
                            selectRect.right = (int) dotX[i];
                            selectRect.bottom = (int) dotY[i] - (int) radius;
                        }
                        break;
                    default:
                        if (touchX <= (13 * dotX[0] / 10)) {
                            popup = popupLeft;
                            selectRect.left = (int) dotX[i];
                            selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
                            selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth();
                            selectRect.bottom = (int) dotY[i] - (int) radius;
                        } else if (touchX > (13 * dotX[0] / 10) && touchX < (11 * dotX[dotX.length - 2] / 10)) {
                            popup = popupCenter;
                            selectRect.left = (int) dotX[i] - (int) radius - popup.getIntrinsicWidth() / 2 - (int) radius;
                            selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
                            selectRect.right = (int) dotX[i] - (int) radius + popup.getIntrinsicWidth() - (int) radius;
                            selectRect.bottom = (int) dotY[i] - (int) radius;
                        } else {
                            popup = popupRight;
                            int left=(int) dotX[i] - popup.getIntrinsicWidth();
                            if (left<0){
                                popup = popupLeft;
                                selectRect.left = (int) dotX[i];
                                selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
                                selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth();
                                selectRect.bottom = (int) dotY[i] - (int) radius;
                            }else{
                                selectRect.left = (int) dotX[i] - popup.getIntrinsicWidth();
                                selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
                                selectRect.right = (int) dotX[i];
                                selectRect.bottom = (int) dotY[i] - (int) radius;
                            }

                        }
                        break;
                }

                DebugLog.d("dotY=" + (int) dotY[i] + ",yScale=" + yScale[4]);
                // 画字体
                selectPoint.setTextAlign(Paint.Align.CENTER);
                selectPoint.setTextSize(textSize * 1.5f);
                selectPoint.setStyle(Paint.Style.FILL);
                selectPoint.setColor(Color.WHITE);
                float centHeight = selectPoint.ascent() + selectPoint.descent();
                float totalSleepW = 0;
                float deepSleepW = 0;
                float totalSleepH = 0;
                float deepSleepH = 0;
                float lessSleepH = 0;
                if (data != null && data.length > 0) {
                    if (data.length == 1) {
                        String unit = "";
                        if (pageType == PageTypeEnum.SPORT) {
                            unit = getResources().getString(R.string.unit_step);
                        } else if (pageType == PageTypeEnum.HEARTRATE) {
                            unit = getResources().getString(R.string.bmp);
                        }
                        canvas.drawText((data[0])[i] + " " + unit, selectRect.left + (selectRect.right - selectRect.left) / 2, selectRect.top + (selectRect.bottom - selectRect.top) / 2 - centHeight / 3, selectPoint);
                    } else if (data.length == 3) {
                        String hourUnit = getResources().getString(R.string.unit_hour_zh);
                        String minuteUnit = getResources().getString(R.string.unit_minute_zh);
                        String totalSleepStr = getResources().getString(R.string.detail_totalSleepLable, (data[0])[i] / 60 + " " + hourUnit + (data[0])[i] % 60 + " " + minuteUnit);
                        String deepSleepStr = getResources().getString(R.string.detail_deepSleepLable, (data[1])[i] / 60 + " " + hourUnit + (data[1])[i] % 60 + " " + minuteUnit);
                        String lessSleepStr = getResources().getString(R.string.detail_lightSleepLable, (data[2])[i] / 60 + " " + hourUnit + (data[2])[i] % 60 + " " + minuteUnit);
                        totalSleepW = ViewUtil.getTextRectWidth(selectPoint, totalSleepStr);
                        deepSleepW = ViewUtil.getTextRectWidth(selectPoint, deepSleepStr);
                        totalSleepH = ViewUtil.getTextRectHeight(selectPoint, totalSleepStr);
                        deepSleepH = ViewUtil.getTextRectHeight(selectPoint, deepSleepStr);
                        lessSleepH = ViewUtil.getTextRectHeight(selectPoint, lessSleepStr);

                        // 修改selectRect属性
                        //这里做一个判断 如果坐标值太低的话会遮挡上面的文字，所以 到达一定高度让他在选中的点下方显示
                        if ((int) dotY[i] < yScale[3] * 0.36) {
                            selectRect.top = (int) dotY[i] + (int) radius;
                        } else {
                            selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) totalSleepH - (int) deepSleepH - (int) lessSleepH;
                        }

                        if ((int) dotY[i] < yScale[3] * 0.36) {
                            selectRect.bottom = (int) dotY[i] + popup.getIntrinsicHeight() + (int) totalSleepH + (int) deepSleepH + (int) lessSleepH;
                        } else {
                            selectRect.bottom = (int) dotY[i] - (int) radius;
                        }
                        int addW = (int) (totalSleepW > deepSleepW ? totalSleepW : deepSleepW);


                        switch (tips) {
                            case "SLEEP":
                                if (i <= 1) {
                                    minValue = dotX[i];
                                } else {
                                    minValue = dotX[1];
                                }
//                                if (touchX <= (13 * minValue / 10)) {
//                                    selectRect.left = (int) dotX[i] - (int) radius;
//                                    selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth() + addW - (int) radius;
//                                } else if (touchX > (13 * minValue / 10) && touchX < (11 * dotX[dotX.length - 3] / 10)) {
//                                    selectRect.left = (int) dotX[i] - addW / 2 - (int) radius * 2 - (int) radius - (int) radius;
//                                    selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth() + addW / 2 - (int) radius * 2 - (int) radius;
//                                } else {
//                                    selectRect.left = (int) dotX[i] - popup.getIntrinsicWidth() - addW - (int) radius;
//                                    selectRect.right = (int) dotX[i] - (int) radius;
//                                }
                                if (touchX <= w/2) {
                                    selectRect.left = (int) dotX[i] - (int) radius;
                                    selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth() + addW - (int) radius;
                                } /*else if (touchX > (13 * minValue / 10) && touchX < (11 * dotX[dotX.length - 3] / 10)) {
                                    selectRect.left = (int) dotX[i] - addW / 2 - (int) radius * 2 - (int) radius - (int) radius;
                                    selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth() + addW / 2 - (int) radius * 2 - (int) radius;
                                }*/ else {
                                    selectRect.left = (int) dotX[i] - popup.getIntrinsicWidth() - addW - (int) radius;
                                    selectRect.right = (int) dotX[i] - (int) radius;
                                }
                                break;
                            default:
                                if (touchX <= (13 * dotX[0] / 10)) {
                                    selectRect.left = (int) dotX[i] - (int) radius;
                                    selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth() + addW - (int) radius;
                                } else if (touchX > (13 * dotX[0] / 10) && touchX < (11 * dotX[dotX.length - 2] / 10)) {
                                    selectRect.left = (int) dotX[i] - addW / 2 - (int) radius * 2 - (int) radius - (int) radius;
                                    selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth() + addW / 2 - (int) radius * 2 - (int) radius;
                                } else {
                                    selectRect.left = (int) dotX[i] - popup.getIntrinsicWidth() - addW - (int) radius;
                                    selectRect.right = (int) dotX[i] - (int) radius;
                                }
                                break;
                        }

                        int selectRectHeight = (selectRect.bottom - selectRect.top) / 2;
                        float selectCentHeight = selectPoint.ascent() + selectPoint.descent();
                        DebugLog.d("x=" + selectRect.left + (selectRect.right - selectRect.left) / 2 + "    y=" + (selectRect.top + selectRectHeight / 3 + totalSleepH + selectCentHeight / 2 - ScreenUtils.dp2px(2)));
                        canvas.drawText(totalSleepStr, selectRect.left + (selectRect.right - selectRect.left) / 2,
                                selectRect.top + selectRectHeight / 3 + totalSleepH + selectCentHeight / 2 - ScreenUtils.dp2px(2), selectPoint);
                        canvas.drawText(deepSleepStr, selectRect.left + (selectRect.right - selectRect.left) / 2,
                                selectRect.top + selectRectHeight / 3 + totalSleepH + deepSleepH + selectCentHeight / 2 + ScreenUtils.dp2px(2)
                                , selectPoint);
                        canvas.drawText(lessSleepStr, selectRect.left + (selectRect.right - selectRect.left) / 2,
                                selectRect.top + selectRectHeight / 3 + totalSleepH + deepSleepH + lessSleepH + selectCentHeight / 2 + ScreenUtils.dp2px(6)
                                , selectPoint);

                    }
                }

                popup.setBounds(selectRect);
                popup.draw(canvas);
                break;
            }
        }
    }


    /**
     * 画选中的圈圈
     *
     * @param canvas
     */
    private void drawSelectCircle(Canvas canvas, float[] dotX, float[] dotY, String tips, int[]... data) {
        selectPoint.setStrokeWidth(2);
        selectPoint.setTextAlign(Paint.Align.CENTER);
        selectPoint.setStyle(Paint.Style.STROKE);
        switch (tips) {
            case "SPORT":
                selectPoint.setColor(sportColor);
                break;
            case "SLEEP":
                selectPoint.setColor(Color.WHITE);
                break;
            default:
                selectPoint.setColor(Color.WHITE);
                break;
        }
        for (int i = 0; i < dotX.length; i++) {
            if (touchX <= (11 * dotX[i] / 10) || touchX <= (13 * dotX[0] / 10)) {
                float radius = getCircleRadius();
                // 画圈圈
                canvas.drawCircle(dotX[i], dotY[i], radius, selectPoint);

                // 设置popup范围
//                canvas.restore();
                if (touchX <= (13 * dotX[0] / 10)) {
                    popup = popupLeft;
                    selectRect.left = (int) dotX[i];
                    selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
                    selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth();
                    selectRect.bottom = (int) dotY[i] - (int) radius;
                } else if (touchX > (13 * dotX[0] / 10) && touchX < (11 * dotX[dotX.length - 2] / 10)) {
                    popup = popupCenter;
                    selectRect.left = (int) dotX[i] - (int) radius - popup.getIntrinsicWidth() / 2 - (int) radius;
                    selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
                    selectRect.right = (int) dotX[i] - (int) radius + popup.getIntrinsicWidth() - (int) radius;
                    selectRect.bottom = (int) dotY[i] - (int) radius;
                } else {
                    popup = popupRight;
                    selectRect.left = (int) dotX[i] - popup.getIntrinsicWidth();
                    selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) radius;
                    selectRect.right = (int) dotX[i];
                    selectRect.bottom = (int) dotY[i] - (int) radius;
                }

                // 画字体
                selectPoint.setTextAlign(Paint.Align.CENTER);
                selectPoint.setTextSize(textSize * 1.5f);
                selectPoint.setStyle(Paint.Style.FILL);
                selectPoint.setColor(Color.WHITE);
                float centHeight = selectPoint.ascent() + selectPoint.descent();
                float totalSleepW = 0;
                float deepSleepW = 0;
                float totalSleepH = 0;
                float deepSleepH = 0;
                if (data != null && data.length > 0) {
                    if (data.length == 1) {
                        String unit = "";
                        if (pageType == PageTypeEnum.SPORT) {
                            unit = getResources().getString(R.string.unit_step);
                        } else if (pageType == PageTypeEnum.HEARTRATE) {
                            unit = getResources().getString(R.string.bmp);
                        }
                        canvas.drawText((data[0])[i] + " " + unit, selectRect.left + (selectRect.right - selectRect.left) / 2, selectRect.top + (selectRect.bottom - selectRect.top) / 2 - centHeight / 3, selectPoint);
                    } else if (data.length == 2) {
                        String hourUnit = getResources().getString(R.string.unit_hour_zh);
                        String minuteUnit = getResources().getString(R.string.unit_minute_zh);
                        String totalSleepStr = getResources().getString(R.string.detail_totalSleep) + "：" + (data[0])[i] / 60 + " " + hourUnit + (data[0])[i] % 60 + " " + minuteUnit;
                        String deepSleepStr = getResources().getString(R.string.detail_deepSleep) + "：" + (data[1])[i] / 60 + " " + hourUnit + (data[1])[i] % 60 + " " + minuteUnit;
                        totalSleepW = ViewUtil.getTextRectWidth(selectPoint, totalSleepStr);
                        deepSleepW = ViewUtil.getTextRectWidth(selectPoint, deepSleepStr);
                        totalSleepH = ViewUtil.getTextRectHeight(selectPoint, totalSleepStr);
                        deepSleepH = ViewUtil.getTextRectHeight(selectPoint, deepSleepStr);

                        // 修改selectRect属性
                        selectRect.top = (int) dotY[i] - popup.getIntrinsicHeight() - (int) totalSleepH - (int) deepSleepH;
                        int addW = (int) (totalSleepW > deepSleepW ? totalSleepW : deepSleepW);
                        if (touchX <= (13 * dotX[0] / 10)) {
                            selectRect.left = (int) dotX[i] - (int) radius;
                            selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth() + addW - (int) radius;
                        } else if (touchX > (13 * dotX[0] / 10) && touchX < (11 * dotX[dotX.length - 2] / 10)) {
                            selectRect.left = (int) dotX[i] - addW / 2 - (int) radius * 2 - (int) radius - (int) radius;
                            selectRect.right = (int) dotX[i] + popup.getIntrinsicWidth() + addW / 2 - (int) radius * 2 - (int) radius;
                        } else {
                            selectRect.left = (int) dotX[i] - popup.getIntrinsicWidth() - addW - (int) radius;
                            selectRect.right = (int) dotX[i] - (int) radius;
                        }

                        int selectRectHeight = (selectRect.bottom - selectRect.top) / 2;
                        float selectCentHeight = selectPoint.ascent() + selectPoint.descent();

                        canvas.drawText(totalSleepStr, selectRect.left + (selectRect.right - selectRect.left) / 2,
                                selectRect.top + selectRectHeight / 2 + totalSleepH + selectCentHeight / 2 - ScreenUtils.dp2px(2), selectPoint);
                        canvas.drawText(deepSleepStr, selectRect.left + (selectRect.right - selectRect.left) / 2,
                                selectRect.top + selectRectHeight / 2 + totalSleepH + deepSleepH + selectCentHeight / 2 + ScreenUtils.dp2px(2)
                                , selectPoint);

                    }
                }

                popup.setBounds(selectRect);
                popup.draw(canvas);
                break;
            }
        }
    }

    /**
     * 画标题
     *
     * @param canvas
     */
    private void drawTitle(Canvas canvas) {
        String title = pageData.title;
        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(textSize * 1.5f);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        float titleHeight = ViewUtil.getTextRectHeight(titlePaint, title);
//        canvas.drawText(title, w / 2, yScale[1] - (yHeight[0] - titleHeight) / 2, titlePaint);
    }

    /**
     * 画图例
     *
     * @param canvas
     */
    private void drawSymbol(Canvas canvas) {
        String[] symbol = pageData.symbols;
        symbolPaint.setColor(Color.WHITE);
        symbolPaint.setTextAlign(Paint.Align.RIGHT);
        symbolPaint.setTextSize(textSize * 1.5f);
        float rOffset = 0;
        for (int i = 0; i < symbol.length; i++) {
            float symbolHeight = ViewUtil.getTextRectHeight(symbolPaint, symbol[i]);
            if (i == 0) {
                rOffset = w - xOffset;
            } else {
                rOffset = rOffset - ScreenUtils.dp2px(12);
            }
//            canvas.drawText(symbol[symbol.length - 1 - i], rOffset, yScale[2] - (yHeight[1] - symbolHeight) / 2, symbolPaint);
            float symbolWidth = ViewUtil.getTextRectWidth(symbolPaint, symbol[i]);
            if (symbol.length > 1) {
                if (i == 0) {
                    circlePaint.setColor(circleColor[1]);
                } else {
                    circlePaint.setColor(circleColor[0]);
                }
            } else {
                circlePaint.setColor(circleColor[1]);
            }
            circlePaint.setTextAlign(Paint.Align.CENTER);
            rOffset = rOffset - symbolWidth - ScreenUtils.dp2px(5);
            int circleRadius = ScreenUtils.dp2px(3);
//            canvas.drawCircle(rOffset, yScale[2] - (yHeight[1] - circleRadius) / 2, circleRadius, circlePaint);
        }
    }

    /**
     * 画奖杯
     *
     * @param canvas
     * @param goalValue
     * @param space
     * @param goalStr
     */
    private void drawCup(Canvas canvas, int goalValue, float space, String goalStr) {
        /**
         * 画奖杯、目标、虚线
         * 这边需要判断是否是年数据，如果不是年数据，则画目标等等，否则，不画
         */
        if (weekMonthYear != WeekMonthYearEnum.YEAR) {
            // 画目标奖杯
//            canvas.drawBitmap(cup, 0, yScale[3] - (int) (goalValue * space) - cup.getHeight(), linePaint);

            /**
             * 区分是运动还是睡眠
             */
            linePaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(goalStr, cup.getWidth() + ScreenUtils.dp2px(3),
                    yScale[3] - (int) (goalValue * space) - (cup.getHeight() - ViewUtil.getTextRectHeight(linePaint, goalStr)) / 2, linePaint);

            // 画虚线
            int lineWidth = ScreenUtils.dp2px(5);
            int lineNum = w / lineWidth;
            for (int i = 0; i < lineNum; i++) {
                canvas.drawLine(lineWidth * i, yScale[3] - (int) (goalValue * space) + ScreenUtils.dp2px(1), lineWidth * i + lineWidth / 2, yScale[3] - (int) (goalValue * space) + ScreenUtils.dp2px(1), linePaint);
            }
        }
    }

    /**
     * 画图表
     *
     * @param canvas
     */
    private void drawChart(Canvas canvas) {
        if (pageType == PageTypeEnum.SPORT) {
            goal = pageData.goal + getResources().getString(R.string.step_str);
        } else if (pageType == PageTypeEnum.SLEEP) {
            goal = NumUtil.isEmptyInt(pageData.goal) / 60 + getResources().getString(R.string.unit_hour_zh) + NumUtil.isEmptyInt(pageData.goal) % 60 + getResources().getString(R.string.unit_minute_zh);
        }
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(1);
        linePaint.setTextSize(textSize * 1.3f);

        /**
         * 画竖线：【这里要区分活动、睡眠和心率】
         * 长竖线：活动和睡眠
         * 短竖线：心率
         */
        linePaint.setAlpha(80);
        for (int i = 0; i < xLables.length; i++) {
            if (pageType != PageTypeEnum.HEARTRATE) {
                // 画活动、睡眠的竖线
                if (weekMonthYear != WeekMonthYearEnum.YEAR) {
                    canvas.drawLine(xOffset + i * xLableSpace, yScale[3], xOffset + i * xLableSpace, yScale[2] + cup.getWidth() / 2, linePaint);
                } else {
                    canvas.drawLine(xOffset + i * xLableSpace, yScale[3], xOffset + i * xLableSpace, yScale[2] + cup.getWidth(), linePaint);
                }
            } else {
                // 画心率竖线
                canvas.drawLine(xOffset + i * xLableSpace, yScale[3], xOffset + i * xLableSpace, yScale[3] - ScreenUtils.dp2px(10), linePaint);
            }
        }
        linePaint.setAlpha(255);

        /**
         * 这边判断是否是心率页面,如果是，则画横线；否则，画竖线
         */
        if (pageType != PageTypeEnum.HEARTRATE) {

            /**
             * 描点画线
             * 说明：需判断是运动还是睡眠
             */
            int dotHeight = yScale[3] - (yScale[2] + cup.getHeight() + ScreenUtils.dp2px(1));
            float stepSpace = 0;
            float sleepSpace = 0;
            int goalSleep = 0;
            int goalStep = 0;
            onlinePaint.setColor(sportColor);
            onlinePaint.setStrokeWidth(3);

            /**
             *   ----  运动   ----
             *   ----  运动   ----
             *   ----  运动   ----
             */

            if (pageType == PageTypeEnum.SPORT) {

                if (weekMonthYear == WeekMonthYearEnum.YEAR && steps != null && steps.length > 0) {
                    for (int i = 0; i < steps.length; i++) {
                        if (steps[i] > goalStep) {
                            goalStep = steps[i];
                        }
                    }
                } else {
                    goalStep = NumUtil.isEmptyInt(pageData.goal);
                }

                stepsDot = pageData.dots.get(0);
                if (stepsDot != null && stepsDot.size() > 0) {
                    steps = new int[stepsDot.size()];
                    for (int i = 0; i < stepsDot.size(); i++) {
                        steps[i] = stepsDot.get(i).data;
                    }
                } else {
                    steps = new int[xLables.length];
                }
                // 最大值
                int maxDotValue = 0;
                if (steps != null && steps.length > 0) {
                    for (int i = 0; i < steps.length; i++) {
                        if (steps[i] > maxDotValue) {
                            maxDotValue = steps[i];
                        }
                    }
                }
                maxDotValue = maxDotValue > goalStep ? maxDotValue : goalStep;
                // 计算刻度
                stepSpace = (float) dotHeight / (float) maxDotValue;
                DebugLog.d("目标步数的每步的间距：" + stepSpace);

                /**
                 * 画奖杯
                 */
                drawCup(canvas, goalStep, stepSpace, goal);

                /**
                 * 先画线
                 */
                for (int i = 0; i < steps.length; i++) {
                    if (stepsDot != null && stepsDot.size() > 0) {
                        if (weekMonthYear == WeekMonthYearEnum.YEAR) {
                            if (isOverCurrentDateYear(stepsDot.get(i).date)) {
                                break;
                            }
                        } else {
                            if (isOverCurrentDate(stepsDot.get(i).date)) {
                                break;
                            }
                        }
                    } else {
                        if (weekMonthYear == WeekMonthYearEnum.WEEK) {
                            int dayOfWeek = TimeUtil.getDayOfWeek(new Date());
                            int index = AppSharedPreferencesUtils.getInstance().getWeekStartIndex();
                            if (i > (dayOfWeek - index)) {
                                break;
                            }
                        } else if (weekMonthYear == WeekMonthYearEnum.MONTH) {
                            int dayOfMonth = TimeUtil.getDayOfMonth();
                            if (i >= dayOfMonth) {
                                break;
                            }
                        } else {
                            int currentMonth = TimeUtil.getCurrentMonth();
                            if (i > currentMonth) {
                                break;
                            }
                        }
                    }

                    int step = steps[i];
                    if (i != 0) {
                        int preStep = steps[i - 1];
                        canvas.drawLine(xOffset + (i - 1) * xLableSpace, yScale[3] - (int) (preStep * stepSpace),
                                xOffset + i * xLableSpace, yScale[3] - (int) (step * stepSpace),
                                onlinePaint);
                    }
                }

                /**
                 * 再描点
                 */
                float[] dotX = new float[steps.length];
                float[] dotY = new float[steps.length];
                linePaint.setColor(sportColor);
                for (int i = 0; i < steps.length; i++) {
                    if (stepsDot != null && stepsDot.size() > 0) {
                        if (weekMonthYear == WeekMonthYearEnum.YEAR) {
                            if (isOverCurrentDateYear(stepsDot.get(i).date)) {
                                break;
                            }
                        } else {
                            if (isOverCurrentDate(stepsDot.get(i).date)) {
                                break;
                            }
                        }
                    } else {
                        if (weekMonthYear == WeekMonthYearEnum.WEEK) {
                            int dayOfWeek = TimeUtil.getDayOfWeek(new Date());
                            int index = AppSharedPreferencesUtils.getInstance().getWeekStartIndex();
                            if (i > (dayOfWeek - index)) {
                                break;
                            }
                        } else if (weekMonthYear == WeekMonthYearEnum.MONTH) {
                            int dayOfMonth = TimeUtil.getDayOfMonth();
                            if (i >= dayOfMonth) {
                                break;
                            }
                        } else {
                            int currentMonth = TimeUtil.getCurrentMonth();
                            if (i > currentMonth) {
                                break;
                            }
                        }
                    }

                    int step = steps[i];
                    dotX[i] = xOffset + i * xLableSpace;
                    dotY[i] = yScale[3] - (int) (step * stepSpace);

                    // 等于0的话空心
                    if (step == 0) {
                        kongPaint.setColor(sportColor);
                        kongPaint.setStyle(Paint.Style.FILL);
                        kongPaint.setStrokeWidth(3);
                        canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (step * stepSpace), realPading , kongPaint);
                        //canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (step * stepSpace), realPading - kongPadding, kongPaint);//以前的那个把
                        kongPaint.setColor(Color.TRANSPARENT);
                        canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (step * stepSpace), realPading, kongPaint);
                    } else {
                        canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (step * stepSpace), realPading, linePaint);
                    }

                }

                // 画选中的圈圈
                drawSelectCircle(canvas, dotX, dotY, "SPORT", touchX, steps);

            }

            /**
             *   ----  睡眠   ----
             *   ----  睡眠   ----
             *   ----  睡眠   ----
             */

            else if (pageType == PageTypeEnum.SLEEP) {
                if (weekMonthYear == WeekMonthYearEnum.YEAR && sleeps != null && sleeps.length > 0) {
                    for (int i = 0; i < sleeps.length; i++) {
                        if (sleeps[i] > goalSleep) {
                            goalSleep = sleeps[i];
                        }
                    }
                } else {
                    goalSleep = NumUtil.isEmptyInt(pageData.goal);
                }

                sleepsDot = pageData.dots.get(0);
                if (sleepsDot != null && sleepsDot.size() > 0) {
                    DebugLog.d("先画线再描点---总睡眠=" + (sleepsDot.size()));
                    sleeps = new int[sleepsDot.size()];
                    for (int i = 0; i < sleepsDot.size(); i++) {
                        sleeps[i] = sleepsDot.get(i).data;
                    }
                } else {
                    sleeps = new int[xLables.length];
                }
                DebugLog.d("先画线再描点---睡眠=" + sleeps.length + ",xLables.length=" + xLables.length);
                // 计算最大值
                int maxDotValue = 0;
                if (sleeps != null && sleeps.length > 0) {
                    for (int i = 0; i < sleeps.length; i++) {
                        if (sleeps[i] > maxDotValue) {
                            maxDotValue = sleeps[i];
                        }
                    }
                }
                maxDotValue = maxDotValue > goalSleep ? maxDotValue : goalSleep;
                // 计算刻度
                sleepSpace = (float) dotHeight / (float) maxDotValue;
                DebugLog.d("目标睡眠的单位时间：" + sleepSpace);

                /**
                 * 画奖杯
                 */
                drawCup(canvas, goalSleep, sleepSpace, goal);

                /**
                 * 先画线
                 */
                onlinePaint.setColor(circleColor[1]);
                for (int i = 0; i < sleeps.length; i++) {
                    if (sleepsDot != null && sleepsDot.size() > 0) {
                        if (weekMonthYear == WeekMonthYearEnum.YEAR) {
                            if (isOverCurrentDateYear(sleepsDot.get(i).date)) {
                                break;
                            }
                        } else {
                            if (isOverCurrentDate(sleepsDot.get(i).date)) {
                                break;
                            }
                        }
                    } else {
                        if (weekMonthYear == WeekMonthYearEnum.WEEK) {
                            int dayOfWeek = TimeUtil.getDayOfWeek(new Date());
                            int index = AppSharedPreferencesUtils.getInstance().getWeekStartIndex();
                            if (i > (dayOfWeek - index)) {
                                break;
                            }
                        } else if (weekMonthYear == WeekMonthYearEnum.MONTH) {
                            int dayOfMonth = TimeUtil.getDayOfMonth();
                            if (i >= dayOfMonth) {
                                break;
                            }
                        } else {
                            int currentMonth = TimeUtil.getCurrentMonth();
                            if (i > currentMonth) {
                                break;
                            }
                        }
                    }

                    int sleepTime = sleeps[i];
                    DebugLog.d("目标睡眠 sleepTime=" + sleepTime);
                    if (i != 0) {
                        int preSleep = sleeps[i - 1];
                        canvas.drawLine(xOffset + (i - 1) * xLableSpace, yScale[3] - (int) (preSleep * sleepSpace),
                                xOffset + i * xLableSpace, yScale[3] - (int) (sleepTime * sleepSpace),
                                onlinePaint);
                    }
                }
                /**
                 * 再描点
                 */
                float[] dotX = new float[sleeps.length];
                float[] dotY = new float[sleeps.length];
                for (int i = 0; i < sleeps.length; i++) {
                    if (sleepsDot != null && sleepsDot.size() > 0) {
                        if (weekMonthYear == WeekMonthYearEnum.YEAR) {
                            if (isOverCurrentDateYear(sleepsDot.get(i).date)) {
                                break;
                            }
                        } else {
                            if (isOverCurrentDate(sleepsDot.get(i).date)) {
                                break;
                            }
                        }
                    } else {
                        if (weekMonthYear == WeekMonthYearEnum.WEEK) {
                            int dayOfWeek = TimeUtil.getDayOfWeek(new Date());
                            int index = AppSharedPreferencesUtils.getInstance().getWeekStartIndex();
                            if (i > (dayOfWeek - index)) {
                                break;
                            }
                        } else if (weekMonthYear == WeekMonthYearEnum.MONTH) {
                            int dayOfMonth = TimeUtil.getDayOfMonth();
                            if (i >= dayOfMonth) {
                                break;
                            }
                        } else {
                            int currentMonth = TimeUtil.getCurrentMonth();
                            if (i > currentMonth) {
                                break;
                            }
                        }
                    }

                    int sleepTime = sleeps[i];
                    dotX[i] = xOffset + i * xLableSpace;
                    dotY[i] = yScale[3] - (int) (sleepTime * sleepSpace);

                    // 等于0的话空心
                    if (sleepTime == 0) {
                        kongPaint.setColor(circleColor[1]);
                        kongPaint.setStyle(Paint.Style.FILL);
                        kongPaint.setStrokeWidth(3);
                    //    canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (sleepTime * sleepSpace), realPading - kongPadding, kongPaint);
                        canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (sleepTime * sleepSpace), realPading, kongPaint);
                        kongPaint.setColor(Color.TRANSPARENT);
                        canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (sleepTime * sleepSpace), realPading, kongPaint);
                    } else {
                        canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (sleepTime * sleepSpace), realPading, onlinePaint);
                    }

                }

                /**
                 * 先画线
                 */
                onlinePaint.setColor(circleColor[0]);
                sleeps2Dot = pageData.dots.get(1);
                if (sleeps2Dot != null && sleeps2Dot.size() > 0) {
                    sleeps2 = new int[sleeps2Dot.size()];
                    for (int i = 0; i < sleeps2Dot.size(); i++) {
                        sleeps2[i] = sleeps2Dot.get(i).data;
                    }
                } else {
                    sleeps2 = new int[xLables.length];
                }

                for (int i = 0; i < sleeps2.length; i++) {
                    if (sleeps2Dot != null && sleeps2Dot.size() > 0) {
                        if (weekMonthYear == WeekMonthYearEnum.YEAR) {
                            if (isOverCurrentDateYear(sleeps2Dot.get(i).date)) {
                                break;
                            }
                        } else {
                            if (isOverCurrentDate(sleeps2Dot.get(i).date)) {
                                break;
                            }
                        }
                    } else {
                        if (weekMonthYear == WeekMonthYearEnum.WEEK) {
                            int dayOfWeek = TimeUtil.getDayOfWeek(new Date());
                            int index = AppSharedPreferencesUtils.getInstance().getWeekStartIndex();
                            if (i > (dayOfWeek - index)) {
                                break;
                            }
                        } else if (weekMonthYear == WeekMonthYearEnum.MONTH) {
                            int dayOfMonth = TimeUtil.getDayOfMonth();
                            if (i >= dayOfMonth) {
                                break;
                            }
                        } else {
                            int currentMonth = TimeUtil.getCurrentMonth();
                            if (i > currentMonth) {
                                break;
                            }
                        }
                    }

                    int sleepTime = sleeps2[i];
                    DebugLog.d("目标睡眠 sleepTime=" + sleepTime);
                    if (i != 0) {
                        int preSleep = sleeps2[i - 1];
                        canvas.drawLine(xOffset + (i - 1) * xLableSpace, yScale[3] - (int) (preSleep * sleepSpace),
                                xOffset + i * xLableSpace, yScale[3] - (int) (sleepTime * sleepSpace),
                                onlinePaint);
                    }
                }

                /**
                 * 再描点
                 */
                linePaint.setColor(circleColor[0]);
                for (int i = 0; i < sleeps2.length; i++) {
                    if (sleeps2Dot != null && sleeps2Dot.size() > 0) {
                        if (weekMonthYear == WeekMonthYearEnum.YEAR) {
                            if (isOverCurrentDateYear(sleeps2Dot.get(i).date)) {
                                break;
                            }
                        } else {
                            if (isOverCurrentDate(sleeps2Dot.get(i).date)) {
                                break;
                            }
                        }
                    } else {
                        if (weekMonthYear == WeekMonthYearEnum.WEEK) {
                            int dayOfWeek = TimeUtil.getDayOfWeek(new Date());
                            int index = AppSharedPreferencesUtils.getInstance().getWeekStartIndex();
                            if (i > (dayOfWeek - index)) {
                                break;
                            }
                        } else if (weekMonthYear == WeekMonthYearEnum.MONTH) {
                            int dayOfMonth = TimeUtil.getDayOfMonth();
                            if (i >= dayOfMonth) {
                                break;
                            }
                        } else {
                            int currentMonth = TimeUtil.getCurrentMonth();
                            if (i > currentMonth) {
                                break;
                            }
                        }
                    }

                    int sleepTime = sleeps2[i];
                    // 等于0的话空心
                    if (sleepTime == 0) {
                        kongPaint.setColor(circleColor[0]);
                        kongPaint.setStyle(Paint.Style.FILL);
                        kongPaint.setStrokeWidth(3);
                      //  canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (sleepTime * sleepSpace), realPading - kongPadding, kongPaint);
                        canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (sleepTime * sleepSpace), realPading , kongPaint);
                        kongPaint.setColor(Color.TRANSPARENT);
                        canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (sleepTime * sleepSpace), realPading, kongPaint);
                    } else {
                        canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - (int) (sleepTime * sleepSpace), realPading, onlinePaint);
                    }
                }

                // 浅睡眠
                lessSleepDot = pageData.dots.get(2);
                if (lessSleepDot != null && lessSleepDot.size() > 0) {
                    DebugLog.d("先画线再描点---浅睡眠=" + (lessSleepDot.size()));
                    lessSleep = new int[lessSleepDot.size()];
                    for (int i = 0; i < lessSleepDot.size(); i++) {
                        lessSleep[i] = lessSleepDot.get(i).data;
                    }
                } else {
                    lessSleep = new int[xLables.length];
                }

                // 画选中的圈圈
                drawSelectCircle(canvas, dotX, dotY, "SLEEP", touchX, sleeps, sleeps2, lessSleep);

            }

            // 画下方渐变色
            if (pageType == PageTypeEnum.SPORT) {
                Shader shader = new LinearGradient(xOffset, yScale[3], w - xOffset, yScale[3],
                        new int[]{sportColor, sportColor, sportColor}, null, Shader.TileMode.CLAMP);
                shaderPaint.setShader(shader);
                shaderPaint.setAlpha(80);
                Path path = new Path();
                path.moveTo(xOffset, yScale[3]);

                int index = -1;
                for (int i = 0; i < steps.length; i++) {
                    if (stepsDot != null && stepsDot.size() > 0) {
                        if (weekMonthYear == WeekMonthYearEnum.YEAR) {
                            if (isOverCurrentDateYear(stepsDot.get(i).date)) {
                                break;
                            } else {
                                index = i;
                            }
                        } else {
                            if (isOverCurrentDate(stepsDot.get(i).date)) {
                                break;
                            } else {
                                index = i;
                            }
                        }

                    }
                    int step = steps[i];
                    path.lineTo(xOffset + i * xLableSpace, yScale[3] - (int) (step * stepSpace));
                }

                if (index != -1) {
                    path.lineTo(xOffset + index * xLableSpace, yScale[3]);
                }

                path.close();
                canvas.drawPath(path, shaderPaint);
            } else if (pageType == PageTypeEnum.SLEEP) {
                // 总睡眠
                Shader shader = new LinearGradient(xOffset, yScale[3], w - xOffset, yScale[3],
                        new int[]{Color.WHITE, Color.WHITE, Color.WHITE}, null, Shader.TileMode.CLAMP);
                shaderPaint.setShader(shader);
                shaderPaint.setAlpha(80);
                Path path = new Path();
                path.moveTo(xOffset, yScale[3]);

                int index = -1;
                for (int i = 0; i < sleeps.length; i++) {
                    if (sleepsDot != null && sleepsDot.size() > 0) {
                        if (weekMonthYear == WeekMonthYearEnum.YEAR) {
                            if (isOverCurrentDateYear(sleepsDot.get(i).date)) {
                                break;
                            } else {
                                index = i;
                            }
                        } else {
                            if (isOverCurrentDate(sleepsDot.get(i).date)) {
                                break;
                            } else {
                                index = i;
                            }
                        }

                    }
                    int sleep = sleeps[i];
                    path.lineTo(xOffset + i * xLableSpace, yScale[3] - (int) (sleep * sleepSpace));
                }

                if (index != -1) {
                    path.lineTo(xOffset + index * xLableSpace, yScale[3]);
                }

//                path.lineTo(w - xOffset, yScale[3]);
                path.close();
                canvas.drawPath(path, shaderPaint);

                // 深睡眠
                Shader shader2 = new LinearGradient(xOffset, yScale[3], w - xOffset, yScale[3],
                        new int[]{circleColor[0], circleColor[0], circleColor[0]}, null, Shader.TileMode.CLAMP);
                shaderPaint.setShader(shader2);
                shaderPaint.setAlpha(50);
                Path path2 = new Path();
                path2.moveTo(xOffset, yScale[3]);

                int index2 = -1;
                for (int i = 0; i < sleeps2.length; i++) {
                    if (sleeps2Dot != null && sleeps2Dot.size() > 0) {
                        if (weekMonthYear == WeekMonthYearEnum.YEAR) {
                            if (isOverCurrentDateYear(sleeps2Dot.get(i).date)) {
                                break;
                            } else {
                                index2 = i;
                            }
                        } else {
                            if (isOverCurrentDate(sleeps2Dot.get(i).date)) {
                                break;
                            } else {
                                index2 = i;
                            }
                        }

                    }

                    int sleep = sleeps2[i];
                    path2.lineTo(xOffset + i * xLableSpace, yScale[3] - (int) (sleep * sleepSpace));
                }

                if (index2 != -1) {
                    path2.lineTo(xOffset + index2 * xLableSpace, yScale[3]);
                }
                path2.close();
                canvas.drawPath(path2, shaderPaint);

            }

        }

        /**
         *  -----  心率数据  --------
         *  -----  心率数据  --------
         *  -----  心率数据  --------
         */

        else

        {
            MAX_HEART_RATE = pageData.lineValue;
            int HEART_VALUE_MIN = MAX_HEART_RATE, HEART_VALUE_MAN = MAX_HEART_RATE / 2;
            float minCentHeight = ViewUtil.getTextRectHeight(linePaint, HEART_VALUE_MIN + "");
            float maxCentHeight = ViewUtil.getTextRectHeight(linePaint, HEART_VALUE_MAN + "");
            float centWidth = ViewUtil.getTextRectWidth(linePaint, HEART_VALUE_MAN + "") > ViewUtil.getTextRectWidth(linePaint, HEART_VALUE_MIN + "") ?
                    ViewUtil.getTextRectWidth(linePaint, HEART_VALUE_MAN + "") : ViewUtil.getTextRectWidth(linePaint, HEART_VALUE_MIN + "");
            centWidth = centWidth > xOffset ? xOffset : centWidth;
            linePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("" + HEART_VALUE_MAN, centWidth / 2, yScale[3] - yHeight[2] * 4 / 10 + minCentHeight / 2, linePaint);
            canvas.drawText("" + HEART_VALUE_MIN, centWidth / 2, yScale[3] - yHeight[2] * 8 / 10 + maxCentHeight / 2, linePaint);
//            canvas.drawLine(xOffset, yScale[3] - yHeight[2] * 4 / 10, w - xOffset, yScale[3] - yHeight[2] * 4 / 10, linePaint);
//            canvas.drawLine(xOffset, yScale[3] - yHeight[2] * 8 / 10, w - xOffset, yScale[3] - yHeight[2] * 8 / 10, linePaint);

            // 画虚线
            int lineWidth = ScreenUtils.dp2px(5);
            int lineNum = (w - xOffset) / lineWidth;
            for (int i = 0; i < lineNum; i++) {
                canvas.drawLine(lineWidth * i + xOffset, yScale[3] - yHeight[2] * 4 / 10, lineWidth * i + xOffset + lineWidth / 2, yScale[3] - yHeight[2] * 4 / 10, linePaint);
                canvas.drawLine(lineWidth * i + xOffset, yScale[3] - yHeight[2] * 8 / 10, lineWidth * i + xOffset + lineWidth / 2, yScale[3] - yHeight[2] * 8 / 10, linePaint);
            }

            /**
             *
             */
            onlinePaint.setStrokeWidth(3);
            onlinePaint.setColor(Color.WHITE);
            int restHeight = yHeight[2] * 8 / 10;
            float heartRateSpace = (float) restHeight / (float) MAX_HEART_RATE;
            DebugLog.d("心率的间隔：" + heartRateSpace);
            ratesDot = pageData.dots.get(0);
            if (ratesDot != null && ratesDot.size() > 0) {
                rates = new int[ratesDot.size()];
                for (int i = 0; i < ratesDot.size(); i++) {
                    rates[i] = ratesDot.get(i).data;
                }
            }
            if (rates == null) {
                rates = new int[xLables.length];
            }

            /**
             * 先画线，再描点
             */
            for (int i = 0; i < rates.length; i++) {
                if (ratesDot != null && ratesDot.size() > 0) {
                    if (weekMonthYear == WeekMonthYearEnum.YEAR) {
                        if (isOverCurrentDateYear(ratesDot.get(i).date)) {
                            break;
                        }
                    } else {
                        if (isOverCurrentDate(ratesDot.get(i).date)) {
                            break;
                        }
                    }
                } else {
                    if (weekMonthYear == WeekMonthYearEnum.WEEK) {
                        int dayOfWeek = TimeUtil.getDayOfWeek(new Date());
                        int index = AppSharedPreferencesUtils.getInstance().getWeekStartIndex();
                        if (i > (dayOfWeek - index)) {
                            break;
                        }
                    } else if (weekMonthYear == WeekMonthYearEnum.MONTH) {
                        int dayOfMonth = TimeUtil.getDayOfMonth();
                        if (i >= dayOfMonth) {
                            break;
                        }
                    } else {
                        int currentMonth = TimeUtil.getCurrentMonth();
                        if (i > currentMonth) {
                            break;
                        }
                    }
                }

                int rate = (rates[i] < 0 ? 0 : rates[i]) > MAX_HEART_RATE ? MAX_HEART_RATE : rates[i];
                if (i != 0) {
                    int preRate = (rates[i - 1] < 0 ? 0 : rates[i - 1]) > MAX_HEART_RATE ? MAX_HEART_RATE : rates[i - 1];
                    canvas.drawLine(xOffset + (i - 1) * xLableSpace, yScale[3] - preRate * heartRateSpace, xOffset + i * xLableSpace, yScale[3] - rate * heartRateSpace, onlinePaint);
                }
            }

            float[] dotX = new float[rates.length];
            float[] dotY = new float[rates.length];

            for (int i = 0; i < rates.length; i++) {
                if (ratesDot != null && ratesDot.size() > 0) {
                    if (weekMonthYear == WeekMonthYearEnum.YEAR) {
                        if (isOverCurrentDateYear(ratesDot.get(i).date)) {
                            break;
                        }
                    } else {
                        if (isOverCurrentDate(ratesDot.get(i).date)) {
                            break;
                        }
                    }
                } else {
                    if (weekMonthYear == WeekMonthYearEnum.WEEK) {
                        int dayOfWeek = TimeUtil.getDayOfWeek(new Date());
                        int index = AppSharedPreferencesUtils.getInstance().getWeekStartIndex();
                        if (i > (dayOfWeek - index)) {
                            break;
                        }
                    } else if (weekMonthYear == WeekMonthYearEnum.MONTH) {
                        int dayOfMonth = TimeUtil.getDayOfMonth();
                        if (i >= dayOfMonth) {
                            break;
                        }
                    } else {
                        int currentMonth = TimeUtil.getCurrentMonth();
                        if (i > currentMonth) {
                            break;
                        }
                    }
                }

                int rate = (rates[i] < 0 ? 0 : rates[i]) > MAX_HEART_RATE ? MAX_HEART_RATE : rates[i];
                dotX[i] = xOffset + i * xLableSpace;
                dotY[i] = yScale[3] - rate * heartRateSpace;
                // 等于0的话空心
                if (rate == 0) {
                    kongPaint.setColor(Color.WHITE);
                    kongPaint.setStyle(Paint.Style.FILL);
                    kongPaint.setStrokeWidth(3);
                 //   canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - rate * heartRateSpace, realPading - kongPadding, kongPaint);
                    canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - rate * heartRateSpace, realPading , kongPaint);
                    kongPaint.setColor(Color.TRANSPARENT);
                    canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - rate * heartRateSpace, realPading, kongPaint);
                } else {
                    canvas.drawCircle(xOffset + i * xLableSpace, yScale[3] - rate * heartRateSpace, realPading, linePaint);
                }
            }

            // 画选中的圈圈
            drawSelectCircle(canvas, dotX, dotY, "HEART", touchX, rates);
        }

    }

    /**
     * 画坐标
     *
     * @param canvas
     */
    private void drawBottom(Canvas canvas) {
        bottomPaint.setColor(Color.WHITE);
        bottomPaint.setTextAlign(Paint.Align.CENTER);
        bottomPaint.setTextSize(textSize * 1.2f);
        float bottomHeight = ViewUtil.getTextRectHeight(bottomPaint, xLables[0]);

        for (int i = 0; i < xLables.length; i++) {

            if (weekMonthYear == WeekMonthYearEnum.MONTH) {
                if (i % 6 == 0 || i == xLables.length - 1) {
                    canvas.drawText(xLables[i], xOffset + xLableSpace * i, yScale[4] - (yHeight[3] - bottomHeight) / 4, bottomPaint);
                }
            } else {
                canvas.drawText(xLables[i], xOffset + xLableSpace * i, yScale[4] - (yHeight[3] - bottomHeight) / 4, bottomPaint);
            }
        }
    }

    /**
     * 触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        isReDraw = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                touchX = event.getX();
                invalidate();
                break;

            default:
                break;
        }
        return false;
    }

    /**
     * 设置页面数据
     *
     * @param pageData
     * @param weekMonthYear
     * @param pageType
     */
    public void setPageData(PageData pageData, WeekMonthYearEnum weekMonthYear, PageTypeEnum pageType) {
        isReDraw = false;
        this.pageData = pageData;
        this.weekMonthYear = weekMonthYear;
        this.pageType = pageType;
        invalidate();
    }

    /**
     * 是否超过当前日期
     *
     * @param date
     * @return
     */
    private boolean isOverCurrentDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return false;
        } else {
            int currentDate = Integer.parseInt(TimeUtil.timeStamp2Date(System.currentTimeMillis(), "yyyyMMdd"));
            if (Integer.parseInt(date) > currentDate) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 是否超过当前日期
     *
     * @param date
     * @return
     */
    private boolean isOverCurrentDateYear(String date) {
        if (TextUtils.isEmpty(date)) {
            return false;
        } else {
            int currentDate = Integer.parseInt(TimeUtil.timeStamp2Date(System.currentTimeMillis(), "yyyyMM"));
            if (Integer.parseInt(date) > currentDate) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 页面的数据模型
     */
    public static class PageData {

        public String title;
        public String allTitle;
        public String goal;
        public int lineValue;
        //        public ArrayList<int[]> dots;
        public ArrayList<ArrayList<Dot>> dots;
        public String[] datas;
        public String[] symbols;

        @Override
        public String toString() {
            return "PageData{" +
                    "title='" + title + '\'' +
                    "allTitle='" + allTitle + '\'' +
                    ", goal='" + goal + '\'' +
                    ", lineValue=" + lineValue +
                    ", dots=" + dots +
                    ", datas=" + datas +
                    ", symbols=" + Arrays.toString(symbols) +
                    '}';
        }
    }

    public static class Dot {
        public String date;
        public int data;

        @Override
        public String toString() {
            return "Dot{" +
                    "date='" + date + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

}
