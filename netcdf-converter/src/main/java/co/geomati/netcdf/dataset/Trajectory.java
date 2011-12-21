package co.geomati.netcdf.dataset;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * <p>
 * Interface to mark a Dataset as containing a trajectory. A trajectory is also
 * a {@link TimeSerie}.
 * </p>
 * <p>
 * Datasets implementing this interface will be created as trajectories
 * following the CF convention. The main variable will depend on a time
 * dimension. Two lat/lon auxiliary coordinate variables will depend this time
 * dimension aswell.
 * 
 * 
 * @author fergonco
 */
public interface Trajectory extends TimeSerie {

	/**
	 * Get the points in the trajectory
	 * 
	 * @return
	 */
	List<Point2D> getTrajectoryPoints();

}
