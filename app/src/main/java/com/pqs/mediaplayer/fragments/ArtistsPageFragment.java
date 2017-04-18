package com.pqs.mediaplayer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pqs.mediaplayer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistsPageFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_artists_page, container, false);
    }

}
