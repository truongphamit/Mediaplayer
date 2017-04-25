package com.pqs.mediaplayer.activities;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pqs.mediaplayer.MainActivity;
import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.dataloaders.PlaylistSongLoader;
import com.pqs.mediaplayer.dataloaders.SongLoader;
import com.pqs.mediaplayer.dataloaders.TopTracksLoader;
import com.pqs.mediaplayer.listener.OnItemClickListener;
import com.pqs.mediaplayer.models.PlayList;
import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.player.PlaybackService;
import com.pqs.mediaplayer.utils.Constants;
import com.pqs.mediaplayer.utils.Utils;
import com.pqs.mediaplayer.views.adapters.PlaylistSongsAdapter;
import com.pqs.mediaplayer.views.adapters.SongsAdapter;
import com.pqs.mediaplayer.views.adapters.SuggestedAdapter;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistActivity extends AppCompatActivity {

    @BindView(R.id.blurFrame)
    ImageView blurFrame;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindView(R.id.name)
    TextView name;

    private SuggestedAdapter adapter;
    private PlaylistSongsAdapter playlistSongsAdapter;

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

    HashMap<String, Runnable> playlistsMap = new HashMap<>();

    Runnable playlistRecents = new Runnable() {
        @Override
        public void run() {
            new loadRecentlyPlayed().execute("");

        }
    };

    Runnable playlistToptracks = new Runnable() {
        @Override
        public void run() {
            new loadTopTracks().execute("");
        }
    };

    Runnable playlistUsercreated = new Runnable() {
        @Override
        public void run() {
            new loadUserCreatedPlaylist().execute("");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_RECENT, playlistRecents);
        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_TOPTRACKS, playlistToptracks);
        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_USERCREATED, playlistUsercreated);

        init();
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

        if (mIsServiceBound) {
            unbindService(mConnection);
            mIsServiceBound = false;
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

    private void init() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        String action = getIntent().getAction();
        Runnable navigation = playlistsMap.get(action);
        if (navigation != null) {
            navigation.run();
        }
    }

    private void loadBitmap(Uri uri) {
        Glide.with(this).load(uri).placeholder(R.drawable.ic_empty).into(blurFrame);
    }

    public void navigateToEqualizer() {
        try {
            // The google MusicFX apps need to be started using startActivityForResult
            startActivityForResult(Utils.createEffectsIntent(), 111);
        } catch (final ActivityNotFoundException notFound) {
            Toast.makeText(this, "Equalizer not found", Toast.LENGTH_SHORT).show();
        }
    }

    private class loadTopTracks extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            new TopTracksLoader(PlaylistActivity.this, TopTracksLoader.QueryType.TopTracks);
            List<Song> toptracks = SongLoader.getSongForCursor(TopTracksLoader.getCursor());
            adapter = new SuggestedAdapter(PlaylistActivity.this, toptracks);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            name.setText(R.string.top_tracks);
            recyclerview.setAdapter(adapter);
            loadBitmap(Utils.getAlbumArtUri(adapter.getSong(0).getAlbumId()));

            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int position) {
                    if (mPlaybackService != null) {
                        PlayList playList = new PlayList();
                        playList.setSongs(adapter.getAllSong());
                        playList.setNumOfSongs(adapter.getAllSong().size());
                        mPlaybackService.play(playList, position);
                        goToNowplaying(PlaylistActivity.this);
                    }
                }
            });
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class loadRecentlyPlayed extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            new TopTracksLoader(PlaylistActivity.this, TopTracksLoader.QueryType.RecentSongs);
            List<Song> recentSong = SongLoader.getSongForCursor(TopTracksLoader.getCursor());
            adapter = new SuggestedAdapter(PlaylistActivity.this, recentSong);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            name.setText(R.string.recently_played);
            recyclerview.setAdapter(adapter);
            loadBitmap(Utils.getAlbumArtUri(adapter.getSong(0).getAlbumId()));

            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int position) {
                    if (mPlaybackService != null) {
                        PlayList playList = new PlayList();
                        playList.setSongs(adapter.getAllSong());
                        playList.setNumOfSongs(adapter.getAllSong().size());
                        mPlaybackService.play(playList, position);
                        goToNowplaying(PlaylistActivity.this);
                    }
                }
            });
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class loadUserCreatedPlaylist extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            int playlistID = getIntent().getExtras().getInt(Constants.PLAYLIST_ID);
            List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(PlaylistActivity.this, playlistID);
            playlistSongsAdapter = new PlaylistSongsAdapter(PlaylistActivity.this, playlistsongs);
            playlistSongsAdapter.setPlaylistId(playlistID);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            name.setText(getIntent().getExtras().getString(Constants.PLAYLIST_NAME));
            recyclerview.setAdapter(playlistSongsAdapter);

            playlistSongsAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int position) {
                    PlayList playList = new PlayList();
                    playList.setSongs(playlistSongsAdapter.getAllSong());
                    playList.setNumOfSongs(playlistSongsAdapter.getAllSong().size());
                    mPlaybackService.play(playList, position);
                    goToNowplaying(PlaylistActivity.this);
                }
            });
        }

        @Override
        protected void onPreExecute() {
        }
    }

    public void goToNowplaying(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_NOWPLAYING);
        context.startActivity(intent);
    }
}
