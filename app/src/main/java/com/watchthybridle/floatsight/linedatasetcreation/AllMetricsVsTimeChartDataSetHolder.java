package com.watchthybridle.floatsight.linedatasetcreation;

import android.content.Context;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;

import java.util.ArrayList;
import java.util.List;

public class AllMetricsVsTimeChartDataSetHolder {

    private List<ChartDataSetProperties> dataSetPropertiesList;

    public AllMetricsVsTimeChartDataSetHolder(Context context, FlySightTrackData flySightTrackData) {
        dataSetPropertiesList = new ArrayList<>();
        dataSetPropertiesList.add(ChartDataSetProperties.getVerticalVelocityProperties(context, flySightTrackData));
        dataSetPropertiesList.add(ChartDataSetProperties.getHorizontalVelocityProperties(context, flySightTrackData));
        dataSetPropertiesList.add(ChartDataSetProperties.getAltitudeVsTimeProperties(context, flySightTrackData));
        dataSetPropertiesList.add(ChartDataSetProperties.getDistanceVsTimeProperties(context, flySightTrackData));
        dataSetPropertiesList.add(ChartDataSetProperties.getGlideProperties(context, flySightTrackData));
    }

    public List<ChartDataSetProperties> getDataSetPropertiesList() {
        return dataSetPropertiesList;
    }

    public LineData getLineDataOuterGraph() {
        List<ILineDataSet> lineDataSets = new ArrayList<>();
        for (ChartDataSetProperties dataSetProperties : dataSetPropertiesList.subList(0, 4)) {
            lineDataSets.add(dataSetProperties.iLineDataSet);
        }
        return new LineData(lineDataSets);
    }

    public LineData getLineDataGlideGraph() {
        List<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(dataSetPropertiesList.get(4).iLineDataSet);
        return new LineData(lineDataSets);
    }
}
