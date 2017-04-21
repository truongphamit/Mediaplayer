package com.pqs.mediaplayer.source;

import android.content.Context;
import android.content.SharedPreferences;

import com.pqs.mediaplayer.player.PlayMode;

/**
 * Created by truongpq on 4/20/17.
 */

public class PreferenceManager {
    private static final String PREFS_NAME = "config.xml";

    private static final String KEY_PLAY_MODE = "playMode";

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor edit(Context context) {
        return preferences(context).edit();
    }

    /**
     * {@link #KEY_PLAY_MODE}
     */
    public static PlayMode lastPlayMode(Context context) {
        String playModeName = preferences(context).getString(KEY_PLAY_MODE, null);
        if (playModeName != null) {
            return PlayMode.valueOf(playModeName);
        }
        return PlayMode.getDefault();
    }

    /**
     * {@link #KEY_PLAY_MODE}
     */
    public static void setPlayMode(Context context, PlayMode playMode) {
        edit(context).putString(KEY_PLAY_MODE, playMode.name()).commit();
    }
}
