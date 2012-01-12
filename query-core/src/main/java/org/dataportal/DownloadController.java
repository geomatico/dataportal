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
import java.util.HashMap;
import java.util.Map;
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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dataportal.controllers.JPADownloadController;
import org.dataportal.controllers.JPAUserController;
import org.dataportal.csw.GetRecordById;
import org.dataportal.model.Download;
import org.dataportal.model.DownloadItem;
import org.dataportal.model.User;
import org.dataportal.utils.DataPortalException;
import org.dataportal.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class to control the files download. Use a Cached Thread Pool to download
 * files concurrently
 * 
 * @author Micho Garcia
 * 
 */
public class DownloadController extends DataPortalController {

	private static Logger logger = Logger.getLogger(DownloadController.class);

	private static final String IDS = "//id/child::node()"; //$NON-NLS-1$
	private static final String IDENTIFIERS = "//BriefRecord/identifier/child::node()"; //$NON-NLS-1$
	private static final String ITEMS = "//item"; //$NON-NLS-1$
	private static final String UNDERSCORE = "_"; //$NON-NLS-1$
	private static final String SLASH = "/"; //$NON-NLS-1$
	private static final int IDSNODELIST = 0;
	private static final int ITEMSSNODELIST = 1;
	private static final int IDENTIFIERSNODELIST = 0;

	private static final String ZIP = ".zip"; //$NON-NLS-1$

	private String tempDir;
	private String id = null;

	/**
	 * Constructor. Reads tempDir from properties file.
	 * @throws MalformedURLException 
	 */
	public DownloadController(String lang) throws MalformedURLException {
		super();
		Messages.setLang(lang);
		this.tempDir = Config.get("temp.dir"); //$NON-NLS-1$
	}

