package co.geomati.netcdf;

import java.io.File;

import junit.framework.TestCase;

public class TestNetCDF extends TestCase {

	public void testGenerate() throws Exception {
		File tempFile = new File(System.getProperty("java.io.tmpdir")
				+ "/test.nc");
		Converter.convert(new SampleDataset(), tempFile);
	}

}
