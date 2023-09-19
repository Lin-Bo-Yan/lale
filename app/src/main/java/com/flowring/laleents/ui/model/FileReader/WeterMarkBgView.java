package com.flowring.laleents.ui.model.FileReader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class WeterMarkBgView extends Drawable {
    private Paint paint = new Paint();
    private List<String> labels;
    private Context context;
    private int degress;   //角度
    private int fontSize;  // 字體大小，單位sp

    public WeterMarkBgView(Context context, List<String> labels, int degress, int fontSize) {
        this.labels = labels;
        this.context = context;
        this.degress = degress;
        this.fontSize = fontSize;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        //getBounds().right 和 getBounds().bottom 獲取繪製區域的寬度和高度。
        int width = getBounds().right;
        int height = getBounds().top + 200;

        //畫布背景色
        canvas.drawColor(Color.parseColor("#40F3F5F9"));
        //水印文字顏色
        paint.setColor(Color.parseColor("#50AEAEAE"));
        //設置抗鋸齒
        paint.setAntiAlias(true);
        // setTextSize方法設置水印文字的大小，使用 sp2px 方法將傳入的字體大小轉換為像素值。
        paint.setTextSize(sp2px(context,fontSize));
        // 保存當前的繪製狀態
        canvas.save();
        // rotate 方法將整個畫布旋轉指定的角度（degress）。
        canvas.rotate(degress);
        // measureText 方法測量第一個水印文字（labels.get(0)）的寬度，將結果存儲在 textWidth 變量中
        float textWidth = paint.measureText(labels.get(0));

        int spacing = 0; // 垂直間距，drawText(要繪製的文本內容,x軸起始位置畫布左上角,y軸起始位置畫布左上角,Paint對象設置文本的顏色)
        for (String label : labels) {
            canvas.drawText(label, 100, height+ spacing, paint);
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
