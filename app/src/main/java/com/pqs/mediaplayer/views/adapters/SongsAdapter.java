package com.pqs.mediaplayer.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.utils.TimeUtils;

import java.util.List;

/**
 * Created by truongpq on 18/04/2017.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    private List<Song> songs;

    public SongsAdapter(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_local_music, parent, false);

        // Return a new holder instance
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.text_view_name.setText(song.getDisplayName());
        holder.text_view_artist.setText(song.getArtist());
        holder.text_view_duration.setText(TimeUtils.formatDuration(song.getDuration()));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_view_name;
        TextView text_view_artist;
        TextView text_view_duration;

        public ViewHolder(View itemView) {
            super(itemView);

            text_view_name = (TextView) itemView.findViewById(R.id.text_view_name);
            text_view_artist = (TextView) itemView.findViewById(R.id.text_view_artist);
            text_view_duration = (TextView) itemView.findViewById(R.id.text_view_duration);
        }
    }
}
