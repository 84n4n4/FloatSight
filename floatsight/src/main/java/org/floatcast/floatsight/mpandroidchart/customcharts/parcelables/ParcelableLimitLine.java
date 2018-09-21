/*
 *
 *     FloatSight
 *     Copyright 2018 Thomas Hirsch
 *     https://github.com/84n4n4/FloatSight
 *
 *     This file is part of FloatSight.
 *     FloatSight is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FloatSight is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with FloatSight.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.floatcast.floatsight.mpandroidchart.customcharts.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.components.LimitLine;

import static com.github.mikephil.charting.utils.Utils.convertPixelsToDp;

public class ParcelableLimitLine implements Parcelable {
    private final float limit;
    private final float lineWidth;
    private final int lineColor;

    public ParcelableLimitLine(LimitLine limitLine) {
        this.lineColor = limitLine.getLineColor();
        this.limit = limitLine.getLimit();
        this.lineWidth = convertPixelsToDp(limitLine.getLineWidth());
    }

    ParcelableLimitLine(Parcel parcel) {
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