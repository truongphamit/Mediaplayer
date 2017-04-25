package com.pqs.mediaplayer.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.activities.PlaylistActivity;
import com.pqs.mediaplayer.dataloaders.PlaylistLoader;
import com.pqs.mediaplayer.dataloaders.PlaylistSongLoader;
import com.pqs.mediaplayer.listener.OnItemClickListener;
import com.pqs.mediaplayer.models.PlayList;
import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.utils.Constants;
import com.pqs.mediaplayer.utils.Utils;
import com.pqs.mediaplayer.views.adapters.PlaylistsAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_playlist)
    RecyclerView rv_playlist;

    private PlaylistsAdapter adapter;
    private List<PlayList> playLists;

    public static PlaylistFragment newInstance() {
        
        Bundle args = new Bundle();
        
        PlaylistFragment fragment = new PlaylistFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public PlaylistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @OnClick(R.id.add_playlist)
    public void add_playlist(View view) {
        new MaterialDialog.Builder(getActivity())
                .title("Create new playlist")
                .input("My playlist", "My playlist", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                    }
                })
                .negativeText("CANCEL")
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        long playlistId = Utils.createPlaylist(getActivity(), dialog.getInputEditText().getText().toString());
                        if (playlistId != -1) {
                            playLists.clear();
                            playLists.addAll(PlaylistLoader.getPlaylists(getActivity()));
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "Unable to create playlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    private void init() {
        setupToolbar();

        rv_playlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        playLists = PlaylistLoader.getPlaylists(getActivity());
        adapter = new PlaylistsAdapter(getActivity(), playLists);
        rv_playlist.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent intent = new Intent(getActivity(), PlaylistActivity.class);
                intent.setAction(Constants.NAVIGATE_PLAYLIST_USERCREATED);
                intent.putExtra(Constants.PLAYLIST_ID, playLists.get(position).getId());
                intent.putExtra(Constants.PLAYLIST_NAME, playLists.get(position).getName());
                startActivity(intent);
            }
        });
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.playlists));
    }
}
