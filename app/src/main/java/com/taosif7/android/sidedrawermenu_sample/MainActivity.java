package com.taosif7.android.sidedrawermenu_sample;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.taosif7.android.sidedrawermenu.SideDrawerMenu;
import com.taosif7.android.sidedrawermenu.helpers.DrawerCallbacks;
import com.taosif7.android.sidedrawermenu.models.menuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DrawerCallbacks {

    private SideDrawerMenu menu;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        menu = new SideDrawerMenu(this, this);
        menu.setItems(items);
        menu.setMenuType(SideDrawerMenu.MenuType.MENU_SUBLIST);
        menu.setUserDetails(ContextCompat.getDrawable(this, R.drawable.jhon), "Jhon Cena", "YouCantSeeMe@wwe.com");
        menu.setPersistentButton(null, null, view -> {
            Toast.makeText(MainActivity.this, "Settings activity will open up!", Toast.LENGTH_SHORT).show();
            menu.closeMenu();
        });
        menu.attachToActivity(this, SideDrawerMenu.direction.RIGHT);

        findViewById(R.id.toggle_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.toggleMenu();
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
            menu.toggleMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onDrawerMenuItemClick(menuItem menuItem) {
        if (menuItem.getLabel().equals("Travel")) return false;
        ((TextView) findViewById(R.id.selectedItemText)).setText(menuItem.getLabel() + " is Selected");

        menu.closeMenu();

        menuItem parent = menuItem.getParent();
        if (parent != null) {
            switch (parent.getLabel()) {
                case "Phones":
                    if (menuItem.getLabel().contains("Iphone"))
                        menu.setItemHighlightColor(ContextCompat.getColor(this, android.R.color.holo_purple));
                    else
                        menu.setItemHighlightColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
                    break;
                case "Tablets":
                    menu.setItemHighlightColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                    break;
                case "Watches":
                    menu.setItemHighlightColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
                    break;
                default:
                    menu.setItemHighlightColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright));
            }
        }

        return true;
    }
}