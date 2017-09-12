package com.ljn.callingsimulation.util;

import android.content.Context;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;

/**
 * Created by 12390 on 2017/9/5.
 */
public class VoiceUtil {

    //written by hsl 2017-09-05
    private SpeechSynthesizer mSpeechSynthesizer;
    public VoiceUtil(Context context){

        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(context);
        this.mSpeechSynthesizer.setAppId("10090455"/*这里只是为了让Demo运行使用的APPID,请替换成自己的id。*/);
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey("Y85i0lnghdFKZxUtWTB3jnrl",
                "pjPQ3Pcq51WxKbtwV94bVHr8dapPxqEn"/*这里只是为了让Demo正常运行使用APIKey,请替换成自己的APIKey*/);
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 授权检测接口(只是通过AuthInfo进行检验授权是否成功。)
        // AuthInfo接口用于测试开发者是否成功申请了在线或者离线授权，如果测试授权成功了，可以删除AuthInfo部分的代码（该接口首次验证时比较耗时），不会影响正常使用（合成使用时SDK内部会自动验证授权）

        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.ONLINE);

    }

    public Integer speak(String text) {
        int result = this.mSpeechSynthesizer.speak(text);
        return result;
    }
}
