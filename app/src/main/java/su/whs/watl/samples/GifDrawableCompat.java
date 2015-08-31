package su.whs.watl.samples;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

import java.io.InputStream;

import su.whs.watl.samples.utils.GifDecoder;

/**
 * Created by igor n. boulliev on 01.09.15.
 */
public class GifDrawableCompat extends Drawable implements Animatable {
    private GifDecoder mDecoder;
    private Bitmap mFrame;
    private boolean mStarted = false;
    private Paint mPaint = new Paint();
    private Rect mSrcRect = new Rect();

    private Runnable updateRunable = new Runnable() {
        @Override
        public void run() {
            nextFrame();
            if (mStarted)
                scheduleSelf(updateRunable, SystemClock.uptimeMillis()+mDecoder.getNextDelay());
        }
    };

    public GifDrawableCompat(InputStream inputStream) {
        mDecoder = new GifDecoder();
        mDecoder.read(inputStream,0);
        mDecoder.advance();
        mFrame = mDecoder.getNextFrame();
        mSrcRect.set(0,0,mFrame.getWidth(),mFrame.getHeight());
    }

    @Override
    public int getIntrinsicWidth() { return mSrcRect.width(); }

    @Override
    public int getIntrinsicHeight() { return mSrcRect.height(); }

    private void nextFrame() {
        mDecoder.advance();
        mFrame = mDecoder.getNextFrame();
        invalidateSelf();
    }

    @Override
    public void start() {
        mStarted = true;
        scheduleSelf(updateRunable,SystemClock.uptimeMillis()+mDecoder.getNextDelay());
    }

    @Override
    public void stop() {
        unscheduleSelf(updateRunable);
        mStarted = false;
    }

    @Override
    public boolean isRunning() {
        return mStarted;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(mFrame,mSrcRect,getBounds(),mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
