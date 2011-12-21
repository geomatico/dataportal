package co.geomati.netcdf.dataset;

import java.util.List;

/**
 * A NetCDF variable containing integer values
 * 
 * @author fergonco
 */
public interface DatasetIntVariable extends DatasetVariable {

	/**
	 * Get the data for the specified variable.
	 * 
	 * @return A list with as many elements as the product of all the variable
	 *         dimensions. Variable dimensions are defined by the interfaces
	 *         implemented by the dataset
	 */
	List<Integer> getData();
}
