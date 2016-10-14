package com.qrfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.qrfood.R;
import com.qrfood.fragment.PastOrdersFragment;
import com.qrfood.fragment.ScanFragment;
import com.qrfood.fragment.dummy.DummyContent;
import com.qrfood.utility.CommonMethods;
import com.qrfood.utility.Constants;
import com.squareup.picasso.Picasso;

import static android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import static com.qrfood.fragment.RestaurantFragment.OnListFragmentInteractionListener;

public class HomeActivity extends AppCompatActivity
        implements OnNavigationItemSelectedListener, OnListFragmentInteractionListener {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private CommonMethods commonMethods;
    private Bundle savedInstanceState = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("Barcode");
                    Log.i("HA", "scan success");
                    Log.i("HA", barcode.displayValue);
                } else {
                    Log.i("HA", "scan fail");
                    Log.d("HA", "No barcode captured, intent data is null");
                }
            } else {
                Log.i("ha", String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_home);
        commonMethods = new CommonMethods(this);

        renderView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.signOut) {
            signOut();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void renderView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        TextView userFullName = (TextView) headerLayout.findViewById(R.id.userFullName);
        userFullName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        ImageView userImage = (ImageView) headerLayout.findViewById(R.id.userImage);
        Picasso.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(userImage);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_content_frame, new ScanFragment()).commit();
        }
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(HomeActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        commonMethods.removeSharedPreference(Constants.userTokenId);
                        startActivity(new Intent(HomeActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (id) {
            case R.id.nav_past_orders:
                fragmentTransaction.replace(R.id.main_content_frame, new PastOrdersFragment());
                fragmentTransaction.addToBackStack(null).commit();

                break;

            case R.id.nav_scan:
                fragmentTransaction.replace(R.id.main_content_frame, new ScanFragment());
                fragmentTransaction.addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.d("HA", "Selected Item: " + item.id);
    }
}
