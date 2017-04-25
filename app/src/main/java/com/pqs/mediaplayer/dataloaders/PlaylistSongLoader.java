package com.pqs.mediaplayer.dataloaders;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore;

import com.pqs.mediaplayer.models.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongpq on 4/23/17.
 */

public class PlaylistSongLoader {

    private static Cursor mCursor;

    private static long mPlaylistID;
    private static Context context;


    public static List<Song> getSongsInPlaylist(Context mContext, long playlistID) {
        ArrayList<Song> mSongList = new ArrayList<>();

        context = mContext;
        mPlaylistID = playlistID;

        final int playlistCount = countPlaylist(context, mPlaylistID);

        mCursor = makePlaylistSongCursor(context, mPlaylistID);

        if (mCursor != null) {
            boolean runCleanup = false;
            if (mCursor.getCount() != playlistCount) {
                runCleanup = true;
            }

            if (!runCleanup && mCursor.moveToFirst()) {
                final int playOrderCol = mCursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.PLAY_ORDER);

                int lastPlayOrder = -1;
                do {
                    int playOrder = mCursor.getInt(playOrderCol);
                    if (playOrder == lastPlayOrder) {
                        runCleanup = true;
                        break;
                    }
                    lastPlayOrder = playOrder;
                } while (mCursor.moveToNext());
            }

            if (runCleanup) {

                cleanupPlaylist(context, mPlaylistID, mCursor);

                mCursor.close();
                mCursor = makePlaylistSongCursor(context, mPlaylistID);
                if (mCursor != null) {
                }
            }
        }

        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setId(mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                song.setTitle(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                String displayName = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                if (displayName.endsWith(".mp3")) {
                    displayName = displayName.substring(0, displayName.length() - 4);
                }
                song.setDisplayName(displayName);
                song.setArtist(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setArtistId(mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)));
                song.setAlbum(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                song.setAlbumId(mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                song.setPath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setDuration(mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                song.setSize(mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));

                mSongList.add(song);
            } while (mCursor.moveToNext());
        }
        // Close the cursor
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongList;
    }

    private static void cleanupPlaylist(final Context context, final long playlistId,
                                        final Cursor cursor) {
        final int idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newDelete(uri).build());

        final int YIELD_FREQUENCY = 100;

        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                final ContentProviderOperation.Builder builder =
                        ContentProviderOperation.newInsert(uri)
                                .withValue(MediaStore.Audio.Playlists.Members.PLAY_ORDER, cursor.getPosition())
                                .withValue(MediaStore.Audio.Playlists.Members.AUDIO_ID, cursor.getLong(idCol));

                if ((cursor.getPosition() + 1) % YIELD_FREQUENCY == 0) {
                    builder.withYieldAllowed(true);
                }
                ops.add(builder.build());
            } while (cursor.moveToNext());
        }

        try {
            context.getContentResolver().applyBatch(MediaStore.AUTHORITY, ops);
        } catch (RemoteException e) {
        } catch (OperationApplicationException e) {
        }
    }


    private static int countPlaylist(final Context context, final long playlistId) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                    new String[]{
                            MediaStore.Audio.Playlists.Members.AUDIO_ID,
                    }, null, null,
                    MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);

            if (c != null) {
                return c.getCount();
            }
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }

        return 0;
    }


    public static final Cursor makePlaylistSongCursor(final Context context, final Long playlistID) {
        final StringBuilder mSelection = new StringBuilder();
        mSelection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
        mSelection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");
        return context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID),
                new String[]{
                        MediaStore.Audio.Playlists.Members._ID,
                        MediaStore.Audio.Playlists.Members.AUDIO_ID,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.ARTIST_ID,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DURATION,
                        MediaStore.Audio.AudioColumns.SIZE,
                        MediaStore.Audio.AudioColumns.TRACK,
                        MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                }, mSelection.toString(), null,
                MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
    }
}
