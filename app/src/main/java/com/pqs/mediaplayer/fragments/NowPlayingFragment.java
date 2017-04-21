package com.pqs.mediaplayer.fragments;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pqs.mediaplayer.MainActivity;
import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.activities.ActivityCallback;
import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.player.IPlayback;
import com.pqs.mediaplayer.player.PlayMode;
import com.pqs.mediaplayer.player.PlaybackService;
import com.pqs.mediaplayer.source.PreferenceManager;
import com.pqs.mediaplayer.utils.TimeUtils;
import com.pqs.mediaplayer.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment implements IPlayback.Callback {

    // Update seek bar every second
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;

    @BindView(R.id.song_title)
    TextView song_title;

    @BindView(R.id.song_artist)
    TextView song_artist;

    @BindView(R.id.tv_progress)
    TextView tv_progress;

    @BindView(R.id.tv_duration)
    TextView tv_duration;

    @BindView(R.id.albumArt)
    ImageView albumArt;

    @BindView(R.id.player_mode)
    ImageView player_mode;

    @BindView(R.id.img_play_prev)
    ImageView img_play_prev;

    @BindView(R.id.img_play_toggle)
    ImageView img_play_toggle;

    @BindView(R.id.img_play_next)
    ImageView img_play_next;

    @BindView(R.id.seek_bar)
    SeekBar seek_bar;

    private PlaybackService playbackService;

    private Handler mHandler = new Handler();

    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) return;

            if (playbackService.isPlaying()) {
                int progress = (int) (seek_bar.getMax()
                        * ((float) playbackService.getProgress() / (float) getCurrentSongDuration()));
                updateProgressTextWithDuration(playbackService.getProgress());
                if (progress >= 0 && progress <= seek_bar.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seek_bar.setProgress(progress, true);
                    } else {
                        seek_bar.setProgress(progress);
                    }
                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL);
                }
            }
        }
    };

    public static NowPlayingFragment newInstance() {

        Bundle args = new Bundle();

        NowPlayingFragment fragment = new NowPlayingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public NowPlayingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        playbackService = ((ActivityCallback) getActivity()).getmPlaybackService();
    }

    @Override
    public void onResume() {
        super.onResume();
        onSongUpdate(playbackService.getPlayingSong());
        playbackService.registerCallback(this);

        if (playbackService != null && playbackService.isPlaying()) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateProgressTextWithProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mProgressCallback);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(getDuration(seekBar.getProgress()));
                if (playbackService.isPlaying()) {
                    mHandler.removeCallbacks(mProgressCallback);
                    mHandler.post(mProgressCallback);
                }
            }
        });

        PlayMode current = PreferenceManager.lastPlayMode(getActivity());
        playbackService.setPlayMode(current);
        updatePlayMode(current);
    }

    @Override
    public void onStop() {
        super.onStop();
        playbackService.unregisterCallback(this);
        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void onSwitchLast(@Nullable Song last) {
        onSongUpdate(last);
    }

    @Override
    public void onSwitchNext(@Nullable Song next) {
        onSongUpdate(next);
    }

    @Override
    public void onComplete(@Nullable Song next) {
        onSongUpdate(next);
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        updatePlayToggle(isPlaying);

        if (isPlaying) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        } else {
            mHandler.removeCallbacks(mProgressCallback);
        }
    }

    @OnClick(R.id.img_play_toggle)
    public void onPlayToggleAction(View view) {
        if (playbackService == null) return;

        if (playbackService.isPlaying()) {
            playbackService.pause();
        } else {
            playbackService.play();
        }
    }

    @OnClick(R.id.img_play_prev)
    public void onPlayLastAction(View view) {
        if (playbackService == null) return;
        playbackService.playLast();
    }

    @OnClick(R.id.img_play_next)
    public void onPlayNextAction(View view) {
        if (playbackService == null) return;
        playbackService.playNext();
    }

    @OnClick(R.id.player_mode)
    public void onPlayModeToggleAction(View view) {
        if (playbackService == null) return;

        PlayMode current = PreferenceManager.lastPlayMode(getActivity());
        PlayMode newMode = PlayMode.switchNextMode(current);
        PreferenceManager.setPlayMode(getActivity(), newMode);
        playbackService.setPlayMode(newMode);
        updatePlayMode(newMode);
    }

    public void updatePlayMode(PlayMode playMode) {
        if (playMode == null) {
            playMode = PlayMode.getDefault();
        }
        switch (playMode) {
            case LIST:
                player_mode.setImageResource(R.drawable.ic_play_mode_list);
                break;
            case LOOP:
                player_mode.setImageResource(R.drawable.ic_play_mode_loop);
                break;
            case SHUFFLE:
                player_mode.setImageResource(R.drawable.ic_play_mode_shuffle);
                break;
            case SINGLE:
                player_mode.setImageResource(R.drawable.ic_play_mode_single);
                break;
        }
    }

    private void onSongUpdate(Song song) {
        if (song == null) {
            img_play_toggle.setImageResource(R.drawable.ic_player_play);
            seek_bar.setProgress(0);
            updateProgressTextWithProgress(0);
            seekTo(0);
            mHandler.removeCallbacks(mProgressCallback);
            return;
        }

        Glide.with(getActivity()).load(Utils.getAlbumArtUri(song.getAlbumId())).placeholder(R.drawable.ic_empty).into(albumArt);
        song_title.setText(song.getDisplayName());
        song_artist.setText(song.getArtist());
        tv_duration.setText(TimeUtils.formatDuration(song.getDuration()));
        mHandler.removeCallbacks(mProgressCallback);
        if (playbackService.isPlaying()) {
            img_play_toggle.setImageResource(R.drawable.ic_player_pause);
            mHandler.post(mProgressCallback);
        }
    }

    public void updatePlayToggle(boolean play) {
        img_play_toggle.setImageResource(play ? R.drawable.ic_player_pause : R.drawable.ic_player_play);
    }

    private int getCurrentSongDuration() {
        Song currentSong = playbackService.getPlayingSong();
        int duration = 0;
        if (currentSong != null) {
            duration = currentSong.getDuration();
        }
        return duration;
    }

    private void updateProgressTextWithDuration(int duration) {
        tv_progress.setText(TimeUtils.formatDuration(duration));
    }

    private void updateProgressTextWithProgress(int progress) {
        int targetDuration = getDuration(progress);
        tv_progress.setText(TimeUtils.formatDuration(targetDuration));
    }

    private void seekTo(int duration) {
        playbackService.seekTo(duration);
    }

    private int getDuration(int progress) {
        return (int) (getCurrentSongDuration() * ((float) progress / seek_bar.getMax()));
    }
}
