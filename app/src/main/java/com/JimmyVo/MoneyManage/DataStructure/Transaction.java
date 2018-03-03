package com.JimmyVo.MoneyManage.DataStructure;

import com.JimmyVo.MoneyManage.Utility.CommonU;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Duy Vo on 12/15/2017.
 */

public class Transaction extends AccountList{

    private Date date = new java.util.Date();
    private String description = new String();

    //////////////////////////////////////////////////////////
    // new constructor
    //

    public Transaction()
    {
        super();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        this.setDate(new Date(year-1900,month,day));
    }

    public Transaction(String description){
        super();
        this.setDescription(description);
    }

    public Transaction(Date date, String description){
        this(description);
        this.setDate(date);
    }

    public Transaction(String date, String description){
        this(description);
        this.setDate(date);
    }


    public Transaction(ArrayList<String> array) {
        this( array.get(0), array.get(1));
        for(int i = 2; i<array.size()-1; i+=2){
            if(false == array.get(i+1).equals("")){
                if  (Double.parseDouble(array.get(i+1))!= 0) {
                      this.add_Sort(Integer.parseInt(array.get(i)),
                        Double.parseDouble(array.get(i + 1)));
                  }
            }
        }
    }

    //////////////////////////////////////////////////////////
    // Date
    //

    public String getDate(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CANADA);
        return sdf.format(this.date);
    }

    public java.util.Date getDate(){return this.date;}

    public void setDate(String year, String month, String day) {
        this.date = new Date(Integer.parseInt(year)-1900,Integer.parseInt(month)-1,Integer.parseInt(day));;
    }

    public void setDate(String date) {
        String [] splitString = date.split("/",-1);
        this.setDate(splitString[2], splitString[0], splitString[1]);
    }

    public void setDate(Date date) {
        this.date = date;
    }
    //////////////////////////////////////////////////////////
    // description
    //

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    //////////////////////////////////////////////////////////
    // ArrayList<String>
    //

    public ArrayList<String> toStringArray(){
        ArrayList<String> outPut = new ArrayList<>(0);

        outPut.add(this.getDate("MM/dd/yyyy"));
        outPut.add(this.getDescription());

        for (int i = 0; i<this.getSize(); i++) {
            outPut.add(this.getAccountCode(i)+"");
            outPut.add(this.getAmount(i)+"");
        }

        return outPut;
    }


    public ArrayList<String> formViewData(){
        ArrayList<String> result = super.formViewData();
        result.add(0,this.getDate("MMM dd,yy"));
        result.add(1,this.getDescription());
        return result;
    }
}
