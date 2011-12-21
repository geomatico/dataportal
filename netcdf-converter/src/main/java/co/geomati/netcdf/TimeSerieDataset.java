package co.geomati.netcdf;

import java.util.Date;
import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.DataType;
import co.geomati.netcdf.dataset.Dataset;

/**
 * A dataset containing a (main variable) depending on a time unlimited
 * coordinate variable
 * 
 * @author fergonco
 * @deprecated
 */
public interface TimeSerieDataset extends Dataset {

	/**
	 * Get the units of the time coordinate
	 * 
	 * @return
	 */
	TimeUnit getTimeUnits();

	/**
	 * Return the date of reference in 0:00Z (UTC)
	 * 
	 * @return
	 */
	Date getReferenceDate();

	/**
	 * Get the type of the dataset main variable
	 * 
	 * @return
	 */
	DataType getVariableType();

	/**
	 * Get the times containing the measures of the main variable. Null if the
	 * main variable has no temporal dimension
	 * 
	 * @return
	 */
	List<Integer> getTimeStamps();

	Array getData();

	/**
	 * Get the value for the _FillValue attribute. Double.NaN if there is no
	 * fill value
	 * 
	 * @return
	 */
	double getFillValue();
}
