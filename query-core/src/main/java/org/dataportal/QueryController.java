/**
 * 
 */
package org.dataportal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dataportal.controllers.JPADownloadController;
import org.dataportal.csw.Catalog;
import org.dataportal.csw.Filter;
import org.dataportal.csw.GetRecordById;
import org.dataportal.csw.GetRecords;
import org.dataportal.csw.Operator;
import org.dataportal.csw.Property;
import org.dataportal.csw.SortBy;
import org.dataportal.model.Download;
import org.dataportal.model.DownloadItem;
import org.dataportal.utils.BBox;
import org.dataportal.utils.DataPortalException;
import org.dataportal.utils.Utils;

/**
 * Controller manage client petitions to server and manage responses from server
 * too. Use Xpath to extract information from XML request and responses and XSLT
 * to transform this to client
 * 
 * @author Micho Garcia
 * 
 */
public class QueryController extends DataPortalController {

	private static Logger logger = Logger.getLogger(QueryController.class);

	private static final int FIRST = 0;

	/**
	 * Constructor. Assign URL catalog server
	 * 
	 * @throws MalformedURLException
	 */
	public QueryController() throws MalformedURLException {
		super();
	}

	/**
	 * Receive the params from the client request and communicates these to
	 * CSWCatalog
	 * 
	 * @param parametros
	 * @return
	 * @throws Exception
	 */
	public String ask2gn(Map<String, String[]> parametros) throws Exception {

		InputStream isCswResponse = null;
		String response = "";

		// id
		if (parametros.get("id") != null) {
			String ddi = parametros.get("id")[FIRST];
			response = getItemsDDI(ddi);
		} else {
			String aCSWQuery = params2Query(parametros);

			isCswResponse = catalogo.sendCatalogRequest(aCSWQuery);
			response = transform(isCswResponse);
			isCswResponse.close();

			logger.debug("RESPONSE2CLIENT: " + response);
		}

		return response;
	}

	private String getItemsDDI(String ddi) throws Exception {

		Download download = new Download(ddi);
		downloadJPAController = new JPADownloadController();
		ArrayList<DownloadItem> items = downloadJPAController
				.getDownloadItems(download);
		if (items.size() == 0) {
			dtException = new DataPortalException("DDI not found or not items");
			dtException.setCode(DDINOTFOUND);
			throw dtException;
		}
		ArrayList<String> isItems = new ArrayList<String>();
		for (DownloadItem item : items) {
			isItems.add(item.getItemId());
		}
		GetRecordById getRecordById = new GetRecordById(GetRecordById.FULL);
		getRecordById.setOutputSchema("csw:IsoRecord");
		String cswQuery = getRecordById.createQuery(isItems);
		InputStream catalogResponse = catalogo.sendCatalogRequest(cswQuery);

		// TODO crear plantilla para respuesta GetRecordsById - modificar
		// respuesta server
		String strCatalogResponse = transform(catalogResponse);

		logger.debug("GetRecordsById RESPONSE: " + strCatalogResponse);

		return strCatalogResponse;
	}

	/**
	 * Transform the response from CSW Catalog into client response
	 * 
	 * @param isCswResponse
	 * @return String
	 * @throws TransformerException
	 * @throws IOException
	 * @throws DataPortalException
	 */
	private String transform(InputStream isCswResponse)
			throws TransformerException, IOException {

		StringWriter writer2Client = new StringWriter();
		InputStream isXslt = Catalog.class
				.getResourceAsStream("/response2client.xsl");

		Source responseSource = new StreamSource(isCswResponse);
		Source xsltSource = new StreamSource(isXslt);

		TransformerFactory transFact = TransformerFactory.newInstance();
		Templates template = transFact.newTemplates(xsltSource);
		Transformer transformer = template.newTransformer();

		transformer.transform(responseSource, new StreamResult(writer2Client));

		writer2Client.flush();
		writer2Client.close();

		isXslt.close();

		return writer2Client.toString();
	}

