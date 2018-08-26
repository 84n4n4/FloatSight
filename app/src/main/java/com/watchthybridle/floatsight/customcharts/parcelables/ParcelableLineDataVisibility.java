package com.watchthybridle.floatsight.customcharts.parcelables;

import android.os.Parcel;
import android.os.Parcelable;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class ParcelableLineDataVisibility implements Parcelable {
    private boolean[] visibility;

    public ParcelableLineDataVisibility(LineData lineData) {
        if (lineData != null) {
            visibility = new boolean[lineData.getDataSets().size()];
            for(ILineDataSet lineDataSet : lineData.getDataSets()) {
                visibility[lineData.getDataSets().indexOf(lineDataSet)] = lineDataSet.isVisible();
            }
        }
    }

    public void setLineDataVisibily(LineData lineData) {
        for(ILineDataSet lineDataSet : lineData.getDataSets()) {
            lineDataSet.setVisible(visibility[lineData.getDataSets().indexOf(lineDataSet)]);
        }
    }

    private ParcelableLineDataVisibility(Parcel parcel) {
        parcel.readBooleanArray(visibility);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeBooleanArray(visibility);
    }

    public static final Creator<ParcelableLineDataVisibility> CREATOR
            = new Creator<ParcelableLineDataVisibility>() {

        public ParcelableLineDataVisibility createFromParcel(Parcel in) {
            return new ParcelableLineDataVisibility(in);
        }

        public ParcelableLineDataVisibility[] newArray(int size) {
            return new ParcelableLineDataVisibility[size];
        }
    };
}