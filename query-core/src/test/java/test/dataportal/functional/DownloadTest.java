package test.dataportal.functional;

import javax.xml.xpath.XPathConstants;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

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
		String ret = callService(new String[0], new String[0], xml);
		String fileName = (String) evaluateXPath(ret, "/download/filename",
				XPathConstants.STRING);
		assertTrue(fileName != null && fileName.length() > 0);

		byte[] bytes = callGetService(new String[] { "file" },
				new String[] { fileName }, null);
		assertTrue(bytes.length > 0);
	}

	private void login(String user, String password) throws Exception {
		String response = callService("login", new String[] { "request",
				"user", "password" }, new String[] { "access", user, password });

		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(response);
		assertTrue(jsonObject.getBoolean("success"));
	}

}
