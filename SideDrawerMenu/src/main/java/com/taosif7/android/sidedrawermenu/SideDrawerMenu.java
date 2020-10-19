package com.taosif7.android.sidedrawermenu;

import android.animation.ObjectAnimator;
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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.taosif7.android.sidedrawermenu.helpers.ContentDragTouchListener;
import com.taosif7.android.sidedrawermenu.helpers.MenuDragTouchListener;

public class SideDrawerMenu extends LinearLayout {

    public int menu_width = 360;

    // Properties
    boolean initialised = false;
    boolean menu_open = false;
    private int screen_width = 600;
    public direction menu_direction = direction.RIGHT;
    // Views
    public LinearLayout menu;

    // Components
    Activity bindedActivity;
    LinearLayout user_content;

    private void init() {
        if (initialised) return;

        // Inflate menu
        inflate(getContext(), R.layout.main_layout, this);

        // set properties to meu
        menu = findViewById(R.id.menu_layout);
        user_content = findViewById(R.id.user_content);


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

        // Set the content to activity
        setContent();

        initialised = true;
    }

    public SideDrawerMenu(Context context) {
        super(context);
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

    private void setContent() {

        if (bindedActivity == null) {
            throw new IllegalStateException("No Activity attached");
        }

        ViewGroup mainContainer = (ViewGroup) bindedActivity.findViewById(android.R.id.content).getRootView();
        View content = mainContainer.getChildAt(0);
        mainContainer.removeView(content);

        // Add user content in out layout
        ((ViewGroup) findViewById(R.id.user_content)).addView(content);
        mainContainer.addView(this);

        menu.setOnTouchListener(new MenuDragTouchListener(this));
        user_content.setOnTouchListener(new ContentDragTouchListener(this));
    }

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

    public void closeMenu() {

        // Animate menu X coordinate
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu, "translationX", (menu_direction == direction.RIGHT) ? menu_width : -menu_width);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public void openMenu() {

        // Animate menu X coordinate
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu, "translationX", 0f);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    // Constants
    public enum direction {LEFT, RIGHT}

    public void toggleMenu() {
        if (menu_open) closeMenu();
        else openMenu();
    }
}
