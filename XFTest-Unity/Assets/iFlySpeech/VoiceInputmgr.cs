using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class VoiceInputmgr : MonoBehaviour {

	// Use this for initialization
	void Start () {

		// 全局单例来维护
		VoiceMgr.Init();
		VoiceMgr.SetCallback(OnResult);
		VoiceMgr.SetSpeakerErrorCallback(OnSpeechError);
		VoiceMgr.SetFlyJsonCallback (OnFlyJsonResult);
		VoiceMgr.SetSpeakerCallback (OnSpeakerCompleted);
		//VoiceMgr.SetOnVolumeCallback (OnMicroPhoneVolumeChanged);

		VoiceMgr.SetParameter("engine_type", "cloud");
		VoiceMgr.SetParameter("audio_format", "pcm");
	}
	
	// Update is called once per frame
	void Update () {
		
	}

	public void fuck(){
        Debug.Log ("Button Clicked. TestClick.");
        VoiceMgr.SpeakText ("我草拟马币");
    }

    /// <summary>
	/// IflyTek result for input voice
	/// </summary>
	/// <param name="text">Text.</param>
	void OnResult(string text)
	{
		Debug.LogWarning ("[Iflytek] OnResult: " + text);
	}

	// Speech recognizer returns error, here we need to make sure everything works fine
	void OnSpeechError(string code)
	{
		Debug.LogWarning ("[Iflytek] OnSpeechError: " + code);

		if (code == "10118") {
			// no input, if still focused, then restart listening, if not focused
			// restart listening
			if (VoiceMgr.isListening == false)
			{
				VoiceMgr.Start();
			}
		}
	}

	void OnFlyJsonResult(string text) {
		Debug.LogWarning ("[Iflytek] OnFlyJsonResult: " + text);
	}

	void OnSpeakerCompleted(string text) 
	{
		Debug.LogWarning ("[Iflytek] OnSpeakerCompleted: " + text);
		if (VoiceMgr.isListening == false)
		{
			int code = VoiceMgr.Start();
			Debug.LogWarning ("[Iflytek] Start Listening");
		}
	}
}
