//package com.ljn.callingsimulation.util;
//
//import android.content.Context;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import com.ljn.callingsimulation.unit.APIService;
//import com.ljn.callingsimulation.unit.exception.UnitError;
//import com.ljn.callingsimulation.unit.listener.OnResultListener;
//import com.ljn.callingsimulation.unit.model.AccessToken;
//import com.ljn.callingsimulation.unit.model.CommunicateResponse;
//
//import java.util.List;
//
///**
// * Created by 12390 on 2017/9/14.
// */
//public class SmartVoiceUtil {
//    private Context context;
//    private String accessToken;
//    private APIService apiService;
//    private String sessionId;
//    public SmartVoiceUtil(Context context){
//        this.context = context;
//        initAccessToken();
//    }
//    public void initAccessToken(){
//        this.apiService = APIService.getInstance();
//        apiService.init(context);
//        apiService.initAccessToken(new OnResultListener<AccessToken>() {
//            @Override
//            public void onResult(AccessToken result) {
//                accessToken = result.getAccessToken();
//                Log.i("MainActivity", "AccessToken->" + result.getAccessToken());
//
//            }
//
//            @Override
//            public void onError(UnitError error) {
//                Log.i("wtf", "AccessToken->" + error.getErrorMessage());
//            }
//        }, "g2kN8H6sQ3gacQWpxGZpfvjg", "wf2epjDHZwP4FfgBcwAb6EFvYGEzABqX ");
//    }
//    public void sendMessageToSmart(String message){
//        apiService.communicate(new OnResultListener<CommunicateResponse>() {
//            @Override
//            public void onResult(CommunicateResponse result) {
//
//                handleResponse(result);
//            }
//
//            @Override
//            public void onError(UnitError error) {
//
//            }
//        }, 1, message, sessionId);
//    }
//    private void handleResponse(CommunicateResponse result) {
//        if (result != null) {
//            sessionId = result.sessionId;
//
//            //  如果有对于的动作action，请执行相应的逻辑
////            List<CommunicateResponse.Action> actionList = result.actionList;
////            for (CommunicateResponse.Action action : actionList) {
////
////                if (!TextUtils.isEmpty(action.say)) {
////                    StringBuilder sb = new StringBuilder();
////                    sb.append(action.say);
////
////                    Message message = new Message(String.valueOf(id++), cs, sb.toString());
////                    messagesAdapter.addToStart(message, true);
////                    if (action.hintList.size() > 0) {
////                        message.setHintList(action.hintList);
////                    }
////                }
//
////                // 执行自己的业务逻辑
////                if ("start_work_satisfy".equals(action.actionId)) {
////                    Log.i(TAG, "开始扫地");
////                } else if ("stop_work_satisfy".equals(action.actionId)) {
////                    Log.i(TAG, "停止工作");
////                } else if ("move_action_satisfy".equals(action.actionId)) {
////                    Log.i(TAG, "移动");
////                } else if ("timed_charge_satisfy".equals(action.actionId)) {
////                    Log.i(TAG, "定时充电");
////                } else if ("timed_task_satisfy".equals(action.actionId)) {
////                    Log.i(TAG, "定时扫地");
////                } else if ("sing_song_satisfy".equals(action.actionId)) {
////                    Log.i(TAG, "唱歌");
////                }
//            }
//        }
//}
