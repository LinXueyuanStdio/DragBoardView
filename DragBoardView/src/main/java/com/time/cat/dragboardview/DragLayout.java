package com.time.cat.dragboardview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.time.cat.dragboardview.helper.DragHelper;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription 配合 {@link DragHelper} 实现拖拽的根布局
 * @usage null
 */
public class DragLayout extends RelativeLayout {

    private DragHelper mDragHelper;

    public DragLayout(Context context) {
        super(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDragHelper(DragHelper dragHelper) {
        mDragHelper = dragHelper;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragHelper != null) {
            if (mDragHelper.isDragging()) {return true;}
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mDragHelper != null) {
                    mDragHelper.updateDraggingPosition(event.getRawX(), event.getRawY());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mDragHelper != null) {mDragHelper.drop();}
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
