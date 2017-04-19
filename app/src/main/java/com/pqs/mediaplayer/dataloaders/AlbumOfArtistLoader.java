package com.pqs.mediaplayer.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.pqs.mediaplayer.models.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongpq on 19/04/2017.
 */

public class AlbumOfArtistLoader {
    private static final String ORDER_BY = MediaStore.Audio.Albums.ALBUM + " ASC";
    private static String[] PROJECTIONS = {
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.LAST_YEAR
    };

    public static List<Album> getAllAlbums(Context context, long artistId) {
        return getSongForCursor(makeAlbumCursor(context, artistId, PROJECTIONS, null, null, ORDER_BY), artistId);
    }

    public static List<Album> getSongForCursor(Cursor data, long artistId) {
        List<Album> albums = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Album album = cursorToAlbum(data, artistId);
                albums.add(album);
            } while (data.moveToNext());
        }

        return albums;
    }

    private static Album cursorToAlbum(Cursor cursor, long artistId) {
        Album album = new Album();
        album.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)));
        album.setArtistID(artistId);
        album.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)));
        album.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)));
        album.setSongCount(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
        album.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.LAST_YEAR)));
        return album;
    }

    public static Cursor makeAlbumCursor(Context context, long artistId, String[] projections, String selection, String[] selectionArgs, String sortOder) {
        return context.getContentResolver().query(MediaStore.Audio.Artists.Albums.getContentUri("external", artistId), projections, selection, selectionArgs, sortOder);
    }
}
