package su.whs.htmlparser;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spanned;

import java.util.Map;

/**
 * Created by igor n. boulliev <igor@whs.su> on 23.01.17.
 */

public class Html {

    public interface HtmlTagHandler {
        void onTagStart(String tag, Map<String,String> attributes, Editable output);
        void onTagEnd(String tag, Editable output);
    }

    public interface DrawableHandler {
        Drawable getDrawable(String url, String mimeType, Map<String,String> attributes);
    }

    static {
        System.loadLibrary("htmlparser");
    }

    public native Spanned fromHtml(String html);
    public native Spanned fromHtml(HtmlTagHandler tagHandler);
    public native Spanned fromHtml(HtmlTagHandler tagHandler, DrawableHandler drawableHandler);
}
