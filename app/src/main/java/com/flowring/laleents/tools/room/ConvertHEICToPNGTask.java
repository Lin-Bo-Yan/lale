package com.flowring.laleents.tools.room;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;

public class ConvertHEICToPNGTask implements Callable<File> {
    private File input;
    private Context context;

    public ConvertHEICToPNGTask(File input, Context context) {
        this.input = input;
        this.context = context;
    }

    @Override
    public File call() throws Exception {
        String filrName = "";
        if (input.getName().endsWith(".HEIC")) {
            //處理檔案名稱
            String name = input.getName().substring(0,input.getName().length()-5);
            filrName = String.format("HEIC_%s.png", name);
        } else if(input.getName().endsWith(".heic")){
            String name = input.getName().substring(0,input.getName().length()-5);
            filrName = String.format("heic_%s.png", name);
        }
        // 將輸入文件轉換為 Uri
        Uri heicUri = Uri.fromFile(input);
        // 建立輸出文件
        File output = new File(input.getParentFile(), filrName);
        // 檢查檔案大小，如果大於1MB，就需要壓縮
        long fileSizeInMB = input.length() / (1024 * 1024);
        int quality = fileSizeInMB > 1 ? 50 : 100;  // 如果大於1MB，壓縮率設為50，否則100
        // 開啟 HEIC 檔案並將其解碼成 Bitmap
        try (ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(heicUri, "r")) {
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd);
                // 壓縮 Bitmap 為 PNG 格式並將其保存在輸出文件中
                try (FileOutputStream out = new FileOutputStream(output)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
                }
            }
        }
        // 回傳轉換後的 PNG 文件
        return output;
    }
}
