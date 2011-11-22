/**
 * 
 */
package org.dataportal.csw;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;

/**
 * @author michogar
 * 
 */
public class SortBy {

	private static Logger logger = Logger.getLogger(SortBy.class);

	private static final String OGCNAMESPACE = "ogc";
	private static final String LF = "\n";

	public static final String ASC = "ASC";
	public static final String DESC = "DESC";

	private DataPortalNS namespacecontext = new DataPortalNS();
	private String order;
	private String propertyName;

	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName
	 *            the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getExpresion() throws XMLStreamException {

		XMLOutputFactory xmlFactoria = XMLOutputFactory.newInstance();
		StringWriter strWriter = new StringWriter();
		XMLStreamWriter xmlWriter = xmlFactoria
				.createXMLStreamWriter(strWriter);

		xmlWriter.setNamespaceContext(namespacecontext);

		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(OGCNAMESPACE), "SortBy");
		xmlWriter.writeNamespace("ogc",
				namespacecontext.getNamespaceURI(OGCNAMESPACE));
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(OGCNAMESPACE), "SortProperty");
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(OGCNAMESPACE), "PropertyName");
		xmlWriter.writeCharacters(propertyName);
		xmlWriter.writeEndElement();
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(OGCNAMESPACE), "SortOrder");
		xmlWriter.writeCharacters(order);
		xmlWriter.writeEndElement();

		xmlWriter.writeDTD(LF);
		xmlWriter.writeEndElement();
		xmlWriter.writeDTD(LF);
		xmlWriter.writeEndElement();

		logger.debug(strWriter.toString());

		return strWriter.toString();
	}
}
