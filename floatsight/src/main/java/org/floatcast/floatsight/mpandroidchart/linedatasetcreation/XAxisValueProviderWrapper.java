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

package org.floatcast.floatsight.mpandroidchart.linedatasetcreation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static org.floatcast.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties.METRIC;

@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
public class XAxisValueProviderWrapper {
    public static final String BUNDLE_KEY = "XAxisValueProviderWrapper";
    @Retention(SOURCE)
    @StringDef({TIME, DISTANCE})
    public @interface XAxisMetric {}
    public static final String TIME = "TIME";
    public static final String DISTANCE = "DISTANCE";

    public String unitSystem;
    public TrackPointValueProvider xAxisValueProvider;

    public XAxisValueProviderWrapper() {
        xAxisValueProvider = TrackPointValueProvider.TIME_VALUE_PROVIDER;
        unitSystem = METRIC;
    }

    public XAxisValueProviderWrapper(@XAxisMetric String type, @ChartDataSetProperties.UnitSystem String unitSystem) {
        this();
        if(type == null) {
            return;
        }
        this.unitSystem = unitSystem;
        update(type);
    }

    public boolean isTime() {
        return xAxisValueProvider == TrackPointValueProvider.TIME_VALUE_PROVIDER;
    }

    public boolean isDistance() {
        return xAxisValueProvider == TrackPointValueProvider.METRIC_DISTANCE_VALUE_PROVIDER;
    }

    public void setTime() {
        xAxisValueProvider = TrackPointValueProvider.TIME_VALUE_PROVIDER;
    }

    public void setDistance() {
        xAxisValueProvider = unitSystem.equals(METRIC) ?
                TrackPointValueProvider.METRIC_DISTANCE_VALUE_PROVIDER :
                TrackPointValueProvider.IMPERIAL_DISTANCE_VALUE_PROVIDER;
    }

    public void setUnitSystem(@ChartDataSetProperties.UnitSystem String unitSystem) {
        this.unitSystem = unitSystem;
        String type = xAxisValueProvider == TrackPointValueProvider.TIME_VALUE_PROVIDER ? TIME : DISTANCE;
        update(type);
    }

    private void update(@XAxisMetric String type) {
        if(type.equals(TIME)) {
            xAxisValueProvider = TrackPointValueProvider.TIME_VALUE_PROVIDER;
        } else {
            xAxisValueProvider = unitSystem.equals(METRIC) ?
                    TrackPointValueProvider.METRIC_DISTANCE_VALUE_PROVIDER :
                    TrackPointValueProvider.IMPERIAL_DISTANCE_VALUE_PROVIDER;
        }
    }

    public String getStringValue() {
        return xAxisValueProvider == TrackPointValueProvider.TIME_VALUE_PROVIDER ? TIME : DISTANCE;
    }
}
