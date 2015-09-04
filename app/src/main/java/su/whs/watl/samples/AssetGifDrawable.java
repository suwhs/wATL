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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;
import su.whs.watl.experimental.LazyDrawable;
import su.whs.watl.samples.utils.GifDecoder;

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


public class AssetGifDrawable extends LazyDrawable {
    private String mPath;
    private Context mContext;
    private boolean mFullVersionLoaded = false;

    public AssetGifDrawable(Context context, String path) {
        super(0, 0);
        mPath = path;
        mContext = context;
        Drawable drawable = readPreviewDrawable();
        if (drawable==null) {
            onFailure();
            return;
        }
        setSize(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        setDrawable(drawable);
    }

    /**
     * readPreviewDrawable() must returns initial image for DynamicDrawableSpan (less resolution version, BW version),
     * or single frame from animation
     *
     * @return
     */
    @Override
    protected Drawable readPreviewDrawable() {
        if (mLoadingError) return null;
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
            onFailure();
            return null;
        }
    }

    /**
     * readFullDrawable must returns high-quality version of image, or Animatable instance
     *
     * @return
     */

    @Override
    protected Drawable readFullDrawable() {
        if (mLoadingError) return null;
        if (mFullVersionLoaded) return null;
        try {
            /* GifDrawable implements Animatable, so we can show animations */
             Drawable r = Build.VERSION.SDK_INT>18 ? new GifDrawable(mContext.getAssets().open(mPath)) : new GifDrawableCompat(mContext.getAssets().open(mPath));
            mFullVersionLoaded = true;
            return r;
        } catch (IOException e) {
            onFailure();
            return null;
        }
    }

    /* call loadFullImage and launch animation */
    @Override
    public void start() {
        loadFullImage();
        super.start();
    }

    public String getPath() {
        return mPath;
    }
}
