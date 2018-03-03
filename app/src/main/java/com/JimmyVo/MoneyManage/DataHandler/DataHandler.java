package com.JimmyVo.MoneyManage.DataHandler;

import android.content.Context;

import com.JimmyVo.MoneyManage.DataStructure.Account;
import com.JimmyVo.MoneyManage.DataStructure.AccountConfig;
import com.JimmyVo.MoneyManage.DataStructure.EndingBalance;
import com.JimmyVo.MoneyManage.DataStructure.EndingBalanceList;
import com.JimmyVo.MoneyManage.DataStructure.TransactionList;
import com.JimmyVo.MoneyManage.Utility.CommonU;
import com.JimmyVo.MoneyManage.Utility.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Created by Duy Vo on 12/18/2017.
 */

public class DataHandler extends StorageApp {

    private enum InitializeState{NO_FILE, WRONG_INDEX, OK}

    private static InitializeState initialize(Context context){
        if (StorageApp.Mapping.restore(context)) {
            if(!StorageApp.Accounts.restore(context)) {
                Message.showAlways(context, "Account Config created");
                StorageApp.Accounts.backup(context);
            }

            if(StorageApp.Mapping.list.size() != 0) {
                if ((StorageApp.Mapping.getFileIndex() == -1)||
                        (StorageApp.Mapping.getFileIndex()>=Mapping.list.size())) {
                    Message.showAlways(context, "Invalid file index, set it to the end");
                    ConfigFile.setFileIndex(context, StorageApp.Mapping.list.size() - 1);
                    return InitializeState.WRONG_INDEX;
                }else {
                    return InitializeState.OK;
                }
            }else {
                Message.showDebug(context, "No file found");
                return InitializeState.NO_FILE;
            }

        } else {
            Message.showAlways(context, "Mapping created!");
            StorageApp.Mapping.backup(context);
            //init again
            return initialize(context);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  ConfigFile
    //

    public static class ConfigFile extends StorageApp {

        public static boolean initialize(Context context) {
            DataHandler.initialize(context);
            return (true);
        }

        public static int getSize() {
            return Mapping.list.size();
        }

        public static ArrayList<String> getFileList() {
            return Mapping.list;
        }

        public static int getFileIndex() {
            return Mapping.getFileIndex();
        }

        public static void setFileIndex(Context context, int idx){
            Mapping.setFileIndex(idx);
            Mapping.backup(context);
        }

        public static void swapFileOrder(Context context, int order1, int order2) {
            Collections.swap(Mapping.list, order1, order2);
            Collections.swap(EndingBalanceList.listOfEndingBalance, order1, order2);
            if(Mapping.getFileIndex() == order1){
                ConfigFile.setFileIndex(context,order2);
            }else {
                ConfigFile.setFileIndex(context,order1);
            }
        }

        public static String newNameTextWatcher(Context context, String fileName){
            return (CommonU.verifyNewName(StorageBase.getFileList(context), CommonU.getStringFilter(), fileName));
        }

        public static boolean rename(Context context, int idx, String fileName) {
            if (fileName.compareTo(Mapping.list.get(Mapping.getFileIndex()))==0) {
                return true;
            } else{
                String errorMessage = newNameTextWatcher(context,fileName);

                if(errorMessage != null) {
                    Message.showAlways(context, errorMessage);
                    return false;
                }else {
                    //loadWithVerify index file
                    DataFile.restore(context, StorageApp.Mapping.list.get(idx));

                    if (idx < EndingBalanceList.getSize()) {
                        EndingBalanceList.get(idx).setDescription(fileName);
                    } else {
                        EndingBalanceList.fill(idx, EndingBalance.calculate(fileName, TransactionList.listOfTransaction));
                    }
                    //save to new file
                    DataFile.backup(context, fileName);

                    //delete old file
                    context.deleteFile(StorageApp.Mapping.list.get(idx));

                    //update
                    StorageApp.Mapping.list.add(idx, fileName);
                    StorageApp.Mapping.list.remove(idx + 1);

                    //backup
                    StorageApp.Mapping.backup(context);
                    return true;
                }
            }
        }

        public static void delete(Context context, int idx) {
            context.deleteFile(StorageApp.Mapping.list.get(idx));
            if(idx < EndingBalanceList.getSize()) {
                EndingBalanceList.remove(idx);
            }else {
                Message.showAlways(context,"Could not find ending balance for this file");
            }
            StorageApp.Mapping.list.remove(idx);
            if(Mapping.getFileIndex()>Mapping.list.size()){
                Mapping.setFileIndex(Mapping.list.size()-1);
            }
            StorageApp.Mapping.backup(context);
        }

        public static boolean createEmpty(Context context, String fileName) {
            String errorMessage = newNameTextWatcher(context,fileName);

            if(errorMessage != null) {
                Message.showAlways(context, errorMessage);
                return false;
            }else {
                TransactionList.removeAll();
                DataFile.backup(context, fileName);
                StorageApp.Mapping.list.add(fileName);
                ConfigFile.setFileIndex(context, Mapping.list.size() - 1);
                EndingBalanceList.fill(Mapping.list.size() - 1, new EndingBalance(fileName));
                return true;
            }
        }

        public static boolean loadTransactionAndEndingBalance(Context context, int idx) {
            if (DataFile.restore(context, StorageApp.Mapping.list.get(idx))) {
                EndingBalanceList.fill(idx, EndingBalance.calculate(
                        Mapping.list.get(idx),
                        TransactionList.listOfTransaction));
                return true;
            }
                return false;

        }

        public static void saveEditor(Context context) {
            int fileIndex = Mapping.getFileIndex();

            if (fileIndex >= 0) {
                EndingBalance endingBalance = EndingBalance.calculate(Mapping.list.get(fileIndex), TransactionList.listOfTransaction);
                EndingBalanceList.fill(fileIndex, endingBalance);

                DataFile.backup(context, Mapping.list.get(getFileIndex()));
            } else {
                Message.showAlways(context, "Wrong file pointer");
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Account Configuration
    //
    public static class ConfigAccount extends StorageApp.Accounts {

        public static void initialize(Context context) {
            if(!StorageApp.Accounts.restore(context)) {
                Message.showAlways(context, "Account Config created");
                StorageApp.Accounts.backup(context);
            }
        }

        public static int getSize() {
            return AccountConfig.getSize();
        }

        public static boolean createNew(Context context, String accountName) {
            String errorMessage = newNameTextWatcher(accountName);
            if(errorMessage != null) {
                Message.showAlways(context, errorMessage);
                return false;
            }else {
                AccountConfig.add(new AccountConfig(accountName));
                StorageApp.Accounts.backup(context);
                return true;
            }
        }

        public static void swap(Context context, int idx1, int idx2) {
            AccountConfig.swapOder(idx1,idx2);
            StorageApp.Accounts.backup(context);
        }

        public static void remove(Context context, int order){
            AccountConfig.remove(AccountConfig.getAccountCode(order));
            StorageApp.Accounts.backup(context);
        }

        public static String newNameTextWatcher( String fileName){
            return (CommonU.verifyNewName(AccountConfig.getAccountList(), CommonU.getStringFilter(), fileName));
        }

        public static boolean renameAccount(Context context, int order, String accountName){
            String errorMessage = newNameTextWatcher(accountName);

            if(errorMessage != null) {
                Message.showAlways(context, errorMessage);
                return false;
            }else {
                AccountConfig.setName(order,accountName);
                StorageApp.Accounts.backup(context);
                return true;
            }
        }

        public static String getAccountName(int order){
            return AccountConfig.getAccountName(order);
        }
        public static boolean isCredit (int order){
            return AccountConfig.isCredit (AccountConfig.getAccountCode(order));
        }
        public static void setCredit(Context context, int order, boolean b) {
            AccountConfig.setCredit(AccountConfig.getAccountCode(order), b);
            StorageApp.Accounts.backup(context);
        }
        public static boolean isActive (int order){
            return AccountConfig.isActive (AccountConfig.getAccountCode(order));
        }
        public static void setActive(Context context, int order, boolean b) {
            AccountConfig.setActive(AccountConfig.getAccountCode(order), b);
            StorageApp.Accounts.backup(context);
        }

        public static int getAccountCode(int accountOrder) {
            return AccountConfig.getAccountCode(accountOrder);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Transaction View
    //

    public static class ViewTransaction {
        public static boolean initialize(Context context){
            InitializeState initialize = DataHandler.initialize(context);
            if(initialize != InitializeState.NO_FILE) {
                if (!ConfigFile.loadTransactionAndEndingBalance(context, StorageApp.Mapping.getFileIndex())) {
                    TransactionList.removeAll();
                }
            }else{
                TransactionList.removeAll();
            }

            return true;
        }

        public static String getFileName(){return ConfigFile.getFileList().get(ConfigFile.getFileIndex());}

        public static int getNumberOfTransaction(){return TransactionList.getSize();}

        public static ArrayList<String> getEndingBalance() {
            int fileIndx = ConfigFile.getFileIndex();
            ArrayList<String> strings = new ArrayList<>(0);
            strings.add(" ");
            strings.addAll(EndingBalanceList.get(fileIndx).formViewData());
            strings.set(1,"Ending Balance");
            strings.remove(strings.size()-1);
            return strings;
        }

        public static ArrayList<String> getTransaction(int idx) {
            ArrayList<String> textArray = new ArrayList<> ();
            textArray.addAll(TransactionList.get(idx).formViewData());
            return textArray;
        }

        public static ArrayList<String> getHeading() {
            ArrayList<String> headingText = AccountConfig.formViewData();
            headingText.add(0,"Description");
            headingText.add(0,"Date");
            return headingText;
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Report View
    //
    public static class ViewSummary {
        public static boolean initialize(Context context){
            InitializeState initialize = DataHandler.initialize(context);
            if(initialize != InitializeState.NO_FILE) {
                for(int i = 0; i< StorageApp.Mapping.list.size(); i++){
                    if (!ConfigFile.loadTransactionAndEndingBalance(context,i))
                    {
                        return false;
                    }
                }
                EndingBalanceList.calculate();
            }else{
                EndingBalanceList.removeAll();
            }
            return true;
        }

        public static int getNumberOfFile(){return EndingBalanceList.getSize();}

        public static ArrayList<String> getSummary() {
            return EndingBalanceList.summary.formViewData();
        }

        public static ArrayList<String> getEndingBalance(int idx) {
            ArrayList<String> textArray = new ArrayList<> ();
            textArray.addAll(EndingBalanceList.get(idx).formViewData());

            return textArray;
        }

        public static ArrayList<String> getHeading() {
            ArrayList<String> headingText = AccountConfig.formViewData();
            headingText.add(0,"Description");
            headingText.add("Total");
            return headingText;
        }

        public static void targetFile(Context context , int iD) {
            ConfigFile.setFileIndex(context, iD);
        }

        public static boolean isEmpty(){
            return ((ViewSummary.getNumberOfFile()!=0));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Google Drive
    //
    public static class GDrive {
        private static String delimiter = "\t";

        public static boolean verifyHeading(ArrayList<String> rawData) {
            ArrayList<String> input =  new ArrayList(Arrays.asList(rawData.get(0).split(delimiter,-1)));

            //remove date and De
            input.remove(0);
            input.remove(0);

            if(input.size() != AccountConfig.getSize()){
                return false;
            }
            for (int i = 0; i < input.size(); i++) {
                if (AccountConfig.accountConfigList.get(i).accountName.compareTo(input.get(i)) != 0) {
                    return false;
                }
            }
            return true;
        }

        public static boolean buildFile(ArrayList<String> input) {
            ArrayList <ArrayList<String>> dataRaw = new ArrayList<>(0);

            //split summary by delimiter
            for (String item : input) {
                String[] strings = item.split(delimiter, -1);
                ArrayList<String> element = new ArrayList(Arrays.asList(strings));
                dataRaw.add(element);
            }

            //remove all summary from Data Structure
            TransactionList.removeAll();

            //reform summary and feed to Data Structure
            for (int i = 1; i < dataRaw.size(); i++) {
                ArrayList<String> element = new ArrayList();

                //Date & description
                for (int j = 0; j < 2; j++) {
                    element.add(dataRaw.get(i).get(0));
                    dataRaw.get(i).remove(0);
                }

                int accountCode = 0;
                for (String item : dataRaw.get(i)) {
                    element.add((accountCode++) + "");
                    element.add(item);
                }

                TransactionList.add(element);
            }
            return true;
        }

        public static void importAccountConfig(ArrayList<String> rawData){
            ArrayList<String> input =  new ArrayList(Arrays.asList(rawData.get(0).split(delimiter,-1)));

            //remove date and De
            input.remove(0);
            input.remove(0);

            AccountConfig.removeAll();
            for(String item : input) {
                AccountConfig.add(new AccountConfig(item));
                AccountConfig.setActive(AccountConfig.getSize()-1, true);
            }
        }

        public static boolean importFile(Context context, ArrayList<String> input, String fileName) {
            int oldFileIndex = ConfigFile.getFileIndex();

            if (GDrive.buildFile(input)) {

                StorageApp.Accounts.backup(context);
                int newFileIndex = StorageBase.getFileList(context).indexOf(fileName);
                if (newFileIndex < 0) {
                    StorageApp.Mapping.list.add(fileName);
                    ConfigFile.setFileIndex(context, Mapping.list.size() - 1);
                    newFileIndex = ConfigFile.getFileIndex();
                } else {
                    ConfigFile.setFileIndex(context, newFileIndex);
                }
                EndingBalanceList.fill(newFileIndex, EndingBalance.calculate(fileName, TransactionList.listOfTransaction));
                DataFile.backup(context, fileName);
                return true;
            }else {
                StorageApp.Accounts.restore(context);
                DataHandler.ConfigFile.loadTransactionAndEndingBalance(context,oldFileIndex);
                return false;
            }
        }

        public static ArrayList<String> exportFile(){
            ArrayList<String> output = new ArrayList<String>();

            //title
            String title = "Date" + delimiter;
            title += "Description" + delimiter;
            for (int i = 0; i < AccountConfig.getSize(); i++){
                title += AccountConfig.getName(i) + delimiter ;
            }
            output.add(title.substring(0, title.length()-1) + "\n");

            //content
            for (int transaction = 0; transaction < TransactionList.getSize(); transaction++){
                String[] buffer = new  String[AccountConfig.getSize()];

                String line =
                        TransactionList.get(transaction).getDate("MM/dd/yyyy") + delimiter;
                line += TransactionList.get(transaction).getDescription() + delimiter;

                for (int i = 0; i < TransactionList.get(transaction).getSize(); i++){
                    buffer[TransactionList.get(transaction).getAccountCode(i)] =
                            TransactionList.get(transaction).getAmount(i) + "";
                }

                for (String item : buffer){
                    if (item == null){
                        line += "" + delimiter;
                    }else {
                        line += item + delimiter;
                    }
                }

                output.add(line.substring(0, line.length()-1) + "\n");
            }

            return output;
        }

        public static String getExportFileName(){
            return StorageApp.Mapping.list.get(StorageApp.Mapping.getFileIndex());
        }
    }
}