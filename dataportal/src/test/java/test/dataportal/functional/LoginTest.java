package test.dataportal.functional;


/**
 * Unit test for login service at ciclope
 */
public class LoginTest extends AbstractFunctionalTest {

	private Services services;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		services = new Services();
	}

	public void testLoginFailed() throws Exception {
		assertFalse(services.login("wronguser", "wrongpass"));
	}

	public void testRegister() throws Exception {
		String userName = "foo@foo.foo";
		assertTrue(services.register(userName, "testpass"));
		assertTrue(services.activate(userName));
	}

	public void testRegisterTwice() throws Exception {
		// Register foo
		String userName = "foo@foo.foo";
		assertTrue(services.register(userName, "testpass"));

		// Register foo again
		assertFalse(services.register(userName, "testpass"));

		// Activate
		assertTrue(services.activate(userName));

		// Register foo again after foo is activated
		assertFalse(services.register(userName, "testpass"));
	}
}
