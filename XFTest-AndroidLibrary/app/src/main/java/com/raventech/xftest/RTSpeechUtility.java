package com.raventech.xftest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.unity3d.player.UnityPlayer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.lang.String;

/**
 * Created by sergiochan on 16/4/5.
 */
public class RTSpeechUtility {
    static SpeechRecognizer mIat;
    static String TAG = "RTSpeechUtility";
    static String GameObjectName = "SpeechRecognizer";

    public static boolean CheckServiceInstalled()
    {
        return SpeechUtility.getUtility().checkServiceInstalled();
    }

    public static String GetServiceUrl()
    {
        return SpeechUtility.getUtility().getComponentUrl();
    }

    public static void SetParameter(String var1, String var2)
    {
        mIat.setParameter(var1, var2);
    }

    public static void Init()
    {
        SpeechUtility su = SpeechUtility.createUtility(UnityPlayer.currentActivity.getApplicationContext(), "appid=" + "5b461e35");
        Log.w(TAG, "su " + (su == null ? "null" : "no null!"));

        mIat = SpeechRecognizer.createRecognizer(UnityPlayer.currentActivity.getApplicationContext(), null);

        mIat.setParameter("asr_ptt", "0");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "10000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "2000");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

    public static boolean isListening()
    {
        return mIat.isListening();
    }

    public static int Start()
    {
        return mIat.startListening(mRecognizerListener);
    }

    public static void Stop()
    {
        mIat.stopListening();
    }

    public static void Cancel()
    {
        mIat.cancel();
    }

    private static RecognizerListener mRecognizerListener = new RecognizerListener()
    {
        public void onBeginOfSpeech()
        {
            UnityPlayer.UnitySendMessage(RTSpeechUtility.GameObjectName, "onBeginOfSpeech", "");
        }

        public void onError(SpeechError error)
        {
            Log.w(RTSpeechUtility.TAG, "onError");

            UnityPlayer.UnitySendMessage(RTSpeechUtility.GameObjectName, "onError", String.valueOf(error.getErrorCode()));
        }

        public void onEndOfSpeech()
        {
            Log.w(RTSpeechUtility.TAG, "onEndOfSpeech");

            UnityPlayer.UnitySendMessage(RTSpeechUtility.GameObjectName, "onEndOfSpeech", "");
        }

        String Parser(RecognizerResult results)
        {
            String json = results.getResultString();
            StringBuffer ret = new StringBuffer();
            try
            {
                JSONTokener tokener = new JSONTokener(json);
                JSONObject joResult = new JSONObject(tokener);

                JSONArray words = joResult.getJSONArray("ws");
                for (int i = 0; i < words.length(); i++)
                {
                    JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                    JSONObject obj = items.getJSONObject(0);
                    ret.append(obj.getString("w"));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return ret.toString();
        }

        public void onResult(RecognizerResult results, boolean isLast)
        {
            String result_str = Parser(results);
            if (!result_str.equals("")) {
                UnityPlayer.UnitySendMessage(RTSpeechUtility.GameObjectName, "onResult", result_str);
            }
        }

        public void onVolumeChanged(int volume, byte[] data)
        {
            UnityPlayer.UnitySendMessage(RTSpeechUtility.GameObjectName, "onVolumeChanged", String.valueOf(volume));
        }

        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
}

