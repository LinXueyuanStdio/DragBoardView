package com.time.cat.dragboardview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.time.cat.dragboardview.adapter.HorizontalAdapter;
import com.time.cat.dragboardview.helper.DragHelper;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/11/30
 * @discription null
 * @usage null
 */
public class DragBoardView extends DragLayout {
    private PagerRecyclerView mRecyclerView;
    private HorizontalAdapter mAdapter;
    private DragLayout mLayoutMain;
    private DragHelper mDragHelper;

    public DragBoardView(Context context) {
        super(context);
        init(context);
    }

    public DragBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DragBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_drag_board, this);
        mLayoutMain = (DragLayout) findViewById(R.id.layout_main);
        mRecyclerView = (PagerRecyclerView) findViewById(R.id.rv_lists);

        // 配置RecycleView
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFlingFactor(0.1f);
        mRecyclerView.addOnPageChangedListener(mOnPagerChangedListener);
        mRecyclerView.addOnLayoutChangeListener(mOnLayoutChangedListener);
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        // 通过 DragHelper 把 RecycleView 绑定到 DragLayout
        mDragHelper = new DragHelper(context);
        mDragHelper.bindHorizontalRecyclerView(mRecyclerView);
        mLayoutMain.setDragHelper(mDragHelper);
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int childCount = mRecyclerView.getChildCount();
            int width = mRecyclerView.getChildAt(0).getWidth();
            int padding = (mRecyclerView.getWidth() - width) / 2;

            for (int j = 0; j < childCount; j++) {
                View v = recyclerView.getChildAt(j);
                //往左 从 padding 到 -(v.getWidth()-padding) 的过程中，由大到小
                float rate = 0;
                if (v.getLeft() <= padding) {
                    if (v.getLeft() >= padding - v.getWidth()) {
                        rate = (padding - v.getLeft()) * 1f / v.getWidth();
                    } else {
                        rate = 1;
                    }
                    v.setScaleX(1 - rate * 0.1f);

                } else {
                    //往右 从 padding 到 recyclerView.getWidth()-padding 的过程中，由大到小
                    if (v.getLeft() <= recyclerView.getWidth() - padding) {
                        rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                    }
                    v.setScaleX(0.9f + rate * 0.1f);
                }
            }
        }
    };
    private View.OnLayoutChangeListener mOnLayoutChangedListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        }
    };
    private PagerRecyclerView.OnPageChangedListener mOnPagerChangedListener = new PagerRecyclerView.OnPageChangedListener() {
        @Override
        public void OnPageChanged(int oldPosition, int newPosition) {

        }
    };

    public void setHorizontalAdapter(@NonNull HorizontalAdapter horizontalAdapter) {
        this.mAdapter = horizontalAdapter;
        mAdapter.setDragHelper(mDragHelper);
        mRecyclerView.setAdapter(mAdapter);
    }

    public DragHelper getDragHelper() {
        return mDragHelper;
    }

    public PagerRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public HorizontalAdapter getAdapter() {
        return mAdapter;
    }
}
