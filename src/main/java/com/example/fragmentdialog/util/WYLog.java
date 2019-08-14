package com.example.fragmentdialog.util;

import android.util.Log;

public class WYLog {

    public static final boolean isDebug = true;

    public static void i(String tag, String msg)
    {
        if (isDebug)  {
            Log.i(tag, msg);
        }
    }

    public static void d( String msg)
    {
        if (isDebug)  {
            Log.d("lyw", msg);
        }
    }

    public static void w(String tag, String msg)
    {
        if (isDebug)  {
            Log.w(tag, msg);
        }
    }


    public static void e( String msg)
    {
        if (isDebug)  {
            Log.e("lyw", msg);
        }
    }

    public static void v(String tag, String msg)
    {
        if (isDebug)  {
            Log.v(tag, msg);
        }
    }
}
