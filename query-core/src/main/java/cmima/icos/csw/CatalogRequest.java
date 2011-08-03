/**
 * 
 */
package cmima.icos.csw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cmima.icos.utils.BBox;

/**
 * @author Micho Garcia
 * 
 */
public class CatalogRequest {

	private static Logger logger = Logger.getLogger(CatalogRequest.class);

	private static final String CSWVERSION = "2.0.2";
	private static final String XMLENCODING = "UTF-8";
	private static final String RESULTTYPE = "results";

	private String outputSchema;
	private String typeNames;

	private StringBuffer bodyQuery;

	/**
	 * @param params
	 */
	public CatalogRequest(String outputSchema, String typeNames) {
		this.outputSchema = outputSchema;
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

		String schemaString = "";
		schemaString = "outputSchema=\"" + this.outputSchema + "\"\n";

		//"<?xml version=\"1.0\" encoding=\""
		//+ CatalogRequest.XMLENCODING + "\"?> \n" +
		String headerRequest = "<csw:GetRecords "
				+ "service=\"CSW\" " + "version=\"" + CatalogRequest.CSWVERSION
				+ "\" " + "resultType=\"" + CatalogRequest.RESULTTYPE + "\" "
				+ "outputFormat=\"application/xml\" " + schemaString
				+ "xmlns:csw=\"http://www.opengis.net/cat/csw/"
				+ CatalogRequest.CSWVERSION + "\"> \n"
				+ this.createHeadersQuery() + "</csw:GetRecords>";

		//logger.debug(headerRequest);

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
				+ "\"> \n" + "<csw:Constraint version=\"1.1.0\"> \n"
				+ "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" "
				+ "xmlns:gml=\"http://www.opengis.net/gml\"> \n" + "$"
				+ "</ogc:Filter> \n </csw:Constraint> \n </csw:Query> \n";

		//logger.debug(queryHeader);

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

		if (queryParams.containsKey("bboxes")) {			
			
			ArrayList<BBox> bboxes = (ArrayList<BBox>) queryParams
					.get("bboxes");
			
			query = bboxes2CSWquery(query, bboxes);
		}
		
		//logger.debug(query.toString());
		bodyQuery.insert(bodyQuery.indexOf("$"), query).deleteCharAt(bodyQuery.indexOf("$"));
		logger.debug("CSW QUERY: " + bodyQuery.toString());
		
		return bodyQuery.toString();
	}

	/**
	 * @param query
	 * @param bboxes
	 */
	private StringBuffer bboxes2CSWquery(StringBuffer query, ArrayList<BBox> bboxes) {
		
		boolean moreThanOne = false;
		if (bboxes.size() > 1) {
			query.append("<ogc:Or> \n");
			moreThanOne = true;
		}
		for (BBox bbox : bboxes) {
			String ogcBBox = "<ogc:BBOX> \n"
					+ "<ogc:PropertyName>iso:BoundingBox</ogc:PropertyName> \n"
					+ "<gml:Envelope xmlns:gml=\"http://www.opengis.net/gml\"> \n";
			String lowerCorner = "<gml:lowerCorner>" + bbox.getXmin() + " "
					+ bbox.getYmin() + "</gml:lowerCorner> \n";
			String upperCorner = "<gml:upperCorner>" + bbox.getXmax() + " "
					+ bbox.getYmax() + "</gml:upperCorner> \n";
			String endGml = "</gml:Envelope> \n </ogc:BBOX> \n";

			query.append(ogcBBox).append(lowerCorner).append(upperCorner)
					.append(endGml);
		}
		if (moreThanOne)
			query.append("</ogc:Or> \n");
		
		return query;
	}

}
