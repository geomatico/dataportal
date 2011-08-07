/**
 * 
 */
package cmima.icos.csw;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

/**
 * @author Micho Garcia
 * 
 */
public class CSWCatalog {

	private static Logger logger = Logger.getLogger(CSWCatalog.class);

	private static URL URL;

	/**
	 * 
	 * @param URL
	 *            String with url csw server
	 * @throws MalformedURLException
	 */
	public CSWCatalog(String URL) throws MalformedURLException {
		CSWCatalog.URL = new URL(URL);
		logger.debug("URL to connect: " + URL);
	}

	/**
	 * 
	 * @param cswQuery
	 * @return
	 * @throws Exception
	 */
	public String sendCatalogRequest(String cswQuery) throws Exception {

		URLConnection conexionCatalog = CSWCatalog.URL.openConnection();

		conexionCatalog.setDoOutput(true);
		conexionCatalog.setRequestProperty("Accept", "*/*");
		conexionCatalog.setRequestProperty("Content-type", "application/xml");

		OutputStreamWriter cswParameters = new OutputStreamWriter(
				conexionCatalog.getOutputStream());
		cswParameters.write(cswQuery);
		cswParameters.flush();

		InputStream isCswResponse = conexionCatalog.getInputStream();
		
		String response = transform(isCswResponse);
		
		isCswResponse.close();

		logger.debug("RESPONSE2CLIENT: " + response);
		
		return response;
	}
	
	/**
	 * @param isCSWResponse
	 * @return
	 */
	public static String transform(InputStream isCSWResponse) {
		
		StringWriter writer2Client = new StringWriter();
		InputStream isXslt = CSWCatalog.class.getResourceAsStream("/response2client.xsl");

		try {
			Source responseSource = new StreamSource(isCSWResponse);
			Source xsltSource = new StreamSource(isXslt);

			TransformerFactory transFact = TransformerFactory.newInstance();
			Templates template = transFact.newTemplates(xsltSource);
			Transformer transformer = template.newTransformer();

			transformer.transform(responseSource, new StreamResult(
					writer2Client));

			writer2Client.flush();
			writer2Client.close();
			
			isXslt.close();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} 
		
		return writer2Client.toString();
	}
}
