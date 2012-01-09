package test.dataportal;

import junit.framework.TestCase;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import test.dataportal.controllers.PersistenceUnitSingleton;

/**
 * Unit test for login service at ciclope
 */
public class LoginTest extends TestCase {

	private Server server;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		PersistenceUnitSingleton.setPersistenceUnit("functional-tests");

		server = new Server(8080);
		WebAppContext context = new WebAppContext();
		context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
		context.setResourceBase("src/main/webapp/");
		context.setContextPath("/dataportal");
		context.setParentLoaderPriority(true);

		server.setHandler(context);
		server.start();
	}

	public void testLoginFailed() throws Exception {
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(
				"http://127.0.0.1:8080/dataportal/login");
		post.addParameter("request", "access");
		post.addParameter("user", "manolo");
		post.addParameter("password", "wrongpass");

		int code = client.executeMethod(post);
		String response = post.getResponseBodyAsString();
		post.releaseConnection();

		assertTrue(code == 200);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(response);
		assertFalse(jsonObject.getBoolean("success"));
	}

	public void testRegister() throws Exception {

	}
}
