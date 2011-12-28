package co.geomati.netcdf.aemet;

import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import co.geomati.netcdf.Converter;
import co.geomati.netcdf.ConverterException;
import co.geomati.netcdf.DatasetConversion;
import co.geomati.netcdf.TimeUnit;
import co.geomati.netcdf.dataset.Dataset;

/**
 * <p>
 * Converts the data from the aemet.
 * </p>
 * <p>
 * The converter takes a file and the suffix to search for the variables in the
 * vocabulary
 * </p>
 * 
 * @author fergonco
 * 
 */
public class ConvertAEMET {

	private static Properties vocabulary;

	public static void main(String[] args) throws ConverterException {
		String[] files = new String[] { "izoco2hour_10_newformat",
				"izoco2daily_night_newformat", "izoco2monthly_night_newformat" };
		String[] meanDescriptions = new String[] { "hourly means",
				"daily night means (20:00-08:00)", "monthly means" };
		for (int i = 0; i < meanDescriptions.length; i++) {
			final String fileName = files[i];
			final String meanDescription = meanDescriptions[i];
			ByteArrayOutputStream os;
			try {
				BufferedInputStream is = new BufferedInputStream(
						new FileInputStream(new File("../../data/aemet/"
								+ fileName + ".txt")));
				os = new ByteArrayOutputStream();
				IOUtils.copy(is, os);
				is.close();
				os.close();
			} catch (FileNotFoundException e) {
				throw new ConverterException("Cannot locate source file", e);
			} catch (IOException e) {
				throw new ConverterException("Cannot read source file", e);
			}
			String content = new String(os.toByteArray());

			String lat = getMatch(content, "LATITUDE: (.*)");
			String lon = getMatch(content, "LONGITUDE: (.*)");
			final Point2D pos = getPosition(lat, lon);

			final String variableName = getVariableName(
					getMatch(content, "PARAMETER: (.*)"), meanDescription);
			final String variableUnit = getMatch(content, "UNIT: (.*)");

			final Date referenceDate = new Date(0);
			final TimeUnit timeUnits = TimeUnit.SECOND;

			Pattern p = Pattern.compile("REM.*\n");
			Matcher matcher = p.matcher(content);
			int lastLineStart = -1;
			int lastLineEnd = -1;
			while (matcher.find()) {
				lastLineStart = matcher.start();
				lastLineEnd = matcher.end();
			}
			if (lastLineStart == -1 || lastLineEnd == -1) {
				throw new ConverterException("Cannot find last header line");
			}
			lastLineStart = lastLineStart + 5;// The REM0x
			String lastLine = content.substring(lastLineStart, lastLineEnd);
			final String[] fields = lastLine.trim().split("\\s+");

			// Add the prefix
			for (int j = 0; j < fields.length; j++) {
				fields[j] = getVariableName(fields[j], meanDescription);
			}

			// Get the data section
			final ArrayList<Integer> timestamps = new ArrayList<Integer>();
			final ArrayList<?>[] data = new ArrayList<?>[fields.length];

			content = content.substring(lastLineEnd);
			String[] lines = content.split("\n");
			for (String line : lines) {
				String[] fieldValues = line.split("\\s+");

				// Get seconds
				String date = fieldValues[0] + " " + fieldValues[1];
				try {
					int seconds = (int) (new SimpleDateFormat(
							"yyyy-MM-dd HH:mm").parse(date).getTime() / 1000);
					timestamps.add(seconds);
				} catch (ParseException e) {
					throw new ConverterException("Cannot parse date: " + date);
				}

				// get the rest
				for (int j = 2; j < fieldValues.length; j++) {
					String fieldValue = fieldValues[j];
					String fieldName = fields[j];

					// Ignore second date/time
					if (fieldName.equals("date")) {
						if (!fieldValue.equals("9999-99-99")) {
							throw new ConverterException(
									"Only continuous meassures supported");
						} else {
							continue;
						}
					}
					if (fieldName.equals("time")) {
						if (!fieldValue.equals("99:99")) {
							throw new ConverterException(
									"Only continuous meassures supported");
						} else {
							continue;
						}
					}

					// Fill the data
					ArrayList<?> array = data[j];
					if (fieldName.startsWith("nd")) {
						@SuppressWarnings("unchecked")
						ArrayList<Integer> intArray = (ArrayList<Integer>) array;
						if (intArray == null) {
							intArray = new ArrayList<Integer>();
							data[j] = intArray;
						}
						intArray.add(new Integer(fieldValue));
					} else {
						@SuppressWarnings("unchecked")
						ArrayList<Double> doubleArray = (ArrayList<Double>) array;
						if (doubleArray == null) {
							doubleArray = new ArrayList<Double>();
							data[j] = doubleArray;
						}
						doubleArray.add(new Double(fieldValue));
					}
				}
			}

			Converter.convert(new DatasetConversion() {

				@Override
				public String getOutputFileName(Dataset dataset) {
					return dataset.getMainVariable().getName();
				}

				@Override
				public int getDatasetCount() {
					return 1;
				}

				@Override
				public Dataset getDataset(int index) throws ConverterException {
					try {
						List<Double> mainVarData = ConvertAEMET
								.<Double> getVariableData(variableName, fields,
										data);
						List<Integer> ndData = ConvertAEMET
								.<Integer> getVariableData("nd", fields, data);
						List<Double> sdData = ConvertAEMET
								.<Double> getVariableData("sd", fields, data);
						return new AEMETDataset(Collections.singletonList(pos),
								variableUnit, getLongName(variableName),
								variableName, meanDescription, mainVarData,
								ndData, sdData, timestamps, referenceDate,
								timeUnits);
					} catch (IOException e) {
						throw new ConverterException(
								"Cannot access aemet vocabulary", e);
					}
				}
			});
		}
	}

