package co.geomati.netcdf.utm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.geomati.netcdf.Converter;
import co.geomati.netcdf.ConverterException;
import co.geomati.netcdf.DatasetConversion;
import co.geomati.netcdf.TimeUnit;
import co.geomati.netcdf.dataset.Dataset;

/**
 * <p>
 * Converts the data from the utm.
 * </p>
 * 
 * @author fergonco
 * 
 */
public class ConvertUTM {
	public static void main(String[] args) throws IOException,
			ConverterException {

		final String base = "29SG20110906_meteo";
		File file = new File("../../data/utm/" + base + ".csv");

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String separator = "\\Q,\\E";
		final String[] fields = reader.readLine().split(separator);

		final ArrayList<MainUTMVariable> mainVariables = new ArrayList<MainUTMVariable>();
		final List<UTMVariable> allVariables = new ArrayList<UTMVariable>();
		UTMTimeVariable tempTimeVariable = null;
		final Positions positions = new Positions();
		UTMVariable latitudeVariable = null;
		UTMVariable longitudeVariable = null;
		for (int i1 = 0; i1 < fields.length; i1++) {
			String field = fields[i1];
			if (field.equals("fecha")) {
				tempTimeVariable = new UTMTimeVariable(TimeUnit.SECOND,
						new Date(0));
				allVariables.add(tempTimeVariable);
			} else if (field.equals("latitud")) {
				latitudeVariable = positions.new Latitude();
				allVariables.add(latitudeVariable);
			} else if (field.equals("longitud")) {
				longitudeVariable = positions.new Longitude();
				allVariables.add(longitudeVariable);
			} else if (!field.startsWith("fecha_")) {
				MainUTMVariable mainVariable = new MainUTMVariable(field);
				mainVariables.add(mainVariable);
				allVariables.add(mainVariable);
			}
		}

		if (tempTimeVariable == null) {
			throw new RuntimeException("No time variable found");
		} else if (latitudeVariable == null || longitudeVariable == null) {
			throw new RuntimeException("No position variables found");
		}
		final UTMTimeVariable timeVariable = tempTimeVariable;

		String line;
		while ((line = reader.readLine()) != null) {
			String[] values = line.split(separator);
			for (int i = 0; i < allVariables.size(); i++) {
				UTMVariable variable = allVariables.get(i);
				variable.addSample(values[i]);
			}
		}

		Converter.convert(new DatasetConversion() {

			@Override
			public int getDatasetCount() {
				return mainVariables.size();
			}

			@Override
			public Dataset getDataset(int index) throws ConverterException {
				return new UTMTrajectoryDataset(base, mainVariables.get(index),
						timeVariable, positions.getTrajectoryPoints());
			}
		});
	}
}
