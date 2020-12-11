package saleh.taosif7.dorjmenu.helpers;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import saleh.taosif7.dorjmenu.DorjMenu;


public class ContentDragTouchListener implements View.OnTouchListener {

    public float start_x = 0, start_y = 0;

    int menu_open_value, menu_close_value;
    Context context;
    DorjMenu drawer;

    public ContentDragTouchListener(DorjMenu drawer) {
        this.context = drawer.getContext();
        this.drawer = drawer;
        menu_open_value = 0;
        menu_close_value = (drawer.menu_direction == DorjMenu.direction.RIGHT ? drawer.menu_width : -drawer.menu_width);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            start_x = motionEvent.getRawX() - drawer.user_content.getX();
            start_y = motionEvent.getRawY() - view.getY();
            return false;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            float displacement_x = motionEvent.getRawX() - start_x;
            float displacement_y = motionEvent.getRawY() - start_y;

            if (Math.abs(displacement_x) >= Math.abs(displacement_y)) {

                ViewGroup.LayoutParams drawerImageParams = drawer.IV_header_bg.getLayoutParams();

                if (drawer.menu_direction == DorjMenu.direction.RIGHT) {
                    if (drawer.menu.getTranslationX() + displacement_x + drawer.menu_width >= menu_open_value) {
                        drawer.menu.setTranslationX(displacement_x + drawer.menu_width);
                        drawerImageParams.width = (int) (drawer.menu_width - drawer.menu.getTranslationX() + 2);
                    }
                } else {
                    if (drawer.menu.getTranslationX() + displacement_x - drawer.menu_width <= menu_open_value) {
                        drawer.menu.setTranslationX(displacement_x - drawer.menu_width);
                        drawerImageParams.width = (int) Math.abs(drawer.menu_width + drawer.menu.getTranslationX() + 2);
                    }
                }

                // Set drawer header bg Layout params
                drawer.IV_header_bg.setLayoutParams(drawerImageParams);

                return true;
            } else return false;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            start_x = 0;

            if (drawer.menu.getTranslationX() == menu_open_value) {
                drawer.closeMenu();
            } else if (drawer.menu.getTranslationX() != menu_close_value || drawer.menu.getTranslationX() != menu_open_value) {

                if (drawer.menu_direction == DorjMenu.direction.RIGHT) {
                    if (drawer.menu.getTranslationX() >= (menu_close_value * 0.5))
                        drawer.closeMenu();
                    else drawer.openMenu();
                } else {
                    if (drawer.menu.getTranslationX() <= (menu_close_value * 0.5))
                        drawer.closeMenu();
                    else drawer.openMenu();
                }

            } else view.performClick();

            return false;
        } else {
            return false;
        }
    }
}
