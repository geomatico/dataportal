/**
 * 
 */
package org.dataportal;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;

/**
 * 
 * Class to manage error from server to client dataportal
 * 
 * @author Micho Garcia
 * 
 */
public class DataPortalError {
	
	private static Logger logger = Logger.getLogger(DataPortalError.class);

	private static final String XMLENCODING = "UTF-8"; //$NON-NLS-1$
	private static final String XMLVERSION = "1.0"; //$NON-NLS-1$

	private static final String LF = "\n"; //$NON-NLS-1$

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
			xmlWriter.writeStartElement("response"); //$NON-NLS-1$
			xmlWriter.writeAttribute("success", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			xmlWriter.writeDTD(LF);
			xmlWriter.writeStartElement("error"); //$NON-NLS-1$
			xmlWriter.writeDTD(LF);
			xmlWriter.writeStartElement("code"); //$NON-NLS-1$
			xmlWriter.writeCharacters(code);
			xmlWriter.writeEndElement();
			xmlWriter.writeDTD(LF);
			xmlWriter.writeStartElement("message"); //$NON-NLS-1$
			xmlWriter.writeCharacters(message);
			xmlWriter.writeEndElement();
			xmlWriter.writeDTD(LF);
			xmlWriter.writeEndElement();
			xmlWriter.writeDTD(LF);
			xmlWriter.writeEndElement();
		} catch (XMLStreamException xmlE) {
			xmlE.printStackTrace();
		}
		
		logger.debug(strWriter.toString());

		return strWriter.toString();
	}
}
