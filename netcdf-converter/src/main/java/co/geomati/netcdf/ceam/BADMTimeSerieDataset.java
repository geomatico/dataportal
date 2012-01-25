package co.geomati.netcdf.ceam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.geomati.netcdf.ConverterException;
import co.geomati.netcdf.IcosDomain;
import co.geomati.netcdf.Institution;
import co.geomati.netcdf.TimeUnit;
import co.geomati.netcdf.dataset.Dataset;
import co.geomati.netcdf.dataset.DatasetDoubleVariable;
import co.geomati.netcdf.dataset.DatasetVariable;
import co.geomati.netcdf.dataset.TimeSerie;

public class BADMTimeSerieDataset implements Dataset, TimeSerie {

	private Variable variable;
	private TimeUnit timeUnits;
	private Date referenceDate;
	private List<Integer> timeStamps;
	private String name;

	public BADMTimeSerieDataset(String baseName, Variable main,
			Variable timeVariable) throws ConverterException {
		this.variable = main;
		setTimeInfo(timeVariable);
		this.name = baseName + "_" + variable.getName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IcosDomain getIcosDomain() {
		return IcosDomain.ENVIRONMENT;
	}

	@Override
	public Institution getInstitution() {
		return Institution.CEAM;
	}

	private void setTimeInfo(Variable timeVariable) throws ConverterException {
		String units = timeVariable.getUnits();
		if (units.equals("YYYY")) {
			timeUnits = TimeUnit.COMMON_YEAR;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			try {
				referenceDate = sdf.parse("0000");
			} catch (ParseException e) {
				throw new RuntimeException("bug");
			}
			ArrayList<Integer> times = new ArrayList<Integer>();
			ArrayList<Object> values = timeVariable.getValues();
			for (Object object : values) {
				times.add(((Number) object).intValue());
			}
			timeStamps = times;
		} else if (units.equals("DOY/YYYY")) {
			timeUnits = TimeUnit.DAYS;
			ArrayList<Integer> times = new ArrayList<Integer>();
			ArrayList<Object> values = timeVariable.getValues();
			Date referenceYear = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			for (Object object : values) {
				String dayYear = (String) object;
				String[] dayYearArray = dayYear.split("/");
				int day = Integer.parseInt(dayYearArray[0]);
				Date year;
				try {
					year = sdf.parse(dayYearArray[1]);
				} catch (ParseException e) {
					throw new ConverterException("invalid year forma: "
							+ dayYearArray[1]);
				}
				if (referenceYear == null) {
					referenceYear = year;
				} else if (!referenceYear.equals(year)) {
					throw new RuntimeException("Wrong reference "
							+ "year for variable: " + timeVariable.getName());
				}
				times.add(day);
			}
			timeStamps = times;

			referenceDate = referenceYear;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public DatasetVariable[] getMainVariables() {
		return new DatasetVariable[] { new DatasetDoubleVariable() {

			@Override
			public String getUnits() {
				return variable.getUnits();
			}

			@Override
			public String getStandardName() {
				return variable.getName();
			}

			@Override
			public String getName() {
				return variable.getName();
			}

			@Override
			public String getLongName() {
				return variable.getLongName();
			}

			@Override
			public Number getFillValue() {
				return null;
			}

			@Override
			public List<Double> getData() {
				ArrayList<Double> ret = new ArrayList<Double>();
				for (Object sample : variable.getValues()) {
					ret.add((Double) sample);
				}

				return ret;
			}
		} };
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

}
