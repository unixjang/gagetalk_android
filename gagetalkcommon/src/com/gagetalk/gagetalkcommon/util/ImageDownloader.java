package com.gagetalk.gagetalkcommon.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hyochan on 4/6/15.
 */
public class ImageDownloader {

    private static final String TAG = "ImageDownloader";

    private static ImageDownloader imageDownloader;
    private SharedPreferences sharedPreferences;
    private Context context;

    private ImageDownloader(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static ImageDownloader getInstance(Context context) {
        if (imageDownloader == null) imageDownloader = new ImageDownloader(context);
        return imageDownloader;
    }

    public void updateCache(final String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                // do something
            }

            @Override
            public void onSuccess(int i, Header[] headers, File file) {
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                addBitmapToCache(url, myBitmap);
            }
        });
    }

    public void download(final String url, final ImageView imageView) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                // do something
            }

            @Override
            public void onSuccess(int i, Header[] headers, File file) {
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setMinimumHeight(156);
                imageView.setImageBitmap(myBitmap);
                addBitmapToCache(url, myBitmap);
            }
        });
    }

    public void getImage(String url, ImageView view) {

        resetPurgeTimer();

        Bitmap bitmap = getBitmapFromCache(url);

        if(bitmap == null) {
            download(url, view);
        } else {
            view.setImageBitmap(bitmap);
        }
    }
/*

    private static ImageDownloaderTask getImageDownloaderTask(ImageView imageView) {
        if(imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if(drawable instanceof DownloadedDrawable) {
                return ((DownloadedDrawable)drawable).getImageDownloaderTask();
            }
        }
        return null;
    }

    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<ImageDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(ImageDownloaderTask task) {
            // super(Color.CYAN);

            bitmapDownloaderTaskReference =
                    new WeakReference<ImageDownloaderTask>(task);
        }

        public ImageDownloaderTask getImageDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }
*/

/*    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String url;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            return downloadImage(params[0]);
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(isCancelled()) {
                bitmap = null;
            }

            addBitmapToCache(url, bitmap);

            if(imageViewReference != null) {
                ImageView imageView = imageViewReference.get();

                if(imageView != null) {
                    ImageDownloaderTask task = getImageDownloaderTask(imageView);
                    if(this == task) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }

        private Bitmap downloadImage(String url) {

            HttpClient client = HttpClientBuilder.create().build();
            *//*final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");*//*
            final HttpGet request = new HttpGet(url);

            try {

                HttpResponse response = client.execute(request);

                final int status = response.getStatusLine().getStatusCode();
                if(status != HttpStatus.SC_OK) {
                    // Log.w(TAG, "Status " + status + " while retrieving bitmap from " + url);
                    return null;
                }

                final HttpEntity entity = response.getEntity();

                InputStream inputStream = null;

                try {
                    inputStream = entity.getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    // Log.d(TAG, "New image downloaded");
                    return bitmap;
                } finally {
                    if(inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }

            } catch (IOException e) {
                // Log.w(TAG, "Error while retrieving bitmap from " + url, e);
            } finally {
                if(client != null) {
                    *//*client.close();*//*
                    client = null;
                }
            }

            return null;
        }
    }*/

	/*
     * Cache-related fields and methods.
     *
     * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
     * Garbage Collector.
     */

    private static final int HARD_CACHE_CAPACITY = 10;
    private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, Bitmap> sHardBitmapCache =
            new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
                private static final long serialVersionUID = 1L;

                @Override
                protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
                    if (size() > HARD_CACHE_CAPACITY) {
                        // Entries push-out of hard reference cache are transferred to soft reference cache
                        sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                        return true;
                    } else
                        return false;
                }
            };

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
            new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);

    private final Handler purgeHandler = new Handler();

    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    /**
     * Adds this bitmap to the cache.
     * @param bitmap The newly downloaded bitmap.
     */
    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    /**
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    private Bitmap getBitmapFromCache(String url) {
        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(url);
            if (bitmap != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, bitmap);
                // Log.d(TAG, "Image from hard cache");
                return bitmap;
            }
        }

        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                // Bitmap found in soft cache
                // Log.d(TAG, "Image from soft cache");
                return bitmap;
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(url);
            }
        }

        return null;
    }

    /**
     * Clears the image cache used internally to improve performance. Note that for memory
     * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
     */
    public void clearCache() {
        // Log.d(TAG, "Clear cache");
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    private void resetPurgeTimer() {
        // Log.d(TAG, "Reset purge timer");
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }

}
