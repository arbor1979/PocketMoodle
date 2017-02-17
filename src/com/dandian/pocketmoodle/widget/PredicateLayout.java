package com.dandian.pocketmoodle.widget;

import java.util.Hashtable;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class PredicateLayout extends LinearLayout {
    int mLeft, mRight, mTop, mBottom;
    Hashtable map = new Hashtable();
    

    public PredicateLayout(Context context) {
        super(context);
    }

    public PredicateLayout(Context context, int horizontalSpacing, int verticalSpacing) {
        super(context);
    }

    public PredicateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mCount = getChildCount();
        int mX = 0;
        int mY = 0;
        mLeft = 0;
        mRight = 0;
        mTop = 0;
        mBottom = 0;
        
        int j = 0;

        View lastview = null;
        for (int i = 0; i < mCount; i++) {
        	
            final View child = getChildAt(i);
           
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            // 此处增加onlayout中的换行判断，用于计算所�?的高�?
            int childw = child.getMeasuredWidth();
            int childh = child.getMeasuredHeight();
           

            Position position = new Position();
            //mLeft = getPosition(i - j, i);
           
            if (mX+childw >= mWidth) {
                //mX = childw;
            	mX=0;
                mY += childh+8;
                j = i;
      
            }
           
           
            mLeft=mX;
            mTop = mY;
            mRight = mLeft +childw;
            mBottom = mTop +childh;
           
         	mX += childw+8;  //将每次子控件宽度进行统计叠加，如果大于设定的高度则需要换行，高度即Top坐标也需重新设置
         	
            position.left = mLeft;
            position.top = mTop;
            position.right = mRight;
            position.bottom = mBottom;
            map.put(child, position);
        }
        setMeasuredDimension(mWidth, mBottom);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            Position pos = (Position) map.get(child);
            if (pos != null) {
                child.layout(pos.left, pos.top, pos.right, pos.bottom);
            } else {
                Log.i("MyLayout", "error");
            }
        }
    }

    private class Position {
        int left, top, right, bottom;
    }

    public int getPosition(int IndexInRow, int childIndex) {
        if (IndexInRow > 0) {
            return getPosition(IndexInRow - 1, childIndex - 1)
                    + getChildAt(childIndex - 1).getMeasuredWidth() + 8;
        }
        return getPaddingLeft();
    }
}