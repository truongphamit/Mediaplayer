package com.pqs.mediaplayer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pqs.mediaplayer.MainActivity;
import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.dataloaders.SongOfAlbumLoader;
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
public class AlbumDetailFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;

    @BindView(R.id.rv_songs)
    RecyclerView rv_songs;

    @BindView(R.id.album_art)
    ImageView album_art;

    private PlaybackService playbackService;
    private SongsAdapter adapter;

    public static AlbumDetailFragment newInstance(String album, long albumId) {

        Bundle args = new Bundle();
        args.putString(MediaStore.Audio.Media.ALBUM, album);
        args.putLong(MediaStore.Audio.Media.ALBUM_ID, albumId);
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AlbumDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_detail, container, false);
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
        collapsing_toolbar.setTitle(getArguments().getString(MediaStore.Audio.Media.ALBUM));
    }

    private void init() {
        playbackService = ((MainActivity) getActivity()).getmPlaybackService();

        rv_songs.setLayoutManager(new LinearLayoutManager(getActivity()));
        Glide.with(getActivity()).load(Utils.getAlbumArtUri(getArguments().getLong(MediaStore.Audio.Media.ALBUM_ID))).placeholder(R.drawable.ic_empty).into(album_art);
        loadSongs();
    }

    private void loadSongs() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                adapter = new SongsAdapter(getActivity(), SongOfAlbumLoader.getAllSongs(getActivity(), getArguments().getLong(MediaStore.Audio.Media.ALBUM_ID)));
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
