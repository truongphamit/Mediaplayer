package com.pqs.mediaplayer.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.listener.OnItemClickListener;
import com.pqs.mediaplayer.models.Album;
import com.pqs.mediaplayer.utils.Utils;

import java.io.IOException;
import java.util.List;

/**
 * Created by truongpq on 4/18/17.
 */

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder>{
    private Context context;
    private List<Album> albums;

    public AlbumsAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_album, parent, false);

        // Return a new holder instance
        return new AlbumsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.album_title.setText(album.getTitle());
        holder.album_artist.setText(album.getArtistName());
        Glide.with(context).load(Utils.getAlbumArtUri(album.getId())).placeholder(R.drawable.ic_empty).into(holder.album_art);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Utils.getAlbumArtUri(album.getId()));
            new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch swatch = palette.getVibrantSwatch();
                    if (swatch != null) {
                        int color = swatch.getRgb();
                        holder.footer.setBackgroundColor(color);
                        int textColor = Utils.getBlackWhiteColor(swatch.getTitleTextColor());
                        holder.album_title.setTextColor(textColor);
                        holder.album_artist.setTextColor(textColor);
                    } else {
                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                        if (mutedSwatch != null) {
                            int color = mutedSwatch.getRgb();
                            holder.footer.setBackgroundColor(color);
                            int textColor = Utils.getBlackWhiteColor(mutedSwatch.getTitleTextColor());
                            holder.album_title.setTextColor(textColor);
                            holder.album_artist.setTextColor(textColor);
                        }
                    }


                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Album getAlbum(int position) {
        return albums.get(position);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView album_artist;
        TextView album_title;
        LinearLayout footer;
        ImageView album_art;

        public ViewHolder(final View itemView) {
            super(itemView);
            album_artist = (TextView) itemView.findViewById(R.id.album_artist);
            album_title = (TextView) itemView.findViewById(R.id.album_title);
            footer = (LinearLayout) itemView.findViewById(R.id.footer);
            album_art = (ImageView) itemView.findViewById(R.id.album_art);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }
}
