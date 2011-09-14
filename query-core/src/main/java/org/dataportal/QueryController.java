/**
 * 
 */
package org.dataportal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dataportal.csw.CSWCatalog;
import org.dataportal.csw.CSWGetRecordById;
import org.dataportal.csw.CSWGetRecords;
import org.dataportal.csw.CSWNamespaceContext;
import org.dataportal.utils.BBox;
import org.dataportal.utils.RangeDate;
import org.dataportal.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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

		Properties queryCoreProp = new Properties();
		try {
			queryCoreProp.load(QueryController.class
					.getResourceAsStream("/query-core.properties"));
			String urlCSW = queryCoreProp.getProperty("urlCsw") + "/srv/en/csw";
			catalogo = new CSWCatalog(urlCSW);
		} catch (IOException e) {
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
	 * Extract id's from client XML request. Checks whether id's are in the
	 * server using a GetRecordById request. If checking is OK, extract the
	 * URL's from client XML request and sends to DownloadController to download
	 * files
	 * 
	 * @param InputStream
	 *            with the XML sends by client
	 */
	public String askgn2download(InputStream isRequestXML) {

		StringBuffer response = new StringBuffer();

		try {
			DocumentBuilderFactory dbFactoria = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dbBuilder = dbFactoria.newDocumentBuilder();
			Document downloadXML = (Document) dbBuilder.parse(isRequestXML);

			CSWNamespaceContext ctx = new CSWNamespaceContext();

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(ctx);

			String variablesExpr = "//id/child::node()";
			NodeList idNodeList = (NodeList) xpath.evaluate(variablesExpr,
					downloadXML, XPathConstants.NODESET);
			ArrayList<String> requestIdes = Utils
					.nodeList2ArrayList(idNodeList);

			CSWGetRecordById getRecordById = new CSWGetRecordById("brief");
			String getRecordByIdQuery = getRecordById.createQuery(requestIdes);
			InputStream isGetRecordByIdResponse = catalogo
					.sendCatalogRequest(getRecordByIdQuery);

			// TODO Controlar excepción retornada del servidor

			ArrayList<String> responseIdes = recordIdes(isGetRecordByIdResponse);

			ArrayList<String> noIdsResponse = Utils.compare2Arraylist(
					requestIdes, responseIdes);

			if (noIdsResponse.size() != 0) {
				logger.info("ID'S NO ENCONTRADOS: "
						+ String.valueOf(noIdsResponse.size()) + " -> "
						+ StringUtils.join(noIdsResponse, " : "));
				QueryError error = new QueryError();
				// TODO change error code
				error.setCode("ides.no.encontrados");
				error.setMessage(StringUtils.join(noIdsResponse, " : "));

				response.append(error.getErrorMessage());
			} else {
				xpath.reset();
				xpath.setNamespaceContext(ctx);
				String urlsExpr = "//data_link/child::node()";
				NodeList urlNodeList = (NodeList) xpath.evaluate(urlsExpr,
						downloadXML, XPathConstants.NODESET);

				// TODO Enviar volumen descarga al usuario y actuar en función

				ArrayList<String> urlsRequest = Utils
						.nodeList2ArrayList(urlNodeList);

				String resutlDownload = DownloadController
						.downloadDatasets(urlsRequest);

				response.append(resutlDownload);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			QueryError error = new QueryError();
			error.setCode("error.ejecucion.servidor");
			error.setMessage(e.getMessage());
			response.append(error.getErrorMessage());

			e.printStackTrace();
		}

		logger.debug(response.toString());

		return response.toString();
	}

	/**
	 * 
	 * Extract an array with id's from GetRecordByID response from CSW Server
	 * 
	 * @param InputStream
	 *            with the response sends by server
	 * @return ArrayList with id's from response
	 */
	private ArrayList<String> recordIdes(InputStream isGetRecordByIdResponse) {

		NodeList ides = null;
		ArrayList<String> arrayIdes = new ArrayList<String>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document xmlGetRecordsById = (Document) dBuilder
					.parse(isGetRecordByIdResponse);

			CSWNamespaceContext ctx = new CSWNamespaceContext();

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(ctx);

			String recordsExpr = "//BriefRecord/identifier/child::node()";
			ides = (NodeList) xpath.evaluate(recordsExpr, xmlGetRecordsById,
					XPathConstants.NODESET);
			if (ides.getLength() != 0)
				arrayIdes = Utils.nodeList2ArrayList(ides);

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		logger.debug("RECORDS: " + String.valueOf(arrayIdes.size()));

		return arrayIdes;
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
