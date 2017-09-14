package com.ljn.callingsimulation;

/**
 * Created by 12390 on 2017/9/14.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;

/**
 * Created by fangzhao on 2016/9/27.
 */
public class ClockView extends View{

    private Paint circlePaint;//圆盘画笔
    private Paint centurePaint;//圆心画笔
    private Paint numPaint;//数字画笔
    private Paint hourPaint;//时针画笔
    private Paint minutePaint;//分针画笔
    private Paint secondPaint;//秒针画笔
    private Paint ArcPaint;//外圈刻度画笔
    private Paint changePaint; //渐变画笔
    private float width;//此时钟控件的默认宽度
    private float height;//此时钟控件的默认高度
    private Calendar calendar; //时间对象
    private Path path; //画三角形

    public ClockView(Context context){
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        initView();
    }

    //此方法用作初始化变量
    private void initView(){
        //初始化各种画笔
        circlePaint = new Paint();
        centurePaint = new Paint();
        ArcPaint = new Paint();
        numPaint = new Paint();
        hourPaint = new Paint();
        minutePaint = new Paint();
        secondPaint = new Paint();
        changePaint = new Paint();
        path = new Path();
        //自己定义的长度宽度，方法作用是把参数2的276转换成dp单位，即276dp
        width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 276, getResources().getDisplayMetrics());
        height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 276, getResources().getDisplayMetrics());
        //圆盘画笔设置
        circlePaint.setAntiAlias(true);//消除锯齿
        circlePaint.setColor(Color.WHITE);//设置圆盘画笔的颜色为红色
        circlePaint.setStyle(Paint.Style.STROKE);//设置画笔的类型为描边
        circlePaint.setStrokeWidth(1);//设置描边宽度
        circlePaint.setAlpha(100);//设置画笔透明度，最高值为255
        //外圈画笔设置
        ArcPaint.setAntiAlias(true);
        ArcPaint.setColor(Color.WHITE);
        ArcPaint.setStyle(Paint.Style.STROKE);
        ArcPaint.setStrokeWidth(1);
        //圆心画笔设置
        centurePaint.setStyle(Paint.Style.STROKE);//设置画笔的类型为铺满
        centurePaint.setColor(Color.WHITE);//设置画笔的颜色为红色
        centurePaint.setAntiAlias(true);//设置圆心画笔为无锯齿
        centurePaint.setStrokeWidth(5);
        //数字画笔设置
        numPaint.setAntiAlias(true);
        numPaint.setColor(Color.WHITE);
        numPaint.setStrokeWidth(1);
        numPaint.setTextSize(20);
        //时钟画笔设置
        hourPaint.setColor(Color.WHITE);
        hourPaint.setAntiAlias(true);
        hourPaint.setStrokeWidth(6);
        hourPaint.setAlpha(100);
        //分钟画笔设置
        minutePaint.setColor(Color.WHITE);
        minutePaint.setAntiAlias(true);
        minutePaint.setStrokeWidth(3);
        //秒钟画笔设置
        secondPaint.setColor(Color.WHITE);
        secondPaint.setAntiAlias(true);
        //渐变画笔设置
        changePaint.setColor(Color.WHITE);
        secondPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //首先获取当前时间
        calendar = Calendar.getInstance();
        //开始画圆盘
        float radius = width / 2 - 20;//圆盘的半径
        for(int i = 0; i < 480; i++){
            //画刻度之前，先把画布的状态保存下来
            canvas.save();
            //让画布旋转3/5度，参数一是需要旋转的度数，参数2,3是旋转的圆心
            canvas.rotate(i*3/4 , getWidth() / 2, getHeight() / 2);
            //旋转后再圆上画上一长10dp的刻度线
            canvas.drawLine(width / 2, height / 2 - radius, width / 2, height / 2 - radius + 15, circlePaint);
            //恢复画布
            canvas.restore();
        }

        //画外围四分圈
        //画圆弧对象，参数1234分别代表，起始x值，起始y值，终点x值，终点y值
        RectF oval = new RectF(5, 5, getWidth() - 5, getHeight() - 5);
        //利用画布进行画弧，参数1是RectF对象，参数2是起始点，参数3是要画的度数（多大），4画笔对象
        canvas.drawArc(oval, -88, 86, false, ArcPaint);
        canvas.drawArc(oval, 2, 86, false, ArcPaint);
        canvas.drawArc(oval, 92, 86, false, ArcPaint);
        canvas.drawArc(oval, 182, 86, false, ArcPaint);
        //画数字，因为时间问题没有自己总结好算法，具体位置需要自己调整
        canvas.drawText("12", getWidth() / 2, getHeight() / 2 - radius - 30, numPaint);
        canvas.drawText("3", getWidth() / 2 + radius + 33, getHeight() / 2, numPaint);
        canvas.drawText("6", getWidth() / 2, getHeight() / 2 + radius + 43, numPaint);
        canvas.drawText("9", getWidth() / 2 - radius - 43, getHeight() / 2, numPaint);

        //画圆心
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 10, centurePaint);

        //画时针
        int hour = calendar.get(Calendar.HOUR);//当前小时数
        canvas.save();
        canvas.rotate(hour * 30, getWidth() / 2, getHeight() / 2);
        canvas.drawLine(getWidth() / 2, getHeight() / 2 - 10, getWidth() / 2, getHeight() / 2 - radius + 80, hourPaint);
        canvas.restore();

        //画分针
        int minute = calendar.get(Calendar.MINUTE);
        canvas.save();
        canvas.rotate(minute * 6, getWidth() / 2, getHeight() / 2);
        canvas.drawLine(getWidth() / 2, getHeight() / 2 - 10, getWidth() / 2, getHeight() / 2 - radius + 20, minutePaint);
        canvas.restore();

        //画秒针
        //获得秒后一位的时间数,如（1.1秒。。。）
        int millis = calendar.get(Calendar.MILLISECOND) / 100;
        float second = (float) (calendar.get(Calendar.SECOND) + millis * 0.1);
        canvas.save();
        canvas.rotate(30 * second / 5, getWidth() / 2, getHeight() / 2);
        //Path是画自定义图形的对象，在构造方法中实例化
        path.moveTo(getWidth() / 2, getHeight() / 2 - radius - 5);//三角形的顶点
        path.lineTo(getWidth() / 2 - 10, getHeight() / 2 - radius + 10);//底边左端点
        path.lineTo(getWidth() / 2 + 10, getHeight() / 2 - radius + 10);//底边右端点
        path.close();//让三个点形成封闭的图形
        canvas.drawPath(path, secondPaint);//把形成的图形化在画布上
        //画渐变进度条
        for(int i = 0 ; i < 200 ; i++){
            changePaint.setAlpha(255-i);
            canvas.save();
            canvas.rotate(-i*3/5 +5, getWidth() / 2, getHeight() / 2);
            canvas.drawLine(width / 2, height / 2 - radius, width / 2, height / 2 - radius + 15, changePaint);
            canvas.restore();
        }
        canvas.restore();


        postInvalidateDelayed(100);//每0.1秒更新一次
    }
}
