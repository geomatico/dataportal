package test.dataportal.functional;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Unit test for login service at ciclope
 */
public class LoginTest extends AbstractFunctionalTest {

	@Override
	protected String getService() {
		return "login";
	}

	public void testLoginFailed() throws Exception {
		String response = callService(new String[] { "request", "user",
				"password" },
				new String[] { "access", "wronguser", "wrongpass" });

		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(response);
		assertFalse(jsonObject.getBoolean("success"));
	}

	public void testRegister() throws Exception {
		String userName = "foo@foo.foo";
		register(userName);
		activate(userName);
	}

	public void testRegisterInvalidMailAddress() throws Exception {
		registerFail("notanemailaddress");
	}

	public void testRegisterTwice() throws Exception {
		// Register foo
		String userName = "foo@foo.foo";
		register(userName);

		// Register foo again
		registerFail(userName);

		// Activate
		activate(userName);

		// Register foo again after foo is activated
		registerFail(userName);
	}

	private void registerFail(String userName) throws IOException,
			HttpException {
		Pair<String, Integer> response = callServiceNoCheck(new String[] {
				"request", "user", "password" }, new String[] { "register",
				userName, "testpass" });
		assertTrue(response.getRight() == 200);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(response
				.getLeft());
		assertFalse(jsonObject.getBoolean("success"));
	}

	private void activate(String userName) throws IOException, HttpException,
			Exception {
		int code = callServiceNoCheck(new String[] { "request", "hash" },
				new String[] { "register", getUserHash(userName) }).getRight();

		assertTrue(code == 302);// Redirect
	}

	private void register(String userName) throws IOException, HttpException {
		String response = callService(new String[] { "request", "user",
				"password" }, new String[] { "register", userName, "testpass" });

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
}
