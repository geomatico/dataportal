package co.geomati.netcdf.aemet;

import java.awt.geom.Point2D;
import java.util.Date;
import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.nc2.Attribute;
import co.geomati.netcdf.IcosDomain;
import co.geomati.netcdf.Institution;
import co.geomati.netcdf.StationDataset;
import co.geomati.netcdf.TimeUnit;

public class AEMETDataset implements StationDataset {

	private List<Point2D> stationPosition;
	private String variableUnits;
	private String variableLongName;
	private String variableName;
	private List<Double> values;
	private List<Integer> timeStamps;
	private Date referenceDate;
	private TimeUnit timeUnits;

	public AEMETDataset(List<Point2D> stationPosition, String variableUnits,
			String variableLongName, String variableName, List<Double> values,
			List<Integer> timeStamps, Date referenceDate, TimeUnit timeUnits) {
		super();
		this.stationPosition = stationPosition;
		this.variableUnits = variableUnits;
		this.variableLongName = variableLongName;
		this.variableName = variableName;
		this.values = values;
		this.timeStamps = timeStamps;
		this.referenceDate = referenceDate;
		this.timeUnits = timeUnits;
	}

	@Override
	public TimeUnit getTimeUnits() {
		return timeUnits;
	}

	@Override
	public Date getReferenceDate() {
		return referenceDate;
	}

	@Override
	public DataType getVariableType() {
		return DataType.DOUBLE;
	}

	@Override
	public List<Integer> getTimeStamps() {
		return timeStamps;
	}

	@Override
	public Array getData() {
		int timeSize = getTimeStamps().size();
		int stationSize = getPositions().size();
		ArrayDouble a = new ArrayDouble.D2(timeSize, stationSize);
		Index ima = a.getIndex();
		for (int i = 0; i < timeSize; i++) {
			for (int j = 0; j < stationSize; j++) {
				a.setDouble(ima.set(i, j), values.get(j * stationSize + i));
			}
		}

		return a;
	}

	@Override
	public Attribute getFillValueAttribute() {
		return new Attribute("_FillValue", -9999);
	}

	@Override
	public IcosDomain getIcosDomain() {
		return IcosDomain.ATMOSPHERE;
	}

	@Override
	public Institution getInstitution() {
		return Institution.AEMET;
	}

	@Override
	public String getVariableName() {
		return variableName;
	}

	@Override
	public String getVariableLongName() {
		return variableLongName;
	}

	@Override
	public String getVariableStandardName() {
		return variableName;
	}

	@Override
	public String getVariableUnits() {
		return variableUnits;
	}

	@Override
	public List<Point2D> getPositions() {
		return stationPosition;
	}

}
