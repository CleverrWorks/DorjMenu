package com.taosif7.android.sidedrawermenu.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

public class menuItem {
    String label;
    Drawable icon;

    public menuItem(String label, Drawable icon) {
        this.label = label;
        this.icon = icon;
    }

    public menuItem(String label, @DrawableRes int DrawableId, Context c) {
        this.label = label;
        this.icon = ContextCompat.getDrawable(c, DrawableId);
    }

}
