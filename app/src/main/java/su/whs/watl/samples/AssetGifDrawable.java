package su.whs.watl.samples;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;
import su.whs.watl.experimental.LazyDrawable;
import su.whs.watl.samples.utils.GifDecoder;

/**
 * Created by igor n. boulliev on 31.08.15.
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

    /* here we return first frame */
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

    @Override
    protected Drawable readFullDrawable() {
        if (mLoadingError) return null;
        if (mFullVersionLoaded) return null;
        try {
             Drawable r = new GifDrawable(mContext.getAssets().open(mPath));
            mFullVersionLoaded = true;
            return r;
        } catch (IOException e) {
            onFailure();
            return null;
        }
    }

    @Override
    public void start() {
        loadFullImage();
        super.start();
    }
}
