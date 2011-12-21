package co.geomati.netcdf.dataset;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Datasets implementing this interface will be created as station data
 * following the CF convention. The main variable will depend on a station
 * dimension.
 * 
 * @author fergonco
 */
public interface GeoreferencedStation extends Station {

	/**
	 * Get the positions of the stations. The number of positions taken into
	 * account is equal to {@link #getStationCount()} so in order to process the
	 * whole list {@link #getStationCount()} must return the result of
	 * {@link List#size()}
	 * 
	 * @return
	 */
	List<Point2D> getPositions();

}
