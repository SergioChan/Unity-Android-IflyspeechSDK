using UnityEngine;
using System.Collections;

public class VoiceMgr : MonoBehaviour
{
//    public static string PARAMS = "params";
	static public bool isSpeaking;

	// Method for all the call backs here
	static System.Action<string> callback = null;
	static System.Action<string> speakerErrorcallback = null;
	static System.Action<string> speakerCallback = null;
	static System.Action<string> flyJsonCallback = null;
	static System.Action<string> onVolumeCallback = null;

	static public void SetCallback(System.Action<string> action)
	{
		callback = action;
	}

	static public void SetSpeakerErrorCallback(System.Action<string> action)
	{
		speakerErrorcallback = action;
	}

	static public void SetOnVolumeCallback(System.Action<string> action)
	{
		onVolumeCallback = action;
	}

	static public void SetFlyJsonCallback(System.Action<string> action)
	{
		flyJsonCallback = action;
	}

	static public void SetSpeakerCallback(System.Action<string> action)
	{
		speakerCallback = action;
	}

    // 初始化
    static public void Init()
    {
        GameObject obj = new GameObject("SpeechRecognizer");
        Object.DontDestroyOnLoad(obj);
        Microphone.IsRecording(null);
        obj.AddComponent<VoiceMgr>();

		GameObject s_obj = new GameObject("SpeekerReceiver");
		Object.DontDestroyOnLoad(s_obj);
		s_obj.AddComponent<VoiceMgr>();

		isSpeaking = false;
		Voice.Init ();
		Speaker.Init ();

		// Set voicer name here
		Speaker.SetParameter ("voice_name","vixy");
		Speaker.SetParameter ("speed", "60");
		Speaker.SetParameter ("category", "read_sentence");
		Speaker.SetParameter ("emot","happy");
		Speaker.SetParameter ("pitch","60");

		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		Voice.SetParameter ("vad_bos","10000");
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入，自动停止录音
		Voice.SetParameter ("vad_eos","2000");
    }

    // 
    static public bool isListening
    {
        get
        {
            return Voice.isListening();
        }
    }
		
    public static void SetParameter(string var1, string var2)
    {
        SalfCall(() => 
        {
            Voice.SetParameter(var1, var2);
        });
    }

    public static bool CheckServiceInstalled()
    {
        bool value = false;
        SalfCall(() => 
        {
            value = Voice.CheckServiceInstalled();
        });

        return value;
    }

    static public string GetServiceUrl()
    {
        string url = string.Empty;
        SalfCall(() =>
        {
            url = Voice.GetServiceUrl();
        });

        return url;
    }

    static void SalfCall(System.Action action)
    {
        action();
    }

	static public void SpeakText (string text) 
	{
		if (isSpeaking == false)
		{
			Speaker.Start (text);
		}
	}

    static public int Start()
    {
		Debug.Log ("SpeechRecognizer:Start");
        int code = 0;
        SalfCall(() => 
        {
            code = Voice.Start();
        });

        return code;
    }

    static public void Stop()
    {
		Debug.Log ("SpeechRecognizer:Stop");
        SalfCall(() =>
        {
            Voice.Stop();
        });
    }
		
    static public void Cancel()
    {
		Debug.Log ("SpeechRecognizer:Cancel");
        SalfCall(() =>
        {
            Voice.Cancel();
        });
    }

    void InitEnd(string code)
    {
		Debug.Log ("SpeechRecognizer:InitEnd:" + code);
    }

    void onBeginOfSpeech(string text)
    {
		Debug.Log ("onBeginOfSpeech");
    }

    void onError(string text)
    {
		if (speakerErrorcallback != null)
		{
			speakerErrorcallback(text);
		}
    }

    void onEndOfSpeech(string text)
    {
		Debug.Log ("onEndOfSpeech");
    }

    void onVolumeChanged(string volume)
    {
		if (onVolumeCallback != null) {
			onVolumeCallback (volume);
		}
//		Debug.Log ("onVolumeChanged:" + volume);
    }

    void onResult(string text)
    {
		Stop ();
        if (callback != null)
        {
            callback(text);
        }
    }

	void onUnderstoodText(string text) 
	{
		if (flyJsonCallback != null)
		{
			flyJsonCallback(text);
		}
	}

	// Below is the speaker delegate callbacks

	void onSpeakBegin(string text)
	{
		isSpeaking = true;
	}

	void onCompletedSuccessfully(string text)
	{
		isSpeaking = false;
		if (speakerCallback != null)
		{
			speakerCallback(text);
		}
	}

	void onCompletedError(string error)
	{
		isSpeaking = false;
		Debug.Log ("Speaking error "+ error);
	}
}

