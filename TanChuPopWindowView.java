package cn.dongha.ido.common.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.veryfit.multi.nativedatabase.HealthHeartRate;
import com.veryfit.multi.nativedatabase.HealthSport;
import com.veryfit.multi.nativedatabase.healthSleep;
import com.veryfit.multi.nativeprotocol.ProtocolUtils;
import com.veryfit.multi.util.DebugLog;
import cn.dongha.ido.MyApplication;
import cn.dongha.ido.R;
import cn.dongha.ido.common.utils.TimeUtil;
import cn.dongha.ido.ui.detail.PageTypeEnum;
import cn.dongha.ido.ui.main.MainFragment;
import cn.dongha.ido.ui.main.PointLineTypeEnum;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by asus on 2017/7/13.
 */
public class MainPopWindow extends PopupWindow {
    private PointLineParent pointLineParent;
    private View view;
    private ProtocolUtils protocolUtils = ProtocolUtils.getInstance();
    private LinkedList<Integer> datas= new LinkedList<>();
    private PointLineView.onDateScrolling selectLinstener;


    public MainPopWindow(View contentView, Activity context,PointLineView.onDateScrolling selectLinstener) {
        super(contentView);
        this.selectLinstener = selectLinstener;
        view = contentView;
        int widths = context.getWindowManager().getDefaultDisplay().getHeight();
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(widths / 3);
        initView();
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

    }


    private void initView() {
        pointLineParent = (PointLineParent) view.findViewById(R.id.main_pointLineParent);
        pointLineParent.setOnDateScrollingLinstener(selectLinstener);
    }

    public void setDatas(PageTypeEnum pageType, int dateOffset) {
        setPointLineData(pageType, dateOffset);
    }

    public void setGoToTheDayOnClicKListener(final View.OnClickListener listener) {
        pointLineParent.getGoToTheDayView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.dateOffset = pointLineParent.getShowingOffset();
                listener.onClick(v);
            }
        });
    }

    private void setPointLineData(PageTypeEnum pageType,int dateOffset) {
        List<Date> dateList = MainFragment.mainDateList;
        datas.clear();
        if (pageType == PageTypeEnum.SPORT) {
            pointLineParent.setType(PointLineTypeEnum.SPORT_TYPE);
            for (int i = 0; i < dateList.size(); i++) {
                Date date = dateList.get(i);
                DebugLog.d("设置页面数据date=" + TimeUtil.timeStamp2Date(date.getTime(), "yyyy-MM-dd"));
                HealthSport sport = protocolUtils.getHealthSport(date);
                if (sport == null) {
                    sport = new HealthSport();
                    sport.setDate(date);
                    sport.setYear(date.getYear());
                    sport.setMonth(date.getMonth() + 1);
                    sport.setDay(date.getDate());
                }
                datas.add(sport.getTotalStepCount());
            }
        pointLineParent.setBackgroundResource(R.drawable.sport_bg);

        } else if (pageType == PageTypeEnum.SLEEP) {
            pointLineParent.setType(PointLineTypeEnum.SLEEP_TYPE);
            for (int i = 0; i < dateList.size(); i++) {
                Date date = dateList.get(i);
                healthSleep sleep = ProtocolUtils.getInstance().getHealthSleep(date);
                if (sleep == null) {
                    sleep = new healthSleep();
                    sleep.setDate(date);
                    sleep.setYear(date.getYear());
                    sleep.setMonth(date.getMonth() + 1);
                    sleep.setDay(date.getDate());
                }
                datas.add(sleep.getTotalSleepMinutes());
            }
            pointLineParent.setBackgroundResource(R.drawable.sleep_bg);
        } else if (pageType == PageTypeEnum.HEARTRATE) {
            pointLineParent.setType(PointLineTypeEnum.HEART_TYPE);
            for (int i = 0; i < dateList.size(); i++) {
                Date date = dateList.get(i);
                HealthHeartRate rate = protocolUtils.getHealthRate(date);
                if (rate == null) {
                    rate = new HealthHeartRate();
                    rate.setDate(date);
                    rate.setYear(date.getYear());
                    rate.setMonth(date.getMonth()+1);
                    rate.setDay(date.getDate());
                }
                datas.add(rate.getSilentHeart());
            }
            pointLineParent.setBackgroundResource(R.drawable.heart_lp_bg);
        }

        pointLineParent.setDatas(datas);
        pointLineParent.setCurrentItem(dateOffset);
    }

}
