package su.whs.hyphens;

/**
 *
 *
 * @author igor n. boulliev <igor@whs.su>
 * Apache License 2.0
 *
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import su.whs.syllabification.parent.LineBreaker;

import static android.content.ContentValues.TAG;


@SuppressLint("UseSparseArrays")
public class HyphenLineBreaker extends LineBreaker {

    private static final HashMap<HyphenPattern, LineBreaker> cached;
    private ReverseLookupHyphenator mHyphenator;

    static {
        cached = new HashMap<HyphenPattern, LineBreaker>();
    }

    public HyphenLineBreaker(HyphenPattern pattern) {
        mHyphenator = new ReverseLookupHyphenator(pattern);
    }

    public static LineBreaker getInstance(HyphenPattern hyphenationPattern) {
        if (hyphenationPattern == null) return new DefaultLineBreaker();
        synchronized (cached) {
            if (!cached.containsKey(hyphenationPattern)) {
                cached.put(hyphenationPattern, new HyphenLineBreaker(hyphenationPattern));
                return cached.get(hyphenationPattern);
            }
            return cached.get(hyphenationPattern);
        }
    }


    public static LineBreaker getInstance(Context context, String name) {
        HyphenPattern pat = PatternsLoader.getInstance().getHyphenPatternAssets(context, name + ".hyphen.dat");
        return getInstance(pat);
    }

    public static void require(Context context, String... names) {
        HyphenPattern pat;
        for (String name : names) {
            pat = PatternsLoader.getInstance()
                    .getHyphenPatternAssets(context, name + ".hyphen.dat");
            if (pat == null) {
                Log.w(TAG, "COULD NOT FIND REQUIRED '" + name + ".hyphen.dat");
            } else
                Log.d(TAG, "loaded: '" + name + "' pattern");
        }
    }

    @Override
    public int nearestLineBreak(char[] text, int start, int end, int limit) {
        return mHyphenator.nearestLineBreak(text, start, end, limit);
    }

    private static class DefaultLineBreaker extends LineBreaker {
        @Override
        public int nearestLineBreak(char[] text, int start, int end, int limit) {
            return start;
        }
    }
}
