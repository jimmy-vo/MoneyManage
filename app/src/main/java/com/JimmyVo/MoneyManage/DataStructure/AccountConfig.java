package com.JimmyVo.MoneyManage.DataStructure;

import java.util.ArrayList;

/**
 * Created by Duy Vo on 12/18/2017.
 */

public class AccountConfig {

    public static ArrayList<AccountConfig> accountConfigList = new ArrayList<>(0);

    public String accountName;
    public boolean isCredit, isActive;
    public int order;


    public AccountConfig(String accountName) {
        this.accountName = accountName;
    }


    public AccountConfig(String accountName, boolean isCredit) {
        this(accountName);
        this.isCredit = isCredit;
        this.isActive = true;
    }

    public AccountConfig(String accountName, boolean isCredit, int order) {
        this(accountName, isCredit);
        this.order = order;
    }

    public AccountConfig(String accountName, boolean isCredit, int order, boolean isActive) {
        this(accountName, isCredit, order);
        this.isActive = isActive;
    }

    public static int getSize() {
        return accountConfigList.size();
    }

    public static void add(AccountConfig accountConfig) {
        accountConfig.order = accountConfigList.size();
        accountConfigList.add(accountConfig);
    }

    public static void add(ArrayList<String> arrayList) {
        AccountConfig accountConfig = new AccountConfig(
                arrayList.get(0),
                Boolean.parseBoolean(arrayList.get(1)),
                Integer.parseInt(arrayList.get(2)),
                Boolean.parseBoolean(arrayList.get(3)));
        accountConfigList.add(accountConfig);
    }

    public static void remove(int AccountCode) {
        int order = AccountConfig.getorder(AccountCode);
        accountConfigList.remove(AccountCode);

        //Addjust order
        for (AccountConfig item : accountConfigList) {
            if (item.order >= order) {
                item.order--;
            }
        }
    }

    public static void swapOder(int order1, int order2) {
        int accountCode1 = 0, accountCode2 = 0;
        //looking for index of order1
        for (AccountConfig item : accountConfigList) {
            if (item.order == order1)
                accountCode1 = accountConfigList.indexOf(item);
            if (item.order == order2)
                accountCode2 = accountConfigList.indexOf(item);
        }
        accountConfigList.get(accountCode1).order = order2;
        accountConfigList.get(accountCode2).order = order1;
    }

    public static void setCredit(int accountCode, boolean isDebit) {
        accountConfigList.get(accountCode).isCredit = isDebit;
    }

    public static boolean isCredit(int accountCode) {
        return accountConfigList.get(accountCode).isCredit;
    }

    public static void setActive(int accountCode, boolean isActive) {
        accountConfigList.get(accountCode).isActive = isActive;
    }

    public static boolean isActive(int accountCode) {
        return accountConfigList.get(accountCode).isActive;
    }

    public static String getName(int accountCode) {
        return accountConfigList.get(accountCode).accountName;
    }

    public static int getorder(int accountCode) {
        return accountConfigList.get(accountCode).order;
    }

    public static int getAccountCode(int order) {
        for (int i = 0; i < accountConfigList.size(); i++) {
            if (accountConfigList.get(i).order == order)
                return i;
        }
        return -1;
    }

    public static String getAccountName(int order) {
        for (int i = 0; i < accountConfigList.size(); i++) {
            if (accountConfigList.get(i).order == order)
                return accountConfigList.get(i).accountName;
        }
        return null;
    }

    public static void setName(int order, String newName){
        for (int i = 0; i < accountConfigList.size(); i++) {
            if (accountConfigList.get(i).order == order){
                 accountConfigList.get(i).setName(newName);
                return;
            }
        }
    }

    public static ArrayList<String> getAccountList(){
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < accountConfigList.size(); i++) {
            result.add(accountConfigList.get(i).accountName);
        }
        return result;
    }

    public void setName(String newName) {
        this.accountName = newName;
    }

    public static void removeAll() {
        accountConfigList.removeAll(accountConfigList);
    }

    public static ArrayList<String> formViewData() {
        ArrayList<String> headingText = new ArrayList<>();
        for (int order = 0; order < getSize(); order++) {
            if(AccountConfig.accountConfigList.get(AccountConfig.getorder(order)).isActive) {
                headingText.add(getAccountName(order));
            }
        }
        return headingText;
    }
}