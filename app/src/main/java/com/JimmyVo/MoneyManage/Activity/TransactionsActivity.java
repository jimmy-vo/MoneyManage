package com.JimmyVo.MoneyManage.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableRow;
import android.widget.TextView;
import com.JimmyVo.MoneyManage.R;
import com.JimmyVo.MoneyManage.BaseActivity.ScrollViewActivity;

import com.JimmyVo.MoneyManage.DataHandler.DataHandler.ViewTransaction;

import java.util.ArrayList;


public class TransactionsActivity extends ScrollViewActivity {
    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentCode = RCODE_TRANSACTION;
        currentLabel = INTENT_TRANSACTION;
        super.onCreate(savedInstanceState);

        isInitialized = ViewTransaction.initialize(this);

        updateView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case RCODE_EDITOR:
            case RCODE_CONFIGACCOUNT:
            case RCODE_GDRIVEIMPORT:
            case RCODE_CONFIGFILES:
                if(resultCode == RESULT_OK)
                {
                    isInitialized = ViewTransaction.initialize(this);
                    updateView();
                }
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuSummary:
                OpenActivity(RCODE_SUMMARY,  currentCode);
                finish();
                break;
            case R.id.menuTransaction:
                break;
            default:
                super.onNavigationItemSelected(item);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (parrentCode == RCODE_SUMMARY){
            setResult(RESULT_OK);
            finish();
        } else if (parrentCode == RCODE_CONFIGFILES){
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected boolean onFloatingButtonClick() {
        onOpenEditor(ViewTransaction.getNumberOfTransaction());
        return true;
    }

    @Override
    protected void onSelectTableRow(int iD) {onOpenEditor(iD);}

    private void onOpenEditor(int iD){
        ArrayList<String> intentData = new ArrayList<>();
        OpenActivity(RCODE_EDITOR,  iD);
    }

    @Override
    protected void onPrepareNavigationMenu(Menu menu){
        super.onPrepareNavigationMenu(menu);

        getSupportActionBar().setTitle(ViewTransaction.getFileName());
    }
    @Override
    protected void updateView(){
        super.updateView();

        getSupportActionBar().setTitle(ViewTransaction.getFileName());
        if (isInitialized) {
            table.removeAllViews();

            if (ViewTransaction.getNumberOfTransaction() != 0) {
                updateContentView();
                updateEndingBalance();
                updateHeading();
                updateFakeHeading();

                //
                for (int i = 0; i < 5; i++) {
                    TableRow tableRow = new TableRow(this);
                    tableRow.addView(new TextView(this));
                    table.addView(tableRow);
                }

            } else {
                updateViewEmpty();
            }
        }
    }

    protected void updateContentView() {
        for (int idx = 0; idx< ViewTransaction.getNumberOfTransaction(); idx++) {
            table.addView(
            updateTableRow(idx, ViewTransaction.getTransaction(idx),
                    new ContenViewFormat(
                            getResources().getColor(R.color.cellBackGround),
                            getResources().getColor(R.color.selectedRow),
                            Typeface.NORMAL,
                            PARA_TEXT_SIZE), 2, true));
        }
    }

    protected void updateHeading() {
        headerRow = updateTableRow(0, ViewTransaction.getHeading(),
                new ContenViewFormat(
                getResources().getColor(R.color.cellHeadingBackground),
                getResources().getColor(R.color.selectedRow),
                Typeface.BOLD, PARA_TEXT_SIZE),
                0, false);
        table.addView(headerRow, 0);
    }

    protected void updateEndingBalance() {
        table.addView(
                updateTableRow(0, ViewTransaction.getEndingBalance(),
                        new ContenViewFormat(
                        getResources().getColor(R.color.cellHeadingBackground),
                        getResources().getColor(R.color.selectedRow),
                        Typeface.BOLD, PARA_TEXT_SIZE),
                        2, false));
    }

}


