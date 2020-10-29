package com.taosif7.android.sidedrawermenu.helpers;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.taosif7.android.sidedrawermenu.SideDrawerMenu;

public class MenuDragTouchListener implements View.OnTouchListener {

    public float down_x = 0;

    int menu_open_value, menu_close_value;
    Context context;
    SideDrawerMenu drawer;

    public MenuDragTouchListener(SideDrawerMenu drawer) {
        this.context = drawer.getContext();
        this.drawer = drawer;
        menu_open_value = 0;
        menu_close_value = (drawer.menu_direction == SideDrawerMenu.direction.RIGHT ? drawer.menu_width : -drawer.menu_width);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            down_x = motionEvent.getRawX();
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            float displacement = motionEvent.getRawX() - down_x;

            ViewGroup.LayoutParams drawerImageParams = drawer.IV_header_bg.getLayoutParams();

            if (drawer.menu_direction == SideDrawerMenu.direction.RIGHT) {
                if (view.getTranslationX() + displacement >= menu_open_value) {
                    view.setTranslationX(displacement);
                    drawerImageParams.width = (int) (drawer.menu_width - drawer.menu.getTranslationX() + 2);
                }
            } else {
                if (view.getTranslationX() + displacement <= menu_open_value) {
                    view.setTranslationX(displacement);
                    drawerImageParams.width = (int) Math.abs(drawer.menu_width + drawer.menu.getTranslationX() + 2);
                }
            }

            // Set drawer header bg Layout params
            drawer.IV_header_bg.setLayoutParams(drawerImageParams);

            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            down_x = 0;

            if (drawer.menu_direction == SideDrawerMenu.direction.RIGHT) {
                if (view.getTranslationX() != menu_close_value || view.getTranslationX() != menu_open_value) {
                    if (view.getTranslationX() >= (menu_close_value * 0.5)) drawer.closeMenu();
                    else drawer.openMenu();
                } else view.performClick();

            } else {
                if (view.getTranslationX() != menu_close_value || view.getTranslationX() != menu_open_value) {
                    if (view.getTranslationX() <= (menu_close_value * 0.5)) drawer.closeMenu();
                    else drawer.openMenu();
                } else view.performClick();
            }

            return false;
        } else {
            view.performClick();
            return false;
        }
    }
}
