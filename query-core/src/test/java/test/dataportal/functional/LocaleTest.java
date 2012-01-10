package test.dataportal.functional;

public class LocaleTest extends AbstractFunctionalTest {

	@Override
	protected String getService() {
		return "locale";
	}

	public void testSetLocale() throws Exception {
		String result = callService(new String[] { "lang" }, new String[] { "en" });
		assertTrue(result.startsWith("OK"));
		result = callService(new String[] { "lang" }, new String[] { "es" });
		assertTrue(result.startsWith("Perfecto"));
	}
}
