package com.JimmyVo.MoneyManage.DataStructure;

import com.JimmyVo.MoneyManage.Utility.CommonU;

import java.util.ArrayList;

/**
 * Created by Duy Vo on 12/29/2017.
 */

public final class EndingBalanceList {

    public static EndingBalance summary = new EndingBalance("SUMMARY");

    public static ArrayList<EndingBalance> listOfEndingBalance = new ArrayList<>();

    public static void removeAll(){
        listOfEndingBalance.removeAll(listOfEndingBalance);
        summary.removeAll();
    }

    public static void  fill(int idx, final EndingBalance endingBalance){
        while(idx >= listOfEndingBalance.size()){
            listOfEndingBalance.add(new EndingBalance(""));
        }
        listOfEndingBalance.add(idx, endingBalance);
        if(idx+1<listOfEndingBalance.size())
            listOfEndingBalance.remove(idx+1);
    }

    public static void calculate (){
        summary.removeAll();
        for (int idx=0; idx < listOfEndingBalance.size(); idx++) {
            for (int i = 0; i < listOfEndingBalance.get(idx).getSize(); i++) {
                while (i >= summary.getSize()) {
                    summary.add(summary.getSize(), 0);
                }
                summary.setAmount(i, summary.getAmount(i) + listOfEndingBalance.get(idx).getAmount(i));
            }
        }
    }

    public static void remove(int idx ){listOfEndingBalance.remove(idx);}

    public static int getSize(){return listOfEndingBalance.size();}

    public static EndingBalance get (int idx){
        return listOfEndingBalance.get(idx);
    }

    public static EndingBalance get (String description){
        for(int i=0; i < listOfEndingBalance.size(); i++){
            if(listOfEndingBalance.get(i).getDescription().compareTo(description)==0){
                return listOfEndingBalance.get(i);
            }
        }
        return null;
    }


    public ArrayList<String> formViewData(){
        ArrayList<String> result = new ArrayList<>(0);
        result.add(summary.getDescription());
        for(int i=0; i<AccountConfig.getSize(); i++){

            if(AccountConfig.accountConfigList.get(AccountConfig.getorder(i)).isActive) {
                result.add(CommonU.amountFormat(this.summary.seachForAmount(AccountConfig.getAccountCode(i))));
            }
        }
        return result;
    }
}
