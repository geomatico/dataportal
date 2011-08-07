/**
 * 
 */
package cmima.icos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import cmima.icos.csw.CSWCatalog;
import cmima.icos.csw.CSWQuery;
import cmima.icos.utils.BBox;
import cmima.icos.utils.RangeDate;
import cmima.icos.utils.Utils;

/**
 * @author Micho Garcia
 * 
 */
public class GnThController {

	private static final int FIRST = 0;
	private static Logger logger = Logger.getLogger(GnThController.class);

	/**
	 * Receive the params from the client request and communicates these to
	 * gnSpeaker
	 * 
	 * TODO Cambiar parametro entrada extractToBBoxes a String[]
	 * 
	 * @param parametros
	 */
	public String ask2gn(Map<String, String[]> parametros) {

		HashMap<String, Object> queryParams = new HashMap<String, Object>();
		String cswResponse = "";

		// bboxes
		if (parametros.containsKey("bboxes")) {
			ArrayList<BBox> bboxes = Utils.extractToBBoxes(parametros);
			if (bboxes != null)
				queryParams.put("bboxes", bboxes);
		}

		// temporal range
		if (parametros.containsKey("start_date")
				&& parametros.containsKey("end_date")) {
			String start_date = parametros.get("start_date")[FIRST];
			String end_date = parametros.get("end_date")[FIRST];
			logger.debug("Range Date: " + start_date + " ; " + end_date);
			
			if (start_date != "" && end_date != ""){
				RangeDate temporalExtent = new RangeDate(start_date, end_date);
				queryParams.put("temporalExtent", temporalExtent);
			}
		}

		// variables
		if (parametros.containsKey("variables")) {
			// TODO
		}

		// free text
		if (parametros.containsKey("text")) {
			String freeText = parametros.get("text")[FIRST];
			if (freeText != "")
				queryParams.put("text", freeText);
		}

		CSWQuery CSWrequest = new CSWQuery("gmd:MD_Metadata", "csw:IsoRecord");
		String aCswQuery = CSWrequest.createQuery(queryParams);

		Properties queryCoreProp = new Properties();

		try {
			queryCoreProp.load(GnThController.class
					.getResourceAsStream("/query-core.properties"));
			String urlCSW = queryCoreProp.getProperty("urlCsw") + "/srv/en/csw";

			logger.debug("URL to CONNECT: " + urlCSW);

			CSWCatalog catalogo = new CSWCatalog(urlCSW);
			cswResponse = catalogo.sendCatalogRequest(aCswQuery);

		} catch (Exception e) {
			logger.error("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return cswResponse;
	}

}
