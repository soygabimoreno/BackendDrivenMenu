package com.gabrielmorenoibarra.android.backenddrivenmenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for encapsulating response received with menu items.
 * Created by Gabriel Moreno on 2017-12-02.
 */
public class CustomMenu {

    private List<MenuItem> items;

    public CustomMenu() {
        items = new ArrayList<>();
    }

    public MenuItem getItem(int i) {
        return items.get(i);
    }

    public int size() {
        return items.size();
    }

    public class MenuItem {
        private String name;
        private String url;
        private String icon;

        public MenuItem(String name, String url, String icon) {
            this.name = name;
            this.url = url;
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getIcon() {
            return icon;
        }
    }
}