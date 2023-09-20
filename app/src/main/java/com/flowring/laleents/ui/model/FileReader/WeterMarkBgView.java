package com.flowring.laleents.ui.model.FileReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flowring.laleents.R;
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
    private String textColor;
    private int textOpacity;  //文字透明度
    private Typeface customTypeface;

    public WeterMarkBgView(Context context, List<String> labels, int degress, int fontSize, String textColor, int textOpacity, int imageOpacity) {
        this.labels = labels;
        this.context = context;
        this.degress = degress;
        this.fontSize = fontSize;
        this.textColor = textColor;
        this.textOpacity = textOpacity;
        this.imageOpacity = imageOpacity;
        customTypeface = Typeface.createFromAsset(context.getAssets(), "SentyDew.ttf");
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // 計算畫布的寬度和高度
        int canvasWidth = getBounds().width();
        int canvasHeight = getBounds().height();

        //畫布背景色
        canvas.drawColor(Color.parseColor("#40F3F5F9"));
        //水印文字顏色
        paint.setColor(Color.parseColor(textColor));
        //設置抗鋸齒
        paint.setAntiAlias(true);
        // setTextSize方法設置水印文字的大小，使用 sp2px 方法將傳入的字體大小轉換為像素值。
        paint.setTextSize(sp2px(context,fontSize));
        // 設置字體
        paint.setTypeface(customTypeface);
        // 設置透明度為半透明
        paint.setAlpha(textOpacity);
        // 設置圖片透明度
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_group);
        bitmapPaint.setAlpha(imageOpacity);

        // 計算圖片的左上角位置，使它們都位於畫布中心
        int imageLeft = (canvasWidth - bitmap.getWidth()) / 2;
        int imageTop = (canvasHeight - bitmap.getHeight()) / 2;
        canvas.drawBitmap(bitmap, imageLeft, imageTop, bitmapPaint);

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
            spacing = spacing + 100;
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

    public static int sp2px(Context context,float spValue){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
