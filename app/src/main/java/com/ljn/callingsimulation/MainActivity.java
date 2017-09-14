package com.ljn.callingsimulation;

import android.content.*;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
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
    }

    @Override
    protected void onDestroy() {
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
                finish();
                MainActivity.this.startActivity(new Intent(MainActivity.this,CallingAdderActivity.class));
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            System.out.println("feikong");
        }else{
            noContentText.setVisibility(View.VISIBLE);
            System.out.println("kong");
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
}
