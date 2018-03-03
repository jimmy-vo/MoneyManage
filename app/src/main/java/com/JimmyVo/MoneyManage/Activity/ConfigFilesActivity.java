package com.JimmyVo.MoneyManage.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import java.util.ArrayList;
import com.JimmyVo.MoneyManage.R;
import com.JimmyVo.MoneyManage.BaseActivity.OrganizerActivity;

import com.JimmyVo.MoneyManage.DataHandler.DataHandler.ConfigFile;


public class ConfigFilesActivity extends OrganizerActivity {
    ArrayList<String> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentCode = RCODE_CONFIGFILES;
        currentLabel = INTENT_CONFIGFILES;
        super.onCreate(savedInstanceState);

        ConfigFile.initialize(this);

        fileList = ConfigFile.getFileList();
        selectedItem = ConfigFile.getFileIndex();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case RCODE_CONFIGACCOUNT:
                setResult(RESULT_OK);
                finish();
                break;
            case RCODE_GDRIVEIMPORT:
                ConfigFile.initialize(this);
                selectedItem = ConfigFile.getFileIndex();
                editingItem = -1;
                updateView();
                break;
        }
    }


    protected void OnClosing(boolean isAnythingChanged) {
        setResult(RESULT_OK);
        finish();
    };


    @Override
    protected boolean onUnselectItem(int idx, int selectedItem){
        ConfigFile.setFileIndex(this,idx);
        OpenActivity(RCODE_TRANSACTION,  currentCode);
        return false;
    }


    @Override
    protected void onSeletedItem(int idx) {
        ConfigFile.setFileIndex(this,idx);
    }

    @Override
    protected boolean onCreateNew(String value) {
        if (ConfigFile.createEmpty(this, value)) {
            editingItem = -1;
            selectedItem = -1;
            isAnythingChanged = true;
            return true;
        }
        return false;
    }


    @Override
    protected boolean onSwapItem(int idx1, int idx2) {
        ConfigFile.swapFileOrder(this,idx1,idx2);
        return true;
    }

    @Override
    protected boolean onRenameItem(int idx, String string) {
        if(ConfigFile.rename(this, idx,string)){
            isAnythingChanged = true;
            editingItem = -1;
            selectedItem =  -1;
            isAnythingChanged = true;
            return true;
        }
        return false;
    }

    @Override
    protected boolean onRemoveItem(int idx) {
        isAnythingChanged = true;
        editingItem = -1;
        ConfigFile.delete(this, idx);
        selectedItem = ConfigFile.getFileIndex();

        updateView();
        return false;
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
                String errorMessage = ConfigFile.newNameTextWatcher(getApplicationContext(), content);

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
                String errorMessage = ConfigFile.newNameTextWatcher(getApplicationContext(), content);

                if(errorMessage!=null){
                    string.setTextColor(getResources().getColor(R.color.colorTextError));
                } else {
                    string.setTextColor(getResources().getColor(R.color.colorTextDefault));
                }
            }
        };
    }

    @Override
    protected void onUpdateViewIndividual() {
        for(int i = 0; i< fileList.size(); i++) {
            updateViewIndividual(i,fileList.get(i), false);

            if(editingItem == -1) {
                final int fileIdx = i;
                ImageButton buttonOpen = createButtonView(android.R.drawable.ic_menu_save);
                buttonOpen.setBackgroundColor(Color.TRANSPARENT);
                buttonOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {onExportClick(fileIdx);}
                });
                tableRow.addView(buttonOpen, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    void onExportClick(final int idx){
        ConfirmDialog diaglog = new ConfirmDialog("Google Drive backup", "Are you sure to save " + ConfigFile.getFileList().get(idx) + " to Google Drive?"){
            @Override
            protected void onAccept() {
                ConfigFile.setFileIndex(getApplicationContext(),idx);
                ConfigFile.loadTransactionAndEndingBalance(getApplicationContext(),idx);
                OpenActivity(RCODE_GDRIVEEXPORT,  currentCode);
                selectedItem = idx;
            }
        };
    }
}
