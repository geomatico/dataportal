package co.geomati.netcdf.dataset;

public interface DatasetVariable {

	String getName();

	String getLongName();

	String getStandardName();

	String getUnits();

	/**
	 * Return the value used as _FillValue (no data). Null if all values in the
	 * variable has a valid value
	 * 
	 * @return
	 */
	Number getFillValue();

}
