package com.ljn.callingsimulation.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.ljn.callingsimulation.MainActivity;
import com.ljn.callingsimulation.bean.Calling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 12390 on 2017/9/12.
 */
public class CustomCommunicate extends Thread implements ICommunicate {
    private static final String TAG = "AudioRecord";
    private static final int SAMPLE_RATE_IN_HZ = 8000;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);

    private static final int SLEEP_TIME = 100;//采样间隔，毫秒
    private static final int STANDER_DB = 37;//低于该值，就认为当前状态没有声音
    private boolean isGetVoiceRun = false;//线程控制
    private Object mLock;//锁
    private double volume = 0.0;//分贝值
    private int timeLow = 0;//出现多少次低于标准分贝值。
    private int timeHigh = 0;//出现多少次高于标准分贝值
    private final int totalSample = 20;//每次采样次数
    private final int confidence = 4;//次数多于该值则认为有人在说话
    private List<String> mVoices = new ArrayList<>();//存储声音字符串的列表
    private int index = 0;//当前播放下标
    private AudioRecord mAudioRecord;
    private Calling calling;

    public CustomCommunicate(Calling calling) {
        this.calling = calling;
        isGetVoiceRun = false;
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        if (mAudioRecord == null) {
            Log.i(TAG, "初始化失败");
        }
        getVoicesFromCalling();
    }

    /**
     * 获取声音
     */
    private void getVoicesFromCalling() {
        String[] strings = calling.getContent().split("\n");
        for (String s : strings) {
            mVoices.add(s);
        }
    }


    @Override
    public void run() {
        mAudioRecord.startRecording();
        short[] buffer = new short[BUFFER_SIZE];
        while (isGetVoiceRun) {
            //r是实际读取的数据长度，一般而言r会小于buffersize
            int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
            long v = 0;
            // 将 buffer 内容取出，进行平方和运算
            for (int i = 0; i < buffer.length; i++) {
                v += buffer[i] * buffer[i];
            }
            // 平方和除以数据总长度，得到音量大小。
            double mean = v / (double) r;
            volume = 10 * Math.log10(mean);
            Log.d(TAG, "分贝值:" + volume);
            if (shouldRunNext()) {
                MainActivity.mVoiceUtil.speak(mVoices.get(index));//阻塞线程
                //关闭线程
                if (index == mVoices.size()) {
                    isGetVoiceRun = false;
                }
            }
            // 大概一秒十次
            synchronized (mLock) {
                try {
                    mLock.wait(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mAudioRecord.stop();
        mAudioRecord.release();
        mAudioRecord = null;
    }

    /**
     * 是否应该开始播放下个录音
     *
     * @return ture 是 false 否
     */
    private boolean shouldRunNext() {
        if (volume <= STANDER_DB) {
            timeLow++;
        } else {
            timeHigh++;
        }
        if (timeLow + timeHigh == totalSample) {
            if (timeLow <= confidence) {
                index++;
                return true;
            }
        }
        return false;
    }

    @Override
    public void begin() {
        this.start();
    }
}
