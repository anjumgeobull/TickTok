package com.efunhub.ticktok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Aspect 16 : 9 of View
 */
public class MovieWrapperView extends FrameLayout {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    public MovieWrapperView(@NonNull Context context) {
        super(context);
    }

    public MovieWrapperView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MovieWrapperView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

  /*  @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int height=getMeasuredHeight();
       // setMeasuredDimension(measuredWidth, measuredWidth / 16 * 9);
        setMeasuredDimension(measuredWidth, height);
    }*/
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
      int width = MeasureSpec.getSize(widthMeasureSpec);
      int height = MeasureSpec.getSize(heightMeasureSpec);

      if (0 == mRatioWidth || 0 == mRatioHeight) {
          setMeasuredDimension(width, height);
      } else {
          if (width < height * mRatioWidth / mRatioHeight) {
              setMeasuredDimension(width, height);
              Log.d("rlijeolid1",String.valueOf(width)+"\t"+String.valueOf(height));
          } else {
              setMeasuredDimension(width , height);
              Log.d("rlijeolid2",String.valueOf(height * mRatioWidth / mRatioHeight)+"\t"+String.valueOf(height));
          }
      }
  }
}

