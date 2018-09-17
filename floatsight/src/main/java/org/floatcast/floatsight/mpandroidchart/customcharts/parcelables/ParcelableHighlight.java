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
import com.github.mikephil.charting.highlight.Highlight;

public class ParcelableHighlight implements Parcelable {
    private final float xValue;
    private final float yValue;
    private final int dataSetIndex;

    public ParcelableHighlight(Highlight highlight) {
        this.xValue = highlight.getX();
        this.yValue = highlight.getY();
        this.dataSetIndex = highlight.getDataSetIndex();
    }

    ParcelableHighlight(Parcel parcel) {
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