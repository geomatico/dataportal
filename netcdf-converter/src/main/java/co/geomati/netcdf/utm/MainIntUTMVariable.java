package co.geomati.netcdf.utm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import co.geomati.netcdf.dataset.DatasetIntVariable;

public class MainIntUTMVariable extends MainUTMVariable implements
		DatasetIntVariable {

	private SimpleDateFormat sf = null;
	private ArrayList<Integer> data = new ArrayList<Integer>();

	public MainIntUTMVariable(String name, String standardName,
			String longName, String units, SimpleDateFormat sf) {
		super(name, standardName, longName, units);

		this.sf = sf;
	}

	@Override
	public void addSample(String sample) {
		try {
			data.add((int) (sf.parse(sample).getTime() / 1000.0));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Integer> getData() {
		return data;
	}
}
