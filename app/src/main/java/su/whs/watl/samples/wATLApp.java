package su.whs.watl.samples;

import android.app.Application;
import android.os.AsyncTask;

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
}
