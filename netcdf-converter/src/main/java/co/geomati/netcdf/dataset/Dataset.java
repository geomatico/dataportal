package co.geomati.netcdf.dataset;

import co.geomati.netcdf.IcosDomain;
import co.geomati.netcdf.Institution;

public interface Dataset {

	IcosDomain getIcosDomain();

	Institution getInstitution();

	DatasetVariable getMainVariable();
}
