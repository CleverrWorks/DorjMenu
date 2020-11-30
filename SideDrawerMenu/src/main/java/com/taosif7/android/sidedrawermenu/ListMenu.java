package com.taosif7.android.sidedrawermenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.taosif7.android.sidedrawermenu.models.menuItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ListMenu extends DrawerMenuModule {

    // Data
    Map<String, View> highlighterViews = new HashMap<String, View>();

    // View references
    ScrollView SV_menu;
    LinearLayout LL_itemsContainer;
    View topShadow, bottomShadow;

    /*
     *
     *
     * Constructors
     *
     *
     *
     */

    public ListMenu(Context context) {
        super(context);
    }

    public ListMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ListMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /*
     *
     *
     * Publicly exposed methods
     *
     *
     *
     */

    public void setItemHighlightColor(int color) {
        super.setItemHighlightColor(color);

        // Update the color
        for (View v : highlighterViews.values())
            if (highlightColor != -1)
                v.findViewById(R.id.item_highlight).setBackgroundTintList(ColorStateList.valueOf(highlightColor));
    }

    /*
     *
     *
     * Internal methods
     *
     *
     *
     */

    void init() {
        inflate(getContext(), R.layout.menu_type_list, this);
        SV_menu = findViewById(R.id.menu_scrollView);
        LL_itemsContainer = findViewById(R.id.menu_itemsContainer);
        topShadow = findViewById(R.id.menu_shadow_top);
        bottomShadow = findViewById(R.id.menu_shadow_bottom);
        buildMenu();
    }

    private View getMenuItemView(menuItem item, int level, boolean lastElement) {
        LinearLayout menuView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.menu_item, null);
        ((TextView) menuView.findViewById(R.id.item_label)).setText(item.getLabel());
        ((ImageView) menuView.findViewById(R.id.item_icon)).setImageDrawable(item.getIcon());
        highlighterViews.put(item.getId(), menuView.findViewById(R.id.item_highlight));
        if (highlightColor != -1)
            menuView.findViewById(R.id.item_highlight).setBackgroundTintList(ColorStateList.valueOf(highlightColor));

        if (item.hasSubItems()) {
            List<menuItem> subItems = item.getSubItems();
            for (int i = 0, subItemsSize = subItems.size(); i < subItemsSize; i++) {
                menuItem subItem = subItems.get(i);
                LinearLayout subItemView = (LinearLayout) getMenuItemView(subItem, level + 1, i == (subItemsSize - 1));
                subItemView.setVisibility(GONE);
                menuView.addView(subItemView, menuView.getChildCount() - 1);
            }

            menuView.findViewById(R.id.item_arrow).setVisibility(VISIBLE);
            menuView.findViewById(R.id.item_arrow).setRotation(90);
            menuView.setTag("collapsed");

            menuView.setOnClickListener(view -> {
                if (menuView.getTag().equals("collapsed")) {
                    for (int i = 1; i < menuView.getChildCount(); i++)
                        menuView.getChildAt(i).setVisibility(VISIBLE);

                    menuView.setTag("expanded");
                    menuView.findViewById(R.id.item_arrow).setRotation(270);
                    highlighterViews.get(item.getId()).setVisibility(INVISIBLE);

                    menuView.getChildAt(menuView.getChildCount() - 1).setVisibility(lastElement ? GONE : VISIBLE);
                } else {
                    for (int i = 1; i < menuView.getChildCount(); i++)
                        menuView.getChildAt(i).setVisibility(GONE);

                    menuView.setTag("collapsed");
                    menuView.findViewById(R.id.item_arrow).setRotation(90);
                    highlighterViews.get(item.getId()).setVisibility(hasSelectedSubItem(item.getSubItems()) ? VISIBLE : INVISIBLE);

                    menuView.getChildAt(menuView.getChildCount() - 1).setVisibility(GONE);
                }
            });
        } else {
            menuView.setTag(item.getId());
            menuView.setOnClickListener(view -> {
                if (listener != null) {
                    boolean highlight = listener.onDrawerMenuItemClick(item);
                    if (highlight) {
                        setSelected(item.getId(), items);
                        highlightMenuItem(items);
                    }
                }
            });
        }

        // Set Item indent
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) menuView.findViewById(R.id.item_body).getLayoutParams();
        params.leftMargin = level * (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        menuView.findViewById(R.id.item_body).setLayoutParams(params);

        return menuView;
    }

    private boolean highlightMenuItem(List<menuItem> items) {
        boolean anySelected = false;
        for (menuItem item : items) {
            anySelected |= item.isSelected();
            if (item.hasSubItems()) anySelected |= highlightMenuItem(item.getSubItems());
            highlighterViews.get(item.getId()).setVisibility(item.isSelected() ? VISIBLE : INVISIBLE);
        }

        return anySelected;
    }

    public void buildMenu() {
        LL_itemsContainer.removeAllViews();
        for (int i = 0, itemsSize = items.size(); i < itemsSize; i++) {
            menuItem item = items.get(i);
            View v = getMenuItemView(item, 0, i == itemsSize - 1);
            LL_itemsContainer.addView(v);
        }

        // Add shadow gradients for menu scrollview
        SV_menu.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int bottom = (SV_menu.getChildAt(SV_menu.getChildCount() - 1)).getHeight() - SV_menu.getHeight() - SV_menu.getScrollY();
            findViewById(R.id.menu_shadow_top).setAlpha(Math.min(1, Math.abs(SV_menu.getScrollY() / 50f)));
            findViewById(R.id.menu_shadow_bottom).setAlpha(Math.min(1, Math.abs(bottom / 50f)));
        });
        SV_menu.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> {
            int bottom = (SV_menu.getChildAt(SV_menu.getChildCount() - 1)).getHeight() - SV_menu.getHeight() - SV_menu.getScrollY();
            findViewById(R.id.menu_shadow_top).setAlpha(Math.min(1, Math.abs(SV_menu.getScrollY() / 50f)));
            findViewById(R.id.menu_shadow_bottom).setAlpha(Math.min(1, Math.abs(bottom / 50f)));
        });
    }
}
