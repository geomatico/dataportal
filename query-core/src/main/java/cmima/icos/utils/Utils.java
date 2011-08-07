/**
 * 
 */
package cmima.icos.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Micho Garcia
 * 
 */
public class Utils {

	private static final int FIRST = 0;
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

	/**
	 * 
	 * Extract the coords of bbox from string[] and convert into an arraylist of
	 * bbox type
	 * 
	 * 
	 * @param parametros
	 */
	public static ArrayList<BBox> extractToBBoxes(Map<String, String[]> parametros) {

		String stringBBoxes = parametros.get("bboxes")[FIRST];
		
		logger.debug("BBOXES: " + stringBBoxes);

		ArrayList<BBox> bboxes = new ArrayList<BBox>();
		StringBuffer tempStringBBoxes = new StringBuffer();

		for (int n = 2; n < stringBBoxes.length() - 1; n++) {

			if (stringBBoxes.charAt(n) != ']') {
				tempStringBBoxes.append(stringBBoxes.charAt(n));
			} else {
				String[] coords = tempStringBBoxes.toString()
						.replaceAll(",\\[", "").split(",");

				logger.debug("COORDS: xmin " + coords[0] + "; ymin "
						+ coords[1] + "; xmax " + coords[2] + "; ymax "
						+ coords[3]);

				bboxes.add(new BBox(coords));
				tempStringBBoxes = new StringBuffer();
			}
		}
		
		if (bboxes.size() == 0)
			return null;

		return bboxes;
	}

}
