package co.geomati.netcdf.ceam;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import co.geomati.netcdf.ConverterException;
import co.geomati.netcdf.DatasetConversion;
import co.geomati.netcdf.dataset.Dataset;

final class BADMDatasetConversion implements DatasetConversion {
	private final ArrayList<VariableGroup> groups;
	private final Point2D position;
	private final String fileName;

	BADMDatasetConversion(ArrayList<VariableGroup> groups, Point2D position,
			String fileName) {
		this.groups = groups;
		this.position = position;
		this.fileName = fileName;
	}

	@Override
	public String getOutputFileName(Dataset dataset) {
		return fileName + "_" + dataset.getMainVariable().getName();
	}

	@Override
	public int getDatasetCount() {
		return groups.size();
	}

	@Override
	public Dataset getDataset(int index) throws ConverterException {
		VariableGroup variableGroup = groups.get(index);
		Variable main = getMainVariable(variableGroup);
		Variable timeVariable = getTimeVariable(variableGroup);
		if (timeVariable != null) {
			if (position == null) {
				return new BADMTimeSerieDataset(main, timeVariable);
			} else {
				return new BADMTimeSerieStationDataset(main, timeVariable,
						position);
			}
		} else {
			throw new RuntimeException();
		}
	}

	private Variable getTimeVariable(VariableGroup variableGroup) {
		for (Variable variable : variableGroup) {
			if (variable.getName().endsWith("_DATE")
					|| variable.getName().endsWith("_YEAR")) {
				return variable;
			}
		}

		return null;
	}

	private Variable getMainVariable(VariableGroup variableGroup) {
		int minLength = Integer.MAX_VALUE;
		Variable argMinLength = null;
		for (Variable variable : variableGroup) {
			if (variable.getName().length() < minLength) {
				minLength = variable.getName().length();
				argMinLength = variable;
			}
		}

		return argMinLength;
	}

}