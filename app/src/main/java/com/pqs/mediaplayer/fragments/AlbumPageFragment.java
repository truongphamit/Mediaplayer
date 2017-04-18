package com.pqs.mediaplayer.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.dataloaders.AlbumLoader;
import com.pqs.mediaplayer.views.adapters.AlbumsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumPageFragment extends Fragment {

    @BindView(R.id.rv_albums)
    RecyclerView rv_albums;

    private AlbumsAdapter adapter;

    public static AlbumPageFragment newInstance() {
        
        Bundle args = new Bundle();
        
        AlbumPageFragment fragment = new AlbumPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AlbumPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_page, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        rv_albums.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        loadAlbums();
    }

    private void loadAlbums() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                adapter = new AlbumsAdapter(getActivity(), AlbumLoader.getAllAlbums(getActivity()));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                rv_albums.setAdapter(adapter);
            }
        }.execute();
    }
}
