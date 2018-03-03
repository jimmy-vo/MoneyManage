package com.JimmyVo.MoneyManage.DataStructure;

/**
 * Created by Duy Vo on 12/16/2017.
 */

public class Account {

    private int accountCode;
    private double amount;

    public Account(int accountCode, double amount) {
        this.accountCode = accountCode;
        this.amount = amount;
    }

    public int getAccountCode() {
        return accountCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount){
        this.amount = amount;
    }

}
