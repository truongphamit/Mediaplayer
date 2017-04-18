package com.pqs.mediaplayer.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.pqs.mediaplayer.models.Artist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongpq on 4/18/17.
 */

public class ArtistLoader {
    private static final Uri MEDIA_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
    private static final String ORDER_BY = MediaStore.Audio.Artists.ARTIST + " ASC";
    private static String[] PROJECTIONS = {
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
    };

    public static List<Artist> getAllArtists(Context context) {
        return getSongForCursor(makeCursor(context, PROJECTIONS, null, null, ORDER_BY));
    }

    public static List<Artist> getSongForCursor(Cursor data) {
        List<Artist> artists = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Artist artist = cursorToArtist(data);
                artists.add(artist);
            } while (data.moveToNext());
        }

        return artists;
    }

    private static Artist cursorToArtist(Cursor cursor) {
        Artist artist = new Artist();
        artist.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)));
        artist.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)));
        artist.setSongCount(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));
        artist.setAlbumCount(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
        return artist;
    }

    public static Cursor makeCursor(Context context, String[] projections, String selection, String[] selectionArgs, String sortOder) {
        return context.getContentResolver().query(MEDIA_URI, projections, selection, selectionArgs, sortOder);
    }
}
