package com.pqs.mediaplayer.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.pqs.mediaplayer.models.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongpq on 4/18/17.
 */

public class AlbumLoader {
    private static final Uri MEDIA_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    private static final String ORDER_BY = MediaStore.Audio.Albums.ALBUM + " ASC";
    private static String[] PROJECTIONS = {
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.LAST_YEAR
    };

    public static List<Album> getAllAlbums(Context context) {
        return getSongForCursor(makeAlbumCursor(context, PROJECTIONS, null, null, ORDER_BY));
    }

    public static List<Album> getSongForCursor(Cursor data) {
        List<Album> albums = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Album album = cursorToAlbum(data);
                albums.add(album);
            } while (data.moveToNext());
        }

        return albums;
    }

    private static Album cursorToAlbum(Cursor cursor) {
        Album album = new Album();
        album.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)));
        album.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)));
        album.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)));
        album.setSongCount(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
        album.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.LAST_YEAR)));
        return album;
    }

    public static Cursor makeAlbumCursor(Context context, String[] projections, String selection, String[] selectionArgs, String sortOder) {
        return context.getContentResolver().query(MEDIA_URI, projections, selection, selectionArgs, sortOder);
    }
}
