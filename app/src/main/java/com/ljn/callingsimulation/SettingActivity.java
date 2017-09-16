package com.ljn.callingsimulation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.*;
import android.widget.TextView;
import com.githang.statusbar.StatusBarCompat;
import com.ljn.callingsimulation.util.DateUtil;

import java.util.Date;

/**
 * Created by 12390 on 2017/9/16.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView themeText;
    private String[] themeItems = {"默认主题", "樱花粉", "炫酷黑"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        FinishListActivity.getInstance().addActivity(this);
        setContentView(R.layout.activity_setting);
        StatusBarCompat.setStatusBarColor(this,0xEFEFF0, true);
        initComponent();
    }

    private void initComponent(){
        findViewById(R.id.setting_cancel_button).setOnClickListener(this);
        findViewById(R.id.about_button).setOnClickListener(this);
        findViewById(R.id.theme_button).setOnClickListener(this);
        themeText = (TextView) findViewById(R.id.theme_text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_cancel_button:
                back();
                break;
            case R.id.about_button:
                showAboutDialog();
                break;
            case R.id.theme_button:
                showThemeDialog();
                break;

        }
    }
    private void back(){
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        finish();
    }

    private void showThemeDialog(){

        AlertDialog.Builder listDialog = new AlertDialog.Builder(SettingActivity.this);
        listDialog.setItems(themeItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeTheme(which);
            }

        });
        AlertDialog alertDialog = listDialog.create();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM);
        lp.height = (int) (d.getHeight() * 0.3);
        lp.width = (int) getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(lp);

        alertDialog.show();
    }

    private void showAboutDialog(){
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        final Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.about_dialog);
        Window window = dialog1.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER_VERTICAL);
        lp.height = (int) (d.getHeight() * 0.8);
        lp.width = (int) getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(lp);
        dialog1.show();
        window.findViewById(R.id.about_dialog_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.cancel();
            }
        });
    }

    private void changeTheme(int which){
        themeText.setText(themeItems[which]);
    }

    @Override
    public void onBackPressed() {
        back();
    }
}
