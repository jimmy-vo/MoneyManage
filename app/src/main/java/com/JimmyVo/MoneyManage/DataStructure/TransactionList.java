package com.JimmyVo.MoneyManage.DataStructure;

import android.content.Context;

import com.JimmyVo.MoneyManage.Utility.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Duy Vo on 12/15/2017.
 */

public class TransactionList {
    public static  ArrayList<Transaction> listOfTransaction = new ArrayList<Transaction>();

    public static int getSize(){
        return listOfTransaction.size();
    }
    public static Transaction get(int id) {
        return listOfTransaction.get(id);
    }
    public static Transaction clone(int id) {
        Transaction transaction = new Transaction(
                listOfTransaction.get(id).getDate(),
                listOfTransaction.get(id).getDescription());
        for(int i = 0; i<listOfTransaction.get(id).getSize(); i++){
            transaction.add(listOfTransaction.get(id).getAccountCode(i),listOfTransaction.get(id).getAmount(i));
        }
        return transaction;
    }
    public static void remove(int id) {
        listOfTransaction.remove(id);
    }

    public static void removeAll() {
        listOfTransaction.removeAll(listOfTransaction);
    }

    public static void add(ArrayList<String> string){
        Transaction transaction = new Transaction(string);
        listOfTransaction.add(new Transaction(string));
    }

    public static void add_Sort(Transaction input) {
        listOfTransaction.add(input);
        Collections.sort(listOfTransaction, new Comparator<Transaction>() {
            public int compare(Transaction o1, Transaction o2) {
                return o1.getDate().before(o2.getDate()) ? -1 : 1;
            }
        });
    }

    public static void replaceBy(int idx, Transaction input){
        listOfTransaction.remove(idx);
        listOfTransaction.add(idx,input);
    }
}
