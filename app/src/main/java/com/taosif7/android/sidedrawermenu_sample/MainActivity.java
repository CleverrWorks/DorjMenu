package com.taosif7.android.sidedrawermenu_sample;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.taosif7.android.sidedrawermenu.SideDrawerMenu;

public class MainActivity extends AppCompatActivity {

    private SideDrawerMenu menu;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = new SideDrawerMenu(this);
        menu.attachToActivity(this);
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
}