	/**
	 * Create the pathfile with the information extracted from properties and
	 * create the directory if not exists
	 * 
	 * @param userName
	 *            User name from session (String)
	 */
	public String createPathFile(String userName) {

		String pathFile = this.tempDir + SLASH + userName;
		File personalDirectory = new File(pathFile);
		boolean exists = personalDirectory.exists();
		if (!exists) {
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
	 * @param userName
	 *            String with user name
	 * @return
	 * @throws Exception
	 */
	public String askgn2download(InputStream isRequestXML)
			throws Exception {

		StringBuffer response = new StringBuffer();

		String[] expresions = { IDS, ITEMS };
		ArrayList<NodeList> nodes = new ArrayList<NodeList>();

		nodes = extractNodeList(isRequestXML, expresions);
		ArrayList<String> requestIdes = Utils.nodeList2ArrayList(nodes
				.get(IDSNODELIST));

		GetRecordById getRecordById = new GetRecordById("brief"); //$NON-NLS-1$
		String getRecordByIdQuery = getRecordById.createQuery(requestIdes);
		InputStream isGetRecordByIdResponse = catalogo
				.sendCatalogRequest(getRecordByIdQuery);

		// TODO Controlar excepción retornada del servidor
		String[] identifiers = { IDENTIFIERS };
		ArrayList<NodeList> identifierArrayList = extractNodeList(
				isGetRecordByIdResponse, identifiers);
		ArrayList<String> responseIdes = Utils
				.nodeList2ArrayList(identifierArrayList
						.get(IDENTIFIERSNODELIST));

		ArrayList<String> noIdsResponse = Utils.compare2Arraylist(requestIdes,
				responseIdes);

		if (noIdsResponse.size() != 0) {
			logger.info("ID'S NO ENCONTRADOS: " //$NON-NLS-1$
					+ String.valueOf(noIdsResponse.size()) + " -> " //$NON-NLS-1$
					+ StringUtils.join(noIdsResponse, " : ")); //$NON-NLS-1$
			dtException = new DataPortalException(
					Messages.getString("downloadcontroller.ids_not_found") //$NON-NLS-1$
							+ StringUtils.join(noIdsResponse, ", ")); //$NON-NLS-1$
			dtException.setCode(IDNOTFOUND);
			throw dtException;
		} else {
			NodeList itemsNodeList = nodes.get(ITEMSSNODELIST);

			int nItems = itemsNodeList.getLength();
			ArrayList<DownloadItem> items = new ArrayList<DownloadItem>(nItems);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(dataPortalCtx);
			for (int i = 0; i < nItems; i++) {
				DownloadItem item = new DownloadItem();
				Node itemDom = itemsNodeList.item(i);
				item.setItemId(xpath.evaluate("id", itemDom)); //$NON-NLS-1$
				item.setUrl(xpath.evaluate("data_link", itemDom)); //$NON-NLS-1$
				item.setInstitution(xpath.evaluate("institution", itemDom)); //$NON-NLS-1$
				item.setIcosDomain(xpath.evaluate("icos_domain", itemDom)); //$NON-NLS-1$
				items.add(item);
			}

			// TODO Enviar volumen descarga al usuario y actuar en función

			String resultDownload = downloadDatasets(items, user.getId());
			response.append(resultDownload);
		}

		logger.debug("RESPONSE CONTROLLER: " + response.toString()); //$NON-NLS-1$

		return response.toString();
	}

	public String getId() {
		return this.id;
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
	 * @throws Exception
	 */
	private void insertDownload(User user, String uid, String fileName,
			ArrayList<DownloadItem> downloadItems) throws Exception {

		Timestamp timeStamp = Utils.extractDateSystemTimeStamp();
		Download download = new Download(uid, fileName, timeStamp, user);
		downloadJPAController = new JPADownloadController();
		downloadJPAController.insertItems(download, downloadItems);
	}

	/**
	 * 
	 * Method to obtain DOI
	 * 
	 * @return String with DOI
	 */
	private String generateId() {
		this.id = UUID.randomUUID().toString();
		return this.id;
	}

	/**
	 * @param Document
	 * @param expresions
	 *            array
	 * @return
	 * @throws Exception
	 */
	private ArrayList<NodeList> extractNodeList(InputStream inputStream,
			String[] expresions) throws Exception {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document documentXML = (Document) dBuilder.parse(inputStream);

		NodeList nodelist;
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(dataPortalCtx);
		ArrayList<NodeList> nodos = new ArrayList<NodeList>();

		for (int n = 0; n < expresions.length; n++) {
			nodelist = (NodeList) xpath.evaluate(expresions[n], documentXML,
					XPathConstants.NODESET);
			nodos.add(nodelist);
		}

		return nodos;
	}

	/**
	 * Method to download files from a url's array and make some necessary
	 * operations like create readme file, compress files and save to rdbms the
	 * download information
	 * 
	 * @param urlsRequest
	 *            ArrayList with url's (ArrayList)
	 * @return String with file name
	 * @throws Exception
	 */
	private String downloadDatasets(ArrayList<DownloadItem> downloadItems,
			String userName) throws Exception {

		User anUser = new User(userName);
		userJPAController = new JPAUserController();
		User user = userJPAController.existsInto(anUser);

		if (user == null) {
			dtException = new DataPortalException(
					Messages.getString("downloadcontroller.user_not_found") + userName); //$NON-NLS-1$
			dtException.setCode(USERNOTFOUND);
			throw dtException;
		}

		StringBuffer response = new StringBuffer();
		String pathFile = createPathFile(userName);
		String nameFile = userName + UNDERSCORE + Utils.extractDateSystem();

		if (pathFile == null) {
			dtException = new DataPortalException(Messages.getString("downloadcontroller.failed_create_dir")); //$NON-NLS-1$
			dtException.setCode(FAILECREATEDIRECTORY);
			throw dtException;
		} else {
			String[] files = createDownloadThreads(downloadItems, pathFile);
			String uid = this.generateId();
			Map<String, String> params = new HashMap<String, String>();
			params.put("ddi", uid); //$NON-NLS-1$
			File readmeFile = createReadmeFile(params, pathFile);
			compressFiles(pathFile, readmeFile, files, nameFile);
			insertDownload(user, uid, nameFile + ZIP, downloadItems);

			logger.debug("FILE to download: " + nameFile + ZIP); //$NON-NLS-1$
			response.append(nameFile + ZIP);
		}

		return response.toString();
	}

	/**
	 * Create README file in user folder with download information
	 * 
	 * @param vars
	 * @param pathFile
	 * @return Returns the readme file ready to compress
	 * @throws IOException
	 */
	private File createReadmeFile(Map<String, String> vars, String pathFile)
			throws IOException {
		InputStream is = DownloadController.class
				.getResourceAsStream("/README.txt"); //$NON-NLS-1$
		String readmeString = IOUtils.toString(is, "UTF-8"); //$NON-NLS-1$
		for (Map.Entry<String, String> var : vars.entrySet()) {
			readmeString = readmeString.replaceAll(
					"\\{" + var.getKey() + "\\}", var.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		File readmeFile = new File(pathFile
				+ "/README.txt" + "_" + System.currentTimeMillis()); //$NON-NLS-1$
		FileUtils.writeStringToFile(readmeFile, readmeString);

		return readmeFile;
	}

	/**
	 * 
	 * Create all Threads to download files
	 * 
	 * @param urlsRequest
	 *            URL's ArrayList
	 * @param pathFile
	 *            path to save files
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private String[] createDownloadThreads(
			ArrayList<DownloadItem> downloadItems,
			String pathFile) throws MalformedURLException,
			InterruptedException, ExecutionException {

		int nItems = downloadItems.size();

		// TODO should it be a field?
		ExecutorService threadsPool = Executors.newCachedThreadPool();
		@SuppressWarnings("unchecked")
		Future<String> futures[] = new Future[nItems];

		for (int i = 0; i < nItems; i++) {
			String url = downloadItems.get(i).getUrl();
			String name = StringUtils.substringAfterLast(url, SLASH);

			DownloadCallable hiloDescarga = new DownloadCallable(url, name,
					pathFile);
			futures[i] = threadsPool.submit(hiloDescarga);

			// TODO why?
			Thread.sleep(2000);
		}

		String[] files = new String[futures.length];
		for (int i = 0; i < nItems; i++) {
			String tarea = futures[i].get();
			files[i] = tarea;
			logger.info("DOWNLOADING FINISH: " + tarea); //$NON-NLS-1$
		}

		return files;
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
	public InputStream getFileContents(String fileName)
			throws FileNotFoundException {
		File file = new File(this.tempDir + SLASH + user.getId() + SLASH + fileName);
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
	public long getFileSize(String fileName)
			throws FileNotFoundException {
		File file = new File(this.tempDir + SLASH + user.getId() + SLASH + fileName);
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
	 * @param readmeFile
	 *            The file containing the instructions
	 * @param files
	 *            The files in pathDir to add to the zip file
	 * @param nameFile
	 *            archive name (String)
	 * @throws IOException
	 */
	private void compressFiles(String pathDir, File readmeFile, String[] files,
			String nameFile)
			throws IOException {

		String filePathName = pathDir + SLASH + nameFile + ".zip"; //$NON-NLS-1$
		OutputStream os = new FileOutputStream(filePathName);
		ZipArchiveOutputStream zipOs = new ZipArchiveOutputStream(os);

		addToZip(zipOs, readmeFile);

		for (String filePath : files) {
			File fl = new File(filePath);
			addToZip(zipOs, fl);
		}

		zipOs.finish();
		zipOs.close();

		os.close();
	}

	private void addToZip(ZipArchiveOutputStream zipOs, File fl)
			throws IOException {
		ArchiveEntry archFl = zipOs.createArchiveEntry(fl, getName(fl));
		zipOs.putArchiveEntry(archFl);
		zipOs.write(FileUtils.readFileToByteArray(fl));
		zipOs.flush();
		zipOs.closeArchiveEntry();

		FileUtils.forceDelete(fl);
	}

	private String getName(File fl) {
		String name = fl.getName();
		return name.substring(0, name.lastIndexOf('_'));
	}

}
