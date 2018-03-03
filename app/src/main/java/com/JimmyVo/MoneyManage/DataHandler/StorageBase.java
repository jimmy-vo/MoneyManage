package com.JimmyVo.MoneyManage.DataHandler;

import android.content.Context;

import com.JimmyVo.MoneyManage.Utility.Message;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Duy Vo on 12/17/2017.
 */

public class StorageBase {

    protected static boolean backup(Context context, String backupName, ArrayList<ArrayList<String>> input, String delimiter){
        try {
            FileOutputStream outputStream = context.openFileOutput(backupName, Context.MODE_PRIVATE);
            //outputStream.write(mytext.getBytes());
            for (ArrayList<String> item : input){
                for(String element : item) {
                    outputStream.write(new String (element+delimiter).getBytes());
                }
                outputStream.write('\n');
            }
            outputStream.close();
            Message.showDebug(context, "Internal Data Saved! - " + backupName);
            return true;
        } catch (IOException e) {;
            Message.showAlways(context, e.getMessage());
            return false;
        }
    }

    public final static boolean createEmptyFile(Context context, String fileName){
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write("".getBytes());
            outputStream.close();
            Message.showDebug(context,  fileName+ " created!");
            return true;
        } catch (IOException e) {
            Message.showAlways(context, e.getMessage());
            return false;
        }
    }


    protected static boolean restore(Context context, String restoreName, ArrayList<ArrayList<String>> output, String delimiter){
        try {
            FileInputStream fis = context.openFileInput(restoreName);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            String line = null;

            while((line = r.readLine())!= null){
                String[] strings = line.split(delimiter,-1);
                ArrayList<String> element = new ArrayList( Arrays.asList(strings));
                output.add(element);
            }

            r.close();
            Message.showDebug(context, "Internal Data loaded! - " + restoreName);
            return true;
        } catch (IOException e) {
            Message.showAlways(context, e.getMessage());
            return false;
        }
    }

    public static ArrayList<String> getFileList(Context context){
        String [] listFile = context.fileList();
        return new ArrayList<>( Arrays.asList(listFile));
    }

    protected static void deleteFile(Context context, String fileName) {
        context.deleteFile(fileName);
    }
}
