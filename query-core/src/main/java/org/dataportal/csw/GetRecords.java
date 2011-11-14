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
public class GetRecords {

	private static Logger logger = Logger.getLogger(GetRecords.class);

	public static final String SERVICE = "CSW";
	public static final String CSWVERSION = "2.0.2";
	private static final String XMLENCODING = "UTF-8";
	private static final String XMLVERSION = "1.0";
	private static final String CSWNAMESPACE = "csw";
	private static final String GMDNAMESPACE = "gmd";

	private static final String LF = "\n";

	private CSWNamespaceContext namespacecontext = null;

	private String resulType = "hits";
	private String outputFormat = "application/xml";
	private String outputSchema = "http://www.opengis.net/cat/csw/2.0.2";
	private String maxRecords = "10";
	private String startPosition = "1";

	private String typeNames = null;
	private String elementSetName = null;
	private String constraintVersion = "1.0.1";

	/**
	 * @return the constraintVersion
	 */
	public String getConstraintVersion() {
		return constraintVersion;
	}

	/**
	 * @param constraintVersion the constraintVersion to set
	 */
	public void setConstraintVersion(String constraintVersion) {
		this.constraintVersion = constraintVersion;
	}

	/**
	 * @return the elementSetName
	 */
	public String getElementSetName() {
		return elementSetName;
	}

	/**
	 * @param elementSetName
	 *            the elementSetName to set
	 */
	public void setElementSetName(String elementSetName) {
		this.elementSetName = elementSetName;
	}

	/**
	 * @return the typeNames
	 */
	public String getTypeNames() {
		return typeNames;
	}

	/**
	 * @param typeNames
	 *            the typeNames to set
	 */
	public void setTypeNames(String typeNames) {
		this.typeNames = typeNames;
	}

	/**
	 * @return the resulType
	 */
	public String getResulType() {
		return resulType;
	}

	/**
	 * @param resulType
	 *            the resulType to set
	 */
	public void setResulType(String resulType) {
		this.resulType = resulType;
	}

	/**
	 * @return the outputFormat
	 */
	public String getOutputFormat() {
		return outputFormat;
	}

	/**
	 * @param outputFormat
	 *            the outputFormat to set
	 */
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	/**
	 * @return the outputSchema
	 */
	public String getOutputSchema() {
		return outputSchema;
	}

	/**
	 * @param outputSchema
	 *            the outputSchema to set
	 */
	public void setOutputSchema(String outputSchema) {
		this.outputSchema = outputSchema;
	}

	/**
	 * @return the maxRecords
	 */
	public String getMaxRecords() {
		return maxRecords;
	}

	/**
	 * @param maxRecords
	 *            the maxRecords to set
	 */
	public void setMaxRecords(String maxRecords) {
		this.maxRecords = maxRecords;
	}

	/**
	 * @return the startPosition
	 */
	public String getStartPosition() {
		return startPosition;
	}

	/**
	 * @param startPosition
	 *            the startPosition to set
	 */
	public void setStartPosition(String startPosition) {
		this.startPosition = startPosition;
	}

	/**
	 * @return the namespacecontext
	 */
	public CSWNamespaceContext getNamespacecontext() {
		return namespacecontext;
	}

	/**
	 * @param namespacecontext
	 *            the namespacecontext to set
	 */
	public void setNamespacecontext(CSWNamespaceContext namespacecontext) {
		this.namespacecontext = namespacecontext;
	}

	public String getExpresion() throws XMLStreamException {

		XMLOutputFactory xmlFactoria = XMLOutputFactory.newInstance();
		StringWriter strWriter = new StringWriter();
		XMLStreamWriter xmlWriter = xmlFactoria
				.createXMLStreamWriter(strWriter);
		xmlWriter.setNamespaceContext(namespacecontext);
		xmlWriter.writeStartDocument(XMLENCODING, XMLVERSION);

		// GetRecords element
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(CSWNAMESPACE), "GetRecords");
		xmlWriter.writeAttribute("service", SERVICE);
		xmlWriter.writeAttribute("version", CSWVERSION);
		xmlWriter.writeAttribute("resulType", resulType);
		xmlWriter.writeAttribute("outputFormat", outputFormat);
		xmlWriter.writeAttribute("outputSchema", outputSchema);
		xmlWriter.writeNamespace("csw",
				namespacecontext.getNamespaceURI(CSWNAMESPACE));
		xmlWriter.writeNamespace("gmd",
				namespacecontext.getNamespaceURI(GMDNAMESPACE));
		xmlWriter.writeAttribute("maxRecords", maxRecords);
		xmlWriter.writeAttribute("starPosition", startPosition);

		// Query element
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(CSWNAMESPACE), "Query");
		xmlWriter.writeAttribute("typeNames", typeNames);

		// ElementSetname element
		if (elementSetName != null) {
			xmlWriter.writeDTD(LF);
			xmlWriter.writeStartElement(
					namespacecontext.getNamespaceURI(CSWNAMESPACE),
					"ElementSetName");
			xmlWriter.writeCharacters(elementSetName);
			xmlWriter.writeEndElement();
			xmlWriter.writeDTD(LF);
		}

		// Constraint element
		xmlWriter.writeStartElement(namespacecontext
				.getNamespaceURI(CSWNAMESPACE), "Constraint");
		xmlWriter.writeAttribute("version", constraintVersion);
		
		// Filter
		// TODO
		
		xmlWriter.writeDTD(LF);
		xmlWriter.writeEndElement();
		xmlWriter.writeDTD(LF);
		xmlWriter.writeEndElement();
		xmlWriter.writeDTD(LF);
		xmlWriter.writeEndElement();

		logger.debug(strWriter.toString());

		return strWriter.toString();
	}
}
