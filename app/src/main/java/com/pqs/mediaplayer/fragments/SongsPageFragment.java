package com.pqs.mediaplayer.fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.utils.FileUtils;
import com.pqs.mediaplayer.views.adapters.SongsAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongsPageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOAD_LOCAL_MUSIC = 0;
    private static final Uri MEDIA_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final String WHERE = MediaStore.Audio.Media.IS_MUSIC + "=1 AND "
            + MediaStore.Audio.Media.SIZE + ">0";
    private static final String ORDER_BY = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";
    private static String[] PROJECTIONS = {
            MediaStore.Audio.Media.DATA, // the real path
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
    };

    @BindView(R.id.rv_songs)
    RecyclerView rv_songs;

    private List<Song> songs;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        getActivity().getSupportLoaderManager().initLoader(URL_LOAD_LOCAL_MUSIC, null, this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void init() {
        songs = new ArrayList<>();
        rv_songs.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SongsAdapter(songs);
        rv_songs.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id != URL_LOAD_LOCAL_MUSIC) return null;

        return new CursorLoader(
                getActivity(),
                MEDIA_URI,
                PROJECTIONS,
                WHERE,
                null,
                ORDER_BY
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Song> songs = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Song song = cursorToMusic(data);
                songs.add(song);
            } while (data.moveToNext());
        }

        onLocalMusicLoaded(songs);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void onLocalMusicLoaded(List<Song> songs) {
        this.songs.addAll(songs);
        adapter.notifyDataSetChanged();
    }

    private Song cursorToMusic(Cursor cursor) {
        String realPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        File songFile = new File(realPath);
        Song song;
        if (songFile.exists()) {
            // Using song parsed from file to avoid encoding problems
            song = FileUtils.fileToMusic(songFile);
            if (song != null) {
                return song;
            }
        }
        song = new Song();
        song.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
        String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
        if (displayName.endsWith(".mp3")) {
            displayName = displayName.substring(0, displayName.length() - 4);
        }
        song.setDisplayName(displayName);
        song.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
        song.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
        song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
        song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
        song.setSize(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
        return song;
    }
}
