package com.JimmyVo.MoneyManage.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import com.JimmyVo.MoneyManage.R;

import com.JimmyVo.MoneyManage.BaseActivity.BaseActivity;

/**
 * Created by Duy Vo on 1/3/2018.
 */

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayoutCompat baseView = (LinearLayoutCompat) findViewById(R.id.baseView);
        baseView.removeView((RelativeLayout) findViewById(R.id.table_wrapper));

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View child = View.inflate(this, R.layout.about_content, null);
        baseView.addView(child);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }
}
