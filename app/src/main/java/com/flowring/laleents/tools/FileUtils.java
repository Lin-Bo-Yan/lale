package com.flowring.laleents.tools;

import static android.content.Context.ACTIVITY_SERVICE;

import static com.flowring.laleents.tools.phone.DefinedUtils.DEFAULT_BUFFER_SIZE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;

import androidx.annotation.WorkerThread;

import com.flowring.laleents.R;
import com.flowring.laleents.tools.cloud.api.AsynNetUtils;
import com.flowring.laleents.tools.phone.DefinedUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
    public static String download(String urlStr) {

        StringBuffer sb = new StringBuffer();

        String line = null;

        BufferedReader buffer = null;

        URL url = null;

        try {

// 建立一個URL物件,代表這個地址

            url = new URL(urlStr);

// 建立一個Http連線

            HttpURLConnection urlConn = (HttpURLConnection) url

                    .openConnection();

// 使用IO流讀取資料

            buffer = new BufferedReader(new InputStreamReader(urlConn

                    .getInputStream()));//urlConn.getInputStream() 代表的就是這個地址的檔案

            while ((line = buffer.readLine()) != null) {

                sb.append(line);

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {

                buffer.close();

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        return sb.toString();

    }

    public static InputStream getInputStreamFromUrl(String urlStr)

            throws MalformedURLException, IOException {

        URL url = new URL(urlStr);

        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

        InputStream inputStream = urlConn.getInputStream();

        return inputStream;

    }


    private static final String TAG = FileUtils.class.getSimpleName();

    public static boolean saveAudioToApp(Context context, String fileName, byte[] getFile) {
        String fileFolder = getApplicationFolder(context, DefinedUtils.FOLDER_AUDIO);
        return saveFileToApp(fileFolder, fileName, getFile);
    }

    public static boolean saveVideoToApp(Context context, String fileName, byte[] getFile) {
        String fileFolder = getApplicationFolder(context, DefinedUtils.FOLDER_VIDEO);
        return saveFileToApp(fileFolder, fileName, getFile);
    }


    private static boolean saveFileToApp(String fileFolder, String fileName, byte[] getFile) {
        File file = new File(fileFolder, fileName);
        try {
            OutputStream output = new FileOutputStream(file);
            output.write(getFile);
            output.flush();
            output.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            StringUtils.HaoLog("e=" + e);
        }
        return false;
    }


    @WorkerThread
    public static void newFolderToDCIM(String folderName) {
        String filePath =
                Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + folderName;
        File dirFile = new File(filePath);

        if (!dirFile.exists()) {//如果資料夾不存在
            dirFile.mkdir();//建立資料夾
        }
    }

    @WorkerThread
    public static boolean savePicToDCIM(String imageUrl, String fileName) {
        String filePath =
                Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + fileName;
        Bitmap bitmapFromURL = getBitmapFromURL(imageUrl);
        if (bitmapFromURL != null) {
            try {

                Bitmap.CompressFormat format = (fileName.contains(".png")) ?
                        Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
                FileOutputStream fileOS = new FileOutputStream(filePath);
                bitmapFromURL.compress(format, 100, fileOS);
                StringUtils.HaoLog("圖片下載完成");
                return true;
            } catch (Exception e) {

                StringUtils.HaoLog("圖片下載失敗");
                e.printStackTrace();
            }
        } else {
            StringUtils.HaoLog("bitmapFromURL=null");
            return false;
        }
        return false;
    }


    public static File createImageFile(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File image = new File(context.getCacheDir(), imageFileName);

        return image;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }


            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    final String path = id.replaceFirst("raw:", "");
                    return path;
                }
                Uri contentUri = uri;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    if (!id.isEmpty()) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        } else if (id.startsWith("msf:")) {
                            return getMSFFile(uri, id, context);
                        } else {
                            contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                        }
                    }
                }

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getMSFFile(Uri uri, String id, Context context) {
        if (id != null && id.startsWith("msf:")) {
            File dir = new File(context.getCacheDir().getAbsolutePath());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            final File file = new File(dir, System.currentTimeMillis() + "." + Objects.requireNonNull(context.getContentResolver().getType(uri)).split("/")[1]);
            Log.d(TAG, "path : " + file.getAbsolutePath());
            try (
                    final InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    OutputStream output = new FileOutputStream(file)) {
                final byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
                output.close();
                return file.getPath();
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.d(TAG, "getMSFFile ERROR" + ex.getMessage());
            }
        }
        return null;
    }


    /*
        public static File[] getFilesFromUris(final Context context, final Uri[] uris, final boolean mustCanRead) {
            if (uris == null) {
                return null;
            }
            final int urisLength = uris.length;
            final File[] files = new File[urisLength];
            for (int i = 0; i < urisLength; ++i) {
                final Uri uri = uris[i];
                files[i] = getFileFromUri(context, uri, mustCanRead);
            }
            return files;
        }

        public static String getAbsolutePathFromUri(final Context context, final Uri uri, final boolean mustCanRead) {
            final File file = getFileFromUri(context, uri, mustCanRead);
            if (file != null) {
                return file.getAbsolutePath();
            } else {
                return null;
            }
        }

        public static File getFileFromUri(final Context context, final Uri uri) {
            return getFileFromUri(context, uri, false);
        }
    */
    public static String getFilePathFromUri(final Context context, final Uri uri) {

        if (DocumentsContract.isDocumentUri(context, uri)) {
            String sFilePath = "";
            File fileTmp = null;
            if (isDownloadsDocument(uri)) {
                String sDocID = DocumentsContract.getDocumentId(uri);
                if (sDocID.startsWith("raw:"))
                    return sDocID.replaceFirst("raw:", "");
                if (sDocID.contains(":"))
                    return sDocID.split(":")[1];
                StringUtils.HaoLog("sDocID=" + sDocID);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(sDocID));

                sFilePath = queryFilePath(context, contentUri, 1);
                fileTmp = new File(sFilePath);
                if (fileTmp.exists()) {
                    StringUtils.HaoLog("回傳成功");
                    return fileTmp.getAbsolutePath();
                } else {
                    StringUtils.HaoLog("回傳失敗");
                    return "";
                }
            } else {
                try {
                    String sFileName = queryFilePath(context, uri, 0);

                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    fileTmp = createTemporalFileFrom(context, inputStream, sFileName);
                    return fileTmp.getPath();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }
        } else {
            String sScheme = uri.getScheme();
            if (sScheme.equals("content")) {

            } else if (sScheme.equals("file"))
                return uri.getPath();
        }

        return "";
    }

    public static File[] getFilePathsFromUris(final Context context, final ArrayList<Uri> uris, final String[] fileNames) {
        File[] outputFiles = new File[uris.size()];
        for (int i = 0; i < uris.size(); i++) {
            Uri uri = uris.get(i);
            String fileName = fileNames[i];
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                // 將成為新緩存文件的文件
                Resources resources = context.getResources();
                String app_name = resources.getString(R.string.app_name);
                String tableOfContents = Environment.DIRECTORY_PICTURES + File.separator + app_name + File.separator + "externalSharing";
                File outputFile = new File(Environment.getExternalStoragePublicDirectory(tableOfContents), fileName);
                //getAbsolutePath()用於獲取文件的絕對路徑。它返回一個字符串，表示文件在文件系統中的完整路徑
                File outputDirectory = new File(Environment.getExternalStoragePublicDirectory(tableOfContents).getAbsolutePath());
                if (!outputDirectory.exists()) {
                    outputDirectory.mkdirs();
                }

                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
                // 這將保存每次迭代的內容
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                // 如果到達文件末尾則為 -1
                int readBytes = 0;
                while (true) {
                    readBytes = bufferedInputStream.read(buffer);
                    // 檢查讀取是否失敗
                    if (readBytes == -1) {
                        bufferedOutputStream.flush();
                        break;
                    }

                    bufferedOutputStream.write(buffer);
                    bufferedOutputStream.flush();
                }
                // 關閉一切
                inputStream.close();
                bufferedInputStream.close();
                bufferedOutputStream.close();
                outputFiles[i] = outputFile;
            } catch (FileNotFoundException e) {
                StringUtils.HaoLog("無法打開閱讀 " + e);
            } catch (IOException e) {
                StringUtils.HaoLog("無法打開閱讀 " + e);
                e.printStackTrace();
            }
        }
        return outputFiles;
    }

    public static String getContentURIFileName(final Context context, final Uri uri){
        String fileName = null;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if(index >= 0){
                    fileName = cursor.getString(index);
                } else { StringUtils.HaoLog("長度小於0"); }
            } else { StringUtils.HaoLog("該列不存在"); }
        } finally {
            if (cursor != null) { cursor.close(); }
        }
        if (fileName != null) {
            return fileName;
        }else {
            return "";
        }
    }

    public static String[] getContentURIFileNames(final Context context, final ArrayList<Uri> uris) {
        String[] fileNames = new String[uris.size()];
        for (int i = 0; i < uris.size(); i++) {
            Uri uri = uris.get(i);
            String fileName = null;
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        fileName = cursor.getString(index);
                    } else {
                        StringUtils.HaoLog("長度小於0");
                    }
                } else {
                    StringUtils.HaoLog("該列不存在");
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            if (fileName != null) {
                fileNames[i] = fileName;
            } else {
                fileNames[i] = "";
            }
        }
        return fileNames;
    }

    public static File getFilePathFromUri(final Context context, final Uri uri, final String fileName) {
        File outputFile = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            // 將成為新緩存文件的文件
            Resources resources = context.getResources();
            String app_name = resources.getString(R.string.app_name);
            String tableOfContents = Environment.DIRECTORY_PICTURES + File.separator + app_name + File.separator + "externalSharing";
            outputFile = new File(Environment.getExternalStoragePublicDirectory(tableOfContents), fileName);
            //getAbsolutePath()用於獲取文件的絕對路徑。它返回一個字符串，表示文件在文件系統中的完整路徑
            File outputDirectory = new File(Environment.getExternalStoragePublicDirectory(tableOfContents).getAbsolutePath());
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            // 這將保存每次迭代的內容
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            // 如果到達文件末尾則為 -1
            int readBytes = 0;
            while (true) {
                readBytes = bufferedInputStream.read(buffer);
                // 檢查讀取是否失敗
                if (readBytes == -1) {
                    bufferedOutputStream.flush();
                    break;
                }

                bufferedOutputStream.write(buffer);
                bufferedOutputStream.flush();
            }
            // 關閉一切
            inputStream.close();
            bufferedInputStream.close();
            bufferedOutputStream.close();
            return outputFile;
        } catch (FileNotFoundException e) {
            StringUtils.HaoLog("無法打開閱讀 " + e);
        } catch (IOException e) {
            StringUtils.HaoLog("無法打開閱讀 " + e);
            e.printStackTrace();
        }
        return outputFile;
    }

    public static File createTemporalFileFrom(final Context context, final InputStream inputStream, final String fileName) {
        try {
            byte[] buffer = new byte[8 * 1024];
            File targetFile = new File(context.getCacheDir(), fileName);
            if (targetFile != null && targetFile.exists())
                targetFile.delete();
            FileOutputStream fileOS = new FileOutputStream(targetFile);

            while (inputStream.read(buffer, 0, buffer.length) != -1) {
                fileOS.write(buffer, 0, buffer.length);
            }
            fileOS.flush();
            fileOS.close();

            return targetFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String queryFilePath(final Context context, final Uri uri, final int idx) {
        final String[] column = {MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, column, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(column[idx]));
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    public static JSONArray getFileInfo(File[] files){
        JSONArray jArray = new JSONArray();
        try {
            for(File file : files){
                JSONObject jsonObject = new JSONObject();
                Uri url = Uri.fromFile(file);
                jsonObject.put("name",file.getName());
                jsonObject.put("mimeType",contentType(fileType(file.getName())));
                jsonObject.put("url",url.toString());
                if(".jpg".equals(fileType(file.getName()))){
                    String pic = ThumbnailUtils.resizeAndConvertToBase64(file.getPath(),50);
                    jsonObject.put("thumbnail",pic);
                }
                if(limitFileSize(file)){
                    jsonObject.put("errorMsg","檔案大小超過50MB，無法上傳");
                } else {
                    //如果是圖片就取得圖片長寬，否則取得影片長寬
                    if(".jpg".equals(fileType(file.getName()))){
                        BitmapFactory.Options options = getBitmapFactory(file);
                        jsonObject.put("Width",options.outWidth);
                        jsonObject.put("Height",options.outHeight);
                    } else {
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(file.getPath());
                        String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                        String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                        jsonObject.put("Width",width);
                        jsonObject.put("Height",height);
                    }
                }
                jArray.put(jsonObject);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jArray;
    }

    public static BitmapFactory.Options getBitmapFactory(File file){
        // 使用 BitmapFactory 讀取圖片檔案
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        return options;
    }

    public static Bitmap getUserPicRound(final Context context, final String userID, final String avatar_url, boolean isGroup) {
        final Bitmap[] bitmap = {null};
        if (userID.isEmpty() || avatar_url == null || avatar_url.isEmpty())
            bitmap[0] = BitmapFactory.decodeResource(context.getResources(),
                    isGroup ? R.drawable.default_group : R.drawable.default_person);
        else if (userID.contains("robot"))
            bitmap[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_group);
        else {
            final String fileName = getUrlFileName(avatar_url, ".jpg");
            bitmap[0] = getCachePicSmall(context, fileName);
            if (bitmap[0] == null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String https = avatar_url;
                        if (avatar_url.contains("mxc://")) {
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                            https = pref.getString(CommonUtils.PREF_MATRIX_URL, "") +
                                    "/_matrix/media/r0/download/" + avatar_url.replace("mxc://", "");
                        }
                        bitmap[0] = NetUtils.GETUserBitmapAndCache(context, https, "", fileName);
                    }
                }).start();
                if (bitmap[0] == null)
                    bitmap[0] = BitmapFactory.decodeResource(context.getResources(),
                            isGroup ? R.drawable.default_group : R.drawable.default_person);
            }
        }
        return bitmap[0];
    }

    public static File saveCachePic(Context context, Bitmap bitmap, String fileName) {
        try {
            File targetFile = new File(context.getCacheDir(), fileName);
            if (targetFile != null && targetFile.exists())
                targetFile.delete();
            Bitmap.CompressFormat format = (fileName.contains(".png")) ?
                    Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
            FileOutputStream fileOS = new FileOutputStream(targetFile);
            bitmap.compress(format, 70, fileOS);
            return targetFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void savePicFile(Bitmap bitmap, String destFilePath, String fileName) {
        try {
            Bitmap.CompressFormat format = (fileName.contains(".png")) ?
                    Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
            FileOutputStream fileOS = new FileOutputStream(destFilePath);
            bitmap.compress(format, 100, fileOS);
        } catch (Exception e) {

            Log.d(TAG, "圖片下載失敗");
            e.printStackTrace();
        }
    }

    public static boolean deletePicFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    public static void saveVideoFile(Context context, Uri videoUri, String destFilePath, int size) {
        ContentResolver cr = context.getContentResolver();

        InputStream inputStream = null;
        BufferedOutputStream bos = null;

        try {
            inputStream = cr.openInputStream(videoUri);
            bos = new BufferedOutputStream(new FileOutputStream(destFilePath, false));
            byte[] b = new byte[size];
            int i = 0;
            while ((i = inputStream.read(b)) != -1) {
                bos.write(b, 0, b.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getVideoFrameFromVideo(Context context, Uri videoUri) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(context, videoUri);
            int width = Integer.valueOf(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int height = Integer.valueOf(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            int angle = Integer.valueOf(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            if (angle == 90 || angle == 270) {
                width = Integer.valueOf(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                height = Integer.valueOf(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            }

            int max = (int) CommonUtils.convertDpToPixel(180, context);
            float fMax = max * 1.0f;
            if (width > height) {
                if (width > max) {
                    float scale = width / fMax;
                    height = (int) (height / scale);
                    width = max;
                }
            } else {
                if (height > max) {
                    float scale = height / fMax;
                    width = (int) (width / scale);
                    height = max;
                }
            }
            Bitmap thumbBmp = mediaMetadataRetriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST);

            bitmap = android.media.ThumbnailUtils.extractThumbnail(thumbBmp, width, height);//Bitmap.createBitmap(thumbBmp, 0, 0, width, height);
            //bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null)
                mediaMetadataRetriever.release();
        }
        return bitmap;
    }

    public static Bitmap getBitmapThumbnail(Context context, Bitmap resBmp) {
        Bitmap bitmap = null;
        try {
            int width = resBmp.getWidth();
            int height = resBmp.getHeight();
            int max = (int) CommonUtils.convertDpToPixel(180, context);
            float fMax = max * 1.0f;
            if (width > height) {
                if (width > max) {
                    float scale = width / fMax;
                    height = (int) (height / scale);
                    width = max;
                }
            } else {
                if (height > max) {
                    float scale = height / fMax;
                    width = (int) (width / scale);
                    height = max;
                }
            }
            bitmap = android.media.ThumbnailUtils.extractThumbnail(resBmp, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return bitmap;
    }

    public static Bitmap getExifRotatedBitmap(String picturePath) {
        Bitmap bm = BitmapFactory.decodeFile(picturePath);
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(picturePath);
        } catch (Exception e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 270;
                    break;
                default:
                    digree = 0;
                    break;
            }
        }
        if (digree != 0) {
            Matrix m = new Matrix();
            m.postRotate(digree);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), m, true);
        }
        return bm;
    }

    public static Bitmap getResizeSticker(String filePath, float size) {
        Bitmap bm = BitmapFactory.decodeFile(filePath);
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 270;
                    break;
                default:
                    digree = 0;
                    break;
            }
        }
        float scale;
        if (bm.getHeight() > bm.getWidth()) {
            scale = size / (float) (bm.getHeight());
        } else {
            scale = size / (float) (bm.getWidth());
        }

        if (digree != 0) {
            Matrix m = new Matrix();
            m.postRotate(digree);
            m.postScale(scale, scale);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), m, true);
        } else {
            Matrix m = new Matrix();
            m.postScale(scale, scale);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), m, true);
        }
        return bm;
    }

    public static Bitmap getCachePic(final Context context, final String fileName) {
        try {
            File file = new File(context.getCacheDir(), fileName);
            if (file.exists()) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                activityManager.getMemoryInfo(mi);
                double availableMegs = mi.availMem / 0x100000L;
                double percentAvail = mi.availMem / (double) mi.totalMem * 100.0;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = (availableMegs < 600) ? 2 : 1;
                return BitmapFactory.decodeFile(file.getPath(), options);
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static Bitmap getCachePicSmall(final Context context, final String fileName) {
        try {
            int size = (int) CommonUtils.convertDpToPixel(90, context);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            File file = new File(context.getCacheDir(), fileName);
            if (file.exists()) {
                BitmapFactory.decodeFile(file.getPath(), options);
                options.inSampleSize = calculateInSampleSize(options, size, size);
                options.inJustDecodeBounds = false;
                return BitmapFactory.decodeFile(file.getPath(), options);
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static Bitmap getStickerCache(final Context context, final String fileName) {
        try {
            int size = (int) CommonUtils.convertDpToPixel(144, context);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            File file = new File(context.getCacheDir(), fileName);
            if (file.exists()) {
                BitmapFactory.decodeFile(file.getPath(), options);
                options.inSampleSize = calculateInSampleSize(options, size, size);
                options.inJustDecodeBounds = false;
                return BitmapFactory.decodeFile(file.getPath(), options);
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String getUrlFileName(String url, String fileType) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        if (fileName.isEmpty())
            return "mxcFile" + fileType;
        return fileName + fileType;
    }

    public static String getImageUrlFileName(String url, String mimeType) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        if (fileName.isEmpty())
            return "mxcFile" + getImageExtension(mimeType);
        return fileName + getImageExtension(mimeType);
    }

    public static String getStickerUrlFileName(String url, String mimeType) {
        url = url.replace("/download", "");
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        if (fileName.isEmpty())
            return "sticker" + getImageExtension(mimeType);
        return fileName;
    }

    public static String getApplicationFolder(Context context, String subFolder) {
        File file = new File(context.getExternalFilesDir("").getParentFile(), subFolder);
        if (file != null) {
            if (!file.exists())
                file.mkdirs();
            return file.getAbsolutePath();
        }
        return context.getExternalCacheDir().getAbsolutePath();
    }

    public static String getMessageFileName(String folder, String fileName, long time) {
        int nID = (int) (time % 100000);
        fileName = String.format("%05d_%s", nID, fileName);
        return fileName;
    }

    // 指定資料夾中不重複的檔案名稱
    public static String getNotDuplicFileName(String folder, String fileName) {
        int num = 1;
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String tmpName = fileName.replaceFirst("[.][^.]+$", "");

        File file = new File(folder, fileName);
        while (file.exists()) {
            fileName = String.format("%s(%d).%s", tmpName, num++, extension);
            file = new File(folder, fileName);
        }

        return fileName;
    }

    public static String getImageExtension(String mimeType) {
        if (mimeType == null) {
            return ".jpg";
        }
        if (mimeType.equals("image/png")) {
            return ".png";
        } else if (mimeType.equals("image/gif")) {
            return ".gif";
        } else {
            return ".jpg";
        }
    }

    public static String getImageMimeType(String extension) {
        if (extension.contains(".png")) {
            return "image/png";
        } else if (extension.contains(".gif")) {
            return "image/gif";
        } else {
            return "image/jpg";
        }
    }

    public static String saveRoomBg(Context context, Bitmap bitmap, String fileName) {
        try {
            File mPicDir = new File(context.getExternalFilesDir("").getParentFile(), DefinedUtils.FOLDER_BACKGROUND);
            if (!mPicDir.exists()) {
                mPicDir.mkdirs();
            }
            File targetFile = new File(mPicDir, fileName + ".jpg");
            if (targetFile != null && targetFile.exists()) {
                targetFile.delete();
            }
            Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
            FileOutputStream fileOS = new FileOutputStream(targetFile);
            bitmap.compress(format, 90, fileOS);
            return targetFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String fileType(String fileName){
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == 0) {
            return "";
        }
        return fileName.substring(lastDotIndex);
    }

    public static String contentType(String value){
        switch (value) {
            case ".xls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".txt":
                return "text/plain";
            case ".jfif":
                return "image/jfif";
            case ".jpe":
                return "image/jpe";
            case ".jpeg":
                return "image/jpeg";
            case ".jpg":
                return "image/jpg";
            case ".m4e":
                return "video/mpeg4";
            case ".m4a":
                return "audio/mp4a-latm";
            case ".mp3":
                return "audio/mp3";
            case ".mp4":
                return "video/mpeg4";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".pdf":
                return "application/pdf";
            case ".zip":
                return "application/zip";
            case ".ppt":
                return "application/vnd.ms-powerpoint";
            case ".pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        }
        return "data";
    }

    public static String toExtension(String value) {
        switch (value) {
            case "application/epub+zip":
                return ".epub";
            case "application/fractals":
                return ".fif";
            case "application/futuresplash":
                return ".spl";
            case "application/hta":
                return ".hta";
            case "application/mac-binhex40":
                return ".hqx";
            case "application/ms-vsi":
                return ".vsi";
            case "application/msaccess":
                return ".accdb";
            case "application/msaccess.addin":
                return ".accda";
            case "application/msaccess.cab":
                return ".accdc";
            case "application/msaccess.exec":
                return ".accde";
            case "application/msaccess.ftemplate":
                return ".accft";
            case "application/msaccess.runtime":
                return ".accdr";
            case "application/msaccess.template":
                return ".accdt";
            case "application/msaccess.webapplication":
                return ".accdw";
            case "application/msonenote":
                return ".one";
            case "application/msword":
                return ".doc";
            case "application/opensearchdescription+xml":
                return ".osdx";
            case "application/pdf":
                return ".pdf";
            case "application/pkcs10":
                return ".p10";
            case "application/pkcs7-mime":
                return ".p7c";
            case "application/pkcs7-signature":
                return ".p7s";
            case "application/pkix-cert":
                return ".cer";
            case "application/pkix-crl":
                return ".crl";
            case "application/postscript":
                return ".ps";
            case "application/vnd.ms-excel":
                return ".xls";
            case "application/vnd.ms-excel.12":
                return ".xlsx";
            case "application/vnd.ms-excel.addin.macroEnabled.12":
                return ".xlam";
            case "application/vnd.ms-excel.sheet.binary.macroEnabled.12":
                return ".xlsb";
            case "application/vnd.ms-excel.sheet.macroEnabled.12":
                return ".xlsm";
            case "application/vnd.ms-excel.template.macroEnabled.12":
                return ".xltm";
            case "application/vnd.ms-officetheme":
                return ".thmx";
            case "application/vnd.ms-pki.certstore":
                return ".sst";
            case "application/vnd.ms-pki.pko":
                return ".pko";
            case "application/vnd.ms-pki.seccat":
                return ".cat";
            case "application/vnd.ms-powerpoint":
                return ".ppt";
            case "application/vnd.ms-powerpoint.12":
                return ".pptx";
            case "application/vnd.ms-powerpoint.addin.macroEnabled.12":
                return ".ppam";
            case "application/vnd.ms-powerpoint.presentation.macroEnabled.12":
                return ".pptm";
            case "application/vnd.ms-powerpoint.slide.macroEnabled.12":
                return ".sldm";
            case "application/vnd.ms-powerpoint.slideshow.macroEnabled.12":
                return ".ppsm";
            case "application/vnd.ms-powerpoint.template.macroEnabled.12":
                return ".potm";
            case "application/vnd.ms-publisher":
                return ".pub";
            case "application/vnd.ms-visio.viewer":
                return ".vsd";
            case "application/vnd.ms-word.document.12":
                return ".docx";
            case "application/vnd.ms-word.document.macroEnabled.12":
                return ".docm";
            case "application/vnd.ms-word.template.12":
                return ".dotx";
            case "application/vnd.ms-word.template.macroEnabled.12":
                return ".dotm";
            case "application/vnd.ms-wpl":
                return ".wpl";
            case "application/vnd.ms-xpsdocument":
                return ".xps";
            case "application/vnd.oasis.opendocument.presentation":
                return ".odp";
            case "application/vnd.oasis.opendocument.spreadsheet":
                return ".ods";
            case "application/vnd.oasis.opendocument.text":
                return ".odt";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                return ".pptx";
            case "application/vnd.openxmlformats-officedocument.presentationml.slide":
                return ".sldx";
            case "application/vnd.openxmlformats-officedocument.presentationml.slideshow":
                return ".ppsx";
            case "application/vnd.openxmlformats-officedocument.presentationml.template":
                return ".potx";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return ".xlsx";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.template":
                return ".xltx";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return ".docx";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.template":
                return ".dotx";
            case "application/windows-appcontent+xml":
                return ".appcontent-ms";
            case "application/x-compress":
                return ".z";
            case "application/x-compressed":
                return ".solitairetheme8";
            case "application/x-dtcp1":
                return ".dtcp-ip";
            case "application/x-gzip":
                return ".gz";
            case "application/x-itunes-itls":
                return ".itls";
            case "application/x-itunes-itms":
                return ".itms";
            case "application/x-itunes-itpc":
                return ".itpc";
            case "application/x-jtx+xps":
                return ".jtx";
            case "application/x-latex":
                return ".latex";
            case "application/x-mix-transfer":
                return ".nix";
            case "application/x-mplayer2":
                return ".asx";
            case "application/x-ms-application":
                return ".application";
            case "application/x-ms-vsto":
                return ".vsto";
            case "application/x-ms-wmd":
                return ".wmd";
            case "application/x-ms-wmz":
                return ".wmz";
            case "application/x-ms-xbap":
                return ".xbap";
            case "application/x-mswebsite":
                return ".website";
            case "application/x-pkcs12":
                return ".p12";
            case "application/x-pkcs7-certificates":
                return ".p7b";
            case "application/x-pkcs7-certreqresp":
                return ".p7r";
            case "application/x-podcast":
                return ".pcast";
            case "application/x-shockwave-flash":
                return ".swf";
            case "application/x-stuffit":
                return ".sit";
            case "application/x-tar":
                return ".tar";
            case "application/x-troff-man":
                return ".man";
            case "application/x-wmplayer":
                return ".asx";
            case "application/x-x509-ca-cert":
                return ".cer";
            case "application/x-zip-compressed":
                return ".zip";
            case "application/xaml+xml":
                return ".xaml";
            case "application/xhtml+xml":
                return ".xht";
            case "application/xml":
                return ".xml";
            case "application/zip":
                return ".zip";
            case "audio/3gpp":
                return ".3gp";
            case "audio/3gpp2":
                return ".3g2";
            case "audio/aac":
                return ".aac";
            case "audio/aiff":
                return ".aiff";
            case "audio/amr":
                return ".amr";
            case "audio/basic":
                return ".au";
            case "audio/ec3":
                return ".ec3";
            case "audio/l16":
                return ".lpcm";
            case "audio/mid":
                return ".mid";
            case "audio/midi":
                return ".mid";
            case "audio/mp3":
                return ".mp3";
            case "audio/mp4":
                return ".m4a";
            case "audio/MP4A-LATM":
                return ".m4a";
            case "audio/mpeg":
                return ".mp3";
            case "audio/mpegurl":
                return ".m3u";
            case "audio/mpg":
                return ".mp3";
            case "audio/vnd.dlna.adts":
                return ".adts";
            case "audio/vnd.dolby.dd-raw":
                return ".ac3";
            case "audio/wav":
                return ".wav";
            case "audio/x-aiff":
                return ".aiff";
            case "audio/x-flac":
                return ".flac";
            case "audio/x-m4a":
                return ".m4a";
            case "audio/x-m4r":
                return ".m4r";
            case "audio/x-matroska":
                return ".mka";
            case "audio/x-mid":
                return ".mid";
            case "audio/x-midi":
                return ".mid";
            case "audio/x-mp3":
                return ".mp3";
            case "audio/x-mpeg":
                return ".mp3";
            case "audio/x-mpegurl":
                return ".m3u";
            case "audio/x-mpg":
                return ".mp3";
            case "audio/x-ms-wax":
                return ".wax";
            case "audio/x-ms-wma":
                return ".wma";
            case "audio/x-wav":
                return ".wav";
            case "image/bmp":
                return ".dib";
            case "image/gif":
                return ".gif";
            case "image/jpeg":
                return ".jpg";
            case "image/jps":
                return ".jps";
            case "image/mpo":
                return ".mpo";
            case "image/pjpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/pns":
                return ".pns";
        }
        return "data";
    }

    public static Uri saveBitmapToGallery(Context context, String fileName, Bitmap bitmap) {
        File mPicDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Lale");
        OutputStream out = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream(1920 * 1920);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            long size = stream.size();
            stream.close();
            mPicDir.mkdirs();
            File filePicture = new File(mPicDir, fileName + ".jpg");
            if (filePicture.exists()) {
                filePicture.delete();
            }

            String mPicPath = filePicture.getAbsolutePath();
            ContentValues values = new ContentValues();
            ContentResolver resolver = context.getContentResolver();
            values.put(MediaStore.Images.ImageColumns.DATA, mPicPath);
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, fileName + ".jpg");
            values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpg");
            //將圖片的拍攝時間設置為當前的時間
            long current = System.currentTimeMillis();
            values.put(MediaStore.Images.ImageColumns.DATE_ADDED, current);
            values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, current);
            values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, current);
            values.put(MediaStore.Images.ImageColumns.SIZE, size);
            values.put(MediaStore.Images.ImageColumns.WIDTH, bitmap.getWidth());
            values.put(MediaStore.Images.ImageColumns.HEIGHT, bitmap.getHeight());
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                out = resolver.openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                return uri;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    // Sticker
    public static void saveCustomStickerNames(Context context, ArrayList<String> iconFileNames) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iconFileNames.size(); i++) {
            sb.append(iconFileNames.get(i)).append(",");
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(CommonUtils.PREF_STICKER_CUSTOM, sb.toString()).commit();
    }

    public static void checkStickerCache(Context context, ArrayList<String> iconFileNames) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String userID = pref.getString(CommonUtils.PREF_USER_ID, "");
        for (int i = 0; i < iconFileNames.size(); i++) {
            String fileName = iconFileNames.get(i);
            if (getStickerCache(context, fileName) == null) {
                saveStickerCache(context, userID, fileName);
            }
        }
    }

    public static void saveStickerCache(Context context, String userID, String fileName) {
        String sURL = "/stickers/" + userID + "/" + fileName + "/download";
        AsynNetUtils.GETSticker(context, sURL, fileName, new AsynNetUtils.BitmapCallback() {
            @Override
            public void onResponse(Bitmap response) {
                Log.d("TEST", "saveStickerCache fileName = " + fileName);
            }
        });
    }

    public static String getStickerUrl(Context context, String fileName) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
        String userID = pref.getString(CommonUtils.PREF_USER_ID, "");
        return String.format("%s/stickers/%s/%s/download", https, userID, fileName);
    }


    public static void saveProductStickerId(Context context, String productId, boolean bAdd) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String list = pref.getString(CommonUtils.PREF_STICKER_DOWNLOAD_ID, "");
        ArrayList<String> paths = new ArrayList<>();
        if (!list.isEmpty()) {
            String[] ids = list.split(",");
            paths = new ArrayList<>(Arrays.asList(ids));
        }

        int idx = paths.indexOf(productId);
        if (bAdd) {
            if (idx == -1) {
                paths.add(productId);
            }
        } else {
            if (idx != -1) {
                paths.remove(idx);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.size(); i++) {
            sb.append(paths.get(i)).append(",");
        }
        pref.edit().putString(CommonUtils.PREF_STICKER_DOWNLOAD_ID, sb.toString()).commit();
    }

    public static String getDownloadStickerFolder(Context context, int productId, boolean withSubFolder) {
        String filePath = String.format("%s/laleSticker%d",
                com.flowring.laleents.tools.FileUtils.getApplicationFolder(context, DefinedUtils.FOLDER_STICKER),
                productId);
        if (withSubFolder) {
            filePath = filePath + String.format("/%d/", productId);
        }
        return filePath;
    }

    public static void deleteAllStickerFolder(File dir) {
        if (!dir.exists()) {
            return;
        }

        if (dir.isDirectory()) {
            for (File child : dir.listFiles()) {
                deleteAllStickerFolder(child);
            }
        }
        dir.delete();
    }

    public static void unzipSticker(Context context, File zipFile, File targetDirectory, String productID) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                String canonicalPath = file.getCanonicalPath();
                if (!canonicalPath.startsWith(targetDirectory.getAbsolutePath())) {
                    // SecurityException
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            file.getAbsolutePath());
                }
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);

                    if (ze.getName().contains("preview")) {
                        CommonUtils.addStickerMapToSP(context, productID, file.getPath());
                    }
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }
    }

    public static boolean limitFileSize(File file){
        long fileSize = file.length();
        long kiloBytes = fileSize/1024;
        long kilobytes = 52428800L;
        //限制檔案大小50 MB，如果檔案大於50 MB
        if(kiloBytes > kilobytes){
            return true;
        }
        return false;
    }
}
