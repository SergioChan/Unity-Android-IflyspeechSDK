using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;

public class Voice
{
    static AndroidJavaClass SpeechRecognizer = null;

    public static bool isListening()
    {
        return SpeechRecognizer == null ? false : SpeechRecognizer.CallStatic<bool>("isListening");
    }

    public static void Init()
    {
        SpeechRecognizer = new AndroidJavaClass("com.raventech.xftest.RTSpeechUtility");
        SpeechRecognizer.CallStatic("Init");
	}

    public static int Start()
    {
        SetParameter("", "");
        return SpeechRecognizer.CallStatic<int>("Start");
    }

    public static void Stop()
    {
        SpeechRecognizer.CallStatic("Stop");
    }

    public static void Cancel()
    {
        SpeechRecognizer.CallStatic("Cancel");
    }

    public static void SetParameter(string var1, string var2)
    {
        SpeechRecognizer.CallStatic("SetParameter", var1, var2);
    }

    public static bool CheckServiceInstalled()
    {
        return SpeechRecognizer.CallStatic<bool>("CheckServiceInstalled");
    }

    public static string GetServiceUrl()
    {
        return SpeechRecognizer.CallStatic<string>("GetServiceUrl");
    }
}
