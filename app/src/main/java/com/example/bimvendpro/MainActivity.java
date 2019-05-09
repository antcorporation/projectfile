package com.example.bimvendpro;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    public static final int DASHBOARD = 1, MACHINE = 2, LOCATIONS = 3, INVENTORY = 4, INGREDIENTS = 5, PURCHASES = 6, PRODUCTS = 7, SHOW_NAVDRAWER = 100, HIDE_NAVDRAWER = 101;
    private String neededCode;
    private DrawerLayout drawer;
    private Drawable navIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Dashboard()).commit();
        toolbar.setTitle("Dashboard");

        navIcon = toolbar.getNavigationIcon();


    }

    @Override
    public void onBackPressed() {
        if (currFrag == INGREDIENTS || currFrag==PRODUCTS) {
            backToPrevFragment();
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private int currFrag = DASHBOARD;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            if (currFrag == DASHBOARD) {

            } else if (currFrag == INVENTORY) {
                new InventoryItemAddDialogue(this).show();
            } else if (currFrag == MACHINE) {
                Intent myIntent = new Intent(this, MachineAddDialogue.class);
                //Optional parameters
                this.startActivity(myIntent);
            } else if (currFrag == INGREDIENTS) {
                new MachineIngredientsAddDialogue(this, neededCode).show();
                return true;
            } else if (currFrag == PURCHASES) {
                new PurchaseAddDialogue(this).show();
                return true;
            }else if (currFrag == PRODUCTS) {
                new PurchaseProductAddDialogue(this,neededCode).show();
                return true;
            }
        } else if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Dashboard()).commit();
            currFrag = DASHBOARD;

            toolbar.setTitle("Dashboard");
        } else if (id == R.id.nav_inventory) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InventoryFragment()).commit();
            currFrag = INVENTORY;
            toolbar.setTitle("Inventory");

        } else if (id == R.id.nav_machines) {


            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MachineFragment()).commit();
            currFrag = MACHINE;

            toolbar.setTitle("Machine");
        } else if (id == R.id.nav_location) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LocationFragment()).commit();
            toolbar.setTitle("Locations");
            currFrag = LOCATIONS;
        } else if (id == R.id.nav_routes) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Routes()).commit();
            toolbar.setTitle("Routes");

        } else if (id == R.id.nav_driver) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DriversFragment()).commit();
            toolbar.setTitle("Drivers");
        } else if (id == R.id.nav_expenses) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ExpenseFragment()).commit();
            toolbar.setTitle("Expenses");
        } else if (id == R.id.nav_purchases) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PurchaseFragment()).commit();
            toolbar.setTitle("Purchases");
            currFrag = PURCHASES;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void changeFragment(Fragment newfragment, String title, String neededCode, int type) {
        Bundle bundle = new Bundle();
        bundle.putString("code", neededCode);
        newfragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newfragment).commit();
        toolbar.setTitle(title);
        currFrag = type;
        this.neededCode = neededCode;
        hideNavigationDrawer();
    }

    public void hideNavigationDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTag(HIDE_NAVDRAWER);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbar.getTag().equals(SHOW_NAVDRAWER)) {
                    drawer.openDrawer(Gravity.START);
                } else {
                    backToPrevFragment();
                }
            }
        });
    }

    public void backToPrevFragment() {
        if (currFrag == INGREDIENTS) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MachineFragment()).commit();
            currFrag = MACHINE;

            toolbar.setTitle("Machine");
            showNavigationDrawer();
        } else if (currFrag == PRODUCTS) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PurchaseFragment()).commit();
            currFrag = PURCHASES;

            toolbar.setTitle("Purchases");
            showNavigationDrawer();
        }
    }

    public void showNavigationDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toolbar.setNavigationIcon(navIcon);
        toolbar.setTag(SHOW_NAVDRAWER);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
