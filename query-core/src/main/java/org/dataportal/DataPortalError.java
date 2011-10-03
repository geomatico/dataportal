/**
 * 
 */
package org.dataportal;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * 
 * Class to manage error from server to client dataportal
 * 
 * @author Micho Garcia
 * 
 */
public class DataPortalError {

	private static final String XMLENCODING = "UTF-8";
	private static final String XMLVERSION = "1.0";

	private static final String LF = "\n";

	private String message;
	private String code;

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Get the XML error message 
	 * 
	 * @return String with XML error
	 */
	public String getErrorMessage() {

		StringWriter strWriter = new StringWriter();
		
		try {
			XMLOutputFactory xmlFactoria = XMLOutputFactory.newInstance();			
			XMLStreamWriter xmlWriter = xmlFactoria
					.createXMLStreamWriter(strWriter);
			xmlWriter.writeStartDocument(XMLENCODING, XMLVERSION);
			xmlWriter.writeDTD(LF);
			xmlWriter.writeStartElement("response");
			xmlWriter.writeAttribute("sucess", "false");
			xmlWriter.writeDTD(LF);
			xmlWriter.writeStartElement("error");
			xmlWriter.writeDTD(LF);
			xmlWriter.writeStartElement("code");
			xmlWriter.writeCharacters(code);
			xmlWriter.writeEndElement();
			xmlWriter.writeDTD(LF);
			xmlWriter.writeStartElement("message");
			xmlWriter.writeCharacters(message);
			xmlWriter.writeEndElement();
			xmlWriter.writeDTD(LF);
			xmlWriter.writeEndElement();
			xmlWriter.writeDTD(LF);
			xmlWriter.writeEndElement();
		} catch (XMLStreamException xmlE) {
			xmlE.printStackTrace();
		}

		return strWriter.toString();
	}
}
