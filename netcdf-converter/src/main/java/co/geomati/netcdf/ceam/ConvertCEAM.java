package co.geomati.netcdf.ceam;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import co.geomati.netcdf.Converter;
import co.geomati.netcdf.ConverterException;
import co.geomati.netcdf.Dataset;
import co.geomati.netcdf.DatasetConversion;

public class ConvertCEAM {
	private static final int WAITING_START = 1;
	private static final int VARIABLE_VALUES = 2;
	private static Properties ceamVocabulary;

	public static void main(String[] args) throws FileNotFoundException,
			IOException, ConverterException {
		final String creatorURL = "http://www.ceam.es/~becario";
		// TODO there is e-mail in xls

		// convertBADM(creatorURL);
		convertTemporalSeries(creatorURL);
	}

	private static void convertTemporalSeries(final String creatorURL)
			throws IOException {
		String[] files = new String[] { "ES-LMa_FLX_2010_01_12_newformat" };
		for (final String fileName : files) {
			Workbook wb = new HSSFWorkbook(new FileInputStream(
					"../../data/ceam/" + fileName + ".xls"));
			Sheet data = wb.getSheetAt(0);
			Iterator<Row> rowIterator = data.rowIterator();

			Row row = null;
			final ArrayList<Variable> variables = new ArrayList<Variable>();
			int firstVarColumn = -1;
			boolean hasTime = false;
			if (rowIterator.hasNext()) {
				row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				int cellIndex = -1;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					cellIndex++;
					String cellValue = cell.getStringCellValue();
					if (cellValue.equals("date")) {
						continue;
					} else if (!cellValue.equals("time")) {
						if (firstVarColumn == -1) {
							firstVarColumn = cellIndex;
						}
						String varName = getVarName(cellValue);
						Variable var = new Variable(varName,
								getLongName(varName), getUnits(varName));
						variables.add(var);
					} else {
						hasTime = true;
					}
				}
			}

			final ArrayList<Integer> timestamps = new ArrayList<Integer>();
			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				Date date = row.getCell(0).getDateCellValue();
				int seconds = (int) (date.getTime() / 1000);
				if (hasTime) {
					Date time = row.getCell(0).getDateCellValue();
					// System.out.println(time);
				}
				timestamps.add(seconds);
			}

			for (int i = 0; i < variables.size(); i++) {
				rowIterator = data.rowIterator();
				rowIterator.next();
				ArrayList<Object> values = new ArrayList<Object>();
				while (rowIterator.hasNext()) {
					row = rowIterator.next();

					double numericCellValue = row.getCell(firstVarColumn + i)
							.getNumericCellValue();
					System.out.println(numericCellValue);
					values.add(numericCellValue);
				}
				variables.get(i).setValues(values);
			}

