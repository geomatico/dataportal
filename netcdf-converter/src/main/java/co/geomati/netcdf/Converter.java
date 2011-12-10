package co.geomati.netcdf;

import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

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

	public static void convert(Dataset dataset, File file)
			throws ConverterException {

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

		nc.addGlobalAttribute("uuid", UUID.randomUUID().toString());
		nc.addGlobalAttribute("naming_authority", "es.icos.dataportal");
		nc.addGlobalAttribute("standard_name_vocabulary",
				props.getProperty("vocabulary_url"));
		nc.addGlobalAttribute("icos_domain", dataset.getIcosDomain().toString());
		nc.addGlobalAttribute("conventions", "CF-1.5");
		nc.addGlobalAttribute("Metadata_Conventions",
				"Unidata Dataset Discovery v1.0");
		nc.addGlobalAttribute("institution", dataset.getInstitution());
		nc.addGlobalAttribute("creator_url", dataset.getCreatorURL());

		if (dataset instanceof StationDataset) {
			nc.addGlobalAttribute("cdm_data_type",
					CDMDataType.STATION.toString());
			addStation(nc, (StationDataset) dataset);
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

		/*
		 * TODO bbox and time range
		 */

		try {
			nc.close();
		} catch (IOException e) {
			throw new ConverterException("Cannot close created nc file", e);
		}
	}

	/*
	 * TODO follow convention on variable attributes
	 */
	private static void addStation(NetcdfFileWriteable nc,
			StationDataset dataset) throws ConverterException {
		ArrayList<Dimension> mainVarDimensions = new ArrayList<Dimension>();

		int stationCount = dataset.getSampleCount();

		// time dimension variable
		Dimension timeDim = nc.addUnlimitedDimension("time");
		Variable time = nc.addVariable("time", DataType.INT,
				new Dimension[] { timeDim });
		time.addAttribute(new Attribute("units", dataset.getTimeUnits()
				.toString()
				+ " since "
				+ dateFormat.format(dataset.getReferenceDate())));
		mainVarDimensions.add(timeDim);

		// Position dimension
		Dimension stationDimension = nc.addDimension("station", stationCount);
		// Position variables
		Variable lat = nc.addVariable("lat", DataType.DOUBLE,
				new Dimension[] { stationDimension });
		lat.addAttribute(new Attribute("units", "degrees_north"));
		Variable lon = nc.addVariable("lon", DataType.DOUBLE,
				new Dimension[] { stationDimension });
		lon.addAttribute(new Attribute("units", "degrees_east"));

		/*
		 * TODO vertical coordinate
		 */

		/*
		 * Add coordinate variables
		 */
		DimensionDescriptor[] dimensionDescriptors = dataset
				.getDimensionDescriptors();
		for (DimensionDescriptor dimensionDescriptor : dimensionDescriptors) {
			Dimension dimension = nc.addDimension(
					dimensionDescriptor.getName(),
					dimensionDescriptor.getSize());
			nc.addVariable(dimensionDescriptor.getName(),
					dimensionDescriptor.getType(),
					new Dimension[] { dimension });
			mainVarDimensions.add(dimension);
		}

		/*
		 * Add variable
		 */
		mainVarDimensions.add(stationDimension);
		Variable mainVar = nc.addVariable(dataset.getVariableName(),
				dataset.getVariableType(),
				mainVarDimensions.toArray(new Dimension[0]));

		try {
			nc.create();

			/*
			 * Write time
			 */
			List<Integer> times = dataset.getTimeStamps();
			try {
				nc.write(time.getName(),
						get1Int(lat, times, new IntSampleGetter<Integer>() {

							@Override
							public int get(Integer t) {
								return t;
							}
						}));
			} catch (InvalidRangeException e) {
				throw new RuntimeException("Bug. This should not "
						+ "happen since time is unlimited", e);
			}

			/*
			 * write positions
			 */
			List<Point2D> points = dataset.getPositions();
			try {
				nc.write(
						lat.getName(),
						get1Double(lat, points,
								new DoubleSampleGetter<Point2D>() {

									@Override
									public double get(Point2D t) {
										return t.getY();
									}
								}));
				nc.write(
						lon.getName(),
						get1Double(lat, points,
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
			 * TODO write coordinate variables
			 */

			/*
			 * TODO write main variable
			 */
			// List<Dimension> dimensions = var.getDimensions();
			// Dimension firstDimension = dimensions.get(0);
			// Dimension secondDimension = dimensions.get(1);
			// ArrayDouble A = new ArrayDouble.D2(firstDimension.getLength(),
			// secondDimension.getLength());
			// Index ima = A.getIndex();
			// for (int i = 0; i < firstDimension.getLength(); i++) {
			// for (int j = 0; j < secondDimension.getLength(); j++) {
			// A.setDouble(ima.set(i, j), doubleValueProvider.get(i, j));
			// }
			// }
			// nc.write(var.getFullName(), A);
		} catch (IOException e) {
			throw new ConverterException("Cannot create netcdf file", e);
		}
	}

	private static void addTrajectory(NetcdfFileWriteable nc, Dataset dataset) {
		// TODO Auto-generated method stub

	}

	private static void addGrid(NetcdfFileWriteable nc, Dataset dataset) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public static <T> ArrayDouble get1Double(Variable var, List<T> list,
			DoubleSampleGetter<T> getter) throws IOException,
			InvalidRangeException {
		Dimension dimension = var.getDimensions().get(0);
		ArrayDouble A = new ArrayDouble.D1(dimension.getLength());
		Index ima = A.getIndex();
		for (int i = 0; i < dimension.getLength(); i++) {
			A.setDouble(ima.set(i), getter.get(list.get(i)));
		}
		return A;
	}

	public static <T> ArrayInt get1Int(Variable var, List<T> list,
			IntSampleGetter<T> getter) throws IOException,
			InvalidRangeException {
		Dimension dimension = var.getDimensions().get(0);
		ArrayInt A = new ArrayInt.D1(dimension.getLength());
		Index ima = A.getIndex();
		for (int i = 0; i < dimension.getLength(); i++) {
			A.setInt(ima.set(i), getter.get(list.get(i)));
		}
		return A;
	}

}
