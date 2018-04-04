package com.time.cat.dragboardview.callback;

import android.support.v7.widget.RecyclerView;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription null
 * @usage null
 */
public interface DragHorizontalViewHolderCallBack {
    /**
     * each Horizontal ViewHolder is supposed to have a RecycleView
     * @return RecycleView in ViewHolder which contains vertical items
     */
    RecyclerView getRecyclerView();
}
