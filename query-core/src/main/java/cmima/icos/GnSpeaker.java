/**
 * 
 */
package cmima.icos;

import java.util.HashMap;

import org.apache.log4j.Logger;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

/**
 * @author Micho Garcia
 * 
 */
public class GnSpeaker {
	
	private final static Logger logger = Logger.getLogger(GnSpeaker.class);

	/**
	 * 
	 */
	private HashMap<String, String[]> parametros;

	// TODO Sacar esto de aquí pero ya mismo
	private static final String gnServiceURL = "http://ciclope.cmima.csic.es:8080/geonetworkcmima";
	private static final String gnUsername = "admin";
	private static final String gnPassword = "pass admin GN";

	/**
	 * 
	 */
	public GnSpeaker(HashMap<String, String[]> params) {
		this.parametros = params;
	}

	private static String arrayToString(String[] a, String separator) {
		StringBuffer result = new StringBuffer();
		if (a.length > 0) {
			result.append(a[0]);
			for (int i = 1; i < a.length; i++) {
				result.append(separator);
				result.append(a[i]);
			}
		}
		return result.toString();
	}

	public int ask2gn() throws GNLibException, GNServerException {

		GNClient client = new GNClient(gnServiceURL);

		boolean logged = client.login(gnUsername, gnPassword);

		if (!logged) {
			throw new RuntimeException("No login");
		}

		GNSearchRequest searchRequest = new GNSearchRequest();

		if (parametros.containsKey("bbox")) {

		}
		if (parametros.containsKey("variables")) {
			logger.info("P VARIABLES: "
					+ arrayToString(parametros.get("variables"), ","));
			// modificar esto para busqueda por campo propio
			searchRequest.addParam(GNSearchRequest.Param.any,
					arrayToString(parametros.get("variables"), ","));
		}
		if (parametros.containsKey("daterange")) {
			// esta busqueda la realiza para fecha creación del metadato
			// para busqueda creación dato utilizar extfrom y extto
			logger.info("P DATERANGE: " + "dateFrom "
					+ parametros.get("daterange")[0] + "dateTo "
					+ parametros.get("daterange")[1]);
			searchRequest.addParam(GNSearchRequest.Param.dateFrom,
					parametros.get("daterange")[0]);
			searchRequest.addParam(GNSearchRequest.Param.dateTo,
					parametros.get("daterange")[1]);
		}

		searchRequest.addConfig(GNSearchRequest.Config.remote, "off");
		GNSearchResponse searchResponse = client.search(searchRequest);

		int nMet = searchResponse.getCount();
		if (nMet != 0) {
			for (GNSearchResponse.GNMetadata metadata : searchResponse) {
			}
			return nMet;
		} else {
			return 0;
		}

	}
}