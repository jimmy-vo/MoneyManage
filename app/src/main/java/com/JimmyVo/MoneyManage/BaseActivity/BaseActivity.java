package com.JimmyVo.MoneyManage.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.JimmyVo.MoneyManage.Activity.ConfigAccountActivity;
import com.JimmyVo.MoneyManage.Activity.ConfigFilesActivity;
import com.JimmyVo.MoneyManage.Activity.EditorActivity;
import com.JimmyVo.MoneyManage.Activity.DriveBackupActivity;
import com.JimmyVo.MoneyManage.Activity.DriveImportActivity;
import com.JimmyVo.MoneyManage.Activity.AboutActivity;
import com.JimmyVo.MoneyManage.Activity.TransactionsActivity;
import com.JimmyVo.MoneyManage.R;
import com.JimmyVo.MoneyManage.Activity.SummaryActivity;

/**
 * Created by Duy Vo on 12/25/2017.
 */

public  class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    protected static final String INTENT_TRANSACTION    = "Transaction";
    protected static final String INTENT_SUMMARY        = "Summary";
    protected static final String INTENT_EDITOR         = "EDITOR";
    protected static final String INTENT_CONFIGACCOUNT  = "Accounts";
    protected static final String INTENT_CONFIGFILES    = "Files";
    protected static final String INTENT_GDRIVEIMPORT   = "Drive Import";
    protected static final String INTENT_GDRIVEEXPORT   = "Drive Export";
    protected static final String INTENT_ABOUT          = "About";

    protected static final int RCODE_TRANSACTION        = 2;
    protected static final int RCODE_SUMMARY            = 4;
    protected static final int RCODE_EDITOR             = 8;
    protected static final int RCODE_CONFIGACCOUNT      = 16;
    protected static final int RCODE_CONFIGFILES        = 32;
    protected static final int RCODE_GDRIVEIMPORT       = 64;
    protected static final int RCODE_GDRIVEEXPORT       = 128;
    protected static final int RCODE_ABOUT = 256;

    NavigationView navigationView;
    protected ActionBarDrawerToggle toggle;
    protected Toolbar toolbar;
    protected DrawerLayout drawer;
    protected int currentCode, parrentCode;
    protected String currentLabel;
    protected FloatingActionButton button_Floating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_Floating = (FloatingActionButton) findViewById(R.id.button_Floating);
        button_Floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFloatingButtonClick();
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
        }else{
            getSupportActionBar().show();
        }


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

       // onPrepareNavigationMenu(navigationView.getMenu());

        parrentCode = getIntent().getIntExtra(currentLabel, -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_drawer, menu);
        return true;
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
                    updateView();
                }
                break;
        }
    }

    protected void onPrepareNavigationMenu(Menu menu) {

        getSupportActionBar().setTitle(currentLabel);

        if(currentCode == RCODE_TRANSACTION){
            menu.findItem(R.id.menuSummary).setVisible(true);
            menu.findItem(R.id.menuTransaction).setVisible(false);
            menu.findItem(R.id.menuImport).setVisible(true);
        }else if(currentCode == RCODE_SUMMARY){
            menu.findItem(R.id.menuSummary).setVisible(false);
            menu.findItem(R.id.menuTransaction).setVisible(true);
            menu.findItem(R.id.menuImport).setVisible(true);
        }else {
            menu.findItem(R.id.menuSummary).setVisible(true);
            menu.findItem(R.id.menuTransaction).setVisible(true);
            menu.findItem(R.id.menuImport).setVisible(false);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.menuImport).setVisible(false);
        menu.findItem(R.id.menuExport).setVisible(false);
        menu.findItem(R.id.menuSetting).setVisible(false);
        menu.findItem(R.id.menuFileConfig).setVisible(false);
        menu.findItem(R.id.menuGoogleDive).setVisible(false);
        menu.findItem(R.id.menuAbout).setVisible(false);
        onPrepareNavigationMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return onNavigationItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuSummary:
                OpenActivity(RCODE_SUMMARY,  currentCode);
                break;
            case R.id.menuTransaction:
                OpenActivity(RCODE_TRANSACTION,  currentCode);
                break;
            case R.id.menuAccountConfig:
                OpenActivity(RCODE_CONFIGACCOUNT,  currentCode);
                break;
            case R.id.menuFileConfig:
                OpenActivity(RCODE_CONFIGFILES,  currentCode);
                break;
            case R.id.menuImport:
                OpenActivity(RCODE_GDRIVEIMPORT,  currentCode);
                break;
            case R.id.menuExport:
                OpenActivity(RCODE_GDRIVEEXPORT,  currentCode);
                break;
            case R.id.menuAbout:
                OpenActivity(RCODE_ABOUT,  currentCode);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected final void OpenActivity(int rCode, int code){
        Intent intent;
        switch (rCode) {
            case RCODE_SUMMARY:
                intent = new Intent(this, SummaryActivity.class);
                intent.putExtra (INTENT_SUMMARY, code);
                startActivityForResult(intent, RCODE_SUMMARY);
                break;
            case RCODE_TRANSACTION:
                intent = new Intent(this, TransactionsActivity.class);
                intent.putExtra (INTENT_TRANSACTION, code);
                startActivityForResult(intent, RCODE_TRANSACTION);
                break;

            case RCODE_CONFIGACCOUNT:
                intent = new Intent(this, ConfigAccountActivity.class);
                intent.putExtra (INTENT_CONFIGACCOUNT, code);
                startActivityForResult(intent, RCODE_CONFIGACCOUNT);
                break;
            case RCODE_CONFIGFILES:
                intent = new Intent(this, ConfigFilesActivity.class);
                intent.putExtra (INTENT_CONFIGFILES, code);
                startActivityForResult(intent, RCODE_CONFIGFILES);
                break;
            case RCODE_GDRIVEIMPORT:
                intent = new Intent(this, DriveImportActivity.class);
                intent.putExtra (INTENT_GDRIVEIMPORT, code).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, RCODE_GDRIVEIMPORT);
                break;
            case RCODE_GDRIVEEXPORT:
                intent = new Intent(this, DriveBackupActivity.class);
                intent.putExtra (INTENT_GDRIVEEXPORT, code).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, RCODE_GDRIVEEXPORT);
                break;
            case RCODE_EDITOR:
                intent = new Intent(this, EditorActivity.class);
                intent.putExtra (INTENT_EDITOR, code);
                startActivityForResult(intent, RCODE_EDITOR);
                break;
            case RCODE_ABOUT:
                intent = new Intent(this, AboutActivity.class);
                intent.putExtra (INTENT_ABOUT, code);
                startActivityForResult(intent, RCODE_ABOUT);
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    protected void updateView() {
//        View header = navigationView.getHeaderView(0);
//        TextView text = (TextView) header.findViewById(R.id.menuTitle);
//        text.setText(DataHandler.CurrentFile.getFileIndex());
    }


    protected boolean onFloatingButtonClick(){ return true;}






}
