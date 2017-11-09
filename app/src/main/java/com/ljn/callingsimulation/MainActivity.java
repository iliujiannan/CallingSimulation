package com.ljn.callingsimulation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ljn.callingsimulation.bean.Calling;
import com.ljn.callingsimulation.util.DateUtil;
import com.ljn.callingsimulation.util.SQLiteOpenHelperUtil;
import com.ljn.callingsimulation.util.VoiceUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addCallingButton;
    private TextView settingButton;
    private ListView callingList;
    private SQLiteOpenHelperUtil dbHelper;
    private MyAdapter myAdapter;
    public static Vector<Calling> callings = null;
    public static VoiceUtil mVoiceUtil;
    private TextView noContentText;
    private Boolean EXIT = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        FinishListActivity.getInstance().addActivity(this);

        mVoiceUtil = new VoiceUtil(MainActivity.this);
        checkPermission();
        setContentView(R.layout.activity_main);

        startService(new Intent(this,MainService.class));
        initDB();
        initComponent();

        /**
         * bgq
         */
        isDestroy = false;
        // 获得AudioManager对象
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//音乐音量,如果要监听铃声音量变化，则改为AudioManager.STREAM_RING
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        onVolumeChangeListener();
        /**
         * bgq
         */
    }

    @Override
    protected void onDestroy() {
        isDestroy = true;
        super.onDestroy();
    }

    private void checkPermission(){
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.RECORD_AUDIO", "com.ljn.callingsimulation"));
        if(!permission){
            Toast.makeText(getApplicationContext(), "未获得录音权限，请添加权限后重新打开此应用",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

    }
    private void initComponent(){
        noContentText = (TextView) findViewById(R.id.main_no_content_hint);
        checkText();
        settingButton = (TextView) findViewById(R.id.add_calling_button);
        addCallingButton = (FloatingActionButton) findViewById(R.id.b_add_phone);
        callingList = (ListView)findViewById(R.id.index_list_view);
        addCallingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this,CallingAdderActivity.class));
                finish();
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                finish();
            }
        });
        myAdapter = new MyAdapter(this);
        callingList.setAdapter(myAdapter);
        callingList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                System.out.println("del");
                AlertDialog.Builder delDialog = new AlertDialog.Builder(MainActivity.this);
                delDialog.setNeutralButton("删除来电", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String[] values = {String.valueOf(MainActivity.callings.get(position).getCallingId())};
                        MainActivity.callings.remove(position);
                        dbHelper.doDelete("calling_id=?", values);
                        myAdapter.notifyDataSetChanged();
                        checkText();


                    }
                }).show();
                return true;
            }
        });
        new Thread(){
            @Override
            public void run() {
                boolean RUN_STATE = true;
                while(RUN_STATE) {
                    if(callings.isEmpty())
                        break;
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callingList.setAdapter(new MyAdapter(MainActivity.this));
                        }
                    });
                }
            }
        }.start();

    }
    private void checkText(){
        if(callings.size()>=1){
            noContentText.setVisibility(View.INVISIBLE);
        }else{
            noContentText.setVisibility(View.VISIBLE);
        }
    }
    private void initDB(){
        dbHelper = new SQLiteOpenHelperUtil(MainActivity.this);
        //callings = new LinkedList<Calling>();
        //dbHelper.doInsert(new String[]{"","ljn", "1", "", "2017-09-05 17:00:00", "1", "1", "1", "1"});
        //dbHelper.doDelete(null,null);
        callings = dbHelper.doQuery("datetime("+ SQLiteOpenHelperUtil.args[4]+")>datetime(CURRENT_TIMESTAMP,'localtime')",null);
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
            return MainActivity.callings.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
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
            String time = MainActivity.callings.get(position).getStartTime().substring(11,16);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String remaining_time = DateUtil.getDistanceTime(sdf.format(new Date()),callings.get(position).getStartTime());

            viewHolder.statrTime.setText(time);
            viewHolder.caller.setText(MainActivity.callings.get(position).getCaller() + "\n" + "将在" + remaining_time + "后来电.");
            if( MainActivity.callings.get(position).getIsOpen().equals( "0")){
                viewHolder.mSwitch.setChecked(false);
            }else{
                viewHolder.mSwitch.setChecked(true);
            }
            viewHolder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        MainActivity.callings.get(position).setIsOpen("1");
                    }else{
                        MainActivity.callings.get(position).setIsOpen("0");
                    }
                    String[] values = new String[]{"","","","","","","","",MainActivity.callings.get(position).getIsOpen(),""};
                    String[] selectionValues = {String.valueOf(MainActivity.callings.get(position).getCallingId())};
                    dbHelper.doUpdate(values,"calling_id=?",selectionValues);
                }
            });
            return convertView;
        }
    }
    public class MsgReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String name = "";
            System.out.println(MainActivity.callings.size());
            Integer id = intent.getIntExtra("id", 0);
            for(Calling calling: callings){
                if(calling.getCallingId()==id){
                    name=calling.getCaller();
                    callings.remove(calling);
                    break;
                }
            }
            System.out.println(MainActivity.callings.size());


