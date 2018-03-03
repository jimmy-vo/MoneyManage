package com.JimmyVo.MoneyManage.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import com.JimmyVo.MoneyManage.R;
import com.JimmyVo.MoneyManage.BaseActivity.OrganizerActivity;

import com.JimmyVo.MoneyManage.DataHandler.DataHandler.ConfigAccount;


public class ConfigAccountActivity extends OrganizerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentCode = RCODE_CONFIGACCOUNT;
        currentLabel = INTENT_CONFIGACCOUNT;
        super.onCreate(savedInstanceState);

        ConfigAccount.initialize(this);

        updateView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case RCODE_CONFIGFILES:
                setResult(RESULT_OK);
                finish();
                break;
            case RCODE_GDRIVEIMPORT:
                OpenActivity(RCODE_TRANSACTION,  currentCode);
                finish();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            setResult(RESULT_OK);
            finish();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(item.getItemId() ==  R.id.menuAccountConfig){//ignore this
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onNavigationItemSelected(item);
        }
        return true;
    }

    @Override
    protected boolean onRenameItem(int idx, String string) {
        if (ConfigAccount.renameAccount(this,idx,string)){
            editingItem = -1;
            selectedItem =  -1;
            return true;
        }
        return false;
    }

    @Override
    protected void ConfirmDialogDelete(final int idx, String name) {
        ConfirmDialog diaglog = new ConfirmDialog("Delete Account", "Delete " + name + " account may lead to data mismatch, because the account code in internal data or on clouds can't be updated. Take your own risk?") {
            @Override
            protected void onAccept() {
                if (onRemoveItem(idx)) updateView();
            }
        };
    }

    @Override
    protected void ConfirmDialogRename(final int idx, String currentName) {
        new ConfirmTextDialog("Rename file", "Input new file's name for" + currentName+"?", currentName) {

            @Override
            protected boolean onAccept(String value) {
                if(onRenameItem(idx, value) || onItemClick(idx)){
                    updateView();
                    return true;
                }
                return false;
            }

            @Override
            protected void onTextChanged(EditText string) {
                String content = string.getText().toString();
                String errorMessage = ConfigAccount.newNameTextWatcher( content);

                if(errorMessage!=null){
                    string.setTextColor(getResources().getColor(R.color.colorTextError));
                } else {
                    string.setTextColor(getResources().getColor(R.color.colorTextDefault));
                }
            }
        };
    }

    @Override
    protected void ConfirmDialogCreateNew(){
        new ConfirmTextDialog("Create new file", "Input the file's name:", "New Name") {
            @Override
            protected boolean onAccept(String value) {
                if(onCreateNew(value)){
                    updateView();
                    return true;
                }
                return false;
            }

            @Override
            protected void onTextChanged(EditText string) {
                String content = string.getText().toString();
                String errorMessage = ConfigAccount.newNameTextWatcher( content);

                if(errorMessage!=null){
                    string.setTextColor(getResources().getColor(R.color.colorTextError));
                } else {
                    string.setTextColor(getResources().getColor(R.color.colorTextDefault));
                }
            }
        };
    }

    @Override
    protected boolean onCreateNew(String value) {
        if (ConfigAccount.createNew(this, value)){
            editingItem = -1;
            selectedItem =  -1;
            return true;
        }
        return false;
    }

    @Override
    protected boolean onSwapItem(int idx1, int idx2) {
        ConfigAccount.swap(this, idx1, idx2);
        editingItem = -1;
        selectedItem =  -1;
        return true;
    }

    @Override
    protected void OnClosing(boolean isAnythingChanged) {
        setResult(RESULT_OK);
        finish();
    }


    @Override
    protected boolean onRemoveItem(int idx) {
        ConfigAccount.remove(this, idx);
        editingItem = -1;
        selectedItem =  -1;
        updateView();
        return false;
    }

    @Override
    public boolean onItemClick(int idx){
        if (selectedItem == -1) {
            if(editingItem == idx) {
                selectedItem = idx;
            }
        }else if(selectedItem == idx){
            if(editingItem != idx) {
                selectedItem = -1;
            }
        }else {
            if(editingItem == selectedItem) {
                onSwapItem(idx, selectedItem);
                editingItem= idx;
                selectedItem = idx;
                return true;
            }
            if(editingItem == idx) {
                selectedItem = -1;
            }
        }
        return false;
    }

    @Override
    protected void onUpdateViewIndividual() {
        //not select anyone
        if(editingItem == -1) selectedItem =-1;

        for(int accountOrder = 0; accountOrder< ConfigAccount.getSize(); accountOrder++)        {
            final int order = accountOrder;

            updateViewIndividual(accountOrder, ConfigAccount.getAccountName(accountOrder),false);

            if(editingItem == -1) {
                final Switch aSwitch = new Switch(this);
                aSwitch.setChecked(ConfigAccount.isCredit(accountOrder));
                aSwitch.setBackgroundColor(Color.TRANSPARENT);
                aSwitch.setTextSize(15);
                aSwitch.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                aSwitch.setTextColor(getResources().getColor(R.color.colorTextDefault));
                aSwitch.setText(ConfigAccount.isCredit(accountOrder)?"Credit":"Debit ");
                aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        ConfigAccount.setCredit(getApplicationContext(), order, b);
                        aSwitch.setText(b?"Credit":"Debit ");
                    }
                });

                tableRow.addView(aSwitch, 1, new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));

                final CheckBox checkBox = new CheckBox(this);
                checkBox.setChecked(ConfigAccount.isActive(accountOrder));
                checkBox.setBackgroundColor(Color.TRANSPARENT);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        ConfigAccount.setActive(getApplicationContext(), order, b);
                    }
                });

                tableRow.addView(checkBox, 2, new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
            }
            TextView accountCode = createTextView(String.format("[%02d]", ConfigAccount.getAccountCode(accountOrder)));
            tableRow.addView(accountCode, 0, new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        }
    }
}
