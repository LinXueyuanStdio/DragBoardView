# DragBoardView

Android 可拖拽的看板视图, 支持项拖拽、列拖拽，支持自动居中。

简单易用，和 `RecyclerView` 用法完全一样！

## 像 `RecyclerView` 一样使用 `DragBoardView` ! [中文文档](https://github.com/LinXueyuanStdio/DragBoardView/blob/master/README_zh.md)

`Recyclerview` 的用法:

<details>
  <summary>1. 添加 recyclerview 的依赖</summary>

```
compile 'com.android.support:recyclerview-v7:23.1.0'
```

在 xml 布局文件中声明:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/item_list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</LinearLayout>
```
</details>

<details>
  <summary>2. 写 item 的布局</summary>

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:orientation="horizontal"
android:layout_width="match_parent"
android:layout_height="wrap_content">

    <TextView
        android:id="@+id/txtChords"
        android:layout_height="wrap_content"
        android:layout_width="wrap_content"
        android:layout_gravity="center_horizontal"
        />
    <TextView
        android:layout_height="wrap_content"
        android:layout_width="wrap_content"
        android:id="@+id/txtLyrics"/>

</LinearLayout>
```
</details>

<details>
  <summary>3. 写 item 对应的数据类.</summary>

什么类都行

```java
public class Item {

    private String name;

    public Item(String n) {
        name = n;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
</details>

<details>
  <summary>4. 写适配器 adapter</summary>

```java
public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private ArrayList<Item> itemList;
    // Constructor of the class
    public ItemArrayAdapter(int layoutId, ArrayList<Item> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.item;
        item.setText(itemList.get(listPosition).getName());
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            item = (TextView) itemView.findViewById(R.id.txtChords);
        }
        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + item.getText());
        }
    }
```
</details>


<details>
  <summary>5. 将适配器 adapter 和 recyclerview 绑定</summary>

```java
public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing list view with the custom adapter
        ArrayList <Item> itemList = new ArrayList<Item>();

        ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item, itemList);
        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemArrayAdapter);

        // Populating list items
        for(int i=0; i<100; i++) {
            itemList.add(new Item("Item " + i));
        }

    }

}
```

</details>

`DragBoardView` 的用法和 `RecyclerView` 一样。

`DragBoardView` 的用法:

<details>
  <summary>1. 添加 DragBoardView 的依赖</summary>

在项目根目录的 `build.gradle` 添加:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

添加依赖

```gradle
dependencies {
    compile 'com.github.LinXueyuanStdio:DragBoardView:v1.0.0'
}
```

在 `xml` 中声明使用

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:fitsSystemWindows="true">

    <android.support.v7.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"/>

    <com.time.cat.dragboardview.DragBoardView
        android:id="@+id/layout_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_below="@id/toolbar"/>

</RelativeLayout>
```

</details>

<details>
  <summary>2. 写 column 和 item 的布局文件.</summary>

column 是列，它必须包含一个 `RecyclerView`

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/col_content_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

        <android.support.v7.widget.RecyclerView
            android:id="@+id/rv"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

</RelativeLayout>
```

item 是列中的项，它的布局请随便写

```xml
<TextView
    android:id="@+id/item_title"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:text="item"/>
```

</details>

<details>
  <summary>3. 写 column 和 item 对应的数据类</summary>

column 类必须实现 `DragColumn` 接口.

```java
public class Entry implements DragColumn {
    private final String name;
    private int columnIndex;
    private final List<DragItem> itemList;

    public Entry(String name, int columnIndex, List<DragItem> items) {
        this.name = name;
        this.columnIndex = columnIndex;
        this.itemList = items;
    }

    public String getName() {
        return name;
    }

    public List<DragItem> getItemList() {
        return itemList;
    }

    @Override
    public int getColumnIndex() {
        return columnIndex;
    }

    @Override
    public void setColumnIndex(int columnIndexInHorizontalRecycleView) {
        //save to database here
    }
}

```

item 类必须实现 `DragItem` 接口.

```java
public class Item implements DragItem {
    private final String itemName;
    private int colIndex;
    private int itemIndex

    public Item(String itemName, int colIndex, int itemIndex) {
        this.itemName = itemName;
        this.colIndex = colIndex;
        this.itemIndex = itemIndex;
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public int getColumnIndex() {
        return colIndex;
    }

    @Override
    public int getItemIndex() {
        return itemIndex;
    }

    @Override
    public void setColumnIndex(int columnIndexInHorizontalRecycleView) {
        //save to database here
    }

    @Override
    public void setItemIndex(int itemIndexInVerticalRecycleView) {
        //save to database here
    }
}
```

</details>

<details>
  <summary>4. 写适配器 adapter</summary>

有两个适配器，一个是列适配器，一个是列中包含的 `RecyclerView` 所需的适配器

```java
public class ColumnAdapter extends HorizontalAdapter<ColumnAdapter.ViewHolder> {...}
public class ItemAdapter extends VerticalAdapter<ItemAdapter.ViewHolder> {...}
```
</details>


<details>
  <summary>5. 将适配器 adapter 和 DragBoardView 绑定</summary>

```java
public class MainActivity extends AppCompatActivity {

    DragBoardView dragBoardView;
    List<DragColumn> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dragBoardView = findViewById(R.id.layout_main);
        mAdapter = new ColumnAdapter(this);
        mAdapter.setData(mData);
        dragBoardView.setHorizontalAdapter(mAdapter);
    }

}
```

</details>

## gif

<img src="art/device-2018-04-04-085351.gif" width="360" height="645"/>

## 截图

|  |  |  |
|:---:|:---:|:---:|
|![截图1](/art/device-2018-04-04-085942.png)|![截图2](/art/device-2018-04-04-090017.png)|![截图3](/art/device-2018-04-04-090030.png)|
|![截图4](/art/device-2018-04-04-090047.png)|![截图5](/art/device-2018-04-04-090115.png)|

## 进阶

Meanwhile, PagerRecyclerView is able to be customized by these 3 params:

|name|format|description|
|:---:|:---:|:---:|
| singlePageFling | boolean | single Page Fling, default false |
| triggerOffset | float | trigger offset, default 0.25f |
| flingFactor | float | fling factor, default 0.15f |

demo 给出了最小实现。

## demo apk
[download](art/app-debug.apk)
