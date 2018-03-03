package com.JimmyVo.MoneyManage.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.widget.TableRow;
import android.widget.TextView;
import com.JimmyVo.MoneyManage.R;
import com.JimmyVo.MoneyManage.BaseActivity.ScrollViewActivity;

import com.JimmyVo.MoneyManage.DataHandler.DataHandler.ViewSummary;


public class SummaryActivity extends ScrollViewActivity {

    private int PARA_TEXT_SIZE = 20;
    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentCode = RCODE_SUMMARY;
        currentLabel = INTENT_SUMMARY;
        super.onCreate(savedInstanceState);

        isInitialized = ViewSummary.initialize(this);

        button_Floating.setImageResource(android.R.drawable.ic_menu_sort_by_size);
        updateView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case RCODE_CONFIGACCOUNT:
            case RCODE_TRANSACTION:
            case RCODE_GDRIVEIMPORT:
            case RCODE_CONFIGFILES:
                if(resultCode == RESULT_OK)
                {
                    isInitialized =  ViewSummary.initialize(this);
                    updateView();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new ConfirmDialog("Exit", "Do you want to exit?") {
            @Override
            protected void onAccept() {
                finish();
            }
        };
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuSummary:

                break;
            case R.id.menuTransaction:
                OpenActivity(RCODE_TRANSACTION,  currentCode);
                finish();
                break;
            default:
                super.onNavigationItemSelected(item);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected boolean onFloatingButtonClick() {
        OpenActivity(RCODE_CONFIGFILES,  currentCode);
        return true;
    }

    @Override
    protected void onSelectTableRow(int iD) {
        ViewSummary.targetFile(this,iD);
        OpenActivity(RCODE_TRANSACTION,  currentCode);
    }

    @Override
    protected void updateContentView() {
        for (int idx = 0; idx< ViewSummary.getNumberOfFile(); idx++) {
            table.addView(
            updateTableRow(idx, ViewSummary.getEndingBalance(idx),
                    new ContenViewFormat(
                            getResources().getColor(R.color.cellBackGround),
                            getResources().getColor(R.color.selectedRow),
                            Typeface.NORMAL,
                            PARA_TEXT_SIZE), 1, true));
        }
    }

    protected void updateHeading() {

        headerRow = updateTableRow(0, ViewSummary.getHeading(),
                new ContenViewFormat(
                        getResources().getColor(R.color.cellHeadingBackground),
                        getResources().getColor(R.color.selectedRow),
                        Typeface.BOLD, PARA_TEXT_SIZE),
                0, false);
        table.addView(headerRow, 0);
    }

    protected void updateSummary() {

        table.addView(updateTableRow(0, ViewSummary.getSummary(),
                new ContenViewFormat(
                        getResources().getColor(R.color.cellHeadingBackground),
                        getResources().getColor(R.color.selectedRow),
                        Typeface.BOLD, PARA_TEXT_SIZE),
                1, false));
    }

    @Override
    protected void updateView(){
        super.updateView();
        if (isInitialized) {
            table.removeAllViews();

            if (ViewSummary.isEmpty()) {
                updateContentView();
                updateSummary();
                updateHeading();
                updateFakeHeading();

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

}


