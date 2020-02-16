package su.whs.mediaview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import androidx.appcompat.app.ActionBarActivity;
import androidx.appcompat.view.ActionMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by igor n. boulliev on 14.01.16.
 */

public class MediaView extends View implements View.OnKeyListener {
    private static final String TAG = "MediaView";
    private Drawable mDrawable = null;
    private float mAnimationPhase = 0f;
    private float mActualScaleX = 1f;
    private float mActualScaleY = 1f;
    private Rect mSrc;
    private Rect mLazyRect = new Rect();
    private Rect mInterpolateSourceRect = new Rect();
    private float mSrcRatio = 1f;
    private boolean mRestoredState = false;
    private Drawable.Callback mSavedCallback = null;
    private ValueAnimator mValueAnimator = null;
    private ScaleGestureDetector mScaleDetector;
    private boolean mEnabledScrollX = false;
    private boolean mEnabledScrollY = false;
    private boolean mOnScrolling = false;
    private boolean mOnDodging = false;
    private boolean mIgnoreDodgingAnimation = false;
    private boolean mInvalidateDrawableOnReturn = false;
    private float mMinimumScaleFactor = 0.5f;
    private float mMaximumScaleFactor = 4f;
    private ActionMode.Callback mActionModeCallbacks = null;

    public Drawable getDrawable() {
        return mDrawable;
    }
//
//    public interface MediaInteractionListener {
//        void onSaveMedia();
//        void onCopyMedia();
//        void onShareMedia();
//    }

    public MediaView(Context context) {
        super(context);
        mRestoredState = true;
        mAnimationPhase = 1f;
        mScaleDetector = getScaleDetector(getContext());
    }

