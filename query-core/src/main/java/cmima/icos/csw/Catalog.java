/**
 * 
 */
package cmima.icos.csw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

/**
 * @author Micho Garcia
 * 
 */
public class Catalog {
	
	private static Logger logger = Logger.getLogger(Catalog.class);

	private static URL URL;

	/**
	 * 
	 * @param URL String with url csw server
	 * @throws MalformedURLException
	 */
	public Catalog(String URL) throws MalformedURLException {
		Catalog.URL = new URL(URL);
		logger.debug("URL to connect: " + URL);
	}

	/**
	 * 
	 * @param cswQuery 
	 * @return
	 * @throws IOException
	 */
	public String sendCatalogRequest(String cswQuery)
			throws IOException {

		URLConnection conexionCatalog = Catalog.URL.openConnection();
		conexionCatalog.setDoOutput(true);
		OutputStreamWriter cswParameters = new OutputStreamWriter(
				conexionCatalog.getOutputStream());
		cswParameters.write(cswQuery);
		cswParameters.flush();

		StringBuffer cswResponse = new StringBuffer();
		BufferedReader cswReader = new BufferedReader(new InputStreamReader(
				conexionCatalog.getInputStream()));
		String line;
		while ((line = cswReader.readLine()) != null) {
			cswResponse.append(line);
		}

		cswReader.close();
		cswParameters.close();

		logger.debug("response CSW: " + cswResponse.toString());
		
		return cswResponse.toString();
		
		/*HttpClient cswCliente = new HttpClient();
		PostMethod postRCSWRequest = new PostMethod(URL);
		
		postRCSWRequest.addRequestHeader("Content-type", "text/xml; charset=UTF-8");
		postRCSWRequest.setQueryString(cswQuery);
		
		int statusCode = cswCliente.executeMethod(postRCSWRequest);
		InputStream responseStream = postRCSWRequest.getResponseBodyAsStream();
		
		BufferedReader entrada = new BufferedReader(new InputStreamReader(responseStream,"UTF-8"));
		
		StringBuffer resp = new StringBuffer();
		String read = "";
		while (read != null) {
			read = entrada.readLine();
			if (read != null) {
				resp.append(read);					
			}
		}
		responseStream.close();
		entrada.close();		
		logger.debug("SERVER RESPONSE"+resp);
		postRCSWRequest.releaseConnection();
		
		return resp.toString();*/
	}
}