			Converter.convert(new DatasetConversion() {

				@Override
				public String getOutputFileName(Dataset dataset) {
					return fileName + "_" + dataset.getVariableStandardName();
				}

				@Override
				public int getDatasetCount() {
					return variables.size();
				}

				@Override
				public Dataset getDataset(int index) throws ConverterException {
					return new CEAMDataset(variables.get(index), timestamps,
							creatorURL);
				}
			});
		}
	}

	private static String getVarName(String cellValue) {
		Pattern p = Pattern.compile("(.*)_\\d_\\d_\\d");
		Matcher matcher = p.matcher(cellValue);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return cellValue;
		}
	}

	private static String getUnits(String name) throws IOException {
		return getCEAMVocabulary().getProperty(name + "_UNITS");
	}

	private static String getLongName(String name) throws IOException {
		return getCEAMVocabulary().getProperty(name);
	}

	private static Properties getCEAMVocabulary() throws IOException {
		if (ceamVocabulary == null) {
			ceamVocabulary = new Properties();
			ceamVocabulary.load(ConvertCEAM.class
					.getResourceAsStream("variables.properties"));
		}

		return ceamVocabulary;
	}

	private static void convertBADM(final String creatorURL)
			throws IOException, FileNotFoundException, ConverterException {
		String[] files = new String[] { "BADM_ES-LMa_2005", "BADM_ES-LMa_2006",
				"BADM_ES-LMa_2007", "BADM_ES-LMa_2008", "BADM_ES-LMa_2009",
				"BADM_ES-LMa_2010" };
		for (String fileName : files) {
			final ArrayList<VariableGroup> groups = new ArrayList<VariableGroup>();
			Workbook wb = new HSSFWorkbook(new FileInputStream(
					"../../data/ceam/" + fileName + ".xls"));
			final Point2D position = getPosition(wb);
			Sheet biodata = wb.getSheet("BioData");
			Iterator<Row> rowIterator = biodata.rowIterator();

			VariableGroup variableGroup = new VariableGroup();
			int state = WAITING_START;
			Variable currentVar;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				String firstCellValue = row.getCell(0).getStringCellValue();
				switch (state) {
				case WAITING_START:
					if (firstCellValue.equals("Variable")) {
						state = VARIABLE_VALUES;
					}
					break;
				case VARIABLE_VALUES:
					String longName = row.getCell(1).getStringCellValue();
					String units;
					Cell unitsCell = row.getCell(2);
					if (unitsCell != null) {
						units = unitsCell.getStringCellValue();
					} else {
						units = null;
					}
					currentVar = new Variable(netcdfize(firstCellValue),
							longName, units);
					Iterator<Cell> cellIterator = row.cellIterator();
					for (int i = 0; i < 3 && cellIterator.hasNext(); i++) {
						cellIterator.next();
					}
					if (cellIterator.hasNext()) {
						ArrayList<Object> values = new ArrayList<Object>();
						while (cellIterator.hasNext()) {
							Cell cell = cellIterator.next();
							Object cellValue = getCellValue(cell);
							if (cellValue == null) {
								continue;
							}
							values.add(cellValue);
						}
						currentVar.setValues(values);
					}

					if (currentVar.getValueCount() > 0) {
						if (variableGroup.isEmpty()) {
							variableGroup.add(currentVar);
						} else {
							String varName = variableGroup.get(0).getName();
							if (!currentVar.getName().startsWith(varName + "_")) {
								groups.add(variableGroup);
								variableGroup = new VariableGroup();
							}
							variableGroup.add(currentVar);
						}
					}
					break;
				}
			}
			Converter.convert(new CEAMDatasetConversion(groups, position,
					creatorURL, fileName));
		}
	}

	private static String netcdfize(String varName) {
		return varName.replaceAll("[<>]", "_").replaceAll("\\s", "");
	}

	private static Point2D getPosition(Workbook wb) throws ConverterException {
		Sheet siteData = wb.getSheet("SiteBioAncData");

		Iterator<Row> rowIterator = siteData.rowIterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String firstCellValue = row.getCell(0).getStringCellValue();
			if (firstCellValue.equals("SITE_DESC")) {
				String description = row.getCell(2).getStringCellValue();
				Pattern p = Pattern.compile("(\\d*\\.\\d*)º\\s[NW]");
				Matcher matcher = p.matcher(description);

				double lat;
				double lon;
				if (matcher.find()) {
					lat = Double.parseDouble(matcher.group(1));
				} else {
					throw getNoPositionException();
				}
				if (matcher.find()) {
					lon = Double.parseDouble(matcher.group(1));
				} else {
					throw getNoPositionException();
				}

				return new Point2D.Double(lon, lat);
			}
		}
		throw getNoPositionException();
	}

	private static ConverterException getNoPositionException() {
		return new ConverterException("Cannot find position "
				+ "information in siteBioAncData sheet");
	}

	private static Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_STRING:
			return cell.getRichStringCellValue().getString();
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			} else {
				return cell.getNumericCellValue();
			}
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();
		default:
			throw new RuntimeException();
		}
	}

	private static final class CEAMDatasetConversion implements
			DatasetConversion {
		private final ArrayList<VariableGroup> groups;
		private final Point2D position;
		private final String creatorURL;
		private final String fileName;

		private CEAMDatasetConversion(ArrayList<VariableGroup> groups,
				Point2D position, String creatorURL, String fileName) {
			this.groups = groups;
			this.position = position;
			this.creatorURL = creatorURL;
			this.fileName = fileName;
		}

		@Override
		public String getOutputFileName(Dataset dataset) {
			return fileName + "_" + dataset.getVariableName();
		}

		@Override
		public int getDatasetCount() {
			return groups.size();
		}

		@Override
		public Dataset getDataset(int index) throws ConverterException {
			return new BADMDataset(groups.get(index), creatorURL, position);
		}
	}
}
