package com.ljn.callingsimulation;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class CallActivity extends AppCompatActivity{

    FloatingActionButton call;
    FloatingActionButton call_end;
    ImageView call_im;
    int mTop, mBottom;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.call);

        FinishListActivity.getInstance().addActivity(this);

        name = getIntent().getStringExtra("name");
        ((TextView)findViewById(R.id.name)).setText(name);

        initView();

        onAnim();
    }

    int lastY;
    int Top, Left, Bottom, Right;

    private void onAnim() {
        call.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                floatingButtonMove(v, event, false);
                return true;
            }
        });
        call_end.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                floatingButtonMove(v, event, true);
                return true;
            }
        });
    }

    private void floatingButtonMove(View v, MotionEvent event, boolean end) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastY = (int) event.getRawY();
                if (mTop == 0) mTop = v.getTop() - 500;
                if (mBottom == 0) mBottom = v.getBottom();
                break;
            case MotionEvent.ACTION_MOVE: // 移动
                // 移动中动态设置位置
                int dy = (int) event.getRawY() - lastY;
                int left = v.getLeft();
                int top = v.getTop() + dy;
                int right = v.getRight();
                int bottom = v.getBottom() + dy;
                if (top >= mTop && bottom <= mBottom) {
                    v.layout(left, top, right, bottom);
                    Top = top;
                    Right = right;
                    Left = left;
                    Bottom = bottom;
                }
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (Top == mTop) {
                    if (!end) {
                        Intent intent = new Intent(CallActivity.this, CalledActivity.class);
                        intent.putExtra("name",name);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                                CallActivity.this, call, "startAnim").toBundle());
                    } else {
                        finish();
                    }
                } else {
                    v.layout(Left, mTop + 500, Right, mBottom);
                }
                break;
        }
    }

    private void initView() {
        call = (FloatingActionButton) findViewById(R.id.call);
        call_end = (FloatingActionButton) findViewById(R.id.call_end);
//        call_im = (ImageView) findViewById(R.id.call_im);
        mTop = 0;
        mBottom = 0;
    }
}