    public MediaView(Context context, String url, String mimeType, Rect src) {
        super(context);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MediaView(Context context, Drawable drawable, Rect src) {
        super(context);
        mDrawable = drawable;
        mSavedCallback = mDrawable.getCallback();
        mDrawable.setCallback(this);
        mSrc = src;
        mInterpolateSourceRect.set(mSrc);
        init();
    }

    public void registerActionModeCallbacks(ActionMode.Callback cb) {
        mActionModeCallbacks = cb;
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnKeyListener(this);
        mScaleDetector = getScaleDetector(getContext());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void restore(Drawable drawable, Rect src) {
        mDrawable = drawable;
        mSavedCallback = mDrawable.getCallback();
        mDrawable.setCallback(this);
        mSrc = src;
        if (mSrc.width()==0 || mSrc.height()==0) {
            mSrc.set(drawable.getBounds());
            mSrc.top = - mSrc.height();
            mSrc.bottom = 0;
            int width = mSrc.width();
            mSrc.left = (mLocalVisibleRect.width() >> 1) - (width >> 1);
            mSrc.right = mSrc.left + width;
        }
        mRestoredState = false;
        mAnimationPhase = 1f;
        invalidate();
    }

    public static MediaView show(MediaContainer parent, String url, String mimeType, Rect src) {
        MediaView mv = new MediaView(parent.getContext(),url,mimeType,src);
        return mv;
    }

    public static MediaView show(MediaContainer parent, Drawable drawable, Rect src) {
        MediaView mv = new MediaView(parent.getContext(),drawable,src);
        return mv;
    }

    public static MediaView pending(MediaContainer parent) {
        MediaView mv = new MediaView(parent.getContext());
        return mv;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mRestoredState) {
            mAnimationPhase = 1f;
            initActionMode();
            return;
        }
        mInterpolateSourceRect.set(mSrc);
        mValueAnimator = ValueAnimator.ofFloat(0,1);
        mValueAnimator.setDuration(300);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) (animation.getAnimatedValue())).floatValue();
                setAnimationPhase(value);
            }
        });
        mValueAnimator.addListener(new EndAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mValueAnimator.removeAllUpdateListeners();
                mValueAnimator = null;
                mActualRect.set(mSrc);
            }
        });
        mValueAnimator.start();
        initActionMode();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MediaView hide() {
        mValueAnimator = ValueAnimator.ofFloat(1,0);
        mValueAnimator.setDuration(300);
        mInterpolateSourceRect.set(mSrc); // actually, interpolate from target to source
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) (animation.getAnimatedValue())).floatValue();
                setAnimationPhase(value);
            }
        });
        mValueAnimator.addListener(new EndAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mActionMode != null) mActionMode.finish();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewParent parent = getParent();
                if (parent instanceof MediaContainer) {
                    ((MediaContainer) parent).onMediaViewClosed();
                }
                if (mValueAnimator!=null)
                    mValueAnimator.removeAllUpdateListeners();
            }
        });
        if (mSavedCallback!=null && mDrawable!=null) {
            mDrawable.setCallback(mSavedCallback);
            if (mInvalidateDrawableOnReturn) {
                mDrawable.scheduleSelf(new Runnable() {
                    @Override
                    public void run() {
                        mSavedCallback.invalidateDrawable(mDrawable);
                    }
                }, SystemClock.uptimeMillis()+100);
            }
        }
        mValueAnimator.start();
        return this;
    }

    private Paint mBackgroundPaint = new Paint();
    private Paint mDebugPaint = new Paint();
    {
        mBackgroundPaint.setColor(Color.BLACK);
        mDebugPaint.setStyle(Paint.Style.STROKE);

        mDebugPaint.setStrokeWidth(2f);
    }
    private Rect mLocalVisibleRect = new Rect();
    private Rect mTargetRect = null;
    private Rect mActualRect = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTargetRect == null) {
            getLocalVisibleRect(mLocalVisibleRect);
            if (!mRestoredState) {
                mTargetRect = calculateTargetRect(mSrc);
                calculateLazyRect(mDrawable);
            }
        }
        if (!mOnDodging)
            mBackgroundPaint.setAlpha((int) (255 * mAnimationPhase));
        canvas.drawRect(mLocalVisibleRect, mBackgroundPaint);
        if (mRestoredState)
            return;
        interpolateActualRect();
        int state = canvas.save();
        canvas.translate(mActualRect.left, mActualRect.top);
        canvas.scale(mActualScaleX, mActualScaleY);
        if (mDrawable!=null) {
            mDrawable.draw(canvas);
            if (BuildConfig.DEBUG && mLazyRect.right>mLazyRect.left) {
                mDebugPaint.setColor(Color.MAGENTA);
                canvas.drawRect(mLazyRect,mDebugPaint);
            }
        }
        canvas.restoreToCount(state);
        if (BuildConfig.DEBUG) {
            mDebugPaint.setColor(Color.RED);
            canvas.drawRect(mSrc, mDebugPaint);
            mDebugPaint.setColor(Color.GREEN);
            canvas.drawRect(mTargetRect, mDebugPaint);
            mDebugPaint.setColor(Color.YELLOW);
            canvas.drawRect(mInterpolateSourceRect, mDebugPaint);
            mDebugPaint.setColor(Color.CYAN);
            canvas.drawRect(mActualRect, mDebugPaint);
        }
    }

    private Rect calculateTargetRect(Rect src) {
        getLocalVisibleRect(mLocalVisibleRect);
        Rect result = new Rect();
        int drawableWidth = mDrawable.getIntrinsicWidth();
        int drawableHeigth = mDrawable.getIntrinsicHeight();
//        if (mDrawable instanceof LazyDrawable) {
//            int cW = ((LazyDrawable)mDrawable).getWrappedDrawableWidth();
//            int cH = ((LazyDrawable)mDrawable).getWrappedDrawableHeight();
//            if (cW>drawableWidth && cH >drawableHeigth) {
//                drawableWidth = cW;
//                drawableHeigth = cH;
//            }
//        }
        mSrcRatio = (float)drawableHeigth / drawableWidth;

        if (drawableWidth < mLocalVisibleRect.width() && drawableHeigth < mLocalVisibleRect.height()) {
            int left = mLocalVisibleRect.width() / 2 - drawableWidth / 2;
            int top = mLocalVisibleRect.height() / 2 - drawableHeigth / 2;
            result.set(left,top,left+drawableWidth,top+drawableHeigth);
            return result;
        }

        calcCenter(mLocalVisibleRect.width(), mLocalVisibleRect.height(), drawableWidth, drawableHeigth, true, result);

        return result;
    }

    private void calculateLazyRect(Drawable drawable) {
        mLazyRect = drawable.getBounds();
    }

    private void interpolateActualRect() {
        int srcWidth = mSrc.width();
        int srcHeight = mSrc.height();
        int isrWidth = mInterpolateSourceRect.width();
        int isrHeight = mInterpolateSourceRect.height();
        int tgtWidth = mTargetRect.width();
        int tgtHeight = mTargetRect.height();
        int centerSrcX = mInterpolateSourceRect.left + ((mOnDodging ? isrWidth : srcWidth) >> 1);
        int centerSrcY = mInterpolateSourceRect.top + ((mOnDodging ? isrHeight : srcHeight) >> 1);
        int centerTgtX = mTargetRect.left + (tgtWidth >> 1);
        int centerTgtY = mTargetRect.top + (tgtHeight >> 1);

        int centerActualX = (int) (centerSrcX + (centerTgtX-centerSrcX) * mAnimationPhase);
        int centerActualY = (int) (centerSrcY + (centerTgtY-centerSrcY) * mAnimationPhase);

        int actualWidth = (int) (srcWidth + (tgtWidth - srcWidth) * (mOnDodging ? 1f : mAnimationPhase));
        int actualHeight = (int) (actualWidth * mSrcRatio/* srcHeight + (tgtHeight - srcHeight) * (mOnDodging ? 1f : mAnimationPhase))*/);

        mActualScaleX = (float)actualWidth / srcWidth;
        mActualScaleY = mActualScaleX; //(float)actualHeight / srcHeight;

        mActualRect.set(
                centerActualX - (actualWidth >> 1),
                centerActualY - (actualHeight >> 1),
                centerActualX + (actualWidth >> 1),
                centerActualY + (actualHeight >> 1));
    }

    private void setAnimationPhase(float phase) {
        mAnimationPhase = phase;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroyActionMode();
    }

    private ActionMode mActionMode;

    private void destroyActionMode() {
        if (mActionMode!=null)
            mActionMode.finish();
        mActionMode = null;
    }

    private void initActionMode() {
        if (mActionModeCallbacks!=null) {
           if (getContext() instanceof ActionBarActivity) {
               mActionMode = ((ActionBarActivity)getContext()).startSupportActionMode(mActionModeCallbacks);
           } else {

           }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnDodging) {
            return true;
        }

        boolean onScale = mScaleDetector.onTouchEvent(event);

        if (mEnabledScrollX || mEnabledScrollY)
            onScale = onScale | detectScrolling(event);
        else if (mOnScrolling) {
            // scrolling sequence was transformed into scaling
            mOnScrolling = false;
        }
        return onScale;
    }

    private float mTouchX = 0f;
    private float mTouchY = 0f;

    private boolean detectScrolling(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mOnScrolling) {
                    mOnScrolling = false;
                    if (needAdjustCenter(mTargetRect)) {
                        mInterpolateSourceRect.set(mTargetRect);
                        doAdjustCenter();
                    }
                    return true;
                }
                return false;
            case MotionEvent.ACTION_DOWN:
                mOnScrolling = true;
                mTouchX = event.getX();
                mTouchY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mOnScrolling) {
                    float x = event.getX();
                    float y = event.getY();
                    float deltaX = mEnabledScrollX ? x - mTouchX : 0f;
                    float deltaY = mEnabledScrollY ? y - mTouchY : 0f;
                    mTouchY = y;
                    mTouchX = x;
                    moveRect(deltaX, deltaY, mTargetRect);
                    invalidate();
                    return true;
                }
        }
        return false;
    }

    private void moveRect(float dx, float dy, Rect rect) {
        rect.left += dx;
        rect.right += dx;
        rect.top += dy;
        rect.bottom += dy;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            KeyEvent.DispatcherState state = getKeyDispatcherState();
            if (state != null) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getRepeatCount() == 0) {
                    state.startTracking(event, this);
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP
                        && !event.isCanceled() && state.isTracking(event)) {
                    hide();
                    return true;
                }
            }
        }
        return super.dispatchKeyEventPreIme(event);
    }

    private void calculateScrollingAvailabilality() {
        if (mTargetRect.width()>mLocalVisibleRect.width())
            mEnabledScrollX = true;
        else
            mEnabledScrollX = false;
        if (mTargetRect.height()>mLocalVisibleRect.height())
            mEnabledScrollY = true;
        else
            mEnabledScrollY = false;
    }

    private boolean needZoomIn(Rect targetRect) {
        // if targetRect too small
        int drawableWidth = mDrawable.getIntrinsicWidth();
        float actualScale = (float)targetRect.width() / drawableWidth;
        if (actualScale < mMinimumScaleFactor) {
            mOnDodging = true;
        }
        return mOnDodging;
    }

    private boolean needZoomOut(Rect targetRect) {
        int drawableWidth = mDrawable.getIntrinsicWidth();
        float actualScale = (float)targetRect.width() / drawableWidth;
        if (actualScale > mMaximumScaleFactor) {
            mOnDodging = true;
        }
        return mOnDodging;
    }

    private boolean needAdjustCenter(Rect targetRect) {
        if (targetRect.width()>mLocalVisibleRect.width()) {
            if (targetRect.left>mLocalVisibleRect.left || targetRect.right<mLocalVisibleRect.right)
                mOnDodging = true;
        } else {
            if (targetRect.centerX() != mLocalVisibleRect.centerX())
                mOnDodging = true;
        }
        if (targetRect.height()>mLocalVisibleRect.height()) {
            if (targetRect.top > mLocalVisibleRect.top || targetRect.bottom < mLocalVisibleRect.bottom)
                mOnDodging = true;
        } else {
            if (targetRect.centerY() != mLocalVisibleRect.centerY())
                mOnDodging = true;
        }
        return mOnDodging;
    }

    private void doZoomIn() {
        // value animator: zoom too small targetRect to minimum scale factor;
        // scale target rect
        int centerX = mTargetRect.centerX();
        int centerY = mTargetRect.centerY();
        float scaledWidth2 = (mSrc.width() * mMinimumScaleFactor) / 2;
        float scaledHeight2 = scaledWidth2 * mSrcRatio;
        mTargetRect.set(
                (int)(centerX - scaledWidth2),
                (int)(centerY - scaledHeight2),
                (int)(centerX + scaledWidth2),
                (int)(centerY + scaledHeight2));
        if (needAdjustCenter(mTargetRect)) {
            doAdjustCenter();
        } else {
            startCorrectionAnimation();
        }
    }

    private void doZoomOut() {
        // value animator: zoom too big targetRect to maximums scale factor;
        // scale target rect
        int centerX = mTargetRect.centerX();
        int centerY = mTargetRect.centerY();
        float scaledWidth2 = (mSrc.width() * mMaximumScaleFactor) / 2;
        float scaledHeight2 = scaledWidth2 * mSrcRatio;
        mTargetRect.set(
                (int)(centerX - scaledWidth2),
                (int)(centerY - scaledHeight2),
                (int)(centerX + scaledWidth2),
                (int)(centerY + scaledHeight2));
        if (needAdjustCenter(mTargetRect)) {
            doAdjustCenter();
        } else {
            startCorrectionAnimation();
        }
    }

    private void doAdjustCenter() {
        mOnDodging = true;
        // mActualRect.set(mTargetRect);
        // value animator: shift overscrolled targetrect back to view rect
        float deltaX = 0f;
        float deltaY = 0f;
        if (mTargetRect.width()>mLocalVisibleRect.width()) {
            if (mTargetRect.left>mLocalVisibleRect.left) {
                deltaX = mLocalVisibleRect.left - mTargetRect.left;
            } else if (mTargetRect.right<mLocalVisibleRect.right) {
                deltaX = mLocalVisibleRect.right - mTargetRect.right;
            }
        } else {
            if (mTargetRect.centerX() != mLocalVisibleRect.centerX()) {
                deltaX = mLocalVisibleRect.centerX() - mTargetRect.centerX();
            }
        }
        if (mTargetRect.height()>mLocalVisibleRect.height()) {
            if (mTargetRect.top > mLocalVisibleRect.top) {
                deltaY = mLocalVisibleRect.top - mTargetRect.top;
            } else if (mTargetRect.bottom < mLocalVisibleRect.bottom) {
                deltaY = mLocalVisibleRect.bottom - mTargetRect.bottom;
            }
        } else {
            if (mTargetRect.centerY() != mLocalVisibleRect.centerY()) {
                deltaY = mLocalVisibleRect.centerY() - mTargetRect.centerY();
            }
        }
        if (deltaX == 0 && deltaY == 0) {
            mOnDodging = false;
            return;
        }
        moveRect(deltaX, deltaY, mTargetRect);
        startCorrectionAnimation();
    }

    private int changeFactor() {
        return 100;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void startCorrectionAnimation() {
        calculateScrollingAvailabilality();
        mAnimationPhase = 0f;
        mValueAnimator = ValueAnimator.ofFloat(0,1);
        mValueAnimator.setDuration(BuildConfig.DEBUG ? 5 * changeFactor() : 2 * changeFactor());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!mOnDodging) {
                    mValueAnimator.cancel();
                    return;
                }
                float value = ((Float) (animation.getAnimatedValue())).floatValue();
                setAnimationPhase(value);
            }
        });
        mValueAnimator.addListener(new EndAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e("ANIM","Animation Started");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e("ANIM","Animation Ended");
                mOnDodging = false;
                invalidate();
            }
        });
        mValueAnimator.start();
    }

    private ScaleGestureDetector getScaleDetector(Context context) {
        return new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            private float mCenterX = 0;
            private float mCenterY = 0;
            private float mPivotWidth = 0f;
            private float mPivotHeight = 0f;

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                // check if we scale too small/too big
                // mInterpolateSourceRect.set(mTargetRect);
                mInterpolateSourceRect.set(
                        mTargetRect.left,
                        mTargetRect.top,
                        mTargetRect.right,
                        mTargetRect.bottom
                );
                if (needZoomIn(mTargetRect))
                    doZoomIn();
                else if (needZoomOut(mTargetRect))
                    doZoomOut();
                else if (needAdjustCenter(mTargetRect))
                    doAdjustCenter();
                else calculateScrollingAvailabilality();
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                mEnabledScrollX = false; // always disable scrolling while scaling
                mEnabledScrollY = false; // always disable scrolling while scaling
                mPivotWidth = (float)mTargetRect.width();
                mPivotHeight = mPivotWidth * mSrcRatio;
                mCenterX = mTargetRect.left + (mPivotWidth / 2);
                mCenterY = mTargetRect.top + (mPivotHeight / 2);
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = detector.getScaleFactor();
                float scaledWidth2 = (mPivotWidth * scale) / 2;
                float scaledHeight2 = (mPivotHeight * scale) / 2;
                mTargetRect.set(
                        (int)(mCenterX - scaledWidth2),
                        (int)(mCenterY - scaledHeight2),
                        (int)(mCenterX + scaledWidth2),
                        (int)(mCenterY + scaledHeight2));
                mPivotWidth = mTargetRect.width();
                mPivotHeight = mPivotWidth * mSrcRatio;
                invalidate();
                return true;
            }
        });
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        boolean result = super.verifyDrawable(who) || mDrawable == who;
        if (result)
            mInvalidateDrawableOnReturn = true;
        return result;
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        new Handler().postAtTime(what,when);
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        invalidate();
    }

    public void unregisterActionModeCallbacks() {
        mActionMode = null;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static abstract class EndAnimatorListener implements ValueAnimator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return mEnabledScrollX;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return mEnabledScrollY;
    }

    /**
     * Calculate the bounds of an image to fit inside a view after scaling and keeping the aspect ratio.
     * @param vw container view width
     * @param vh container view height
     * @param iw image width
     * @param ih image height
     * @param neverScaleUp if <code>true</code> then it will scale images down but never up when fiting
     * @param out Rect that is provided to receive the result. If <code>null</code> then a new rect will be created
     * @return Same rect object that was provided to the method or a new one if <code>out</code> was <code>null</code>
     */
    public static Rect calcCenter (int vw, int vh, int iw, int ih, boolean neverScaleUp, Rect out) {

        double scale = Math.min((double) vw / (double) iw, (double) vh / (double) ih);

        int h = (int)(!neverScaleUp || scale<1.0 ? scale * ih : ih);
        int w = (int)(!neverScaleUp || scale<1.0 ? scale * iw : iw);
        int x = ((vw - w)>>1);
        int y = ((vh - h)>>1);

        if (out == null)
            out = new Rect( x, y, x + w, y + h );
        else
            out.set( x, y, x + w, y + h );

        return out;
    }

}
