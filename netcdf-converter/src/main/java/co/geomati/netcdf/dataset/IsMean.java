package co.geomati.netcdf.dataset;

import java.util.List;

public interface IsMean {

	Number getNDFillValue();

	Number getSDFillValue();

	List<Double> getSDData();

	List<Integer> getNDData();

	String getMeanDescription();

}
