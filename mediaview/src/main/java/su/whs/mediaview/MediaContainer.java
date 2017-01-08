package su.whs.mediaview;

import android.content.Context;

/**
 * Created by igor n. boulliev on 14.01.16.
 */
public interface MediaContainer {
    Context getContext();
    void addView(IMediaView mv);
    void onMediaViewClosed();
}
