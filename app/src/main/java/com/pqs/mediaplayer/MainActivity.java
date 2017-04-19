package com.pqs.mediaplayer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.pqs.mediaplayer.activities.SettingsActivity;
import com.pqs.mediaplayer.fragments.AlbumPageFragment;
import com.pqs.mediaplayer.fragments.ArtistsPageFragment;
import com.pqs.mediaplayer.fragments.GenresPageFragment;
import com.pqs.mediaplayer.fragments.NowPlayingFragment;
import com.pqs.mediaplayer.fragments.SettingsFragment;
import com.pqs.mediaplayer.fragments.SongsPageFragment;
import com.pqs.mediaplayer.fragments.SuggestedPageFragment;
import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.utils.FileUtils;
import com.pqs.mediaplayer.utils.Utils;
import com.pqs.mediaplayer.views.adapters.PagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MAINACTIVITY";

    @BindView(R.id.tabs)
    TabLayout tabs;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        init();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_playlists) {

        } else if (id == R.id.nav_now_playing) {
            Utils.slideFragment(NowPlayingFragment.newInstance(), getSupportFragmentManager());
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(SongsPageFragment.newInstance());
        fragments.add(AlbumPageFragment.newInstance());
        fragments.add(ArtistsPageFragment.newInstance());

        List<String> titles = new ArrayList<>();
        titles.add("SONGS");
        titles.add("ALBUMS");
        titles.add("ARTISTS");

        viewpager.setAdapter(new PagerAdapter(getSupportFragmentManager(), this, fragments, titles));
        viewpager.setOffscreenPageLimit(2);
        tabs.setupWithViewPager(viewpager);
    }
}
