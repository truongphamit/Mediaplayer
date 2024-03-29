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

import com.bumptech.glide.Glide;
import com.pqs.mediaplayer.MainActivity;
import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.activities.ActivityCallback;
import com.pqs.mediaplayer.dataloaders.AlbumOfArtistLoader;
import com.pqs.mediaplayer.dataloaders.SongOfAlbumLoader;
import com.pqs.mediaplayer.dataloaders.SongOfArtistLoader;
import com.pqs.mediaplayer.listener.OnItemClickListener;
import com.pqs.mediaplayer.models.Album;
import com.pqs.mediaplayer.models.PlayList;
import com.pqs.mediaplayer.player.PlaybackService;
import com.pqs.mediaplayer.utils.Utils;
import com.pqs.mediaplayer.views.adapters.AlbumsAdapter;
import com.pqs.mediaplayer.views.adapters.SongsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistDetailFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;

    @BindView(R.id.rv_songs)
    RecyclerView rv_songs;

    @BindView(R.id.rv_albums)
    RecyclerView rv_albums;

    private SongsAdapter songsAdapter;
    private AlbumsAdapter albumsAdapter;
    private PlaybackService playbackService;


    public static ArtistDetailFragment newInstance(String artist, long artistId) {

        Bundle args = new Bundle();
        args.putString(MediaStore.Audio.Media.ARTIST, artist);
        args.putLong(MediaStore.Audio.Media.ARTIST_ID, artistId);
        ArtistDetailFragment fragment = new ArtistDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ArtistDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist_detail, container, false);
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
        collapsing_toolbar.setTitle(getArguments().getString(MediaStore.Audio.Media.ARTIST));
    }

    private void init() {
        playbackService = ((ActivityCallback) getActivity()).getmPlaybackService();

        rv_songs.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_albums.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        loadSongs();
    }

    private void loadSongs() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                albumsAdapter = new AlbumsAdapter(getActivity(), AlbumOfArtistLoader.getAllAlbums(getActivity(), getArguments().getLong(MediaStore.Audio.Media.ARTIST_ID)));
                songsAdapter = new SongsAdapter(getActivity(), SongOfArtistLoader.getAllSongs(getActivity(), getArguments().getLong(MediaStore.Audio.Media.ARTIST_ID)));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                albumsAdapter.setList(true);
                rv_albums.setAdapter(albumsAdapter);
                rv_songs.setAdapter(songsAdapter);

                albumsAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
                        Album album = albumsAdapter.getAlbum(position);
                        Utils.slideFragment(AlbumDetailFragment.newInstance(album.getTitle(), album.getId()), getActivity().getSupportFragmentManager());
                    }
                });

                songsAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
                        PlayList playList = new PlayList();
                        playList.setSongs(songsAdapter.getAllSong());
                        playList.setNumOfSongs(songsAdapter.getAllSong().size());
                        playbackService.play(playList, position);
                        Utils.slideFragment(NowPlayingFragment.newInstance(), getActivity().getSupportFragmentManager());
                    }
                });
            }
        }.execute();
    }
}
