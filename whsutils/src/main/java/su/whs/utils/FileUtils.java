package su.whs.utils;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by igor n. boulliev on 01.05.15.
 */
public class FileUtils {

    public interface ContentFilter {
        void filter(byte[] buffer, int len);
    }

    public static void Copy(File src, File dst) throws IOException {
        for (File content : src.listFiles()) {
            if (content.isDirectory()) {
                File tgt = new File(dst,content.getName());
                if (!tgt.exists()) tgt.mkdir();
                Copy(content,tgt);
            } else {
                File tgt = new File(dst,content.getName());
                InputStream in = new FileInputStream(content);
                OutputStream out = new FileOutputStream(tgt);
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
        }
    }

    public static void Copy(File src, File dst, ContentFilter filter) throws IOException {
        for (File content : src.listFiles()) {
            if (content.isDirectory()) {
                File tgt = new File(dst,content.getName());
                if (!tgt.exists()) tgt.mkdir();
                Copy(content,tgt, filter);
            } else {
                File tgt = new File(dst,content.getName());
                InputStream in = new FileInputStream(content);
                OutputStream out = new FileOutputStream(tgt);
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    if (filter!=null)
                        filter.filter(buf,len);
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
        }
    }

    public static void Remove(File dir) {
        if (dir.isDirectory() && dir.exists())
            for (File child : dir.listFiles())
                Remove(child);
        dir.delete();
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static long freeSpace(String path) {
        StatFs stat = new StatFs(path);
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN_MR2)
            return stat.getBlockSize() * stat.getFreeBlocks();
        else
            return stat.getFreeBytes();
    }

    public static long freeSpace(File file) {
        return freeSpace(file.getAbsolutePath());
    }

    public static long maxSpace(File a, File b) {
        long s1 = freeSpace(a);
        long s2 = freeSpace(b);
        return s1>s2 ? s1 : s2;
    }

    public static String[] getWritableStorages(Context context) {
        StatFs stat = null;
        List<String> result = new ArrayList<String>();
        File cache = context.getCacheDir();
        File cacheExt = context.getExternalCacheDir();
        long cacheSize = maxSpace(cache,cacheExt);
        File files = context.getFilesDir();

        File filesExt = context.getExternalFilesDir(null);
        long filesSize = maxSpace(files,filesExt);

        File mnt = new File("/mnt");
        if (mnt.exists() && mnt.isDirectory()) {
            File[] points = mnt.listFiles();

        }

        if (result.size()<1) return null;

        String[] r = new String[result.size()];
        for(int i=0; i<r.length; i++)
            r[i] = result.get(i);
        return r;
    }



}
