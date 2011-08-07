/**
 * 
 */
package cmima.icos.utils;

/**
 * @author Micho Garcia
 * 
 */
public class RangeDate {

	private String start_date;
	private String end_date;

	/**
	 * @return the start_date
	 */
	public String getStart_date() {
		return start_date;
	}

	/**
	 * @return the end_date
	 */
	public String getEnd_date() {
		return end_date;
	}

	public RangeDate(String start_date, String end_date) {
		this.start_date = start_date;
		this.end_date = end_date;
	}

	public String toOGCTemporalExtent() {

		String ogcTExtent = "<ogc:And>\n<ogc:PropertyIsGreaterThanOrEqualTo>\n<ogc:PropertyName>TempExtent_begin</ogc:PropertyName>\n<ogc:Literal>"
				+ start_date
				+ "</ogc:Literal>\n</ogc:PropertyIsGreaterThanOrEqualTo>\n"
				+ "<ogc:PropertyIsLessThanOrEqualTo>\n<ogc:PropertyName>TempExtent_end</ogc:PropertyName>\n<ogc:Literal>"
				+ end_date + "</ogc:Literal>\n</ogc:PropertyIsLessThanOrEqualTo>\n</ogc:And>\n";
		
		return ogcTExtent;
	}
}