	private static String getVariableName(String input, String suffix) {
		return input.toLowerCase().trim();
	}

	private static <T> List<T> getVariableData(String variableName,
			String[] fields, ArrayList<?>[] data) throws ConverterException {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].startsWith(variableName)) {
				@SuppressWarnings("unchecked")
				List<T> ret = (List<T>) data[i];
				return ret;
			}
		}

		throw new ConverterException("Cannot find CO2");
	}

	private static String getLongName(String variableName) throws IOException {
		if (vocabulary == null) {
			vocabulary = new Properties();
			vocabulary.load(ConvertAEMET.class
					.getResourceAsStream("vocabulary.properties"));
		}

		return vocabulary.getProperty(variableName);
	}

	private static Point2D getPosition(String lat, String lon)
			throws ConverterException {
		double latitude = degreesToDouble(lat);
		double longitude = degreesToDouble(lon);

		return new Point2D.Double(latitude, longitude);
	}

	private static double degreesToDouble(String latlon)
			throws ConverterException {
		latlon = latlon.trim();
		char northingWesting = latlon.charAt(latlon.length() - 1);
		if (northingWesting == 'N' || northingWesting == 'W') {
			String[] matches = getMatches(
					latlon.substring(0, latlon.length() - 1),
					"(\\d*)\\s*(\\d*)'");
			try {
				return Integer.parseInt(matches[0])
						+ Integer.parseInt(matches[1]) / 60.0;
			} catch (NumberFormatException e) {
				throw new ConverterException("Cannot parse "
						+ "latitude/longitude: " + latlon);
			}
		} else {
			throw new ConverterException(
					"Latitude must be N and longitude must be W");
		}
	}

	private static String getMatch(String content, String regexp) {
		return getMatches(content, regexp)[0];
	}

	private static String[] getMatches(String content, String regexp) {
		Pattern p = Pattern.compile(regexp);
		Matcher matcher = p.matcher(content);
		if (matcher.find()) {
			String[] ret = new String[matcher.groupCount()];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = matcher.group(i + 1);
			}
			return ret;
		} else {
			return null;
		}
	}
}
