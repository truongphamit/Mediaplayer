package com.pqs.mediaplayer.utils;

import android.content.Context;
import android.os.Environment;

import com.pqs.mediaplayer.R;
import com.pqs.mediaplayer.models.Folder;
import com.pqs.mediaplayer.models.PlayList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongpq on 4/17/17.
 */

public class DBUtils {

    public static PlayList generateFavoritePlayList(Context context) {
        PlayList favorite = new PlayList();
        favorite.setFavorite(true);
        favorite.setName(context.getString(R.string.mp_play_list_favorite));
        return favorite;
    }

    public static List<Folder> generateDefaultFolders() {
        List<Folder> defaultFolders = new ArrayList<>(3);
        // File sdcardDir = Environment.getExternalStorageDirectory();
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        // defaultFolders.add(FileUtils.folderFromDir(sdcardDir));
        defaultFolders.add(FileUtils.folderFromDir(downloadDir));
        defaultFolders.add(FileUtils.folderFromDir(musicDir));
        return defaultFolders;
    }
}
