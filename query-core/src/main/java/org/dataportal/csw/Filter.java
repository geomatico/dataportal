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
 * @author Micho Garcia
 *
 */
public class Filter {
	
	private static Logger logger = Logger.getLogger(Filter.class);
	
	private CSWNamespaceContext namespacecontext = new CSWNamespaceContext();
	
	private static final String OGCNAMESPACE = "ogc";
	private static final String GMLNAMESPACE = "gml";
	
	private static final String LF = "\n";
	
	private ArrayList<String> rules;

	/**
	 * @return the rules
	 */
	public ArrayList<String> getRules() {
		return rules;
	}

	/**
	 * @param rules the rules to set
	 */
	public void setRules(ArrayList<String> rules) {
		this.rules = rules;
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
		for (String rule : rules) {
			strWriter = strWriter.append(rule);
		}
		
		xmlWriter.writeDTD(LF);
		xmlWriter.writeEndElement();
		
		xmlWriter.flush();
		xmlWriter.close();
		
		logger.debug(strWriter.toString());
		
		return strWriter.toString();
	}

}
