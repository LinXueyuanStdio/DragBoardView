package com.time.cat.dragboardview.callback;

import android.view.MotionEvent;
import android.view.View;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/6/2
 * @discription 设计监听，未使用
 * @usage null
 */
public interface onDragColumnListener {
    void onStartDragColumn(View columnView, int startPosition);

    void onDraggingColumn(View columnView, MotionEvent event);

    void onEndDragColumn(View columnView, int startPosition, int endPosition);
}
