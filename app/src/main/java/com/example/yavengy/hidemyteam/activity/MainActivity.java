package com.example.yavengy.hidemyteam.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.yavengy.hidemyteam.R;

import static com.example.yavengy.hidemyteam.Util.TagNFilters.filterArray;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private boolean isHomePage = true;
    public static Context mainContext;
    public static int screenWidth;

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(isHomePage) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_filter, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            isHomePage = false;
            updateMenu();
            displayView(1);
            //return true;
        }

        if (id == R.id.action_back) {
            isHomePage = true;
            updateMenu();
            saveFilterArray();
            displayView(0);
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = getApplicationContext();

        loadFilterArray();

        screenWidth = getScreenWidth();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        updateMenu();

        displayView(0);

    }

    private void loadFilterArray(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mainContext);

        int value;

        for (int i = 0; i < filterArray.length; i++) {

            value = preferences.getInt(Integer.toString(i), 2);
            if(value == 2){
                filterArray[i] = 0;
            } else {
                filterArray[i] = value;
            }
        }
    }

    private void saveFilterArray(){

        for (int i = 0; i < filterArray.length; i++) {
            saveFilters(Integer.toString(i), filterArray[i], mainContext);
        }
    }

    private void updateMenu(){

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                isHomePage = true;
                saveFilterArray();
                break;
            case 1:
                fragment = new FilterFragment();
                title = getString(R.string.title_filters);
                isHomePage = false;
                break;
            case 2:
                fragment = new SkinFragment();
                title = getString(R.string.title_change_skin);
                isHomePage = false;
                break;
            case 3:
                fragment = new DeletedArticlesFragment();
                title = getString(R.string.title_deleted_articles);
                isHomePage = false;
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    public static Context getMainContext(){
        return mainContext;
    }

    public static void saveFilters(String key, int value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getScreenWidth(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

}
