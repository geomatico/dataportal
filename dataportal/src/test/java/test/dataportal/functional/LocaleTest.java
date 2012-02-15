package test.dataportal.functional;

public class LocaleTest extends AbstractFunctionalTest {

	public void testSetLocale() throws Exception {
		Services services = new Services();
		String result = services.setLocale("en");
		assertTrue(result.startsWith("OK"));
		result = services.setLocale("es");
		assertTrue(result.startsWith("Perfecto"));
	}
}
