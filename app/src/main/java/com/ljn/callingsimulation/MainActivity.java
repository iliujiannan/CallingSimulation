package com.ljn.callingsimulation;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import com.ljn.callingsimulation.bean.Calling;
import com.ljn.callingsimulation.util.DateUtil;
import com.ljn.callingsimulation.util.SQLiteOpenHelperUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addCallingButton;
    private TextView settingButton;
    private ListView callingList;
    private SQLiteOpenHelperUtil dbHelper;
    private LinkedList<Calling> callings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_main);
        initDB();
        initComponent();
    }
    private void initComponent(){
        settingButton = (TextView) findViewById(R.id.add_calling_button);
        addCallingButton = (FloatingActionButton) findViewById(R.id.b_add_phone);
        callingList = (ListView)findViewById(R.id.index_list_view);
        addCallingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                MainActivity.this.startActivity(new Intent(MainActivity.this,CallingAdder.class));
            }
        });
        callingList.setAdapter(new MyAdapter(this));
    }
    private void initDB(){
        dbHelper = new SQLiteOpenHelperUtil(MainActivity.this);
        //callings = new LinkedList<Calling>();
        //dbHelper.doInsert(new String[]{"","ljn", "1", "", "2017-09-05 17:00:00", "1", "1", "1", "1"});
        //dbHelper.doDelete(null,null);
        callings = dbHelper.doQuery("datetime("+ SQLiteOpenHelperUtil.args[4]+")>datetime(CURRENT_TIMESTAMP,'localtime')",null);
        System.out.println(callings.size());
    }
    static  class ViewHolder{
        public TextView statrTime;
        public TextView caller;
        public SwitchCompat mSwitch;
    }
    private class MyAdapter extends BaseAdapter{

        private LayoutInflater mInflater = null;

        private MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return callings.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null){

                convertView = mInflater.inflate(R.layout.item,null);
                viewHolder = new ViewHolder();

                viewHolder.statrTime = (TextView) convertView.findViewById(R.id.item_time);

                viewHolder.caller = (TextView) convertView.findViewById(R.id.item_remaining_time);
                viewHolder.mSwitch = (SwitchCompat) convertView.findViewById(R.id.item_switch);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String time = callings.get(position).getStartTime().substring(11,16);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String remaining_time = DateUtil.getDistanceTime(sdf.format(new Date()),callings.get(position).getStartTime());

            viewHolder.statrTime.setText(time);
            viewHolder.caller.setText(callings.get(position).getCaller() + "\n" + "将在" + remaining_time + "后来电.");
            final String isOpen = callings.get(position).getIsOpen();
            if( isOpen.equals( "0")){
                viewHolder.mSwitch.setChecked(false);
            }else{
                viewHolder.mSwitch.setChecked(true);
            }
            viewHolder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                }
            });
            return convertView;
        }
    }
}
