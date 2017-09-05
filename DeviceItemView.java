package cn.dongha.ido.common.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.veryfit.multi.util.DebugLog;
import cn.dongha.ido.R;
import cn.dongha.ido.common.utils.ScreenUtil;
import cn.dongha.ido.common.utils.ScreenUtils;

/**
 * @author: sslong
 * @package: com.veryfit.
 * @description: ${TODO}{一句话描述该类的作用}
 * @date: 2016/5/17 16:05
 */
public class ItemLableValue extends RelativeLayout {

    private TextView lableView;

    public ValueStateTextView valueView;

    private String lable, value;

    private String targetActivty;

    protected Paint paint;

    protected boolean hasBottomLine;

    protected int bottomLineColor;

    protected boolean hasTopLine;

    private boolean has_all_line;

    private float line_offset_px = 0;
    private float lineSize = 2f;

    protected int topLineColor;
    public View rightIv;
    public ItemLableValue(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemLableValue(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.item_lable_value, this, true);
        lableView = (TextView) findViewById(R.id.lable);
        valueView = (ValueStateTextView) findViewById(R.id.value);
        ImageView imageView = (ImageView) findViewById(R.id.left_drawable);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItemLableValue);

        lable = a.getString(R.styleable.ItemLableValue_lable_text);
        value = a.getString(R.styleable.ItemLableValue_value_text);
        targetActivty = a.getString(R.styleable.ItemLableValue_target_activty);
        // boolean isOpen = a.getBoolean(R.styleable.ItemLableValue_opened,
        // true);
        // boolean isEnable =
        // a.getBoolean(R.styleable.ItemLableValue_android_enabled, true);
        int valueTextColor = a.getColor(R.styleable.ItemLableValue_valueTextColor, 0);
        has_all_line = a.getBoolean(R.styleable.ItemLableValue_has_all_line, false);
        hasBottomLine = a.getBoolean(R.styleable.ItemLableValue_has_bottom_line, true);
        hasTopLine = a.getBoolean(R.styleable.ItemLableValue_has_top_line, false);

        lineSize = context.getResources().getDimension(R.dimen.x1);
        if (hasBottomLine) {
            bottomLineColor = a.getColor(R.styleable.ItemLableValue_bottom_line_color, getResources().getColor(R.color.driver_color));
            initDraw(context);
        }

        if (hasTopLine) {
            topLineColor = a.getColor(R.styleable.ItemLableValue_top_line_color, getResources().getColor(R.color.driver_color));
            initTopDraw();
        }

        Drawable rightDrawable = a.getDrawable(R.styleable.ItemLableValue_right_arrow);
        Drawable leftDrawable = a.getDrawable(R.styleable.ItemLableValue_left_drawable);
        if (leftDrawable == null) {
            imageView.setVisibility(View.GONE);
        }
        imageView.setImageDrawable(leftDrawable);
        a.recycle();

        if (rightDrawable != null) {
            rightDrawable.setBounds(0, 0, rightDrawable.getIntrinsicWidth(), rightDrawable.getIntrinsicHeight());
            valueView.setCompoundDrawables(null, null, rightDrawable, null);
        }
        if (valueTextColor != 0) {
            valueView.setTextColor(valueTextColor);
        }

        lableView.setText(lable);
//        lableView.setTextColor(getResources().getColor(R.color.normal_font_color));
        valueView.setText(value);
//        lableView.setTextSize(12);
        // valueView.setOpen(isOpen);
        // valueView.setEnabled(isEnable);
        if (targetActivty != null) {
            setOnClickListener(onClick);
        }
    }

    protected void initDraw(Context context) {
        setWillNotDraw(false);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(bottomLineColor);
        paint.setStrokeWidth(lineSize);
        line_offset_px = context.getResources().getDimension(R.dimen.x30);
    }


    protected void initTopDraw() {
        setWillNotDraw(false);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(topLineColor);
        paint.setStrokeWidth(lineSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hasBottomLine) {
            if (has_all_line) {
                canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), paint);
            } else {
//                canvas.drawLine(ScreenUtils.dp2px(35), getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), paint);
                canvas.drawLine(line_offset_px, getMeasuredHeight(), getMeasuredWidth()-line_offset_px, getMeasuredHeight(), paint);
            }
        }
        if (hasTopLine) {
//            if (has_all_line) {
//                canvas.drawLine(0, 0, getMeasuredWidth(), 0, paint);
//            } else {
//                canvas.drawLine(ScreenUtils.dp2px(35), 0, getMeasuredWidth(), 0, paint);
//            }
            canvas.drawLine(0, 0, getMeasuredWidth(), 0, paint);
        }
    }

    private OnClickListener onClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            DebugLog.d("onClick : targetActivty = " + targetActivty);
            if (targetActivty != null && valueView.isEnabled()) {
                Intent intent = new Intent();
                intent.setClassName(getContext(), targetActivty);
                getContext().startActivity(intent);
            }
        }
    };


    public void setEnable(boolean enable) {
        super.setEnabled(enable);
        valueView.setEnabled(enable);
//		if(!enable){
//		}
    }

    public boolean isEnable() {
        return valueView.isEnabled();
    }

    public String getValue() {
        return valueView.getText().toString().trim();
    }

    public void setValue(String value) {
        valueView.setText(value);
    }

    public boolean isOpen() {
        return valueView.isOpen();
    }

    public void setOpen(boolean isOpen) {
        valueView.setOpen(isOpen);
        valueView.setText(isOpen ? R.string.remind_state_open : R.string.remind_state_close);
    }

    public void setValueState(boolean isOpen, int strId) {
        setEnable(isOpen);
        setOpen(isOpen);
        valueView.setText(strId);
    }

    public void setValueState(boolean isOpen, String str) {
        setEnable(isOpen);
        setOpen(isOpen);
        valueView.setText(str);
    }

    public void setValueColor(int colorId) {
        valueView.setTextColor(colorId);
    }

}

