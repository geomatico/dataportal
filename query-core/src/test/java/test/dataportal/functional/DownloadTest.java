package test.dataportal.functional;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

public class DownloadTest extends AbstractFunctionalTest {

	public void testDownload() throws Exception {
		String userName = "fergonco@doesnot.exist";
		String password = "testpass";
		Services services = new Services();
		assertTrue(services.register(userName, password));
		assertTrue(services.activate(userName));

		DownloadRunnable t1 = new DownloadRunnable(userName, password,
				"download-post.xml", "29HE20101129_posicion.nc");
		DownloadRunnable t2 = new DownloadRunnable(userName, password,
				"download-post2.xml", "29HE20101129_posicion.nc");
		t1.start();
		t2.start();

		while (t1.isAlive() || t2.isAlive()) {
			Thread.yield();
		}

		assertTrue(t1.error, t1.error == null);
		assertTrue(t2.error, t2.error == null);
	}

	private final class DownloadRunnable extends Thread {
		private String error;
		private String downloadResource;
		private String fileCheck;
		private String userName;
		private String password;

		public DownloadRunnable(String userName, String password,
				String downloadResource, String fileCheck) {
			this.downloadResource = downloadResource;
			this.fileCheck = fileCheck;
			this.error = null;
			this.userName = userName;
			this.password = password;
		}

		@Override
		public void run() {
			try {
				download(downloadResource, fileCheck);
			} catch (Exception e) {
				error = e.getMessage();
				e.printStackTrace();
			}
		}

		private void download(String downloadResource, String fileCheck)
				throws IOException, HttpException,
				ParserConfigurationException, SAXException,
				XPathExpressionException, Exception, FileNotFoundException,
				ZipException {
			String xml = new String(IOUtils.toByteArray(this.getClass()
					.getResourceAsStream(downloadResource)));
			Services services = new Services();
			services.login(userName, password);
			String ret = services.download(xml);
			String fileName = (String) evaluateXPath(ret, "/download/filename",
					XPathConstants.STRING);
			threadAssert("download zip", fileName != null
					&& fileName.length() > 0);

			// Download zip
			byte[] bytes = services.getFile(fileName);
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
				if (zipEntry.getName().startsWith(fileCheck)) {
					fileFound = true;
				}
				count++;
			}
			threadAssert("zip file count", count > 1); // readme and the other
														// file
			threadAssert("zip does not contain file", fileFound);
		}

		private void threadAssert(String testDescription, boolean test) {
			if (!test) {
				error = testDescription;
			}
		}

	}

}
