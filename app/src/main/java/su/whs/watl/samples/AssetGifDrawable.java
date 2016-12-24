/*
 * Copyright 2015 whs.su
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package su.whs.watl.samples;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;
import su.whs.watl.samples.utils.GifDecoder;
import su.whs.wlazydrawable.PreviewDrawable;

/**
 * Created by igor n. boulliev on 31.08.15.
 */

/**
 * demo code for show animated drawables in TextViewEx
 *
 * LazyDrawable required for animation on demand launch,
 * and for support AOSP < 11
 *
 *
 */


public class AssetGifDrawable extends PreviewDrawable {
    private String mPath;
    private Context mContext;
    private boolean mFullVersionLoaded = false;

    public AssetGifDrawable(Context context, String path) {
        super(context,800, 500);
        setScaleType(ScaleType.FILL);
        mPath = path;
        mContext = context;
    }

    @Override
    public void onVisibilityChanged(boolean b) {

    }

    @Override
    protected void onLoadingError() {

    }

    /* call loadFullImage and launch animation */
    @Override
    public void start() {
        if (!mFullVersionLoaded) {
            super.loadFullDrawable();
        }
        super.start();
    }

    @Override
    protected Drawable getPreviewDrawable() {
        GifDecoder decoder = new GifDecoder();
        try {
            InputStream inputStream = mContext.getAssets().open(mPath);
            decoder.read(inputStream, 0);
            decoder.advance();
            Bitmap frame = decoder.getNextFrame();
            Drawable result = new BitmapDrawable(mContext.getResources(),frame);
            inputStream.close();
            return result;
        } catch (IOException e) {
            onLoadingError();
            return null;
        }
    }

    @Override
    protected Drawable getFullDrawable() {
        try { // sleep added to display loading animation
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
        if (mFullVersionLoaded) return null;
        try {
            /* GifDrawable implements Animatable, so we can show animations */
            Drawable r = Build.VERSION.SDK_INT > 18 ? new GifDrawable(mContext.getAssets().open(mPath)) : new GifDrawableCompat(mContext.getAssets().open(mPath));
            mFullVersionLoaded = true;
            return r;
        } catch (IOException e) {
            onLoadingError();
            return null;
        }
    }

    @Override
    protected int getSampling() {
        return 1;
    }

    public String getPath() {
        return mPath;
    }
    private Drawable mPlayButtonDrawable;
    public void setPlayButtonDrawable(Drawable rr) {
        rr.setBounds(0,0,rr.getIntrinsicWidth(),rr.getIntrinsicHeight());
        mPlayButtonDrawable = rr;
        invalidateSelfOnUiThread();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (!isLoading() && !isRunning() && mPlayButtonDrawable!=null) {
            super.drawProgress(canvas,mPlayButtonDrawable,0,255);
        }
    }
}
