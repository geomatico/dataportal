/**
 * 
 */
package org.dataportal.csw;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;

/**
 * Class to create a CSW Standard query GetRecordById
 * 
 * @author Micho Garcia
 * 
 */
public class GetRecordById {

	private static Logger logger = Logger.getLogger(GetRecordById.class);

	public static final String SERVICE = "CSW";
	public static final String CSW_VERSION = "2.0.2";
	private static final String XMLENCODING = "UTF-8";
	private static final String XMLVERSION = "1.0";

	private static final String NAMESPACE_CSW = "http://www.opengis.net/cat/csw/2.0.2";

	private static final String LF = "\n";

	// ElementSetName
	public static final String BRIEF = "brief";
	public static final String SUMMARY = "summary";
	public static final String FULL = "full";

	// OutputFormat
	public static final String OUTPUTFORMAT = "application/xml";

	// ResultType
	public static final String HITS = "hits";
	public static final String RESULTS = "results";
	public static final String VALIDATE = "validate";

	private DataPortalNS namespacecontext = new DataPortalNS();
	private String elementSetName;
	private String outputFormat;
	private String outputSchema = "";

	/**
	 * Constructor with ElementSetName
	 * 
	 * @param ElementSetName
	 */
	public GetRecordById(String elementSetNameValue) {

		super();
		String elementSName;
		if (elementSetNameValue.equals(FULL))
			elementSName = FULL;
		else if (elementSetNameValue.equals(SUMMARY))
			elementSName = SUMMARY;
		else
			elementSName = BRIEF;
		elementSetName = elementSName;
		outputFormat = OUTPUTFORMAT;
		outputSchema = NAMESPACE_CSW;
	}

	/**
	 * @return the namespacecontext
	 */
	public DataPortalNS getNamespacecontext() {
		return namespacecontext;
	}

	/**
	 * @param namespacecontext the namespacecontext to set
	 */
	public void setNamespacecontext(DataPortalNS namespacecontext) {
		this.namespacecontext = namespacecontext;
	}

	/**
	 * @return the outputFormat
	 */
	public String getOutputFormat() {
		return outputFormat;
	}

	/**
	 * @param outputFormat the outputFormat to set
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
	 * @param outputSchema the outputSchema to set
	 */
	public void setOutputSchema(String outputSchema) {
		this.outputSchema = outputSchema;
	}

	/**
	 * Create a GetRecordById query with all id's in arrayList
	 * 
	 * @param ArrayList with id's
	 * @throws XMLStreamException
	 * 
	 */
	public String createQuery(ArrayList<String> ides) throws XMLStreamException {

		XMLOutputFactory xmlFactoria = XMLOutputFactory.newInstance();
		StringWriter strWriter = new StringWriter();
		XMLStreamWriter xmlWriter = xmlFactoria
				.createXMLStreamWriter(strWriter);
		xmlWriter.setNamespaceContext(namespacecontext);
		xmlWriter.writeStartDocument(XMLENCODING, XMLVERSION);
		xmlWriter.writeDTD(LF);
		xmlWriter.writeStartElement(NAMESPACE_CSW, "GetRecordById");
		xmlWriter.writeNamespace("csw", NAMESPACE_CSW);
		xmlWriter.writeAttribute("service", SERVICE);
		xmlWriter.writeAttribute("version", CSW_VERSION);
		xmlWriter.writeAttribute("outputFormat", outputFormat);

		xmlWriter.writeAttribute("outputSchema", outputSchema);

		for (String ide : ides) {
			xmlWriter.writeStartElement(NAMESPACE_CSW, "Id");
			xmlWriter.writeCharacters(ide);
			xmlWriter.writeEndElement();
			xmlWriter.writeDTD(LF);
		}
		xmlWriter.writeStartElement(NAMESPACE_CSW, "ElementSetName");
		xmlWriter.writeCharacters(elementSetName);
		xmlWriter.writeEndElement();
		xmlWriter.writeDTD(LF);
		xmlWriter.writeEndElement();
		xmlWriter.flush();
		xmlWriter.close();

		logger.debug(strWriter.toString());

		return strWriter.toString();
	}
}
