/**
 * 
 */
package cmima.icos;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.rmi.CORBA.Util;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import cmima.icos.csw.CSWCatalog;
import cmima.icos.csw.CSWCatalogQuery;
import cmima.icos.utils.BBox;
import cmima.icos.utils.RangeDate;
import cmima.icos.utils.Utils;

/**
 * @author Micho Garcia
 * 
 */
public class QueryController {
	
	private static Logger logger = Logger.getLogger(QueryController.class);

	private static final int FIRST = 0;
	private static String urlCSW = "";

	/**
	 * 
	 */
	public QueryController() {

		Properties queryCoreProp = new Properties();
		try {
			queryCoreProp.load(QueryController.class
					.getResourceAsStream("/query-core.properties"));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		urlCSW = queryCoreProp.getProperty("urlCsw") + "/srv/en/csw";
	}

	/**
	 * Receive the params from the client request and communicates these to
	 * gnSpeaker
	 * 
	 * @param parametros
	 */
	public String ask2gn(Map<String, String[]> parametros) {

		InputStream isCswResponse = null;
		String aCSWQuery = params2Query(parametros);
		String response = "";

		try {
			CSWCatalog catalogo = new CSWCatalog(urlCSW);
			isCswResponse = catalogo.sendCatalogRequest(aCSWQuery);

			response = transform(isCswResponse);

			isCswResponse.close();

			logger.debug("RESPONSE2CLIENT: " + response);

		} catch (IOException e) {
			logger.error("ERROR: " + e.getMessage());
		}

		return response;
	}

	/**
	 * @param isCswResponse
	 * @return
	 */
	private String transform(InputStream isCswResponse) {

		StringWriter writer2Client = new StringWriter();
		InputStream isXslt = CSWCatalog.class
				.getResourceAsStream("/response2client.xsl");

		try {			
			Source responseSource = new StreamSource(isCswResponse);
			Source xsltSource = new StreamSource(isXslt);

			TransformerFactory transFact = TransformerFactory.newInstance();
			Templates template = transFact.newTemplates(xsltSource);
			Transformer transformer = template.newTransformer();

			transformer.transform(responseSource, new StreamResult(
					writer2Client));

			writer2Client.flush();
			writer2Client.close();

			isXslt.close();

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return writer2Client.toString();
	}

	/**
	 * @param parametros
	 * @return
	 */
	private String params2Query(Map<String, String[]> parametros) {

		HashMap<String, Object> queryParams = new HashMap<String, Object>();

		// bboxes
		String stringBBoxes = parametros.get("bboxes")[FIRST];
		ArrayList<BBox> bboxes = Utils.extractToBBoxes(stringBBoxes);
		if (bboxes != null)
			queryParams.put("bboxes", bboxes);

		// temporal range
		String start_date = parametros.get("start_date")[FIRST];
		String end_date = parametros.get("end_date")[FIRST];
		if (start_date != "" && end_date != "") {
			RangeDate temporalExtent = new RangeDate(start_date, end_date);
			queryParams.put("temporalExtent", temporalExtent);
		}

		// variables
		// TODO

		// free text
		String freeText = parametros.get("text")[FIRST];
		if (freeText != "")
			queryParams.put("text", freeText);
		
		// pagination
		String startPosition = parametros.get("start")[FIRST];
		String maxRecords = parametros.get("limit")[FIRST];
		

		CSWCatalogQuery CSWrequest = new CSWCatalogQuery("gmd:MD_Metadata", "csw:IsoRecord");
		CSWrequest.setMaxRecords(maxRecords);
		CSWrequest.setStartPosition(startPosition);
		String aCSWQuery = CSWrequest.createQuery(queryParams);

		return aCSWQuery;
	}

}
