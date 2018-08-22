package com.watchthybridle.floatsight.linedatasetcreation;

import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;

public class DataSetWithFormat {
	public LineDataSet dataSet;
	public DecimalFormat decimalFormat;

	public DataSetWithFormat(LineDataSet lineDataSet, DecimalFormat decimalFormat) {
		this.dataSet = lineDataSet;
		this.decimalFormat = decimalFormat;
	}
}
