package co.geomati;

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

public class App {
	private static final int WAITING_START = 1;
	private static final int VARIABLE_VALUES = 2;

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		Workbook wb = new HSSFWorkbook(new FileInputStream(
				"data/ceam/BADM_ES-LMa_2006.xls"));
		Sheet biodata = wb.getSheet("BioData");
		Iterator<Row> rowIterator = biodata.rowIterator();

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

				System.out.println(currentVar);
				break;
			}

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

	private static class Variable {
		private String name;
		private String longName;
		private String units;
		private ArrayList<Object> values = new ArrayList<Object>();

		public Variable(String name, String longName, String units) {
			super();
			this.name = name;
			this.longName = longName;
			this.units = units;
		}

		public void setValues(ArrayList<Object> values) {
			this.values = values;
		}

		@Override
		public String toString() {
			String ret = name + "(" + longName + ")(" + units + "):";
			for (Object value : values) {
				ret += value + ",";
			}
			return ret;
		}
	}
}
