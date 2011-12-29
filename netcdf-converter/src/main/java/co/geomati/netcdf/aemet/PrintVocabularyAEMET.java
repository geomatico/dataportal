package co.geomati.netcdf.aemet;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class PrintVocabularyAEMET {

	public static void main(String[] args) throws IOException {
		Properties ceamVocabulary = new Properties();
		ceamVocabulary.load(PrintVocabularyAEMET.class
				.getResourceAsStream("vocabulary.properties"));
		System.out.println("<atmosphere>");
		Iterator<Object> it = ceamVocabulary.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			System.out.println("<term>");
			String standardName = key.toLowerCase();
			String longName = escape((String) ceamVocabulary.get(key));
			System.out.println("<standard-name>" + standardName
					+ "</standard-name>");
			System.out.println("<description>" + longName + "</description>");
			System.out.println("</term>");
		}
		System.out.println("</atmosphere>");
	}

	private static String escape(String string) {
		return string.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
}
