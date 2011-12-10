package co.geomati.netcdf;

import ucar.ma2.DataType;

public class DimensionDescriptor {

	private String name;
	private int size;
	private DataType type;

	public DimensionDescriptor(String name, int size, DataType type) {
		super();
		this.name = name;
		this.size = size;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public DataType getType() {
		return type;
	}
}
