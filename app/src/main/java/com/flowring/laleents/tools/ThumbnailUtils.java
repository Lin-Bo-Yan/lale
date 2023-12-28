package com.flowring.laleents.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ThumbnailUtils {

    public static String getImageThumbnail(){
        return "/9j/4AAQSkZJRgABAQEAYABgAAD/4QAiRXhpZgAATU0AKgAAAAgAAQESAAMAAAABAAEAAAAAAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAA9AD0DASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9/M5pC6r1YfnTN+B838XFfFHwl+I3xo/al/aA+MGg6N8WP+EG0v4d+IJbC1T/AIRix1P7RE1zdRxrl1Rl2Lbjkli27k5HKvbcD7aVs0BgRnIx6182f8M+fHtE/eftIde3/Cv9O+b/AMfoi/Z5/aAbgftJ53cK3/CvdN6/Tf8AWkm3qhOVj6T3D1FLnNfL/wCzT+0v4ztPFX/CO/EBf7Qs4NT1XQ7bxRmCH+2rq0urhP8Aj0gTMOY7aU8naPLxklhn6YtbqG7topI2ytwokU4PzA8/qKav1GWM0UijaKWmBHs3giviv/gnLEp/at/ahXYrAeLRwSeP9M1P3r7WPyn8K+Kv+CcLY/ax/ai/7G0f+lmp1PNqkzOpJqLaPpDxX+0b4A8AXv2fWPHHhLR7gu8bR3ur28Dh0OGXDsOQTgjtmsmP9sH4UXkvlp8Tvh7LM3RB4itMn/yJ6V+Mn7c/xi1zxb+1N4+0uaWbUI9F8VavbWlvFbIXVBduuBtAZsBBycnj61k6Z8NfFXwXe18Xa34d0/W9LhdopNOuYrxZAXVkVpBGYmUBsEESDqvXOK/TsJwDUrYeNenLfW39I/OcVxt7DE/V3v8A15n3p4z8W/GL4tWPi7wF4L8I/DLx94Eutd1LVbKWPxFbSXVxFLqct15jGHUI2UCSXHKrjhTznPS/sffH74yfCj9pvwb8GviBo+h6LY61bT3ttaxiOaa0s47SfyY45YpXXYrWpX96XkIB3Mcg14h/wRG+J+s+Lv2vPFFhKLex0D/hHL68ttLhiIFkzX9q20M5aUgb2HzyMeeSa+jvi7z/AMFpvhHt+6PCdwD9fK1avhc6wMsJiPYy3R93l+KdelGo+qPtSP7vrTqZG1PrzTvIVmDDcPmHT8a+Lf8AgnGv/GV/7URPy58Wj/0s1OvtKJNiYYDd1AFfF/8AwTejLftX/tSZ3Sf8VaMYP3f9M1P/AD+FTrzJrVE1LODXU+Y/jF+zPo/wU/aO8feO/FiwXEeueIr+/wBPR0W6jCy3Eu/enl5HE6YAPUd8V4l8UP2kpPiA7WtpHJ/YzSrJ/pCkNIAmMMA5BG/kDH8IPav2n8WfBvwj48jI1jwr4f1gbizfbdPhnGSQSfnU9SAfwFc2P2OPhLGGRfhh8P8A5RwB4ftB/wC06/Vsn8QqeEoqnKm21/Xc/Fc28NMZisZ9bjVt9/8AkfDv/BGf4Z/Yvj9qniy1j2Wmq+GbiEDco+f7Xa7sKBkDdG2Mnpivafi98n/BaT4QqvfwnOWB9fK1avqLwV8J/C/wziU+H/DWhaDGqGL/AIl9hFbYUtuK/IBwWAJHc818ufF5Sf8AgtJ8I2zwfClxj/v1q2K+GzzMlj8VLEtWu9j9SyPB1cNQjRqO7ikr+iR9pxin02EELzTq8Q9tjQnHvXy1Y/sB+Nvh/wDFTxt4m8BfGI+D38earLqeo2//AAikGoby00ssa7ppjjZ50gyoXdu5HAA+pQmDQV96Ubx2EfOafsy/HrLD/hpLg+nw+04f+z0H9mP48Kd3/DSPX/qn+n//ABdfRhX3oddw9KTXULs+c/8AhmX48Z/5OQOO4PgDTyD+HmVX+Hv7CnijTv2nPDfxQ8afE/8A4TbWPDdrNZQJ/wAI5Fpu6B4p0CZil2ja1w7ZKEnpnpj6VC4XFLihX3YDRw9OooqgP//Z";
    }

    public static Bitmap getChatRoomThumbnail(File file){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        //圖片的原始高度和寬度
        int height = options.outHeight;
        int width = options.outWidth;

        // 新圖的寬要小於等於這個值
        final int MAX_WIDTH = 500;
        // 求出要縮小的 scale 值，必需是2的次方，ex: 1,2,4,8,16...
        int scale = 1;
        while(width > MAX_WIDTH){
            width /= 2;
            scale *= 2;
        }
        while(height > MAX_WIDTH){
            height /= 2;
            scale *= 2;
        }

//        float reqWidth = 500; // 目標寬度
//        float reqHeight = 500; // 目標高度
//        float widthRatio = (float)width/reqWidth;
//        float heightRatio = (float)height/reqHeight;
//        float maxRatio = Math.max(widthRatio, heightRatio);
//        reqWidth = width / maxRatio;
//        reqHeight = height / maxRatio;
//        // 計算樣本量
//        options.inSampleSize = calculateInSampleSize(options, (int) reqWidth, (int)reqHeight);
        // 使用 inSampleSize 集解碼位圖
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    // Bitmap 轉 Base64
    public static String getBase64FromBitmap(Bitmap bitmap) {
        String base64 = "";
        ByteArrayOutputStream baos = null;
        try{
            if(bitmap != null){
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                baos.flush();
                baos.close();
                byte[] byteArray = baos.toByteArray();
                base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                if(baos != null){
                    baos.flush();
                    baos.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return base64;
    }

    // 檔案路徑 轉 Base64
    public static String getBase64FromPath(String path) {
        String base64 = "";
        try {
            File file = new File(path);
            byte[] buffer = new byte[(int) file.length() + 100];
            @SuppressWarnings("resource")
            int length = new FileInputStream(file).read(buffer);
            base64 = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

    public static String resizeAndConvertToBase64(String imagePath, int thumbnailSize) {
        // 加載原始圖像
        Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);
        // 計算縮放比例
        float scaleFactor = calculateScaleFactor(originalBitmap.getWidth(), originalBitmap.getHeight(), thumbnailSize);
        // 計算縮放後的尺寸
        int targetWidth = Math.round(originalBitmap.getWidth() * scaleFactor);
        int targetHeight = Math.round(originalBitmap.getHeight() * scaleFactor);
        // 縮放圖像為縮略圖
        Bitmap thumbnailBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, false);
        // 釋放原始圖像資源
        originalBitmap.recycle();
        // 將縮略圖轉換為Base64字符串
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    // 計算縮放比例
    private static float calculateScaleFactor(int originalWidth, int originalHeight, int targetSize) {
        float scale;
        if (originalWidth > originalHeight) {
            scale = (float) targetSize / originalWidth;
        } else {
            scale = (float) targetSize / originalHeight;
        }
        return scale;
    }
}
