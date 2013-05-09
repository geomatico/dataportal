package co.geomati.netcdf.ceam;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import co.geomati.netcdf.IConverter;

/**
 * Convert BADM files. Unmaintained after refactoring since BADM files were not
 * required (and quite difficult)
 * 
 * @author fergonco, Micho Garcia
 */
public class ConvertBADM implements IConverter{
	private static final int WAITING_START = 1;
	private static final int VARIABLE_VALUES = 2;

	@Override
	public void doConversion(String[] files, String path) throws IOException, ConverterException {

		for (String fileName : files) {
			final ArrayList<VariableGroup> groups = new ArrayList<VariableGroup>();
			Workbook wb = new HSSFWorkbook(new FileInputStream(
					path + File.separator + fileName + ".xls"));
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

					if (!currentVar.isEmpty()) {
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
			Converter.convert(new BADMDatasetConversion(groups, position,
					fileName));
		}
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

	private static String netcdfize(String varName) {
		return varName.replaceAll("[<>]", "_").replaceAll("\\s", "");
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

}
