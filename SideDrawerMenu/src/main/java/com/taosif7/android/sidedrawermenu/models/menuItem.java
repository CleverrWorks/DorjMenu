package com.taosif7.android.sidedrawermenu.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class menuItem {
    String id;
    String label;
    Drawable icon;
    List<menuItem> subItems = new ArrayList<>();

    boolean selected = false;

    public menuItem(String label, Drawable icon) {
        this.label = label;
        this.icon = icon;
        this.id = UUID.randomUUID().toString();
    }

    public menuItem(String label, @DrawableRes int DrawableId, Context c) {
        this.label = label;
        this.icon = ContextCompat.getDrawable(c, DrawableId);
        this.id = UUID.randomUUID().toString();
    }

    public void addSubItem(menuItem item) {
        this.subItems.add(item);
    }

    public void addSubItems(menuItem... items) {
        for (menuItem item : items) addSubItem(item);
    }

    public boolean hasSubItems() {
        return subItems.size() > 0;
    }

    public List<menuItem> getSubItems() {
        return subItems;
    }

    public String getLabel() {
        return label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
