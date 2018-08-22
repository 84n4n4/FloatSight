package com.watchthybridle.floatsight.linedatasetcreation;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ChartDataSetHolder {
	List<DataSetWithFormat> dataSetsWithFormats = new ArrayList<>();

	public ChartDataSetHolder() {
	}

	public void addDataSetWithFormat(DataSetWithFormat dataSetWithFormat) {
		dataSetsWithFormats.add(dataSetWithFormat);
	}

	public LineData getLineData() {
		return new LineData(getLineDataSets());
	}

	public List<DecimalFormat> getFormats() {
		List<DecimalFormat> decimalFormats = new ArrayList<>();
		for (DataSetWithFormat dataSetWithFormat : dataSetsWithFormats) {
			decimalFormats.add(dataSetWithFormat.decimalFormat);
		}
		return decimalFormats;
	}

	private List<ILineDataSet> getLineDataSets() {
		List<ILineDataSet> lineDataSets = new ArrayList<>();
		for (DataSetWithFormat dataSetWithFormat : dataSetsWithFormats) {
			lineDataSets.add(dataSetWithFormat.dataSet);
		}
		return lineDataSets;
	}
}
