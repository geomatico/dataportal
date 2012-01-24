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
	private List<Integer> timeStamps;
	private Date referenceDate;
	private TimeUnit timeUnits;
	private DatasetVariable variable;

	public AEMETDataset(List<Point2D> stationPosition, String variableUnits,
			String variableLongName, String variableName, List<Double> values,
			List<Integer> timeStamps, Date referenceDate, TimeUnit timeUnits) {
		Variable var = new Variable(variableUnits, variableLongName,
				variableName, values);
		init(stationPosition, timeStamps, referenceDate, timeUnits, var);
	}

	private void init(List<Point2D> stationPosition, List<Integer> timeStamps,
			Date referenceDate, TimeUnit timeUnits, DatasetVariable var) {
		this.stationPosition = stationPosition;
		this.variable = var;
		this.timeStamps = timeStamps;
		this.referenceDate = referenceDate;
		this.timeUnits = timeUnits;
	}

	public AEMETDataset(List<Point2D> stationPosition, String variableUnits,
			String variableLongName, String variableName,
			String meanDescription, List<Double> values,
			List<Integer> ndValues, List<Double> sdValues,
			List<Integer> timeStamps, Date referenceDate, TimeUnit timeUnits) {
		this(stationPosition, variableUnits, variableLongName, variableName,
				values, timeStamps, referenceDate, timeUnits);
		MeanVariable var = new MeanVariable(variableUnits, variableLongName,
				variableName, meanDescription, values, ndValues, sdValues);
		init(stationPosition, timeStamps, referenceDate, timeUnits, var);
	}

	@Override
	public String getName() {
		return variable.getName();
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
	public DatasetVariable[] getMainVariables() {
		return new DatasetVariable[] { variable };
	}

	@Override
	public int getStationCount() {
		return 1;
	}

	@Override
	public List<Point2D> getStationPositions() {
		return stationPosition;
	}

	private static abstract class AbstractVariable implements
			DatasetDoubleVariable {
		protected String variableUnits;
		protected String variableLongName;
		protected String variableName;
		protected List<Double> values;

		public AbstractVariable(String variableUnits, String variableLongName,
				String variableName, List<Double> values) {
			super();
			this.variableUnits = variableUnits;
			this.variableLongName = variableLongName;
			this.variableName = variableName;
			this.values = values;
		}

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

	}

	private static final class Variable extends AbstractVariable implements
			DatasetDoubleVariable {

		public Variable(String variableUnits, String variableLongName,
				String variableName, List<Double> values) {
			super(variableUnits, variableLongName, variableName, values);
		}

	}

	private static final class MeanVariable extends AbstractVariable implements
			DatasetDoubleMeanVariable {

		private String meanDescription;
		private List<Integer> ndValues;
		private List<Double> sdValues;

		public MeanVariable(String variableUnits, String variableLongName,
				String variableName, String meanDescription,
				List<Double> values, List<Integer> ndValues,
				List<Double> sdValues) {
			super(variableUnits, variableLongName, variableName, values);
			this.meanDescription = meanDescription;
			this.ndValues = ndValues;
			this.sdValues = sdValues;
		}

		@Override
		public String getMeanDescription() {
			return meanDescription;
		}

		@Override
		public Number getNDFillValue() {
			return 0;
		}

		@Override
		public Number getSDFillValue() {
			return -999.99;
		}

		@Override
		public List<Double> getSDData() {
			return sdValues;
		}

		@Override
		public List<Integer> getNDData() {
			return ndValues;
		}
	}

}
