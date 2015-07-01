package su.whs.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by igor n. boulliev on 04.05.15.
 */
public class NetUtils {
    public interface Result {
        void onError(int errorCode);
        void onSuccess(InputStream is);
    }
    public static InputStream HttpGet(URI url, Result result) {
        HttpClient hc = new DefaultHttpClient();
        HttpGet hg = new HttpGet(url);
        try {
            HttpResponse r = hc.execute(hg);
            HttpEntity e = r.getEntity();
            if (result!=null && e!=null)
                result.onSuccess(e.getContent());
            return e.getContent();
        } catch (IOException e) {
            if (result!=null) {
                result.onError(-1);
            }
        } catch (SecurityException e) {
            Log.e("NetUtils", "NetUtils require INTERNET permission ");
        }

        return null;
    }

    public static InputStream HttpGet(URI url) {
        return HttpGet(url,null);
    }


}
