package com.projects.aungkhanthtoo.bookshop.data;

/**
 * Created by Lenovo on 2/20/2018.
 */

public class Header {
    private int position;
    private String name;

    public Header(int position, String name) {
        this.position = position;
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
