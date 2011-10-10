/**
 * 
 */
package org.dataportal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dataportal.controllers.JPADownloadController;
import org.dataportal.controllers.JPAUserController;
import org.dataportal.csw.CSWCatalog;
import org.dataportal.csw.CSWGetRecordById;
import org.dataportal.csw.CSWNamespaceContext;
import org.dataportal.model.Download;
import org.dataportal.model.DownloadItem;
import org.dataportal.model.User;
import org.dataportal.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Class to control the files download. Use a Cached Thread Pool to download
 * files concurrently
 * 
 * @author Micho Garcia
 * 
 */
public class DownloadController {

	private static Logger logger = Logger.getLogger(DownloadController.class);

	private String tempDir;
	private static CSWCatalog catalogo;
	private User user = null;
	private JPAUserController userJPAController = null;
	private JPADownloadController downloadJPAController = null;

	/**
	 * Constructor. Reads tempDir from properties file.
	 */
	public DownloadController() {
		this.tempDir = Config.get("temp.dir");
		String url = Config.get("csw.url");
		try {
			catalogo = new CSWCatalog(url);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Create the pathfile with the information extracted from properties and
	 * create the directory if not exits
	 * 
	 * @param userName
	 *            User name from session (String)
	 */
	public String createPathFile(String userName) {

		String pathFile = this.tempDir + "/" + userName;
		File personalDirectory = new File(pathFile);
		boolean exits = personalDirectory.exists();
		if (!exits) {
			boolean createDir = personalDirectory.mkdir();
			if (!createDir)
				pathFile = null;
		}

		return pathFile;
	}

	/**
	 * Extract id's from client XML request. Checks whether id's are in the
	 * server using a GetRecordById request. If checking is OK, extract the
	 * URL's from client XML request and sends to DownloadController to download
	 * files
	 * 
	 * @param InputStream
	 *            with the XML sends by client
	 * @throws Exception 
	 */
	public String askgn2download(InputStream isRequestXML, String userName) throws Exception {

		StringBuffer response = new StringBuffer();

		try {
			DocumentBuilderFactory dbFactoria = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dbBuilder = dbFactoria.newDocumentBuilder();
			Document downloadXML = (Document) dbBuilder.parse(isRequestXML);

			CSWNamespaceContext ctx = new CSWNamespaceContext();

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(ctx);

			String variablesExpr = "//id/child::node()";
			NodeList idNodeList = (NodeList) xpath.evaluate(variablesExpr,
					downloadXML, XPathConstants.NODESET);
			ArrayList<String> requestIdes = Utils
					.nodeList2ArrayList(idNodeList);

			CSWGetRecordById getRecordById = new CSWGetRecordById("brief");
			String getRecordByIdQuery = getRecordById.createQuery(requestIdes);
			InputStream isGetRecordByIdResponse = catalogo
					.sendCatalogRequest(getRecordByIdQuery);

			// TODO Controlar excepción retornada del servidor

			ArrayList<String> responseIdes = recordIdes(isGetRecordByIdResponse);

			ArrayList<String> noIdsResponse = Utils.compare2Arraylist(
					requestIdes, responseIdes);

			if (noIdsResponse.size() != 0) {
				logger.info("ID'S NO ENCONTRADOS: "
						+ String.valueOf(noIdsResponse.size()) + " -> "
						+ StringUtils.join(noIdsResponse, " : "));
				DataPortalError error = new DataPortalError();
				error.setCode("id.not.found");
				error.setMessage("The following dataset IDs where not found: " + StringUtils.join(noIdsResponse, ", "));
				response.append(error.getErrorMessage());
			} else {
				xpath.reset();
				xpath.setNamespaceContext(ctx);
				String urlsExpr = "//data_link/child::node()";
				NodeList urlNodeList = (NodeList) xpath.evaluate(urlsExpr,
						downloadXML, XPathConstants.NODESET);

				// TODO Enviar volumen descarga al usuario y actuar en función

				ArrayList<String> urlsRequest = Utils
						.nodeList2ArrayList(urlNodeList);

				User anUser = new User(userName);
				userJPAController = new JPAUserController();
				user = userJPAController.exitsInto(anUser);

				if (user != null) {
					String resultDownload = downloadDatasets(urlsRequest,
							userName);

					// TODO realizar comprobación antes inserción en base de
					// datos
					// modificar respuesta método downloadDatasets a tipo XML

					insertDownload(user, resultDownload, urlsRequest);
					response.append(resultDownload);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw(e);
		}

		logger.debug("RESPONSE CONTROLLER: " + response.toString());

		return response.toString();
	}

	/**
	 * 
	 * Insert download into RDBMS
	 * 
	 * @param user
	 *            User
	 * @param fileName
	 *            String with filename
	 * @param urlsRequest
	 *            url request array
	 */
	private void insertDownload(User user, String fileName,
			ArrayList<String> urlsRequest) {

		String DOI = getDOI();
		Timestamp timeStamp = Utils.extractDateSystemTimeStamp();
		Download download = new Download(DOI, fileName, timeStamp, user);
		ArrayList<DownloadItem> items = new ArrayList<DownloadItem>();
		for (String url : urlsRequest) {
			DownloadItem item = new DownloadItem(url);
			items.add(item);
		}
		downloadJPAController = new JPADownloadController();
		downloadJPAController.insertItems(download, items);
	}

	/**
	 * 
	 * Method to obtain DOI
	 * 
	 * @return String with DOI
	 */
	private String getDOI() {
		// Generating random UUID (java built-in)
		return UUID.randomUUID().toString();
	}

	/**
	 * 
	 * Extract an array with id's from GetRecordByID response from CSW Server
	 * 
	 * @param InputStream
	 *            with the response sends by server
	 * @return ArrayList with id's from response
	 */
	private ArrayList<String> recordIdes(InputStream isGetRecordByIdResponse) {

		NodeList ides = null;
		ArrayList<String> arrayIdes = new ArrayList<String>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document xmlGetRecordsById = (Document) dBuilder
					.parse(isGetRecordByIdResponse);

			// TODO Extraer esto a una clase
			CSWNamespaceContext ctx = new CSWNamespaceContext();

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(ctx);

			String recordsExpr = "//BriefRecord/identifier/child::node()";
			ides = (NodeList) xpath.evaluate(recordsExpr, xmlGetRecordsById,
					XPathConstants.NODESET);
			// TODO EOF
			if (ides.getLength() != 0)
				arrayIdes = Utils.nodeList2ArrayList(ides);

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		logger.debug("RECORDS: " + String.valueOf(arrayIdes.size()));

		return arrayIdes;
	}

	/**
	 * Method to download files from a url's array.
	 * 
	 * @param urlsRequest
	 *            ArrayList with url's (ArrayList)
	 * @return Message
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	private String downloadDatasets(ArrayList<String> urlsRequest,
			String userName) throws InterruptedException, ExecutionException,
			IOException {

		// TODO cambiar esto por una clase mensaje de dataportal
		StringBuffer response = null;

		String pathFile = createPathFile(userName);
		if (pathFile == null) {
			DataPortalError error = new DataPortalError();
			error.setCode("failed.create.directory");
			error.setMessage("Failed to create directory");
			response = new StringBuffer();
			response.append(error.getErrorMessage());
			logger.error("FAILED create directory");
		} else {
			createDownloadThreads(urlsRequest, pathFile);
			String nameFile = userName + "_" + Utils.extractDateSystem();
			String filePathName = compressFiles(pathFile, nameFile);
			// TODO cambiar mensaje
			logger.debug("FILE to download: " + filePathName);
			response = new StringBuffer();
			response.append(StringUtils.substringAfterLast(filePathName, "/"));
		}

		return response.toString();
	}

	/**
	 * 
	 * Create all Threads to download files
	 * 
	 * @param urlsRequest URL's ArrayList
	 * @param pathFile path to save files
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void createDownloadThreads(ArrayList<String> urlsRequest,
			String pathFile) throws MalformedURLException,
			InterruptedException, ExecutionException {
		int urls = urlsRequest.size();

		ExecutorService threadsPool = Executors.newCachedThreadPool();
		int future = 0;
		Future futures[] = new Future[urls];

		for (String url : urlsRequest) {
			String name = StringUtils.substringAfterLast(url, "/");

			DownloadCallable hiloDescarga = new DownloadCallable(url, name,
					pathFile);
			futures[future] = threadsPool.submit(hiloDescarga);
			future += 1;

			Thread.sleep(2000);
		}

		for (int i = 0; i < urls; i++) {
			String tarea = (String) futures[i].get();
			logger.info("DOWNLOADING FINISH: " + tarea);
		}
	}

	/**
	 * Returns an inputStream to download the compressed file
	 * 
	 * @param fileName
	 *            Name of compressed file
	 * @param userName
	 *            User name that generated the file
	 * @return
	 * @throws FileNotFoundException
	 */
	public InputStream getFileContents(String fileName, String userName)
			throws FileNotFoundException {
		File file = new File(this.tempDir + "/" + userName + "/" + fileName);
		return new FileInputStream(file);
	}

	/**
	 * Returns the downloadable file size
	 * 
	 * @param fileName
	 *            Name of compressed file
	 * @param userName
	 *            User name that generated the file
	 * @return
	 * @throws FileNotFoundException
	 */
	public long getFileSize(String fileName, String userName)
			throws FileNotFoundException {
		File file = new File(this.tempDir + "/" + userName + "/" + fileName);
		if (file.exists() && file.isFile())
			return file.length();
		else
			return 0;
	}

	/**
	 * 
	 * Compress in tar format files in directory
	 * 
	 * @param pathDir
	 *            directory path (String)
	 * @param nameFile
	 *            archive name (String)
	 * @throws IOException
	 */
	private String compressFiles(String pathDir, String nameFile)
			throws IOException {

		String filePathName = pathDir + "/" + nameFile + ".zip";
		OutputStream os = new FileOutputStream(filePathName);
		ZipArchiveOutputStream zipOs = new ZipArchiveOutputStream(os);

		File directory = new File(pathDir);
		String extensions[] = { "nc" };
		@SuppressWarnings("unchecked")
		Iterator<File> itFiles = FileUtils.iterateFiles(directory, extensions,
				false);

		while (itFiles.hasNext()) {
			File fl = itFiles.next();
			ArchiveEntry archFl = zipOs.createArchiveEntry(fl, fl.getName());
			zipOs.putArchiveEntry(archFl);
			zipOs.write(FileUtils.readFileToByteArray(fl));
			zipOs.flush();
			zipOs.closeArchiveEntry();
		}

		zipOs.finish();
		zipOs.close();

		os.close();

		return filePathName;
	}

}
