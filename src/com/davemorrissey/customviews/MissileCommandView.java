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

import java.util.*;

public class MissileCommandView extends View {

    private Paint paint;

    private Set<Explosion> explosions = new HashSet<Explosion>();
    private Set<Missile> missiles = new HashSet<Missile>();

    private class Explosion {
        private long timestamp;
        private PointF location;
    }

    private class Missile {
        private long timestamp;
        private float x;
    }

    private final Random random = new Random();
    private static final int MESSAGE_DROP_MISSILE = 1;

    private Bitmap rocket;

    private Handler handler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == MESSAGE_DROP_MISSILE) {
                float x = random.nextFloat() * getWidth();
                Missile missile = new Missile();
                missile.timestamp = System.currentTimeMillis();
                missile.x = x;
                missiles.add(missile);
                handler.sendEmptyMessageDelayed(MESSAGE_DROP_MISSILE, random.nextInt(1500));
                invalidate();
            }
            return false;
        }
    });

    public MissileCommandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(3f);
        handler.sendEmptyMessageDelayed(MESSAGE_DROP_MISSILE, 1500);
        rocket = BitmapFactory.decodeResource(this.getResources(), R.drawable.rocket);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Explosion explosion = new Explosion();
            explosion.timestamp = System.currentTimeMillis();
            explosion.location = new PointF(event.getX(), event.getY());
            explosions.add(explosion);
            invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setColor(Color.YELLOW);
        paint.setStyle(Style.FILL);

        List<Explosion> newExplosions = new ArrayList<Explosion>();
        Iterator<Explosion> explosionIterator = this.explosions.iterator();
        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();
            long explosionElapsed = System.currentTimeMillis() - explosion.timestamp;
            if (explosionElapsed > 1500) {
                explosionIterator.remove();
            } else {
                int radius = (int) (100 * (explosionElapsed / (double)1500));
                int alpha = (int)(255 - (255 * (explosionElapsed / (double)1500)));
                paint.setAlpha(alpha);
                canvas.drawOval(new RectF(explosion.location.x - radius, explosion.location.y - radius, explosion.location.x + radius, explosion.location.y + radius), paint);

                Iterator<Missile> missileIterator = missiles.iterator();
                while (missileIterator.hasNext()) {
                    Missile missile = missileIterator.next();
                    long missileElapsed = System.currentTimeMillis() - missile.timestamp;
                    float missileY = (missileElapsed / 10000f) * getHeight();

                    double dist = Math.sqrt(Math.pow(explosion.location.x - missile.x, 2) + Math.pow(explosion.location.y - missileY, 2));
                    if (dist < radius) {
                        missileIterator.remove();
                        Explosion missileExplosion = new Explosion();
                        missileExplosion.timestamp = System.currentTimeMillis();
                        missileExplosion.location = new PointF(missile.x, missileY);
                        newExplosions.add(missileExplosion);
                    }

                }

                invalidate();
            }
        }
        this.explosions.addAll(newExplosions);

        paint.setAlpha(255);
        Iterator<Missile> missileIterator = missiles.iterator();
        while (missileIterator.hasNext()) {
            Missile missile = missileIterator.next();
            long missileElapsed = System.currentTimeMillis() - missile.timestamp;
            if (missileElapsed > 10000) {
                missileIterator.remove();
            } else {
                float missileY = (missileElapsed / 10000f) * getHeight();
                canvas.drawBitmap(rocket, missile.x - (rocket.getWidth()/2), missileY - (rocket.getHeight()/2), paint);
                invalidate();
            }
        }

    }

}
