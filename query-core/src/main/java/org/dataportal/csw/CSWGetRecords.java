/**
 * 
 */
package org.dataportal.csw;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.dataportal.utils.BBox;
import org.dataportal.utils.RangeDate;

/**
 * @author Micho Garcia
 * 
 */
public class CSWGetRecords {

	private static Logger logger = Logger.getLogger(CSWGetRecords.class);

	private static final String CSWVERSION = "2.0.2";
	private static final String XMLENCODING = "UTF-8";
	private static final String RESULTTYPE = "results";

	private String outputSchema = "";
	private String typeNames = "";
	private String startPosition = "1";
	private String maxRecords = "10";
	private String sort = "";
	private String dir = "";

	/**
	 * @param params
	 */
	public CSWGetRecords(String typeNames, String outputSchema) {
		this.outputSchema = outputSchema;
		this.typeNames = typeNames;
	}

	/**
	 * @param params
	 */
	public CSWGetRecords(String typeNames) {
		this.typeNames = typeNames;
	}

	/**
	 * @param startPosition
	 *            the startPosition to set
	 * 
	 */
	public void setStartPosition(String startPosition) {
		Integer tempStartPosition = Integer.parseInt(startPosition) + 1;
		this.startPosition = tempStartPosition.toString();
	}

	/**
	 * @param maxRecords
	 *            the maxRecords to set
	 */
	public void setMaxRecords(String maxRecords) {
		this.maxRecords = maxRecords;
	}

	/**
	 * @param sort
	 *            the sort to set
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}

	/**
	 * @param dir
	 *            the dir to set
	 */
	public void setDir(String dir) {
		this.dir = dir;

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
				+ "version=\"" + CSWGetRecords.CSWVERSION + "\" "
				+ "resultType=\"" + CSWGetRecords.RESULTTYPE + "\" "
				+ "outputFormat=\"application/xml\"" + outputSchemaString
				+ "xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" "
				+ "xmlns:csw=\"http://www.opengis.net/cat/csw/"
				+ CSWGetRecords.CSWVERSION + "\" maxRecords=\""
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
				+ "</ogc:Filter>\n</csw:Constraint>\n" + this.createSortBy()
				+ "</csw:Query>\n";

		// logger.debug(queryHeader);

		return queryHeader;
	}

	/**
	 * 
	 * Create CSW sort property
	 * 
	 * @returnString
	 */
	private String createSortBy() {

		String sortByCSW = "<ogc:SortBy xmlns:ogc=\"http://www.opengis.net/ogc\">\n"
				+ "<ogc:SortProperty>\n<ogc:PropertyName>"
				+ sort
				+ "</ogc:PropertyName>\n<ogc:SortOrder>"
				+ dir
				+ "</ogc:SortOrder>\n</ogc:SortProperty>\n</ogc:SortBy>\n";

		return sortByCSW;
	}

	/**
	 * 
	 * Create the query's body with the params
	 * 
	 * @param queryParams
	 * @return String
	 */
	public String createQuery(HashMap<String, Object> queryParams) {

		StringBuffer bodyQuery = new StringBuffer(createHeadersRequest());

		StringBuffer query = new StringBuffer();
		boolean moreThanOneParams = false;

		if (queryParams.size() > 1) {
			query.append("<ogc:And>\n");
			moreThanOneParams = true;
		}

		// bboxes
		if (queryParams.containsKey("bboxes")) {
			@SuppressWarnings("unchecked")
			ArrayList<BBox> bboxes = (ArrayList<BBox>) queryParams
					.get("bboxes");
			query.append(bboxes2CSWquery(bboxes));
		}

		// temporal extent
		if (queryParams.containsKey("temporalExtent")) {
			RangeDate temporalExtent = (RangeDate) queryParams
					.get("temporalExtent");
			query.append(temporalExtent.toOGCTemporalExtent());
		}

		// variables
		if (queryParams.containsKey("variables")) {
			String variables[] = (String[]) queryParams.get("variables");
			query.append(variables2CSWQuery(variables));
		}

		// free text
		if (queryParams.containsKey("text")) {
			String text = (String) queryParams.get("text");
			query.append(freeText2CSWQuery(text));

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
	 * Create CSW Query from string array
	 * 
	 * @param variables
	 * @return
	 */
	private Object variables2CSWQuery(String[] variables) {

		String variablesCSW = "";

		boolean moreThanOne = false;
		if (variables.length > 1) {
			variablesCSW = variablesCSW.concat("<ogc:Or>\n");
			moreThanOne = true;
		}
		for (String variable : variables) {
			String queryVariables = "<ogc:PropertyIsLike wildCard=\"%\" singleChar=\"_\" escapeChar=\"\\\\\">\n"
					+ "<ogc:PropertyName>ContentInfo</ogc:PropertyName>\n"
					+ "<ogc:Literal>"
					+ variable
					+ "</ogc:Literal>\n</ogc:PropertyIsLike>\n";
			variablesCSW = variablesCSW.concat(queryVariables);
		}
		if (moreThanOne)
			variablesCSW = variablesCSW.concat("</ogc:Or>\n");

		return variablesCSW;
	}

	/**
	 * Convert bboxes array into OGC text to query
	 * 
	 * @param query
	 * @param bboxes
	 */
	private String bboxes2CSWquery(ArrayList<BBox> bboxes) {

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
	private String freeText2CSWQuery(String text) {

		String freeText = "<ogc:PropertyIsLike wildCard=\"%\" singleChar=\"_\" escapeChar=\"\\\\\">\n"
				+ "<ogc:PropertyName>AnyText</ogc:PropertyName>\n"
				+ "<ogc:Literal>"
				+ text
				+ "</ogc:Literal>\n</ogc:PropertyIsLike>\n";

		return freeText;
	}
}
