package com.pqs.mediaplayer.views.adapters;

/**
 * Created by truongpq on 4/21/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pqs.mediaplayer.MainActivity;
import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.activities.SearchActivity;
import com.pqs.mediaplayer.fragments.AlbumDetailFragment;
import com.pqs.mediaplayer.fragments.ArtistDetailFragment;
import com.pqs.mediaplayer.models.Album;
import com.pqs.mediaplayer.models.Artist;
import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.utils.Constants;
import com.pqs.mediaplayer.utils.TimeUtils;
import com.pqs.mediaplayer.utils.Utils;

import java.util.Collections;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemHolder> {

    private Activity mContext;
    private List searchResults = Collections.emptyList();

    public SearchAdapter(Activity context) {
        this.mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0:
                View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_local_music, null);
                ItemHolder ml0 = new ItemHolder(v0);
                return ml0;
            case 1:
                View v1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_search, null);
                ItemHolder ml1 = new ItemHolder(v1);
                return ml1;
            case 2:
                View v2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist, null);
                ItemHolder ml2 = new ItemHolder(v2);
                return ml2;
            case 10:
                View v10 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_section_header, null);
                ItemHolder ml10 = new ItemHolder(v10);
                return ml10;
            default:
                View v3 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_local_music, null);
                ItemHolder ml3 = new ItemHolder(v3);
                return ml3;
        }
    }

    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, final int i) {
        switch (getItemViewType(i)) {
            case 0:
                final Song song = (Song) searchResults.get(i);
                itemHolder.title.setText(song.getDisplayName());
                itemHolder.songartist.setText(song.getAlbum());
                itemHolder.text_view_duration.setText(TimeUtils.formatDuration(song.getDuration()));
                Glide.with(mContext).load(Utils.getAlbumArtUri(song.getAlbumId())).placeholder(R.drawable.ic_empty).into(itemHolder.albumArt);

                itemHolder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(mContext, v);

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.option_add_to_playlist:
                                        break;
                                    case R.id.option_go_to_album:
                                        goToAlbum(mContext, song.getAlbum(), song.getAlbumId());
                                        break;
                                    case R.id.option_go_to_artist:
                                        goToArtist(mContext, song.getArtist(), song.getArtistId());
                                        break;
                                    case R.id.option_delete_from_device:
                                        Utils.showDeleteDialog(mContext, song.getDisplayName(), SearchAdapter.this, i);
                                        break;
                                }
                                return false;
                            }
                        });

                        popupMenu.inflate(R.menu.more_option);
                        popupMenu.show();
                    }
                });
                break;
            case 1:
                Album album = (Album) searchResults.get(i);
                itemHolder.albumtitle.setText(album.getTitle());
                itemHolder.albumartist.setText(album.getArtistName());
                Glide.with(mContext).load(Utils.getAlbumArtUri(album.getId())).placeholder(R.drawable.ic_empty).into(itemHolder.albumArt);
                break;
            case 2:
                Artist artist = (Artist) searchResults.get(i);
                itemHolder.artisttitle.setText(artist.getName());
                itemHolder.albumsongcount.setText(artist.getAlbumCount() + " albums | " + artist.getSongCount() + " songs");
                break;
            case 10:
                itemHolder.sectionHeader.setText((String) searchResults.get(i));
            case 3:
                break;
        }
    }

    @Override
    public void onViewRecycled(ItemHolder itemHolder) {

    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (searchResults.get(position) instanceof Song)
            return 0;
        if (searchResults.get(position) instanceof Album)
            return 1;
        if (searchResults.get(position) instanceof Artist)
            return 2;
        if (searchResults.get(position) instanceof String)
            return 10;
        return 3;
    }

    public void goToAlbum(Context context, String album, long albumId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_ALBUM);
        intent.putExtra(Constants.ALBUM_ID, albumId);
        intent.putExtra(Constants.ALBUM, album);
        context.startActivity(intent);
    }

    public void goToArtist(Context context, String artist, long artistId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_ARTIST);
        intent.putExtra(Constants.ARTIST_ID, artistId);
        intent.putExtra(Constants.ARTIST, artist);
        context.startActivity(intent);
    }

    public void goToNowplaying(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_NOWPLAYING);
        context.startActivity(intent);
    }

    public void updateSearchResults(List searchResults) {
        this.searchResults = searchResults;
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onSongItemClick(View itemView, Song song);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        protected TextView title, songartist, albumtitle, artisttitle, albumartist, albumsongcount, sectionHeader, text_view_duration;
        protected ImageView albumArt, artistImage, menu;

        public ItemHolder(View view) {
            super(view);

            this.title = (TextView) view.findViewById(R.id.text_view_name);
            this.songartist = (TextView) view.findViewById(R.id.text_view_artist);
            this.text_view_duration = (TextView) view.findViewById(R.id.text_view_duration);
            this.menu = (ImageView) view.findViewById(R.id.more_option);

            this.albumsongcount = (TextView) view.findViewById(R.id.album_song_count);
            this.artisttitle = (TextView) view.findViewById(R.id.artist_name);
            this.albumtitle = (TextView) view.findViewById(R.id.album_title);
            this.albumartist = (TextView) view.findViewById(R.id.album_artist);
            this.albumArt = (ImageView) view.findViewById(R.id.albumArt);
            this.artistImage = (ImageView) view.findViewById(R.id.artistImage);

            this.sectionHeader = (TextView) view.findViewById(R.id.section_header);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getItemViewType()) {
                        case 0:
                            // Song
                            if (listener != null) {
                                listener.onSongItemClick(v, (Song) searchResults.get(getLayoutPosition()));
                                goToNowplaying(mContext);
                            }
                            break;
                        case 1:
                            // Album
                            Album album = (Album) searchResults.get(getLayoutPosition());
                            goToAlbum(mContext, album.getArtistName(), album.getId());
                            break;
                        case 2:
                            // Artist
                            Artist artist = (Artist) searchResults.get(getLayoutPosition());
                            goToArtist(mContext, artist.getName(), artist.getId());
                            break;
                    }
                }
            });
        }
    }
}
