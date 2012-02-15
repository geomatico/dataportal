/**
 * 
 */
package test.dataportal.utils;

import java.util.Locale;

import org.dataportal.Messages;

import junit.framework.TestCase;

/**
 * @author michogarcia
 *
 */
public class MessagesTest extends TestCase {

	/**
	 * Test method for {@link org.dataportal.Messages#setLang(java.lang.String)}.
	 */
	public void testSetLang() {
		Messages.setLang("es");
		Locale locale = Messages.getLocale();
		assertEquals("es", locale.getLanguage());
	}

	/**
	 * Test method for {@link org.dataportal.Messages#getString(java.lang.String)}.
	 */
	public void testGetString() {
		Messages.setLang("es");
		String translated = Messages.getString("downloadcontroller.failed_create_dir");
		assertEquals("Error al crear el directorio", translated);
	}

}
