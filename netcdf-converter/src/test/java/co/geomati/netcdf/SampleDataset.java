package co.geomati.netcdf;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.nc2.Attribute;
import co.geomati.netcdf.ceam.AbstractCEAMDataset;

public class SampleDataset extends AbstractCEAMDataset implements
		StationDataset {

	public SampleDataset() {
		super("http://www.ceam.es");
	}

	@Override
	public String getVariableName() {
		return "temperature";
	}

	@Override
	public String getVariableLongName() {
		return "Air temperature at see level";
	}

	@Override
	public String getVariableStandardName() {
		return "air_temperature";
	}

	@Override
	public String getVariableUnits() {
		return "grados jarrrrl";
	}

	@Override
	public TimeUnit getTimeUnits() {
		return TimeUnit.SECOND;
	}

	@Override
	public Date getReferenceDate() {
		return new Date();
	}

	@Override
	public DataType getVariableType() {
		return DataType.DOUBLE;
	}

	@Override
	public List<Point2D> getPositions() {
		ArrayList<Point2D> ret = new ArrayList<Point2D>();
		ret.add(new Point2D.Double(10, 10));
		ret.add(new Point2D.Double(20, 20));
		ret.add(new Point2D.Double(30, 30));
		return ret;
	}

	@Override
	public List<Integer> getTimeStamps() {
		List<Integer> ret = new ArrayList<Integer>();
		ret.add(10);
		ret.add(20);
		ret.add(30);
		ret.add(40);
		ret.add(50);
		ret.add(60);
		return ret;
	}

	@Override
	public Array getData() {
		int timeSize = getTimeStamps().size();
		int stationSize = getPositions().size();
		ArrayDouble a = new ArrayDouble.D2(timeSize, stationSize);
		Index ima = a.getIndex();
		for (int i = 0; i < timeSize; i++) {
			for (int j = 0; j < stationSize; j++) {
				a.setDouble(ima.set(i, j), 30 + i);
			}
		}

		return a;
	}

	@Override
	public Attribute getFillValueAttribute() {
		return null;
	}

}