package com.taosif7.android.sidedrawermenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.taosif7.android.sidedrawermenu.helpers.HelperMethods;
import com.taosif7.android.sidedrawermenu.models.menuItem;

import java.util.List;

class PageMenu extends DrawerMenuModule {

    // Constants
    final String TAG_HIGHLIGHTER = "HighlighterView";

    // View references
    ScrollView SV_menu;
    HorizontalScrollView HSV_nav;
    LinearLayout LL_itemsContainer, LL_navItemsContainer;
    View topShadow, bottomShadow;

    /*
     *
     *
     * Constructors
     *
     *
     *
     */

    public PageMenu(Context context) {
        super(context);
    }

    public PageMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PageMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

        // Update the color of highlighter views
        if (highlightColor != -1) for (View hv : getAllHighlighterViews())
            hv.setBackgroundTintList(ColorStateList.valueOf(highlightColor));
    }

    /*
     *
     *
     *
     *
     * Private methods
     *
     *
     *
     *
     */

    void init() {
        inflate(getContext(), R.layout.menu_type_page, this);
        SV_menu = findViewById(R.id.menu_scrollView);
        HSV_nav = findViewById(R.id.menu_nav);
        LL_itemsContainer = findViewById(R.id.menu_itemsContainer);
        LL_navItemsContainer = findViewById(R.id.menu_navItemsContainer);
        topShadow = findViewById(R.id.menu_shadow_top);
        bottomShadow = findViewById(R.id.menu_shadow_bottom);
        buildMenu();
    }

    private View getMenuItemView(menuItem item) {
        LinearLayout menuView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.menu_item, null);
        ((TextView) menuView.findViewById(R.id.item_label)).setText(item.getLabel());
        ((ImageView) menuView.findViewById(R.id.item_icon)).setImageDrawable(item.getIcon());

        View itemHighlighterView = menuView.findViewById(R.id.item_highlight);
        itemHighlighterView.setTag(TAG_HIGHLIGHTER);
        if (highlightColor != -1)
            itemHighlighterView.setBackgroundTintList(ColorStateList.valueOf(highlightColor));


        if (item.hasSubItems()) {

            if (hasSelectedSubItem(item.getSubItems()))
                itemHighlighterView.setVisibility(VISIBLE);

            menuView.findViewById(R.id.item_arrow).setVisibility(VISIBLE);
            menuView.findViewById(R.id.item_arrow).setRotation(270);

            menuView.setOnClickListener(view -> {

                showMenuItems(item.getSubItems());
                setNavItemClear(item);

            });

        } else {

            if (item.isSelected())
                itemHighlighterView.setVisibility(item.isSelected() ? VISIBLE : INVISIBLE);

            menuView.setOnClickListener(view -> {
                if (listener != null) {
                    boolean highlight = listener.onDrawerMenuItemClick(item);
                    if (highlight) {

                        // Hide all highlighter views
                        for (View hv : getAllHighlighterViews()) hv.setVisibility(INVISIBLE);

                        setSelected(item.getId(), items);
                        itemHighlighterView.setVisibility(VISIBLE);
                    }

                }
            });
        }

        // Set Item indent
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) menuView.findViewById(R.id.item_body).getLayoutParams();
        params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        menuView.findViewById(R.id.item_body).setLayoutParams(params);

        return menuView;
    }

    public void buildMenu() {
        showMenuItems(items);

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

    private void showMenuItems(List<menuItem> items) {
        LL_itemsContainer.removeAllViews();
        for (menuItem item : items) {
            View v = getMenuItemView(item);
            LL_itemsContainer.addView(v);
        }
    }

    private void setNavItems(menuItem item) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.nav_item, null);
        ((TextView) itemView.findViewById(R.id.nav_item_label)).setText(item.getLabel());
        LL_navItemsContainer.addView(itemView, 0);

        itemView.setOnClickListener(view -> {
            showMenuItems(item.getSubItems());
            setNavItemClear(item);
        });


        if (item.getParent() != null) {
            setNavItems(item.getParent());
        } else {
            View itemViewRoot = LayoutInflater.from(getContext()).inflate(R.layout.nav_item, null);
            ((TextView) itemViewRoot.findViewById(R.id.nav_item_label)).setText("Root");
            LL_navItemsContainer.addView(itemViewRoot, 0);

            itemViewRoot.setOnClickListener(view -> {
                showMenuItems(items);
                LL_navItemsContainer.removeAllViews();
            });
        }

        HSV_nav.post(() -> HSV_nav.fullScroll(HorizontalScrollView.FOCUS_RIGHT));

    }

    private void setNavItemClear(menuItem item) {
        LL_navItemsContainer.removeAllViews();
        setNavItems(item);
    }

    List<View> getAllHighlighterViews() {
        return HelperMethods.findViewWithTagRecursively(LL_itemsContainer, TAG_HIGHLIGHTER);
    }

}
