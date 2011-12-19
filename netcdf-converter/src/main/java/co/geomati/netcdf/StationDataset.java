package co.geomati.netcdf;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * A dataset containing a station variable (main variable), a time unlimited
 * coordinate variable, latitude and longitude coordinate auxiliary variables as
 * specified in CF convention
 * 
 * @author fergonco
 */
public interface StationDataset extends TimeSerieDataset {

	/**
	 * Get the positions of the stations. Return null if there is no position
	 * info
	 * 
	 * @return
	 */
	List<Point2D> getPositions();

}
