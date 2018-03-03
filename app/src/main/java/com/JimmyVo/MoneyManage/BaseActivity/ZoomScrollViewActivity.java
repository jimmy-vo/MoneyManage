package com.JimmyVo.MoneyManage.BaseActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.ScaleAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;

import com.JimmyVo.MoneyManage.R;

/**
 * Created by Duy Vo on 1/3/2018.
 */

public class ZoomScrollViewActivity extends ScrollViewActivity {

    // step 1: add some instance
    private float mScale = 1f;
    private ScaleGestureDetector mScaleDetector;
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createZoomView();
    }

    protected void createZoomView() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener());

        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = 1 - detector.getScaleFactor();

                float prevScale = mScale;
                mScale += scale;

                if (mScale < 0.1f) // Minimum scale condition:
                    mScale = 0.1f;

                if (mScale > 10f) // Maximum scale condition:
                    mScale = 10f;


                com.JimmyVo.MoneyManage.Utility.Message.showAlways(ZoomScrollViewActivity.this,mScale+"");

                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        1f / prevScale, 1f / mScale,
                        1f / prevScale, 1f / mScale,
                        detector.getFocusX(), detector.getFocusY());
                scaleAnimation.setDuration(0);
                scaleAnimation.setFillAfter(true);
                TableLayout layout = (TableLayout) findViewById(R.id.table_main);
                layout.startAnimation(scaleAnimation);
                return true;

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent event){
        super.dispatchTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }
}
