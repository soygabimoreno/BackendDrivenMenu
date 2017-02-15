package com.gabrielmorenoibarra.android.backenddrivenmenu;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.gabrielmorenoibarra.g.G;
import com.gabrielmorenoibarra.g.java.GLastModified;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Application Controller.
 * Created by Gabriel Moreno on 2017-12-02.
 */
public class AppController extends MultiDexApplication {

    public static final String TAG = AppController.class.getSimpleName();

    private static WeakReference<AppController> ourInstance;
    private AppCompatActivity currentActivity;
    private RequestQueue requestQueue;

    public static synchronized AppController getInstance() {
        return ourInstance.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + hashCode());
        ourInstance = new WeakReference<>(this);
        DataManager dataManager = DataManager.getInstance();
        dataManager.setPrefs(getSharedPreferences(getPackageName(), Context.MODE_PRIVATE));
        if (G.isConnectedToInternet(this)) {
            String url = BuildConfig.URL_REQUEST;
            String user = BuildConfig.USER;
            String password = BuildConfig.PASSWORD;
//            final long lastModified = new GLastModified(url, user, password).getMillis();
            final long lastModified = new GLastModified(url).getMillis();
            if (lastModified > dataManager.getLastModified()) {
                Map<String, Object> params = new HashMap<>();
                RequestManager.getInstance().doRequest(user, password, this, Keys.REQUEST_TAG_URL_BASE, url,
                        params, new RequestManager.RequestManagerResponse() {
                            @Override
                            public void onResponse(boolean success, JSONObject content, Exception e) {
                                try {
                                    if (success) {
                                        DataManager dataManager = DataManager.getInstance();
                                        dataManager.setUrlBase(content.getString(Keys.URL_BASE));
                                        dataManager.setTheme(content.getString(Keys.THEME));
                                        dataManager.setUrlHome(content.getString(Keys.URL_HOME));
                                        dataManager.setMenu(content.getJSONObject(Keys.MENU));
                                        dataManager.setLastModified(lastModified);
                                        updateCurrentActivity(); // We update the Activity
                                    } else {
                                        if (e == null) {
                                            Log.e(TAG, "Exception is null.");
                                        } else {
                                            Log.e(TAG, "Exception: " + e.toString());
                                        }
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
            }
        }
    }

    /**
     * Register the <code>{@link AppCompatActivity}</code> to it will be updated.<p>
     * It is made in a synchronized way.
     * @param updatable Activity to update.
     */
    public synchronized void setUpdatable(AppCompatActivity updatable) {
        this.currentActivity = updatable;
    }

    /**
     * Update the current <code>{@link AppCompatActivity}</code>.<p>
     * It is made in a synchronized way.
     */
    public synchronized void updateCurrentActivity() {
        if (currentActivity != null) ((MainActivity) currentActivity).updateActivityOnUiThread();
    }

    /**
     * @return the request queue.
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(this);
        return requestQueue;
    }

    /**
     * Add parameters to the request queue.
     * @param req Request list.
     * @param tag Request tag.
     * @param <T> Type of the request.
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? AppController.TAG : tag);
        getRequestQueue().add(req);
    }
}