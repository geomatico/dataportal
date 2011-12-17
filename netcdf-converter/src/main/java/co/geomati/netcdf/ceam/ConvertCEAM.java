package co.geomati.netcdf.ceam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import co.geomati.netcdf.Converter;
import co.geomati.netcdf.ConverterException;

public class ConvertCEAM {
	private static final int WAITING_START = 1;
	private static final int VARIABLE_VALUES = 2;

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		String creatorURL = "http://www.ceam.es/~becario";

		Workbook wb = new HSSFWorkbook(new FileInputStream(
				"../../data/ceam/BADM_ES-LMa_2006.xls"));
		Sheet biodata = wb.getSheet("BioData");
		Iterator<Row> rowIterator = biodata.rowIterator();

		ArrayList<Variable> variableGroup = new ArrayList<Variable>();
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
				currentVar = new Variable(firstCellValue, longName, units);
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
						if (currentVar.getName().startsWith(varName + "_")) {
							variableGroup.add(currentVar);
						} else {
							createNC(variableGroup, creatorURL);
						}
					}
				}
				break;
			}

		}
	}

	private static void createNC(ArrayList<Variable> variableGroup,
			String creatorURL) {
		if (variableGroup.size() == 0) {
			return;
		}

		CEAMDataset dataset = new CEAMDataset(variableGroup, creatorURL);
		File tempFile = new File(System.getProperty("java.io.tmpdir") + "/"
				+ dataset.getVariableName() + ".nc");
		try {
			Converter.convert(dataset, tempFile);
		} catch (ConverterException e) {
			System.err.println("Cannot convert");
			e.printStackTrace();
		}
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
