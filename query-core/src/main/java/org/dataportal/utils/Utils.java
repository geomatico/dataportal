/**
 * 
 */
package org.dataportal.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

/**
 * 
 * Utils to use in Dataportal
 * 
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

	/**
	 * 
	 * Extract the coords of bbox from string[] and convert into an arraylist of
	 * bbox type
	 * 
	 * 
	 * @param parametros
	 */
	public static ArrayList<BBox> extractToBBoxes(String stringBBoxes) {

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

	/**
	 * 
	 * Transform a NodeList in an ArrayList
	 * 
	 * @param aNodeList
	 * @return
	 */
	public static ArrayList<String> nodeList2ArrayList(NodeList aNodeList) {
		String debug = "";
		ArrayList<String> aArrayList = new ArrayList<String>();
		for (int nNodo = 0; nNodo < aNodeList.getLength(); nNodo++) {
			aArrayList.add(aNodeList.item(nNodo).getNodeValue());
			debug += aNodeList.item(nNodo).getNodeValue();
		}
		logger.debug("CONTENT: " + debug);
		return aArrayList;
	}

	/**
	 * 
	 * Compare the content of 2 ArrayList
	 * 
	 * @param ArrayList arrayUno
	 * @param ArrayList arrayDos
	 * @return ArrayList with diferences
	 */
	public static ArrayList<String> compare2Arraylist(ArrayList<String> arrayRequest,
			ArrayList<String> arrayResponse) {

		ArrayList<String> diferentes = new ArrayList<String>();
		boolean encontrado = false;
		
		for (String idRequest : arrayRequest) {
			encontrado = false;
			for (String idResponse : arrayResponse) {
				if (idResponse.equals(idRequest))
					encontrado = true;
			}
			if (!encontrado)
				diferentes.add(idRequest);
		}

		return diferentes;
	}
}
