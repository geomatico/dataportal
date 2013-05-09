package co.geomati.netcdf.ceam;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import co.geomati.netcdf.Converter;
import co.geomati.netcdf.ConverterException;
import co.geomati.netcdf.DatasetConversion;
import co.geomati.netcdf.IConverter;
import co.geomati.netcdf.dataset.Dataset;

public class ConvertCEAM implements IConverter {
	
	private static Properties ceamVocabulary;

	@Override
	public void doConversion(String[] files, String path) throws IOException, ConverterException {

		for (final String fileName : files) {
			Workbook wb = new HSSFWorkbook(new FileInputStream(
					path + File.separator + fileName + ".xls"));
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
					String time = row.getCell(1).getStringCellValue();
					try {
						SimpleDateFormat timeFormat = new SimpleDateFormat(
								"HH:mm");
						timeFormat.setTimeZone(TimeZone.getTimeZone("GMT0"));
						seconds += (int) (timeFormat.parse(time).getTime() / 1000);
					} catch (ParseException e) {
						throw new RuntimeException("Invalid time format", e);
					}
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
					values.add(numericCellValue);
				}
				variables.get(i).setValues(values);
			}

			Converter.convert(new DatasetConversion() {

				@Override
				public int getDatasetCount() {
					return variables.size();
				}

				@Override
				public Dataset getDataset(int index) throws ConverterException {
					return new CEAMDataset(fileName, variables.get(index),
							timestamps);
				}
			});
		}
	}

	private String getVarName(String cellValue) {
		Pattern p = Pattern.compile("(.*)_\\d_\\d_\\d");
		Matcher matcher = p.matcher(cellValue);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return cellValue;
		}
	}

	private String getUnits(String name) throws IOException {
		return getCEAMVocabulary().getProperty(name + "_UNITS");
	}

	private String getLongName(String name) throws IOException {
		return getCEAMVocabulary().getProperty(name);
	}

	private Properties getCEAMVocabulary() throws IOException {
		if (ceamVocabulary == null) {
			ceamVocabulary = new Properties();
			ceamVocabulary.load(ConvertCEAM.class
					.getResourceAsStream("variables.properties"));
		}

		return ceamVocabulary;
	}
}
