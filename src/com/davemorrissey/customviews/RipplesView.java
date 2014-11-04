package com.davemorrissey.customviews;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RipplesView extends View {

    private long duration = 3000;

    private Paint paint;

    private List<Tap> taps = new ArrayList<Tap>();

    private class Tap {
        private long timestamp;
        private PointF location;
        private int color;
    }

    public RipplesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(10f);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            float h = ((System.currentTimeMillis() % 3000)/3000f) * 360;
            Tap tap = new Tap();
            tap.timestamp = System.currentTimeMillis();
            tap.location = new PointF(event.getX(), event.getY());
            tap.color = Color.HSVToColor(new float[] { h, 1f, 1f });
            taps.add(tap);
            invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Iterator<Tap> it = taps.iterator();
        while (it.hasNext()) {
            Tap tap = it.next();
            long age = System.currentTimeMillis() - tap.timestamp;
            if (age > duration) {
                it.remove();
            } else {
                int radius = (int) (500 * (age / (double)duration));
                int alpha = (int)(255 - (255 * (age / (double)duration)));
                paint.setColor(tap.color);
                paint.setAlpha(alpha);
                canvas.drawOval(new RectF(tap.location.x - radius, tap.location.y - radius, tap.location.x + radius, tap.location.y + radius), paint);
                invalidate();
            }
        }

    }

}
