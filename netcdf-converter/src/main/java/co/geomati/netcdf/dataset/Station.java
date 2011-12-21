package co.geomati.netcdf.dataset;

/**
 * Interface to mark a Dataset as Station. Note that for georeferenced stations
 * (coordinates) {@link GeoreferencedStation} must be used instead. Datasets
 * implementing only this interface will make the main variable depend on a
 * station dimension with no variable associated to it.
 * 
 * @author fergonco
 */
public interface Station {

	/**
	 * @return Number of stations
	 */
	int getStationCount();
}
