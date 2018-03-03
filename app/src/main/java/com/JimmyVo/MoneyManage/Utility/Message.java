package com.JimmyVo.MoneyManage.Utility;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Duy Vo on 12/25/2017.
 */

public final class Message extends Activity {

    public static void showAlways (Context context, String string){
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    public static void showDebug (Context context, String string){
        //Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
