package co.geomati.netcdf.utm;

import java.util.ArrayList;
import java.util.List;

import co.geomati.netcdf.dataset.DatasetDoubleVariable;

public class MainDoubleUTMVariable extends MainUTMVariable implements
		DatasetDoubleVariable {

	private ArrayList<Double> data = new ArrayList<Double>();

	public MainDoubleUTMVariable(String name, String standardName,
			String longName, String units) {
		super(name, standardName, longName, units);
	}

	@Override
	public void addSample(String sample) {
		data.add(Double.parseDouble(sample));
	}

	@Override
	public List<Double> getData() {
		return data;
	}
}
