package test.dataportal.functional;

/**
 * Test case to run jetty server. It just starts jetty server and waits for an
 * enter on the console. Useful to test with jmeter
 * 
 * @author fergonco
 */
public class JettyStarter extends AbstractFunctionalTest {

	@Override
	protected String getService() {
		return null;
	}

	public void testRunJetty() throws Exception {
		System.in.read();
	}

}
