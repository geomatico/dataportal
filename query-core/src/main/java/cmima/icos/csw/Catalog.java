/**
 * 
 */
package cmima.icos.csw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
		conexionCatalog.setRequestProperty("Accept", "*/*");
		conexionCatalog.setRequestProperty("Content-type",
				"application/xml");
		
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
	}
}
