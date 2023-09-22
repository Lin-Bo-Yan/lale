package com.flowring.laleents.ui.model.FileReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flowring.laleents.tools.StringUtils;

import java.util.List;

public class WeterMarkBgView extends Drawable {
    private Paint paint = new Paint();
    private Paint bitmapPaint = new Paint();
    private List<String> labels;
    private Context context;
    private int degress;      //角度
    private int fontSize;     // 字體大小，單位sp
    private int imageOpacity; // 圖片透明度
    private int imageScale;    // 圖片縮放大小
    private String textColor;
    private int textOpacity;  //文字透明度
    private String textFont;  // 文字字體
    private Bitmap bitmap;

    public WeterMarkBgView(Context context, List<String> labels, int degress, int fontSize, String textColor, int textOpacity, int imageOpacity, int imageScale, String textFont, Bitmap bitmap) {
        this.labels = labels;
        this.context = context;
        this.degress = degress;
        this.fontSize = fontSize;
        this.textColor = textColor;
        this.textOpacity = textOpacity;
        this.imageOpacity = imageOpacity;
        this.imageScale = imageScale;
        this.textFont = textFont;
        this.bitmap = bitmap;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // 計算畫布的寬度和高度
        int canvasWidth = getBounds().width();
        int canvasHeight = getBounds().height();

        //畫布背景色
        //canvas.drawColor(Color.parseColor("#40F3F5F9"));
        //水印文字顏色
        paint.setColor(Color.parseColor(textColor));
        //設置抗鋸齒
        paint.setAntiAlias(true);
        // setTextSize方法設置水印文字的大小，使用 sp2px 方法將傳入的字體大小轉換為像素值。
        paint.setTextSize(sp2px(context,fontSize));

        Typeface customTypeface = textFontConversion(context,textFont);
        // 設置字體
        paint.setTypeface(customTypeface);
        // 設置透明度為半透明
        paint.setAlpha(textOpacity);

        // 設置圖片
        if(bitmap != null){
            float scale = convertToFloatPercentage(imageScale);
            // 設定圖片縮放比例
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            // 使用Matrix物件對Bitmap進行縮放
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            // 設置圖片透明度
            bitmapPaint.setAlpha(imageOpacity);

            // 計算圖片的左上角位置，使它們都位於畫布中心
            int imageLeft = (canvasWidth - bitmap.getWidth()) / 2;
            int imageTop = (canvasHeight - bitmap.getHeight()) / 2;
            canvas.drawBitmap(bitmap, imageLeft, imageTop, bitmapPaint);
        }

        // 保存當前的繪製狀態
        canvas.save();

        // 獲取水印文字的寬度和高度
        float textWidth = paint.measureText(labels.get(0));
        float textHeight = paint.getTextSize();
        // 計算文字的左上角位置，使它們都位於畫布中心
        int textLeft = (canvasWidth - (int)textWidth) / 2;
        int textTop = (canvasHeight + (int)textHeight) / 2;

        // rotate 方法將整個畫布旋轉指定的角度（degress），以下改成旋轉文字角度
        canvas.rotate(degress, textLeft + textWidth / 2, textTop - textHeight / 2);

        int spacing = 0; // 垂直間距，drawText(要繪製的文本內容,x軸起始位置畫布左上角,y軸起始位置畫布左上角,Paint對象設置文本的顏色)
        for (String label : labels) {
            canvas.drawText(label, textLeft, textTop  + spacing, paint);
            spacing += textHeight; // 將行間距增加到下一行文字的高度
        }

        // 使用 canvas.restore() 恢復到 canvas.save() 之前的繪製狀態
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        //表示不確定畫筆的不透明度
        return PixelFormat.UNKNOWN;
    }

    private static int sp2px(Context context,float spValue){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private static float convertToFloatPercentage(int percent) {
        if (percent > 100) {
            StringUtils.HaoLog("百分比必須低於100");
            return 1.0f;
        } else if(percent < 0){
            StringUtils.HaoLog("百分比必須高於0");
            return 0.1f;
        }
        return (float) percent / 100.0f;
    }

    private static Typeface textFontConversion(Context context, String textFont){
        Typeface customTypeface;
        switch (textFont){
            case "MingLiU":
                customTypeface = Typeface.createFromAsset(context.getAssets(), "MingLiU.ttf");
                return customTypeface;
            case "PMingLiU":
                customTypeface = Typeface.createFromAsset(context.getAssets(), "PMingLiU.ttf");
                return customTypeface;
            case "DFKai-SB":
                customTypeface = Typeface.createFromAsset(context.getAssets(), "DFKai_SB.ttf");
                return customTypeface;
            case "Roboto":
                customTypeface = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
                return customTypeface;
            case "Montserrat":
                customTypeface = Typeface.createFromAsset(context.getAssets(), "Montserrat-Light.ttf");
                return customTypeface;
            default:
                customTypeface = Typeface.createFromAsset(context.getAssets(), "NotoSerifTC-Light.otf");
                return customTypeface;
        }
    }
}
