package co.geomati.netcdf.dataset;

import java.util.Date;
import java.util.List;

import co.geomati.netcdf.TimeUnit;

/**
 * Interface to be implemented by those Dataset whose main variable has a time
 * dimension.
 * 
 * @author fergonco
 */
public interface TimeSerie {

	/**
	 * Get the units of the time coordinate
	 * 
	 * @return never null
	 */
	TimeUnit getTimeUnits();

	/**
	 * Return the date of reference in 0:00Z (UTC)
	 * 
	 * @return never null
	 */
	Date getReferenceDate();

	/**
	 * Get the times containing the measures of the main variable. Times are
	 * specified as a number of units since the reference date.
	 * 
	 * @return never null
	 */
	List<Integer> getTimeStamps();

}
