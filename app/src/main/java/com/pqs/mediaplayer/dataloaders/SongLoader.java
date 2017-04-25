package com.pqs.mediaplayer.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.pqs.mediaplayer.models.Song;
import com.pqs.mediaplayer.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongpq on 4/18/17.
 */

public class SongLoader {
    public static final Uri MEDIA_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public static final String WHERE = MediaStore.Audio.Media.IS_MUSIC + "=1 AND "
            + MediaStore.Audio.Media.SIZE + ">0";
    public static final String ORDER_BY = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";
    public static String[] PROJECTIONS = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA, // the real path
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
    };

    public static List<Song> getAllSongs(Context context) {
        return getSongForCursor(makeSongCursor(context, PROJECTIONS, WHERE, null, ORDER_BY));
    }

    public static List<Song> searchSongs(Context context, String searchString, int limit) {
        List<Song> result = getSongForCursor(makeSongCursor(context, PROJECTIONS, WHERE + " AND title LIKE ?", new String[]{searchString + "%"}, ORDER_BY));
        if (result.size() < limit) {
            result.addAll(getSongForCursor(makeSongCursor(context, PROJECTIONS, WHERE + " AND title LIKE ?", new String[]{"%_" + searchString + "%"}, ORDER_BY)));
        }
        return result.size() < limit ? result : result.subList(0, limit);
    }

    public static List<Song> getSongForCursor(Cursor data) {
        List<Song> songs = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Song song = cursorToMusic(data);
                songs.add(song);
            } while (data.moveToNext());
        }

        return songs;
    }

    private static Song cursorToMusic(Cursor cursor) {
        Song song = new Song();
        song.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
        song.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
        String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
        if (displayName.endsWith(".mp3")) {
            displayName = displayName.substring(0, displayName.length() - 4);
        }
        song.setDisplayName(displayName);
        song.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
        song.setArtistId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)));
        song.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
        song.setAlbumId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
        song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
        song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
        song.setSize(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
        return song;
    }

    public static Cursor makeSongCursor(Context context, String[] projections, String selection, String[] selectionArgs, String sortOder) {
        return context.getContentResolver().query(MEDIA_URI, projections, selection, selectionArgs, sortOder);
    }
}
