package com.JimmyVo.MoneyManage.Utility;

import android.support.v4.util.ObjectsCompat;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Duy Vo on 12/27/2017.
 */

public final class CommonU {
    public static String amountFormat(double amount){
       return ((amount == 0)? "-- " : (amount > 0) ?
                String.format("%,.2f ", amount) :
                String.format("(%,.2f)", -amount));
    }

    public static int indexof(ArrayList<String> array, String string){
        for (int i=0; i<array.size(); i++){
            if(array.get(i).compareTo(string) == 0){
                return i;
            }
        }
        return -1;
    }

    public static boolean amountStringVerify(String string){
        try {
            double amount = Double.parseDouble(
                    string.toString().
                            replaceAll("$", "").
                            replace(",", "").
                            replace(" ", ""));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double parseStringAmount(String string){
        try {
            double amount = Double.parseDouble(
                    string.toString().
                            replaceAll("$", "").
                            replace(",", "").
                            replace(" ", ""));
            return amount;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String verifyNewName(ArrayList<String> currentNameList, ArrayList<String> filter, String newName){
        if(currentNameList.indexOf(newName) == -1){
            if(newName.compareTo("")==0){
                return "This name can't be empty!";
            }

            for (String item: filter){
                if(newName.contains(item)){
                    return "This name contains invalid character!";
                }
            }

            return null;
        }else {
            return "This name is existing!";
        }
    }

    public static ArrayList<String> getStringFilter(){
        ArrayList<String> result = new ArrayList<>(0);
        result.add("\n");
        result.add("\t");
        return result;
    }

}
