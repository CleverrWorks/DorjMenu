package com.taosif7.android.sidedrawermenu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.taosif7.android.sidedrawermenu.helpers.DrawerCallbacks;
import com.taosif7.android.sidedrawermenu.models.menuItem;

import java.util.ArrayList;
import java.util.List;

public abstract class DrawerMenuModule extends RelativeLayout {

    List<menuItem> items = new ArrayList<>();
    DrawerCallbacks listener;
    int highlightColor = -1;

    public DrawerMenuModule(Context context) {
        super(context);
        init();
    }

    public DrawerMenuModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawerMenuModule(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DrawerMenuModule(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setItems(List<menuItem> items) {
        this.items.clear();
        this.items.addAll(items);
        buildMenu();
    }

    public void setListener(DrawerCallbacks callbacks) {
        this.listener = callbacks;
    }

    public void setItemHighlightColor(int color) {
        this.highlightColor = color;
    }

    boolean hasSelectedSubItem(List<menuItem> items) {
        boolean hasSelected = false;
        for (menuItem item : items) {
            hasSelected |= item.isSelected();
            if (item.hasSubItems()) hasSelected |= hasSelectedSubItem(item.getSubItems());
        }
        return hasSelected;
    }

    void setSelected(String id, List<menuItem> items) {
        for (menuItem item : items) {
            item.setSelected(item.getId().equals(id));
            if (item.hasSubItems()) setSelected(id, item.getSubItems());
        }
    }

    abstract void init();

    abstract void buildMenu();

}
