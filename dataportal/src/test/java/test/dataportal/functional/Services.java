package test.dataportal.functional;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Services {
	protected static final String DB_PATH = "target/test";

	private HttpClient client = null;
	private boolean checkCode = true;

	private HttpClient getClient() {
		if (client == null) {
			client = new HttpClient();
		}

		return client;
	}

	public void setCheckCode(boolean checkCode) {
		this.checkCode = checkCode;
	}

	public String download(String xml) throws HttpException, IOException {
		return callService("download", new String[0], new String[0], xml);
	}

	protected String callService(String service, String[] paramNames,
			String[] paramValues) throws IOException, HttpException {
		return callService(service, paramNames, paramValues, null);
	}

	protected String callService(String service, String[] paramNames,
			String[] paramValues, String content) throws IOException,
			HttpException {
		Pair<String, Integer> response = callServiceNoCheck(service,
				paramNames, paramValues, content);
		String responseContent = response.getLeft();
		int code = response.getRight();
		checkCodeIfNecessary(code);
		return responseContent;
	}

	private void checkCodeIfNecessary(int code) {
		if (checkCode && code != 200) {
			throw new IllegalStateException("received code: " + code);
		}
	}

	/*
	 * TODO inline if only one call
	 */
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
		post.releaseConnection();
		Pair<String, Integer> response = new ImmutablePair<String, Integer>(
				ret, code);
		return response;
	}

	public boolean login(String user, String password) throws HttpException,
			IOException {
		String response = callService("login", new String[] { "request",
				"user", "password" }, new String[] { "access", user, password });

		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(response);
		return jsonObject.getBoolean("success");
	}

	public boolean register(String userName, String password)
			throws HttpException, IOException {
		String response = callService("login", new String[] { "request",
				"user", "password" }, new String[] { "register", userName,
				"testpass" });

		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(response);
		return jsonObject.getBoolean("success");
	}

	public boolean activate(String userName) throws HttpException, IOException,
			Exception {
		Pair<String, Integer> response = callServiceNoCheck("login",
				new String[] { "request", "hash" }, new String[] { "register",
						getUserHash(userName) }, null);
		int code = response.getRight();

		if (checkCode && code != 302) {
			throw new IllegalStateException("received code: " + code);
		} else {
			return code == 302;
		}
	}

	private String getUserHash(String userName) throws Exception {
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:" + DB_PATH,
				"sa", "");

		Statement st = conn.createStatement();
		ResultSet resultSet = st
				.executeQuery("select * from \"users\" where \"id\"='"
						+ userName + "'");
		if (!resultSet.first()) {
			throw new RuntimeException();
		}

		String hash = resultSet.getString("hash");

		resultSet.close();
		st.close();
		conn.close();

		return hash;
	}

	public byte[] getFile(String fileName) throws HttpException, IOException {
		HttpClient client = getClient();
		GetMethod get = new GetMethod(
				"http://127.0.0.1:8080/dataportal/download");
		get.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		get.setQueryString(new NameValuePair[] { new NameValuePair("file",
				fileName) });
		get.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		int code = client.executeMethod(get);
		checkCodeIfNecessary(code);
		InputStream stream = get.getResponseBodyAsStream();
		byte[] ret = IOUtils.toByteArray(stream);
		get.releaseConnection();
		return ret;
	}

	public String setLocale(String locale) throws HttpException, IOException {
		return callService("locale", new String[] { "lang" },
				new String[] { locale });
	}

	public String query(String lang, String bboxes, String start_date,
			String end_date, String variables, String text, String start,
			String limit, String sort, String dir) throws HttpException,
			IOException {
		return callService("search", new String[] { "lang", "bboxes",
				"start_date", "end_date", "variables", "text", "start",
				"limit", "sort", "dir" },
				new String[] { lang, bboxes, start_date, end_date, variables,
						text, start, limit, sort, dir });
	}

}
