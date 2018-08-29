package com.watchthybridle.floatsight.customcharts.parcelables;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class ParcelableLineDataSetVisibility implements Parcelable {
    @Retention(SOURCE)
    @IntDef({VISIBLE, GONE})
    public @interface LineDataSetVisibility {}
    public static final int VISIBLE = 1;
    public static final int GONE = 0;
    private int visibility;

    public ParcelableLineDataSetVisibility(ILineDataSet lineDataSet) {
        if (lineDataSet != null) {
            visibility = lineDataSet.isVisible() ? VISIBLE : GONE;
        }
    }

    public void setLineDataSetVisibily(ILineDataSet lineDataSet) {
        lineDataSet.setVisible(visibility == VISIBLE);
    }

    private ParcelableLineDataSetVisibility(Parcel parcel) {
        visibility = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(visibility);
    }

    public static final Creator<ParcelableLineDataSetVisibility> CREATOR
            = new Creator<ParcelableLineDataSetVisibility>() {

        public ParcelableLineDataSetVisibility createFromParcel(Parcel in) {
            return new ParcelableLineDataSetVisibility(in);
        }

        public ParcelableLineDataSetVisibility[] newArray(int size) {
            return new ParcelableLineDataSetVisibility[size];
        }
    };
}