	/**
	 * Extract the params from a Map and create a Query in CSW 2.0.2 standard
	 * using GetRecords class
	 * 
	 * @param parametros
	 * @return String
	 */
	private String params2Query(Map<String, String[]> parametros) {

		try {

			ArrayList<String> filterRules = new ArrayList<String>();
			GetRecords getrecords = new GetRecords();
			getrecords.setResulType("results");
			getrecords.setOutputSchema("csw:IsoRecord");
			getrecords.setTypeNames("gmd:MD_Metadata");
			getrecords.setElementSetName(GetRecords.FULL);

			// bboxes
			Operator orBBox = new Operator("Or");

			String stringBBoxes = parametros.get("bboxes")[FIRST];
			ArrayList<BBox> bboxes = Utils.extractToBBoxes(stringBBoxes);
			if (bboxes != null) {
				if (bboxes.size() > 1) {
					ArrayList<String> rulesBBox = new ArrayList<String>();
					for (BBox bbox : bboxes) {
						rulesBBox.add(bbox.toOGCBBox());
					}
					orBBox.setRules(rulesBBox);
					filterRules.add(orBBox.getExpresion());
				} else {
					filterRules.add(bboxes.get(FIRST).toOGCBBox());
				}
			}

			// temporal range
			String start_date = parametros.get("start_date")[FIRST];
			Property greatherThanStartDate = new Property(
					"PropertyIsGreaterThanOrEqualTo");
			boolean existsStartDate = false;
			if (start_date != "") {
				greatherThanStartDate.setPropertyName("TempExtent_begin");
				greatherThanStartDate.setLiteral(start_date);
				existsStartDate = true;
			}

			String end_date = parametros.get("end_date")[FIRST];
			if (end_date != "") {
				Property lessThanEndDate = new Property(
						"PropertyIsLessThanOrEqualTo");
				lessThanEndDate.setPropertyName("TempExtent_end");
				lessThanEndDate.setLiteral(end_date);

				if (existsStartDate) {
					ArrayList<String> andRangeDateRules = new ArrayList<String>();
					andRangeDateRules.add(greatherThanStartDate.getExpresion());
					andRangeDateRules.add(lessThanEndDate.getExpresion());
					Operator andRangeDate = new Operator("And");
					andRangeDate.setRules(andRangeDateRules);
					filterRules.add(andRangeDate.getExpresion());
				} else {
					filterRules.add(lessThanEndDate.getExpresion());
				}
			} else {
				if (existsStartDate)
					filterRules.add(greatherThanStartDate.getExpresion());
			}

			// variables
			String variables = parametros.get("variables")[FIRST];
			if (variables != "") {
				String queryVariables[] = variables.split(",");

				if (queryVariables.length > 1) {
					Operator orVariables = new Operator("Or");
					ArrayList<String> arrayVariables = new ArrayList<String>();
					for (String aVariable : queryVariables) {
						Property propVariable = new Property("PropertyIsLike");
						propVariable.setPropertyName("ContentInfo");
						propVariable.setLiteral(aVariable);
						arrayVariables.add(propVariable.getExpresion());
					}
					orVariables.setRules(arrayVariables);
					filterRules.add(orVariables.getExpresion());
				} else {
					Property propVariable = new Property("PropertyIsLike");
					propVariable.setPropertyName("ContentInfo");
					propVariable.setLiteral(queryVariables[FIRST]);
					filterRules.add(propVariable.getExpresion());
				}
			}

			// free text
			String freeText = parametros.get("text")[FIRST];
			if (freeText != "") {
				Property propFreeText = new Property("PropertyIsLike");
				propFreeText.setPropertyName("AnyText");
				propFreeText.setLiteral(freeText);
				filterRules.add(propFreeText.getExpresion());
			}

			// Default pagination & ordering values
			String startPosition = "1";
			String maxRecords = "25";
			String sort = "title";
			String dir = "asc";

			startPosition = String.valueOf(Integer.valueOf(parametros
					.get("start")[FIRST]) + 1);
			getrecords.setStartPosition(startPosition);

			maxRecords = parametros.get("limit")[FIRST];
			getrecords.setMaxRecords(maxRecords);

			sort = parametros.get("sort")[FIRST];
			dir = parametros.get("dir")[FIRST];

			SortBy sortby = new SortBy();
			sortby.setPropertyName(sort);
			sortby.setOrder(dir);

			getrecords.setSortby(sortby);

			Filter filtro = new Filter();
			filtro.setRules(filterRules);

			getrecords.setFilter(filtro);

			logger.debug(getrecords.getExpresion());

			return getrecords.getExpresion();

		} catch (XMLStreamException e) {
			logger.error(e.getMessage());
		}

		return null;
	}
}
