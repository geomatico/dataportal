package test.dataportal.functional;

/**
 * Test case to run jetty server. It just starts jetty server and waits for an
 * enter on the console. Useful to test with jmeter
 * 
 * @author fergonco
 */
public class JettyStarter extends AbstractFunctionalTest {

	public void testRunJetty() throws Exception {
		String userName = "fergonco@doesnot.exist";
		Services services = new Services();
		services.register(userName, "testpass");
		services.activate(userName);
		System.in.read();
	}

}
