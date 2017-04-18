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
public class GenresPageFragment extends Fragment {

    public static GenresPageFragment newInstance() {
        
        Bundle args = new Bundle();
        
        GenresPageFragment fragment = new GenresPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public GenresPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_genres_page, container, false);
    }

}
