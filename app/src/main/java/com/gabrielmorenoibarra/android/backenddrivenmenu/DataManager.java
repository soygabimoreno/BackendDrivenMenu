package com.gabrielmorenoibarra.android.backenddrivenmenu;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Data management.
 * Created by Gabriel Moreno on 2017-13-02.
 */
public class DataManager {

    /** Shared preferences of the app. */
    private SharedPreferences prefs;

    private static DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    /**
     * Set the <code>SharedPreferences</code>. There is to do this at the beginning of app initialization.
     */
    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    /**
     * @return the date (in seconds since 1970) of last modification of the file data on hosting.
     */
    public long getLastModified() {
        return prefs.getLong(Keys.LAST_MODIFIED, -1);
    }

    /**
     * Set the date (in seconds since 1970) of last modification of the file data on hosting.
     */
    public void setLastModified(long lastModified) {
        prefs.edit().putLong(Keys.LAST_MODIFIED, lastModified).apply();
    }

    /**
     * @return the name of person that uses application.
     */
    public String getName() {
        return prefs.getString(Keys.NAME, "");
    }

    /**
     * Set the name of person that uses application.
     */
    public void setName(String name) {
        prefs.edit().putString(Keys.NAME, name).apply();
    }

    /**
     * @return the base URL where the website is hosted.
     */
    public String getUrlBase() {
        return prefs.getString(Keys.URL_BASE, "");
    }

    /**
     * Set the base URL where the website is hosted.
     */
    public void setUrlBase(String urlBase) {
        prefs.edit().putString(Keys.URL_BASE, urlBase).apply();
    }

    /**
     * Set the home URL where the app is launched.
     */
    public void setUrlHome(String urlHome) {
        prefs.edit().putString(Keys.URL_HOME, urlHome).apply();
    }

    /**
     * @return the home URL where the app is launched.
     */
    public String getUrlHome() {
        return prefs.getString(Keys.URL_HOME, "");
    }

    public CustomMenu getMenu() {
        return new Gson().fromJson(prefs.getString(Keys.MENU, ""), CustomMenu.class);
    }

    public void setMenu(JSONObject menu) {
        prefs.edit().putString(Keys.MENU, menu.toString()).apply();
    }

    public String getTheme() {
        return prefs.getString(Keys.THEME, "");
    }

    public void setTheme(String theme) {
        prefs.edit().putString(Keys.THEME, theme).apply();
    }
}
