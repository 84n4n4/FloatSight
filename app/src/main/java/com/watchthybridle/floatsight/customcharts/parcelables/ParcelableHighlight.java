package com.watchthybridle.floatsight.customcharts.parcelables;

import android.os.Parcel;
import android.os.Parcelable;
import com.github.mikephil.charting.highlight.Highlight;

public class ParcelableHighlight implements Parcelable {
    private float xValue;
    private float yValue;
    private int dataSetIndex;

    public ParcelableHighlight(Highlight highlight) {
        this.xValue = highlight.getX();
        this.yValue = highlight.getY();
        this.dataSetIndex = highlight.getDataSetIndex();
    }

    private ParcelableHighlight(Parcel parcel) {
        xValue = parcel.readFloat();
        yValue = parcel.readFloat();
        dataSetIndex = parcel.readInt();
    }

    public Highlight getHighLight() {
        return new Highlight(xValue, yValue, dataSetIndex);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeFloat(xValue);
        parcel.writeFloat(yValue);
        parcel.writeInt(dataSetIndex);
    }

    public static final Creator<ParcelableHighlight> CREATOR
            = new Creator<ParcelableHighlight>() {

        public ParcelableHighlight createFromParcel(Parcel in) {
            return new ParcelableHighlight(in);
        }

        public ParcelableHighlight[] newArray(int size) {
            return new ParcelableHighlight[size];
        }
    };
}