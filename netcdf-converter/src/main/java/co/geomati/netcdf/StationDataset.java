package co.geomati.netcdf;

import java.awt.geom.Point2D;
import java.util.Date;
import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.DataType;

/**
 * A dataset containing a station variable (main variable), a time unlimited
 * coordinate variable, latitude and longitude coordinate auxiliary variables as
 * specified in CF convention
 * 
 * @author fergonco
 */
public interface StationDataset extends Dataset {

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
	 * Get the positions of the stations. Return null if there is no position
	 * info
	 * 
	 * @return
	 */
	List<Point2D> getPositions();

	/**
	 * Get the times containing the measures of the main variable. Null if the
	 * main variable has no temporal dimension
	 * 
	 * @return
	 */
	List<Integer> getTimeStamps();

	/**
	 * Get the station data array. It has two dimensions, being time the first
	 * one and the slowest changing one and station position index the second
	 * one.
	 * 
	 * @return
	 */
	Array getStationData();

}
