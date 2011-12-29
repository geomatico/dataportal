package org.dataportal;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$
	private static final String DEFAULT_LANGUAGE = "en"; //$NON-NLS-1$

	private static Locale locale = new Locale(DEFAULT_LANGUAGE);
	private static ResourceBundle resource_bundle = ResourceBundle.getBundle(
			BUNDLE_NAME, locale);

	/**
	 * @return the locale
	 */
	public static Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale
	 *            the lang to set
	 */
	public static void setLang(String language) {
		if (language == null)
			locale = new Locale(DEFAULT_LANGUAGE);
		else
			locale = new Locale(language);
		resource_bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return resource_bundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
