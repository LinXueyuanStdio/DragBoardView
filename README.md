# DragBoardView
可拖拽看板, 只用RecycleView实现

# gif

<img src="art/device-2018-04-04-085351.gif" width="360" height="645"/>

# 截图

|  |  |  |
|:---:|:---:|:---:|
|![截图1](/art/device-2018-04-04-085942.png)|![截图2](/art/device-2018-04-04-090017.png)|![截图3](/art/device-2018-04-04-090030.png)|
|![截图4](/art/device-2018-04-04-090047.png)|![截图5](/art/device-2018-04-04-090115.png)|

### how to use
0. Add it in your `root` `build.gradle` at the end of repositories:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency
```gradle
dependencies {
    compile 'com.github.LinXueyuanStdio:DragBoardView:v1.0.0'
}
```

1. declare `DragLayout` and `PagerRecyclerView` in your xml file
```xml
<com.time.cat.dragboardview.DragLayout
    android:id="@+id/layout_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_below="@id/toolbar">

    <com.time.cat.dragboardview.PagerRecyclerView
        android:id="@+id/rv_lists"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:clipToPadding="false"
        android:paddingLeft="30dp"
        android:paddingRight="30dp"
        android:scrollbars="horizontal"
        app:singlePageFling="true"
        app:triggerOffset="0.1"/>

</com.time.cat.dragboardview.DragLayout>

```
Meanwhile, PagerRecyclerView is able to be customized by these 3 params:

|name|format|description|
|:---:|:---:|:---:|
| singlePageFling | boolean | single Page Fling, default false |
| triggerOffset | float | trigger offset, default 0.25f |
| flingFactor | float | fling factor, default 0.15f |

2. in Java files:
```java
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
```

demo is more clear

### demo apk
[download](art/app-debug.apk)
