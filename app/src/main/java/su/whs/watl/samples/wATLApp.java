package su.whs.watl.samples;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import su.whs.watl.text.HyphenLineBreaker;
import su.whs.watl.text.LineBreaker;
import su.whs.watl.text.hyphen.HyphenPattern;
import su.whs.watl.text.hyphen.PatternsLoader;

/**
 * Created by igor n. boulliev on 03.04.15.
 */
public class wATLApp extends Application {
    private boolean hyphenatorReady = false;
    public static boolean serializationEnabled = false;
    private ContentLoader contentLoader = new ContentLoader();

    public interface StateListener {
        void onHyphenatorLoaded();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected void onPreExecute() {
                hyphenatorReady = false;
            }

            @Override
            protected Void doInBackground(Void... params) {
                HyphenPattern pat = PatternsLoader
                        .getInstance(getBaseContext())
                        .getHyphenPatternAssets("en_us.hyphen.dat");
                if (pat!=null) {
                    LineBreaker hlb = HyphenLineBreaker.getInstance(pat);
                    hyphenatorReady = true;
                }

                return null;
            }



            @Override
            protected void onPostExecute(Void params) {
                notifyListeners();
            }
        }.execute();

//        /** benchmark **/
//        contentLoader.removeCachedVersion(getBaseContext(),ContentLoader.ARTICLE_SCIENCE1);
//        long start = SystemClock.uptimeMillis();
//        for(int i=0; i<100; i++) {
//            ContentLoader.Article a = contentLoader.get(ContentLoader.ARTICLE_SCIENCE1);
//            try {
//                a.load(getApplicationContext(), new ArticleView() {
//                    @Override
//                    public void setLoadingState(boolean state, int percents) {
//
//                    }
//
//                    @Override
//                    public void setContent(String title, String author, String source, CharSequence content) {
//
//                    }
//                });
//            } catch (Exception e) {
//                Log.e("App", "benchmark failed");
//                e.printStackTrace();
//                return;
//            }
//        }
//        long spent = SystemClock.uptimeMillis() - start;
//        Log.e("APP:PERF", "100 loads: "+String.valueOf(spent)+" ms");
//        start = SystemClock.uptimeMillis();
        if (serializationEnabled) {
            contentLoader.cacheArticleAsSerializedSpanned(getApplicationContext(), new Runnable() {
                @Override
                public void run() {
                    Log.d("wATLApp", "serialization finished");
                }
            });
        }
//        spent = SystemClock.uptimeMillis() - start;
//        Log.e("APP:PERF", "caching time spent: " + String.valueOf(spent) + " ms");
//        start = SystemClock.uptimeMillis();
//        for(int i=0; i<100; i++) {
//            ContentLoader.Article a = contentLoader.get(ContentLoader.ARTICLE_SCIENCE1);
//            try {
//                a.load(getApplicationContext(),new ArticleView() {
//                    @Override
//                    public void setLoadingState(boolean state, int percents) {
//
//                    }
//
//                    @Override
//                    public void setContent(String title, String author, String source, CharSequence content) {
//
//                    }
//                });
//            } catch (Exception e) {
//                // e.printStackTrace();
//                Log.e("APP:PERF", "benchmark failed");
//                e.printStackTrace();
//                return;
//            }
//        }
//        spent = SystemClock.uptimeMillis() - start;
//        Log.e("APP:PERF", "100 deserialization time spent: "+String.valueOf(spent)+" ms");
    }

    List<StateListener> mStateListeners = new ArrayList<StateListener>();

    public void addStateListener(StateListener listener) {
        synchronized (this) {
            if (!mStateListeners.contains(listener))
                mStateListeners.add(listener);
            if (hyphenatorReady)
                listener.onHyphenatorLoaded();
        }
    }

    public void removeStateListener(StateListener listener) {
        synchronized (this) {
            if (mStateListeners.contains(listener))
                mStateListeners.remove(listener);
        }
    }

    private void notifyListeners() {
        if (hyphenatorReady)
            synchronized (this) {
                for(StateListener listener : mStateListeners)
                    listener.onHyphenatorLoaded();
            }
    }

    public void getArticle(final String uuid, final ArticleView view) {
        new AsyncTask<Void,Void,ContentLoader.Article>() {

            @Override
            protected ContentLoader.Article doInBackground(Void... params) {
                ContentLoader.Article article = contentLoader.get(uuid);

                return article;
            }

            @Override
            protected void onPostExecute(ContentLoader.Article article) {
                try {
                    article.load(getApplicationContext(),view);
                    view.setLoadingState(false,100);
                } catch (Exception e) {
                    view.setContent(null,null,null,"error reading article: " + e.toString());
                }
            }
        }.execute();
    }
}
