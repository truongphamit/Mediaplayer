package com.pqs.mediaplayer.fragments;


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

import com.pqs.mediaplayer.MainActivity;
import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.activities.ActivityCallback;
import com.pqs.mediaplayer.dataloaders.SongLoader;
import com.pqs.mediaplayer.player.PlaybackService;
import com.pqs.mediaplayer.views.adapters.SongsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

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
                mostPlayedAdapter = new SongsAdapter(getActivity(), SongLoader.getAllSongs(getActivity()).subList(0, 5));
                recentAdapter = new SongsAdapter(getActivity(), SongLoader.getAllSongs(getActivity()).subList(0, 5));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                rv_most_played.setAdapter(mostPlayedAdapter);
                rv_recent_played.setAdapter(recentAdapter);

//                albumsAdapter.setOnItemClickListener(new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View itemView, int position) {
//                        Album album = albumsAdapter.getAlbum(position);
//                        Utils.slideFragment(AlbumDetailFragment.newInstance(album.getTitle(), album.getId()), getActivity().getSupportFragmentManager());
//                    }
//                });
//
//                songsAdapter.setOnItemClickListener(new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View itemView, int position) {
//                        PlayList playList = new PlayList();
//                        playList.setSongs(songsAdapter.getAllSong());
//                        playList.setNumOfSongs(songsAdapter.getAllSong().size());
//                        playbackService.play(playList, position);
//                        Utils.slideFragment(NowPlayingFragment.newInstance(), getActivity().getSupportFragmentManager());
//                    }
//                });
            }
        }.execute();
    }
}
