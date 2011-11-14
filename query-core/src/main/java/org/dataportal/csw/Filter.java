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
public class Filter {
	
	private static Logger logger = Logger.getLogger(Filter.class);
	
	private CSWNamespaceContext namespacecontext = null;
	
	private static final String OGCNAMESPACE = "ogc";
	private static final String GMLNAMESPACE = "gml";
	
	private static final String LF = "\n";
	
	private String rules;

	/**
	 * @return the rules
	 */
	public String getRules() {
		return rules;
	}

	/**
	 * @param rules the rules to set
	 */
	public void setRules(String rules) {
		this.rules = rules;
	}

	/**
	 * @return the namespacecontext
	 */
	public CSWNamespaceContext getNamespacecontext() {
		return namespacecontext;
	}

	/**
	 * @param namespacecontext the namespacecontext to set
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
		
		xmlWriter.writeStartElement(
				namespacecontext.getNamespaceURI(OGCNAMESPACE), "Filter");
		xmlWriter.writeNamespace("ogc",
				namespacecontext.getNamespaceURI(OGCNAMESPACE));
		xmlWriter.writeNamespace("gml",
				namespacecontext.getNamespaceURI(GMLNAMESPACE));
		
		xmlWriter.writeCharacters("");
		xmlWriter.writeDTD(LF);
		
		// rules
		strWriter = strWriter.append(rules);
		
		xmlWriter.writeDTD(LF);
		xmlWriter.writeEndElement();
		
		xmlWriter.flush();
		xmlWriter.close();
		
		logger.debug(strWriter.toString());
		
		return strWriter.toString();
	}

}
