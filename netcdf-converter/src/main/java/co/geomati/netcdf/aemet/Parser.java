package co.geomati.netcdf.aemet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	private int index;
	private String text;

	public Parser(String text) {
		this.index = 0;
		this.text = text;
	}

	public String getUnparsed() {
		return text.substring(index);
	}

	public String read(String regexp) {
		Pattern p = Pattern.compile(regexp);
		Matcher matcher = p.matcher(text);
		if (matcher.find(index)) {
			if (index == matcher.start() && matcher.end() > matcher.start()) {
				index = matcher.end();
				return text.substring(matcher.start(), matcher.end());
			}
		}

		return null;
	}

	public int consume(String... fragments) {
		for (String fragment : fragments) {
			while (index < text.length()) {
				int inputLength = Math.min(fragment.length(), text.length()
						- index);
				String input = text.substring(index, index + inputLength);
				if (input.equals(fragment)) {
					index += fragment.length();
				} else {
					break;
				}
			}
		}

		return index;
	}

}