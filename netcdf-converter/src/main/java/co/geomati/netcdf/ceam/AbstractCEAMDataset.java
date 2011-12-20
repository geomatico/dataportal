package co.geomati.netcdf.ceam;

import co.geomati.netcdf.Dataset;
import co.geomati.netcdf.IcosDomain;
import co.geomati.netcdf.Institution;

public abstract class AbstractCEAMDataset implements Dataset {

	@Override
	public IcosDomain getIcosDomain() {
		return IcosDomain.ENVIRONMENT;
	}

	@Override
	public Institution getInstitution() {
		return Institution.CEAM;
	}

}
