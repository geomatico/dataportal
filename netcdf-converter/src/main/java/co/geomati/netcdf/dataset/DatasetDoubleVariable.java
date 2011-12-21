package co.geomati.netcdf.dataset;

import java.util.List;

/**
 * A NetCDF variable containing double values
 * 
 * @author fergonco
 */
public interface DatasetDoubleVariable extends DatasetVariable {

	/**
	 * Get the data for the specified variable.
	 * 
	 * @return A list with as many elements as the product of all the variable
	 *         dimensions. Variable dimensions are defined by the interfaces
	 *         implemented by the dataset
	 */
	List<Double> getData();
}
