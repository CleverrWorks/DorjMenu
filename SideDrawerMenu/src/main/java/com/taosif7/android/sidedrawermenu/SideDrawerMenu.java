package com.taosif7.android.sidedrawermenu;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
    View menuNavHelper;
    private int screen_width = 600;

    // Components
    Activity bindedActivity;

    // Views
    LinearLayout menu;
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
        menuNavHelper = findViewById(R.id.menu_nav_helper);
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

        // Set listeners
        GestureDetector gd = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

                float min_swipe_size = (float) (screen_width * 0.2);

                float start_x = motionEvent.getAxisValue(MotionEvent.AXIS_X);
                float end_x = motionEvent1.getAxisValue(MotionEvent.AXIS_X);
                float start_y = motionEvent.getAxisValue(MotionEvent.AXIS_Y);
                float end_y = motionEvent1.getAxisValue(MotionEvent.AXIS_Y);

                float x_mag = Math.abs(start_x - end_x);
                float y_mag = Math.abs(start_y - end_y);

                if (start_x < end_x && x_mag > y_mag && x_mag >= min_swipe_size) {
                    closeMenu();
                    return true;
                }
                return false;
            }
        });

        menu.setOnTouchListener((view, motionEvent) -> {
            boolean event = gd.onTouchEvent(motionEvent);
            if (!event) view.performClick();
            return !event;
        });

        menuNavHelper.setOnClickListener(view -> toggleMenu());
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

    public void openMenu() {
        if (menu_open) return;
        menu_open = true;

        // Animate menu X coordinate
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu, "translationX", 0f);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        // Change menu nav helper size
        ViewGroup.LayoutParams lp = menuNavHelper.getLayoutParams();
        lp.width = screen_width - menu_width;
        menuNavHelper.setLayoutParams(lp);
    }

    public void closeMenu() {
        if (!menu_open) return;
        menu_open = false;

        // Animate menu X coordinate
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu, "translationX", menu_width);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        // Change menu nav helper size
        ViewGroup.LayoutParams lp = menuNavHelper.getLayoutParams();
        lp.width = (int) (screen_width * 0.1);
        menuNavHelper.setLayoutParams(lp);
    }

    public void toggleMenu() {
        if (menu_open) closeMenu();
        else openMenu();
    }
}
