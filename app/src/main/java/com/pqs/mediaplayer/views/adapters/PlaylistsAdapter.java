package com.pqs.mediaplayer.views.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.dataloaders.PlaylistLoader;
import com.pqs.mediaplayer.listener.OnItemClickListener;
import com.pqs.mediaplayer.models.PlayList;

import java.util.List;

/**
 * Created by truongpq on 4/23/17.
 */

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.ViewHolder> {
    private Context context;
    private List<PlayList> playLists;

    public PlaylistsAdapter(Context context, List<PlayList> playLists) {
        this.context = context;
        this.playLists = playLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_playlist, parent, false);

        // Return a new holder instance
        return new PlaylistsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PlayList playList = playLists.get(position);

        holder.tv_playlist_name.setText(playList.getName());

        holder.more_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.option_delete:
                                PlaylistLoader.deletePlaylists(context, playList.getId());
                                playLists.remove(position);
                                notifyItemRemoved(position);
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.inflate(R.menu.playlist_more_option);
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return playLists.size();
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_playlist_name;
        ImageView more_option;

        public ViewHolder(final View itemView) {
            super(itemView);
            tv_playlist_name = (TextView) itemView.findViewById(R.id.tv_playlist_name);
            more_option = (ImageView) itemView.findViewById(R.id.more_option);

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
