package su.whs.watl.samples;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import su.whs.watl.text.LineBreaker;
import su.whs.watl.text.hyphen.HyphenPattern;

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
                try {
                    DataInputStream is = new DataInputStream(getAssets().open("en_us.hyphen.dat"));
                    LineBreaker hlb = HyphenLineBreaker.getInstance(HyphenPattern.EN_US,is);
                    is.close();
                    Log.v("wATL", "hyphen rules loaded");
                    return null;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void params) {
                hyphenatorReady = true;
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
