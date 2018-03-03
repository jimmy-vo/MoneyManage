package com.JimmyVo.MoneyManage.BaseActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.JimmyVo.MoneyManage.DataHandler.DataHandler.ConfigFile;
import com.JimmyVo.MoneyManage.R;


public abstract class OrganizerActivity extends BaseUtility {

    protected boolean isAnythingChanged;
    protected int selectedItem = -1;
    protected int editingItem = -1;

    protected TableLayout table;
    protected TableRow tableRow;


    protected int maxItem = 0;
    private LinearLayoutCompat baseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        table = new TableLayout(this);
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        scroll.addView(table);

        baseView = (LinearLayoutCompat) findViewById(R.id.baseView);
        baseView.removeView((RelativeLayout) findViewById(R.id.table_wrapper));
        baseView.addView(scroll);

        FloatingActionButton button_Floating = (FloatingActionButton) findViewById(R.id.button_Floating);
        button_Floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFloatingButtonClick()) updateView();
            }
        });

        isAnythingChanged = false;
    }

    @Override
    public void onBackPressed() {
        if(editingItem != -1)
        {
            editingItem = -1;
            updateView();
        }else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                ConfigFile.initialize(this);
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected boolean onFloatingButtonClick() {
        ConfirmDialogCreateNew();
        editingItem = -1;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                OnClosing(isAnythingChanged);
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void updateView() {
        table.removeAllViews();
        onUpdateViewIndividual();
        maxItem = table.getChildCount();

        onCorrectView();
        for (int i = 0; i < 5; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.addView(new TextView(this));
            table.addView(tableRow);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void updateViewIndividual(final int idx, String text, boolean isNew) {
        tableRow = new TableRow(this);
        tableRow.setOrientation(LinearLayout.HORIZONTAL);
        tableRow.setGravity(Gravity.RIGHT);

        final LinearLayout v = tableRow;

        final TextView textViews = createTextView(text);
        tableRow.addView(textViews, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1));

        if (idx == editingItem) {
            textViews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConfirmDialogRename(idx, textViews.getText().toString());
                }
            });

            ImageButton buttonReverse = createButtonView(android.R.drawable.ic_menu_revert);
            buttonReverse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onCancelClick(idx)) updateView();
                }
            });

            tableRow.addView(buttonReverse,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        } else {
            textViews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClick(idx)) updateView();
                }
            });

            if (editingItem == -1) {

                ImageButton buttonEdit = createButtonView(android.R.drawable.ic_menu_edit);
                buttonEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onEditPress(idx)) updateView();
                    }
                });

                ImageButton buttonRemove = createButtonView(android.R.drawable.ic_menu_delete);
                buttonRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ConfirmDialogDelete( idx, textViews.getText().toString());

                    }
                });

                tableRow.addView(buttonRemove,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tableRow.addView(buttonEdit,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
        }


        if (idx == selectedItem) {
            tableRow.setBackgroundColor((getResources().getColor(R.color.selectedRow)));
        }

        table.addView(tableRow,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    protected void ConfirmDialogDelete(final int idx, String name) {
        ConfirmDialog diaglog = new ConfirmDialog("Deleter", "Are you sure to delete " + name + "?") {
            @Override
            protected void onAccept() {
                if (onRemoveItem(idx)) updateView();
            }
        };
    }

    protected void ConfirmDialogRename(final int idx, String currentName) {
        new ConfirmTextDialog("Rename", "Input new rename for" + currentName+"?", currentName) {

            @Override
            protected boolean onAccept(String value) {
                if(onRenameItem(idx, value) || onItemClick(idx)){
                    updateView();
                    return true;
                }
                return false;
            }
        };
    }

    protected void ConfirmDialogCreateNew(){
        new ConfirmTextDialog("Create new", "Input name:", "New Name") {
            @Override
            protected boolean onAccept(String value) {
                if(onCreateNew(value)){
                    updateView();
                    return true;
                }
                return false;
            }
        };
    }

    protected boolean onRemoveItem(int idx){
        editingItem = -1;
        selectedItem = maxItem;
        updateView();
        return true;
    }

    protected boolean onEditPress(int idx) {
        editingItem = idx;
        selectedItem = idx;
        return true;
    }

    protected boolean onCancelClick(int idx) {
        editingItem = -1;
        selectedItem = idx;
        return true;
    }

    protected boolean onRenameItem(int idx, String string){
        editingItem = -1;
        selectedItem = idx;
        updateView();
        return true;
    }

    protected boolean onCreateNew(String value) {
        return true;
    }

    protected boolean onItemClick(int idx){
        boolean result = true;
        if (selectedItem == -1) {
            selectedItem = idx;
            onSeletedItem(idx);
        }else if(selectedItem == idx){
            if(editingItem != idx) {
                result =  onUnselectItem(idx, selectedItem);
                selectedItem = -1;
            }
        }else {
            if(editingItem == selectedItem) {
                result = onSwapItem(idx, selectedItem);
                editingItem= idx;
            }
            selectedItem = idx;
            onSeletedItem(idx);
        }
        return result;
    }

    protected void onSeletedItem(int idx) {

    }

    protected boolean onSwapItem(int idx1, int idx2){return true;}

    protected boolean onUnselectItem(int idx, int selectedItem){
        return false;
    }

    protected void OnClosing(boolean isAnythingChanged) {};

    protected void onCorrectView() { }

    protected  void onUpdateViewIndividual(){}
}
