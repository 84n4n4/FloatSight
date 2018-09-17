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
import android.support.annotation.IntDef;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
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

    ParcelableLineDataSetVisibility(Parcel parcel) {
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