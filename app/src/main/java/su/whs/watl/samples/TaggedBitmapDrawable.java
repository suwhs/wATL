package su.whs.watl.samples;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by igor n. boulliev on 02.09.15.
 */
public class TaggedBitmapDrawable extends BitmapDrawable {
    private String mTag;
    public TaggedBitmapDrawable(Resources res, Bitmap bitmap, String tag) {
        super(res,bitmap);
        mTag = tag;
    }

    public String getTag() { return mTag; }
}
