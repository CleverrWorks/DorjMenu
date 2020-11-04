package com.taosif7.android.sidedrawermenu.helpers;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class HelperMethods {

    /**
     * Get all the views which matches the given Tag recursively
     *
     * @param root parent view. for e.g. Layouts
     * @param tag  tag to look for
     * @return List of views
     */
    public static List<View> findViewWithTagRecursively(ViewGroup root, Object tag) {
        List<View> allViews = new ArrayList<View>();

        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = root.getChildAt(i);

            if (childView instanceof ViewGroup) {
                allViews.addAll(findViewWithTagRecursively((ViewGroup) childView, tag));
            } else {
                final Object tagView = childView.getTag();
                if (tagView != null && tagView.equals(tag))
                    allViews.add(childView);
            }
        }

        return allViews;
    }

}
