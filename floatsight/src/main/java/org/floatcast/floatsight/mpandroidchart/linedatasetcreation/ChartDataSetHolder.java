package org.floatcast.floatsight.mpandroidchart.linedatasetcreation;

import android.content.Context;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import org.floatcast.floatsight.data.FlySightTrackData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartDataSetHolder {
    public static final Integer VERT_VELOCITY = 0;
    public static final Integer HOR_VELOCITY = 1;
    public static final Integer ALTITUDE = 2;
    public static final Integer GLIDE = 3;
    public static final Integer DISTANCE = 4;
    public static final Integer TIME = 5;

    private static final List<Integer> OUTER_GRAPH_METRICS = Arrays.asList(ALTITUDE, VERT_VELOCITY, HOR_VELOCITY);
    private static final List<Integer> INNER_GRAPH_METRICS = Arrays.asList(GLIDE);

    private List<ChartDataSetProperties> dataSetPropertiesList;
    private FlySightTrackData flySightTrackData;

    public ChartDataSetHolder(Context context,
                              FlySightTrackData flySightTrackData,
                              TrackPointValueProvider xAxisValueProvider,
                              CappedTrackPointValueProvider glideYValueProvider,
                              @ChartDataSetProperties.UnitSystem String unitSystem) {
        this.flySightTrackData = flySightTrackData;
        dataSetPropertiesList = new ArrayList<>();
        dataSetPropertiesList.add(new ChartDataSetProperties.VerticalVelocityDataSetProperties(xAxisValueProvider, unitSystem));
        dataSetPropertiesList.add(new ChartDataSetProperties.HorizontalVelocityDataSetProperties(xAxisValueProvider, unitSystem));
        dataSetPropertiesList.add(new ChartDataSetProperties.AltitudeDataSetProperties(xAxisValueProvider, unitSystem));
        dataSetPropertiesList.add(new ChartDataSetProperties.GlideDataSetProperties(xAxisValueProvider, glideYValueProvider));
        dataSetPropertiesList.add(new ChartDataSetProperties.DistanceDataSetProperties(xAxisValueProvider, unitSystem));
        dataSetPropertiesList.add(new ChartDataSetProperties.TimeDataSetProperties(xAxisValueProvider));
        for(ChartDataSetProperties dataSetProperties : dataSetPropertiesList) {
            dataSetProperties.initLineData(context, flySightTrackData);
        }
    }

    public FlySightTrackData getFlySightTrackData() {
        return flySightTrackData;
    }

    public List<ChartDataSetProperties> getDataSetPropertiesList() {
        return dataSetPropertiesList;
    }


    public LineData getLineDataOuterGraph() {
        return getLineDataForGraph(OUTER_GRAPH_METRICS);
    }

    public LineData getLineDataGlideGraph() {
        return getLineDataForGraph(INNER_GRAPH_METRICS);
    }

    private LineData getLineDataForGraph(List<Integer> metrics) {
        List<ILineDataSet> lineDataSets = new ArrayList<>();
        for (int metric : metrics) {
            lineDataSets.add(dataSetPropertiesList.get(metric).iLineDataSet);
        }
        return new LineData(lineDataSets);
    }
}
