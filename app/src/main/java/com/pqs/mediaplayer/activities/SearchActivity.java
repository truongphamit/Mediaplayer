package com.pqs.mediaplayer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.dataloaders.AlbumLoader;
import com.pqs.mediaplayer.dataloaders.ArtistLoader;
import com.pqs.mediaplayer.dataloaders.SongLoader;
import com.pqs.mediaplayer.models.Album;
import com.pqs.mediaplayer.models.Artist;
import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.player.PlaybackService;
import com.pqs.mediaplayer.utils.Utils;
import com.pqs.mediaplayer.views.adapters.SearchAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnTouchListener, ActivityCallback {

    private final Executor mSearchExecutor = Executors.newSingleThreadExecutor();
    @Nullable
    private AsyncTask mSearchTask = null;

    private SearchView mSearchView;
    private InputMethodManager mImm;
    private String queryString;

    private SearchAdapter adapter;
    private RecyclerView recyclerView;

    private List<Object> searchResults = Collections.emptyList();

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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(this);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onSongItemClick(View itemView, Song song) {
                if (mPlaybackService != null) mPlaybackService.play(song);
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

        if (mIsServiceBound) {
            unbindService(mConnection);
            mIsServiceBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.search_library));

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.menu_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });

        menu.findItem(R.id.menu_search).expandActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        onQueryTextChange(query);
        hideInputManager();

        return true;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {

        if (newText.equals(queryString)) {
            return true;
        }
        if (mSearchTask != null) {
            mSearchTask.cancel(false);
            mSearchTask = null;
        }
        queryString = newText;
        if (queryString.trim().equals("")) {
            searchResults.clear();
            adapter.updateSearchResults(searchResults);
            adapter.notifyDataSetChanged();
        } else {
            mSearchTask = new SearchTask().executeOnExecutor(mSearchExecutor, queryString);
        }

        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideInputManager();
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mSearchTask != null && mSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
            mSearchTask.cancel(false);
        }
        super.onDestroy();
    }

    public void hideInputManager() {
        if (mSearchView != null) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
            }
            mSearchView.clearFocus();
        }
    }

    @Override
    public PlaybackService getmPlaybackService() {
        return mPlaybackService;
    }

    private class SearchTask extends AsyncTask<String, Void, ArrayList<Object>> {

        @Override
        protected ArrayList<Object> doInBackground(String... params) {
            ArrayList<Object> results = new ArrayList<>(27);
            List<Song> songList = SongLoader.searchSongs(SearchActivity.this, params[0], 10);
            if (!songList.isEmpty()) {
                results.add("Songs");
                results.addAll(songList);
            }

            if (isCancelled()) {
                return null;
            }
            List<Album> albumList = AlbumLoader.getAlbums(SearchActivity.this, params[0], 7);
            if (!albumList.isEmpty()) {
                results.add("Albums");
                results.addAll(albumList);
            }

            if (isCancelled()) {
                return null;
            }
            List<Artist> artistList = ArtistLoader.getArtists(SearchActivity.this, params[0], 7);
            if (!artistList.isEmpty()) {
                results.add("Artists");
                results.addAll(artistList);
            }
            if (results.size() == 0) {
                results.add("Nothing found");
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> objects) {
            super.onPostExecute(objects);
            mSearchTask = null;
            if (objects != null) {
                adapter.updateSearchResults(objects);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
