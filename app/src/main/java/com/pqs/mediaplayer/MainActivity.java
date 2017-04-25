package com.pqs.mediaplayer;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pqs.mediaplayer.activities.ActivityCallback;
import com.pqs.mediaplayer.activities.SearchActivity;
import com.pqs.mediaplayer.activities.SettingsActivity;
import com.pqs.mediaplayer.dataloaders.SongLoader;
import com.pqs.mediaplayer.dataloaders.TopTracksLoader;
import com.pqs.mediaplayer.fragments.AlbumDetailFragment;
import com.pqs.mediaplayer.fragments.AlbumPageFragment;
import com.pqs.mediaplayer.fragments.ArtistDetailFragment;
import com.pqs.mediaplayer.fragments.ArtistsPageFragment;
import com.pqs.mediaplayer.fragments.NowPlayingFragment;
import com.pqs.mediaplayer.fragments.PlaylistFragment;
import com.pqs.mediaplayer.fragments.SongsPageFragment;
import com.pqs.mediaplayer.fragments.SuggestedPageFragment;
import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.player.IPlayback;
import com.pqs.mediaplayer.player.PlaybackService;
import com.pqs.mediaplayer.utils.Constants;
import com.pqs.mediaplayer.utils.Utils;
import com.pqs.mediaplayer.views.adapters.PagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ActivityCallback, IPlayback.Callback {

    @BindView(R.id.bottom_bar_player)
    View bottom_bar_player;

    @BindView(R.id.tv_playbar_title)
    TextView tv_playbar_title;

    @BindView(R.id.tv_playbar_author)
    TextView tv_playbar_author;

    @BindView(R.id.btn_playbar_prev)
    ImageView btn_playbar_prev;

    @BindView(R.id.btn_playbar_play)
    ImageView btn_playbar_play;

    @BindView(R.id.btn_playbar_next)
    ImageView btn_playbar_next;

    @BindView(R.id.albumArt)
    ImageView albumArt;

    public static final int PERMISSIONS_REQUEST = 2;

    Map<String, Runnable> actionMaps = new HashMap<>();

    Runnable navigateAlbum = new Runnable() {
        public void run() {
            long albumID = getIntent().getExtras().getLong(Constants.ALBUM_ID);
            String album = getIntent().getExtras().getString(Constants.ALBUM);
            Utils.slideFragmentWithoutAddBackStack(AlbumDetailFragment.newInstance(album, albumID), getSupportFragmentManager());
        }
    };
    Runnable navigateArtist = new Runnable() {
        public void run() {
            long artistID = getIntent().getExtras().getLong(Constants.ARTIST_ID);
            String artist = getIntent().getExtras().getString(Constants.ARTIST);
            Utils.slideFragmentWithoutAddBackStack(ArtistDetailFragment.newInstance(artist, artistID), getSupportFragmentManager());
        }
    };
    Runnable navigateNowPlaying = new Runnable() {
        public void run() {
            Utils.slideFragmentWithoutAddBackStack(NowPlayingFragment.newInstance(), getSupportFragmentManager());
        }
    };

    private PlaybackService mPlaybackService;
    private boolean mIsServiceBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mPlaybackService = ((PlaybackService.LocalBinder) service).getService();
            mIsServiceBound = true;
            mPlaybackService.registerCallback(MainActivity.this);

            if (isPermissionGranted()) {
                init();
            }

            updatePlaybar();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mPlaybackService = null;
            mIsServiceBound = false;
        }
    };

    private Snackbar snackbar;

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

        requestPermission();

        snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.access_grant, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Settings intent
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                        finish();
                    }
                });

        actionMaps.put(Constants.NAVIGATE_ALBUM, navigateAlbum);
        actionMaps.put(Constants.NAVIGATE_ARTIST, navigateArtist);
        actionMaps.put(Constants.NAVIGATE_NOWPLAYING, navigateNowPlaying);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    updatePlaybar();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, PlaybackService.class);
        if (!Utils.isServiceRunning(this, PlaybackService.class)) {
            startService(intent);
        }
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mPlaybackService.unregisterCallback(this);

        if (mIsServiceBound) {
            unbindService(mConnection);
            mIsServiceBound = false;
        }
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
        if (!Utils.hasEffectsPanel(this)) {
            menu.removeItem(R.id.action_equalizer);
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

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_equalizer:
                navigateToEqualizer();
                break;
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.action_sleep_timer:
                Utils.showSleepTimerDialog(this);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    if (snackbar.isShown())
                        snackbar.dismiss();
                    init();
                } else {
                    // Show Dialog setting request permissiion
                    if (!snackbar.isShown())
                        snackbar.show();
                }
                return;
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_suggested) {
            Utils.slideFragment(SuggestedPageFragment.newInstance(), getSupportFragmentManager());
        } else if (id == R.id.nav_playlists) {
            Utils.slideFragment(PlaylistFragment.newInstance(), getSupportFragmentManager());
        } else if (id == R.id.nav_now_playing) {
            Utils.slideFragment(NowPlayingFragment.newInstance(), getSupportFragmentManager());
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.bottom_bar_player)
    public void now_playing(View view) {
        Utils.slideFragment(NowPlayingFragment.newInstance(), getSupportFragmentManager());
    }

    @OnClick(R.id.btn_playbar_play)
    public void onPlayAction(View view) {
        if (mPlaybackService == null) return;

        if (mPlaybackService.isPlaying()) {
            mPlaybackService.pause();
        } else {
            mPlaybackService.play();
        }
    }

    @OnClick(R.id.btn_playbar_next)
    public void onNextAction(View view) {
        if (mPlaybackService == null) return;
        mPlaybackService.playNext();
    }

    @OnClick(R.id.btn_playbar_prev)
    public void onPreAction(View view) {
        if (mPlaybackService == null) return;
        mPlaybackService.playLast();
    }

    @Override
    public void onSwitchLast(@Nullable Song last) {
        onSongUpdate(last);
    }

    @Override
    public void onSwitchNext(@Nullable Song next) {
        onSongUpdate(next);
    }

    @Override
    public void onComplete(@Nullable Song next) {
        onSongUpdate(next);
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        btn_playbar_play.setImageResource(isPlaying ? R.drawable.ic_remote_view_pause : R.drawable.ic_remote_view_play);
    }

    private void init() {
        Runnable navigation = actionMaps.get(getIntent().getAction());
        if (navigation != null) {
            navigation.run();
        }

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

    @Override
    public PlaybackService getmPlaybackService() {
        return mPlaybackService;
    }

    public void navigateToEqualizer() {
        try {
            // The google MusicFX apps need to be started using startActivityForResult
            startActivityForResult(Utils.createEffectsIntent(), 111);
        } catch (final ActivityNotFoundException notFound) {
            Toast.makeText(this, "Equalizer not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestPermission() {
        if (!isPermissionGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
        }
    }

    private boolean isPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show Dialog setting request permissiion
                if (!snackbar.isShown())
                    snackbar.show();

                return false;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void updatePlaybar() {
        onSongUpdate(mPlaybackService.getPlayingSong());
    }

    private void onSongUpdate(Song song) {
        if (song == null) {
            btn_playbar_play.setImageResource(R.drawable.ic_remote_view_play);
            return;
        }

        tv_playbar_title.setText(song.getDisplayName());
        tv_playbar_author.setText(song.getArtist());
        Glide.with(this).load(Utils.getAlbumArtUri(song.getAlbumId())).placeholder(R.drawable.ic_empty).into(albumArt);

        if (mPlaybackService.isPlaying()) {
            btn_playbar_play.setImageResource(R.drawable.ic_remote_view_pause);
        }
    }
}
