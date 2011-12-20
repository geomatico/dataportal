package co.geomati.netcdf;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

public class Converter {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss:S");

	static {
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT0"));
	}

	public static void convert(DatasetConversion conversion) {
		Report r = new Report();
		for (int i = 0; i < conversion.getDatasetCount(); i++) {
			try {
				r.addRecord();
				Dataset dataset = conversion.getDataset(i);
				r.setDatasetName(dataset.getVariableStandardName());
				File tempFile = new File(System.getProperty("java.io.tmpdir")
						+ "/" + conversion.getOutputFileName(dataset) + ".nc");
				convert(dataset, tempFile);
			} catch (RuntimeException e) {
				r.datasetError(e);
			} catch (ConverterException e) {
				r.datasetError(e);
			}
		}

		System.out.println(r);
	}

	static void convert(Dataset dataset, File file) throws ConverterException {

		Properties props = new Properties();
		BufferedInputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(new File(
					"conversion.properties")));
		} catch (FileNotFoundException e) {
			throw new ConverterException("Cannot find 'conversion.properties'",
					e);
		}

		try {
			props.load(is);
		} catch (IOException e) {
			throw new ConverterException("Error reading properties", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// ignore
			}
		}

		NetcdfFileWriteable nc;
		try {
			nc = NetcdfFileWriteable.createNew(file.getAbsolutePath(), false);
		} catch (IOException e) {
			throw new ConverterException("Cannot create the file", e);
		}

		/*
		 * Global metadata
		 */
		nc.addGlobalAttribute("id", UUID.randomUUID().toString());
		nc.addGlobalAttribute("naming_authority", "UUID");
		nc.addGlobalAttribute("standard_name_vocabulary",
				props.getProperty("vocabulary_url"));
		nc.addGlobalAttribute("icos_domain", dataset.getIcosDomain().toString());
		nc.addGlobalAttribute("conventions", "CF-1.5");
		nc.addGlobalAttribute("Metadata_Conventions",
				"Unidata Dataset Discovery v1.0");
		nc.addGlobalAttribute("institution", dataset.getInstitution().getName());
		nc.addGlobalAttribute("creator_url", dataset.getInstitution().getUrl());

		if (dataset instanceof StationDataset) {
			addStation(nc, (StationDataset) dataset);
		} else if (dataset instanceof TimeSerieDataset) {
			addTimeSerie(nc, (TimeSerieDataset) dataset);
		} else if (dataset instanceof TrajectoryDataset) {
			nc.addGlobalAttribute("cdm_data_type",
					CDMDataType.TRAJECTORY.toString());
			addTrajectory(nc, (TrajectoryDataset) dataset);
		} else if (dataset instanceof GridDataset) {
			nc.addGlobalAttribute("cdm_data_type",
					CDMDataType.TRAJECTORY.toString());
			addGrid(nc, (GridDataset) dataset);
		} else {
			throw new ConverterException("Unsupported Dataset implementation");
		}

		try {
			nc.close();
		} catch (IOException e) {
			throw new ConverterException("Cannot close created nc file", e);
		}
	}

	/**
	 * Write a timeseries using a coordinate variable called "time" to store the
	 * time
	 * 
	 * @param nc
	 * @param dataset
	 * @throws ConverterException
	 */
	private static void addTimeSerie(NetcdfFileWriteable nc,
			TimeSerieDataset dataset) throws ConverterException {
		writeTimeBox(nc, dataset);
		// time dimension variable
		Variable time = createTimeVariable(nc, dataset);
		/*
		 * TODO vertical coordinate
		 */

		/*
		 * Add main variable
		 */
		ArrayList<Dimension> mainVarDimensions = new ArrayList<Dimension>();
		mainVarDimensions.add(time.getDimension(0));
		String variableName = dataset.getVariableName();
		Variable mainVar = nc.addVariable(variableName,
				dataset.getVariableType(),
				mainVarDimensions.toArray(new Dimension[0]));
		mainVar.addAttribute(new Attribute("long_name", dataset
				.getVariableLongName()));
		mainVar.addAttribute(new Attribute("standard_name", dataset
				.getVariableStandardName()));
		mainVar.addAttribute(new Attribute("units", dataset.getVariableUnits()));
		Attribute fillValueAttribute = dataset.getFillValueAttribute();
		if (fillValueAttribute != null) {
			mainVar.addAttribute(fillValueAttribute);
		}

		try {
			nc.create();

			/*
			 * Write time
			 */
			writeTimeValues(nc, dataset, time);

			/*
			 * write main variable
			 */
			Array a = dataset.getData();
			try {
				nc.write(mainVar.getName(), a);
			} catch (InvalidRangeException e) {
				throw new ConverterException("Too many data on main variable",
						e);
			}

		} catch (IOException e) {
			throw new ConverterException("Cannot create netcdf file", e);
		}

	}

	private static void addStation(NetcdfFileWriteable nc,
			StationDataset dataset) throws ConverterException {
		writeTimeBox(nc, dataset);

		List<Point2D> stationPositions = dataset.getPositions();
		Rectangle2D bbox = getBBox(stationPositions);
		nc.addGlobalAttribute("geospatial_lat_min", bbox.getMinY());
		nc.addGlobalAttribute("geospatial_lat_max", bbox.getMaxY());
		nc.addGlobalAttribute("geospatial_lon_min", bbox.getMinX());
		nc.addGlobalAttribute("geospatial_lon_max", bbox.getMaxX());
		nc.addGlobalAttribute("cdm_data_type", CDMDataType.STATION.toString());

		// time dimension variable
		Variable time = createTimeVariable(nc, dataset);

		// Position dimension
		Dimension stationDimension = nc.addDimension("station",
				stationPositions.size());
		// Position variables
		Variable lat = nc.addVariable("lat", DataType.DOUBLE,
				new Dimension[] { stationDimension });
		lat.addAttribute(new Attribute("axis", "Y"));
		lat.addAttribute(new Attribute("standard_name", "latitude"));
		lat.addAttribute(new Attribute("units", "degrees_north"));
		Variable lon = nc.addVariable("lon", DataType.DOUBLE,
				new Dimension[] { stationDimension });
		lon.addAttribute(new Attribute("axis", "X"));
		lon.addAttribute(new Attribute("standard_name", "longitude"));
		lon.addAttribute(new Attribute("units", "degrees_east"));

		/*
		 * TODO vertical coordinate
		 */

		/*
		 * Add main variable
		 */
		ArrayList<Dimension> mainVarDimensions = new ArrayList<Dimension>();
		if (time != null) {
			mainVarDimensions.add(time.getDimension(0));
		}
		mainVarDimensions.add(stationDimension);
		String variableName = dataset.getVariableName();
		Variable mainVar = nc.addVariable(variableName,
				dataset.getVariableType(),
				mainVarDimensions.toArray(new Dimension[0]));
		mainVar.addAttribute(new Attribute("coordinates", "lat lon"));
		mainVar.addAttribute(new Attribute("long_name", dataset
				.getVariableLongName()));
		mainVar.addAttribute(new Attribute("standard_name", dataset
				.getVariableStandardName()));
		mainVar.addAttribute(new Attribute("units", dataset.getVariableUnits()));

		try {
			nc.create();

			/*
			 * Write time
			 */
			writeTimeValues(nc, dataset, time);

			/*
			 * write positions
			 */
			try {
				nc.write(
						lat.getName(),
						get1Double(stationPositions,
								new DoubleSampleGetter<Point2D>() {

									@Override
									public double get(Point2D t) {
										return t.getY();
									}
								}));
				nc.write(
						lon.getName(),
						get1Double(stationPositions,
								new DoubleSampleGetter<Point2D>() {

									@Override
									public double get(Point2D t) {
										return t.getX();
									}
								}));
			} catch (InvalidRangeException e) {
				throw new ConverterException("The specified positions exceed "
						+ "the number of stations", e);
			}

			/*
			 * write main variable
			 */
			Array a = dataset.getData();
			try {
				nc.write(mainVar.getName(), a);
			} catch (InvalidRangeException e) {
				throw new ConverterException("Too many data on main variable",
						e);
			}
		} catch (IOException e) {
			throw new ConverterException("Cannot create netcdf file", e);
		}
	}

	private static void writeTimeValues(NetcdfFileWriteable nc,
			TimeSerieDataset dataset, Variable time) throws IOException {
		if (time != null) {
			try {
				nc.write(
						time.getName(),
						get1Int(dataset.getTimeStamps(),
								new IntSampleGetter<Integer>() {

									@Override
									public int get(Integer t) {
										return t;
									}
								}));
			} catch (InvalidRangeException e) {
				throw new RuntimeException("Bug. This should not "
						+ "happen since time is unlimited", e);
			}
		}
	}

	private static Variable createTimeVariable(NetcdfFileWriteable nc,
			TimeSerieDataset dataset) {
		Variable time = null;
		List<Integer> times = dataset.getTimeStamps();
		Date referenceDate = dataset.getReferenceDate();
		TimeUnit timeUnit = dataset.getTimeUnits();
		if (times != null) {
			Dimension timeDim = nc.addUnlimitedDimension("time");
			time = nc.addVariable("time", DataType.INT,
					new Dimension[] { timeDim });
			time.addAttribute(new Attribute("units", timeUnit.toString()
					+ " since " + dateFormat.format(referenceDate)));
			time.addAttribute(new Attribute("axis", "T"));
		}

		return time;
	}

	private static void writeTimeBox(NetcdfFileWriteable nc,
			TimeSerieDataset dataset) {
		List<Integer> times = dataset.getTimeStamps();
		Date referenceDate = dataset.getReferenceDate();
		TimeUnit timeUnit = dataset.getTimeUnits();
		if (times != null) {
			long[] timeBox = getTimeBox(times, referenceDate, timeUnit);
			DateTimeFormatter parser = ISODateTimeFormat.dateTime();
			parser = parser.withZoneUTC();
			nc.addGlobalAttribute("time_coverage_start",
					parser.print(timeBox[0]));
			nc.addGlobalAttribute("time_coverage_end", parser.print(timeBox[1]));
		}
	}

	private static long[] getTimeBox(List<Integer> times, Date referenceDate,
			TimeUnit unit) {
		long minTime = Long.MAX_VALUE;
		long maxTime = Long.MIN_VALUE;
		for (Integer magnitude : times) {
			long time = referenceDate.getTime()
					+ getMilliseconds(magnitude, unit);
			if (time < minTime) {
				minTime = time;
			}
			if (time > maxTime) {
				maxTime = time;
			}
		}

		return new long[] { maxTime, minTime };
	}

	private static long getMilliseconds(Integer magnitude, TimeUnit unit) {
		if (unit == TimeUnit.SECOND) {
			return magnitude * 1000L;
		} else if (unit == TimeUnit.MINUTE) {
			return magnitude * 1000L * 60;
		} else if (unit == TimeUnit.HOUR) {
			return magnitude * 1000L * 60 * 60;
		} else if (unit == TimeUnit.DAYS) {
			return magnitude * 1000L * 60 * 60 * 24;
		} else if (unit == TimeUnit.COMMON_YEAR) {
			return magnitude * 1000L * 60 * 60 * 24 * 365;
		} else {
			throw new RuntimeException("Bug");
		}
	}

	private static Rectangle2D getBBox(List<Point2D> points) {
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		for (Point2D p : points) {
			double x = p.getX();
			double y = p.getY();
			if (x < minX) {
				minX = x;
			}
			if (x > maxX) {
				maxX = x;
			}
			if (y < minY) {
				minY = y;
			}
			if (y > maxY) {
				maxY = y;
			}
		}

		double width = maxX - minX;
		double height = maxY - minY;
		return new Rectangle2D.Double(minX, minY, width, height);
	}

	private static void addTrajectory(NetcdfFileWriteable nc, Dataset dataset) {
		throw new UnsupportedOperationException();
	}

	private static void addGrid(NetcdfFileWriteable nc, Dataset dataset) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public static <T> ArrayDouble get1Double(List<T> list,
			DoubleSampleGetter<T> getter) throws IOException,
			InvalidRangeException {
		ArrayDouble A = new ArrayDouble.D1(list.size());
		Index ima = A.getIndex();
		for (int i = 0; i < list.size(); i++) {
			A.setDouble(ima.set(i), getter.get(list.get(i)));
		}
		return A;
	}

	public static <T> ArrayInt get1Int(List<T> list, IntSampleGetter<T> getter)
			throws IOException, InvalidRangeException {
		ArrayInt A = new ArrayInt.D1(list.size());
		Index ima = A.getIndex();
		for (int i = 0; i < list.size(); i++) {
			A.setInt(ima.set(i), getter.get(list.get(i)));
		}
		return A;
	}
}
