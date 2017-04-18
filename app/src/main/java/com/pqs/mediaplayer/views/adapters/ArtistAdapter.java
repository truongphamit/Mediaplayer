package com.pqs.mediaplayer.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.models.Artist;

import java.util.List;

/**
 * Created by truongpq on 4/18/17.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder>{
    private Context context;
    private List<Artist> artists;

    public ArtistAdapter(Context context, List<Artist> artists) {
        this.context = context;
        this.artists = artists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_album, parent, false);

        // Return a new holder instance
        return new ArtistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.artist_name.setText(artist.getName());
        holder.album_song_count.setText(artist.getAlbumCount() + " albums | " + artist.getSongCount() + " songs");
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView album_song_count;
        TextView artist_name;
        LinearLayout footer;
        ImageView artistImage;

        public ViewHolder(View itemView) {
            super(itemView);
            album_song_count = (TextView) itemView.findViewById(R.id.album_artist);
            artist_name = (TextView) itemView.findViewById(R.id.album_title);
            footer = (LinearLayout) itemView.findViewById(R.id.footer);
            artistImage = (ImageView) itemView.findViewById(R.id.album_art);
        }
    }
}
