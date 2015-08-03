package com.hankcheng.simpledrawing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

public class BackgroundView extends View {

    private Canvas backgroundCanvas;
    private Bitmap canvasBitmap;
    private Paint canvasPaint;

    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        backgroundCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }

    public void startNew() {
        backgroundCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setBackgroundCanvas(Bitmap bitmap) {
        backgroundCanvas.drawBitmap(bitmap, 0, 0, canvasPaint);
    }

}
