package com.pqs.mediaplayer.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.activities.ActivityCallback;
import com.pqs.mediaplayer.activities.PlaylistActivity;
import com.pqs.mediaplayer.dataloaders.SongLoader;
import com.pqs.mediaplayer.dataloaders.TopTracksLoader;
import com.pqs.mediaplayer.listener.OnItemClickListener;
import com.pqs.mediaplayer.models.PlayList;
import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.player.PlaybackService;
import com.pqs.mediaplayer.utils.Constants;
import com.pqs.mediaplayer.utils.Utils;
import com.pqs.mediaplayer.views.adapters.SongsAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuggestedPageFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_most_played)
    RecyclerView rv_most_played;

    @BindView(R.id.rv_recent_played)
    RecyclerView rv_recent_played;

    private SongsAdapter mostPlayedAdapter;
    private SongsAdapter recentAdapter;
    private PlaybackService playbackService;

    public static SuggestedPageFragment newInstance() {

        Bundle args = new Bundle();

        SuggestedPageFragment fragment = new SuggestedPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SuggestedPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suggested_page, container, false);
        ButterKnife.bind(this, view);
        setupToolbar();
        init();
        return view;
    }

    @OnClick(R.id.more_most_played)
    public void more_most_played(View view) {
        if (mostPlayedAdapter.getAllSong().size() > 0) {
            Intent intent = new Intent(getActivity(), PlaylistActivity.class);
            intent.setAction(Constants.NAVIGATE_PLAYLIST_TOPTRACKS);
            startActivity(intent);
        }
    }

    @OnClick(R.id.more_recent_played)
    public void more_recent_played(View view) {
        if (recentAdapter.getAllSong().size() > 0) {
            Intent intent = new Intent(getActivity(), PlaylistActivity.class);
            intent.setAction(Constants.NAVIGATE_PLAYLIST_RECENT);
            startActivity(intent);
        }
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.suggested));
    }

    private void init() {
        playbackService = ((ActivityCallback) getActivity()).getmPlaybackService();

        rv_most_played.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_recent_played.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadSongs();
    }

    private void loadSongs() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                new TopTracksLoader(getActivity(), TopTracksLoader.QueryType.TopTracks);
                List<Song> topTrack = SongLoader.getSongForCursor(TopTracksLoader.getCursor());
                if (topTrack.size() > 5) {
                    mostPlayedAdapter = new SongsAdapter(getActivity(), topTrack.subList(0, 5));
                } else {
                    mostPlayedAdapter = new SongsAdapter(getActivity(), topTrack);
                }

                new TopTracksLoader(getActivity(), TopTracksLoader.QueryType.RecentSongs);
                List<Song> recentsongs = SongLoader.getSongForCursor(TopTracksLoader.getCursor());
                if (recentsongs.size() > 5) {
                    recentAdapter = new SongsAdapter(getActivity(), recentsongs.subList(0, 5));
                } else {
                    recentAdapter = new SongsAdapter(getActivity(), recentsongs);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                rv_most_played.setAdapter(mostPlayedAdapter);
                rv_recent_played.setAdapter(recentAdapter);

                mostPlayedAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
                        if (mostPlayedAdapter.getAllSong().size() > 20) {
                            PlayList playList = new PlayList();
                            playList.setSongs(mostPlayedAdapter.getAllSong().subList(0, 19));
                            playList.setNumOfSongs(20);
                            playbackService.play(playList, position);
                            Utils.slideFragment(NowPlayingFragment.newInstance(), getActivity().getSupportFragmentManager());
                        } else {
                            PlayList playList = new PlayList();
                            playList.setSongs(mostPlayedAdapter.getAllSong());
                            playList.setNumOfSongs(mostPlayedAdapter.getAllSong().size());
                            playbackService.play(playList, position);
                            Utils.slideFragment(NowPlayingFragment.newInstance(), getActivity().getSupportFragmentManager());
                        }
                    }
                });

                recentAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
                        if (recentAdapter.getAllSong().size() > 20) {
                            PlayList playList = new PlayList();
                            playList.setSongs(recentAdapter.getAllSong().subList(0, 19));
                            playList.setNumOfSongs(20);
                            playbackService.play(playList, position);
                            Utils.slideFragment(NowPlayingFragment.newInstance(), getActivity().getSupportFragmentManager());
                        } else {
                            PlayList playList = new PlayList();
                            playList.setSongs(recentAdapter.getAllSong());
                            playList.setNumOfSongs(recentAdapter.getAllSong().size());
                            playbackService.play(playList, position);
                            Utils.slideFragment(NowPlayingFragment.newInstance(), getActivity().getSupportFragmentManager());
                        }
                    }
                });
            }
        }.execute();
    }
}
