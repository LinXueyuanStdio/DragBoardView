package com.time.cat.dragboardview.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.time.cat.dragboardview.DragLayout;
import com.time.cat.dragboardview.PagerRecyclerView;
import com.time.cat.dragboardview.callback.DragHorizontalAdapterCallBack;
import com.time.cat.dragboardview.callback.DragHorizontalViewHolderCallBack;
import com.time.cat.dragboardview.callback.DragVerticalAdapterCallBack;
import com.time.cat.dragboardview.utils.AttrAboutPhone;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription view 的拖拽功能，给 {@link DragLayout} 用
 * @usage null
 */
public class DragHelper {
    public static final int TYPE_CONTENT = 1;
    public static final int TYPE_FOOTER = 2;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private ImageView mDragImageView;
    private RecyclerView mCurrentVerticalRecycleView;
    private PagerRecyclerView mHorizontalRecyclerView;

    private Object tag;
    private boolean isDraggingItem = false;//抓起=true，否则=false
    private Object columnObject;
    private boolean isDraggingColumn = false;//是否正在拖拽一列，抓起=true，否则=false

    private float mBornLocationX, mBornLocationY;//抓起时 view 的坐标
    private int offsetX, offsetY;//抓起时 view 坐标和点击点的距离
    private boolean confirmOffset = false;//是否确定了 offset

    private Timer mHorizontalScrollTimer = new Timer();//横向滑动
    private TimerTask mHorizontalScrollTimerTask;//横向滑动
    private static final int HORIZONTAL_STEP = 30;// 横向滑动步伐.
    private static final int HORIZONTAL_SCROLL_PERIOD = 20;// 滑动时间间隔
    private int leftScrollBounce;// 拖动的时候，开始向左滚动的边界
    private int rightScrollBounce;// 拖动的时候，开始向右滚动的边界

    private Timer mVerticalScrollTimer = new Timer();//纵向滑动
    private TimerTask mVerticalScrollTimerTask;//纵向滑动
    private static final int VERTICAL_STEP = 10;// 纵向滑动步伐.
    private static final int VERTICAL_SCROLL_PERIOD = 10;
    private int upScrollBounce;// 拖动的时候，开始向上滚动的边界
    private int downScrollBounce;// 拖动的时候，开始向下滚动的边界
    private int mPosition = -1;// 拖动的 View 在纵向 recyclerView 上的 position
    private int mPagerPosition = -1;// 拖动的 View 在横向 recyclerView 上的 position


