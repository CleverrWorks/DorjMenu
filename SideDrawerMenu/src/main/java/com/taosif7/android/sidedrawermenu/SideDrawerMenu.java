package com.taosif7.android.sidedrawermenu;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.taosif7.android.sidedrawermenu.helpers.ContentDragTouchListener;
import com.taosif7.android.sidedrawermenu.helpers.DrawerCallbacks;
import com.taosif7.android.sidedrawermenu.helpers.MenuDragTouchListener;
import com.taosif7.android.sidedrawermenu.models.menuItem;

import java.util.ArrayList;
import java.util.List;

public class SideDrawerMenu extends LinearLayout {

    // Properties
    public int menu_width = 360;
    public FrameLayout user_content;
    boolean initialised = false;
    boolean menu_open = false;
    private int screen_width = 600;
    public direction menu_direction = direction.RIGHT;

    // Views
    public RelativeLayout menu;
    public ImageView IV_header_bg;
    // Menu Items Data
    List<menuItem> items = new ArrayList<>();
    Activity bindedActivity;
    int highlightColor = -1;

    // Other
    DrawerCallbacks listener;
    private ListMenu menuItemsList;

    public SideDrawerMenu(Context context, DrawerCallbacks listener) {
        super(context);
        this.listener = listener;
    }

    private void setContent() {

        if (bindedActivity == null) {
            throw new IllegalStateException("No Activity attached");
        }

        ViewGroup mainContainer = (ViewGroup) bindedActivity.findViewById(android.R.id.content).getRootView();
        View content = mainContainer.getChildAt(0);
        ViewGroup user_container = bindedActivity.findViewById(android.R.id.content);
        mainContainer.removeView(content);

        // Add user content in out layout
        ((ViewGroup) findViewById(R.id.user_content)).addView(content);
        mainContainer.addView(this);

        // Touch Listeners
        ContentDragTouchListener contentDrag = new ContentDragTouchListener(this);
        MenuDragTouchListener menuDrag = new MenuDragTouchListener(this);

        menu.setOnTouchListener(menuDrag);
        user_content.setOnTouchListener(contentDrag);
        ((ViewGroup) user_container.getChildAt(0)).setOnTouchListener(contentDrag);
        ((ViewGroup) user_container.getChildAt(0)).setFitsSystemWindows(true);

        // Build menu
        menuItemsList = findViewById(R.id.menu);
        menuItemsList.setItems(items);
        if (highlightColor != -1) menuItemsList.setItemHighlightColor(highlightColor);
        if (listener != null) menuItemsList.setListener(listener);
    }

    private void init() {
        if (initialised) return;

        // Inflate menu
        inflate(getContext(), R.layout.main_layout, this);

        // set properties to meu
        menu = findViewById(R.id.menu_layout);
        user_content = findViewById(R.id.user_content);
        IV_header_bg = findViewById(R.id.drawer_header_bg);
        View menuEndBorder = findViewById(R.id.menuEndBorder);


        // set properties
        CoordinatorLayout.LayoutParams params_menu = (CoordinatorLayout.LayoutParams) menu.getLayoutParams();
        params_menu.insetEdge = (menu_direction == direction.RIGHT) ? Gravity.END : Gravity.START;
        params_menu.gravity = (menu_direction == direction.RIGHT) ? Gravity.END : Gravity.START;
        menu.setLayoutParams(params_menu);

        CoordinatorLayout.LayoutParams params_content = (CoordinatorLayout.LayoutParams) user_content.getLayoutParams();
        params_content.dodgeInsetEdges = (menu_direction == direction.RIGHT) ? Gravity.END : Gravity.START;
        user_content.setLayoutParams(params_content);

        menu.getLayoutParams().width = menu_width;
        menu.setTranslationX((menu_direction == direction.RIGHT) ? menu_width : -menu_width);
        findViewById(R.id.profileImage).setClipToOutline(true);

        RelativeLayout.LayoutParams params_headerImage = (RelativeLayout.LayoutParams) IV_header_bg.getLayoutParams();
        params_headerImage.addRule((menu_direction == direction.RIGHT) ? RelativeLayout.ALIGN_PARENT_START : RelativeLayout.ALIGN_PARENT_END);
        IV_header_bg.setLayoutParams(params_headerImage);

        RelativeLayout.LayoutParams params_border = (RelativeLayout.LayoutParams) menuEndBorder.getLayoutParams();
        params_border.addRule((menu_direction == direction.RIGHT) ? RelativeLayout.ALIGN_PARENT_START : RelativeLayout.ALIGN_PARENT_END);
        menuEndBorder.setLayoutParams(params_border);

        // Set the content to activity
        setContent();

        initialised = true;
    }

    public SideDrawerMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideDrawerMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SideDrawerMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // Constants
    public enum direction {LEFT, RIGHT}

    public void attachToActivity(Activity activity, direction direction) {
        bindedActivity = activity;
        menu_direction = direction;

        // Get screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_width = displayMetrics.widthPixels;
        menu_width = (int) (screen_width * 0.7);

        // Attach init method to activity's onResume
        activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                init();
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                closeMenu();
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    /*
     *
     *
     * Exposed Methods
     *
     *
     */

    public void setItems(List<menuItem> items) {
        this.items.clear();
        this.items.addAll(items);
        if (menuItemsList != null) menuItemsList.setItems(items);
    }

    public void setItemHighlightColor(int color) {
        this.highlightColor = color;
        if (menuItemsList != null) menuItemsList.setItemHighlightColor(color);
    }

    public void closeMenu() {

        // Animate menu X coordinate
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu, "translationX", (menu_direction == direction.RIGHT) ? menu_width : -menu_width);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        // Animate drawer Image bg
        ValueAnimator anim = ValueAnimator.ofInt(IV_header_bg.getMeasuredWidth(), 0);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams drawerImageParams = IV_header_bg.getLayoutParams();
            drawerImageParams.width = val;
            IV_header_bg.setLayoutParams(drawerImageParams);
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(400);
        anim.start();
    }

    public void openMenu() {

        // Animate menu X coordinate
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu, "translationX", 0f);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        // Animate drawer Image bg
        ValueAnimator anim = ValueAnimator.ofInt(IV_header_bg.getMeasuredWidth(), menu_width);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams drawerImageParams = IV_header_bg.getLayoutParams();
            drawerImageParams.width = val;
            IV_header_bg.setLayoutParams(drawerImageParams);
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(200);
        anim.start();
    }

    public void toggleMenu() {
        if (menu_open) closeMenu();
        else openMenu();
    }
}
