package co.geomati.netcdf.ceam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import co.geomati.netcdf.TimeSerieDataset;
import co.geomati.netcdf.TimeUnit;

public class CEAMDataset extends AbstractCEAMDataset implements
		TimeSerieDataset {

	private Variable variable;
	private ArrayList<Integer> timestamps;

	public CEAMDataset(Variable variable, ArrayList<Integer> seconds) {
		this.variable = variable;
		this.timestamps = seconds;
	}

	@Override
	public String getVariableName() {
		return variable.getName();
	}

	@Override
	public String getVariableLongName() {
		return variable.getLongName();
	}

	@Override
	public String getVariableStandardName() {
		return variable.getName();
	}

	@Override
	public String getVariableUnits() {
		return variable.getUnits();
	}

	@Override
	public TimeUnit getTimeUnits() {
		return TimeUnit.SECOND;
	}

	@Override
	public Date getReferenceDate() {
		return new Date(0);
	}

	@Override
	public DataType getVariableType() {
		return DataType.DOUBLE;
	}

	@Override
	public List<Integer> getTimeStamps() {
		return timestamps;
	}

	@Override
	public Array getData() {
		int timeCount = getTimeStamps().size();
		ArrayDouble a = new ArrayDouble.D1(timeCount);
		Index ima = a.getIndex();
		for (int j = 0; j < timeCount; j++) {
			Double value = (Double) variable.getValues().get(j);
			a.setDouble(ima.set(j), value);
		}

		return a;
	}

	@Override
	public double getFillValue() {
		return -9999;
	}
}