//            context.startActivity(new Intent(context,CallActivity.class).putExtra("name",name));
        }
    }

    @Override
    public void onBackPressed() {
        if(!EXIT) {
            Toast.makeText(getApplicationContext(), "再次点击退出模拟来电",
                    Toast.LENGTH_SHORT).show();
            EXIT = true;
        }else{
            finish();
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EXIT = false;
            }
        }.start();
    }

    /**
     * 当前音量
     */
    private int currentVolume;
    /**
     * 控制音量的对象
     */
    public AudioManager mAudioManager;
    /**
     * 系统最大音量
     */
    private int maxVolume;
    /**
     * 确保关闭程序后，停止线程
     */
    private boolean isDestroy;

    /**
     * 监听音量按键的线程
     */
    private Thread volumeChangeThread;
    private int count = 0;
    private SQLiteOpenHelperUtil sqLiteOpenHelperUtil;

    /**
     * 持续监听音量变化 说明： 当前音量改变时，将音量值重置为最大值减2
     */
    public void onVolumeChangeListener() {
        sqLiteOpenHelperUtil = new SQLiteOpenHelperUtil(MainActivity.this);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        final int[] i = {1};
        volumeChangeThread = new Thread() {
            public void run() {
                while (true) {
                    boolean isDerease = false;
                    // 监听的时间间隔
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        System.out.println("error in onVolumeChangeListener Thread.sleep(20) " + e.getMessage());
                    }

                    if (currentVolume < mAudioManager.getStreamVolume(AudioManager.STREAM_RING)) {
                        count++;
                        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
                        // 设置音量等于 maxVolume-2的原因是：当音量值是最大值和最小值时，按音量加或减没有改变，所以每次都设置为固定的值。
                    }
                    if (currentVolume > mAudioManager.getStreamVolume(AudioManager.STREAM_RING)) {
                        count--;
                        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
                    }

                    if (count >= 1 || count <= -1) {
                        Date now = new Date();
                        String time = DateUtil.dateToString(new Date(now.getTime() + (i[0]++)*60 * 1000));
                        System.out.println("按下了音量+");
                        count = 0;
                        String[] values = new String[10];
                        values[0] = "";
                        values[1] = "老王大哥";
                        values[2] = "自定义对话";
                        values[3] = "赶紧回来吃饭\n快点\n再见";
                        values[4] = time;
                        values[5] = "男声";
                        values[6] = "1";
                        values[7] = "1";
                        values[8] = "1";
                        values[9] = "1";
                        sqLiteOpenHelperUtil.doInsert(values);
                        try {
                            Thread.sleep(1000*60*5);
                        } catch (InterruptedException e) {
                            System.out.println("error in onVolumeChangeListener Thread.sleep(20) " + e.getMessage());
                        }
                    }
                }
            }
        };
        volumeChangeThread.start();
    }

    private String calculate(int mi) {
        //通过repeat hour minute 计算响铃的yyyy-MM-dd HH：mm：ss
        Integer day = DateUtil.getIndOfDay("每天");
        Integer nowDay = DateUtil.getIndOfDay(DateUtil.getWeekOfDate(new Date()));
        Integer c = 0;
        if (day != -1 && nowDay != -1) {
            if (day >= nowDay) {
                c = day - nowDay;
            } else {
                c = 7 + day - nowDay;
            }
        }
        String strNowDay = DateUtil.dateToString(new Date(), "yyyy-MM-dd");
        Date nowDate = DateUtil.stringToDate(strNowDay + " 00:00:00");
        Date targetDate = new Date(nowDate.getTime() + c * 24 * 60 * 60 * 1000 + mi * 60 * 1000);
        String targetStringDate = DateUtil.dateToString(targetDate);
        return targetStringDate;
    }

}
