package com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class XAxisValueProviderWrapper {
    public static final String BUNDLE_KEY = "XAxisValueProviderWrapper";
    @Retention(SOURCE)
    @StringDef({TIME, DISTANCE})
    public @interface XAxisMetric {}
    public static final String TIME = "TIME";
    public static final String DISTANCE = "DISTANCE";

    public TrackPointValueProvider xAxisValueProvider;

    public XAxisValueProviderWrapper() {
        xAxisValueProvider = TrackPointValueProvider.TIME_VALUE_PROVIDER;
    }

    public XAxisValueProviderWrapper(@XAxisMetric String type) {
        this();
        if(type != null) {
            xAxisValueProvider = type.equals(TIME) ?
                    TrackPointValueProvider.TIME_VALUE_PROVIDER : TrackPointValueProvider.DISTANCE_VALUE_PROVIDER;
        }
    }

    public boolean isTime() {
        return xAxisValueProvider == TrackPointValueProvider.TIME_VALUE_PROVIDER;
    }

    public boolean isDistance() {
        return xAxisValueProvider == TrackPointValueProvider.DISTANCE_VALUE_PROVIDER;
    }

    public void setTime() {
        xAxisValueProvider = TrackPointValueProvider.TIME_VALUE_PROVIDER;
    }

    public void setDistance() {
        xAxisValueProvider = TrackPointValueProvider.DISTANCE_VALUE_PROVIDER;
    }

    public String getStringValue() {
        return xAxisValueProvider == TrackPointValueProvider.TIME_VALUE_PROVIDER ? TIME : DISTANCE;
    }
}
