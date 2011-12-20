package co.geomati.netcdf;

public interface Dataset {

	IcosDomain getIcosDomain();

	Institution getInstitution();

	String getVariableName();

	String getVariableLongName();

	String getVariableStandardName();

	String getVariableUnits();

}
