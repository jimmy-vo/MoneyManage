package com.JimmyVo.MoneyManage.BaseActivity;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.JimmyVo.MoneyManage.DataStructure.TransactionList;
import com.JimmyVo.MoneyManage.R;

import java.util.ArrayList;


public  class ScrollViewActivity extends BaseUtility {

    protected TableRow tableRow;


    protected TableRow headerRow;
    protected TableRow.LayoutParams params;
    protected TableLayout table;
    protected RelativeLayout tableWrapper;

    protected View fakeHeaderView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        table = (TableLayout) findViewById(R.id.table_main);
        tableWrapper = (RelativeLayout)findViewById(R.id.table_wrapper);
        params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(1,0,1,0);


        final HorizontalScrollView scrollView = (HorizontalScrollView)findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView != null) {
                    updateHeadingOnScroll(scrollView.getScrollX(), scrollView.getScrollY());
                }
            }
        });

        //updateView();
    }


    protected void updateHeadingOnScroll(int x, int y) {
        if(TransactionList.getSize() != 0) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp.leftMargin = -x;
            if (fakeHeaderView != null) {
                tableWrapper.removeView(fakeHeaderView);
                tableWrapper.addView(fakeHeaderView, lp);
            }
        }
    }


    protected void updateViewEmpty(){
        TableRow tableRow = new TableRow(this);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTypeface(textView.getTypeface(),Typeface.BOLD_ITALIC);
        textView.setTextSize(PARA_TEXT_SIZE);
        textView.setText("File is empty");
        LinearLayout layout = new LinearLayout(this);
        tableRow.addView(layout);
        table.addView(tableRow);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels-10;
        layout.addView(textView,params);
    }


    protected void updateFakeHeading(){
        //fake heading
        fakeHeaderView = new View(this) {
            @SuppressLint("MissingSuperCall")
            @Override
            public void draw(Canvas canvas) {
                headerRow.draw(canvas);
            }
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = headerRow.getMeasuredWidth();
                int height = headerRow.getMeasuredHeight();
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        tableWrapper.addView(fakeHeaderView);
    }

    @SuppressLint("ClickableViewAccessibility")
    protected TableRow updateTableRow(final int row, ArrayList<String> text, final ContenViewFormat format, int description, boolean isRowHandler) {
        tableRow = new TableRow(this);
        tableRow.setId(TableRow.generateViewId());
        tableRow.setPadding(1,1,1,1);
        if (isRowHandler) onCreateRowHandler(row, tableRow,  format);

        //Configure format
        for (int col = 0; col < text.size(); col++) {
            TextView textView = new TextView(this);
            if (description == 0)
                tableRow.addView(createTextView(format, text.get(col), View.TEXT_ALIGNMENT_CENTER));
            else if (col < description)
                tableRow.addView(createTextView(format, text.get(col), View.TEXT_ALIGNMENT_TEXT_START));
            else
                tableRow.addView(createTextView(format, text.get(col), View.TEXT_ALIGNMENT_TEXT_END));
        }
        return tableRow;
    }

    protected void onCreateRowHandler(final int row, TableRow tableRow, final ScrollViewActivity.ContenViewFormat format){
        tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectTableRow(row);
            }
        });

        tableRow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        v.setBackgroundColor(format.selectedColor);
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
    }

    protected void onSelectTableRow(int row) {    }

    protected void updateView(){ }
    protected void updateHeading() { }
    protected void updateEndingBalance() { }
    protected void updateContentView() { }
}


