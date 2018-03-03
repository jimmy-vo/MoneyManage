package com.JimmyVo.MoneyManage.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.JimmyVo.MoneyManage.DataHandler.DataHandler;
import com.JimmyVo.MoneyManage.DataStructure.AccountConfig;
import com.JimmyVo.MoneyManage.DataStructure.Transaction;
import com.JimmyVo.MoneyManage.DataStructure.TransactionList;
import com.JimmyVo.MoneyManage.R;
import com.JimmyVo.MoneyManage.BaseActivity.BaseUtility;
import com.JimmyVo.MoneyManage.Utility.CommonU;
import com.JimmyVo.MoneyManage.Utility.Message;


public class EditorActivity extends BaseUtility
{

    public abstract class ConfirmTextAndSpinnerDialog {
        EditText text;
        Spinner spinner;
        Button button;
        public ConfirmTextAndSpinnerDialog(String title, String mess, ArrayList<String > array, int idx, double value) {
            button = new Button(EditorActivity.this);
            if (value>=0) {
                button.setText("+");
            }else   {
                button.setText("-");
            }
            button.setTextSize(30);
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setOnTouchListener(new ImageButton.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            v.setBackgroundColor((getResources().getColor(R.color.selectedRow)));
                            v.invalidate();
                            break;
                        default:
                            v.setBackgroundColor(Color.TRANSPARENT);
                            v.invalidate();
                            break;
                    }
                    return false;
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(button.getText().toString().compareTo("+")==0)
                        button.setText("-");
                    else
                        button.setText("+");
                }
            });
            text = createEditText(Math.abs(value)+"");
            if (value==0)
                text.setText("");
            text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            createEditTextDoubleFilter(text);
            spinner = createSpinner(array,idx);
            LinearLayout linearLayout = new LinearLayout(EditorActivity.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.addView(spinner, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(button, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(text, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
            linearLayout.setPadding(15,15,15,15);
            AlertDialog alertDialog = new AlertDialog.Builder(EditorActivity.this).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(mess);
            alertDialog.setView(linearLayout);
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface alertDialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface alertDialog, int which) {
                    if(onAccept(spinner.getSelectedItem().toString() , text.getText().toString() ,
                            (button.getText().toString().compareTo("+")==0)))
                        alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
        protected abstract boolean onAccept(String  seletedItem, String value, boolean isPositive);
    }

    @SuppressLint("ValidFragment")
    class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            c.setTime(date);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            date = new Date(year-1900,month,day);
            transaction.setDate(date);
            yearText.setText(transaction.getDate("yyyy"));
            dateView.setText(transaction.getDate("MMM dd"));
        }
    }

    private TableLayout table;
    private ViewGroup tableRow;

    private Date date;
    private TextView yearText;
    private TextView dateView;
    private AppCompatMultiAutoCompleteTextView contentText;

    private int requestIdx;
    static Transaction transaction ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentCode = RCODE_EDITOR;
        currentLabel = INTENT_EDITOR;
        super.onCreate(savedInstanceState);

        //special case for editor
        requestIdx = parrentCode;

        if (requestIdx < TransactionList.getSize()) {
            transaction = TransactionList.clone(requestIdx);
        }else  {
            transaction = new Transaction();
        }
        date = transaction.getDate();

        initLayout();
        updateView();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(item.getItemId() ==  R.id.menuFileConfig){//ignore this
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onNavigationItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            requestIdx = TransactionList.getSize();
            closeActivity(false);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case RCODE_CONFIGACCOUNT:
                if(resultCode == RESULT_OK)
                {
                    updateView();
                }
                break;
            case RCODE_GDRIVEIMPORT:
                if(resultCode == RESULT_OK)
                {
                    OpenActivity(RCODE_TRANSACTION,  currentCode);
                    closeActivity(false);
                }
                break;
        }
    }

    @Override
    protected void updateView() {
        table.removeAllViews();

        String[] array = {"Account","     Debit",
                                    "    Credit", " "};

        tableRow = new TableRow(this);
        for (int i=0; i<4; i++) {
            TextView textView = createTextView(array[i]);
            if(i==0){
                TableRow.LayoutParams param = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
                param.weight = 1;
                textView.setLayoutParams(param);
                tableRow.addView(textView);
            }else {
                tableRow.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
            tableRow.setBackgroundColor(getResources().getColor(R.color.cellHeadingBackground   ));
        }
        table.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT));

        for (int i=0; i<transaction.getSize(); i++) {
            final int idx = i;
            final ArrayList<String> string = new ArrayList<String>();

            double amount = transaction.getAmount(i);
            string.add(transaction.getAccountName(i));

            string.add(CommonU.amountFormat(amount));
            if ((AccountConfig.isCredit(transaction.getAccountCode(i)) && (amount > 0))||
            ((!AccountConfig.isCredit(transaction.getAccountCode(i)) && (amount < 0)))){
                string.add(1, "");
            } else {
                string.add(2,"");
            }

            tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            final TableRow tableRow = new TableRow(this);

            final TextView[] textView = new TextView[3];
            for (int ii=0; ii< string.size(); ii++)   {
                textView[ii] = createTextView(string.get(ii));
                textView[ii].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(idx < AccountConfig.getSize()-1){
                new ConfirmTextAndSpinnerDialog(
                        "Account Editor",
                        "Please input the amount for new account",
                        transaction.generateEmptyAccountListIncludeThis(idx),
                        transaction.generateEmptyAccountListIncludeThis(idx).indexOf(string.get(0)),
                        transaction.getAmount(idx)) {
                    @Override
                    protected boolean onAccept(String seletedItem, String value, boolean isPositive) {
                        return onEditItem(idx, seletedItem, value, isPositive);
                    }
                };}}});

            }

                TableRow.LayoutParams param = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
                param.weight = 1;
                textView[0].setLayoutParams(param);
                //tableRow.addView(textView[0]);

                tableRow.addView(textView[0], new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1));
                tableRow.addView(textView[1], new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tableRow.addView(textView[2], new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                ImageButton buttonRemove = createButtonView(android.R.drawable.ic_menu_delete);
                buttonRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new ConfirmDialog("Delete",
                                    "Are you sure to delete the amount of " +
                                            CommonU.amountFormat(transaction.getAmount(idx)) +
                                            " in the " +
                                            AccountConfig.getAccountList().get(idx) + " account?") {
                                                @Override
                                                protected void onAccept() {
                                    onRemoveItem(idx);
                                }
                                            };
                    }
                }
            );

            tableRow.addView(buttonRemove, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            table.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

        for (int i = 0; i < 5; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.addView(new TextView(this));
            table.addView(tableRow);
        }
    }

    @Override
    protected boolean onFloatingButtonClick(){
        if(transaction.getSize()<AccountConfig.getSize()) {
            ConfirmTextAndSpinnerDialog diaglog = new ConfirmTextAndSpinnerDialog(
                    "Add new amount",
                    "Please input the amount:", transaction.generateEmptyAccountList(),
                    0,
                    0) {

                @Override
                protected boolean onAccept(String seletedItem, String value, boolean isPositive) {
                    return onCreateNewItem(seletedItem, value, isPositive);
                }
            };
            return true;
        }
        else {
            new ConfirmDialog("Add new account", "All accounts are added, do you want to add more account?") {
                @Override
                protected void onAccept() {
                    OpenActivity(RCODE_CONFIGACCOUNT,  currentCode);
                }
            };
            return false;
        }
    }

    public void closeActivity(boolean IsUpdateOrDelete){
        if (IsUpdateOrDelete){
            transaction.verify();

            if (requestIdx < TransactionList.getSize()) {
                TransactionList.replaceBy(requestIdx,transaction);
                Message.showAlways(this, "Transaction edited!");
            }else {
                TransactionList.add_Sort(transaction);
                Message.showAlways(this, "Transaction created!");
            }
            DataHandler.ConfigFile.saveEditor(this);
            setResult(RESULT_OK);
        }else{
            if (requestIdx < TransactionList.getSize()) {
                //display file saved message
                TransactionList.remove(requestIdx);
                Message.showAlways(this, "Transaction deleted!");
                DataHandler.ConfigFile.saveEditor(this);
                setResult(RESULT_OK);
            }else{
                Message.showAlways(this, "Transaction canceled!");
                setResult(RESULT_CANCELED);
            }
        }
        finish();
    }

    private void onDescriptionChange(String string) {
        transaction.setDescription(string);
    }

    private boolean onCreateNewItem(String selectedItem, String value, Boolean isPositive) {
        if(CommonU.amountStringVerify(value)) {
            double amount = CommonU.parseStringAmount(value);
            amount = amount * ((amount>0&&isPositive)? 1:-1);
            transaction.add(CommonU.indexof(AccountConfig.getAccountList(), selectedItem), amount);
            updateView();
            return true;
        }else{
            return false;
        }
    }

    private boolean onEditItem(int idx, String selectedItem, String value, Boolean isPositive) {
        if(CommonU.amountStringVerify(value)) {
            double amount = CommonU.parseStringAmount(value);
            amount = amount * ((amount>0&&isPositive)? 1:-1);
            transaction.set(
                    idx,
                    AccountConfig.getAccountList().indexOf(selectedItem),
                    amount);
            updateView();
            return true;
        }else{
            return false;
        }
    }

    private void onRemoveItem(int idx) {
        transaction.removeIndex(idx);
        updateView();
    }

    private void onDeleteClick() {
        closeActivity(false);
    }

    private void onSaveClick() {
        closeActivity(true);
    }


    private void initLayout(){
        table = new TableLayout(this);
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        scroll.addView(table);

        LinearLayout descriptionLayout = createDescriptionView();
        descriptionLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout mainLinearLayout = new LinearLayout(this);
        mainLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mainLinearLayout.addView(descriptionLayout);
        mainLinearLayout.addView(scroll);

        LinearLayoutCompat baseView = (LinearLayoutCompat) findViewById(R.id.baseView);
        baseView.removeView((RelativeLayout) findViewById(R.id.table_wrapper));
        baseView.addView(mainLinearLayout);

        FloatingActionButton button_Floating = (FloatingActionButton) findViewById(R.id.button_Floating);
        button_Floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFloatingButtonClick()) updateView();
            }
        });
    }

    private LinearLayout createDescriptionView (){

        yearText = new TextView(this);
        dateView = new TextView(this);
        contentText = new AppCompatMultiAutoCompleteTextView(this);
        yearText.setTextSize(50);
        dateView.setTextSize(40);

        yearText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);;
        dateView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        yearText.setText(transaction.getDate("yyyy"));
        dateView.setText(transaction.getDate("MMM dd"));

        LinearLayout dateLayout = new LinearLayout(this);
        dateLayout.addView(yearText,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,Gravity.TOP));
        dateLayout.addView(dateView,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,Gravity.BOTTOM));
        dateLayout.setOrientation(LinearLayout.VERTICAL);
        dateLayout.setPadding(30,30,30,30);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "Select date");}
        });

        contentText.setText(transaction.getDescription());
        contentText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        contentText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        contentText.setTextAppearance(R.style.TextAppearance_AppCompat_Large);
        new SetTextWatcher(contentText) {
            @Override
            protected void onTextChange(String string) {
                if(string.contains("\n") || string.contains("\n")){
                    string = string
                            .replace("\n","")
                            .replace("\n","");
                    Message.showAlways(EditorActivity.this, "It can't be special characters");
                }
                onDescriptionChange(string);
            }
        };
        // TextInputLayout
        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setPadding(30,30,30,30);
        textInputLayout.addView(contentText,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textInputLayout.setHint("Description:");
        textInputLayout.setHintTextAppearance(R.style.TextLabel);
        LinearLayout descriptionLayout;
        descriptionLayout = new LinearLayout(this);
        descriptionLayout.setOrientation(LinearLayout.HORIZONTAL);


        ImageButton acceptButton = createButtonView(android.R.drawable.ic_menu_save);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClick();
            }
        });

        ImageButton deleteButton = createButtonView(android.R.drawable.ic_menu_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteClick();
            }
        });

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);
        buttonLayout.addView(acceptButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,Gravity.TOP));
        buttonLayout.addView(deleteButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,Gravity.BOTTOM));

        descriptionLayout.addView(dateLayout,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        descriptionLayout.addView(textInputLayout,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1));
        descriptionLayout.addView(buttonLayout,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));

        return descriptionLayout;
    }
}

