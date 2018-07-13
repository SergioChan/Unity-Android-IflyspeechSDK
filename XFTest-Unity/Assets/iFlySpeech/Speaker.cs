using System;
using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;

public class Speaker
{
	static AndroidJavaClass AndroidSpeaker = null;

	#if UNITY_EDITOR

	public static void Init(){}

	public static void Start(String text){}

	public static void Stop(){}

	public static void Pause(){}

	public static void Resume(){}

	public static void SetParameter(string var1, string var2){}
	#elif UNITY_ANDROID

	public static void Init()
	{
	AndroidSpeaker = new AndroidJavaClass("com.raventech.xftest.RTSpeaker");
	AndroidSpeaker.CallStatic ("Init");
	}

	public static void Start(String text)
	{
	SetParameter("", "");
	AndroidSpeaker.CallStatic("StartSpeaking",text);
	}

	public static void Stop()
	{
	AndroidSpeaker.CallStatic("cancelSpeaking");
	}

	public static void Pause()
	{
	AndroidSpeaker.CallStatic("pauseSpeaking");
	}

	public static void Resume()
	{
	AndroidSpeaker.CallStatic("resumeSpeaking");
	}

	public static void SetParameter(string var1, string var2)
	{
	AndroidSpeaker.CallStatic("SetParameter", var1, var2);
	}

	#endif


}


