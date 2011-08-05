/**
 * 
 */
package cmima.icos;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cmima.icos.csw.Catalog;
import cmima.icos.csw.CatalogRequest;
import cmima.icos.utils.BBox;

/**
 * @author Micho Garcia
 * 
 */
public class GnThController {

	private static Logger logger = Logger.getLogger(GnThController.class);

	/**
	 * Receive the params from the client request and communicates these to
	 * gnSpeaker
	 * 
	 * (TODO extraer la URL del servidor del metodo)
	 * 
	 * @param parametros
	 */
	public void ask2gn(HashMap<String, String[]> parametros) {

		HashMap<String, Object> queryParams = new HashMap<String, Object>();
		// bboxes
		if (parametros.containsKey("bboxes")) {
			ArrayList<BBox> bboxes = extractToBBoxes(parametros);
			queryParams.put("bboxes", bboxes);
		}

		CatalogRequest CSWrequest = new CatalogRequest("csw:IsoRecord",
				"gmd:MD_Metadata");
		String cswQuery = CSWrequest.createQuery(queryParams);
		
		try {
			Catalog catalogo = new Catalog("http://ciclope.cmima.csic.es:8080/geonetworkcmima/srv/en/csw");
			String cswResponse = catalogo.sendCatalogRequest(cswQuery);
			
		} catch (MalformedURLException malUrlE) {
			logger.error(malUrlE.getMessage());
			malUrlE.printStackTrace();
		} catch (IOException ioE) {
			logger.error(ioE.getMessage());
			ioE.printStackTrace();
		}
	}

	/**
	 * 
	 * Extract the coords of bbox from string[] and convert into an arraylist of
	 * bbox type 
	 * 
	 * TODO (SACAR ESTE METODO DE AQUI, posible utils)
	 * 
	 * @param parametros
	 */
	private ArrayList<BBox> extractToBBoxes(HashMap<String, String[]> parametros) {

		String stringBBoxes = parametros.get("bboxes")[0];

		ArrayList<BBox> bboxes = new ArrayList<BBox>();
		StringBuffer tempStringBBoxes = new StringBuffer();

		for (int n = 2; n < stringBBoxes.length() - 1; n++) {

			if (stringBBoxes.charAt(n) != ']') {
				tempStringBBoxes.append(stringBBoxes.charAt(n));
			} else {
				String[] coords = tempStringBBoxes.toString()
						.replaceAll(",\\[", "").split(",");

				logger.debug("COORDS: xmin " + coords[0] + "; ymin "
						+ coords[1] + "; xmax " + coords[2] + "; ymax "
						+ coords[3]);

				bboxes.add(new BBox(coords));
				tempStringBBoxes = new StringBuffer();
			}
		}

		return bboxes;
	}
}
