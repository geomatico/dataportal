/**
 * 
 */
package org.dataportal.utils;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.dataportal.csw.DataPortalNS;

/**
 * @author Micho Garcia
 * 
 */
public class BBox {

	private static Logger logger = Logger.getLogger(BBox.class);

	private DataPortalNS namespacecontext = new DataPortalNS();

	private static final String OGCNAMESPACE = "ogc";
	private static final String GMLNAMESPACE = "gml";

	private static final String LF = "\n";

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
	 * @throws XMLStreamException
	 */
	public String toOGCBBox() throws XMLStreamException {

		XMLOutputFactory xmlFactoria = XMLOutputFactory.newInstance();
		StringWriter strWriter = new StringWriter();
		XMLStreamWriter xmlWriter = xmlFactoria
				.createXMLStreamWriter(strWriter);
		xmlWriter.setNamespaceContext(namespacecontext);
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(OGCNAMESPACE), "BBOX");
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(OGCNAMESPACE), "PropertyName");
		xmlWriter.writeCharacters("iso:BoundingBox");
		xmlWriter.writeEndElement();
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(GMLNAMESPACE), "Envelope");
		xmlWriter.writeNamespace(GMLNAMESPACE,
				namespacecontext.getNamespaceURI(GMLNAMESPACE));
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(namespacecontext.getNamespaceURI(GMLNAMESPACE), "lowerCorner");
		xmlWriter.writeCharacters("".concat(xmin).concat(" ").concat(ymin));
		xmlWriter.writeEndElement();
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(namespacecontext.getNamespaceURI(GMLNAMESPACE), "upperCorner");
		xmlWriter.writeCharacters("".concat(xmax).concat(" ").concat(ymax));
		xmlWriter.writeEndElement();
		xmlWriter.writeDTD(LF);
		xmlWriter.writeEndElement();
		xmlWriter.writeDTD(LF);
		xmlWriter.writeEndElement();

		logger.debug(strWriter.toString());
		
		return strWriter.toString();
	}
}
