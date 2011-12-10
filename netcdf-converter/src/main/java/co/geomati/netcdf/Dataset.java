package co.geomati.netcdf;

public interface Dataset {

	IcosDomain getIcosDomain();

	String getInstitution();

	String getCreatorURL();

	int getSampleCount();

	String getVariableName();

}
