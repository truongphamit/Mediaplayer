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
import com.pqs.mediaplayer.dataloaders.ArtistLoader;
import com.pqs.mediaplayer.views.adapters.ArtistAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistsPageFragment extends Fragment {

    @BindView(R.id.rv_artists)
    RecyclerView rv_artists;

    private ArtistAdapter adapter;

    public static ArtistsPageFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ArtistsPageFragment fragment = new ArtistsPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ArtistsPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists_page, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        rv_artists.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        loadArtists();
    }

    private void loadArtists() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                adapter = new ArtistAdapter(getActivity(), ArtistLoader.getAllArtists(getActivity()));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                rv_artists.setAdapter(adapter);
            }
        }.execute();
    }
}
