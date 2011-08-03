/**
 * 
 */
package cmima.icos.utils;

/**
 * @author Micho Garcia
 * 
 */
public class BBox {

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
}
