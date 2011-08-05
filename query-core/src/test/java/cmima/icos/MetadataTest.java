package cmima.icos;

import java.io.File;
import java.io.FileInputStream;

import cmima.icos.csw.Metadata;
import cmima.icos.utils.BBox;
import junit.framework.TestCase;

public class MetadataTest extends TestCase {

	private static Metadata metadato;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {

		super.setUp();
		String dir = System.getProperty("user.dir");
		File metadatoFile = new File(dir + "/src/test/java/metadatoTest.xml");

		try {
			FileInputStream archivo = new FileInputStream(metadatoFile);
			metadato = new Metadata(archivo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetExtent() {

		String[] coordenadasTest = { "-20.76556", "9.54645", "-20.567",
				"21.54534" };

		BBox bboxTest = new BBox(coordenadasTest);
		BBox metadatoExtent = metadato.getExtent();

		assertEquals(bboxTest.getXmax(), metadatoExtent.getXmax());
		assertEquals(bboxTest.getYmax(), metadatoExtent.getYmax());
		assertEquals(bboxTest.getXmin(), metadatoExtent.getXmin());
		assertEquals(bboxTest.getYmin(), metadatoExtent.getYmin());
	}

	public void testGetTemporalExtent() {

		String[] dateTest = { "2011-07-10T12:00:00Z", "2011-07-15T12:00:00Z" };
		String[] metadatoTemporalExtent = metadato.getTemporalExtent();

		assertEquals(dateTest[0], metadatoTemporalExtent[0]);
		assertEquals(dateTest[1], metadatoTemporalExtent[1]);

	}

	public void testGetVariables() {
		String[] variablesTest = { "sea_water_salinity",
				"sea_water_temperature", "fluorescence",
				"sea_water_electrical_conductivity", "sea_water_sigmat",
				"date", "longitude", "latitude", "acquisition_instrument_date" };

		String[] metadatoVariables = metadato.getVariables();

		assertEquals(variablesTest[0], metadatoVariables[0]);
		assertEquals(variablesTest[3], metadatoVariables[3]);
		assertEquals(variablesTest[5], metadatoVariables[5]);
		assertEquals(variablesTest[8], metadatoVariables[8]);
		assertEquals(variablesTest[1], metadatoVariables[1]);
	}

	public void testGetTitle() {
		String title = "metadato1";
		String metadataTitle = metadato.getTitle();

		assertEquals(title, metadataTitle);
	}
	
	public void testGetAbstract() {
		String strAbstract = "NetCDF dataset";
		String metadataAbstract = metadato.getAbstract();
		
		assertEquals(strAbstract, metadataAbstract);
	}
	
	public void testGetSchema() {
		String strSchema = "ISO 19115:2003/19139";
		String metadataSchema = metadato.getSchema();
		
		assertEquals(strSchema, metadataSchema);
	}
}
