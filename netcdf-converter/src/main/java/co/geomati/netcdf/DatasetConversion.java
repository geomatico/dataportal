package co.geomati.netcdf;

import co.geomati.netcdf.dataset.Dataset;

/**
 * A class representing a set of Dataset to be converted to NetCDF
 * 
 * @author fergonco
 */
public interface DatasetConversion {

	/**
	 * Number of datasets to convert to NetCDF
	 * 
	 * @return
	 */
	int getDatasetCount();

	/**
	 * Get the i-th dataset to convert
	 * 
	 * @param index
	 * @return
	 * @throws ConverterException
	 */
	Dataset getDataset(int index) throws ConverterException;
}
