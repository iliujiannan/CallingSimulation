package com.ljn.callingsimulation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.ljn.callingsimulation.bean.Calling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CallActivity extends AppCompatActivity {

    FloatingActionButton call;
    FloatingActionButton call_end;
    FloatingActionButton call_message;

    ArrayList<FloatingActionButton> floatingActionButtons = new ArrayList<>();

    ImageView call_im;
    int mTop, mBottom;
    Calling calling;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);



        //activity在锁屏状态下显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.call);
        FinishListActivity.getInstance().addActivity(this);

        calling = (Calling)getIntent().getSerializableExtra("calling");
        ((TextView) findViewById(R.id.name)).setText(calling.getCaller());
        initView();
        onAnim();
        startMusic();

        //初次开启动画
        startAnim();

    }

    private void startMusic() {
        mediaPlayer = MediaPlayer.create(this, getSystemDefultRingtoneUri());
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
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
                if (Top < mTop + 10 && Top > mTop - 10) {
                    if (!end) {
                        Intent intent = new Intent(CallActivity.this, CalledActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("calling", calling);
                        intent.putExtras(mBundle);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                                CallActivity.this, call, "startAnim").toBundle());
                        mediaPlayer.stop();
                    } else {
                        finish();
                        mediaPlayer.stop();
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
        call_message = (FloatingActionButton) findViewById(R.id.call_message);
//        call_im = (ImageView) findViewById(R.id.call_im);
        mTop = 0;
        mBottom = 0;

        floatingActionButtons.add(call_end);
        floatingActionButtons.add(call);
        floatingActionButtons.add(call_message);
    }

    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_RINGTONE);
    }

    /**
     * 关闭菜单动画
     */
    private void stopAnim() {

        for (int i = 0; i < 3; i++) {
            ObjectAnimator animator;
            if (i == 1) {
                animator = ObjectAnimator.ofFloat(floatingActionButtons.get(i), "translationY", -(150F), 0F);
            } else {
                animator = ObjectAnimator.ofFloat(floatingActionButtons.get(i), "translationY", -(80F), 0F);
            }
            animator.setDuration(1000); //动画持续时间
            animator.setInterpolator(new BounceInterpolator());
            animator.setStartDelay(i * 500);////动画间隔
            animator.start();

            final int finalI = i;
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (finalI == 2)
                        startAnim();
                }
            });
        }

    }

    /**
     * 开启菜单动画
     */
    private void startAnim() {

        for (int i = 0; i < 3; i++) {
            AnimatorSet animatorSet = new AnimatorSet();//组合动画
            ObjectAnimator animator;
            if (i == 1) {
                animator = ObjectAnimator.ofFloat(floatingActionButtons.get(i), "translationY", 0, -(150F));
            } else {
                animator = ObjectAnimator.ofFloat(floatingActionButtons.get(i), "translationY", 0, -(80F));
            }
            animatorSet.playTogether(animator);
            animatorSet.setDuration(1000);  //动画持续时间
            animatorSet.setInterpolator(new BounceInterpolator());
            animatorSet.setStartDelay((i + 1) * 500); //动画间隔
            animatorSet.start();

            final int finalI = i;
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (finalI == 2) {
                        stopAnim();
                    }
                }
            });
        }
    }

    /**
     * 屏蔽返回按钮
     */
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
