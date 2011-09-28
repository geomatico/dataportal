/**
 * 
 */
package org.dataportal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.dataportal.csw.CSWCatalog;
import org.dataportal.csw.CSWGetRecords;
import org.dataportal.utils.BBox;
import org.dataportal.utils.RangeDate;
import org.dataportal.utils.Utils;

/**
 * Controller manage client petitions to server and manage responses from server
 * too. Use Xpath to extract information from XML request and responses and XSLT
 * to transform this to client
 * 
 * @author Micho Garcia
 * 
 */
public class QueryController {

	private static Logger logger = Logger.getLogger(QueryController.class);

	private static final int FIRST = 0;
	private static CSWCatalog catalogo;

	/**
	 * Constructor. Assign URL catalog server
	 */
	public QueryController() {
	    String url = Config.get("csw.url");
	    try {
			catalogo = new CSWCatalog(url);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
		}

	}

	/**
	 * Receive the params from the client request and communicates these to
	 * CSWCatalog
	 * 
	 * @param parametros
	 */
	public String ask2gn(Map<String, String[]> parametros) {

		InputStream isCswResponse = null;
		String aCSWQuery = params2Query(parametros);
		String response = "";

		try {
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
	 * Transform the response from CSW Catalog into client response
	 * 
	 * @param isCswResponse
	 * @return String
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
	 * Extract the params from a Map and create a Query in CSW 2.0.2 standard
	 * 
	 * @param parametros
	 * @return String
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
		String variables = parametros.get("variables")[FIRST];
		if (variables != "") {
			String queryVariables[] = variables.split(",");
			queryParams.put("variables", queryVariables);
		}

		// free text
		String freeText = parametros.get("text")[FIRST];
		if (freeText != "")
			queryParams.put("text", freeText);

		// pagination
		String startPosition = parametros.get("start")[FIRST];
		String maxRecords = parametros.get("limit")[FIRST];

		// Order
		String sort = parametros.get("sort")[FIRST];
		String dir = parametros.get("dir")[FIRST];

		CSWGetRecords CSWrequest = new CSWGetRecords("gmd:MD_Metadata",
				"csw:IsoRecord");
		CSWrequest.setMaxRecords(maxRecords);
		CSWrequest.setStartPosition(startPosition);
		CSWrequest.setSort(sort);
		CSWrequest.setDir(dir);

		String aCSWQuery = CSWrequest.createQuery(queryParams);

		return aCSWQuery;
	}

}
