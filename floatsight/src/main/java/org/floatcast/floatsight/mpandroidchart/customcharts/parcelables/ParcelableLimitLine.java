package org.floatcast.floatsight.mpandroidchart.customcharts.parcelables;

import android.os.Parcel;
import android.os.Parcelable;
import com.github.mikephil.charting.components.LimitLine;

import static com.github.mikephil.charting.utils.Utils.convertPixelsToDp;

public class ParcelableLimitLine implements Parcelable {
    private float limit;
    private float lineWidth;
    private int lineColor;

    public ParcelableLimitLine(LimitLine limitLine) {
        this.lineColor = limitLine.getLineColor();
        this.limit = limitLine.getLimit();
        this.lineWidth = convertPixelsToDp(limitLine.getLineWidth());
    }

    private ParcelableLimitLine(Parcel parcel) {
        lineColor = parcel.readInt();
        limit = parcel.readFloat();
        lineWidth = parcel.readFloat();
    }

    public LimitLine getLimitLine() {
        LimitLine limitLine = new LimitLine(limit);
        limitLine.setLineColor(lineColor);
        limitLine.setLineWidth(lineWidth);
        return limitLine;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(lineColor);
        parcel.writeFloat(limit);
        parcel.writeFloat(lineWidth);
    }

    public static final Parcelable.Creator<ParcelableLimitLine> CREATOR
            = new Parcelable.Creator<ParcelableLimitLine>() {

        public ParcelableLimitLine createFromParcel(Parcel in) {
            return new ParcelableLimitLine(in);
        }

        public ParcelableLimitLine[] newArray(int size) {
            return new ParcelableLimitLine[size];
        }
    };
}