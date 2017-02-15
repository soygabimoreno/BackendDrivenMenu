package com.gabrielmorenoibarra.android.backenddrivenmenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.gabrielmorenoibarra.g.G;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity of application.
 * Created by Gabriel Moreno on 2017-12-02.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawerLayout) DrawerLayout drawer;
    @BindView(R.id.navigationView) NavigationView navigationView;
    @BindView(R.id.wv) WebView wv;
    @BindView(R.id.pb) ProgressBar pb;

    @BindDrawable(R.drawable.logo_g) Drawable logo;

    private CustomMenu customMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + hashCode());
        DataManager dataManager = DataManager.getInstance();
        String theme = dataManager.getTheme();
        if (!theme.isEmpty()) {
            int themeResId = getResources().getIdentifier(theme, "style", getPackageName());
            setTheme(themeResId);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        customMenu = dataManager.getMenu();
        Menu menu = navigationView.getMenu();

        if (customMenu != null && customMenu.size() <= menu.size()) {
            int i = 0;
            int defaultIcon = getResources().getIdentifier("ic_menu_default", Keys.DRAWABLE, getPackageName());
            while (i < customMenu.size()) {
                MenuItem item = menu.getItem(i);
                item.setTitle(customMenu.getItem(i).getName());
                int iconRes = getResources().getIdentifier(customMenu.getItem(i).getIcon(), Keys.DRAWABLE, getPackageName());
                if (iconRes == 0) iconRes = defaultIcon;
                item.setIcon(iconRes);
                i++;
            }
            while (i < menu.size()) {
                menu.getItem(i).setVisible(false); // Hide surplus items
                i++;
            }
        }

        navigationView.setNavigationItemSelectedListener(this);

        if (G.isConnectedToInternet(this)) {
            new CustomWebViewClient(this, wv, dataManager.getUrlHome(), dataManager.getUrlBase(), new UrlCache(this), 0).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + hashCode());
        AppController.getInstance().setUpdatable(this);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + hashCode());
        AppController.getInstance().setUpdatable(null);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (wv.canGoBack()) { // This is for user can go back with physical back button
                wv.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.about_title));
            ImageView iv = new ImageView(this);
            int p = G.dpToPx(this, 10);
            iv.setPadding(p, G.dpToPx(this, 20), p, p);
            iv.setImageDrawable(logo);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    visitWeb();
                    alertDialog.hide();
                }
            });
            alertDialog.setView(iv);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.visit_web), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    visitWeb();
                }
            });
            alertDialog.setCancelable(true);
            alertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item0:
                loadUrl(0);
                break;
            case R.id.item1:
                loadUrl(1);
                break;
            case R.id.item2:
                loadUrl(2);
                break;
            case R.id.item3:
                loadUrl(3);
                break;
            case R.id.item4:
                loadUrl(4);
                break;
            case R.id.item5:
                loadUrl(5);
                break;
            case R.id.item6:
                loadUrl(6);
                break;
            case R.id.item7:
                loadUrl(7);
                break;
            case R.id.item8:
                loadUrl(8);
                break;
            case R.id.item9:
                loadUrl(9);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadUrl(int i) {
        String currentUrl = wv.getUrl();
        String newUrl = DataManager.getInstance().getUrlBase() + customMenu.getItem(i).getUrl();

        String s1 = removeSlashAtTheEnd(currentUrl);
        String s2 = removeSlashAtTheEnd(newUrl);

        if (!s1.equalsIgnoreCase(s2)) {
            wv.loadUrl(newUrl);
        }
    }

    private String removeSlashAtTheEnd(String s) {
        if (s.endsWith("/")) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    private void visitWeb() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://gabrielmorenoibarra.com"));
        startActivity(intent);
    }

    public void showProgressBar() {
        pb.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        pb.setVisibility(View.GONE);
    }

    /**
     * Update this <code>{@link AppCompatActivity}</code>.
     */
    public void updateActivityOnUiThread() {
        Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + " " + hashCode());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                G.refreshActivity(MainActivity.this);
            }
        });
    }
}