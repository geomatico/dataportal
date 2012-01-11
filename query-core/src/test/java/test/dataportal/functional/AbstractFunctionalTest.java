package test.dataportal.functional;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.mail.MessagingException;

import junit.framework.TestCase;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.dataportal.SystemSingleton;
import org.dataportal.datasources.Mail;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public abstract class AbstractFunctionalTest extends TestCase {

	protected static final String DB_PATH = "target/test";
	protected Server server;
	private HttpClient client = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deleteIfExists(new File(DB_PATH + ".trace.db"));
		deleteIfExists(new File(DB_PATH + ".h2.db"));

		SystemSingleton.setPersistenceUnit("functional-tests");
		SystemSingleton.setMail(new Mail() {
			@Override
			public void send(String to, String subject, String template,
					Map<String, String> vars) throws MessagingException,
					IOException {
				/*
				 * Don't send in testing. We just look directly on the database
				 */
			}
		});

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
		return callService(getService(), paramNames, paramValues);
	}

	protected String callService(String service, String[] paramNames,
			String[] paramValues) throws IOException, HttpException {
		Pair<String, Integer> response = callServiceNoCheck(service,
				paramNames, paramValues, null);
		String content = response.getLeft();
		int code = response.getRight();
		assertTrue(code + "", code == 200);
		return content;
	}

	protected Pair<String, Integer> callServiceNoCheck(String[] paramNames,
			String[] paramValues) throws IOException, HttpException {
		return callServiceNoCheck(paramNames, paramValues, null);
	}

	protected Pair<String, Integer> callServiceNoCheck(String[] paramNames,
			String[] paramValues, String content) throws IOException,
			HttpException {
		return callServiceNoCheck(getService(), paramNames, paramValues,
				content);
	}

	private Pair<String, Integer> callServiceNoCheck(String service,
			String[] paramNames, String[] paramValues, String content)
			throws IOException, HttpException {
		HttpClient client = getClient();
		PostMethod post = new PostMethod("http://127.0.0.1:8080/dataportal/"
				+ service);
		post.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		if (content != null) {
			post.setRequestEntity(new StringRequestEntity(content, null, null));
		}
		for (int i = 0; i < paramValues.length; i++) {
			post.addParameter(paramNames[i], paramValues[i]);
		}
		post.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		int code = client.executeMethod(post);
		String ret = post.getResponseBodyAsString();
		if (code != 200) {
			System.err.println(ret);
		}
		Cookie[] cookies = client.getState().getCookies();
		for (Cookie cookie : cookies) {
			System.out.println(cookie);
		}
		post.releaseConnection();
		Pair<String, Integer> response = new ImmutablePair<String, Integer>(
				ret, code);
		return response;
	}

	private HttpClient getClient() {
		if (client == null) {
			client = new HttpClient();
		}

		return client;
	}

	protected void activate(String userName) throws IOException, HttpException,
			Exception {
		activate(getService(), userName);
	}

	protected void activate(String service, String userName)
			throws IOException, HttpException, Exception {
		Pair<String, Integer> response = callServiceNoCheck(service,
				new String[] { "request", "hash" }, new String[] { "register",
						getUserHash(userName) }, null);
		int code = response.getRight();
		System.out.println(response.getLeft());

		assertTrue(code == 302);// Redirect
	}

	protected void register(String userName) throws IOException, HttpException {
		register(getService(), userName);
	}

	protected void register(String service, String userName)
			throws IOException, HttpException {
		String response = callService(service, new String[] { "request",
				"user", "password" }, new String[] { "register", userName,
				"testpass" });

		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(response);
		assertTrue(response, jsonObject.getBoolean("success"));
	}

	private String getUserHash(String userName) throws Exception {
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:" + DB_PATH,
				"sa", "");

		Statement st = conn.createStatement();
		ResultSet resultSet = st
				.executeQuery("select * from \"users\" where \"id\"='"
						+ userName + "'");
		assertTrue(resultSet.first());

		String hash = resultSet.getString("hash");

		resultSet.close();
		st.close();
		conn.close();

		return hash;
	}

	protected abstract String getService();

}
