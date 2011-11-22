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
 * @author Micho Garcia
 * 
 */
public class Property {

	private static Logger logger = Logger.getLogger(Property.class);

	private String propertyName = null;
	private String literal = null;
	private String typeProperty = null;

	private static final String OGCNAMESPACE = "ogc";
	private static final String LF = "\n";

	private DataPortalNS namespacecontext = new DataPortalNS();

	public Property(String typeProperty) {
		super();
		this.typeProperty = typeProperty;
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

	/**
	 * @return the literal
	 */
	public String getLiteral() {
		return literal;
	}

	/**
	 * @param literal
	 *            the literal to set
	 */
	public void setLiteral(String literal) {
		this.literal = literal;
	}

	/**
	 * @return
	 * @throws XMLStreamException
	 */
	public String getExpresion() throws XMLStreamException {

		XMLOutputFactory xmlFactoria = XMLOutputFactory.newInstance();
		StringWriter strWriter = new StringWriter();
		XMLStreamWriter xmlWriter = xmlFactoria
				.createXMLStreamWriter(strWriter);
		xmlWriter.setNamespaceContext(namespacecontext);
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(OGCNAMESPACE),
				typeProperty);
		if (typeProperty.equals("PropertyIsLike")){
			xmlWriter.writeAttribute("wildCard", "%");
			xmlWriter.writeAttribute("singleChar", "_");
			xmlWriter.writeAttribute("escapeChar", "\\\\");
		}
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(OGCNAMESPACE), "PropertyName");
		xmlWriter.writeCharacters(getPropertyName());
		xmlWriter.writeEndElement();
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(OGCNAMESPACE), "Literal");
		xmlWriter.writeCharacters(getLiteral());
		xmlWriter.writeEndElement();
		xmlWriter.writeDTD(LF);
		xmlWriter.writeEndElement();
		
		xmlWriter.flush();
		xmlWriter.close();

		logger.debug(strWriter.toString());

		return strWriter.toString();
	}

}
