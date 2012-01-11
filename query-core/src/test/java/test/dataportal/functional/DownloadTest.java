package test.dataportal.functional;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

public class DownloadTest extends AbstractFunctionalTest {

	@Override
	protected String getService() {
		return "download";
	}

	public void testDownload() throws Exception {
		String userName = "fergonco@doesnot.exist";
		register("login", userName);
		activate("login", userName);
		login(userName, "testpass");

		String xml = new String(IOUtils.toByteArray(this.getClass()
				.getResourceAsStream("download-post.xml")));
		Pair<String, Integer> ret = callServiceNoCheck(new String[0],
				new String[0], xml);
		assertTrue(ret.getRight() == 200);
		System.out.println(ret.getLeft());
	}

	private void login(String user, String password) throws Exception {
		String response = callService("login", new String[] { "request",
				"user", "password" }, new String[] { "access", user, password });

		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(response);
		assertTrue(jsonObject.getBoolean("success"));
	}

}
