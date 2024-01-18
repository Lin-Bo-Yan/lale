package com.dmcbig.mediapicker.data;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.dmcbig.mediapicker.R;
import com.dmcbig.mediapicker.StringUtils;
import com.dmcbig.mediapicker.entity.Folder;
import com.dmcbig.mediapicker.entity.Media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by dmcBig on 2017/6/9.
 */

public class MediaLoader extends LoaderM implements LoaderManager.LoaderCallbacks {
    String[] MEDIA_PROJECTION = {
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.PARENT,
            MediaStore.Files.FileColumns.MIME_TYPE};

    Context mContext;
    DataCallback mLoader;

    public MediaLoader(Context context, DataCallback loader) {
        this.mContext = context;
        this.mLoader = loader;
    }

    @Override
    public Loader onCreateLoader(int picker_type, Bundle bundle) {
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");
        CursorLoader cursorLoader = new CursorLoader(
                mContext,
                queryUri,
                MEDIA_PROJECTION,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        try {
            ArrayList<Folder> folders = new ArrayList<>();
            Folder allFolder = new Folder(mContext.getResources().getString(R.string.all_dir_name));
            folders.add(allFolder);
            Folder allVideoDir = new Folder(mContext.getResources().getString(R.string.all_video));
            folders.add(allVideoDir);
            Cursor cursor = (Cursor) o;
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
                long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
                int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));

                if (size < 1) continue;
                if (path == null || "".equals(path)) continue;
                String dirName = getParent(path);// don't show lale background folder
                if (".background".equals(dirName)) {
                    continue;
                }
                List<String> acceptedMimeTypes = specificExtension();
                if (acceptedMimeTypes.contains(mimeType)) {
                    Media media = new Media(path, name, dateTime, mediaType, size, id, dirName, mimeType);
                    allFolder.addMedias(media);

                    if (mediaType == 3) {
                        allVideoDir.addMedias(media);
                    }

                    int index = hasDir(folders, dirName);
                    if (index != -1) {
                        folders.get(index).addMedias(media);
                    } else {
                        Folder folder = new Folder(dirName);
                        folder.addMedias(media);
                        folders.add(folder);
                    }
                }
            }
            mLoader.onData(folders);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }

    private List<String> specificExtension(){
        List<String> acceptedMimeTypes = Arrays.asList("image/png", "image/x-ms-bmp", "video/mp4", "image/jpeg", "image/gif", "image/heic", "image/jpg","image/jpe");
        return acceptedMimeTypes;
    }
}
