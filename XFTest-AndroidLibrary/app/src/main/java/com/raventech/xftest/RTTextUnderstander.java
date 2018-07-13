package com.raventech.xftest;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.unity3d.player.UnityPlayer;

/**
 * Created by sergiochan on 16/4/9.
 */
public class RTTextUnderstander {
    // 语义理解对象（语音到语义）。

    static TextUnderstander  mTextUnderstander;
    static String TAG = "RTTextUnderstander";
    static String GameObjectName = "TextUnderstander";

    public static void SetParameter(String var1, String var2)
    {
        mTextUnderstander.setParameter(var1, var2);
    }

    public static void Init()
    {
        mTextUnderstander = TextUnderstander.createTextUnderstander(UnityPlayer.currentActivity.getApplicationContext(), mTextUdrInitListener);
    }

    /**
     * 初始化监听器（文本到语义）。
     */
    private static InitListener mTextUdrInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "textUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.d(TAG,"textUnderstanderListener init error!");
            }
        }
    };

    public static void BeginUnderstand (String text)
    {
        int ret = 0;// 函数调用返回值
        if(mTextUnderstander.isUnderstanding()){
            mTextUnderstander.cancel();
        }else {
            ret = mTextUnderstander.understandText(text, mTextUnderstanderListener);
            if(ret != 0)
            {
                Log.d(TAG, "语义理解失败,错误码:" + ret);
            }
        }
    }

    private static TextUnderstanderListener mTextUnderstanderListener = new TextUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult result) {
            if (null != result) {
                // 显示
                String text = result.getResultString();
                if (!TextUtils.isEmpty(text)) {
                    UnityPlayer.UnitySendMessage(RTTextUnderstander.GameObjectName, "onUnderstoodText",text);
                }
            } else {
                Log.d(TAG, "understander result WRONG!!:null");
            }
        }

        @Override
        public void onError(SpeechError error) {
            // 文本语义不能使用回调错误码14002，请确认您下载sdk时是否勾选语义场景和私有语义的发布
            Log.d(TAG, "onError Code：" + error.getErrorCode());

        }
    };
}
