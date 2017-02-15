package com.gabrielmorenoibarra.android.backenddrivenmenu;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.WebResourceResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to cache data from webs.
 * Created by Gabriel Moreno on 2017-13-02.
 */
public class UrlCache {

    public static final String TAG = UrlCache.class.getSimpleName();

    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60L * ONE_SECOND;
    public static final long ONE_HOUR = 60L * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;

    private static class CacheEntry {
        String url;
        String fileName;
        String mimeType;
        String encoding;
        long maxAgeMillis;

        private CacheEntry(String url, String fileName, String mimeType, String encoding, long maxAgeMillis) {
            this.url = url;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.encoding = encoding;
            this.maxAgeMillis = maxAgeMillis;
        }
    }

    private Activity activity;
    private Map<String, CacheEntry> cacheEntries = new HashMap<>();
    private File rootDir;

    public UrlCache(Activity activity) {
        this.activity = activity;
        rootDir = activity.getFilesDir();
    }

    public void register(String urlBase, String url, long maxAgeMillis) {
        Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + hashCode());
        String cacheFileName = url.replace(urlBase, "");
        cacheFileName = cacheFileName.replaceAll("/", "_");
        CacheEntry entry = new CacheEntry(url, "file_" + cacheFileName, "text/html", "UTF-8", maxAgeMillis);
        cacheEntries.put(url, entry);
    }

    public WebResourceResponse load(String url) {
        CacheEntry cacheEntry = cacheEntries.get(url);
        if (cacheEntry == null) return null;

        File cachedFile = new File(rootDir.getPath() + File.separator + cacheEntry.fileName);
        if (cachedFile.exists()) {
            long cacheEntryAge = System.currentTimeMillis() - cachedFile.lastModified();
            if (cacheEntryAge > cacheEntry.maxAgeMillis) {
                if (cachedFile.delete()) {
                    Log.d(TAG, "Deleted '" + url + "' from cache");
                } else {
                    Log.e(TAG, "Error deleting '" + url + "' from cache!");
                }
                return load(url); // Cached file deleted, call load() again
            }

            // Cached file exists and is not too old. Return file:
            Log.d(TAG, "Loading '" + url + "' from cache...");
            try {
                return new WebResourceResponse(cacheEntry.mimeType, cacheEntry.encoding, new FileInputStream(cachedFile));
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Error loading cached file: " + cachedFile.getPath() + " : " + e.getMessage(), e);
            }
        } else { // File is not cached yet
            try {
                downloadAndStore(url, cacheEntry);
                return load(url); // The file exists in the cache, so we can just call this method again to read it
            } catch (Exception e) {
                Log.e(TAG, "Error reading file over network: " + cachedFile.getPath(), e);
            }
        }
        return null;
    }

    private void downloadAndStore(String url, CacheEntry cacheEntry) throws IOException {
        Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + hashCode());
        URL urlObj = new URL(url);
        URLConnection urlConnection = urlObj.openConnection();
        InputStream is = urlConnection.getInputStream();
        FileOutputStream fos = activity.openFileOutput(cacheEntry.fileName, Context.MODE_PRIVATE);

        int data = is.read();
        while (data != -1) {
            fos.write(data);
            data = is.read();
        }
        is.close();
        fos.close();
        Log.i(TAG, "Cache file '" + cacheEntry.fileName + "' successfully stored");
    }
}