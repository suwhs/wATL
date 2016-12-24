package su.whs.lazydrawable.parent;

import android.graphics.drawable.Drawable;

/**
 * Created by igor n. boulliev on 18.08.16.
 */
public interface LazyDrawable  {
    public abstract void Unload();
    public abstract void onVisibilityChanged(boolean visible);
    public abstract void load();
    public abstract void setCallbackCompat(Drawable.Callback drawableCallbacks);
}
