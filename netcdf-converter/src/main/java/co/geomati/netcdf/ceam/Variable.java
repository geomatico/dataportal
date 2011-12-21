package co.geomati.netcdf.ceam;

import java.util.ArrayList;

class Variable {
	private String name;
	private String longName;
	private String units;
	private ArrayList<Object> values;

	public Variable(String name, String longName, String units) {
		super();
		this.name = name;
		this.longName = longName;
		this.units = units;
	}

	public void setValues(ArrayList<Object> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		String ret = name + "(" + longName + ")(" + units + "):";
		for (Object value : values) {
			ret += value + ",";
		}
		return ret;
	}

	public String getName() {
		return name;
	}

	public String getLongName() {
		return longName;
	}

	public String getUnits() {
		return units;
	}

	public ArrayList<Object> getValues() {
		return values;
	}

	public boolean isEmpty() {
		return values != null && values.size() > 0;
	}
}