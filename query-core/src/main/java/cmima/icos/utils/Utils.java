/**
 * 
 */
package cmima.icos.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * @author Micho Garcia
 * 
 */
public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class);

	public static String convertStreamToString(InputStream is) throws Exception {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		return sb.toString();
	}


}
