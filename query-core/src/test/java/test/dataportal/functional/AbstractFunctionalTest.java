package test.dataportal.functional;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.dataportal.controllers.PersistenceUnitSingleton;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public abstract class AbstractFunctionalTest extends TestCase {

	protected static final String DB_PATH = "target/test";
	protected Server server;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deleteIfExists(new File(DB_PATH + ".trace.db"));
		deleteIfExists(new File(DB_PATH + ".h2.db"));

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

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.stop();
	}

	private void deleteIfExists(File dbFile) {
		if (dbFile.exists()) {
			assertTrue(dbFile.delete());
		}
	}

	protected String callService(String[] paramNames, String[] paramValues)
			throws IOException, HttpException {
		Pair<String, Integer> response = callServiceNoCheck(paramNames,
				paramValues);
		String content = response.getLeft();
		int code = response.getRight();
		assertTrue(code + "", code == 200);
		return content;
	}

	protected Pair<String, Integer> callServiceNoCheck(String[] paramNames,
			String[] paramValues) throws IOException, HttpException {
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("http://127.0.0.1:8080/dataportal/"
				+ getService());
		for (int i = 0; i < paramValues.length; i++) {
			post.addParameter(paramNames[i], paramValues[i]);
		}

		int code = client.executeMethod(post);
		String ret = post.getResponseBodyAsString();
		if (code != 200) {
			System.err.println(ret);
		}
		post.releaseConnection();
		Pair<String, Integer> response = new ImmutablePair<String, Integer>(
				ret, code);
		return response;
	}

	protected abstract String getService();

}
