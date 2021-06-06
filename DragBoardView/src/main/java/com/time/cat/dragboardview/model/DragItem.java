package com.time.cat.dragboardview.model;

import androidx.annotation.IntRange;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/11/30
 * @discription null
 * @usage null
 */
public interface DragItem {
    @IntRange(from = 0)
    int getColumnIndex();

    @IntRange(from = 0)
    int getItemIndex();

    void setColumnIndex(@IntRange(from = 0) int columnIndexInHorizontalRecycleView);

    void setItemIndex(@IntRange(from = 0) int itemIndexInVerticalRecycleView);
}
