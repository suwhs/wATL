package su.whs.utils;

/**
 * Created by igor n. boulliev on 02.05.15.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * reads zip content tree into cache for fast access
 */

public class ZipFileExt {

    private class Entry {
        public String mName;
        public String mPath;
        public ZipEntry mEntry;
        public List<Entry> mEntries = null;

        public Entry(String name, ZipEntry entry) {
            File f = new File(name);
            mName = f.getName();
            mPath = f.getParent() == null ? null : f.getParent(); // need debug for first-level names
            mEntry = entry;
            if (mEntry.isDirectory())
                mEntries = new ArrayList<Entry>();
        }

        public String getName() { return mName; }
        public ZipEntry getEntry() { return mEntry; }
        public InputStream getInputStream() throws IOException { return ZipFileExt.this.mZip.getInputStream(mEntry); }
        /* */
        public List<Entry> list() {
            return mEntries;
        }
        /* */

    }

    private List<Entry> mRoot = new ArrayList<Entry>();
    private Map<String,Entry> mPathMap = new HashMap<String,Entry>();
    private ZipFile mZip = null;
    private ZipFileExt mParent = null;

    /**
     *
     * @param fileName
     * @throws IOException
     */

    public ZipFileExt(String fileName) throws IOException {
        /** all files comes with full names /like/a/this/path **/
        /** so, recreate virtual directory tree */
        mZip = new ZipFile(fileName);

        List<Entry> stack = new ArrayList<Entry>(); /* */
        List<Entry> dirEntries = new ArrayList<Entry>();

        String dirName = null;
        Entry dirEntry = null;
        /* */
        Enumeration<? extends ZipEntry> entries = mZip.entries();
        /* contract:
        *   entry.isDirectory() == true && entry.getName().endsWith("/") == false
        *   entry.getPath().endsWith("/") == false
        * */

        while(entries.hasMoreElements()) {
            ZipEntry current = entries.nextElement();
            String name = current.getName();
            Entry e = new Entry(name,current);
            mPathMap.put(name,e);
        }

        /*** */

        /** */

        for(Entry entry : mPathMap.values()) {
            if (entry.mPath==null)
                mRoot.add(entry);
            else if (mPathMap.containsKey(entry.mPath)) {
                Entry parent = mPathMap.get(entry.mPath);
                parent.mEntries.add(entry); // connect entry to parent
            } else {
                /** */
            throw new RuntimeException();
            }
        }

        /** */

    }

    /**
     *
     * @return Entry
     */

    public List<Entry> list() {
        return mRoot;
    }

    /* ZipFileExt */
    public static  void test_zip(String fileName) {
        try {
            ZipFileExt zfe = new ZipFileExt(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
