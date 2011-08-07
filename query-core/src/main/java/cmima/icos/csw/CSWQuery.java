/**
 * 
 */
package cmima.icos.csw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cmima.icos.utils.BBox;
import cmima.icos.utils.RangeDate;

/**
 * @author Micho Garcia
 * 
 */
public class CSWQuery {

	private static Logger logger = Logger.getLogger(CSWQuery.class);

	private static final String CSWVERSION = "2.0.2";
	private static final String XMLENCODING = "UTF-8";
	private static final String RESULTTYPE = "results";

	private String outputSchema = "";
	private String typeNames = "";

	private StringBuffer bodyQuery;

	/**
	 * @param params
	 */
	public CSWQuery(String typeNames, String outputSchema) {
		this.outputSchema = outputSchema;
		this.typeNames = typeNames;

		bodyQuery = new StringBuffer(createHeadersRequest());
	}

	/**
	 * @param params
	 */
	public CSWQuery(String typeNames) {
		this.typeNames = typeNames;

		bodyQuery = new StringBuffer(createHeadersRequest());
	}

	/**
	 * Create a header request string for GetRecords
	 * 
	 * @param id
	 *            is the ID of the record to get
	 * @param cat
	 *            Catalog The catalog to get the record from
	 * @return A properly formatted request string
	 * @throws IOException
	 */
	private String createHeadersRequest() {

		String outputSchemaString = " ";
		if (this.outputSchema != "") {
			outputSchemaString = " outputSchema=\"" + this.outputSchema
					+ "\"\n";
		}

		String headerRequest = "<csw:GetRecords " + "service=\"CSW\" "
				+ "version=\"" + CSWQuery.CSWVERSION + "\" " + "resultType=\""
				+ CSWQuery.RESULTTYPE + "\" "
				+ "outputFormat=\"application/xml\"" + outputSchemaString
				+ "xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" "
				+ "xmlns:csw=\"http://www.opengis.net/cat/csw/"
				+ CSWQuery.CSWVERSION + "\">\n" + this.createHeadersQuery()
				+ "</csw:GetRecords>";

		// logger.debug(headerRequest);

		return headerRequest;
	}

	/**
	 * 
	 * Create a header query
	 * 
	 * @param typeNames
	 * @return String
	 */
	private String createHeadersQuery() {

		String queryHeader = "<csw:Query typeNames=\"" + this.typeNames
				+ "\">\n" + "<csw:Constraint version=\"1.1.0\">\n"
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

			query = bboxes2CSWquery(query, bboxes);
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
			String text = (String)queryParams.get("text");
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
	 * @param query
	 * @param bboxes
	 */
	private StringBuffer bboxes2CSWquery(StringBuffer query,
			ArrayList<BBox> bboxes) {

		boolean moreThanOne = false;
		if (bboxes.size() > 1) {
			query.append("<ogc:Or>\n");
			moreThanOne = true;
		}
		for (BBox bbox : bboxes) {
			String strBbox = bbox.toOGCBBox();
			query.append(strBbox);
		}
		if (moreThanOne)
			query.append("</ogc:Or>\n");

		return query;
	}

	/**
	 * @param text
	 * @return
	 */
	private String freeText2Query(String text) {

		String freeText = "<ogc:PropertyIsLike wildCard=\"%\" singleChar=\"_\" escape=\"\\\\\">\n"
				+ "<ogc:PropertyName>AnyText</ogc:PropertyName>\n"
				+ "<ogc:Literal>"
				+ text
				+ "</ogc:Literal>\n</ogc:PropertyIsLike>\n";
		
		return freeText;
	}
}
