package com.JimmyVo.MoneyManage.DataStructure;

import com.JimmyVo.MoneyManage.Utility.CommonU;

import java.util.ArrayList;

/**
 * Created by Duy Vo on 12/29/2017.
 */

public class EndingBalance extends AccountList{

    private String description;

    public EndingBalance(String description) {
        super();
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static EndingBalance calculate(String description, ArrayList<Transaction> listOfTransaction){
        EndingBalance newEndingBalance = new EndingBalance(description);

        double[] buffer = new   double[AccountConfig.getSize()];
        for(Transaction transaction: listOfTransaction){
            for (int i=0; i<buffer.length; i++){
                buffer[i] += transaction.seachForAmount(i);
            }
        }

        for (int i=0; i<buffer.length; i++){
            newEndingBalance.add(i,buffer[i]);
        }

        return newEndingBalance;
    }

    @Override
    public ArrayList<String> formViewData(){
        double summation = 0;
        ArrayList<String> result = new ArrayList<>(0);
        result.add(this.getDescription());
        for(int i=0; i<AccountConfig.getSize(); i++){
            if(AccountConfig.accountConfigList.get(AccountConfig.getorder(i)).isActive) {
                int accountCode = AccountConfig.getAccountCode(i);
                double amount = this.seachForAmount(accountCode);
                boolean isCredit = AccountConfig.isCredit(accountCode);
                summation += ((isCredit) ? -amount : amount);
                result.add(CommonU.amountFormat(amount));
            }
        }
        result.add(CommonU.amountFormat(summation));
        return result;
    }

}
