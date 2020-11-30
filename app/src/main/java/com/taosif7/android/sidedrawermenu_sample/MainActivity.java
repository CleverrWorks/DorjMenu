package com.taosif7.android.sidedrawermenu_sample;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.taosif7.android.sidedrawermenu.SideDrawerMenu;
import com.taosif7.android.sidedrawermenu.helpers.DrawerCallbacks;
import com.taosif7.android.sidedrawermenu.models.menuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DrawerCallbacks {

    private SideDrawerMenu drawerMenu;
    private SharedPreferences sharedPreferences;

    // properties
    boolean ltr = false;
    int menu_direction = 1;
    int menu_type = 0;

    // Constants
    final String PROP_LTR = "left_to_right";
    final String PROP_DIRECTION = "direction";
    final String PROP_MENUTYPE = "menu_type";

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get properties from shared Prefs
        sharedPreferences = getSharedPreferences("drawer_lib", MODE_PRIVATE);
        ltr = sharedPreferences.getBoolean(PROP_LTR, ltr);
        menu_direction = sharedPreferences.getInt(PROP_DIRECTION, menu_direction);
        menu_type = sharedPreferences.getInt(PROP_MENUTYPE, menu_type);

        // Set Menu properties
        drawerMenu = new SideDrawerMenu(this, this);
        drawerMenu.setItems(getMenuItems());
        drawerMenu.setMenuType((menu_type == 0) ? SideDrawerMenu.MenuType.MENU_SUBLIST : SideDrawerMenu.MenuType.MENU_PAGES);
        drawerMenu.setHeaderBackground(getResources().getDrawable(R.drawable.drawer_bg));
        drawerMenu.setUserDetails(getDrawable(R.drawable.profile_pic), "Marques Brownlee", "business@MKBHD.com", true);
        drawerMenu.setPersistentButton(null, null, view -> {
            Snackbar.make(getWindow().getDecorView(), "you can set custom Text, Icon and click listener to this button. you can even hide it.", Snackbar.LENGTH_SHORT).show();
            drawerMenu.closeMenu();
        });
        drawerMenu.setCTAButton("Buy Pro version", ContextCompat.getColor(this, android.R.color.holo_green_dark), view -> Snackbar.make(getWindow().getDecorView(), "you can set custom Text and click listener to this button. you can even hide it.", Snackbar.LENGTH_SHORT).show());
        drawerMenu.setHeaderButton(getDrawable(R.drawable.ic_baseline_edit_24), view -> Snackbar.make(getWindow().getDecorView(), "You can set and Icon and action to this button. you can even hide it.", Snackbar.LENGTH_SHORT).show());
        drawerMenu.forceRTLLayout(ltr);
        drawerMenu.attachToActivity(this, (menu_direction == 0) ? SideDrawerMenu.direction.LEFT : SideDrawerMenu.direction.RIGHT);

        // Set control properties
        ((SwitchCompat) findViewById(R.id.rtl_switch)).setChecked(ltr);
        ((Spinner) findViewById(R.id.menu_direction_spinner)).setSelection(menu_direction);
        ((Spinner) findViewById(R.id.menu_type_spinner)).setSelection(menu_type);

        // Set listeners for controls

        findViewById(R.id.toggle_btn).setOnClickListener(view -> drawerMenu.toggleMenu());

        ((SwitchCompat) findViewById(R.id.rtl_switch)).setOnCheckedChangeListener((compoundButton, b) -> {
            sharedPreferences.edit().putBoolean(PROP_LTR, b).apply();
            restartActivity();
        });

        ((Spinner) findViewById(R.id.menu_direction_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == menu_direction) return;
                sharedPreferences.edit().putInt(PROP_DIRECTION, i).apply();
                restartActivity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ((Spinner) findViewById(R.id.menu_type_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == menu_type) return;
                sharedPreferences.edit().putInt(PROP_MENUTYPE, i).apply();
                restartActivity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.toggle_menu) {
            drawerMenu.toggleMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onDrawerMenuItemClick(menuItem menuItem) {
        if (menuItem.getLabel().equals("Travel")) return false;
        ((TextView) findViewById(R.id.selectedItemText)).setText(menuItem.getLabel() + " is Selected");

        drawerMenu.closeMenu();

        menuItem parent = menuItem.getParent();
        if (parent != null) {
            switch (parent.getLabel()) {
                case "Phones":
                    if (menuItem.getLabel().contains("Iphone"))
                        drawerMenu.setItemHighlightColor(ContextCompat.getColor(this, android.R.color.holo_purple));
                    else
                        drawerMenu.setItemHighlightColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
                    break;
                case "Tablets":
                    drawerMenu.setItemHighlightColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                    break;
                case "Watches":
                    drawerMenu.setItemHighlightColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
                    break;
                default:
                    drawerMenu.setItemHighlightColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright));
            }
        }

        return true;
    }

    void restartActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
        //recreate();
    }

    List<menuItem> getMenuItems() {
        List<menuItem> items = new ArrayList<>();
        menuItem devicesMenu = new menuItem("Devices", ContextCompat.getDrawable(this, R.drawable.ic_baseline_devices_other_24));
        menuItem phonesItems = new menuItem("Phones", ContextCompat.getDrawable(this, R.drawable.ic_baseline_smartphone_24));
        menuItem tabletsItems = new menuItem("Tablets", ContextCompat.getDrawable(this, R.drawable.ic_baseline_tablet_android_24));
        menuItem watchItems = new menuItem("Watches", ContextCompat.getDrawable(this, R.drawable.ic_baseline_watch_24));
        phonesItems.addSubItems(
                new menuItem("Xiaomi Mi A3", ContextCompat.getDrawable(this, R.drawable.ic_baseline_phone_android_24)),
                new menuItem("Redmi 3s Prime", ContextCompat.getDrawable(this, R.drawable.ic_baseline_phone_android_24)),
                new menuItem("Galaxy note 9", ContextCompat.getDrawable(this, R.drawable.ic_baseline_phone_android_24)),
                new menuItem("Iphone 5", ContextCompat.getDrawable(this, R.drawable.ic_baseline_phone_iphone_24)),
                new menuItem("Iphone 5s", ContextCompat.getDrawable(this, R.drawable.ic_baseline_phone_iphone_24)),
                new menuItem("Iphone 8", ContextCompat.getDrawable(this, R.drawable.ic_baseline_phone_iphone_24)),
                new menuItem("Iphone 12", ContextCompat.getDrawable(this, R.drawable.ic_baseline_phone_iphone_24)));
        tabletsItems.addSubItems(
                new menuItem("Samsung Tab 4", ContextCompat.getDrawable(this, R.drawable.ic_baseline_tablet_android_24)),
                new menuItem("Samsung Tab 7", ContextCompat.getDrawable(this, R.drawable.ic_baseline_tablet_android_24)),
                new menuItem("Ipad Air", ContextCompat.getDrawable(this, R.drawable.ic_baseline_tablet_mac_24))
        );
        watchItems.addSubItems(
                new menuItem("Watch", ContextCompat.getDrawable(this, R.drawable.ic_baseline_watch_24)),
                new menuItem("Watch 2", ContextCompat.getDrawable(this, R.drawable.ic_baseline_watch_24)),
                new menuItem("Watch 5", ContextCompat.getDrawable(this, R.drawable.ic_baseline_watch_24)),
                new menuItem("Watch 7", ContextCompat.getDrawable(this, R.drawable.ic_baseline_watch_24)),
                new menuItem("Watch 11", ContextCompat.getDrawable(this, R.drawable.ic_baseline_watch_24)),
                new menuItem("Watch 12", ContextCompat.getDrawable(this, R.drawable.ic_baseline_watch_24)),
                new menuItem("Watch 16", ContextCompat.getDrawable(this, R.drawable.ic_baseline_watch_24)));

        devicesMenu.addSubItems(
                phonesItems,
                tabletsItems,
                watchItems);

        items.add(devicesMenu);
        items.add(new menuItem("Travel", ContextCompat.getDrawable(this, R.drawable.ic_baseline_business_center_24)));
        return items;
    }
}