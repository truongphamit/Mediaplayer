package com.pqs.mediaplayer.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pqs.mediaplayer.MainActivity;
import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.fragments.AlbumDetailFragment;
import com.pqs.mediaplayer.fragments.ArtistDetailFragment;
import com.pqs.mediaplayer.listener.OnItemClickListener;
import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.utils.TimeUtils;
import com.pqs.mediaplayer.utils.Utils;

import java.util.List;

/**
 * Created by truongpq on 18/04/2017.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    private Context context;
    private List<Song> songs;

    public SongsAdapter(Context context, List<Song> songs) {
        this.context = context;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Song song = songs.get(position);
        holder.text_view_name.setText(song.getDisplayName());
        holder.text_view_artist.setText(song.getArtist());
        holder.text_view_duration.setText(TimeUtils.formatDuration(song.getDuration()));
        Glide.with(context).load(Utils.getAlbumArtUri(song.getAlbumId())).placeholder(R.drawable.ic_empty).into(holder.albumArt);

        holder.more_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.option_play:
                                break;
                            case R.id.option_add_to_playlist:
                                break;
                            case R.id.option_go_to_album:
                                Utils.slideFragment(AlbumDetailFragment.newInstance(song.getAlbum(), song.getAlbumId()), ((MainActivity) context).getSupportFragmentManager());
                                break;
                            case R.id.option_go_to_artist:
                                Utils.slideFragment(ArtistDetailFragment.newInstance(song.getArtist(), song.getArtistId()), ((MainActivity) context).getSupportFragmentManager());
                                break;
                            case R.id.option_delete_from_device:
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.inflate(R.menu.more_option);
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_view_name;
        TextView text_view_artist;
        TextView text_view_duration;
        ImageView albumArt;
        View more_option;

        public ViewHolder(final View itemView) {
            super(itemView);

            text_view_name = (TextView) itemView.findViewById(R.id.text_view_name);
            text_view_artist = (TextView) itemView.findViewById(R.id.text_view_artist);
            text_view_duration = (TextView) itemView.findViewById(R.id.text_view_duration);
            albumArt = (ImageView) itemView.findViewById(R.id.albumArt);
            more_option = itemView.findViewById(R.id.layout_action);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }
}
