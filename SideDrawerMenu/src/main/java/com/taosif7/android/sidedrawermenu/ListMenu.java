package com.taosif7.android.sidedrawermenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.taosif7.android.sidedrawermenu.helpers.DrawerCallbacks;
import com.taosif7.android.sidedrawermenu.models.menuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ListMenu extends RelativeLayout {


    // Data
    List<menuItem> items = new ArrayList<>();
    Map<String, View> highlighterViews = new HashMap<String, View>();
    int highlightColor = -1;

    // View references
    ScrollView SV_menu;
    LinearLayout LL_itemsContainer;
    View topShadow, bottomShadow;

    // Components
    private DrawerCallbacks listener;

    /*
     *
     *
     * Publicly exposed methods
     *
     *
     *
     */

    public ListMenu(Context context) {
        super(context);
        init();
    }

    public ListMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /*
     *
     *
     *
     *
     * Constructors
     *
     *
     *
     *
     */

    public ListMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

        // Update the color
        for (View v : highlighterViews.values())
            if (highlightColor != -1)
                v.findViewById(R.id.item_highlight).setBackgroundTintList(ColorStateList.valueOf(highlightColor));
    }

    private void init() {
        inflate(getContext(), R.layout.menu_type_list, this);
        SV_menu = findViewById(R.id.menu_scrollView);
        LL_itemsContainer = findViewById(R.id.menu_itemsContainer);
        topShadow = findViewById(R.id.menu_shadow_top);
        bottomShadow = findViewById(R.id.menu_shadow_bottom);
        buildMenu();
    }

    private View getMenuItemView(menuItem item, int level) {
        LinearLayout menuView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.menu_item, null);
        ((TextView) menuView.findViewById(R.id.item_label)).setText(item.getLabel());
        ((ImageView) menuView.findViewById(R.id.item_icon)).setImageDrawable(item.getIcon());
        highlighterViews.put(item.getId(), menuView.findViewById(R.id.item_highlight));
        if (highlightColor != -1)
            menuView.findViewById(R.id.item_highlight).setBackgroundTintList(ColorStateList.valueOf(highlightColor));

        if (item.hasSubItems()) {
            for (menuItem subItem : item.getSubItems()) {
                LinearLayout subItemView = (LinearLayout) getMenuItemView(subItem, level + 1);
                subItemView.setVisibility(GONE);
                menuView.addView(subItemView, menuView.getChildCount() - 1);
            }

            menuView.findViewById(R.id.item_arrow).setVisibility(VISIBLE);
            menuView.setTag("collapsed");

            menuView.setOnClickListener(view -> {
                if (menuView.getTag().equals("collapsed")) {
                    for (int i = 1; i < menuView.getChildCount(); i++)
                        menuView.getChildAt(i).setVisibility(VISIBLE);

                    menuView.setTag("expanded");
                    menuView.findViewById(R.id.item_arrow).setRotationX(180);
                    highlighterViews.get(item.getId()).setVisibility(INVISIBLE);

                    menuView.getChildAt(menuView.getChildCount() - 1).setVisibility(VISIBLE);
                } else {
                    for (int i = 1; i < menuView.getChildCount(); i++)
                        menuView.getChildAt(i).setVisibility(GONE);

                    menuView.setTag("collapsed");
                    menuView.findViewById(R.id.item_arrow).setRotationX(0);
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

    private boolean hasSelectedSubItem(List<menuItem> items) {
        boolean hasSelected = false;
        for (menuItem item : items) {
            hasSelected |= item.isSelected();
            if (item.hasSubItems()) hasSelected |= hasSelectedSubItem(item.getSubItems());
        }
        return hasSelected;
    }

    private void setSelected(String id, List<menuItem> items) {
        for (menuItem item : items) {
            item.setSelected(item.getId().equals(id));
            if (item.hasSubItems()) setSelected(id, item.getSubItems());
        }
    }

    public void buildMenu() {
        LL_itemsContainer.removeAllViews();
        for (menuItem item : items) {
            View v = getMenuItemView(item, 1);
            LL_itemsContainer.addView(v);
        }

        // Add shadow gradients for menu scrollview
        SV_menu.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int bottom = (SV_menu.getChildAt(SV_menu.getChildCount() - 1)).getHeight() - SV_menu.getHeight() - SV_menu.getScrollY();
            findViewById(R.id.menu_shadow_top).setAlpha(Math.min(1, Math.abs(SV_menu.getScrollY() / 50f)));
            findViewById(R.id.menu_shadow_bottom).setAlpha(Math.min(1, Math.abs(bottom / 50f)));
        });
    }
}
