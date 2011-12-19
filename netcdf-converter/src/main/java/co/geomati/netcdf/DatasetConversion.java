package co.geomati.netcdf;

public interface DatasetConversion {

	int getDatasetCount();

	Dataset getDataset(int index) throws ConverterException;

	String getOutputFileName(Dataset dataset);
}
