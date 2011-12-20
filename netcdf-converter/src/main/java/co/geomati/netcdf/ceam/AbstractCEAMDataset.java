package co.geomati.netcdf.ceam;

import co.geomati.netcdf.Dataset;
import co.geomati.netcdf.IcosDomain;

public abstract class AbstractCEAMDataset implements Dataset {
	private String creatorURL;

	public AbstractCEAMDataset(String creatorURL) {
		this.creatorURL = creatorURL;
	}

	@Override
	public IcosDomain getIcosDomain() {
		return IcosDomain.ENVIRONMENT;
	}

	@Override
	public String getInstitution() {
		return "CEAM";
	}

	@Override
	public String getCreatorURL() {
		return creatorURL;
	}

}
