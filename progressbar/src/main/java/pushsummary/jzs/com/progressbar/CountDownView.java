package pushsummary.jzs.com.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by a on 2016/10/14.
 */
public class CountDownView extends View {
    /**
     * 1、进度条的背景颜色
     * 2. 边框的宽度
     * 3.边框的颜色
     * 4.文字的大小
     * 5.文字的颜色
     * 6.文本文字
     *
     * @param context
     */


    private static final String TAG = CountDownView.class.getSimpleName();
    private static final int BACKGROUND_COLOR = 0x50555555; //背景颜色
    private static final float BORDER_WIDTH = 15f;
    private static final int BORDER_COLOR = 0xFF6ADBFE;
    private static final String TEXT = "跳过广告";
    private static final float TEXT_SIZE = 50f;
    private static final int TEXT_COLOR = 0xFFFFFFFF;


    private int backgroundColor;
    private float borderWidth;
    private int borderColor;
    private String text;
    private int textColor;
    private float textSize;
    // 定义三只画笔 圆形，文字 ，边框

    private Paint circlePaint;
    private TextPaint textPaint;
    private Paint borderPaint;

    private float progress = 135; //当前进度
    private StaticLayout staticLayout;

    private CountDownTimerListener listener;

    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义属性值
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
//        获取进度的背景颜色
        backgroundColor = ta.getColor(R.styleable.CountDownView_background_color, BACKGROUND_COLOR);
        borderColor = ta.getColor(R.styleable.CountDownView_border_color, BORDER_COLOR);
        borderWidth = ta.getDimension(R.styleable.CountDownView_border_width, BORDER_WIDTH);
        text = ta.getString(R.styleable.CountDownView_text);
        if (TextUtils.isEmpty(text)) {
            text = TEXT;
        }
        textColor = ta.getColor(R.styleable.CountDownView_text_color, TEXT_COLOR);
        textSize = ta.getDimension(R.styleable.CountDownView_text_size, TEXT_SIZE);

        ta.recycle();
        init();

    }


    //初始化
    private void init() {

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true); //设置抗锯齿
        circlePaint.setDither(true);//设置防抖动
        circlePaint.setColor(backgroundColor); //设置画笔颜色
        circlePaint.setStyle(Paint.Style.FILL); //设置画笔为实心


        textPaint = new TextPaint(); //鞋子笔
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);//设置字体的对齐方式

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setDither(true);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        int textWidth = (int) textPaint.measureText(text.substring(0, (text.length() + 1) / 2));
        staticLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1F, 1, false);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            width = staticLayout.getWidth();
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            height = staticLayout.getHeight();
        }
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int min = Math.min(width, height);
        //画底盘
        canvas.drawCircle(width / 2, height / 2, min / 2, circlePaint);
        //画边框
        RectF rectF;
        if (width > height) {
            rectF = new RectF(width / 2 - min / 2 + borderWidth / 2, 0 + borderWidth / 2, width / 2 + min / 2 - borderWidth / 2, height - borderWidth / 2);
        } else {
            rectF = new RectF(borderWidth / 2, height / 2 - min / 2 + borderWidth / 2, width - borderWidth / 2, height / 2 - borderWidth / 2 + min / 2);
        }
        canvas.drawArc(rectF, -90, progress, false, borderPaint);
        //画居中的文字
//       canvas.drawText("稍等片刻", width / 2, height / 2 - textPaint.descent() + textPaint.getTextSize() / 2, textPaint);
        canvas.translate(width / 2, height / 2 - staticLayout.getHeight() / 2);
        staticLayout.draw(canvas);
    }

    public void start() {
        if (listener != null) {
            listener.onStartCount();
        }
        CountDownTimer countDownTimer = new CountDownTimer(3600, 36) {
            @Override
            public void onTick(long millisUntilFinished) {
                progress = ((3600 - millisUntilFinished) / 3600f) * 360;
                Log.d(TAG, "progress:" + progress);
                invalidate();
            }

            @Override
            public void onFinish() {
                progress = 360;
                invalidate();
                if (listener != null) {
                    listener.onFinishCount();
                }
            }
        }.start();
    }

    public void setCountDownTimerListener(CountDownTimerListener listener) {
        this.listener = listener;
    }

    public interface CountDownTimerListener {

        void onStartCount();

        void onFinishCount();
    }
}

