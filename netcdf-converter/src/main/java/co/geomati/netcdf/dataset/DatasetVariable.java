package co.geomati.netcdf.dataset;

public interface DatasetVariable {

	/**
	 * Description of the variable. Typically this is implemented by looking in
	 * some specific vocabulary
	 * 
	 * @return
	 */
	String getLongName();

	/**
	 * The name to identify this variable in the ICOS vocabulary
	 * 
	 * @return
	 */
	String getStandardName();

	/**
	 * Name of the variable as it will appear in the netcdf file
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Unit description
	 * 
	 * @return
	 */
	String getUnits();

	/**
	 * Return the value used as _FillValue (no data) in the main dataset
	 * variable (the one returned by the {@link Dataset#getMainVariable()}).
	 * Null if all values in the variable have a valid value
	 * 
	 * @return
	 */
	Number getFillValue();

}