    @SuppressLint("ClickableViewAccessibility")
    public DragHelper(Activity activity) {
        mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mWindowParams.alpha = 1.0f;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowParams.x = 0;
        mWindowParams.y = 0;

        mDragImageView = new ImageView(activity);
        mDragImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDragImageView.setPadding(10, 10, 10, 10);
        mDragImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE || event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isDraggingItem) {
                        dropItem();
                    } else if (isDraggingColumn) {
                        dropCol();
                    }
                }
                return false;
            }
        });
    }


    //<drag column>
    /**
     * 是否在拖动一列
     */
    public boolean isDraggingColumn() {
        return isDraggingColumn;
    }

    /**
     * 抓起
     *
     * @param columnView  抓起的 View
     * @param position 抓起的 View 在 横向RecyclerView 的 position
     */
    public void dragCol(View columnView, int position) {
        columnView.destroyDrawingCache();
        columnView.setDrawingCacheEnabled(true);
        Bitmap bitmap = columnView.getDrawingCache();
        if (bitmap != null && !bitmap.isRecycled()) {
            mDragImageView.setImageBitmap(bitmap);
            mDragImageView.setRotation(1.5f);
            mDragImageView.setAlpha(0.8f);

            isDraggingColumn = true;

            columnObject = columnView.getTag();
            int dragPage = mHorizontalRecyclerView.getCurrentPosition();
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) mHorizontalRecyclerView.findViewHolderForAdapterPosition(dragPage);
            if (holder != null && holder.itemView != null && holder.getItemViewType() == TYPE_CONTENT) {
                mCurrentVerticalRecycleView = ((DragHorizontalViewHolderCallBack) holder).getRecyclerView();
                mPagerPosition = dragPage;
            }

            getTargetHorizontalRecyclerViewScrollBoundaries();
            getTargetVerticalRecyclerViewScrollBoundaries();

            int[] location = new int[2];
            columnView.getLocationOnScreen(location);
            mWindowParams.x = location[0];
            mWindowParams.y = location[1];
            mBornLocationX = location[0];
            mBornLocationY = location[1];
            confirmOffset = false;
            mPosition = position;
            mWindowManager.addView(mDragImageView, mWindowParams);
            getHorizontalAdapter().onDrag(position);
        }
    }

    /**
     * 放下
     */
    public void dropCol() {
        if (isDraggingColumn) {
            mWindowManager.removeView(mDragImageView);
            isDraggingColumn = false;

            if (mVerticalScrollTimerTask != null) {
                mVerticalScrollTimerTask.cancel();
            }

            if (mHorizontalScrollTimerTask != null) {
                mHorizontalScrollTimerTask.cancel();
            }

            if (mHorizontalRecyclerView != null) {
                mHorizontalRecyclerView.backToCurrentPage();
            }

            getHorizontalAdapter().onDrop(mPagerPosition, mPosition, tag);
        }
    }

    /**
     * 更新当前拖动点下面的 RecyclerView
     */
    private void updateSlidingHorizontalRecyclerView(float x, float y) {
        int newPage = getHorizontalCurrentPosition(x, y); // 传入的是相对屏幕的 x,y
        if (mPagerPosition != newPage) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) mHorizontalRecyclerView.findViewHolderForAdapterPosition(newPage);
            if (holder != null && holder.itemView != null && holder.getItemViewType() == TYPE_CONTENT) {
                getHorizontalAdapter().onDragOut();
                mCurrentVerticalRecycleView = ((DragHorizontalViewHolderCallBack) holder).getRecyclerView();
                mPagerPosition = newPage;
                getHorizontalAdapter().onDragIn(mPosition, columnObject);

            }
        }
    }

    /**
     * 获取当前拖动项的 position
     *
     * @param rowX 拖动点相对于屏幕的横坐标
     * @param rowY 拖动点相对于屏幕的纵坐标
     */
    private void findViewPositionInHorizontalRV(float rowX, float rowY) {
        int[] location = new int[2];
        mHorizontalRecyclerView.getLocationOnScreen(location);
        float x = rowX - location[0];
        float y = rowY - location[1];
        View child = mHorizontalRecyclerView.findChildViewUnder(x, y);// 这个方法传入的值是相对于 recyclerView 的
        int newPosition = mHorizontalRecyclerView.getChildAdapterPosition(child);
        int footerChildIndex = mHorizontalRecyclerView.getChildCount() + 1;//把抓住的加回去
        if (newPosition != RecyclerView.NO_POSITION
                && newPosition != footerChildIndex) {
            getHorizontalAdapter().updateDragItemVisibility(mPosition);
            if (mPosition != newPosition && mPosition < footerChildIndex) {
                mPosition = newPosition;
            }
        }
    }

    /**
     * 获取当前纵向 RecyclerView 的 Adapter
     *
     * @return Adapter
     */
    private DragHorizontalAdapterCallBack getHorizontalAdapter() {
        return (DragHorizontalAdapterCallBack) mHorizontalRecyclerView.getAdapter();
    }
    //</drag column>


    /**
     * 绑定 横向的 RecyclerView
     *
     * @param view 横向的 RecyclerView
     */
    public void bindHorizontalRecyclerView(@NonNull PagerRecyclerView view) {
        mHorizontalRecyclerView = view;
        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            throw new RuntimeException("LayoutManager must be LinearLayoutManager");
        }
    }

    //<drag item>
    /**
     * 是否在拖动一项
     */
    public boolean isDraggingItem() {
        return isDraggingItem;
    }

    /**
     * 抓起
     *
     * @param dragger  抓起的 View
     * @param position 抓起的 View 在 RecyclerView 的 position
     */
    public void dragItem(View dragger, int position) {
        dragger.destroyDrawingCache();
        dragger.setDrawingCacheEnabled(true);
        Bitmap bitmap = dragger.getDrawingCache();
        if (bitmap != null && !bitmap.isRecycled()) {
            mDragImageView.setImageBitmap(bitmap);
            mDragImageView.setRotation(1.5f);
            mDragImageView.setAlpha(0.8f);

            isDraggingItem = true;
            tag = dragger.getTag();
            int dragPage = mHorizontalRecyclerView.getCurrentPosition();
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) mHorizontalRecyclerView.findViewHolderForAdapterPosition(dragPage);
            if (holder != null && holder.itemView != null && holder.getItemViewType() == TYPE_CONTENT) {
                mCurrentVerticalRecycleView = ((DragHorizontalViewHolderCallBack) holder).getRecyclerView();

                mPagerPosition = dragPage;
            }

            getTargetHorizontalRecyclerViewScrollBoundaries();
            getTargetVerticalRecyclerViewScrollBoundaries();

            int[] location = new int[2];
            dragger.getLocationOnScreen(location);
            mWindowParams.x = location[0];
            mWindowParams.y = location[1];
            mBornLocationX = location[0];
            mBornLocationY = location[1];
            confirmOffset = false;
            mPosition = position;
            mWindowManager.addView(mDragImageView, mWindowParams);
            getCurrentVerticalAdapter().onDrag(position);
        }
    }

    /**
     * 放下
     */
    public void dropItem() {
        if (isDraggingItem) {
            mWindowManager.removeView(mDragImageView);
            isDraggingItem = false;

            if (mVerticalScrollTimerTask != null) {
                mVerticalScrollTimerTask.cancel();
            }

            if (mHorizontalScrollTimerTask != null) {
                mHorizontalScrollTimerTask.cancel();
            }

            if (mHorizontalRecyclerView != null) {
                mHorizontalRecyclerView.backToCurrentPage();
            }

            getCurrentVerticalAdapter().onDrop(mPagerPosition, mPosition, tag);
        }
    }

    /**
     * 更新当前拖动点下面的 RecyclerView
     */
    private void updateSlidingVerticalRecyclerView(float x, float y) {
        int newPage = getHorizontalCurrentPosition(x, y); // 传入的是相对屏幕的 x,y
        if (mPagerPosition != newPage) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) mHorizontalRecyclerView.findViewHolderForAdapterPosition(newPage);
            if (holder != null && holder.itemView != null && holder.getItemViewType() == TYPE_CONTENT) {
                getCurrentVerticalAdapter().onDragOut();

                mCurrentVerticalRecycleView = ((DragHorizontalViewHolderCallBack) holder).getRecyclerView();
                mPagerPosition = newPage;

                getCurrentVerticalAdapter().onDragIn(mPosition, tag);
            }
        }
    }

    /**
     * 获取当前拖动项的 position
     *
     * @param rowX 拖动点相对于屏幕的横坐标
     * @param rowY 拖动点相对于屏幕的纵坐标
     */
    private void findViewPositionInCurVerticalRV(float rowX, float rowY) {
        int[] location = new int[2];
        mCurrentVerticalRecycleView.getLocationOnScreen(location);
        float x = rowX - location[0];
        float y = rowY - location[1];
        View child = mCurrentVerticalRecycleView.findChildViewUnder(x, y);// 这个方法传入的值是相对于 recyclerView 的
        int newPosition = mCurrentVerticalRecycleView.getChildAdapterPosition(child);
        if (newPosition != RecyclerView.NO_POSITION) {
            getCurrentVerticalAdapter().updateDragItemVisibility(mPosition);
            if (mPosition != newPosition) {
                mPosition = newPosition;
            }
        }
    }

    /**
     * 获取当前纵向 RecyclerView 的 Adapter
     *
     * @return Adapter
     */
    private DragVerticalAdapterCallBack getCurrentVerticalAdapter() {
        return (DragVerticalAdapterCallBack) mCurrentVerticalRecycleView.getAdapter();
    }
    //</drag item>


    //<common>
    /**
     * 更新拖动点的坐标
     *
     * @param rowX 相对屏幕的横坐标
     * @param rowY 相对屏幕的纵坐标
     */
    public void updateDraggingPosition(float rowX, float rowY) {
        if (mWindowManager == null || mWindowParams == null)
            return;
        if (!confirmOffset) {
            calculateOffset(rowX, rowY);
        }
        if (isDraggingItem) {
            mWindowParams.x = (int) (rowX - offsetX);
            mWindowParams.y = (int) (rowY - offsetY);
            mWindowManager.updateViewLayout(mDragImageView, mWindowParams);
            updateSlidingVerticalRecyclerView(rowX, rowY);
            findViewPositionInCurVerticalRV(rowX, rowY);
            recyclerViewScrollHorizontal((int) rowX, (int) rowY);
            recyclerViewScrollVertical((int) rowX, (int) rowY);
        } else if (isDraggingColumn) {
            mWindowParams.x = (int) (rowX - offsetX);
            mWindowParams.y = (int) (rowY - offsetY);
            mWindowManager.updateViewLayout(mDragImageView, mWindowParams);
            updateSlidingHorizontalRecyclerView(rowX, rowY);
            findViewPositionInHorizontalRV(rowX, rowY);
            recyclerViewScrollHorizontal((int) rowX, (int) rowY);
            recyclerViewScrollVertical((int) rowX, (int) rowY);
        }
    }

    /**
     * 计算拖动点和生成的 ImageView 坐标的偏移
     *
     * @param x 横坐标
     * @param y 纵坐标
     */
    private void calculateOffset(float x, float y) {
        offsetX = (int) Math.abs(x - mBornLocationX);
        offsetY = (int) Math.abs(y - mBornLocationY);
        confirmOffset = true;
    }

    /**
     * 获取纵向滑动边界
     */
    private void getTargetVerticalRecyclerViewScrollBoundaries() {
        int[] location = new int[2];
        mCurrentVerticalRecycleView.getLocationOnScreen(location);
        upScrollBounce = location[1] + 150;
        downScrollBounce = location[1] + mCurrentVerticalRecycleView.getHeight() - 150;
    }

    /**
     * 获取横向滑动边界
     */
    private void getTargetHorizontalRecyclerViewScrollBoundaries() {
        leftScrollBounce = 200;
        rightScrollBounce = AttrAboutPhone.screenWidth - 200;
        Log.i("MyTag", "leftScrollBounce " + leftScrollBounce + " rightScrollBounce " + rightScrollBounce);
    }

    /**
     * 横向 RecyclerView 滑动
     *
     * @param x 拖动点横坐标
     */
    private void recyclerViewScrollHorizontal(final int x, final int y) {
        if (mHorizontalScrollTimerTask != null)
            mHorizontalScrollTimerTask.cancel();

        if (x > rightScrollBounce) {
            mHorizontalScrollTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHorizontalRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mHorizontalRecyclerView.scrollBy(HORIZONTAL_STEP, 0);
                            findViewPositionInCurVerticalRV(x, y);
                        }
                    });
                }
            };
            mHorizontalScrollTimer.schedule(mHorizontalScrollTimerTask, 0, HORIZONTAL_SCROLL_PERIOD);
        } else if (x < leftScrollBounce) {
            mHorizontalScrollTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHorizontalRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mHorizontalRecyclerView.scrollBy(-HORIZONTAL_STEP, 0);
                            findViewPositionInCurVerticalRV(x, y);
                        }
                    });
                }
            };
            mHorizontalScrollTimer.schedule(mHorizontalScrollTimerTask, 0, HORIZONTAL_SCROLL_PERIOD);
        }
    }

    /**
     * 纵向 RecyclerView 滑动
     *
     * @param y 拖动点纵坐标
     */
    private void recyclerViewScrollVertical(final int x, final int y) {
        if (mVerticalScrollTimerTask != null)
            mVerticalScrollTimerTask.cancel();
        if (y > downScrollBounce) {
            mVerticalScrollTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mCurrentVerticalRecycleView.post(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentVerticalRecycleView.scrollBy(0, VERTICAL_STEP);
                            findViewPositionInCurVerticalRV(x, y);
                        }
                    });
                }
            };
            mVerticalScrollTimer.schedule(mVerticalScrollTimerTask, 0, VERTICAL_SCROLL_PERIOD);
        } else if (y < upScrollBounce) {
            mVerticalScrollTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mCurrentVerticalRecycleView.post(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentVerticalRecycleView.scrollBy(0, -VERTICAL_STEP);
                            findViewPositionInCurVerticalRV(x, y);
                        }
                    });
                }
            };
            mVerticalScrollTimer.schedule(mVerticalScrollTimerTask, 0, VERTICAL_SCROLL_PERIOD);
        }
    }


    private int getHorizontalCurrentPosition(float rowX, float rowY) {
        int[] location = new int[2];
        mHorizontalRecyclerView.getLocationOnScreen(location);
        float x = rowX - location[0];
        float y = rowY - location[1];
        View child = mHorizontalRecyclerView.findChildViewUnder(x, y);
        if (child != null) {
            return mHorizontalRecyclerView.getChildAdapterPosition(child);
        }
        return mPagerPosition;
    }
    //</common>
}
