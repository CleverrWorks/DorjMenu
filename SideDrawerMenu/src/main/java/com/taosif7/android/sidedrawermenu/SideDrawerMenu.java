package com.taosif7.android.sidedrawermenu;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class SideDrawerMenu extends LinearLayout {

    // Properties
    boolean initialised = false;
    boolean menu_open = false;
    // Components
    Activity bindedActivity;
    // Views
    LinearLayout menu;
    private int screen_width = 600;
    private int menu_width = 360;

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

    private void init() {
        if (initialised) return;

        // Inflate menu
        inflate(getContext(), R.layout.main_layout, this);

        // set properties to meu
        menu = findViewById(R.id.menu_layout);
        menu.getLayoutParams().width = menu_width;
        menu.setTranslationX(menu_width);
        findViewById(R.id.profileImage).setClipToOutline(true);

        // Set the content to activity
        setContent();

        initialised = true;
    }

    private void setContent() {

        if (bindedActivity == null) {
            throw new IllegalStateException("No Activity attached");
        }

        ViewGroup contentParent = (ViewGroup) bindedActivity.findViewById(android.R.id.content).getRootView();
        View content = contentParent.getChildAt(0);
        contentParent.removeView(content);
        ((ViewGroup) findViewById(R.id.user_content)).addView(content);
        contentParent.addView(this);
    }

    /*
     *
     *
     * Exposed Methods
     *
     *
     */

    public void attachToActivity(Activity activity) {
        bindedActivity = activity;

        // Get screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_width = displayMetrics.widthPixels;
        menu_width = (int) (screen_width * 0.6);

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

    public void openMenu() {
        if (menu_open) return;
        menu_open = true;

        // TODO: Animate menu X coordinate
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu, "translationX", 0f);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public void closeMenu() {
        if (!menu_open) return;
        menu_open = false;

        // TODO: Animate menu X coordinate
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu, "translationX", menu_width);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public void toggleMenu() {
        if (menu_open) closeMenu();
        else openMenu();
    }
}
