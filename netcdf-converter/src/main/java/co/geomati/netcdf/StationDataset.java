package co.geomati.netcdf;

import java.awt.geom.Point2D;
import java.util.Date;
import java.util.List;

import ucar.ma2.DataType;

/**
 * A dataset containing a station variable (main variable), a time unlimited
 * coordinate variable, latitude and longitude coordinate auxiliary variables as
 * specified in CF convention and as many additional dimensions as desired
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
	 * Get a descriptor for each dimension of the dataset main variable except
	 * time and station
	 * 
	 * @return
	 */
	DimensionDescriptor[] getDimensionDescriptors();

	/**
	 * Get the type of the dataset main variable
	 * 
	 * @return
	 */
	DataType getVariableType();

	/**
	 * Get the positions of the stations
	 * 
	 * @return
	 */
	List<Point2D> getPositions();

	/**
	 * Get the times containing the measures of the main variable
	 * 
	 * @return
	 */
	List<Integer> getTimeStamps();

}
