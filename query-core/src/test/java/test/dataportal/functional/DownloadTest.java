package test.dataportal.functional;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

		// Download zip
		byte[] bytes = callGetService(new String[] { "file" },
				new String[] { fileName }, null);
		File tempZip = File.createTempFile("testDownload", ".zip");
		BufferedOutputStream tempOS = new BufferedOutputStream(
				new FileOutputStream(tempZip));
		IOUtils.write(bytes, tempOS);
		tempOS.close();

		// Check zip contents
		ZipFile zf = new ZipFile(tempZip);
		Enumeration<? extends ZipEntry> entries = zf.entries();
		boolean fileFound = false;
		int count = 0;
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) entries.nextElement();
			if (zipEntry.getName().equals("29HE20101129_posicion.nc")) {
				fileFound = true;
			}
			count++;
		}
		assertTrue(count > 1); // readme and the other file
		assertTrue(fileFound);
	}

	private void login(String user, String password) throws Exception {
		String response = callService("login", new String[] { "request",
				"user", "password" }, new String[] { "access", user, password });

		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(response);
		assertTrue(jsonObject.getBoolean("success"));
	}

}
