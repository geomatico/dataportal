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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DownloadTest extends AbstractFunctionalTest {

	public void testDownload() throws Exception {
		String userName = "fergonco@doesnot.exist";
		String password = "testpass";
		Services services = new Services();
		assertTrue(services.register(userName, password));
		assertTrue(services.activate(userName));

		String START = "0";
		String ORDER_FIELD = "title";
		String ORDER_DIRECTION = "ASC";
		String LANG = "es";
		String LIMIT = "4";
		String ret = services.query(LANG, "", "", "", "", "", START, LIMIT,
				ORDER_FIELD, ORDER_DIRECTION);
		NodeList down1 = (NodeList) evaluateXPath(ret,
				"/response/item[position() <= 3]", XPathConstants.NODESET);
		String query1 = nodeList2String(down1, "response");
		String checkFile1 = (String) evaluateXPath(ret,
				"/response/item[1]/title", XPathConstants.STRING);
		checkFile1 = checkFile1.substring(checkFile1.lastIndexOf('/') + 1);
		NodeList down2 = (NodeList) evaluateXPath(ret,
				"/response/item[position() >= 3]", XPathConstants.NODESET);
		String query2 = nodeList2String(down2, "response");
		String checkFile2 = (String) evaluateXPath(ret,
				"/response/item[4]/title", XPathConstants.STRING);
		checkFile2 = checkFile2.substring(checkFile1.lastIndexOf('/') + 1);
		DownloadRunnable t1 = new DownloadRunnable(userName, password, query1,
				checkFile1);
		DownloadRunnable t2 = new DownloadRunnable(userName, password, query2,
				checkFile2);
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
		private String downloadQuery;
		private String fileCheck;
		private String userName;
		private String password;

		public DownloadRunnable(String userName, String password,
				String downloadQuery, String fileCheck) {
			this.downloadQuery = downloadQuery;
			this.fileCheck = fileCheck;
			this.error = null;
			this.userName = userName;
			this.password = password;
		}

		@Override
		public void run() {
			try {
				download(downloadQuery, fileCheck);
			} catch (Exception e) {
				error = e.getMessage();
				e.printStackTrace();
			}
		}

		private void download(String xml, String fileCheck) throws IOException,
				HttpException, ParserConfigurationException, SAXException,
				XPathExpressionException, Exception, FileNotFoundException,
				ZipException {
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
