package com.JimmyVo.MoneyManage.DataStructure;

import com.JimmyVo.MoneyManage.Utility.CommonU;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Duy Vo on 12/15/2017.
 */

public class AccountList {
    private ArrayList<Account> listOfAccount = new ArrayList<Account>();


    public AccountList() {
        this.listOfAccount = new ArrayList<>(0);
    }

    //////////////////////////////////////////////////////////
    // Acoount
    //

    public double getAmount (int idx){
        return this.listOfAccount.get(idx).getAmount();
    }

    public int getAccountCode (int idx){
        return this.listOfAccount.get(idx).getAccountCode();
    }

    public String getAccountName (int idx){return AccountConfig.getName(this.listOfAccount.get(idx).getAccountCode());}

    public int getSize(){return this.listOfAccount.size();}

    public void setAmount(int idx, double amount){
        this.listOfAccount.get(idx).setAmount(amount);
    }

    public double seachForAmount (int accountCode){
        for (Account item: this.listOfAccount) {
            if (item.getAccountCode() == accountCode){
                return item.getAmount();
            }
        }
        return 0;
    }

    //////////////////////////////////////////////////////////
    // Manage arraylist
    //
    public ArrayList<String> generateEmptyAccountList(){
        ArrayList<String> result = AccountConfig.getAccountList();
        for (int i=0; i< this.listOfAccount.size(); i++){
            result.remove(AccountConfig.getName(this.listOfAccount.get(i).getAccountCode()));
        }
        return result;
    }
    public ArrayList<String> generateEmptyAccountListIncludeThis(int idx){
        ArrayList<String> result = AccountConfig.getAccountList();
        for (int i=0; i< this.listOfAccount.size(); i++){
            if(i!=idx) {
                result.remove(AccountConfig.getName(this.listOfAccount.get(i).getAccountCode()));
            }
        }
        return result;
    }

    public void set(int idx, int accountCode, double amount){
        if(idx<this.getSize())
        this.listOfAccount.set(idx, new Account(accountCode,amount));
    }

    public void add(int accountCode, double amount) {
        this.listOfAccount.add(new Account(accountCode, amount));
    }


    public boolean add_Sort(int accountCode, double amount){
        for(Account item : this.listOfAccount){
            if(accountCode == item.getAccountCode()){
                return false;
            }
        }
        if (accountCode< AccountConfig.getSize()) {
            this.listOfAccount.add(new Account(accountCode, amount));
            return true;
        }

        Collections.sort(this.listOfAccount, new Comparator<Account>() {
            public int compare(Account o1, Account o2) {
                return ((o1.getAccountCode()) > (o2.getAccountCode()))
                        ? 1 : -1;
            }
        });
        return false;
    }

    public boolean removeIndex(int index){
        if (index<this.listOfAccount.size()){
            this.listOfAccount.remove(index);
            return true;
        }
        return false;
    }

    public void removeAll(){
        this.listOfAccount.removeAll(this.listOfAccount);
    }


    public void verify(){
        for(int i=0; i< this.listOfAccount.size(); i++){
            if(this.listOfAccount.get(i).getAmount()==0){
                this.listOfAccount.remove(this.listOfAccount.indexOf(this.listOfAccount.get(i)));
            }
        }
    }

    public ArrayList<String> formViewData(){
        ArrayList<String> result = new ArrayList<>(0);
        for(int i=0; i<AccountConfig.getSize(); i++){
            if(AccountConfig.accountConfigList.get(AccountConfig.getorder(i)).isActive)
                result.add(CommonU.amountFormat(this.seachForAmount(AccountConfig.getAccountCode(i))));
        }
        return result;
    }

}
