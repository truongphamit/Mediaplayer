package com.pqs.mediaplayer.utils;

import android.content.ContentUris;
import android.graphics.Color;
import android.net.Uri;

/**
 * Created by truongpq on 4/18/17.
 */

public class Utils {
    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    public static int getBlackWhiteColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness >= 0.5) {
            return Color.WHITE;
        } else return Color.BLACK;
    }
}
