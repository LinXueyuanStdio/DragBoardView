package com.time.cat.demo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.time.cat.demo.adapter.RecyclerViewHorizontalDataAdapter;
import com.time.cat.demo.data.Entry;
import com.time.cat.demo.data.Item;
import com.time.cat.dragboardview.callback.DragActivityCallBack;
import com.time.cat.dragboardview.helper.DragHelper;
import com.time.cat.dragboardview.DragLayout;
import com.time.cat.dragboardview.PagerRecyclerView;
import com.time.cat.dragboardview.utils.AttrAboutPhone;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription 主界面
 * @usage null
 */
public class MainActivity extends AppCompatActivity implements DragActivityCallBack {

    private PagerRecyclerView mRecyclerView;
    private RecyclerViewHorizontalDataAdapter mAdapter;
    private DragLayout mLayoutMain;
    private DragHelper mDragHelper;

    private MainActivity mActivity;

    private List<Entry> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        String brand = Build.BRAND;
        if (brand.contains("Xiaomi")) {
            setXiaomiDarkMode(this);
        } else if (brand.contains("Meizu")) {
            setMeizuDarkMode(this);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLayoutMain = (DragLayout) findViewById(R.id.layout_main);
        mRecyclerView = (PagerRecyclerView) findViewById(R.id.rv_lists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFlingFactor(0.1f);

        mAdapter = new RecyclerViewHorizontalDataAdapter(mActivity, mData);
        View footer = getLayoutInflater().inflate(R.layout.recyclerview_footer_addlist, null, false);
        mAdapter.setFooterView(footer);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnPageChangedListener(mOnPagerChangedListener);
        mRecyclerView.addOnLayoutChangeListener(mOnLayoutChangedListener);
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        mDragHelper = new DragHelper(mActivity);
        mDragHelper.bindHorizontalRecyclerView(mRecyclerView);
        mLayoutMain.setDragHelper(mDragHelper);
        getDataAndRefreshView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        AttrAboutPhone.saveAttr(this);
        AttrAboutPhone.initScreen(this);
        super.onWindowFocusChanged(hasFocus);
    }

    private void getDataAndRefreshView() {
        for (int i = 0; i < 3; i ++) {
            List<Item> itemList = new ArrayList<>();
            for (int j = 0; j < 5; j ++) {
                itemList.add(new Item("entry " + i + " item id " + j, "item name " + j, "info " + j));
            }
            mData.add(new Entry("entry id " + i, "name " + i, itemList));
        }
        mAdapter.notifyDataSetChanged();
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

    @Override
    public DragHelper getDragHelper() {
        return mDragHelper;
    }

    // below is useless

    /**
     * 小米手机设置darkMode
     */
    public static boolean setXiaomiDarkMode(Activity activity) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkModeFlag, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 魅族手机设置darkMode
     */
    public static boolean setMeizuDarkMode(Activity activity) {
        boolean result = false;
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class
                    .getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            value |= bit;
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
            result = true;
        } catch (Exception e) {
        }
        return result;
    }
}

