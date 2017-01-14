package su.whs.hyphens;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by igor n. boulliev on 10.04.15.
 */

public class PatternsLoader extends InputStreamPatternLoader {
    private static final String TAG = "PatternsLoader";
    private static PatternsLoader mInstance = null;
    private Map<String,HyphenPattern> mCache = new HashMap<String,HyphenPattern>();
    private PatternsLoader() {}


    public static PatternsLoader getInstance() {
        synchronized (PatternsLoader.class) {
            if (mInstance == null)
                mInstance = new PatternsLoader();
        }
        return mInstance;
    }

    public HyphenPattern getHyphenPatternAssets(Context context, String fileName) {
        synchronized (mCache) {
            if (mCache.containsKey(fileName))
                return mCache.get(fileName);
            AssetManager am = context.getAssets();
            if (am != null) {
                try {
                    HyphenPattern hyphenPattern = getHyphenPatternInputStream(am.open(fileName));
                    if (hyphenPattern == null)
                        return null;
                    mCache.put(fileName,hyphenPattern);
                } catch (IOException e) {
                    Log.e(TAG, "Error loading hyphenation rules:" + e);
                    return null;
                }
            }
            return mCache.get(fileName);
        }
    }
}
