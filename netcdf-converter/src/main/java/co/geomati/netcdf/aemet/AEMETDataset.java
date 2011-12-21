package co.geomati.netcdf.aemet;

import java.awt.geom.Point2D;
import java.util.Date;
import java.util.List;

import co.geomati.netcdf.IcosDomain;
import co.geomati.netcdf.Institution;
import co.geomati.netcdf.TimeUnit;
import co.geomati.netcdf.dataset.Dataset;
import co.geomati.netcdf.dataset.DatasetDoubleVariable;
import co.geomati.netcdf.dataset.DatasetVariable;
import co.geomati.netcdf.dataset.GeoreferencedStation;
import co.geomati.netcdf.dataset.TimeSerie;

public class AEMETDataset implements Dataset, GeoreferencedStation, TimeSerie {

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
	public List<Integer> getTimeStamps() {
		return timeStamps;
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
	public DatasetVariable getMainVariable() {
		return new DatasetDoubleVariable() {

			@Override
			public String getUnits() {
				return variableUnits;
			}

			@Override
			public String getStandardName() {
				return variableName;
			}

			@Override
			public String getName() {
				return variableName;
			}

			@Override
			public String getLongName() {
				return variableLongName;
			}

			@Override
			public Number getFillValue() {
				return -99999.99;
			}

			@Override
			public List<Double> getData() {
				return values;
			}
		};
	}

	@Override
	public int getStationCount() {
		return 1;
	}

	@Override
	public List<Point2D> getStationPositions() {
		return stationPosition;
	}

}
