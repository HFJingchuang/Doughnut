package com.doughnut.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 币种
 */
public class Currency implements Parcelable, Comparable<Currency> {
    private int image;
    private String name;
    private String issue;
    private boolean isSelect;

    public Currency() {
    }

    protected Currency(Parcel in) {
        image = in.readInt();
        name = in.readString();
        issue = in.readString();
        isSelect = in.readByte() != 0;
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

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

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public boolean getIsSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        this.isSelect = select;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(image);
        dest.writeString(name);
        dest.writeString(issue);
        dest.writeByte((byte) (isSelect ? 1 : 0));
    }
}