/**
 * 
 */
package cmima.icos.utils;

import org.apache.log4j.Logger;

/**
 * @author Micho Garcia
 * 
 */
public class BBox {

	private static Logger logger = Logger.getLogger(BBox.class);

	private String xmax;
	private String xmin;
	private String ymax;
	private String ymin;

	/**
	 * 
	 * Create a bbox object from a array with coords. Is easier to work with
	 * this
	 * 
	 * @param coords
	 */
	public BBox(String[] coords) {
		this.xmin = coords[0];
		this.ymin = coords[1];
		this.xmax = coords[2];
		this.ymax = coords[3];

		logger.debug("BBOX - xmin: " + this.xmin + " ymin: " + this.ymin
				+ " xmax: " + this.xmax + " ymax: " + this.ymax);
	}

	/**
	 * @return the xmax
	 */
	public String getXmax() {
		return xmax;
	}

	/**
	 * @return the xmin
	 */
	public String getXmin() {
		return xmin;
	}

	/**
	 * @return the ymax
	 */
	public String getYmax() {
		return ymax;
	}

	/**
	 * @return the ymin
	 */
	public String getYmin() {
		return ymin;
	}
	
	/**
	 * @param bbox
	 * @return
	 */
	public String toOGCBBox() {
		
		String ogcBBox = "<ogc:BBOX>\n"
				+ "<ogc:PropertyName>iso:BoundingBox</ogc:PropertyName>\n"
				+ "<gml:Envelope xmlns:gml=\"http://www.opengis.net/gml\">\n";
		String lowerCorner = "<gml:lowerCorner>" + xmin + " "
				+ ymin + "</gml:lowerCorner>\n";
		String upperCorner = "<gml:upperCorner>" + xmax + " "
				+ ymax + "</gml:upperCorner>\n";
		String endGml = "</gml:Envelope>\n</ogc:BBOX>\n";

		String strBbox = ogcBBox + lowerCorner + upperCorner + endGml;
		
		return strBbox;
	}
}
