package com.JimmyVo.MoneyManage.DataHandler;

import android.content.Context;

import com.JimmyVo.MoneyManage.DataStructure.AccountConfig;
import com.JimmyVo.MoneyManage.DataStructure.Transaction;
import com.JimmyVo.MoneyManage.DataStructure.TransactionList;
import com.JimmyVo.MoneyManage.Utility.Message;

import java.util.ArrayList;

/**
 * Created by Duy Vo on 12/28/2017.
 */

public class StorageApp {
    protected static class Mapping extends StorageBase {

        protected static ArrayList<String> list = new ArrayList<>(0);
        private static int index = -1;
        private final static String delimiter = "\t";
        private final static String fileName = "MappingFile";

        protected static void backup(Context context) {
            ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>(1);
            output.add(new ArrayList<String>());

            for(String item: Mapping.list){
                output.get(0).add(item);
            }
            output.get(0).add(index +"");


            StorageBase.backup(context, fileName, output, delimiter);
        }

        protected static boolean restore(Context context) {
            ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
            if (StorageBase.restore(context, fileName, input, delimiter)) {
                if ((input.size() > 0) && (input.get(0).size() > 0)) {
                    Mapping.list.removeAll(Mapping.list);
                    for (String item : input.get(0)) {
                        Mapping.list.add(item);
                    }
                    Mapping.list.remove("");
                    try {
                        index = Integer.parseInt(Mapping.list.get(Mapping.list.size()-1));
                    }catch (NumberFormatException e){
                        Message.showAlways(context,"Could not parse file index");
                        return false;
                    }
                    Mapping.list.remove(Mapping.list.size()-1);
                }
                return true;
            }
            return false;
        }

        protected static ArrayList<String> readFileList(Context context){
            ArrayList<String> result = getFileList(context);
            result.remove(Accounts.fileName);
            result.remove(Mapping.fileName);
            return result;
        }

        protected static void setFileIndex(int idx){
            index = idx;
        }

        protected static int getFileIndex (){
            return index;
        }
    }

    protected static final class DataFile extends StorageBase {
        private final static String delimiter = "\t";
        private static String fileName = "temp";


        protected static void backup(Context context, String fileName){
            DataFile.fileName = fileName;
            DataFile.backup(context);
        }

        private static void backup(Context context) {
            ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();

            //add transactions to buffer
            for (Transaction item : TransactionList.listOfTransaction)
                output.add(item.toStringArray());

            //now write
            backup(context, fileName, output, delimiter);
        }

        protected static boolean restore(Context context, String fileName) {
            DataFile.fileName = fileName;
            return DataFile.restore(context);
        }

        private static boolean restore(Context context) {
            ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
            TransactionList.removeAll();
            if(restore(context, fileName, input, delimiter)) {
                if ((input != null) && (input.size() > 0)) {
                    for (ArrayList<String> item : input) {
                        TransactionList.add(item);
                    }
                }
            }
            return true;
        }
    }

    protected static class Accounts extends StorageBase {
        private final static String delimiter = "\t";
        public final static String fileName = "ConfigAccount";

        protected static void backup(Context context) {
            ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();

            int idx = 0;
            for (AccountConfig item : AccountConfig.accountConfigList) {
                output.add(new ArrayList<String>(0));
                output.get(idx).add(item.accountName);
                output.get(idx).add(item.isCredit + "");
                output.get(idx).add(item.order + "");
                output.get(idx).add(item.isActive + "");
                idx++;
            }
            StorageBase.backup(context, fileName, output, delimiter);
        }

        protected static boolean restore(Context context) {
            ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();

            if (StorageBase.restore(context, fileName, input, delimiter)) {

                if (input != null) {
                    AccountConfig.removeAll();
                    for (ArrayList<String> item : input) {
                        AccountConfig.add(item);
                    }
                }
                return true;

            }
            return false;
        }
    }
}
