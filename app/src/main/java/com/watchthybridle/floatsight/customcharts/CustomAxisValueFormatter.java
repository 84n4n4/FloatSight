package com.watchthybridle.floatsight.customcharts;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

class CustomAxisValueFormatter implements IAxisValueFormatter {
    private DecimalFormat mFormat;

    CustomAxisValueFormatter(DecimalFormat decimalFormat) {
        mFormat = decimalFormat;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value);
    }
}