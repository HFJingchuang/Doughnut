package com.doughnut.utils;

import java.io.Serializable;

/**
 * 币种
 */
public class Currency implements Serializable, Comparable<Currency> {
    private int image;
    private String name;
    private boolean isSelect = false;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsSelect() {
        return isSelect;
    }

    public void setSelect(boolean selecet) {
        this.isSelect = selecet;
    }

    @Override
    public int compareTo(Currency o) {
        boolean b1 = Util.isStartWithNumber(this.getName());
        boolean b2 = Util.isStartWithNumber(o.getName());
        if (b1 && !b2) {
            return 1;
        } else if (!b1 && b2) {
            return -1;
        } else {
            return this.getName().compareTo(o.getName());
        }
    }
}