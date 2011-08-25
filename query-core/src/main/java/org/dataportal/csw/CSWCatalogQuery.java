/**
 * 
 */
package org.dataportal.csw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.dataportal.utils.BBox;
import org.dataportal.utils.RangeDate;

/**
 * @author Micho Garcia
 * 
 */
public class CSWCatalogQuery {

	private static Logger logger = Logger.getLogger(CSWCatalogQuery.class);

	private static final String CSWVERSION = "2.0.2";
	private static final String XMLENCODING = "UTF-8";
	private static final String RESULTTYPE = "results";

	private String outputSchema = "";
	private String typeNames = "";
	private String startPosition = "1";
	private String maxRecords = "10";

	private StringBuffer bodyQuery;

	/**
	 * @param params
	 */
	public CSWCatalogQuery(String typeNames, String outputSchema) {
		this.outputSchema = outputSchema;
		this.typeNames = typeNames;

		bodyQuery = new StringBuffer(createHeadersRequest());
	}

	/**
	 * @param params
	 */
	public CSWCatalogQuery(String typeNames) {
		this.typeNames = typeNames;

		bodyQuery = new StringBuffer(createHeadersRequest());
	}

	/**
	 * @param startPosition
	 *            the startPosition to set
	 * 
	 *            TODO cambiar esto en cliente
	 */
	public void setStartPosition(String startPosition) {
		if (startPosition.equals("0"))
			this.startPosition = "1";
		else
			this.startPosition = startPosition;
	}

	/**
	 * @param maxRecords
	 *            the maxRecords to set
	 */
	public void setMaxRecords(String maxRecords) {
		this.maxRecords = maxRecords;
	}

	/**
	 * Create a header request string for GetRecords
	 * 
	 * @return A properly formatted request string
	 * 
	 */
	private String createHeadersRequest() {

		String outputSchemaString = " ";
		if (this.outputSchema != "") {
			outputSchemaString = " outputSchema=\"" + this.outputSchema
					+ "\"\n";
		}

		String headerRequest = "<csw:GetRecords " + "service=\"CSW\" "
				+ "version=\"" + CSWCatalogQuery.CSWVERSION + "\" "
				+ "resultType=\"" + CSWCatalogQuery.RESULTTYPE + "\" "
				+ "outputFormat=\"application/xml\"" + outputSchemaString
				+ "xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" "
				+ "xmlns:csw=\"http://www.opengis.net/cat/csw/"
				+ CSWCatalogQuery.CSWVERSION + "\" maxRecords=\""
				+ this.maxRecords + "\" " + "startPosition=\""
				+ this.startPosition + "\">\n" + this.createHeadersQuery()
				+ "</csw:GetRecords>";

		// logger.debug(headerRequest);

		return headerRequest;
	}

	/**
	 * 
	 * Create a header filter query
	 * 
	 * @return String
	 */
	private String createHeadersQuery() {

		String queryHeader = "<csw:Query typeNames=\"" + this.typeNames
				+ "\">\n" + "<csw:ElementSetName>full</csw:ElementSetName>\n"
				+ "<csw:Constraint version=\"1.1.0\">\n"
				+ "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" "
				+ "xmlns:gml=\"http://www.opengis.net/gml\">\n" + "$"
				+ "</ogc:Filter>\n</csw:Constraint>\n</csw:Query>\n";

		// logger.debug(queryHeader);

		return queryHeader;
	}

	/**
	 * 
	 * Create the query's body with the params
	 * 
	 * @param queryParams
	 * @return String
	 */
	public String createQuery(HashMap<String, Object> queryParams) {

		StringBuffer query = new StringBuffer();
		boolean moreThanOneParams = false;

		if (queryParams.size() > 1) {
			query.append("<ogc:And>\n");
			moreThanOneParams = true;
		}

		if (queryParams.containsKey("bboxes")) {
			@SuppressWarnings("unchecked")
			ArrayList<BBox> bboxes = (ArrayList<BBox>) queryParams
					.get("bboxes");
			query.append(bboxes2CSWquery(bboxes));
		}

		if (queryParams.containsKey("temporalExtent")) {
			RangeDate temporalExtent = (RangeDate) queryParams
					.get("temporalExtent");
			query.append(temporalExtent.toOGCTemporalExtent());
		}

		if (queryParams.containsKey("variables")) {
			// TODO
		}

		if (queryParams.containsKey("text")) {
			String text = (String) queryParams.get("text");
			query.append(freeText2Query(text));

		}

		if (moreThanOneParams)
			query.append("</ogc:And>\n");

		// logger.debug(query.toString());
		bodyQuery.insert(bodyQuery.indexOf("$"), query).deleteCharAt(
				bodyQuery.indexOf("$"));
		logger.debug("CSW QUERY: " + bodyQuery.toString());

		return bodyQuery.toString();
	}

	/**
	 * Convert bboxes array into OGC text to query
	 * 
	 * @param query
	 * @param bboxes
	 */
	private String bboxes2CSWquery(
			ArrayList<BBox> bboxes) {
		
		String query = "";

		boolean moreThanOne = false;
		if (bboxes.size() > 1) {
			query = query.concat("<ogc:Or>\n");
			moreThanOne = true;
		}
		for (BBox bbox : bboxes) {
			String strBbox = bbox.toOGCBBox();
			query = query.concat(strBbox);
		}
		if (moreThanOne)
			query = query.concat("</ogc:Or>\n");

		return query;
	}

	/**
	 * Convert a text into OGC property
	 * 
	 * @param text
	 * @return String
	 */
	private String freeText2Query(String text) {

		String freeText = "<ogc:PropertyIsLike wildCard=\"%\" singleChar=\"_\" escapeChar=\"\\\\\">\n"
				+ "<ogc:PropertyName>AnyText</ogc:PropertyName>\n"
				+ "<ogc:Literal>"
				+ text
				+ "</ogc:Literal>\n</ogc:PropertyIsLike>\n";

		return freeText;
	}
}
