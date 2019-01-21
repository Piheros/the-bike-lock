/**
 * @file MainActivity.java
 * @version 0.2
 * @author Pierre Pavlovic
 * @date 21/01/2018
 *
 * @section License
 *
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * Copyright (C) 2018  Mehdi Bouafia & Pierre Pavlovic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package pfe.thebikelock.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import pfe.thebikelock.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentAbout about;
    private FragmentLocation location;
    private FragmentUnlock unlock;
    private FragmentMainBis mainBis;
    private FragmentInformation information;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Layout drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Define navigation view
        this.navigationView = findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);
        this.navigationView.getMenu().getItem(0).setChecked(true);

        // Fragments
        this.about = new FragmentAbout();
        this.location = new FragmentLocation();
        this.unlock = new FragmentUnlock();
        this.mainBis = new FragmentMainBis();
        this.information = new FragmentInformation();

        // Function return
        goViewHome();

        //Suppress Title App
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }

    // Return to an over fragment
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // if the fragment showed is the map or the about
            Fragment fragmentLocation = getSupportFragmentManager().findFragmentByTag("FRAGMENT_LOCATION");
            Fragment fragmentAbout = getSupportFragmentManager().findFragmentByTag("FRAGMENT_ABOUT");
            Fragment fragmentUnlock = getSupportFragmentManager().findFragmentByTag("FRAGMENT_UNLOCK");
            Fragment fragmentMainBis = getSupportFragmentManager().findFragmentByTag("FRAGMENT_MAIN_BIS");
            Fragment fragmentInformation = getSupportFragmentManager().findFragmentByTag("FRAGMENT_INFORMATION");

            if ((fragmentMainBis != null && fragmentMainBis.isVisible()) ||(fragmentLocation != null && fragmentLocation.isVisible()) ||
                    (fragmentAbout != null && fragmentAbout.isVisible()) || (fragmentUnlock != null && fragmentUnlock.isVisible()) ||
                    (fragmentInformation != null && fragmentInformation.isVisible())) {
                goViewHome();
            } else {
                super.onBackPressed();
            }
        }
    }

    // Display option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Click to fragment about
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //About
        if (id == R.id.action_app_bar) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.from_top, R.animator.to_bottom)
                    .replace(R.id.actual_fragment, getFragmentAbout(), "FRAGMENT_ABOUT")
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Navigation to fragment view
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                goViewHome();
                break;
            case R.id.nav_location:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.actual_fragment, this.location, "FRAGMENT_LOCATION")
                        .commit();
                break;
            case R.id.nav_information:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.actual_fragment, this.information, "FRAGMENT_INFORMATION")
                        .commit();
                break;
            case R.id.nav_unlock:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.actual_fragment, this.unlock, "FRAGMENT_UNLOCK")
                        .commit();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public FragmentAbout getFragmentAbout() {
        return about;
    }

    public FragmentUnlock getFragmentUnlock() {
        return unlock;
    }

    public FragmentInformation getFragmentInformation() {
        return information;
    }

    public void setSelectedItemMenu(int id) {
        navigationView.getMenu().getItem(id).setChecked(true);
    }

    // If not already home
    private void goViewHome() {
        getSupportFragmentManager().beginTransaction()
                    .replace(R.id.actual_fragment, this.mainBis, "FRAGMENT_MAIN_BIS")
                    .commit();
        this.setSelectedItemMenu(0);
    }


}
