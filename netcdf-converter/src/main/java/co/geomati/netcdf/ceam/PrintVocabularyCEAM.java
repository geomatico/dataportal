package co.geomati.netcdf.ceam;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class PrintVocabularyCEAM {

	public static void main(String[] args) throws IOException {
		Properties ceamVocabulary = new Properties();
		ceamVocabulary.load(PrintVocabularyCEAM.class
				.getResourceAsStream("variables.properties"));
		System.out.println("<environment>");
		Iterator<Object> it = ceamVocabulary.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (key.endsWith("_UNITS")) {
				continue;
			} else {
				System.out.println("<term>");
				String standardName = key.toLowerCase();
				String longName = escape((String) ceamVocabulary.get(key));
				System.out.println("<standard-name>" + standardName
						+ "</standard-name>");
				System.out.println("<description>" + longName
						+ "</description>");
				System.out.println("</term>");
			}
		}
		System.out.println("</environment>");
	}

	private static String escape(String string) {
		return string.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
}
