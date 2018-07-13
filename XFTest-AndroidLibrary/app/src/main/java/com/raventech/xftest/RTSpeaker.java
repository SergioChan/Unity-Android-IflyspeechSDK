package com.raventech.xftest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.unity3d.player.UnityPlayer;

/**
 * Created by sergiochan on 16/4/6.
 */
public class RTSpeaker {
    static SpeechSynthesizer mTts;;
    static String TAG = "RTSpeaker";
    static String GameObjectName = "SpeekerReceiver";

    static String speakText = "";
    static String voicer = "xiaoyan";

    // 缓冲进度
    static int mPercentForBuffering = 0;
    // 播放进度
    static int mPercentForPlaying = 0;

    public static void SetParameter(String var1, String var2) { mTts.setParameter(var1, var2); }

    public static void Init()
    {
        SpeechUtility su = SpeechUtility.createUtility(UnityPlayer.currentActivity.getApplicationContext(), "appid=" + "5b461e35");
        Log.w(TAG, "su " + (su == null ? "null" : "no null!"));

        mTts = SpeechSynthesizer.createSynthesizer(UnityPlayer.currentActivity.getApplicationContext(), mTtsInitListener);
        setParam();
        //mTts.destroy(); 需要在什么时候调用？应该暂时不需要吧 搞成 static 之后了就是单例了吧
    }

    public static void StartSpeaking(String text) {
        // 设置参数
        int code = mTts.startSpeaking(text, mTtsListener);
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
//			int code = mTts.synthesizeToUri(text, path, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            speakText = "";
            if(code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED){
                //未安装则跳转到提示安装页面
                Log.d(TAG,"Speaking ERROR_COMPONENT_NOT_INSTALLED");
            }else {
                Log.d(TAG,"Speaking error with code :" + code);
            }
        } else {
            speakText = text;
        }
    }

    public static void cancelSpeaking () {
        mTts.stopSpeaking();
    }

    public static void pauseSpeaking () {
        mTts.pauseSpeaking();
    }

    public static void resumeSpeaking () {
        mTts.resumeSpeaking();
    }

    /**
     * 初始化监听。
     */
    private static InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * 合成回调监听。
     */
    private static SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            UnityPlayer.UnitySendMessage(RTSpeaker.GameObjectName, "onSpeakBegin", speakText);
            Log.d(TAG, "onSpeakBegin");
        }

        @Override
        public void onSpeakPaused() {
            Log.d(TAG, "onSpeakPaused");
        }

        @Override
        public void onSpeakResumed() {
            Log.d(TAG, "onSpeakResumed");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
            Log.d(TAG, "Buffering percent with : " + mPercentForBuffering + "/" + mPercentForPlaying);
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
            Log.d(TAG, "Playing percent with : " + mPercentForBuffering + "/" + mPercentForPlaying);
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                UnityPlayer.UnitySendMessage(RTSpeaker.GameObjectName, "onCompletedSuccessfully", speakText);
                Log.d(TAG, "Playing Completed");
            } else if (error != null) {
                UnityPlayer.UnitySendMessage(RTSpeaker.GameObjectName, "onCompletedError", error.getMessage());
                Log.d(TAG, "Playing Error :" + error.getMessage());
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**
     * 参数设置
     * @param param
     * @return
     */
    private static void setParam(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数

        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "60");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "60");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");

        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }
}
