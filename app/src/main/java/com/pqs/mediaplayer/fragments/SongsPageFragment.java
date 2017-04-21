package com.pqs.mediaplayer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pqs.mediaplayer.MainActivity;
import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.dataloaders.SongLoader;
import com.pqs.mediaplayer.listener.OnItemClickListener;
import com.pqs.mediaplayer.models.PlayList;
import com.pqs.mediaplayer.player.PlaybackService;
import com.pqs.mediaplayer.utils.Utils;
import com.pqs.mediaplayer.views.adapters.SongsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongsPageFragment extends Fragment {

    @BindView(R.id.rv_songs)
    RecyclerView rv_songs;

    private PlaybackService playbackService;
    private SongsAdapter adapter;

    public static SongsPageFragment newInstance() {

        Bundle args = new Bundle();

        SongsPageFragment fragment = new SongsPageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public SongsPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs_page, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        playbackService = ((MainActivity) getActivity()).getmPlaybackService();

        rv_songs.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadSongs();
    }

    private void loadSongs() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                adapter = new SongsAdapter(getActivity(), SongLoader.getAllSongs(getActivity()));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                rv_songs.setAdapter(adapter);

                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
                        PlayList playList = new PlayList();
                        playList.setSongs(adapter.getAllSong());
                        playList.setNumOfSongs(adapter.getAllSong().size());
                        playbackService.play(playList, position);
                        Utils.slideFragment(NowPlayingFragment.newInstance(), getActivity().getSupportFragmentManager());
                    }
                });
            }
        }.execute();
    }
}
