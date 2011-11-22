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
 * @author michogar
 *
 */
public class Operator {
	
	private static Logger logger = Logger.getLogger(Operator.class);

	private static final String OGCNAMESPACE = "ogc";
	private static final String LF = "\n";

	private DataPortalNS namespacecontext = new DataPortalNS();
	
	private ArrayList<String> rules;
	private String typeOperator;
	
	
	public Operator(String typeOperator) {
		super();
		this.typeOperator = typeOperator;
	}

	/**
	 * @return the typeOperator
	 */
	public String getTypeOperator() {
		return typeOperator;
	}

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
				namespacecontext.getNamespaceURI(OGCNAMESPACE),
				typeOperator);
		xmlWriter.writeCharacters("");
		xmlWriter.writeDTD(LF);
		
		for (String rule : rules) {
			strWriter.append(rule);
			xmlWriter.writeDTD(LF);
		}

		xmlWriter.writeEndElement();
		
		xmlWriter.flush();
		xmlWriter.close();		
		
		logger.debug(strWriter.toString());
		
		return strWriter.toString();		
	}
}
