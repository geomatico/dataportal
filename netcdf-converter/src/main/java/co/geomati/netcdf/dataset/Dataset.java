package co.geomati.netcdf.dataset;

import co.geomati.netcdf.IcosDomain;
import co.geomati.netcdf.Institution;

public interface Dataset {

	/**
	 * Get the name of the Dataset
	 * 
	 * @param dataset
	 * @return
	 */
	String getName();

	IcosDomain getIcosDomain();

	Institution getInstitution();

	DatasetVariable[] getMainVariables();
}
