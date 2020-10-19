package com.taosif7.android.sidedrawermenu.helpers;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.taosif7.android.sidedrawermenu.SideDrawerMenu;

public class ContentDragTouchListener implements View.OnTouchListener {

    public float start_x = 0;

    int menu_open_value, menu_close_value;
    Context context;
    SideDrawerMenu drawer;

    public ContentDragTouchListener(SideDrawerMenu drawer) {
        this.context = drawer.getContext();
        this.drawer = drawer;
        menu_open_value = 0;
        menu_close_value = (drawer.menu_direction == SideDrawerMenu.direction.RIGHT ? drawer.menu_width : -drawer.menu_width);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            start_x = motionEvent.getRawX() - view.getX();
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            float displacement = motionEvent.getRawX() - start_x;

            if (drawer.menu_direction == SideDrawerMenu.direction.RIGHT) {
                if (drawer.menu.getTranslationX() + displacement + drawer.menu_width >= menu_open_value)
                    drawer.menu.setTranslationX(displacement + drawer.menu_width);
            } else {
                if (drawer.menu.getTranslationX() + displacement - drawer.menu_width <= menu_open_value)
                    drawer.menu.setTranslationX(displacement - drawer.menu_width);
            }


            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            start_x = 0;

            if (drawer.menu_direction == SideDrawerMenu.direction.RIGHT) {
                if (drawer.menu.getTranslationX() != menu_close_value || drawer.menu.getTranslationX() != menu_open_value) {
                    if (drawer.menu.getTranslationX() >= (menu_close_value * 0.5))
                        drawer.closeMenu();
                    else drawer.openMenu();
                } else view.performClick();

            } else {
                if (drawer.menu.getTranslationX() != menu_close_value || drawer.menu.getTranslationX() != menu_open_value) {
                    if (drawer.menu.getTranslationX() <= (menu_close_value * 0.5))
                        drawer.closeMenu();
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
